package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.BackofficeHelper;
import es.caib.emiserv.logic.helper.ConfiguracioAsincronHelper;
import es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.BackofficeSolicitudEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;
import es.caib.emiserv.persist.repository.BackofficePeticioRepository;
import es.caib.emiserv.persist.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Gestió de les consultes asíncrones processades com a síncrones: estimació de la finalització i
 * accions (pausa, reintents i cancel·lació).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class ConsultaAsincronaServiceImplTest {

	private static final Long CERT = 10L;

	private ConsultaAsincronaServiceImpl service;
	private BackofficePeticioRepository backofficePeticioRepository;
	private BackofficeSolicitudRepository backofficeSolicitudRepository;
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	private BackofficeHelper backofficeHelper;
	private MockEnvironment environment;
	private ServeiEntity servei;

	@BeforeEach
	void setUp() {
		service = new ConsultaAsincronaServiceImpl();
		backofficePeticioRepository = mock(BackofficePeticioRepository.class);
		backofficeSolicitudRepository = mock(BackofficeSolicitudRepository.class);
		serveiRutaDestiRepository = mock(ServeiRutaDestiRepository.class);
		backofficeHelper = mock(BackofficeHelper.class);
		ConfiguracioAsincronHelper configuracio = new ConfiguracioAsincronHelper();
		environment = new MockEnvironment();
		ReflectionTestUtils.setField(configuracio, "environment", environment);
		ReflectionTestUtils.setField(service, "backofficePeticioRepository", backofficePeticioRepository);
		ReflectionTestUtils.setField(service, "backofficeSolicitudRepository", backofficeSolicitudRepository);
		ReflectionTestUtils.setField(service, "serveiRutaDestiRepository", serveiRutaDestiRepository);
		ReflectionTestUtils.setField(service, "backofficeHelper", backofficeHelper);
		ReflectionTestUtils.setField(service, "configuracioAsincronHelper", configuracio);
		servei = ServeiEntity.getBuilder("SCDCPAJU", "Padró", ServeiTipusEnumDto.ENRUTADOR).build();
		// 2 s entre enviaments, 2 simultanis: 2 s per sol·licitud
		servei.updateAsyncSincron(true, 2000, 2);
	}

	@Test
	void laFinalitzacioTeEnCompteLesConsultesAnteriorsAlMateixCedent() {
		when(backofficeSolicitudRepository.findCuaPerEnviar()).thenReturn(Arrays.asList(
				fila(1L, false, "7040", BackofficeSolicitudEstatEnumDto.ENVIANT, 2),
				fila(1L, false, "7040", BackofficeSolicitudEstatEnumDto.PENDENT, 8),
				fila(2L, false, "7001", BackofficeSolicitudEstatEnumDto.PENDENT, 5),
				fila(3L, false, "7040", BackofficeSolicitudEstatEnumDto.PENDENT, 10)));

		long ara = System.currentTimeMillis();
		ConsultaAsincronaServiceImpl.Estimacio estimacio = service.estimar(Map.of(CERT, servei));

		// Consulta 1: 10 sol·licituds al cedent 7040 (20 s); consulta 3: les seves 10 i les 10 anteriors (40 s)
		assertAprox(ara + 20000, estimacio.dataFiPerPeticio.get(1L));
		assertAprox(ara + 10000, estimacio.dataFiPerPeticio.get(2L));
		assertAprox(ara + 40000, estimacio.dataFiPerPeticio.get(3L));
		long[] pendents7040 = estimacio.pendentsPerDesti.get(new ConsultaAsincronaServiceImpl.Desti(CERT, "7040"));
		assertEquals(18, pendents7040[0]);
		assertEquals(2, pendents7040[1]);
	}

	@Test
	void lesConsultesICedentsPausatsNoTenenDataPrevistaNiOcupenLaCua() {
		ServeiRutaDestiEntity ruta7001 = ServeiRutaDestiEntity.getBuilder(servei, "7001", "http://cedent", 1L).build();
		ruta7001.updatePausada(true);
		when(serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(any(), eq("7001"))).thenReturn(ruta7001);
		when(backofficeSolicitudRepository.findCuaPerEnviar()).thenReturn(Arrays.asList(
				fila(1L, true, "7040", BackofficeSolicitudEstatEnumDto.PENDENT, 50),
				fila(2L, false, "7001", BackofficeSolicitudEstatEnumDto.PENDENT, 5),
				fila(3L, false, "7040", BackofficeSolicitudEstatEnumDto.PENDENT, 5)));

		long ara = System.currentTimeMillis();
		ConsultaAsincronaServiceImpl.Estimacio estimacio = service.estimar(Map.of(CERT, servei));

		assertNull(estimacio.dataFiPerPeticio.get(1L));
		assertNull(estimacio.dataFiPerPeticio.get(2L));
		assertTrue(estimacio.peticionsAmbDestinsPausats.contains(2L));
		// La consulta pausada no endarrereix la 3
		assertAprox(ara + 10000, estimacio.dataFiPerPeticio.get(3L));
	}

	@Test
	void unReintentProgramatEndarrereixLaFinalitzacio() {
		Date properIntent = new Date(System.currentTimeMillis() + 300000);
		when(backofficeSolicitudRepository.findCuaPerEnviar()).thenReturn(List.<Object[]>of(
				new Object[] {1L, false, CERT, "7040", BackofficeSolicitudEstatEnumDto.PENDENT, 1L, properIntent}));

		ConsultaAsincronaServiceImpl.Estimacio estimacio = service.estimar(Map.of(CERT, servei));

		assertAprox(properIntent.getTime() + 2000, estimacio.dataFiPerPeticio.get(1L));
	}

	@Test
	void cancelarFinalitzaLesSolicitudsNoEnviadesAmbElCodiDeCancelacio() {
		environment.setProperty("es.caib.emiserv.async.cancelacio.codi", "0227");
		BackofficePeticioEntity peticio = peticio(1L, PeticioEstatEnumDto.PENDENT);
		peticio.updatePausada(true);
		BackofficeSolicitudEntity tramitada = solicitud(peticio, "000001");
		tramitada.marcarTramitada();
		BackofficeSolicitudEntity enviant = solicitud(peticio, "000002");
		enviant.iniciarEnviament(new Date());
		BackofficeSolicitudEntity pendent = solicitud(peticio, "000003");
		when(backofficeSolicitudRepository.findByPeticioOrderByIdAsc(peticio)).thenReturn(Arrays.asList(tramitada, enviant, pendent));

		int cancelades = service.cancelar(1L);

		assertEquals(2, cancelades);
		assertEquals(BackofficeSolicitudEstatEnumDto.TRAMITADA, tramitada.getEstat());
		assertEquals(BackofficeSolicitudEstatEnumDto.ERROR, enviant.getEstat());
		assertEquals("0227", pendent.getErrorCodi());
		assertTrue(pendent.getDarrerError().startsWith("Consulta cancel·lada"));
		assertFalse(peticio.isPausada());
		verify(backofficeHelper).recomptarPeticio(peticio, null);
	}

	@Test
	void reintentarTornaAEnviarLesErroniesILesProgramades() {
		BackofficePeticioEntity peticio = peticio(1L, PeticioEstatEnumDto.PENDENT);
		BackofficeSolicitudEntity erronia = solicitud(peticio, "000001");
		erronia.iniciarEnviament(new Date());
		erronia.finalitzarAmbError("0503", "Connection refused");
		BackofficeSolicitudEntity programada = solicitud(peticio, "000002");
		programada.iniciarEnviament(new Date());
		programada.reprogramar("Read timed out", new Date(System.currentTimeMillis() + 600000));
		BackofficeSolicitudEntity pendent = solicitud(peticio, "000003");
		when(backofficeSolicitudRepository.findByPeticioOrderByIdAsc(peticio)).thenReturn(Arrays.asList(erronia, programada, pendent));

		assertEquals(2, service.reintentar(1L));

		assertEquals(BackofficeSolicitudEstatEnumDto.PENDENT, erronia.getEstat());
		assertNull(erronia.getErrorCodi());
		assertEquals(0, erronia.getIntents());
		assertNull(programada.getDataProperIntent());
		verify(backofficeHelper).recomptarPeticio(peticio, null);
	}

	@Test
	void lesAccionsNoEsPodenFerSobreConsultesFinalitzades() {
		peticio(1L, PeticioEstatEnumDto.TRAMITADA);

		assertThrows(ValidationException.class, () -> service.pausar(1L));
		assertThrows(ValidationException.class, () -> service.cancelar(1L));
		assertThrows(ValidationException.class, () -> service.reintentar(1L));
		verify(backofficeHelper, never()).recomptarPeticio(any(), any());
	}

	private BackofficePeticioEntity peticio(Long id, PeticioEstatEnumDto estat) {
		BackofficePeticioEntity peticio = BackofficePeticioEntity.getBuilder(
				ScspCorePeticionRespuestaEntity.getBuilder(CERT, "PBLASYNC000" + id, "0002").build(),
				new Date()).estat(estat).build();
		ReflectionTestUtils.setField(peticio, "id", id);
		when(backofficePeticioRepository.findById(id)).thenReturn(Optional.of(peticio));
		return peticio;
	}

	private static BackofficeSolicitudEntity solicitud(BackofficePeticioEntity peticio, String id) {
		return BackofficeSolicitudEntity.getBuilder(peticio, id).build();
	}

	private static Object[] fila(Long peticio, boolean pausada, String entitat, BackofficeSolicitudEstatEnumDto estat, long num) {
		return new Object[] {peticio, pausada, CERT, entitat, estat, num, null};
	}

	private static void assertAprox(long esperat, Date data) {
		assertTrue(data != null && Math.abs(data.getTime() - esperat) < 1000, "Esperat " + new Date(esperat) + " però és " + data);
	}

}
