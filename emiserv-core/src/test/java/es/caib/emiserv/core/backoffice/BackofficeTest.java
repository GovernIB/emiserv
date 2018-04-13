/**
 * 
 */
package es.caib.emiserv.core.backoffice;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.caib.emiserv.core.api.service.ws.backoffice.Atributos;
import es.caib.emiserv.core.api.service.ws.backoffice.Consentimiento;
import es.caib.emiserv.core.api.service.ws.backoffice.DatosGenericos;
import es.caib.emiserv.core.api.service.ws.backoffice.EmiservBackoffice;
import es.caib.emiserv.core.api.service.ws.backoffice.Emisor;
import es.caib.emiserv.core.api.service.ws.backoffice.Funcionario;
import es.caib.emiserv.core.api.service.ws.backoffice.Peticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Procedimiento;
import es.caib.emiserv.core.api.service.ws.backoffice.Solicitante;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.core.api.service.ws.backoffice.Solicitudes;
import es.caib.emiserv.core.api.service.ws.backoffice.Transmision;
import es.caib.emiserv.core.service.ws.DatosEspecificosHandler;
import es.caib.emiserv.core.service.ws.PeticioRespostaHandler;
import es.caib.emiserv.core.service.ws.WsClientHelper;

/**
 * Test de peticions a backoffice.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeTest {

	private static final String SERVICE_URL = "https://proves.caib.es/sisdepen/ws/PinBalService";
	private static final String USERNAME = "$emiserv_sisdepen";
	private static final String PASSWORD = "emiserv_sisdepen";

	@Test
	public void peticioSincrona() throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException, ParserConfigurationException {
		ByteArrayOutputStream xmlPeticio = new ByteArrayOutputStream();
		ByteArrayOutputStream xmlResposta = new ByteArrayOutputStream();
		PeticioRespostaHandler peticioRespostaHandler = new PeticioRespostaHandler(
				xmlPeticio,
				xmlResposta);
		DatosEspecificosHandler datosEspecificosHandler = new DatosEspecificosHandler();
		getEmiservBackoffice(
				peticioRespostaHandler,
				datosEspecificosHandler).peticionSincrona(getPeticion());
		System.out.println(">>> PETICIO: " + xmlPeticio.toString());
		System.out.println(">>> RESPOSTA: " + xmlResposta.toString());
	}

	private Peticion getPeticion() throws ParserConfigurationException {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setCodigoCertificado("SVDSCDDWS01");
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
			PeticioRespostaHandler peticioRespostaHandler,
			DatosEspecificosHandler datosEspecificosHandler) throws InstanceNotFoundException, MalformedObjectNameException, MalformedURLException, RemoteException, NamingException, CreateException {
		EmiservBackoffice port = new WsClientHelper<EmiservBackoffice>().generarClientWs(
				getClass().getResource("/es/caib/emiserv/core/backoffice/EmiservBackoffice.wsdl"),
				SERVICE_URL,
				new QName(
						"http://caib.es/emiserv/backoffice",
						"EmiservBackofficeService"),
				USERNAME,
				PASSWORD,
				null,
				EmiservBackoffice.class,
				peticioRespostaHandler,
				datosEspecificosHandler);
		return port;
	}

}
