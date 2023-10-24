/**
 * 
 */
package es.caib.emiserv.logic.backoffice;

import es.caib.emiserv.logic.intf.service.ws.backoffice.*;
import es.caib.emiserv.logic.service.ws.DatosEspecificosHandler;
import es.caib.emiserv.logic.service.ws.PeticioRespostaHandler;
import es.caib.emiserv.logic.service.ws.WsClientHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Test de peticions a backoffice.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EmiservScSpBackofficeTest {

	private static final String SERVICE_URL = "http://localhost:8080/ws/EmiservBackoffice";
	private static final String USERNAME = null; // $
	private static final String PASSWORD = null;

	private static final String CODI_CERTIFICAT = "SVDSCDDWS01";


	@Test
	public void backofficePeticioSincrona() throws Exception {
		ByteArrayOutputStream xmlPeticio = new ByteArrayOutputStream();
		ByteArrayOutputStream xmlResposta = new ByteArrayOutputStream();
		PeticioRespostaHandler peticioRespostaHandler = new PeticioRespostaHandler(
				xmlPeticio,
				xmlResposta);
		DatosEspecificosHandler datosEspecificosHandler = new DatosEspecificosHandler();
		var respuesta = getEmiservBackoffice(
				SERVICE_URL,
				USERNAME,
				PASSWORD,
				peticioRespostaHandler,
				datosEspecificosHandler).peticionSincrona(getPeticion());
		System.out.println(">>> PETICIO: " + xmlPeticio.toString());
		System.out.println(">>> RESPOSTA: " + xmlResposta.toString());

		if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
			for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
				if (transmisionDatos != null && transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTransmision() != null) {
					String idSolicitud = transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud();
					Element datosEspecificos = datosEspecificosHandler.getDatosEspecificosRespostaComElement(
							idSolicitud);
					if (datosEspecificos != null) {
						transmisionDatos.setDatosEspecificos(datosEspecificos);
					}
					Assertions.assertNotNull(transmisionDatos.getDatosEspecificos());
				}
			}
		}
		modificarDatosEspecificosTransmisionResposta(
				respuesta,
				CODI_CERTIFICAT);
		emplenarCampsBuits(respuesta);
	}

	@Test
	public void backofficePeticioSincronaSenseDatosEspecificos() throws Exception {
		ByteArrayOutputStream xmlPeticio = new ByteArrayOutputStream();
		ByteArrayOutputStream xmlResposta = new ByteArrayOutputStream();
		PeticioRespostaHandler peticioRespostaHandler = new PeticioRespostaHandler(
				xmlPeticio,
				xmlResposta);
		DatosEspecificosHandler datosEspecificosHandler = new DatosEspecificosHandler();
		var respuesta = getEmiservBackoffice(
				SERVICE_URL,
				USERNAME,
				PASSWORD,
				peticioRespostaHandler,
				datosEspecificosHandler).peticionSincrona(getPeticionSenseDatosEspecificos());
		System.out.println(">>> PETICIO: " + xmlPeticio.toString());
		System.out.println(">>> RESPOSTA: " + xmlResposta.toString());

		if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
			for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
				if (transmisionDatos != null && transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTransmision() != null) {
					String idSolicitud = transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud();
					Element datosEspecificos = datosEspecificosHandler.getDatosEspecificosRespostaComElement(
							idSolicitud);
					if (datosEspecificos != null) {
						transmisionDatos.setDatosEspecificos(datosEspecificos);
					}
					Assertions.assertNotNull(transmisionDatos.getDatosEspecificos());
				}
			}
		}
		modificarDatosEspecificosTransmisionResposta(
				respuesta,
				CODI_CERTIFICAT);
		emplenarCampsBuits(respuesta);
	}

	private Peticion getPeticionSenseDatosEspecificos() throws ParserConfigurationException {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setCodigoCertificado("SVDCCAACPCWS01");
		atributos.setIdPeticion("PRECIB0000732152");
		atributos.setNumElementos("1");
		atributos.setTimeStamp("2023-07-19T14:26:05.458+02:00");
		peticion.setAtributos(atributos);
		Solicitudes solicitudes = new Solicitudes();
		SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Emisor emisor = new Emisor();
		emisor.setNombreEmisor("Baleares");
		emisor.setNifEmisor("S0711001H");
		datosGenericos.setEmisor(emisor);
		Solicitante solicitante = new Solicitante();
		solicitante.setConsentimiento(Consentimiento.Si);
		solicitante.setFinalidad("prueba del servicio");
		Funcionario funcionario = new Funcionario();
		funcionario.setNombreCompletoFuncionario("Adrián Menéndez Simarro");
		funcionario.setNifFuncionario("03914307Y");
		solicitante.setFuncionario(funcionario);
		solicitante.setIdentificadorSolicitante("S2800568D");
		solicitante.setNombreSolicitante("Ministerio Asuntos Económ. y Transf. Digital");
		Procedimiento procedimiento = new Procedimiento();
		procedimiento.setCodProcedimiento("S2800568D_TEST_00001");
		procedimiento.setNombreProcedimiento("Procedimiento de pruebas 00001");
		solicitante.setProcedimiento(procedimiento);
		solicitante.setUnidadTramitadora("Centro de Atención a Integradores Desarrolladores");
		solicitante.setConsentimiento(Consentimiento.Si);
		datosGenericos.setSolicitante(solicitante);
		Transmision transmision = new Transmision();
		transmision.setCodigoCertificado("SVDCCAACPCWS01");
		transmision.setIdSolicitud("PRECIB0000732152");
		transmision.setFechaGeneracion("2023-07-19T14:26:02.812+02:00");
		datosGenericos.setTransmision(transmision);
		Titular titular = new Titular();
		titular.setTipoDocumentacion(TipoDocumentacion.NIF);
		titular.setDocumentacion("41462511C");
		datosGenericos.setTitular(titular);
		solicitudTransmision.setDatosGenericos(datosGenericos);
		solicitudTransmision.setDatosEspecificos(null);
		ArrayList<SolicitudTransmision> solicitudesTransmision = new ArrayList<SolicitudTransmision>();
		solicitudesTransmision.add(solicitudTransmision);
		solicitudes.setSolicitudTransmision(solicitudesTransmision);
		peticion.setSolicitudes(solicitudes);
		return peticion;
	}

	private Peticion getPeticion() throws ParserConfigurationException {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setCodigoCertificado(CODI_CERTIFICAT);
		atributos.setIdPeticion("PBL00000000000000000001008");
		atributos.setNumElementos("1");
		atributos.setTimeStamp("2017-02-13T15:20:21.921+01:00");
		peticion.setAtributos(atributos);
		Solicitudes solicitudes = new Solicitudes();
		SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Emisor emisor = new Emisor();
		emisor.setNombreEmisor("CAIB");
		emisor.setNifEmisor("S0711001H");
		datosGenericos.setEmisor(emisor);
		Solicitante solicitante = new Solicitante();
		solicitante.setConsentimiento(Consentimiento.Si);
		solicitante.setFinalidad("ProvaConcepte#::##::#1234");
		Funcionario funcionario = new Funcionario();
		funcionario.setNombreCompletoFuncionario("Sion Andreu");
		funcionario.setNifFuncionario("97669911C");
		solicitante.setFuncionario(funcionario);
		solicitante.setIdentificadorSolicitante("B07167448");
		solicitante.setNombreSolicitante("Limit Tecnologies");
		Procedimiento procedimiento = new Procedimiento();
		procedimiento.setCodProcedimiento("ProvaConcepte");
		procedimiento.setNombreProcedimiento("Prova de concepte");
		solicitante.setProcedimiento(procedimiento);
		solicitante.setUnidadTramitadora("Departament d'atenció al cliente");
		datosGenericos.setSolicitante(solicitante);
		Transmision transmision = new Transmision();
		transmision.setCodigoCertificado("SVDSCDDWS01");
		transmision.setIdSolicitud("PBL00000000000000000001008");
		transmision.setFechaGeneracion("2017-02-13T15:20:21.894+01:00");
		datosGenericos.setTransmision(transmision);
		Titular titular = new Titular();
		titular.setTipoDocumentacion(TipoDocumentacion.DNI);
		titular.setDocumentacion("12345678X");
		titular.setNombre("Nom");
		titular.setApellido1("Linnatge1");
		titular.setApellido2("Linnatge2");
		datosGenericos.setTitular(titular);
		solicitudTransmision.setDatosGenericos(datosGenericos);
		solicitudTransmision.setDatosEspecificos(crearDatosEspecificos());
		ArrayList<SolicitudTransmision> solicitudesTransmision = new ArrayList<SolicitudTransmision>();
		solicitudesTransmision.add(solicitudTransmision);
		solicitudes.setSolicitudTransmision(solicitudesTransmision);
		peticion.setSolicitudes(solicitudes);
		return peticion;
	}

	private Element crearDatosEspecificos() throws ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc = fac.newDocumentBuilder().newDocument();
		Element datosEspecificos = doc.createElement("datosEspecificos");
		Element consulta = doc.createElement("Consulta");
		Element codigoComunidadAutonoma = doc.createElement("CodigoComunidadAutonoma");
		codigoComunidadAutonoma.setTextContent("07");
		consulta.appendChild(codigoComunidadAutonoma);
		Element consentimientoTiposDiscapacidad = doc.createElement("ConsentimientoTiposDiscapacidad");
		consentimientoTiposDiscapacidad.setTextContent("S");
		consulta.appendChild(consentimientoTiposDiscapacidad);
		datosEspecificos.appendChild(consulta);
		doc.appendChild(datosEspecificos);
		return doc.getDocumentElement();
	}

	private EmiservBackoffice getEmiservBackoffice(
			String backofficeUrl,
			String username,
			String password,
			PeticioRespostaHandler peticioRespostaHandler,
			DatosEspecificosHandler datosEspecificosHandler) throws Exception {
		EmiservBackoffice port = new WsClientHelper<EmiservBackoffice>().generarClientWs(
				getClass().getResource("/es/caib/emiserv/backoffice/EmiservBackoffice.wsdl"),
				backofficeUrl,
				new QName(
						"http://caib.es/emiserv/backoffice",
						"EmiservBackofficeService"),
				username,
				password,
				null,
				EmiservBackoffice.class,
				peticioRespostaHandler,
				datosEspecificosHandler);
		return port;
	}

	private void modificarDatosEspecificosTransmisionResposta(
			Respuesta respuesta,
			String serveiCodi) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, SAXException, IOException {
		for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
			if (transmisionDatos.getDatosEspecificos() != null) {
				Element datosEspecificos = (Element)transmisionDatos.getDatosEspecificos();
				String datosEspecificosModificats = modificarDatosEspecificosResposta(
						nodeToString(datosEspecificos),
						serveiCodi);
				transmisionDatos.setDatosEspecificos(
						bytesToDocument(datosEspecificosModificats.getBytes()).getDocumentElement());
			}
		}
	}

	private String nodeToString(Node node) throws TransformerFactoryConfigurationError, TransformerException {
		if (node == null)
			return null;
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}

	private Document bytesToDocument(
			byte[] bytes) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(
				new ByteArrayInputStream(bytes));
	}

	private String modificarDatosEspecificosResposta(
			String datosEspecificosText,
			String serveiCodi) {
		String token1 = "DatosEspecificos";
		String token2 = "datosEspecificos";
		int indexDatespInici = datosEspecificosText.indexOf(token1);
		int indexDatespFi = 0;
		if (indexDatespInici != -1) {
			indexDatespFi = indexDatespInici + token1.length();
		} else {
			indexDatespInici = datosEspecificosText.indexOf(token2);
			if (indexDatespInici != -1) {
				indexDatespFi = indexDatespInici + token2.length();
			}
		}
		int indexFi = datosEspecificosText.indexOf(">");
		if (indexDatespInici != -1) {
			String datosEspecificosAmbNs1 = datosEspecificosText.substring(1, indexDatespInici) + token1;
			StringBuilder datosEspecificosSb = new StringBuilder();
			datosEspecificosSb.append("<");
			datosEspecificosSb.append(datosEspecificosAmbNs1);
			int indexXmlns = datosEspecificosText.indexOf("xmlns");
			int indexXmlnsDatesp = datosEspecificosText.indexOf("esquemas/datosespecificos");
			boolean conteXmlns = indexXmlns != -1 && indexXmlnsDatesp != -1 && indexXmlnsDatesp < indexFi;
			if (!conteXmlns) {
				String xmlns = " xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"";
//				ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.findByCodigoCertificado(serveiCodi);
//				if (scspCoreServicio.getVersionEsquema().contains("2")) {
//					xmlns = " xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\"";
//				} else if (scspCoreServicio.getVersionEsquema().contains("3")) {
//					xmlns = " xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"";
//				} else {
//					xmlns = "";
//				}
				datosEspecificosSb.append(xmlns);
			}
			int indexDatespBarraInici = datosEspecificosText.indexOf("/" + datosEspecificosAmbNs1);
			if (indexDatespBarraInici == -1) {
				String datosEspecificosAmbNs2 = datosEspecificosText.substring(1, indexDatespInici) + token2;
				indexDatespBarraInici = datosEspecificosText.indexOf("/" + datosEspecificosAmbNs2);
			}
			datosEspecificosSb.append(datosEspecificosText.substring(indexDatespFi, indexDatespBarraInici + 1));
			datosEspecificosSb.append(datosEspecificosAmbNs1);
			datosEspecificosSb.append(">");
			return datosEspecificosSb.toString();
		} else {
			return null;
		}
	}

	private void emplenarCampsBuits(
			Respuesta respuesta) {
		if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
			for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
				if (transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTransmision() != null) {
					Transmision transmision = transmisionDatos.getDatosGenericos().getTransmision();
					if (transmision.getFechaGeneracion() == null || transmision.getFechaGeneracion().isEmpty()) {
						transmision.setFechaGeneracion(
								new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
					}
					if (transmision.getIdTransmision() == null || transmision.getIdTransmision().isEmpty()) {
						transmision.setIdTransmision(transmision.getIdSolicitud());
					}
				}
			}
		}
	}
}