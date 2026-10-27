package es.caib.emiserv.api.interna.scsp.pinbal;

import es.caib.emiserv.api.interna.scsp.pinbal.ClientScspPinbal.RespostaScsp;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.respuesta.TransmisionDatos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Proves de l'emisor SCSP d'EMISERV (llibreries SCSP 5 amb Spring-WS) fent les peticions de
 * la mateixa manera que PINBAL, per comprovar que el canvi d'Axis2 a Spring-WS no afecta els
 * requeridors.
 *
 * Les proves necessiten un EMISERV desplegat i només s'executen si s'indica la URL de l'emisor:
 *
 *   mvn -pl emiserv-api-interna-scsp test -DskipTests=false -Dtest=EmisorScspPinbalTest \
 *       -Demiserv.scsp.prova.config=/ruta/emisor-scsp-prova.properties
 *
 * Vegeu src/test/resources/emisor-scsp-prova.properties.example per a la configuració.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@EnabledIf("es.caib.emiserv.api.interna.scsp.pinbal.ConfiguracioProva#isConfigurada")
class EmisorScspPinbalTest {

	private static final String NS_RESPUESTA = "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";
	private static final String NS_CONFIRMACION = "http://intermediacion.redsara.es/scsp/esquemas/V3/confirmacionPeticion";
	private static final String ESTAT_PENDENT = "0002";
	private static final String ESTAT_TRAMITADA = "0003";

	private ConfiguracioProva configuracio;
	private PeticioPinbalHelper peticioHelper;
	private ClientScspPinbal client;

	@BeforeEach
	void setUp() {
		configuracio = new ConfiguracioProva();
		peticioHelper = new PeticioPinbalHelper(configuracio);
		client = new ClientScspPinbal(configuracio);
	}

	/**
	 * Petició síncrona signada. Es prova l'URL nova i les URLs d'Axis2 (/services/..., acabades en
	 * barra o amb l'adreça de l'endpoint del WSDL d'Axis2), que poden tenir configurades els requeridors.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
			"/peticionSincrona",
			"/services/peticionSincrona",
			"/services/peticionSincrona/",
			"/services/peticionSincrona.peticionSincronaHttpSoap11Endpoint/"})
	void peticioSincrona(String path) throws Exception {
		String serveiCodi = configuracio.getServeiCodi();
		String idPeticion = peticioHelper.generarIdPeticio();
		Peticion peticion = peticioHelper.crearPeticion(idPeticion, serveiCodi);

		RespostaScsp resposta = client.enviar(
				configuracio.getUrlBase() + path,
				ClientScspPinbal.ACCIO_SINCRONA,
				PeticioPinbalHelper.marshal(peticion));

		assertNoFault(resposta);
		assertEquals(NS_RESPUESTA, resposta.getPayload().getNamespaceURI(), resposta.toXml());
		assertEquals("Respuesta", resposta.getPayload().getLocalName(), resposta.toXml());
		Respuesta respuesta = PeticioPinbalHelper.unmarshal(resposta.getPayload(), Respuesta.class);
		assertEquals(idPeticion, respuesta.getAtributos().getIdPeticion());
		assertEquals(serveiCodi, respuesta.getAtributos().getCodigoCertificado());
		assertEquals(
				configuracio.getEstatEsperat(),
				respuesta.getAtributos().getEstado().getCodigoEstado(),
				"Estat inesperat: " + respuesta.getAtributos().getEstado().getLiteralError());
		assertNotNull(respuesta.getTransmisiones(), "La resposta no conté transmissions");
		assertEquals(1, respuesta.getTransmisiones().getTransmisionDatos().size());
		TransmisionDatos transmisionDatos = respuesta.getTransmisiones().getTransmisionDatos().get(0);
		assertEquals(idPeticion, transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud());
		assertFalse(
				isBuit(transmisionDatos.getDatosGenericos().getTransmision().getIdTransmision()),
				"La transmissió no té IdTransmision");
		assertSignaturaResposta(resposta);
	}

	/**
	 * Petició asíncrona + sol·licitud de resposta, com fa PINBAL amb realizaPeticionAsincrona
	 * i recuperaRespuesta (les dues van a l'URL asíncrona del servei).
	 */
	@ParameterizedTest
	@ValueSource(strings = {
			"/peticionAsincrona",
			"/services/peticionAsincrona",
			"/services/peticionAsincrona.peticionAsincronaHttpSoap11Endpoint/"})
	void peticioAsincrona(String path) throws Exception {
		String serveiCodi = configuracio.getServeiAsincronCodi();
		assumeTrue(serveiCodi != null, "No s'ha configurat cap servei asíncron (" + ConfiguracioProva.PREFIX + "servei.asincron)");
		String url = configuracio.getUrlBase() + path;
		String idPeticion = peticioHelper.generarIdPeticio();
		Peticion peticion = peticioHelper.crearPeticion(idPeticion, serveiCodi);

		RespostaScsp confirmacio = client.enviar(url, ClientScspPinbal.ACCIO_ASINCRONA, PeticioPinbalHelper.marshal(peticion));

		assertNoFault(confirmacio);
		assertEquals(NS_CONFIRMACION, confirmacio.getPayload().getNamespaceURI(), confirmacio.toXml());
		ConfirmacionPeticion confirmacionPeticion = PeticioPinbalHelper.unmarshal(confirmacio.getPayload(), ConfirmacionPeticion.class);
		assertEquals(idPeticion, confirmacionPeticion.getAtributos().getIdPeticion());
		assertEquals(ESTAT_PENDENT, confirmacionPeticion.getAtributos().getEstado().getCodigoEstado());
		assertSignaturaResposta(confirmacio);

		long limit = System.currentTimeMillis() + configuracio.getTimeoutSegons() * 1000L;
		Respuesta respuesta;
		RespostaScsp resposta;
		do {
			Thread.sleep(2000);
			resposta = client.enviar(
					url,
					ClientScspPinbal.ACCIO_SOLICITUD_RESPUESTA,
					PeticioPinbalHelper.marshal(peticioHelper.crearSolicitudRespuesta(idPeticion, serveiCodi, 1)));
			assertNoFault(resposta);
			respuesta = PeticioPinbalHelper.unmarshal(resposta.getPayload(), Respuesta.class);
		} while (ESTAT_PENDENT.equals(respuesta.getAtributos().getEstado().getCodigoEstado()) && System.currentTimeMillis() < limit);

		assertEquals(idPeticion, respuesta.getAtributos().getIdPeticion());
		assertEquals(
				ESTAT_TRAMITADA,
				respuesta.getAtributos().getEstado().getCodigoEstado(),
				"Estat inesperat: " + respuesta.getAtributos().getEstado().getLiteralError());
		assertNotNull(respuesta.getTransmisiones(), "La resposta no conté transmissions");
		assertEquals(1, respuesta.getTransmisiones().getTransmisionDatos().size());
		assertSignaturaResposta(resposta);
	}

	/**
	 * Els errors s'han de retornar com a SOAP Fault amb HTTP 500 i amb els camps que llegeix
	 * el client de PINBAL (ScspSoapFaultMessageResolver): IdPeticion, CodigoEstado i LiteralError.
	 */
	@Test
	void peticioSenseSignaturaRetornaFault() throws Exception {
		String idPeticion = peticioHelper.generarIdPeticio();
		Peticion peticion = peticioHelper.crearPeticion(idPeticion, configuracio.getServeiCodi());

		RespostaScsp resposta = client.enviar(
				configuracio.getUrlBase() + "/peticionSincrona",
				ClientScspPinbal.ACCIO_SINCRONA,
				PeticioPinbalHelper.marshal(peticion),
				false);

		assertFaultScsp(resposta, idPeticion);
	}

	@Test
	void peticioServeiInexistentRetornaFault() throws Exception {
		String idPeticion = peticioHelper.generarIdPeticio();
		Peticion peticion = peticioHelper.crearPeticion(idPeticion, "EMSPROVAINEXISTENT");

		RespostaScsp resposta = client.enviar(
				configuracio.getUrlBase() + "/peticionSincrona",
				ClientScspPinbal.ACCIO_SINCRONA,
				PeticioPinbalHelper.marshal(peticion));

		assertFaultScsp(resposta, idPeticion);
	}

	/** Compatibilitat amb les URLs de consulta del WSDL d'Axis2 (WsdlCompatibilitatFilter). */
	@ParameterizedTest
	@ValueSource(strings = {"/peticionSincrona", "/services/peticionSincrona", "/services/peticionAsincrona"})
	void wsdl(String path) throws Exception {
		String url = configuracio.getUrlBase() + path;

		HttpResponse<String> wsdl = get(url + "?wsdl");

		assertEquals(200, wsdl.statusCode());
		assertTrue(wsdl.body().contains("wsdl:definitions"), wsdl.body());
		assertTrue(wsdl.body().contains("soapAction=\"peticionSincrona\""), wsdl.body());
		assertTrue(wsdl.body().contains("location=\"" + url + "\""), "L'adreça del servei no és la consultada: " + wsdl.body());
		assertTrue(wsdl.body().contains("schemaLocation=\"" + url + "?xsd=peticion.xsd\""), wsdl.body());

		HttpResponse<String> xsd = get(url + "?xsd=peticion.xsd");

		assertEquals(200, xsd.statusCode());
		assertTrue(xsd.body().contains("name=\"Peticion\""), xsd.body());
		assertEquals(404, get(url + "?xsd=../web.xml").statusCode());
	}

	private void assertNoFault(RespostaScsp resposta) {
		if (resposta.isFault()) {
			fail("L'emisor ha retornat un SOAP Fault [" + resposta.getValor("CodigoEstado") + "] " +
					resposta.getValor("LiteralError") + ":\n" + resposta.toXml());
		}
	}

	private void assertFaultScsp(RespostaScsp resposta, String idPeticion) {
		assertTrue(resposta.isFault(), "S'esperava un SOAP Fault:\n" + resposta.toXml());
		assertTrue(resposta.isFaultHttp500(), "El SOAP Fault no s'ha retornat amb HTTP 500:\n" + resposta.toXml());
		assertEquals(idPeticion, resposta.getValor("IdPeticion"), resposta.toXml());
		assertFalse(isBuit(resposta.getValor("CodigoEstado")), "El SOAP Fault no té CodigoEstado:\n" + resposta.toXml());
		assertFalse(isBuit(resposta.getValor("LiteralError")), "El SOAP Fault no té LiteralError:\n" + resposta.toXml());
		System.out.println("SOAP Fault rebut: [" + resposta.getValor("CodigoEstado") + "] " + resposta.getValor("LiteralError"));
	}

	private void assertSignaturaResposta(RespostaScsp resposta) throws Exception {
		if (SignaturaRespostaHelper.isSignada(resposta.getDocument())) {
			SignaturaRespostaHelper.validar(resposta.getDocument());
		} else if (configuracio.isSignaturaRespostaObligatoria()) {
			fail("La resposta no està signada:\n" + resposta.toXml());
		}
	}

	private HttpResponse<String> get(String url) throws Exception {
		HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(configuracio.getTimeoutSegons())).build();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.timeout(Duration.ofSeconds(configuracio.getTimeoutSegons()))
				.GET()
				.build();
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private static boolean isBuit(String valor) {
		return valor == null || valor.isBlank();
	}

}
