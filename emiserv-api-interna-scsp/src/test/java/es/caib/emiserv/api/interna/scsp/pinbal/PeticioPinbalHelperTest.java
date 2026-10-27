package es.caib.emiserv.api.interna.scsp.pinbal;

import es.scsp.bean.common.peticion.Peticion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprova que les peticions de prova es generen amb el mateix format que les de PINBAL.
 * No necessita cap EMISERV desplegat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class PeticioPinbalHelperTest {

	private static final String NS_PETICION = "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
	private static final String NS_SOLICITUD = "http://intermediacion.redsara.es/scsp/esquemas/V3/solicitudRespuesta";

	private Properties propietatsOriginals;

	@BeforeEach
	void setUp() {
		propietatsOriginals = (Properties)System.getProperties().clone();
		System.clearProperty(ConfiguracioProva.PROPIETAT_CONFIG);
		System.setProperty(ConfiguracioProva.PREFIX + "emisor.nif", "S0711001H");
		System.setProperty(ConfiguracioProva.PREFIX + "emisor.nom", "Emisor de proves");
		System.setProperty(ConfiguracioProva.PREFIX + "solicitant.id", "S0711001H");
		System.setProperty(ConfiguracioProva.PREFIX + "solicitant.nom", "Solicitant de proves");
	}

	@AfterEach
	void tearDown() {
		System.setProperties(propietatsOriginals);
	}

	@Test
	void peticioSimpleComPinbal() throws Exception {
		PeticioPinbalHelper helper = new PeticioPinbalHelper(new ConfiguracioProva());
		String idPeticion = helper.generarIdPeticio();
		Peticion peticion = helper.crearPeticion(idPeticion, "SVDPROVA");

		Element xml = PeticioPinbalHelper.marshal(peticion);

		assertTrue(idPeticion.length() <= 26);
		assertEquals(NS_PETICION, xml.getNamespaceURI());
		assertEquals("Peticion", xml.getLocalName());
		assertEquals(idPeticion, valor(xml, "IdPeticion"));
		assertEquals("1", valor(xml, "NumElementos"));
		assertEquals("SVDPROVA", valor(xml, "CodigoCertificado"));
		// Amb una única sol·licitud PINBAL posa l'id de la petició com a id de sol·licitud
		assertEquals(idPeticion, valor(xml, "IdSolicitud"));
		assertEquals("Si", valor(xml, "Consentimiento"));
		assertEquals("NIF", valor(xml, "TipoDocumentacion"));
		assertEquals("S0711001H", valor(xml, "NifEmisor"));
		assertEquals("PROVES", valor(xml, "Finalidad"));
		assertEquals(idPeticion, PeticioPinbalHelper.unmarshal(xml, Peticion.class).getAtributos().getIdPeticion());
	}

	@Test
	void peticioMultipleComPinbal() throws Exception {
		PeticioPinbalHelper helper = new PeticioPinbalHelper(new ConfiguracioProva());
		String idPeticion = helper.generarIdPeticio();

		Element xml = PeticioPinbalHelper.marshal(helper.crearPeticion(idPeticion, "SVDPROVA", 2));

		assertEquals("2", valor(xml, "NumElementos"));
		assertEquals("000001", xml.getElementsByTagNameNS(NS_PETICION, "IdSolicitud").item(0).getTextContent());
		assertEquals("000002", xml.getElementsByTagNameNS(NS_PETICION, "IdSolicitud").item(1).getTextContent());
	}

	@Test
	void solicitudRespuestaComPinbal() throws Exception {
		PeticioPinbalHelper helper = new PeticioPinbalHelper(new ConfiguracioProva());

		Element xml = PeticioPinbalHelper.marshal(helper.crearSolicitudRespuesta("PBLPROVA0001", "SVDPROVA", 1));

		assertEquals(NS_SOLICITUD, xml.getNamespaceURI());
		assertEquals("SolicitudRespuesta", xml.getLocalName());
		assertEquals("PBLPROVA0001", valor(xml, "IdPeticion"));
		assertEquals("S0711001H", valor(xml, "IdentificadorSolicitante"));
	}

	private static String valor(Element element, String localName) {
		return element.getElementsByTagNameNS("*", localName).item(0).getTextContent();
	}

}
