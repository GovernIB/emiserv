/**
 * 
 */
package es.caib.emiserv.backoffice;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.caib.emiserv.backoffice.ws.EmiservBackoffice;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Funcionario;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Procedimiento;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.Solicitudes;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.Transmision;

/**
 * @author josepg
 *
 */
public class BackofficeTest {

	private static final String XMLNS_DATOS_ESPECIFICOS_V2 = "http://www.map.es/scsp/esquemas/datosespecificos";
	//private static final String XMLNS_DATOS_ESPECIFICOS_V3 = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";

	private static final String BACKOFFICE_URL = "http://localhost:8080/emiserv/ws/EmiservBackoffice";



	public static void main(String[] args) {
		try {
			BackofficeTest backofficeTest = new BackofficeTest();
			Peticion peticion = backofficeTest.crearPeticionBackoffice(
					"VDRSFWS02",
					"G07896004",
					"FUNDACIO IBIT",
					"IBIT_20101223_PRUEBA",
					"PROCEDIMIENTO DE PRUEBA FUNDACION IBIT",
					"MPR TESTER",
					"12345678Z", //"97669911C", //"12345678Z",
					"02634348C",
					"DNI");
			/*Respuesta respuesta = */backofficeTest.getEmiservBackoffice().peticionSincrona(peticion);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private Peticion crearPeticionBackoffice(
			String servicioCodi,
			String solicitanteCif,
			String solicitanteNombre,
			String procedimentCodi,
			String procedimentNom,
			String funcionarioNombre,
			String funcionarioNif,
			String titularDocumentacion,
			String titularTipoDocumentacion) throws Exception {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setCodigoCertificado(servicioCodi);
		peticion.setAtributos(atributos);
		Solicitudes solicitudes = new Solicitudes();
		solicitudes.setSolicitudTransmision(new ArrayList<SolicitudTransmision>());
		SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Solicitante solicitante = new Solicitante();
		solicitante.setIdentificadorSolicitante(solicitanteCif);
		solicitante.setNombreSolicitante(solicitanteNombre);
		solicitante.setFinalidad(procedimentCodi + "#::##::#" + procedimentNom);
		solicitante.setConsentimiento(Consentimiento.Si);
		Procedimiento procedimiento = new Procedimiento();
		procedimiento.setCodProcedimiento(procedimentCodi);
		procedimiento.setNombreProcedimiento(procedimentNom);
		solicitante.setProcedimiento(procedimiento);
		Funcionario funcionario = new Funcionario();
		funcionario.setNombreCompletoFuncionario(funcionarioNombre);
		funcionario.setNifFuncionario(funcionarioNif);
		solicitante.setFuncionario(funcionario);
		datosGenericos.setSolicitante(solicitante);
		Titular titular = new Titular();
		titular.setDocumentacion(titularDocumentacion);
		titular.setTipoDocumentacion(TipoDocumentacion.valueOf(titularTipoDocumentacion));
		datosGenericos.setTitular(titular);
		Transmision transmision = new Transmision();
		transmision.setCodigoCertificado(servicioCodi);
		datosGenericos.setTransmision(transmision);
		solicitudTransmision.setDatosGenericos(datosGenericos);
		solicitudTransmision.setDatosEspecificos(crearDatosEspecificos());
		solicitudes.getSolicitudTransmision().add(solicitudTransmision);
		peticion.setSolicitudes(solicitudes);
		return peticion;
	}

	private Element crearDatosEspecificos() throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc = fac.newDocumentBuilder().newDocument();
		Element datosEspecificos = doc.createElement("DatosEspecificos");
		datosEspecificos.setAttribute("xmlns", XMLNS_DATOS_ESPECIFICOS_V2);
		Element solicitanteDatos = doc.createElement("SolicitanteDatos");
		Element tipo = doc.createElement("Tipo");
		tipo.setTextContent("app");
		solicitanteDatos.appendChild(tipo);
		datosEspecificos.appendChild(solicitanteDatos);
		Element solicitud = doc.createElement("Solicitud");
		Element espanol = doc.createElement("Espanol");
		espanol.setTextContent("s");
		solicitud.appendChild(espanol);
		datosEspecificos.appendChild(solicitud);
		doc.appendChild(datosEspecificos);
		return doc.getDocumentElement();
	}

	@XmlElementRef(name = "datosEspecificos", type = JAXBElement.class)
	private EmiservBackoffice getEmiservBackoffice() throws Exception {
		Service service = Service.create(
				new URL(BACKOFFICE_URL + "?wsdl"),
				new QName(
						"http://caib.es/emiserv/backoffice",
						"EmiservBackofficeService"));
		EmiservBackoffice port = (EmiservBackoffice)service.getPort(
				new QName(
						"http://caib.es/emiserv/backoffice",
						"EmiservBackofficePort"),
				EmiservBackoffice.class);
		/*BindingProvider bindingProvider = (BindingProvider)port;
		bindingProvider.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				BACKOFFICE_URL);
		bindingProvider.getBinding().getHandlerChain().add(new SOAPLoggingHandler());*/
		return port;
	}	

}
