package es.caib.emiserv.api.interna.scsp.pinbal;

import es.caib.emiserv.api.interna.scsp.pinbal.ClientScspPinbal.RespostaScsp;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Respuesta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Proves de l'emisor SCSP d'EMISERV amb un servei que xifra les dades específiques (WS-Security
 * amb xifratge, com els serveis xifrats que consulta PINBAL).
 *
 * Comprova que l'emisor desxifra la petició i xifra i signa la resposta. Si les classes de WSS4J o
 * de xmlsec es carreguessin dels mòduls del JBoss (WSS4J 2.2.2) en lloc del war (2.4.1), el
 * xifratge fallaria (NoSuchMethodError a WSSecEncrypt.build) i l'emisor retornaria un SOAP Fault.
 *
 * El servei de l'emisor ha de tenir tipus de seguretat WS-Security, una clau de xifratge i les
 * parts a xifrar en el format de WSS4J ({Content}{namespace}DatosEspecificos). Propietats:
 *
 *   emiserv.scsp.prova.servei.xifrat=CODI_SERVEI
 *   emiserv.scsp.prova.xifrat.alias.emisor=alias del certificat de l'emisor al keystore de les proves
 *   emiserv.scsp.prova.dades.especifiques=/ruta/datos-especificos.xml
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@EnabledIf("es.caib.emiserv.api.interna.scsp.pinbal.EmisorScspXifratTest#isConfigurada")
class EmisorScspXifratTest {

	private static final String NS_RESPUESTA = "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";
	private static final String NS_XMLENC = "http://www.w3.org/2001/04/xmlenc#";
	private static final String PARTS_PER_DEFECTE =
			"{Content}{" + PeticioPinbalHelper.NAMESPACE_DATOS_ESPECIFICOS + "}DatosEspecificos";

	private ConfiguracioProva configuracio;
	private PeticioPinbalHelper peticioHelper;
	private ClientScspPinbal client;

	static boolean isConfigurada() {
		ConfiguracioProva configuracio = new ConfiguracioProva();
		return configuracio.get("url") != null && configuracio.get("servei.xifrat") != null;
	}

	@BeforeEach
	void setUp() {
		configuracio = new ConfiguracioProva();
		peticioHelper = new PeticioPinbalHelper(configuracio);
		client = new ClientScspPinbal(configuracio);
	}

	@ParameterizedTest
	@ValueSource(strings = {"/peticionSincrona", "/services/peticionSincrona"})
	void peticioSincronaXifrada(String path) throws Exception {
		String serveiCodi = configuracio.get("servei.xifrat");
		String aliasEmisor = configuracio.get("xifrat.alias.emisor");
		assertNotNull(aliasEmisor, "Falta la propietat " + ConfiguracioProva.PREFIX + "xifrat.alias.emisor");
		String parts = configuracio.get("xifrat.parts") != null ? configuracio.get("xifrat.parts") : PARTS_PER_DEFECTE;
		String idPeticion = peticioHelper.generarIdPeticio();
		Peticion peticion = peticioHelper.crearPeticion(idPeticion, serveiCodi);
		assertNotNull(
				peticion.getSolicitudes().getSolicitudTransmision().get(0).getDatosEspecificos(),
				"La petició no té dades específiques per xifrar (propietat " + ConfiguracioProva.PREFIX + "dades.especifiques)");
		RespostaScsp resposta;
		try {
			resposta = client.enviarXifrat(
					configuracio.getUrlBase() + path,
					ClientScspPinbal.ACCIO_SINCRONA,
					PeticioPinbalHelper.marshal(peticion),
					aliasEmisor,
					parts);
		} catch (Exception ex) {
			// La validació WSS4J de la resposta falla si no ve xifrada i signada
			fail("No s'ha pogut desxifrar o validar la resposta de l'emisor: " + ex, ex);
			return;
		}
		assertTrue(!resposta.isFault(), "L'emisor ha retornat un SOAP Fault " + resposta.getFaultString() + ":\n" + resposta.toXml());
		assertEquals(NS_RESPUESTA, resposta.getPayload().getNamespaceURI(), resposta.toXml());
		assertEquals(0, resposta.getDocument().getElementsByTagNameNS(NS_XMLENC, "EncryptedData").getLength(),
				"La resposta no s'ha desxifrat:\n" + resposta.toXml());
		Respuesta respuesta = PeticioPinbalHelper.unmarshal(resposta.getPayload(), Respuesta.class);
		assertEquals(idPeticion, respuesta.getAtributos().getIdPeticion());
		assertEquals(configuracio.getEstatEsperat(), respuesta.getAtributos().getEstado().getCodigoEstado(), resposta.toXml());
		Element datosEspecificos = (Element)resposta.getDocument().getElementsByTagNameNS(
				PeticioPinbalHelper.NAMESPACE_DATOS_ESPECIFICOS, "DatosEspecificos").item(0);
		assertNotNull(datosEspecificos, "La resposta desxifrada no té dades específiques:\n" + resposta.toXml());
	}

}
