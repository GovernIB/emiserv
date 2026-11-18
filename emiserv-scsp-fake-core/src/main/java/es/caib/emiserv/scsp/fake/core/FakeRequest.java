package es.caib.emiserv.scsp.fake.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static es.caib.emiserv.scsp.fake.core.FakeNs.*;

/**
 * Dades d'una petició SCSP (v2/v3, amb o sense sobre SOAP) o d'una {@code peticion} del model
 * de backoffice d'EMISERV, necessàries per generar una resposta fake.
 */
public final class FakeRequest {
	final String bodyRoot;
	final String bodyNamespace;
	final String serviceCode;
	final String entityCode;
	final boolean forceError;
	final boolean noData;
	final SignatureStyle signatureStyle;
	final String idPeticion;
	final String numElementos;
	final String tipusDocumentacio;
	final String titularDocumentacio;
	final String titularNom;
	final String titularLlinatge1;
	final String titularLlinatge2;
	final String titularDataNaixement;
	final String comunitatAutonoma;
	final String nifEmisor;
	final String nomEmisor;
	final String identificadorSolicitant;
	final String nomSolicitant;
	final String unitatTramitadora;
	final String codiProcediment;
	final String nomProcediment;
	final String finalitat;
	final String consentiment;
	final String nomCompletFuncionari;
	final String nifFuncionari;
	final List<String> idSolicitudes;
	// Sol·licitud del padró (SCDCPAJU, SCDHPAJU): DatosEspecificos/Solicitud
	final String provinciaSolicitud;
	final String municipioSolicitud;
	final String documentSolicitud;

	private FakeRequest(
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
		List<String> idSolicitudes,
		String provinciaSolicitud,
		String municipioSolicitud,
		String documentSolicitud) {
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
		this.provinciaSolicitud = provinciaSolicitud;
		this.municipioSolicitud = municipioSolicitud;
		this.documentSolicitud = documentSolicitud;
	}

	public static FakeRequest fromXml(String xml) throws IOException {
		try {
			DocumentBuilderFactory factory = XmlSegur.documentBuilderFactory();
			Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
			Element bodyRoot = findSoapBodyRoot(document);
			if (bodyRoot == null) {
				// Petició sense sobre SOAP: l'arrel del document és la petició
				bodyRoot = document.getDocumentElement();
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
			Element solicitudPadro = firstElement(bodyRoot, "Solicitud");
			SignatureStyle signatureStyle = detectSignatureStyle(document);
			return new FakeRequest(
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
				idSolicitudes,
				solicitudPadro != null ? firstValue(solicitudPadro, "ProvinciaSolicitud") : null,
				solicitudPadro != null ? firstValue(solicitudPadro, "MunicipioSolicitud") : null,
				solicitudPadro != null ? firstValue(solicitudPadro, "Valor") : null);
		} catch (Exception e) {
			throw new IOException("No s'ha pogut interpretar l'XML d'entrada", e);
		}
	}

	/** Còpia de la petició amb les dades que venen de la ruta (servei, entitat, mode error/sense dades). */
	public FakeRequest withRuta(String serviceCodeRuta, String entityCodeRuta, boolean forceErrorRuta, boolean noDataRuta) {
		return new FakeRequest(
			bodyRoot,
			bodyNamespace,
			serviceCodeRuta != null ? serviceCodeRuta : serviceCode,
			entityCodeRuta,
			forceErrorRuta,
			noDataRuta,
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
			idSolicitudes,
			provinciaSolicitud,
			municipioSolicitud,
			documentSolicitud);
	}

	public String getBodyRoot() { return bodyRoot; }
	public String getServiceCode() { return serviceCode; }
	public String getEntityCode() { return entityCode; }
	public boolean isForceError() { return forceError; }
	public boolean isNoData() { return noData; }
	public SignatureStyle getSignatureStyle() { return signatureStyle; }
	public String getIdPeticion() { return idPeticion; }
	public String getNumElementos() { return numElementos; }
	public String getTitularDocumentacio() { return titularDocumentacio; }
	public String getTitularNom() { return titularNom; }
	public String getTitularLlinatge1() { return titularLlinatge1; }
	public String getTitularLlinatge2() { return titularLlinatge2; }
	public List<String> getIdSolicitudes() { return idSolicitudes; }

	/** Identificadors de sol·licitud de la petició; si no en té, un de generat a partir de l'identificador de petició. */
	public List<String> getSolicitudIds() {
		return effectiveSolicitudIds();
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

	private static Element firstElement(Element root, String localName) {
		NodeList nodes = root.getElementsByTagNameNS("*", localName);
		return nodes.getLength() > 0 ? (Element)nodes.item(0) : null;
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

	static String firstNonBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return null;
	}
}
