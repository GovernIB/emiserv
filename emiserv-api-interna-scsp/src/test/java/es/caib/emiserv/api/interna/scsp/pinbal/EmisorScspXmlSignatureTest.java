package es.caib.emiserv.api.interna.scsp.pinbal;

import es.caib.emiserv.api.interna.scsp.pinbal.ClientScspPinbal.RespostaScsp;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.common.signature.XMLDSigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Proves de l'emisor SCSP d'EMISERV amb un servei de tipus de seguretat XMLSignature (signatura
 * XMLDSig del Body, la que fan servir la majoria de serveis que PINBAL consulta a EMISERV).
 *
 * La petició se signa i la signatura de la resposta es valida amb la mateixa classe de les
 * llibreries SCSP 5 que fa servir PINBAL (es.scsp.common.signature.XMLDSigManager, xmlsec).
 *
 *   emiserv.scsp.prova.servei.xmlsignature=CODI_SERVEI
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@EnabledIf("es.caib.emiserv.api.interna.scsp.pinbal.EmisorScspXmlSignatureTest#isConfigurada")
class EmisorScspXmlSignatureTest {

	private static final String NS_RESPUESTA = "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";

	private ConfiguracioProva configuracio;
	private PeticioPinbalHelper peticioHelper;
	private ClientScspPinbal client;

	static boolean isConfigurada() {
		ConfiguracioProva configuracio = new ConfiguracioProva();
		return configuracio.get("url") != null && configuracio.get("servei.xmlsignature") != null;
	}

	@BeforeEach
	void setUp() {
		configuracio = new ConfiguracioProva();
		peticioHelper = new PeticioPinbalHelper(configuracio);
		client = new ClientScspPinbal(configuracio);
	}

	@ParameterizedTest
	@ValueSource(strings = {"/peticionSincrona", "/services/peticionSincrona"})
	void peticioSincronaXmlSignature(String path) throws Exception {
		String serveiCodi = configuracio.get("servei.xmlsignature");
		String idPeticion = peticioHelper.generarIdPeticio();
		Peticion peticion = peticioHelper.crearPeticion(idPeticion, serveiCodi);
		RespostaScsp resposta = client.enviarXmlSignature(
				configuracio.getUrlBase() + path,
				ClientScspPinbal.ACCIO_SINCRONA,
				PeticioPinbalHelper.marshal(peticion));
		assertFalse(resposta.isFault(), "L'emisor ha retornat un SOAP Fault " + resposta.getFaultString() + ":\n" + resposta.toXml());
		assertEquals(NS_RESPUESTA, resposta.getPayload().getNamespaceURI(), resposta.toXml());
		Respuesta respuesta = PeticioPinbalHelper.unmarshal(resposta.getPayload(), Respuesta.class);
		assertEquals(idPeticion, respuesta.getAtributos().getIdPeticion());
		assertEquals(configuracio.getEstatEsperat(), respuesta.getAtributos().getEstado().getCodigoEstado(), resposta.toXml());
		X509Certificate signant = new XMLDSigManager().validateSignature(resposta.getDocument());
		assertNotNull(signant, "La resposta no està signada amb XMLSignature:\n" + resposta.toXml());
	}

}
