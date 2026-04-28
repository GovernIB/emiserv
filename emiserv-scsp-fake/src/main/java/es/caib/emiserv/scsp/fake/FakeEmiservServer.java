package es.caib.emiserv.scsp.fake;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class FakeEmiservServer {

	private static final String DEFAULT_HOST = "0.0.0.0";
	private static final int DEFAULT_PORT = 18080;
	private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String BACKOFFICE_NS = "http://caib.es/emiserv/backoffice";
	private static final String DEFAULT_SCSP_NS = "http://intermediacion.redsara.es/scsp/esquemas/solicitudrespuesta";
	private static final String SCSP_V3_PETICION_NS = "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
	private static final String SCSP_V3_RESPUESTA_NS = "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";
	private static final String SCSP_V3_SOAPFAULT_ATRIBUTOS_NS = "http://intermediacion.redsara.es/scsp/esquemas/V3/soapfaultatributos";
	private static final String DATOS_ESPECIFICOS_NS = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";
	private static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	private static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
	private static final String DS_NS = "http://www.w3.org/2000/09/xmldsig#";
	private static final String X509_V3_TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";
	private static final String BASE64_BINARY_TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";
	private static final String DEFAULT_KEYSTORE_RESOURCE = "interoperabilitat.jks";
	private static final String DEFAULT_KEYSTORE_PASSWORD = "tecnologies";
	private static final String DEFAULT_KEY_ALIAS = "limit_pinbal";
	private static final String STATUS_PATH = "/__fake-emiserv";
	private static final String BACKOFFICE_PATH = "/ws/EmiservBackoffice";
	private static final String BACKOFFICE_WSDL_RESOURCE = "es/caib/emiserv/scsp/fake/EmiservBackoffice.wsdl";

	private FakeEmiservServer() {
	}

	public static void main(String[] args) throws Exception {
		String host = System.getProperty("fake.emiserv.host", DEFAULT_HOST);
		int port = Integer.parseInt(System.getProperty("fake.emiserv.port", String.valueOf(DEFAULT_PORT)));
		HttpServer server = createServer(host, port);
		server.start();
		System.out.println("Fake Emiserv escoltant a http://" + host + ":" + port);
		System.out.println("SCSP fake: POST http://" + host + ":" + port + "/scsp/<servei>/<entitat>");
		System.out.println("SCSP fake error: POST http://" + host + ":" + port + "/scsp/<servei>/<entitat>/ko");
		System.out.println("SCSP fake sense dades: POST http://" + host + ":" + port + "/scsp/<servei>/<entitat>/no");
		System.out.println("Backoffice fake: POST http://" + host + ":" + port + BACKOFFICE_PATH);
		System.out.println("Backoffice WSDL: GET  http://" + host + ":" + port + BACKOFFICE_PATH + "?wsdl");
	}

	static HttpServer createServer(String host, int port) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
		server.createContext("/", new FakeHandler());
		server.setExecutor(Executors.newCachedThreadPool());
		return server;
	}

	private static final class FakeHandler implements HttpHandler {
		private final SoapResponseSigner signer = new SoapResponseSigner();

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			try {
				String path = exchange.getRequestURI().getPath();
				if ("GET".equalsIgnoreCase(exchange.getRequestMethod()) && STATUS_PATH.equals(path)) {
					writeText(exchange, 200, statusBody());
					return;
				}
				if ("GET".equalsIgnoreCase(exchange.getRequestMethod()) && isBackofficeWsdlRequest(exchange)) {
					writeXml(exchange, 200, loadBackofficeWsdl(exchange));
					return;
				}
				if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
					writeText(exchange, 405, "Només s'accepten peticions GET a l'estat/WSDL i POST SOAP");
					return;
				}

				String requestXml = readBody(exchange.getRequestBody());
				RequestInfo requestInfo = RequestInfo.fromXml(requestXml);
				requestInfo = requestInfo.withPathInfo(ScspPathInfo.fromPath(path));
				log(exchange, requestInfo, requestXml);

				if (isBackofficePath(path)) {
					handleBackoffice(exchange, requestInfo);
				} else {
					handleScsp(exchange, requestInfo, requestXml);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
				writeText(exchange, 500, "Error intern del fake: " + e.getMessage());
			} finally {
				exchange.close();
			}
		}

		private void handleBackoffice(HttpExchange exchange, RequestInfo requestInfo) throws IOException {
			String operation = requestInfo.bodyRoot;
			if ("peticionSincrona".equals(operation)) {
				writeXml(exchange, 200, SoapTemplates.backofficeSyncResponse(requestInfo));
				return;
			}
			if ("solicitarRespuesta".equals(operation)) {
				writeXml(exchange, 200, SoapTemplates.backofficeSolicitudResponse(requestInfo));
				return;
			}
			if ("peticionAsincrona".equals(operation)) {
				writeXml(exchange, 200, SoapTemplates.backofficeAsyncResponse(requestInfo));
				return;
			}
			writeXml(exchange, 500, SoapTemplates.soapFault("Server", "Operacio backoffice no suportada: " + operation));
		}

		private void handleScsp(HttpExchange exchange, RequestInfo requestInfo, String requestXml) throws IOException {
			String xmlLower = requestXml.toLowerCase();
			if (requestInfo.forceError || xmlLower.contains("<error") || xmlLower.contains(":error")) {
				writeXml(exchange, 500, signer.sign(
					SoapTemplates.scspSoapFault(
						requestInfo,
						"Server",
						"0101",
						"Error forcat pel fake SCSP"),
					requestInfo.signatureStyle));
				return;
			}
			writeXml(exchange, 200, signer.sign(SoapTemplates.scspResponse(requestInfo), requestInfo.signatureStyle));
		}

		private String loadBackofficeWsdl(HttpExchange exchange) {
			try (InputStream input = getClass().getClassLoader().getResourceAsStream(BACKOFFICE_WSDL_RESOURCE)) {
				if (input == null) {
					throw new IOException("No s'ha trobat el WSDL de backoffice");
				}
				String wsdl = readBody(input);
				String address = resolveBaseUrl(exchange) + BACKOFFICE_PATH;
				return wsdl.replace(
					"location=\"http://localhost:8080/emiservback/ws/EmiservBackoffice\"",
					"location=\"" + address + "\"");
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		private boolean isBackofficeWsdlRequest(HttpExchange exchange) {
			String path = exchange.getRequestURI().getPath();
			String query = exchange.getRequestURI().getQuery();
			return isBackofficePath(path) && query != null && query.toLowerCase().contains("wsdl");
		}

		private boolean isBackofficePath(String path) {
			return BACKOFFICE_PATH.equals(path) || "/backoffice".equals(path) || "/backoffice/EmiservBackoffice".equals(path);
		}

		private String resolveBaseUrl(HttpExchange exchange) {
			String host = exchange.getRequestHeaders().getFirst("Host");
			if (host == null || host.isBlank()) {
				host = exchange.getLocalAddress().getHostString() + ":" + exchange.getLocalAddress().getPort();
			}
			return "http://" + host;
		}

		private void log(HttpExchange exchange, RequestInfo requestInfo, String requestXml) {
			System.out.println("[FAKE-EMISERV] method=" + exchange.getRequestMethod()
				+ " path=" + exchange.getRequestURI().getPath()
				+ " bodyRoot=" + requestInfo.bodyRoot
				+ " servei=" + requestInfo.serviceCode
				+ " entitat=" + requestInfo.entityCode
				+ " forceError=" + requestInfo.forceError
				+ " noData=" + requestInfo.noData
				+ " idPeticion=" + requestInfo.idPeticion
				+ " idSolicitudes=" + requestInfo.idSolicitudes);
			System.out.println(requestXml);
		}

		private String statusBody() {
			return "Fake Emiserv actiu\n"
				+ "- SCSP POST /scsp/<servei>/<entitat> o /scsp/<servei>/<entitat>/ko o /scsp/<servei>/<entitat>/no\n"
				+ "- Backoffice POST " + BACKOFFICE_PATH + "\n"
				+ "- WSDL GET " + BACKOFFICE_PATH + "?wsdl\n";
		}
	}

	static final class RequestInfo {
		private final String bodyRoot;
		private final String bodyNamespace;
		private final String serviceCode;
		private final String entityCode;
		private final boolean forceError;
		private final boolean noData;
		private final SignatureStyle signatureStyle;
		private final String idPeticion;
		private final String numElementos;
		private final String tipusDocumentacio;
		private final String titularDocumentacio;
		private final String titularNom;
		private final String titularLlinatge1;
		private final String titularLlinatge2;
		private final String titularDataNaixement;
		private final String comunitatAutonoma;
		private final String nifEmisor;
		private final String nomEmisor;
		private final String identificadorSolicitant;
		private final String nomSolicitant;
		private final String unitatTramitadora;
		private final String codiProcediment;
		private final String nomProcediment;
		private final String finalitat;
		private final String consentiment;
		private final String nomCompletFuncionari;
		private final String nifFuncionari;
		private final List<String> idSolicitudes;

		private RequestInfo(
			String bodyRoot,
			String bodyNamespace,
			String serviceCode,
			String entityCode,
			boolean forceError,
			boolean noData,
			SignatureStyle signatureStyle,
			String idPeticion,
			String numElementos,
			String tipusDocumentacio,
			String titularDocumentacio,
			String titularNom,
			String titularLlinatge1,
			String titularLlinatge2,
			String titularDataNaixement,
			String comunitatAutonoma,
			String nifEmisor,
			String nomEmisor,
			String identificadorSolicitant,
			String nomSolicitant,
			String unitatTramitadora,
			String codiProcediment,
			String nomProcediment,
			String finalitat,
			String consentiment,
			String nomCompletFuncionari,
			String nifFuncionari,
			List<String> idSolicitudes) {
			this.bodyRoot = bodyRoot;
			this.bodyNamespace = bodyNamespace;
			this.serviceCode = serviceCode;
			this.entityCode = entityCode;
			this.forceError = forceError;
			this.noData = noData;
			this.signatureStyle = signatureStyle;
			this.idPeticion = idPeticion;
			this.numElementos = numElementos;
			this.tipusDocumentacio = tipusDocumentacio;
			this.titularDocumentacio = titularDocumentacio;
			this.titularNom = titularNom;
			this.titularLlinatge1 = titularLlinatge1;
			this.titularLlinatge2 = titularLlinatge2;
			this.titularDataNaixement = titularDataNaixement;
			this.comunitatAutonoma = comunitatAutonoma;
			this.nifEmisor = nifEmisor;
			this.nomEmisor = nomEmisor;
			this.identificadorSolicitant = identificadorSolicitant;
			this.nomSolicitant = nomSolicitant;
			this.unitatTramitadora = unitatTramitadora;
			this.codiProcediment = codiProcediment;
			this.nomProcediment = nomProcediment;
			this.finalitat = finalitat;
			this.consentiment = consentiment;
			this.nomCompletFuncionari = nomCompletFuncionari;
			this.nifFuncionari = nifFuncionari;
			this.idSolicitudes = idSolicitudes;
		}

		static RequestInfo fromXml(String xml) throws IOException {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
				Element bodyRoot = findSoapBodyRoot(document);
				if (bodyRoot == null) {
					throw new IOException("No s'ha trobat el cos SOAP");
				}
				List<String> idSolicitudes = valuesByLocalName(bodyRoot, "IdSolicitud");
				if (idSolicitudes.isEmpty()) {
					idSolicitudes = valuesByLocalName(bodyRoot, "idSolicitud");
				}
				String serviceCode = firstNonBlank(
					firstValue(bodyRoot, "CodigoCertificado"),
					firstValue(bodyRoot, "codigoCertificado"),
					"FAKE_CERT");
				String idPeticion = firstNonBlank(
					firstValue(bodyRoot, "IdPeticion"),
					firstValue(bodyRoot, "idPeticion"),
					"FAKE-PET-" + System.currentTimeMillis());
				String numElementos = firstNonBlank(
					firstValue(bodyRoot, "NumElementos"),
					firstValue(bodyRoot, "numElementos"),
					idSolicitudes.isEmpty() ? "1" : String.valueOf(idSolicitudes.size()));
				String tipusDocumentacio = firstNonBlank(
					firstValue(bodyRoot, "TipoDocumentacion"),
					firstValue(bodyRoot, "tipoDocumentacion"),
					"NIF");
				String titularDocumentacio = firstNonBlank(
					firstValue(bodyRoot, "Documentacion"),
					firstValue(bodyRoot, "documentacion"));
				String titularNom = firstNonBlank(
					firstValue(bodyRoot, "Nombre"),
					firstValue(bodyRoot, "nombre"));
				String titularLlinatge1 = firstNonBlank(
					firstValue(bodyRoot, "Apellido1"),
					firstValue(bodyRoot, "apellido1"));
				String titularLlinatge2 = firstNonBlank(
					firstValue(bodyRoot, "Apellido2"),
					firstValue(bodyRoot, "apellido2"));
				String titularDataNaixement = firstNonBlank(
					firstValue(bodyRoot, "FechaNacimiento"),
					firstValue(bodyRoot, "fechaNacimiento"));
				String comunitatAutonoma = firstNonBlank(
					firstValue(bodyRoot, "CodigoComunidadAutonoma"),
					firstValue(bodyRoot, "codigoComunidadAutonoma"),
					"04");
				String nifEmisor = firstNonBlank(
					firstValue(bodyRoot, "NifEmisor"),
					firstValue(bodyRoot, "nifEmisor"),
					"S2833002E");
				String nomEmisor = firstNonBlank(
					firstValue(bodyRoot, "NombreEmisor"),
					firstValue(bodyRoot, "nombreEmisor"),
					"Govern de les Illes Balears");
				String identificadorSolicitant = firstNonBlank(
					firstValue(bodyRoot, "IdentificadorSolicitante"),
					firstValue(bodyRoot, "identificadorSolicitante"),
					"S2816015H");
				String nomSolicitant = firstNonBlank(
					firstValue(bodyRoot, "NombreSolicitante"),
					firstValue(bodyRoot, "nombreSolicitante"),
					"Límit Tecnologies");
				String unitatTramitadora = firstNonBlank(
					firstValue(bodyRoot, "UnidadTramitadora"),
					firstValue(bodyRoot, "unidadTramitadora"),
					"Dept.");
				String codiProcediment = firstNonBlank(
					firstValue(bodyRoot, "CodProcedimiento"),
					firstValue(bodyRoot, "codProcedimiento"),
					"TEST");
				String nomProcediment = firstNonBlank(
					firstValue(bodyRoot, "NombreProcedimiento"),
					firstValue(bodyRoot, "nombreProcedimiento"),
					"Procediment de test");
				String finalitat = firstNonBlank(
					firstValue(bodyRoot, "Finalidad"),
					firstValue(bodyRoot, "finalidad"),
					"Finalitat de prova");
				String consentiment = firstNonBlank(
					firstValue(bodyRoot, "Consentimiento"),
					firstValue(bodyRoot, "consentimiento"),
					"Si");
				String nomCompletFuncionari = firstNonBlank(
					firstValue(bodyRoot, "NombreCompletoFuncionario"),
					firstValue(bodyRoot, "nombreCompletoFuncionario"),
					"Admin");
				String nifFuncionari = firstNonBlank(
					firstValue(bodyRoot, "NifFuncionario"),
					firstValue(bodyRoot, "nifFuncionario"),
					"12345678Z");
				SignatureStyle signatureStyle = detectSignatureStyle(document);
				return new RequestInfo(
					bodyRoot.getLocalName(),
					bodyRoot.getNamespaceURI(),
					serviceCode,
					null,
					false,
					false,
					signatureStyle,
					idPeticion,
					numElementos,
					tipusDocumentacio,
					titularDocumentacio,
					titularNom,
					titularLlinatge1,
					titularLlinatge2,
					titularDataNaixement,
					comunitatAutonoma,
					nifEmisor,
					nomEmisor,
					identificadorSolicitant,
					nomSolicitant,
					unitatTramitadora,
					codiProcediment,
					nomProcediment,
					finalitat,
					consentiment,
					nomCompletFuncionari,
					nifFuncionari,
					idSolicitudes);
			} catch (Exception e) {
				throw new IOException("No s'ha pogut interpretar l'XML d'entrada", e);
			}
		}

		RequestInfo withPathInfo(ScspPathInfo pathInfo) {
			if (pathInfo == null) {
				return this;
			}
			String effectiveServiceCode = pathInfo.serviceCode != null ? pathInfo.serviceCode : serviceCode;
			return new RequestInfo(
				bodyRoot,
				bodyNamespace,
				effectiveServiceCode,
				pathInfo.entityCode,
				pathInfo.forceError,
				pathInfo.noData,
				signatureStyle,
				idPeticion,
				numElementos,
				tipusDocumentacio,
				titularDocumentacio,
				titularNom,
				titularLlinatge1,
				titularLlinatge2,
				titularDataNaixement,
				comunitatAutonoma,
				nifEmisor,
				nomEmisor,
				identificadorSolicitant,
				nomSolicitant,
				unitatTramitadora,
				codiProcediment,
				nomProcediment,
				finalitat,
				consentiment,
				nomCompletFuncionari,
				nifFuncionari,
				idSolicitudes);
		}

		String scspNamespace() {
			return bodyNamespace != null && !bodyNamespace.isBlank() ? bodyNamespace : DEFAULT_SCSP_NS;
		}

		String scspResponseNamespace() {
			if (SCSP_V3_PETICION_NS.equals(bodyNamespace)) {
				return SCSP_V3_RESPUESTA_NS;
			}
			return scspNamespace();
		}

		List<String> effectiveSolicitudIds() {
			if (!idSolicitudes.isEmpty()) {
				return idSolicitudes;
			}
			List<String> generated = new ArrayList<>();
			generated.add(idPeticion);
			return generated;
		}

		private static Element findSoapBodyRoot(Document document) {
			NodeList bodies = document.getElementsByTagNameNS(SOAP_NS, "Body");
			if (bodies.getLength() == 0) {
				return null;
			}
			Node body = bodies.item(0);
			NodeList children = body.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element) {
					return (Element) child;
				}
			}
			return null;
		}

		private static SignatureStyle detectSignatureStyle(Document document) {
			NodeList wsseSecurity = document.getElementsByTagNameNS(WSSE_NS, "Security");
			if (wsseSecurity.getLength() > 0) {
				return SignatureStyle.WS_SECURITY;
			}
			NodeList dsSignature = document.getElementsByTagNameNS(DS_NS, "Signature");
			if (dsSignature.getLength() > 0) {
				return SignatureStyle.XML_SIGNATURE;
			}
			return SignatureStyle.XML_SIGNATURE;
		}

		private static String firstValue(Element root, String localName) {
			List<String> values = valuesByLocalName(root, localName);
			return values.isEmpty() ? null : values.get(0);
		}

		private static List<String> valuesByLocalName(Element root, String localName) {
			NodeList nodes = root.getElementsByTagNameNS("*", localName);
			List<String> values = new ArrayList<>();
			for (int i = 0; i < nodes.getLength(); i++) {
				String value = nodes.item(i).getTextContent();
				if (value != null && !value.isBlank()) {
					values.add(value.trim());
				}
			}
			return values;
		}

		private static String firstNonBlank(String... values) {
			for (String value : values) {
				if (value != null && !value.isBlank()) {
					return value;
				}
			}
			return null;
		}
	}

	static final class ScspPathInfo {
		private final String serviceCode;
		private final String entityCode;
		private final boolean forceError;
		private final boolean noData;

		private ScspPathInfo(String serviceCode, String entityCode, boolean forceError, boolean noData) {
			this.serviceCode = serviceCode;
			this.entityCode = entityCode;
			this.forceError = forceError;
			this.noData = noData;
		}

		static ScspPathInfo fromPath(String path) {
			if (path == null || !path.startsWith("/scsp/")) {
				return null;
			}
			String[] rawParts = path.split("/");
			List<String> parts = new ArrayList<>();
			for (String rawPart : rawParts) {
				if (rawPart != null && !rawPart.isBlank()) {
					parts.add(rawPart);
				}
			}
			if (parts.size() < 3 || !"scsp".equals(parts.get(0))) {
				return null;
			}
			String serviceCode = parts.get(1);
			String entityCode = parts.get(2);
			boolean forceError = parts.size() > 3 && "ko".equalsIgnoreCase(parts.get(3));
			boolean noData = parts.size() > 3 && "no".equalsIgnoreCase(parts.get(3));
			return new ScspPathInfo(serviceCode, entityCode, forceError, noData);
		}
	}

	enum SignatureStyle {
		XML_SIGNATURE,
		WS_SECURITY
	}

	static final class SoapTemplates {

		private SoapTemplates() {
		}

		static String backofficeSyncResponse(RequestInfo requestInfo) {
			return envelope(
				"<tns:peticionSincronaResponse xmlns:tns=\"" + BACKOFFICE_NS + "\">"
					+ "<respuesta>"
					+ backofficeRespuestaBody(requestInfo)
					+ "</respuesta>"
					+ "</tns:peticionSincronaResponse>");
		}

		static String backofficeSolicitudResponse(RequestInfo requestInfo) {
			return envelope(
				"<tns:solicitarRespuestaResponse xmlns:tns=\"" + BACKOFFICE_NS + "\">"
					+ "<respuesta>"
					+ backofficeRespuestaBody(requestInfo)
					+ "</respuesta>"
					+ "</tns:solicitarRespuestaResponse>");
		}

		static String backofficeAsyncResponse(RequestInfo requestInfo) {
			return envelope(
				"<tns:peticionAsincronaResponse xmlns:tns=\"" + BACKOFFICE_NS + "\">"
					+ "<respuesta>"
					+ "<atributos>"
					+ "<codigoCertificado>" + esc(requestInfo.serviceCode) + "</codigoCertificado>"
					+ "<estado><codigoEstado>0003</codigoEstado><literalError>OK</literalError><tiempoEstimadoRespuesta>0</tiempoEstimadoRespuesta></estado>"
					+ "<idPeticion>" + esc(requestInfo.idPeticion) + "</idPeticion>"
					+ "<numElementos>" + esc(requestInfo.numElementos) + "</numElementos>"
					+ "<timeStamp>2026-04-27T10:00:00+02:00</timeStamp>"
					+ "</atributos>"
					+ "</respuesta>"
					+ "</tns:peticionAsincronaResponse>");
		}

		static String scspResponse(RequestInfo requestInfo) {
			String ns = requestInfo.scspResponseNamespace();
			StringBuilder transmissions = new StringBuilder();
			int counter = 1;
			for (String idSolicitud : requestInfo.effectiveSolicitudIds()) {
				transmissions.append("<TransmisionDatos>")
					.append("<DatosGenericos>")
					.append("<Emisor>")
					.append("<NifEmisor>").append(esc(scspNifEmisor(requestInfo))).append("</NifEmisor>")
					.append("<NombreEmisor>").append(esc(defaultIfBlank(requestInfo.nomEmisor, "Govern de les Illes Balears"))).append("</NombreEmisor>")
					.append("</Emisor>")
					.append("<Solicitante>")
					.append("<IdentificadorSolicitante>").append(esc(defaultIfBlank(requestInfo.identificadorSolicitant, "S2816015H"))).append("</IdentificadorSolicitante>")
					.append("<NombreSolicitante>").append(esc(defaultIfBlank(requestInfo.nomSolicitant, "Límit Tecnologies"))).append("</NombreSolicitante>")
					.append("<UnidadTramitadora>").append(esc(defaultIfBlank(requestInfo.unitatTramitadora, "Dept."))).append("</UnidadTramitadora>")
					.append("<Procedimiento>")
					.append("<CodProcedimiento>").append(esc(defaultIfBlank(requestInfo.codiProcediment, "TEST"))).append("</CodProcedimiento>")
					.append("<NombreProcedimiento>").append(esc(defaultIfBlank(requestInfo.nomProcediment, "Procediment de test"))).append("</NombreProcedimiento>")
					.append("</Procedimiento>")
					.append("<Finalidad>").append(esc(defaultIfBlank(requestInfo.finalitat, "Finalitat de prova"))).append("</Finalidad>")
					.append("<Consentimiento>").append(esc(defaultIfBlank(requestInfo.consentiment, "Si"))).append("</Consentimiento>")
					.append("<Funcionario>")
					.append("<NombreCompletoFuncionario>").append(esc(defaultIfBlank(requestInfo.nomCompletFuncionari, "Admin"))).append("</NombreCompletoFuncionario>")
					.append("<NifFuncionario>").append(esc(defaultIfBlank(requestInfo.nifFuncionari, "12345678Z"))).append("</NifFuncionario>")
					.append("</Funcionario>")
					.append("</Solicitante>")
					.append("<Titular>")
					.append(optionalTag("TipoDocumentacion", requestInfo.tipusDocumentacio))
					.append(optionalTag("Documentacion", requestInfo.titularDocumentacio))
					.append(optionalTag("Nombre", requestInfo.titularNom))
					.append(optionalTag("Apellido1", requestInfo.titularLlinatge1))
					.append(optionalTag("Apellido2", requestInfo.titularLlinatge2))
					.append("</Titular>")
					.append("<Transmision>")
					.append("<CodigoCertificado>").append(esc(requestInfo.serviceCode)).append("</CodigoCertificado>")
					.append("<IdSolicitud>").append(esc(idSolicitud)).append("</IdSolicitud>")
					.append("<IdTransmision>").append(esc(requestInfo.idPeticion)).append("-").append(counter).append("</IdTransmision>")
					.append("<FechaGeneracion>2026-04-27T10:00:00+02:00</FechaGeneracion>")
					.append("</Transmision>")
					.append("</DatosGenericos>")
					.append(scspDatosEspecificos(requestInfo))
					.append("</TransmisionDatos>");
				counter++;
			}
			return envelope(
				"<Respuesta xmlns=\"" + ns + "\">"
					+ "<Atributos>"
					+ "<CodigoCertificado>" + esc(requestInfo.serviceCode) + "</CodigoCertificado>"
					+ "<IdPeticion>" + esc(requestInfo.idPeticion) + "</IdPeticion>"
					+ "<NumElementos>" + esc(requestInfo.numElementos) + "</NumElementos>"
					+ "<TimeStamp>2026-04-27T10:00:00+02:00</TimeStamp>"
					+ "<Estado><CodigoEstado>0003</CodigoEstado><LiteralError>OK</LiteralError></Estado>"
					+ "</Atributos>"
					+ "<Transmisiones>" + transmissions + "</Transmisiones>"
					+ "</Respuesta>");
		}

		private static String scspDatosEspecificos(RequestInfo requestInfo) {
			if ("SVDSCTFNWS01".equalsIgnoreCase(requestInfo.serviceCode)) {
				return familiaNombrosaDatosEspecificos(requestInfo);
			}
			return "<DatosEspecificos xmlns=\"" + DATOS_ESPECIFICOS_NS + "\">"
				+ "<Retorno>"
				+ "<Estado>"
				+ "<CodigoEstado>0</CodigoEstado>"
				+ "<LiteralError>Existe el titulo de familia numerosa</LiteralError>"
				+ "</Estado>"
				+ "<TituloFamiliaNumerosaRetorno>"
				+ "<CodigoComunidadAutonoma>" + esc(defaultIfBlank(requestInfo.comunitatAutonoma, "04")) + "</CodigoComunidadAutonoma>"
				+ "<NumeroTitulo>FAKE-" + esc(defaultIfBlank(requestInfo.idPeticion, "PET")) + "</NumeroTitulo>"
				+ "<Categoria>G</Categoria>"
				+ "<TituloVigente>S</TituloVigente>"
				+ "<FechaCaducidad>31/12/2035</FechaCaducidad>"
				+ "<NumeroHijos>2</NumeroHijos>"
				+ "</TituloFamiliaNumerosaRetorno>"
				+ "</Retorno>"
				+ "</DatosEspecificos>";
		}

		private static String familiaNombrosaDatosEspecificos(RequestInfo requestInfo) {
			if (requestInfo.noData) {
				return "<DatosEspecificos xmlns=\"" + DATOS_ESPECIFICOS_NS + "\">"
					+ "<Retorno>"
					+ "<Estado>"
					+ "<CodigoEstado>7</CodigoEstado>"
					+ "<LiteralError>No s&apos;ha trobat informació</LiteralError>"
					+ "</Estado>"
					+ "</Retorno>"
					+ "</DatosEspecificos>";
			}
			String entitat = defaultIfBlank(requestInfo.entityCode, "MALLORCA").toUpperCase();
			String codiTitol = "TFN-" + entitat + "-" + sanitizeDigits(requestInfo.titularDocumentacio, "18225486") + "-2026";
			return "<DatosEspecificos xmlns=\"" + DATOS_ESPECIFICOS_NS + "\">"
				+ "<Retorno>"
				+ "<Estado>"
				+ "<CodigoEstado>0</CodigoEstado>"
				+ "<LiteralError>Existe el titulo de familia numerosa</LiteralError>"
				+ "</Estado>"
				+ "<TituloFamiliaNumerosaRetorno>"
				+ "<CodigoComunidadAutonoma>" + esc(defaultIfBlank(requestInfo.comunitatAutonoma, "04")) + "</CodigoComunidadAutonoma>"
				+ "<NumeroTitulo>" + esc(codiTitol) + "</NumeroTitulo>"
				+ "<Categoria>G</Categoria>"
				+ "<TituloVigente>S</TituloVigente>"
				+ "<FechaExpedicion>01/01/2024</FechaExpedicion>"
				+ "<FechaCaducidad>31/12/2035</FechaCaducidad>"
				+ "<NumeroHijos>3</NumeroHijos>"
				+ "</TituloFamiliaNumerosaRetorno>"
				+ "<ListaBeneficiariosRetorno>"
				+ "<BeneficiarioRetorno>"
				+ optionalTag("TipoDocumentacion", requestInfo.tipusDocumentacio)
				+ optionalTag("Documentacion", defaultIfBlank(requestInfo.titularDocumentacio, "18225486X"))
				+ optionalTag("FechaNacimiento", requestInfo.titularDataNaixement)
				+ optionalTag("Nombre", defaultIfBlank(requestInfo.titularNom, "Sion"))
				+ optionalTag("Apellido1", defaultIfBlank(requestInfo.titularLlinatge1, "Andreu"))
				+ optionalTag("Apellido2", requestInfo.titularLlinatge2)
				+ "<Titular>S</Titular>"
				+ "</BeneficiarioRetorno>"
				+ "</ListaBeneficiariosRetorno>"
				+ "</Retorno>"
				+ "</DatosEspecificos>";
		}

		private static String scspNifEmisor(RequestInfo requestInfo) {
			if ("SVDSCTFNWS01".equalsIgnoreCase(requestInfo.serviceCode)) {
				return "S2833002E";
			}
			return defaultIfBlank(requestInfo.nifEmisor, "S2833002E");
		}

		private static String scspSoapFaultAtributosNs(RequestInfo requestInfo) {
			if (requestInfo != null && SCSP_V3_RESPUESTA_NS.equals(requestInfo.scspResponseNamespace())) {
				return SCSP_V3_SOAPFAULT_ATRIBUTOS_NS;
			}
			return SCSP_V3_SOAPFAULT_ATRIBUTOS_NS;
		}

		static String soapFault(String faultCode, String faultString) {
			return envelope(
				"<soapenv:Fault>"
					+ "<faultcode>soapenv:" + esc(faultCode) + "</faultcode>"
					+ "<faultstring>" + esc(faultString) + "</faultstring>"
					+ "</soapenv:Fault>");
		}

		static String scspSoapFault(
				RequestInfo requestInfo,
				String faultCode,
				String codigoEstado,
				String literalError) {
			return envelope(
				"<soapenv:Fault>"
					+ "<faultcode>soapenv:" + esc(faultCode) + "</faultcode>"
					+ "<faultstring>" + esc(defaultIfBlank(literalError, "Error forcat pel fake SCSP")) + "</faultstring>"
					+ "<detail>"
					+ "<a:Atributos xmlns:a=\"" + scspSoapFaultAtributosNs(requestInfo) + "\">"
					+ "<a:IdPeticion>" + esc(defaultIfBlank(requestInfo.idPeticion, "FAKE-PET")) + "</a:IdPeticion>"
					+ "<a:NumElementos>" + esc(defaultIfBlank(requestInfo.numElementos, "1")) + "</a:NumElementos>"
					+ "<a:TimeStamp>2026-04-27T10:00:00+02:00</a:TimeStamp>"
					+ "<a:CodigoCertificado>" + esc(defaultIfBlank(requestInfo.serviceCode, "FAKE_CERT")) + "</a:CodigoCertificado>"
					+ "<a:Estado>"
					+ "<a:CodigoEstado>" + esc(defaultIfBlank(codigoEstado, "0101")) + "</a:CodigoEstado>"
					+ "<a:LiteralError>" + esc(defaultIfBlank(literalError, "Error forcat pel fake SCSP")) + "</a:LiteralError>"
					+ "</a:Estado>"
					+ "</a:Atributos>"
					+ "</detail>"
					+ "</soapenv:Fault>");
		}

		private static String backofficeRespuestaBody(RequestInfo requestInfo) {
			StringBuilder transmissions = new StringBuilder();
			int counter = 1;
			for (String idSolicitud : requestInfo.effectiveSolicitudIds()) {
				transmissions.append("<transmisionDatos>")
					.append("<datosGenericos>")
					.append("<transmision>")
					.append("<codigoCertificado>").append(esc(requestInfo.serviceCode)).append("</codigoCertificado>")
					.append("<fechaGeneracion>2026-04-27T10:00:00+02:00</fechaGeneracion>")
					.append("<idSolicitud>").append(esc(idSolicitud)).append("</idSolicitud>")
					.append("<idTransmision>").append(esc(requestInfo.idPeticion)).append("-").append(counter).append("</idTransmision>")
					.append("</transmision>")
					.append("</datosGenericos>")
					.append("<datosEspecificos><resultado>Resposta fake backoffice</resultado></datosEspecificos>")
					.append("</transmisionDatos>");
				counter++;
			}
			return "<atributos>"
				+ "<codigoCertificado>" + esc(requestInfo.serviceCode) + "</codigoCertificado>"
				+ "<estado><codigoEstado>0003</codigoEstado><literalError>OK</literalError><tiempoEstimadoRespuesta>0</tiempoEstimadoRespuesta></estado>"
				+ "<idPeticion>" + esc(requestInfo.idPeticion) + "</idPeticion>"
				+ "<numElementos>" + esc(requestInfo.numElementos) + "</numElementos>"
				+ "<timeStamp>2026-04-27T10:00:00+02:00</timeStamp>"
				+ "</atributos>"
				+ "<transmisiones>" + transmissions + "</transmisiones>";
		}

		private static String envelope(String body) {
			return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<soapenv:Envelope xmlns:soapenv=\"" + SOAP_NS + "\">"
				+ "<soapenv:Body>"
				+ body
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";
		}

		private static String esc(String value) {
			if (value == null) {
				return "";
			}
			return value
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
		}

		private static String defaultIfBlank(String value, String defaultValue) {
			return value == null || value.isBlank() ? defaultValue : value;
		}

		private static String optionalTag(String tagName, String value) {
			if (value == null || value.isBlank()) {
				return "";
			}
			return "<" + tagName + ">" + esc(value) + "</" + tagName + ">";
		}

		private static String sanitizeDigits(String value, String defaultValue) {
			String source = defaultIfBlank(value, defaultValue);
			StringBuilder digits = new StringBuilder();
			for (int i = 0; i < source.length(); i++) {
				char c = source.charAt(i);
				if (Character.isLetterOrDigit(c)) {
					digits.append(c);
				}
			}
			return digits.length() == 0 ? defaultValue : digits.toString();
		}

	}

	static final class SoapResponseSigner {
		private final PrivateKey privateKey;
		private final X509Certificate certificate;

		SoapResponseSigner() {
			try {
				String keyAlias = System.getProperty("fake.scsp.keyAlias", DEFAULT_KEY_ALIAS);
				String keystorePassword = System.getProperty("fake.scsp.keystorePass", DEFAULT_KEYSTORE_PASSWORD);
				String keyPassword = System.getProperty("fake.scsp.keyPassword", keystorePassword);
				KeyStore keyStore = loadKeyStore(keystorePassword);
				this.privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyPassword.toCharArray());
				this.certificate = (X509Certificate) keyStore.getCertificate(keyAlias);
				if (privateKey == null || certificate == null) {
					throw new IllegalStateException("No s'ha trobat la clau privada o el certificat per a l'alias " + keyAlias);
				}
			} catch (Exception e) {
				throw new IllegalStateException("No s'ha pogut inicialitzar la firma del fake SCSP", e);
			}
		}

		String sign(String xml, SignatureStyle signatureStyle) {
			try {
				Document document = parse(xml);
				if (signatureStyle == SignatureStyle.WS_SECURITY) {
					removeSecurityHeader(document);
					Element securityHeader = ensureSecurityHeader(document);
					String idSeed = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
					String tokenId = "CertId-" + idSeed;
					String keyInfoId = "KeyId-" + idSeed;
					String securityTokenReferenceId = "STRId-" + idSeed;
					appendBinarySecurityToken(document, securityHeader, tokenId);
					appendWsSecuritySignature(document, securityHeader, tokenId, keyInfoId, securityTokenReferenceId);
				} else {
					removePlainSignature(document);
					Element header = ensureSoapHeader(document);
					appendXmlSignature(document, header);
				}
				return toXml(document);
			} catch (Exception e) {
				throw new IllegalStateException("No s'ha pogut firmar la resposta SOAP fake", e);
			}
		}

		private KeyStore loadKeyStore(String keystorePassword) throws Exception {
			String resource = System.getProperty("fake.scsp.keystoreResource", DEFAULT_KEYSTORE_RESOURCE);
			InputStream input = FakeEmiservServer.class.getClassLoader().getResourceAsStream(resource);
			if (input == null) {
				throw new IllegalStateException("No s'ha trobat el keystore al classpath: " + resource);
			}
			try {
				KeyStore keyStore = KeyStore.getInstance("JKS");
				keyStore.load(input, keystorePassword.toCharArray());
				return keyStore;
			} finally {
				input.close();
			}
		}

		private Document parse(String xml) throws Exception {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		}

		private Element ensureSoapHeader(Document document) {
			Element envelope = document.getDocumentElement();
			Element header = findFirstByLocalName(envelope, "Header");
			if (header == null) {
				header = document.createElementNS(SOAP_NS, qualifyName(envelope.getPrefix(), "Header"));
				Element body = findFirstByLocalName(envelope, "Body");
				envelope.insertBefore(header, body);
			}
			return header;
		}

		private Element ensureSecurityHeader(Document document) {
			Element header = ensureSoapHeader(document);
			Element security = document.createElementNS(WSSE_NS, "wsse:Security");
			header.appendChild(security);
			return security;
		}

		private void appendBinarySecurityToken(Document document, Element securityHeader, String tokenId) throws Exception {
			Element binarySecurityToken = document.createElementNS(WSSE_NS, "wsse:BinarySecurityToken");
			binarySecurityToken.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", WSU_NS);
			binarySecurityToken.setAttribute("EncodingType", BASE64_BINARY_TYPE);
			binarySecurityToken.setAttribute("ValueType", X509_V3_TYPE);
			binarySecurityToken.setAttributeNS(WSU_NS, "wsu:Id", tokenId);
			binarySecurityToken.setTextContent(Base64.getEncoder().encodeToString(certificate.getEncoded()));
			securityHeader.appendChild(binarySecurityToken);
		}

		private void appendWsSecuritySignature(
			Document document,
			Element securityHeader,
			String tokenId,
			String keyInfoId,
			String securityTokenReferenceId) throws Exception {
			Element body = findFirstByLocalName(document.getDocumentElement(), "Body");
			if (body == null) {
				throw new IllegalStateException("No s'ha trobat el Body del SOAP");
			}
			body.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", WSU_NS);
			if (!body.hasAttribute("Id")) {
				body.setAttribute("Id", "Body");
			}
			String bodyId = body.getAttributeNS(WSU_NS, "Id");
			if (bodyId == null || bodyId.isEmpty()) {
				bodyId = "id-" + positiveRandomNumber();
				body.setAttributeNS(WSU_NS, "wsu:Id", bodyId);
			}
			body.setIdAttributeNS(WSU_NS, "Id", true);
			body.setIdAttribute("Id", true);

			XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
			List<Transform> transforms = Arrays.asList(
				signatureFactory.newTransform(CanonicalizationMethod.EXCLUSIVE, (javax.xml.crypto.dsig.spec.TransformParameterSpec) null)
			);
			Reference reference = signatureFactory.newReference(
				"#" + bodyId,
				signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
				transforms,
				null,
				null
			);
			SignedInfo signedInfo = signatureFactory.newSignedInfo(
				signatureFactory.newCanonicalizationMethod(
					CanonicalizationMethod.EXCLUSIVE,
					(javax.xml.crypto.dsig.spec.C14NMethodParameterSpec) null),
				signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
				Collections.singletonList(reference)
			);

			KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
			Element securityTokenReference = createSecurityTokenReference(document, tokenId, securityTokenReferenceId);
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(new DOMStructure(securityTokenReference)), keyInfoId);

			DOMSignContext signContext = new DOMSignContext(privateKey, securityHeader);
			signContext.setDefaultNamespacePrefix("ds");
			signContext.putNamespacePrefix(WSSE_NS, "wsse");
			signContext.putNamespacePrefix(WSU_NS, "wsu");
			XMLSignature signature = signatureFactory.newXMLSignature(
				signedInfo,
				keyInfo,
				null,
				"Signature-" + positiveRandomNumber(),
				null);
			signature.sign(signContext);
		}

		private void appendXmlSignature(Document document, Element header) throws Exception {
			Element body = findFirstByLocalName(document.getDocumentElement(), "Body");
			if (body == null) {
				throw new IllegalStateException("No s'ha trobat el Body del SOAP");
			}
			String bodyId = body.getAttribute("Id");
			if (bodyId == null || bodyId.isEmpty()) {
				bodyId = "MsgBody";
				body.setAttribute("Id", bodyId);
			}
			body.setIdAttribute("Id", true);

			XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
			List<Transform> transforms = Arrays.asList(
				signatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments",
					(javax.xml.crypto.dsig.spec.TransformParameterSpec) null)
			);
			Reference reference = signatureFactory.newReference(
				"#" + bodyId,
				signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
				transforms,
				null,
				null
			);
			SignedInfo signedInfo = signatureFactory.newSignedInfo(
				signatureFactory.newCanonicalizationMethod(
					"http://www.w3.org/TR/2001/REC-xml-c14n-20010315",
					(javax.xml.crypto.dsig.spec.C14NMethodParameterSpec) null),
				signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
				Collections.singletonList(reference)
			);

			KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(
				keyInfoFactory.newX509Data(Collections.singletonList(certificate)))
			);

			DOMSignContext signContext = new DOMSignContext(privateKey, header);
			signContext.setDefaultNamespacePrefix("ds");
			XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
			signature.sign(signContext);
		}

		private Element createSecurityTokenReference(Document document, String tokenId, String securityTokenReferenceId) {
			Element securityTokenReference = document.createElementNS(WSSE_NS, "wsse:SecurityTokenReference");
			securityTokenReference.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:wsu", WSU_NS);
			securityTokenReference.setAttributeNS(WSU_NS, "wsu:Id", securityTokenReferenceId);
			Element reference = document.createElementNS(WSSE_NS, "wsse:Reference");
			reference.setAttribute("URI", "#" + tokenId);
			reference.setAttribute("ValueType", X509_V3_TYPE);
			securityTokenReference.appendChild(reference);
			return securityTokenReference;
		}

		private void removeSecurityHeader(Document document) {
			Element envelope = document.getDocumentElement();
			Element header = findFirstByLocalName(envelope, "Header");
			if (header == null) {
				return;
			}
			List<Element> securityHeaders = findAllByNamespaceAndLocalName(header, WSSE_NS, "Security");
			for (Element securityHeader : securityHeaders) {
				header.removeChild(securityHeader);
			}
		}

		private void removePlainSignature(Document document) {
			Element envelope = document.getDocumentElement();
			Element header = findFirstByLocalName(envelope, "Header");
			if (header == null) {
				return;
			}
			List<Element> signatures = findAllByNamespaceAndLocalName(header, DS_NS, "Signature");
			for (Element signature : signatures) {
				header.removeChild(signature);
			}
		}

		private String toXml(Document document) throws Exception {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.toString();
		}

		private Element findFirstByLocalName(Element root, String localName) {
			if (root == null) {
				return null;
			}
			if (localName.equals(root.getLocalName())) {
				return root;
			}
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element) {
					Element found = findFirstByLocalName((Element) child, localName);
					if (found != null) {
						return found;
					}
				}
			}
			return null;
		}

		private List<Element> findAllByNamespaceAndLocalName(Element root, String namespace, String localName) {
			List<Element> elements = new ArrayList<>();
			collectByNamespaceAndLocalName(root, namespace, localName, elements);
			return elements;
		}

		private void collectByNamespaceAndLocalName(Element root, String namespace, String localName, List<Element> elements) {
			if (root == null) {
				return;
			}
			if (localName.equals(root.getLocalName()) && namespace.equals(root.getNamespaceURI())) {
				elements.add(root);
			}
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof Element) {
					collectByNamespaceAndLocalName((Element) child, namespace, localName, elements);
				}
			}
		}

		private String qualifyName(String prefix, String localName) {
			return prefix == null || prefix.isEmpty() ? localName : prefix + ":" + localName;
		}

		private long positiveRandomNumber() {
			return Math.abs(UUID.randomUUID().getMostSignificantBits());
		}
	}

	private static String readBody(InputStream inputStream) throws IOException {
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	private static void writeText(HttpExchange exchange, int status, String body) throws IOException {
		writeResponse(exchange, status, "text/plain; charset=UTF-8", body);
	}

	private static void writeXml(HttpExchange exchange, int status, String body) throws IOException {
		writeResponse(exchange, status, "text/xml; charset=UTF-8", body);
	}

	private static void writeResponse(HttpExchange exchange, int status, String contentType, String body) throws IOException {
		byte[] payload = body.getBytes(StandardCharsets.UTF_8);
		Headers headers = exchange.getResponseHeaders();
		headers.set("Content-Type", contentType);
		exchange.sendResponseHeaders(status, payload.length);
		exchange.getResponseBody().write(payload);
	}
}
