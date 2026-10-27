package es.caib.emiserv.api.interna.scsp.pinbal;

import com.sun.net.httpserver.HttpServer;
import es.caib.emiserv.api.interna.scsp.pinbal.ClientScspPinbal.RespostaScsp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprova el client de proves contra un servidor HTTP local: que les peticions surten signades
 * i amb la capçalera SOAPAction igual que les de PINBAL, i que els SOAP Fault es distingeixen
 * segons el codi HTTP. No necessita cap EMISERV desplegat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class ClientScspPinbalTest {

	private static final String ALIAS = "pinbal";
	private static final String PASSWORD = "proves";
	private static final String FAULT = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>"
			+ "<soapenv:Fault><faultcode>soapenv:Server</faultcode><faultstring>Error de proves</faultstring><detail>"
			+ "<fault:fault xmlns:fault=\"http://intermediacion.redsara.es/scsp/esquemas/V3/fault\">"
			+ "<fault:IdPeticion>%s</fault:IdPeticion><fault:TimeStamp>2025-01-01T00:00:00.000+01:00</fault:TimeStamp>"
			+ "<fault:CodigoCertificado>SVDPROVA</fault:CodigoCertificado><fault:CodigoEstado>0227</fault:CodigoEstado>"
			+ "<fault:LiteralError>Error de proves</fault:LiteralError></fault:fault>"
			+ "</detail></soapenv:Fault></soapenv:Body></soapenv:Envelope>";

	@TempDir
	Path tempDir;

	private HttpServer server;
	private Properties propietatsOriginals;
	private final AtomicReference<byte[]> peticioRebuda = new AtomicReference<>();
	private final AtomicReference<String> soapActionRebuda = new AtomicReference<>();

	@BeforeEach
	void setUp() throws Exception {
		Path keystore = tempDir.resolve("pinbal.p12");
		Process keytool = new ProcessBuilder(
				Paths.get(System.getProperty("java.home"), "bin", "keytool").toString(),
				"-genkeypair", "-alias", ALIAS, "-keyalg", "RSA", "-keysize", "2048", "-validity", "2",
				"-dname", "CN=PINBAL proves, O=CAIB, C=ES",
				"-storetype", "PKCS12", "-keystore", keystore.toString(),
				"-storepass", PASSWORD, "-keypass", PASSWORD).inheritIO().start();
		assertEquals(0, keytool.waitFor(), "No s'ha pogut generar el keystore de proves amb keytool");
		// La configuració de les proves contra EMISERV no s'ha de fer servir aquí
		propietatsOriginals = (Properties)System.getProperties().clone();
		System.clearProperty(ConfiguracioProva.PROPIETAT_CONFIG);
		System.setProperty(ConfiguracioProva.PREFIX + "keystore.path", keystore.toString());
		System.setProperty(ConfiguracioProva.PREFIX + "keystore.password", PASSWORD);
		System.setProperty(ConfiguracioProva.PREFIX + "keystore.alias", ALIAS);
		System.setProperty(ConfiguracioProva.PREFIX + "emisor.nif", "S0711001H");
		System.setProperty(ConfiguracioProva.PREFIX + "emisor.nom", "Emisor de proves");
		System.setProperty(ConfiguracioProva.PREFIX + "solicitant.id", "S0711001H");
		System.setProperty(ConfiguracioProva.PREFIX + "solicitant.nom", "Solicitant de proves");
	}

	@AfterEach
	void tearDown() {
		if (server != null) {
			server.stop(0);
		}
		if (propietatsOriginals != null) {
			System.setProperties(propietatsOriginals);
		}
	}

	@Test
	void peticioSignadaComPinbal() throws Exception {
		iniciarServidor(500);
		ConfiguracioProva configuracio = new ConfiguracioProva();
		PeticioPinbalHelper helper = new PeticioPinbalHelper(configuracio);
		String idPeticion = helper.generarIdPeticio();

		new ClientScspPinbal(configuracio).enviar(
				url(),
				ClientScspPinbal.ACCIO_SINCRONA,
				PeticioPinbalHelper.marshal(helper.crearPeticion(idPeticion, "SVDPROVA")));

		assertEquals("\"peticionSincrona\"", soapActionRebuda.get());
		Document peticio = parse(peticioRebuda.get());
		assertTrue(SignaturaRespostaHelper.isSignada(peticio), new String(peticioRebuda.get(), StandardCharsets.UTF_8));
		X509Certificate certificat = SignaturaRespostaHelper.validar(peticio);
		assertEquals("CN=PINBAL proves, O=CAIB, C=ES", certificat.getSubjectX500Principal().getName().replace(",", ", "));
		assertEquals(idPeticion, peticio.getElementsByTagNameNS("*", "IdPeticion").item(0).getTextContent());
	}

	@Test
	void peticioSenseSignatura() throws Exception {
		iniciarServidor(500);
		ConfiguracioProva configuracio = new ConfiguracioProva();
		PeticioPinbalHelper helper = new PeticioPinbalHelper(configuracio);

		new ClientScspPinbal(configuracio).enviar(
				url(),
				ClientScspPinbal.ACCIO_SINCRONA,
				PeticioPinbalHelper.marshal(helper.crearPeticion(helper.generarIdPeticio(), "SVDPROVA")),
				false);

		assertFalse(SignaturaRespostaHelper.isSignada(parse(peticioRebuda.get())));
	}

	@Test
	void faultAmbHttp500() throws Exception {
		iniciarServidor(500);
		ConfiguracioProva configuracio = new ConfiguracioProva();
		PeticioPinbalHelper helper = new PeticioPinbalHelper(configuracio);
		String idPeticion = helper.generarIdPeticio();

		RespostaScsp resposta = new ClientScspPinbal(configuracio).enviar(
				url(),
				ClientScspPinbal.ACCIO_SINCRONA,
				PeticioPinbalHelper.marshal(helper.crearPeticion(idPeticion, "SVDPROVA")));

		assertTrue(resposta.isFault());
		assertTrue(resposta.isFaultHttp500());
		assertEquals(idPeticion, resposta.getValor("IdPeticion"));
		assertEquals("0227", resposta.getValor("CodigoEstado"));
		assertEquals("Error de proves", resposta.getValor("LiteralError"));
	}

	@Test
	void faultAmbHttp200() throws Exception {
		iniciarServidor(200);
		ConfiguracioProva configuracio = new ConfiguracioProva();
		PeticioPinbalHelper helper = new PeticioPinbalHelper(configuracio);

		RespostaScsp resposta = new ClientScspPinbal(configuracio).enviar(
				url(),
				ClientScspPinbal.ACCIO_SINCRONA,
				PeticioPinbalHelper.marshal(helper.crearPeticion(helper.generarIdPeticio(), "SVDPROVA")));

		assertTrue(resposta.isFault());
		assertFalse(resposta.isFaultHttp500());
	}

	private void iniciarServidor(int httpStatus) throws Exception {
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/", exchange -> {
			byte[] peticio = exchange.getRequestBody().readAllBytes();
			peticioRebuda.set(peticio);
			soapActionRebuda.set(exchange.getRequestHeaders().getFirst("SOAPAction"));
			String idPeticion = "";
			try {
				idPeticion = parse(peticio).getElementsByTagNameNS("*", "IdPeticion").item(0).getTextContent();
			} catch (Exception ignored) {
			}
			byte[] resposta = String.format(FAULT, idPeticion).getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().set("Content-Type", "text/xml;charset=UTF-8");
			exchange.sendResponseHeaders(httpStatus, resposta.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(resposta);
			}
		});
		server.start();
	}

	private String url() {
		return "http://127.0.0.1:" + server.getAddress().getPort() + "/emiservapi/interna/scsp/services/peticionSincrona";
	}

	private static Document parse(byte[] xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
	}

}
