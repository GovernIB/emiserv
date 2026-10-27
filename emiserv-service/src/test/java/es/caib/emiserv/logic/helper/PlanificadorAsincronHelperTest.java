package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.BackofficePeticioRepository;
import es.caib.emiserv.persist.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Planificador de l'enviament de les sol·licituds de les consultes asíncrones processades com a
 * peticions síncrones: límits de concurrència per destí i global i temps mínim entre enviaments.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class PlanificadorAsincronHelperTest {

	private static final Long CERT_PADRO = 10L;
	private static final Long CERT_FN = 20L;

	private PlanificadorAsincronHelper planificador;
	private BackofficeHelper backofficeHelper;
	private BackofficeSolicitudRepository backofficeSolicitudRepository;
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	private MockEnvironment environment;
	private ServeiEntity padro;
	private ServeiEntity familiaNombrosa;
	/** Sol·licituds pendents de cada destí (clau certificat:entitat). */
	private final Map<String, Queue<Long>> pendents = new ConcurrentHashMap<>();
	private final AtomicInteger enviantAra = new AtomicInteger();
	private final AtomicInteger maximSimultanis = new AtomicInteger();
	private final Map<String, List<Long>> iniciEnviaments = new ConcurrentHashMap<>();
	private final AtomicInteger enviades = new AtomicInteger();
	private final AtomicLong duradaEnviamentMs = new AtomicLong(100);

	@BeforeEach
	void setUp() {
		planificador = new PlanificadorAsincronHelper();
		backofficeHelper = mock(BackofficeHelper.class);
		backofficeSolicitudRepository = mock(BackofficeSolicitudRepository.class);
		serveiRutaDestiRepository = mock(ServeiRutaDestiRepository.class);
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ScspCoreServicioRepository scspCoreServicioRepository = mock(ScspCoreServicioRepository.class);
		BackofficePeticioRepository backofficePeticioRepository = mock(BackofficePeticioRepository.class);
		ConfiguracioAsincronHelper configuracio = new ConfiguracioAsincronHelper();
		environment = new MockEnvironment();
		ReflectionTestUtils.setField(configuracio, "environment", environment);
		ReflectionTestUtils.setField(planificador, "backofficeHelper", backofficeHelper);
		ReflectionTestUtils.setField(planificador, "configuracioAsincronHelper", configuracio);
		ReflectionTestUtils.setField(planificador, "serveiRepository", serveiRepository);
		ReflectionTestUtils.setField(planificador, "serveiRutaDestiRepository", serveiRutaDestiRepository);
		ReflectionTestUtils.setField(planificador, "scspCoreServicioRepository", scspCoreServicioRepository);
		ReflectionTestUtils.setField(planificador, "backofficePeticioRepository", backofficePeticioRepository);
		ReflectionTestUtils.setField(planificador, "backofficeSolicitudRepository", backofficeSolicitudRepository);

		padro = ServeiEntity.getBuilder("SCDCPAJU", "Padró", ServeiTipusEnumDto.ENRUTADOR).build();
		familiaNombrosa = ServeiEntity.getBuilder("SVDSCTFNWS01", "Família nombrosa", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).build();
		when(serveiRepository.findByBackofficeCaibAsyncTipus(BackofficeAsyncTipusEnumDto.PROCESSAR_SINCRONES)).thenReturn(Collections.emptyList());
		when(serveiRepository.findByTipusIn(anyList())).thenReturn(List.of(padro, familiaNombrosa));
		ScspCoreServicioEntity servicioPadro = ScspCoreServicioEntity.getBuilder("SCDCPAJU").build();
		ReflectionTestUtils.setField(servicioPadro, "id", CERT_PADRO);
		ScspCoreServicioEntity servicioFn = ScspCoreServicioEntity.getBuilder("SVDSCTFNWS01").build();
		ReflectionTestUtils.setField(servicioFn, "id", CERT_FN);
		when(scspCoreServicioRepository.findByCodigoCertificadoIn(anyList())).thenReturn(List.of(servicioPadro, servicioFn));
		when(backofficeHelper.recuperarEnviamentsInterromputs(any())).thenReturn(Collections.emptySet());
		when(backofficeSolicitudRepository.findIdsEnviablesSenseEntitat(anyList(), any())).thenReturn(Collections.emptyList());
		when(backofficePeticioRepository.findIdsPendentsSenseSolicitudsPerEnviar(anyList())).thenReturn(Collections.emptyList());
		when(backofficeHelper.reservarSeguentSolicitud(anyLong(), any())).thenAnswer(inv -> {
			Queue<Long> cua = pendents.get(inv.getArgument(0) + ":" + inv.getArgument(1));
			return cua != null ? cua.poll() : null;
		});
		when(backofficeHelper.enviarSolicitud(anyLong())).thenAnswer(inv -> {
			Long id = inv.getArgument(0);
			iniciEnviaments.computeIfAbsent(String.valueOf(id / 1000), k -> new CopyOnWriteArrayList<>()).add(System.currentTimeMillis());
			maximSimultanis.accumulateAndGet(enviantAra.incrementAndGet(), Math::max);
			Thread.sleep(duradaEnviamentMs.get());
			enviantAra.decrementAndGet();
			enviades.incrementAndGet();
			return 1L;
		});
	}

	@AfterEach
	void tearDown() {
		planificador.aturar();
	}

	@Test
	void respectaLaConcurrenciaDelDesti() throws Exception {
		environment.setProperty("es.caib.emiserv.async.cedent.interval", "0");
		environment.setProperty("es.caib.emiserv.async.cedent.concurrencia", "2");
		afegirPendents(CERT_PADRO, "7040", 1, 8);

		planificador.planificar();
		esperarEnviades(8);

		assertEquals(2, maximSimultanis.get());
		verify(backofficeHelper, org.mockito.Mockito.times(8)).actualitzarEstatPeticio(eq(1L), anyLong());
	}

	@Test
	void respectaElTempsMinimEntreEnviamentsAlDesti() throws Exception {
		environment.setProperty("es.caib.emiserv.async.cedent.interval", "300");
		environment.setProperty("es.caib.emiserv.async.cedent.concurrencia", "3");
		duradaEnviamentMs.set(10);
		afegirPendents(CERT_PADRO, "7040", 1, 4);

		planificador.planificar();
		esperarEnviades(4);

		List<Long> inicis = new ArrayList<>(iniciEnviaments.get("1"));
		Collections.sort(inicis);
		for (int i = 1; i < inicis.size(); i++) {
			assertTrue(inicis.get(i) - inicis.get(i - 1) >= 280, "Enviaments massa seguits: " + inicis);
		}
	}

	@Test
	void respectaLaConcurrenciaGlobal() throws Exception {
		environment.setProperty("es.caib.emiserv.async.cedent.interval", "0");
		environment.setProperty("es.caib.emiserv.async.cedent.concurrencia", "3");
		environment.setProperty("es.caib.emiserv.async.concurrencia.global", "4");
		afegirPendents(CERT_PADRO, "7040", 1, 6);
		afegirPendents(CERT_PADRO, "7001", 2, 6);
		afegirPendents(CERT_FN, null, 3, 6);

		planificador.planificar();

		// Els fils es reparteixen entre tots els destins, no només els primers
		verify(backofficeHelper, timeout(2000).atLeastOnce()).reservarSeguentSolicitud(CERT_FN, null);
		esperarEnviades(18);
		assertTrue(maximSimultanis.get() <= 4, "Simultanis: " + maximSimultanis.get());
	}

	@Test
	void elRitmeDelServeiPrevalSobreElGlobal() throws Exception {
		environment.setProperty("es.caib.emiserv.async.cedent.concurrencia", "4");
		padro.updateAsyncSincron(true, 0, 1);
		afegirPendents(CERT_PADRO, "7040", 1, 4);

		planificador.planificar();
		esperarEnviades(4);

		assertEquals(1, maximSimultanis.get());
	}

	@Test
	void unaRutaPausadaNoSEnvia() throws Exception {
		ServeiRutaDestiEntity ruta = ServeiRutaDestiEntity.getBuilder(padro, "7040", "http://cedent", 1L).build();
		ruta.updatePausada(true);
		when(serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(any(), eq("7040"))).thenReturn(ruta);
		afegirPendents(CERT_PADRO, "7040", 1, 2);

		planificador.planificar();
		Thread.sleep(300);

		assertEquals(0, planificador.getFilsTotal());
		verify(backofficeHelper, never()).reservarSeguentSolicitud(anyLong(), any());
	}

	@Test
	void unErrorEnUnaSolicitudNoAturaLesAltres() throws Exception {
		environment.setProperty("es.caib.emiserv.async.cedent.interval", "0");
		environment.setProperty("es.caib.emiserv.async.cedent.concurrencia", "1");
		when(backofficeHelper.enviarSolicitud(1001L)).thenThrow(new RuntimeException("Row was updated or deleted by another transaction"));
		afegirPendents(CERT_PADRO, "7040", 1, 3);

		planificador.planificar();
		esperarEnviades(2);

		verify(backofficeHelper).enviarSolicitud(1002L);
		verify(backofficeHelper).enviarSolicitud(1003L);
	}

	@Test
	void lesSolicitudsSenseEntitatDelsEnrutadorsEsResolen() {
		when(backofficeSolicitudRepository.findIdsEnviablesSenseEntitat(eq(List.of(CERT_PADRO)), any())).thenReturn(List.of(5L, 6L));
		when(backofficeHelper.resoldreEntitatSolicitud(6L)).thenReturn(9L);
		when(backofficeSolicitudRepository.findDestinsAmbSolicitudsEnviables(anyList(), any())).thenReturn(Collections.emptyList());

		planificador.planificar();

		verify(backofficeHelper).resoldreEntitatSolicitud(5L);
		// La sol·licitud que no s'ha pogut resoldre ha finalitzat amb error: es recompta la petició
		verify(backofficeHelper).actualitzarEstatPeticio(9L, null);
		verify(backofficeHelper, never()).actualitzarEstatPeticio(eq(5L), isNull());
	}

	private void afegirPendents(Long certificado, String entitat, long peticio, int num) {
		Queue<Long> cua = new ConcurrentLinkedQueue<>();
		for (int i = 1; i <= num; i++) {
			cua.add(peticio * 1000 + i);
		}
		pendents.put(certificado + ":" + entitat, cua);
		List<Object[]> destins = new ArrayList<>();
		for (Map.Entry<String, Queue<Long>> e: pendents.entrySet()) {
			String[] parts = e.getKey().split(":");
			destins.add(new Object[] {Long.valueOf(parts[0]), "null".equals(parts[1]) ? null : parts[1], (long)e.getValue().size(), 1L});
		}
		when(backofficeSolicitudRepository.findDestinsAmbSolicitudsEnviables(anyList(), any())).thenReturn(destins);
	}

	/** Espera els enviaments executant el planificador periòdicament (com la tasca programada). */
	private void esperarEnviades(int num) throws InterruptedException {
		long limit = System.currentTimeMillis() + 10000;
		while (enviades.get() < num && System.currentTimeMillis() < limit) {
			Thread.sleep(100);
			planificador.planificar();
		}
		assertEquals(num, enviades.get());
	}

}
