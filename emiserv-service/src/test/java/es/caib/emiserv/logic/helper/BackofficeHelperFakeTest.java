package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.helper.BackofficeDestiHelper.BackofficeDesti;
import es.caib.emiserv.logic.helper.BackofficeHelper.RespostaBackofficeEntitat;
import es.caib.emiserv.logic.helper.BackofficeHelper.RespuestaAmbException;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.DatosGenericos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitudes;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Titular;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Transmision;
import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.BackofficeSolicitudEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.BackofficeComunicacioRepository;
import es.caib.emiserv.persist.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Mode fake dels serveis backoffice i de les rutes BACKOFFICE dels enrutadors: les peticions es
 * responen amb dades simulades sense contactar cap backoffice ni cap emisor de cedent.
 */
class BackofficeHelperFakeTest {

	private static final String NS_DATESP = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";

	private BackofficeHelper backofficeHelper;
	private MockEnvironment environment;
	private ServeiRepository serveiRepository;
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	private ClientScspHelper clientScspHelper;
	private BackofficeDestiHelper backofficeDestiHelper;

	@BeforeEach
	void setUp() {
		backofficeHelper = spy(new BackofficeHelper());
		environment = new MockEnvironment();
		environment.setProperty(ServeiFakeHelper.PROPIETAT_PERMES, "true");
		serveiRepository = mock(ServeiRepository.class);
		serveiRutaDestiRepository = mock(ServeiRutaDestiRepository.class);
		clientScspHelper = mock(ClientScspHelper.class);
		ScspCoreServicioRepository scspCoreServicioRepository = mock(ScspCoreServicioRepository.class);
		ScspCoreServicioEntity servicio = ScspCoreServicioEntity.getBuilder("SCDCPAJU").build();
		ReflectionTestUtils.setField(servicio, "versionEsquema", "V3");
		when(scspCoreServicioRepository.findByCodigoCertificado(org.mockito.ArgumentMatchers.anyString())).thenReturn(servicio);
		ConfiguracioAsincronHelper configuracio = new ConfiguracioAsincronHelper();
		ReflectionTestUtils.setField(configuracio, "environment", environment);
		backofficeDestiHelper = new BackofficeDestiHelper();
		ReflectionTestUtils.setField(backofficeDestiHelper, "environment", environment);
		ReflectionTestUtils.setField(backofficeDestiHelper, "serveiRutaDestiRepository", serveiRutaDestiRepository);
		ReflectionTestUtils.setField(backofficeHelper, "serveiRepository", serveiRepository);
		ReflectionTestUtils.setField(backofficeHelper, "scspCoreServicioRepository", scspCoreServicioRepository);
		ReflectionTestUtils.setField(backofficeHelper, "backofficeComunicacioRepository", mock(BackofficeComunicacioRepository.class));
		ReflectionTestUtils.setField(backofficeHelper, "backofficeSolicitudRepository", mock(BackofficeSolicitudRepository.class));
		ReflectionTestUtils.setField(backofficeHelper, "serveiRutaDestiRepository", serveiRutaDestiRepository);
		ReflectionTestUtils.setField(backofficeHelper, "clientScspHelper", clientScspHelper);
		ReflectionTestUtils.setField(backofficeHelper, "configuracioAsincronHelper", configuracio);
		ReflectionTestUtils.setField(backofficeHelper, "backofficeDestiHelper", backofficeDestiHelper);
		ReflectionTestUtils.setField(backofficeHelper, "xmlHelper", new XmlHelper());
		ReflectionTestUtils.setField(backofficeHelper, "environment", environment);
		ReflectionTestUtils.setField(backofficeHelper, "serveiFakeHelper", new ServeiFakeHelper(environment));
	}

	@Test
	void serveiFakeNoContactaElBackoffice() {
		ServeiEntity servei = serveiBackofficeFake();
		BackofficePeticioEntity peticio = peticioEntitat("PBLFAKE0001");

		RespuestaAmbException resposta = enviar(peticio("PBLFAKE0001", "SCDCPAJU", solicitud("000001", "11111111H")), peticio, null);

		assertNull(resposta.getException());
		assertEquals("0003", resposta.getRespuesta().getAtributos().getEstado().getCodigoEstado());
		assertTrue(resposta.getRespuesta().getAtributos().getEstado().getLiteralError().startsWith(ServeiFakeHelper.LITERAL_PREFIX));
		Element datosEspecificos = (Element)resposta.getRespuesta().getTransmisiones().getTransmisionDatos().get(0).getDatosEspecificos();
		assertEquals(NS_DATESP, datosEspecificos.getNamespaceURI());
		assertTrue(datosEspecificos.getTextContent().contains("11111111H"));
		// La comunicació es desa igual que amb un backoffice real
		assertEquals(PeticioEstatEnumDto.TRAMITADA, peticio.getEstat());
		assertTrue(peticio.getComunicacioDarrera().getRespostaXml().contains("PBLFAKE0001"));
		assertNotNull(servei);
	}

	@Test
	void fakeNoPermesUsaElBackofficeReal() {
		environment.setProperty(ServeiFakeHelper.PROPIETAT_PERMES, "false");
		ServeiEntity servei = serveiBackofficeFake();
		BackofficeDestiHelper destiHelper = mock(BackofficeDestiHelper.class);
		ReflectionTestUtils.setField(backofficeHelper, "backofficeDestiHelper", destiHelper);
		Peticion peticion = peticio("PBLFAKE0002", "SCDCPAJU", solicitud("000001", "11111111H"));
		when(destiHelper.getDesti(servei, peticion)).thenReturn(
				new BackofficeDesti("SCDCPAJU", null, "http://no-existeix.invalid", null));

		BackofficeDesti desti = ReflectionTestUtils.invokeMethod(backofficeHelper, "getBackofficeDesti", servei, peticion, null);

		verify(destiHelper).getDesti(servei, peticion);
		assertFalse(desti.isFake());
		assertEquals("http://no-existeix.invalid", desti.getUrl());
	}

	@Test
	@SuppressWarnings("unchecked")
	void enrutadorMultipleFakeNoEnviaAEmisorsCedents() throws Exception {
		ServeiEntity servei = ServeiEntity.getBuilder("SVDSCTFNWS01", "Família nombrosa", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).build();
		servei.updateFake(true, ServeiFakeResultatEnumDto.OK, null);
		when(serveiRepository.findByCodi("SVDSCTFNWS01")).thenReturn(servei);
		when(serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(servei)).thenReturn(Arrays.asList(
				ServeiRutaDestiEntity.getBuilder(servei, "MALLORCA", "http://no-existeix.invalid", 1L).
						desti(ServeiRutaDestiTipusEnumDto.BACKOFFICE, null).build(),
				ServeiRutaDestiEntity.getBuilder(servei, "MENORCA", "https://no-existeix.invalid/emisor", 2L).
						desti(ServeiRutaDestiTipusEnumDto.EMISERV, null).build()));
		BackofficePeticioEntity peticio = peticioEntitat("PBLFAKE0003");
		BackofficeSolicitudEntity solicitud = BackofficeSolicitudEntity.getBuilder(peticio, "000001").build();

		RespuestaAmbException resposta = enviar(peticio("PBLFAKE0003", "SVDSCTFNWS01", solicitud("000001", "11111111H")), peticio, solicitud);

		assertNull(resposta.getException());
		verifyNoInteractions(clientScspHelper);
		ArgumentCaptor<List<RespostaBackofficeEntitat>> respostes = ArgumentCaptor.forClass(List.class);
		verify(backofficeHelper).escollirRespostaEnrutadorMultiple(eq(servei), respostes.capture());
		assertEquals(2, respostes.getValue().size());
		assertEquals("MALLORCA", respostes.getValue().get(0).getEntitatCodi());
		assertEquals("MENORCA", respostes.getValue().get(1).getEntitatCodi());
		for (RespostaBackofficeEntitat r: respostes.getValue()) {
			assertNull(r.getException());
			assertNotNull(r.getRespuesta());
			assertTrue(r.getXmlResposta().toString().contains("PBLFAKE0003"));
		}
	}

	@Test
	void duesPeticionsConcurrentsNoComparteixenHandler() throws Exception {
		serveiBackofficeFake();
		ExecutorService executor = Executors.newFixedThreadPool(2);
		try {
			for (int i = 0; i < 10; i++) {
				BackofficePeticioEntity peticioA = peticioEntitat("PBLFAKE-A" + i);
				BackofficePeticioEntity peticioB = peticioEntitat("PBLFAKE-B" + i);
				CountDownLatch inici = new CountDownLatch(1);
				Peticion a = peticio("PBLFAKE-A" + i, "SCDCPAJU", solicitud("000001", "11111111H"));
				Peticion b = peticio("PBLFAKE-B" + i, "SCDCPAJU", solicitud("000001", "22222222J"));
				Future<RespuestaAmbException> futurA = executor.submit(enviarEnFil(inici, a, peticioA));
				Future<RespuestaAmbException> futurB = executor.submit(enviarEnFil(inici, b, peticioB));
				inici.countDown();
				assertNull(futurA.get().getException());
				assertNull(futurB.get().getException());
				String xmlA = peticioA.getComunicacioDarrera().getRespostaXml();
				String xmlB = peticioB.getComunicacioDarrera().getRespostaXml();
				assertTrue(xmlA.contains("PBLFAKE-A" + i), xmlA);
				assertFalse(xmlA.contains("PBLFAKE-B" + i), xmlA);
				assertTrue(xmlB.contains("PBLFAKE-B" + i), xmlB);
				assertFalse(xmlB.contains("PBLFAKE-A" + i), xmlB);
			}
		} finally {
			executor.shutdown();
		}
	}

	private Callable<RespuestaAmbException> enviarEnFil(CountDownLatch inici, Peticion peticion, BackofficePeticioEntity peticio) {
		return () -> {
			inici.await();
			return enviar(peticion, peticio, null);
		};
	}

	private RespuestaAmbException enviar(Peticion peticion, BackofficePeticioEntity peticio, BackofficeSolicitudEntity solicitud) {
		return ReflectionTestUtils.invokeMethod(
				backofficeHelper,
				"peticioBackofficeSincronaEnviar",
				peticion,
				peticio,
				solicitud,
				true);
	}

	private ServeiEntity serveiBackofficeFake() {
		ServeiEntity servei = ServeiEntity.getBuilder("SCDCPAJU", "Padró", ServeiTipusEnumDto.BACKOFFICE).
				backofficeCaibUrl("http://no-existeix.invalid").
				build();
		servei.updateConfigurat(true);
		servei.updateFake(true, ServeiFakeResultatEnumDto.OK, null);
		when(serveiRepository.findByCodi("SCDCPAJU")).thenReturn(servei);
		return servei;
	}

	private static BackofficePeticioEntity peticioEntitat(String idPeticion) {
		return BackofficePeticioEntity.getBuilder(
				ScspCorePeticionRespuestaEntity.getBuilder(10L, idPeticion, "0002").build(),
				new Date()).build();
	}

	private static Peticion peticio(String idPeticion, String serveiCodi, SolicitudTransmision solicitud) {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(idPeticion);
		atributos.setCodigoCertificado(serveiCodi);
		atributos.setNumElementos("1");
		peticion.setAtributos(atributos);
		Solicitudes solicitudes = new Solicitudes();
		solicitudes.setSolicitudTransmision(new ArrayList<SolicitudTransmision>());
		solicitudes.getSolicitudTransmision().add(solicitud);
		solicitud.getDatosGenericos().getTransmision().setCodigoCertificado(serveiCodi);
		peticion.setSolicitudes(solicitudes);
		return peticion;
	}

	private static SolicitudTransmision solicitud(String idSolicitud, String document) {
		SolicitudTransmision solicitud = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Titular titular = new Titular();
		titular.setDocumentacion(document);
		datosGenericos.setTitular(titular);
		Transmision transmision = new Transmision();
		transmision.setIdSolicitud(idSolicitud);
		datosGenericos.setTransmision(transmision);
		solicitud.setDatosGenericos(datosGenericos);
		solicitud.setDatosEspecificos(element(
				"<DatosEspecificos xmlns=\"" + NS_DATESP + "\"><Solicitud>" +
				"<ProvinciaSolicitud>07</ProvinciaSolicitud><MunicipioSolicitud>040</MunicipioSolicitud>" +
				"</Solicitud></DatosEspecificos>"));
		return solicitud;
	}

	private static Element element(String xml) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

}
