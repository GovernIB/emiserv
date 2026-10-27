package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.XmlHelper;
import es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.persist.entity.RedireccioPeticioEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
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
import org.mockito.ArgumentCaptor;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Peticions asíncrones a l'enrutador dels serveis que les processen com a peticions síncrones a
 * cada cedent: s'envien senceres a l'emisor SCSP d'EMISERV, igual que les sol·licituds de resposta.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class RedireccioServiceImplAsincronTest {

	private static final String SERVEI = "SCDCPAJU";
	private static final String URL_EMISOR_ASINCRONA = RedireccioServiceImpl.EMISOR_URL_PER_DEFECTE + "peticionAsincrona";

	private RedireccioServiceImpl redireccioService;
	private ServeiRepository serveiRepository;
	private ScspCoreServicioRepository scspCoreServicioRepository;
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	private RedireccioPeticioRepository redireccioPeticioRepository;
	private RedireccioMissatgeRepository redireccioMissatgeRepository;
	private ServeiEntity servei;

	@BeforeEach
	void setUp() {
		redireccioService = new RedireccioServiceImpl();
		serveiRepository = mock(ServeiRepository.class);
		scspCoreServicioRepository = mock(ScspCoreServicioRepository.class);
		serveiRutaDestiRepository = mock(ServeiRutaDestiRepository.class);
		redireccioPeticioRepository = mock(RedireccioPeticioRepository.class);
		ReflectionTestUtils.setField(redireccioService, "environment", new MockEnvironment());
		ReflectionTestUtils.setField(redireccioService, "xmlHelper", new XmlHelper());
		ReflectionTestUtils.setField(redireccioService, "serveiRepository", serveiRepository);
		ReflectionTestUtils.setField(redireccioService, "scspCoreServicioRepository", scspCoreServicioRepository);
		ReflectionTestUtils.setField(redireccioService, "serveiRutaDestiRepository", serveiRutaDestiRepository);
		ReflectionTestUtils.setField(redireccioService, "redireccioPeticioRepository", redireccioPeticioRepository);
		ReflectionTestUtils.setField(redireccioService, "redireccioSolicitudRepository", mock(RedireccioSolicitudRepository.class));
		redireccioMissatgeRepository = mock(RedireccioMissatgeRepository.class);
		ReflectionTestUtils.setField(redireccioService, "redireccioMissatgeRepository", redireccioMissatgeRepository);
		EntitatRepository entitatRepository = mock(EntitatRepository.class);
		when(entitatRepository.findByCif(any())).thenReturn(Optional.empty());
		ReflectionTestUtils.setField(redireccioService, "entitatRepository", entitatRepository);
		servei = ServeiEntity.getBuilder(SERVEI, "Padró de convivència", ServeiTipusEnumDto.ENRUTADOR).
				resolverClass("es.caib.emiserv.logic.resolver.EmpadronamentEntitatResolver").
				build();
		when(serveiRepository.findByCodi(SERVEI)).thenReturn(servei);
		when(scspCoreServicioRepository.findByCodigoCertificado(SERVEI)).thenReturn(ScspCoreServicioEntity.getBuilder(SERVEI).build());
	}

	@Test
	void peticioAsincronaAmbDiversesSolicitudsVaALEmisor() throws Exception {
		servei.updateAsyncSincron(true, null, null);

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(3), true);

		assertFalse(resultat.isError(), resultat::getErrorDescripcio);
		assertEquals(ServeiTipusEnumDto.ENRUTADOR, resultat.getTipus());
		assertEquals(URL_EMISOR_ASINCRONA, resultat.getUrlDesti());
		assertEquals("PBLASYNC0001", resultat.getAtributPeticioId());
		// No es resol cap entitat: cada sol·licitud pot anar a un cedent diferent
		verifyNoInteractions(serveiRutaDestiRepository);
		ArgumentCaptor<RedireccioPeticioEntity> peticio = ArgumentCaptor.forClass(RedireccioPeticioEntity.class);
		verify(redireccioPeticioRepository).save(peticio.capture());
		assertEquals(3, peticio.getValue().getNumTransmissions());
	}

	@Test
	void peticioAsincronaSenseLOpcioActivadaEsRebutja() throws Exception {
		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(3), true);

		assertTrue(resultat.isError());
		assertEquals("0415", resultat.getErrorCodi());
		assertEquals("El servicio no admite peticiones asíncronas con más de una solicitud", resultat.getFaultErrorString());
	}

	@Test
	void peticioSincronaDiversesSolicitudsEsRebutjaEncaraQueAcceptiAsincrones() throws Exception {
		servei.updateAsyncSincron(true, null, null);

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(3), false);

		assertTrue(resultat.isError());
		assertEquals("0415", resultat.getErrorCodi());
	}

	@Test
	void peticioAsincronaDUnServeiNoConfiguratALEmisor() throws Exception {
		servei.updateAsyncSincron(true, null, null);
		when(scspCoreServicioRepository.findByCodigoCertificado(SERVEI)).thenReturn(null);

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(peticio(2), true);

		assertTrue(resultat.isError());
		assertEquals("0504", resultat.getErrorCodi());
	}

	@Test
	void solicitudRespuestaVaALEmisorSenseCrearCapRegistre() throws Exception {
		servei.updateAsyncSincron(true, null, null);

		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(solicitudRespuesta(), true);

		assertFalse(resultat.isError(), resultat::getErrorDescripcio);
		assertEquals(URL_EMISOR_ASINCRONA, resultat.getUrlDesti());
		assertEquals("PBLASYNC0001", resultat.getAtributPeticioId());
		assertEquals(SERVEI, resultat.getAtributCodigoCertificado());
		verify(redireccioPeticioRepository, never()).save(any());
	}

	@Test
	void solicitudRespuestaDUnServeiSenseAsincronsEsRebutja() throws Exception {
		RedireccioProcessarResultatDto resultat = redireccioService.processarPeticio(solicitudRespuesta(), true);

		assertTrue(resultat.isError());
		assertEquals("0401", resultat.getErrorCodi());
	}

	@Test
	void laConfirmacioDeLaPeticioActualitzaLEstat() throws Exception {
		RedireccioPeticioEntity peticio = RedireccioPeticioEntity.getBuilder("PBLASYNC0001", SERVEI, "0001", 0, 3, "S0711001H").build();
		when(redireccioPeticioRepository.findByPeticioIdAndServeiCodi("PBLASYNC0001", SERVEI)).thenReturn(Collections.singletonList(peticio));
		String confirmacio = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
				"<ConfirmacionPeticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/confirmacionPeticion\"><Atributos>" +
				"<IdPeticion>PBLASYNC0001</IdPeticion><NumElementos>3</NumElementos><TimeStamp>2026-10-27T19:00:00.000+01:00</TimeStamp>" +
				"<Estado><CodigoEstado>0002</CodigoEstado><LiteralError>En proceso</LiteralError><TiempoEstimadoRespuesta>60</TiempoEstimadoRespuesta></Estado>" +
				"<CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos></ConfirmacionPeticion></soapenv:Body></soapenv:Envelope>";

		redireccioService.processarResposta("PBLASYNC0001", SERVEI, confirmacio.getBytes(), null, URL_EMISOR_ASINCRONA);

		assertEquals("0002", peticio.getEstat());
		verify(redireccioMissatgeRepository, times(1)).save(any());

		// Les sol·licituds de resposta mentre es processa no es desen
		String enProces = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
				"<Respuesta xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta\"><Atributos>" +
				"<IdPeticion>PBLASYNC0001</IdPeticion><NumElementos>3</NumElementos><TimeStamp>2026-10-27T19:05:00.000+01:00</TimeStamp>" +
				"<Estado><CodigoEstado>0002</CodigoEstado></Estado><CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos></Respuesta>" +
				"</soapenv:Body></soapenv:Envelope>";
		redireccioService.processarResposta("PBLASYNC0001", SERVEI, enProces.getBytes(), null, URL_EMISOR_ASINCRONA);
		redireccioService.processarResposta("PBLASYNC0001", SERVEI, enProces.getBytes(), null, URL_EMISOR_ASINCRONA);

		verify(redireccioMissatgeRepository, times(1)).save(any());

		redireccioService.processarResposta("PBLASYNC0001", SERVEI, enProces.replace("<CodigoEstado>0002", "<CodigoEstado>0003").getBytes(), null, URL_EMISOR_ASINCRONA);

		assertEquals("0003", peticio.getEstat());
		verify(redireccioMissatgeRepository, times(2)).save(any());
	}

	private static byte[] peticio(int numSolicituds) {
		StringBuilder solicituds = new StringBuilder();
		for (int i = 1; i <= numSolicituds; i++) {
			solicituds.append("<SolicitudTransmision><DatosGenericos>" +
					"<Emisor><NifEmisor>S0711001H</NifEmisor><NombreEmisor>CAIB</NombreEmisor></Emisor>" +
					"<Solicitante><IdentificadorSolicitante>S0711001H</IdentificadorSolicitante><NombreSolicitante>Govern</NombreSolicitante>" +
					"<Finalidad>Prova</Finalidad><Consentimiento>Si</Consentimiento></Solicitante>" +
					"<Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>1234567" + i + "Z</Documentacion></Titular>" +
					"<Transmision><CodigoCertificado>SCDCPAJU</CodigoCertificado><IdSolicitud>00000" + i + "</IdSolicitud></Transmision>" +
					"</DatosGenericos></SolicitudTransmision>");
		}
		return ("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
				"<Peticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\"><Atributos>" +
				"<IdPeticion>PBLASYNC0001</IdPeticion><NumElementos>" + numSolicituds + "</NumElementos>" +
				"<TimeStamp>2026-10-27T19:00:00.000+01:00</TimeStamp><CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos>" +
				"<Solicitudes>" + solicituds + "</Solicitudes></Peticion></soapenv:Body></soapenv:Envelope>").getBytes();
	}

	private static byte[] solicitudRespuesta() {
		return ("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
				"<SolicitudRespuesta xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/solicitudRespuesta\"><Atributos>" +
				"<IdPeticion>PBLASYNC0001</IdPeticion><NumElementos>3</NumElementos>" +
				"<TimeStamp>2026-10-27T20:00:00.000+01:00</TimeStamp><CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos>" +
				"</SolicitudRespuesta></soapenv:Body></soapenv:Envelope>").getBytes();
	}

}
