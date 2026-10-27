package es.caib.emiserv.logic.helper;

import com.sun.net.httpserver.HttpServer;
import es.caib.emiserv.logic.helper.ClientScspHelper.Credencials;
import es.caib.emiserv.logic.helper.ClientScspHelper.RespostaCedent;
import es.caib.emiserv.logic.helper.ClientScspHelper.ScspCedentException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.persist.entity.scsp.ScspCoreClavePrivadaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreParametroConfiguracionEntity;
import es.caib.emiserv.persist.repository.scsp.ScspCoreClavePrivadaRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreParametroConfiguracionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Client SCSP per a les peticions als emisors dels cedents: signatura WS-Security de les peticions,
 * validació de la signatura de les respostes i tractament dels SOAP Fault.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class ClientScspHelperTest {

	private static final String PASSWORD = "proves";
	private static final String NS_PETICION = "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
	private static final String NS_RESPUESTA = "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";

	@TempDir
	static Path tempDir;
	private static Path keystore;
	private static Credencials caib;
	private static Credencials cedent;

	private ClientScspHelper client;
	private HttpServer server;
	private final AtomicReference<String> peticioRebuda = new AtomicReference<>();

	@BeforeAll
	static void generarClaus() throws Exception {
		keystore = tempDir.resolve("emisor.p12");
		for (String alias: new String[] {"caib", "cedent"}) {
			Process keytool = new ProcessBuilder(
					Paths.get(System.getProperty("java.home"), "bin", "keytool").toString(),
					"-genkeypair", "-alias", alias, "-keyalg", "RSA", "-keysize", "2048", "-validity", "2",
					"-dname", "CN=" + alias + ", O=Proves, C=ES",
					"-storetype", "PKCS12", "-keystore", keystore.toString(),
					"-storepass", PASSWORD, "-keypass", PASSWORD).inheritIO().start();
			assertEquals(0, keytool.waitFor(), "No s'ha pogut generar el keystore de proves amb keytool");
		}
		KeyStore ks = KeyStore.getInstance("PKCS12");
		try (InputStream is = Files.newInputStream(keystore)) {
			ks.load(is, PASSWORD.toCharArray());
		}
		caib = new Credencials((PrivateKey)ks.getKey("caib", PASSWORD.toCharArray()), (X509Certificate)ks.getCertificate("caib"));
		cedent = new Credencials((PrivateKey)ks.getKey("cedent", PASSWORD.toCharArray()), (X509Certificate)ks.getCertificate("cedent"));
	}

	@BeforeEach
	void setUp() {
		client = new ClientScspHelper();
	}

	@AfterEach
	void tearDown() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	void laPeticioSignadaEsValida() throws Exception {
		Document signada = client.crearPeticioSignada(peticion(), caib);
		String xml = ClientScspHelper.toXml(signada);

		// Format dels requeridors SCSP: BinarySecurityToken i referència directa al token
		assertTrue(xml.contains("wsse:BinarySecurityToken"), xml);
		assertTrue(xml.contains("wsse:SecurityTokenReference"), xml);
		assertTrue(xml.contains(ClientScspHelper.RSA_SHA256), xml);
		client.validarSignatura(ClientScspHelper.parse(xml), xml, null);
	}

	@Test
	void unMissatgeModificatNoEsValida() throws Exception {
		String xml = ClientScspHelper.toXml(client.crearPeticioSignada(peticion(), caib));
		String modificat = xml.replace("12345678Z", "87654321X");

		ScspCedentException ex = assertThrows(ScspCedentException.class,
				() -> client.validarSignatura(ClientScspHelper.parse(modificat), xml, modificat));
		assertTrue(ex.getMessage().contains("no és vàlida"), ex.getMessage());
		assertFalse(ex.isDefinitiu());
	}

	@Test
	void peticioIRespostaSignadesAmbLEmisorDelCedent() throws Exception {
		iniciarEmisor(200, ClientScspHelper.toXml(client.crearPeticioSignada(respuesta("0003"), cedent)));

		RespostaCedent resposta = client.enviarPeticioSincrona(url(), "peticionSincrona", peticion(), caib, 10, true);

		assertEquals("Respuesta", resposta.getRespuesta().getLocalName());
		// L'emisor rep una petició signada vàlida
		client.validarSignatura(ClientScspHelper.parse(peticioRebuda.get()), peticioRebuda.get(), null);
		assertTrue(peticioRebuda.get().contains("PBLCEDENT0001"));
	}

	@Test
	void unaRespostaSenseSignarEsRebutja() throws Exception {
		iniciarEmisor(200, ClientScspHelper.toXml(sobre(respuesta("0003"))));

		ScspCedentException ex = assertThrows(ScspCedentException.class,
				() -> client.enviarPeticioSincrona(url(), "peticionSincrona", peticion(), caib, 10, true));

		assertTrue(ex.getMessage().contains("no està signada"), ex.getMessage());
		// Si la ruta no ho requereix, s'accepta
		assertNotNull(client.enviarPeticioSincrona(url(), "peticionSincrona", peticion(), caib, 10, false).getRespuesta());
	}

	@Test
	void unSoapFaultDAutoritzacioEsDefinitiu() throws Exception {
		iniciarEmisor(500, "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><soapenv:Fault>" +
				"<faultcode>soapenv:Client</faultcode><faultstring>No autorizado</faultstring><detail>" +
				"<Atributos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/soapfaultatributos\"><Estado>" +
				"<CodigoEstado>0301</CodigoEstado><LiteralError>Organismo no autorizado</LiteralError></Estado></Atributos>" +
				"</detail></soapenv:Fault></soapenv:Body></soapenv:Envelope>");

		ScspCedentException ex = assertThrows(ScspCedentException.class,
				() -> client.enviarPeticioSincrona(url(), "peticionSincrona", peticion(), caib, 10, true));

		assertEquals("0301", ex.getCodiEstat());
		assertTrue(ex.isDefinitiu());
		assertTrue(ex.getMessage().contains("Organismo no autorizado"));
		assertNotNull(ex.getXmlPeticio());
	}

	@Test
	void unErrorDelServidorDelCedentEsReintenta() throws Exception {
		iniciarEmisor(500, "<html>Service Unavailable</html");

		ScspCedentException ex = assertThrows(ScspCedentException.class,
				() -> client.enviarPeticioSincrona(url(), "peticionSincrona", peticion(), caib, 10, true));

		assertFalse(ex.isDefinitiu());
	}

	@Test
	void credencialsDelMagatzemDeClausDeLEmisor() throws Exception {
		ScspCoreParametroConfiguracionRepository parametres = mock(ScspCoreParametroConfiguracionRepository.class);
		ScspCoreClavePrivadaRepository claus = mock(ScspCoreClavePrivadaRepository.class);
		ReflectionTestUtils.setField(client, "scspCoreParametroConfiguracionRepository", parametres);
		ReflectionTestUtils.setField(client, "scspCoreClavePrivadaRepository", claus);
		when(parametres.findById("keystoreFile")).thenReturn(Optional.of(new ScspCoreParametroConfiguracionEntity("keystoreFile", "file:" + keystore, null)));
		when(parametres.findById("keystoreType")).thenReturn(Optional.of(new ScspCoreParametroConfiguracionEntity("keystoreType", "PKCS12", null)));
		when(parametres.findById("keystorePass")).thenReturn(Optional.of(new ScspCoreParametroConfiguracionEntity("keystorePass", PASSWORD, null)));
		when(claus.findById(1L)).thenReturn(Optional.of(ScspCoreClavePrivadaEntity.builder().alies("caib").password(PASSWORD).build()));

		Credencials credencials = client.getCredencials(1L);

		assertEquals(caib.getCertificat(), credencials.getCertificat());
		assertThrows(ValidationException.class, () -> client.getCredencials(null));
	}

	private void iniciarEmisor(int codiHttp, String resposta) throws Exception {
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/emisor/services/peticionSincrona", exchange -> {
			try (InputStream is = exchange.getRequestBody()) {
				peticioRebuda.set(new String(is.readAllBytes(), StandardCharsets.UTF_8));
			}
			byte[] cos = resposta.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "text/xml; charset=UTF-8");
			exchange.sendResponseHeaders(codiHttp, cos.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(cos);
			}
		});
		server.start();
	}

	private String url() {
		return "http://localhost:" + server.getAddress().getPort() + "/emisor/services/peticionSincrona";
	}

	private static Element peticion() throws Exception {
		return ClientScspHelper.parse(
				"<Peticion xmlns=\"" + NS_PETICION + "\"><Atributos><IdPeticion>PBLCEDENT0001</IdPeticion><NumElementos>1</NumElementos>" +
				"<TimeStamp>2026-10-27T20:00:00.000+01:00</TimeStamp><CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos>" +
				"<Solicitudes><SolicitudTransmision><DatosGenericos><Titular><TipoDocumentacion>NIF</TipoDocumentacion>" +
				"<Documentacion>12345678Z</Documentacion></Titular></DatosGenericos></SolicitudTransmision></Solicitudes></Peticion>").getDocumentElement();
	}

	private static Element respuesta(String estat) throws Exception {
		return ClientScspHelper.parse(
				"<Respuesta xmlns=\"" + NS_RESPUESTA + "\"><Atributos><IdPeticion>PBLCEDENT0001</IdPeticion><NumElementos>1</NumElementos>" +
				"<Estado><CodigoEstado>" + estat + "</CodigoEstado></Estado><CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos></Respuesta>").getDocumentElement();
	}

	private static Document sobre(Element contingut) throws Exception {
		Document document = ClientScspHelper.nouDocument();
		Element envelope = document.createElementNS(ClientScspHelper.NS_SOAPENV, "soapenv:Envelope");
		document.appendChild(envelope);
		Element body = document.createElementNS(ClientScspHelper.NS_SOAPENV, "soapenv:Body");
		envelope.appendChild(body);
		body.appendChild(document.importNode(contingut, true));
		return document;
	}

}
