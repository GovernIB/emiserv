package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ClientScspHelper;
import es.caib.emiserv.logic.helper.ServeiFakeHelper;
import es.caib.emiserv.logic.helper.XmlHelper;
import es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.logic.intf.dto.RedireccioRespostaFakeDto;
import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.EntitatRepository;
import es.caib.emiserv.persist.repository.RedireccioMissatgeRepository;
import es.caib.emiserv.persist.repository.RedireccioPeticioRepository;
import es.caib.emiserv.persist.repository.RedireccioSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Mode fake dels serveis enrutadors: reescriptura de les rutes a l'esquema "fake:" i generació
 * de les respostes fake.
 */
class RedireccioServiceImplFakeTest {

	private static final String SERVEI = "SCDCPAJU";
	private static final String URL_EMISOR = RedireccioServiceImpl.EMISOR_URL_PER_DEFECTE;

	private RedireccioServiceImpl redireccioService;
	private MockEnvironment environment;
	private ServeiRepository serveiRepository;
	private ScspCoreServicioRepository scspCoreServicioRepository;
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	private ClientScspHelper clientScspHelper;
	private ServeiEntity servei;

	@BeforeEach
	void setUp() {
		redireccioService = new RedireccioServiceImpl();
		environment = new MockEnvironment();
		environment.setProperty(ServeiFakeHelper.PROPIETAT_PERMES, "true");
		serveiRepository = mock(ServeiRepository.class);
		scspCoreServicioRepository = mock(ScspCoreServicioRepository.class);
		serveiRutaDestiRepository = mock(ServeiRutaDestiRepository.class);
		clientScspHelper = mock(ClientScspHelper.class);
		RedireccioPeticioRepository redireccioPeticioRepository = mock(RedireccioPeticioRepository.class);
		when(redireccioPeticioRepository.findByPeticioIdAndServeiCodi(any(), any())).thenReturn(Collections.emptyList());
		EntitatRepository entitatRepository = mock(EntitatRepository.class);
		when(entitatRepository.findByCif(any())).thenReturn(Optional.empty());
		ReflectionTestUtils.setField(redireccioService, "environment", environment);
		ReflectionTestUtils.setField(redireccioService, "xmlHelper", new XmlHelper());
		ReflectionTestUtils.setField(redireccioService, "serveiRepository", serveiRepository);
		ReflectionTestUtils.setField(redireccioService, "scspCoreServicioRepository", scspCoreServicioRepository);
		ReflectionTestUtils.setField(redireccioService, "serveiRutaDestiRepository", serveiRutaDestiRepository);
		ReflectionTestUtils.setField(redireccioService, "redireccioPeticioRepository", redireccioPeticioRepository);
		ReflectionTestUtils.setField(redireccioService, "redireccioSolicitudRepository", mock(RedireccioSolicitudRepository.class));
		ReflectionTestUtils.setField(redireccioService, "redireccioMissatgeRepository", mock(RedireccioMissatgeRepository.class));
		ReflectionTestUtils.setField(redireccioService, "entitatRepository", entitatRepository);
		ReflectionTestUtils.setField(redireccioService, "serveiFakeHelper", new ServeiFakeHelper(environment));
		ReflectionTestUtils.setField(redireccioService, "clientScspHelper", clientScspHelper);
		servei = ServeiEntity.getBuilder(SERVEI, "Padró de convivència", ServeiTipusEnumDto.ENRUTADOR).
				resolverClass("es.caib.emiserv.logic.resolver.EmpadronamentEntitatResolver").
				build();
		servei.updateFake(true, ServeiFakeResultatEnumDto.OK, null);
		when(serveiRepository.findByCodi(SERVEI)).thenReturn(servei);
	}

	@Test
	void enrutadorSimpleFakeReescriuLaUrl() throws Exception {
		when(serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(any(), any())).thenReturn(
				ruta("07040", ServeiRutaDestiTipusEnumDto.EMISERV, "https://ajuntament.es/emisor/"));

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(1, true), false);

		assertFalse(resultat.isError(), resultat::getErrorDescripcio);
		assertTrue(resultat.isFake());
		assertEquals("fake:SCDCPAJU/07040/", resultat.getUrlDesti());
	}

	@Test
	void enrutadorSimpleSenseEntitatUsaGuio() throws Exception {
		servei = ServeiEntity.getBuilder(SERVEI, "Padró", ServeiTipusEnumDto.ENRUTADOR).urlPerDefecte("https://x/").build();
		servei.updateFake(true, ServeiFakeResultatEnumDto.OK, null);
		when(serveiRepository.findByCodi(SERVEI)).thenReturn(servei);

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(1, false), false);

		assertTrue(resultat.isFake());
		assertEquals("fake:SCDCPAJU/-/", resultat.getUrlDesti());
	}

	@Test
	void enrutadorMultipleFakeReescriuTotesLesRutesEmiserv() throws Exception {
		servei = ServeiEntity.getBuilder(SERVEI, "Padró", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).build();
		servei.updateFake(true, ServeiFakeResultatEnumDto.OK, null);
		when(serveiRepository.findByCodi(SERVEI)).thenReturn(servei);
		when(serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(servei)).thenReturn(Arrays.asList(
				ruta("MALLORCA", ServeiRutaDestiTipusEnumDto.EMISERV, "https://mallorca.es/emisor/"),
				ruta("MENORCA", ServeiRutaDestiTipusEnumDto.BACKOFFICE, "https://menorca.es/backoffice"),
				ruta("EIVISSA", ServeiRutaDestiTipusEnumDto.EMISERV, "https://eivissa.es/emisor/")));

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(1, false), false);

		assertTrue(resultat.isFake());
		assertEquals("fake:SCDCPAJU/MALLORCA/", resultat.getUrlDestins().get("MALLORCA"));
		assertEquals("fake:SCDCPAJU/EIVISSA/", resultat.getUrlDestins().get("EIVISSA"));
	}

	@Test
	void rutaBackofficeIEmisorPropiNoEsReescriuen() throws Exception {
		servei = ServeiEntity.getBuilder(SERVEI, "Padró", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).build();
		servei.updateFake(true, ServeiFakeResultatEnumDto.OK, null);
		when(serveiRepository.findByCodi(SERVEI)).thenReturn(servei);
		when(serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(servei)).thenReturn(Arrays.asList(
				ruta("MALLORCA", ServeiRutaDestiTipusEnumDto.EMISERV, "https://mallorca.es/emisor/"),
				ruta("MENORCA", ServeiRutaDestiTipusEnumDto.BACKOFFICE, "https://menorca.es/backoffice")));

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(1, false), false);

		assertEquals(URL_EMISOR, resultat.getUrlDestins().get(RedireccioServiceImpl.CLAU_BACKOFFICES));
		assertEquals("fake:SCDCPAJU/MALLORCA/", resultat.getUrlDestins().get("MALLORCA"));
	}

	@Test
	void peticioAsincronaAsyncSincronNoEsReescriu() throws Exception {
		servei.updateAsyncSincron(true, null, null);
		when(scspCoreServicioRepository.findByCodigoCertificado(SERVEI)).thenReturn(ScspCoreServicioEntity.getBuilder(SERVEI).build());

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(2, true), true);

		assertFalse(resultat.isError(), resultat::getErrorDescripcio);
		assertFalse(resultat.isFake());
		assertEquals(URL_EMISOR + RedireccioServiceImpl.OPERACIO_ASINCRONA, resultat.getUrlDesti());
	}

	@Test
	void fakeNoPermesNoCanviaRes() throws Exception {
		environment.setProperty(ServeiFakeHelper.PROPIETAT_PERMES, "false");
		when(serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(any(), any())).thenReturn(
				ruta("07040", ServeiRutaDestiTipusEnumDto.EMISERV, "https://ajuntament.es/emisor/"));

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(1, true), false);

		assertFalse(resultat.isFake());
		assertEquals("https://ajuntament.es/emisor/", resultat.getUrlDesti());
	}

	@Test
	void generarRespostaFakeServeiNoFakeRetorna404() throws Exception {
		servei.updateFake(false, null, null);

		RedireccioRespostaFakeDto resposta = redireccioService.generarRespostaFake(SERVEI, "07040", peticio(1, true), false);

		assertEquals(404, resposta.getCodiHttp());
	}

	@Test
	void generarRespostaFakeSenseClauRetornaFault0502() throws Exception {
		when(scspCoreServicioRepository.findByCodigoCertificado(SERVEI)).thenReturn(null);

		RedireccioRespostaFakeDto resposta = redireccioService.generarRespostaFake(SERVEI, "07040", peticio(1, true), false);

		assertEquals(500, resposta.getCodiHttp());
		assertTrue(resposta.getXml().contains("0502"), resposta.getXml());
		assertFalse(resposta.getXml().contains("Signature"));
	}

	@Test
	void generarRespostaFakeAmbCredencialsInvalidesRetornaFault0502() throws Exception {
		ScspCoreServicioEntity core = mock(ScspCoreServicioEntity.class);
		when(core.getClaveFirma()).thenReturn(7L);
		when(scspCoreServicioRepository.findByCodigoCertificado(SERVEI)).thenReturn(core);
		when(clientScspHelper.getCredencials(7L)).thenThrow(new ValidationException("Sense magatzem de claus"));

		RedireccioRespostaFakeDto resposta = redireccioService.generarRespostaFake(SERVEI, "07040", peticio(1, true), false);

		assertEquals(500, resposta.getCodiHttp());
		assertTrue(resposta.getXml().contains("Sense magatzem de claus"), resposta.getXml());
		assertFalse(resposta.getXml().contains("Signature"));
	}

	@Test
	void generarRespostaFakeErrorRetorna500AmbFault() throws Exception {
		servei.updateFake(true, ServeiFakeResultatEnumDto.ERROR, null);
		credencialsDeProva();

		RedireccioRespostaFakeDto resposta = redireccioService.generarRespostaFake(SERVEI, "07040", peticio(1, true), false);

		assertEquals(500, resposta.getCodiHttp());
		assertTrue(resposta.getXml().contains("0502"), resposta.getXml());
		assertTrue(resposta.getXml().contains("[FAKE] Error simulat"), resposta.getXml());
		assertTrue(resposta.getXml().contains("Signature"));
	}

	@Test
	void generarRespostaFakeOkEsSignada() throws Exception {
		credencialsDeProva();

		RedireccioRespostaFakeDto resposta = redireccioService.generarRespostaFake(SERVEI, "07040", peticio(2, true), false);

		assertEquals(200, resposta.getCodiHttp());
		assertTrue(resposta.getXml().contains("ds:Signature"), resposta.getXml());
		assertTrue(resposta.getXml().contains("[FAKE]"), resposta.getXml());
	}

	@Test
	void generarRespostaFakeAsincronaRetornaFault0502() throws Exception {
		credencialsDeProva();

		RedireccioRespostaFakeDto resposta = redireccioService.generarRespostaFake(SERVEI, "07040", peticio(1, true), true);

		assertEquals(500, resposta.getCodiHttp());
		assertTrue(resposta.getXml().contains("0502"), resposta.getXml());
		assertTrue(resposta.getXml().contains("[FAKE] Les peticions asíncrones només s&apos;admeten amb asyncSincron"), resposta.getXml());
		assertFalse(resposta.getXml().contains("Respuesta"));
	}

	@Test
	void generarRespostaFakeAsincronaServeiNoFakeRetorna404() throws Exception {
		servei.updateFake(false, null, null);

		assertEquals(404, redireccioService.generarRespostaFake(SERVEI, "07040", peticio(1, true), true).getCodiHttp());
	}

	private void credencialsDeProva() throws Exception {
		ScspCoreServicioEntity core = mock(ScspCoreServicioEntity.class);
		when(core.getClaveFirma()).thenReturn(7L);
		when(scspCoreServicioRepository.findByCodigoCertificado(SERVEI)).thenReturn(core);
		Path dir = Files.createTempDirectory("fake-redireccio-test");
		Path jks = dir.resolve("test.jks");
		try {
			Process p = new ProcessBuilder(
					System.getProperty("java.home") + "/bin/keytool", "-genkeypair", "-alias", "test",
					"-keyalg", "RSA", "-keysize", "2048", "-validity", "2", "-dname", "CN=fake-test",
					"-keystore", jks.toString(), "-storetype", "JKS", "-storepass", "changeit", "-keypass", "changeit").
					redirectErrorStream(true).start();
			p.getInputStream().readAllBytes();
			assertEquals(0, p.waitFor());
			KeyStore ks = KeyStore.getInstance("JKS");
			try (java.io.InputStream in = Files.newInputStream(jks)) {
				ks.load(in, "changeit".toCharArray());
			}
			when(clientScspHelper.getCredencials(7L)).thenReturn(new ClientScspHelper.Credencials(
					(PrivateKey)ks.getKey("test", "changeit".toCharArray()),
					(X509Certificate)ks.getCertificate("test")));
		} finally {
			Files.deleteIfExists(jks);
			Files.deleteIfExists(dir);
		}
	}

	private static ServeiRutaDestiEntity ruta(String entitat, ServeiRutaDestiTipusEnumDto tipus, String url) {
		return ServeiRutaDestiEntity.getBuilder(null, entitat, url, 1L).desti(tipus, null).build();
	}

	private static byte[] peticio(int numSolicituds, boolean ambDadesEspecifiques) {
		StringBuilder solicituds = new StringBuilder();
		for (int i = 1; i <= numSolicituds; i++) {
			solicituds.append("<SolicitudTransmision><DatosGenericos>" +
					"<Emisor><NifEmisor>S0711001H</NifEmisor><NombreEmisor>CAIB</NombreEmisor></Emisor>" +
					"<Solicitante><IdentificadorSolicitante>S0711001H</IdentificadorSolicitante><NombreSolicitante>Govern</NombreSolicitante>" +
					"<Finalidad>Prova</Finalidad><Consentimiento>Si</Consentimiento></Solicitante>" +
					"<Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>1234567" + i + "Z</Documentacion></Titular>" +
					"<Transmision><CodigoCertificado>SCDCPAJU</CodigoCertificado><IdSolicitud>00000" + i + "</IdSolicitud></Transmision>" +
					"</DatosGenericos>" +
					(ambDadesEspecifiques ?
							"<d:DatosEspecificos xmlns:d=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"><d:Solicitud>" +
							"<d:ProvinciaSolicitud>07</d:ProvinciaSolicitud><d:MunicipioSolicitud>040</d:MunicipioSolicitud>" +
							"</d:Solicitud></d:DatosEspecificos>" : "") +
					"</SolicitudTransmision>");
		}
		return ("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
				"<Peticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\"><Atributos>" +
				"<IdPeticion>PBLFAKE0001</IdPeticion><NumElementos>" + numSolicituds + "</NumElementos>" +
				"<TimeStamp>2026-10-27T19:00:00.000+01:00</TimeStamp><CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos>" +
				"<Solicitudes>" + solicituds + "</Solicitudes></Peticion></soapenv:Body></soapenv:Envelope>").getBytes();
	}

}
