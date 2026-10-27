package es.caib.emiserv.logic.helper;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Lectura de les sol·licituds de les peticions asíncrones que es processen com a síncrones
 * (petició SCSP v3 guardada per les llibreries SCSP a CORE_TOKEN_DATA).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class BackofficeHelperPeticioPendentTest {

	private static final String PETICIO =
			"<ns3:Peticion xmlns:ns3=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\" " +
			"xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">" +
			"<ns3:Atributos><ns3:IdPeticion>PBLPROVA0001</ns3:IdPeticion><ns3:NumElementos>2</ns3:NumElementos></ns3:Atributos>" +
			"<ns3:Solicitudes>" +
			solicitud("000001", "S0000001A", "040") +
			solicitud("000002", "S0000002B", "001") +
			"</ns3:Solicitudes></ns3:Peticion>";

	private final XmlHelper xmlHelper = new XmlHelper();
	private final BackofficeHelper backofficeHelper = new BackofficeHelper();

	@Test
	void obteLaSolicitudIndicada() throws Exception {
		ReflectionTestUtils.setField(backofficeHelper, "xmlHelper", xmlHelper);

		Element segona = ReflectionTestUtils.invokeMethod(backofficeHelper, "getElementSolicitudTransmision", PETICIO, "000002");

		assertNotNull(segona, "No s'ha trobat la segona sol·licitud");
		// Dades genèriques i específiques de la sol·licitud (no de la primera de la petició)
		assertEquals("S0000002B", xmlHelper.getTextFromFirstNode(segona,
				"./peticion3:DatosGenericos/peticion3:Emisor/peticion3:NifEmisor"));
		Node datosEspecificos = xmlHelper.getFirstNode(segona, "./*[local-name()='DatosEspecificos']");
		assertNotNull(datosEspecificos, "No s'han trobat les dades específiques (namespace datosespecificos)");
		assertEquals("http://intermediacion.redsara.es/scsp/esquemas/datosespecificos", datosEspecificos.getNamespaceURI());
		assertEquals("001", ((Element)datosEspecificos).getElementsByTagNameNS("*", "MunicipioSolicitud").item(0).getTextContent());
	}

	private static String solicitud(String idSolicitud, String nifEmisor, String municipi) {
		return "<ns3:SolicitudTransmision><ns3:DatosGenericos>" +
				"<ns3:Emisor><ns3:NifEmisor>" + nifEmisor + "</ns3:NifEmisor><ns3:NombreEmisor>Emisor</ns3:NombreEmisor></ns3:Emisor>" +
				"<ns3:Titular><ns3:TipoDocumentacion>NIF</ns3:TipoDocumentacion></ns3:Titular>" +
				"<ns3:Transmision><ns3:IdSolicitud>" + idSolicitud + "</ns3:IdSolicitud></ns3:Transmision>" +
				"</ns3:DatosGenericos>" +
				"<ns2:DatosEspecificos><Solicitud xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">" +
				"<ProvinciaSolicitud>7</ProvinciaSolicitud><MunicipioSolicitud>" + municipi + "</MunicipioSolicitud></Solicitud>" +
				"</ns2:DatosEspecificos></ns3:SolicitudTransmision>";
	}

}
