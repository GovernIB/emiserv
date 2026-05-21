package es.caib.emiserv.logic.service;

import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.IntegracioPeticions;
import es.caib.comanda.model.server.monitoring.IntegracioSalut;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaSalut;
import es.caib.comanda.ms.salut.helper.SalutComponentsHelper;
import es.caib.comanda.ms.salut.helper.components.MonitorComponentsMemoria;
import es.caib.emiserv.logic.helper.SalutHelper;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.SubsistemesEnum;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAplicacionEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionCertificadoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionOrganismoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmAplicacionRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreEmAutorizacionCertificadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AplicacioServiceImplTest {

	@BeforeEach
	void resetSalutStats() throws Exception {
		Field subsistemaField = SalutHelper.class.getDeclaredField("subsistemaComponentHelper");
		subsistemaField.setAccessible(true);
		subsistemaField.set(null, new SalutComponentsHelper(
				new MonitorComponentsMemoria(20),
				SubsistemesEnum::containsCodi,
				SubsistemesEnum.CODIS));

		Field integracioField = SalutHelper.class.getDeclaredField("integracioComponentHelper");
		integracioField.setAccessible(true);
		integracioField.set(null, new SalutComponentsHelper(
				new MonitorComponentsMemoria(20),
				(codi) -> false,
				Collections.emptyList()));
	}

	@Test
	void getIntegracionsInfoRetornaAplicacionsConfiguradesOrdenadesPerId() {
		AplicacioServiceImpl service = new AplicacioServiceImpl();
		ScspCoreEmAplicacionRepository repository = mock(ScspCoreEmAplicacionRepository.class);
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ReflectionTestUtils.setField(service, "scspCoreEmAplicacionRepository", repository);
		ReflectionTestUtils.setField(service, "serveiRepository", serveiRepository);

		when(repository.findAll()).thenReturn(Arrays.asList(
				aplicacio(20, "B12345678", "Aplicació B"),
				aplicacio(10, "A12345678", "Nom molt llarg ".repeat(25))));
		when(serveiRepository.findByActiuTrue()).thenReturn(Collections.emptyList());

		List<IntegracioInfo> integracions = service.getIntegracionsInfo();

		assertEquals(Arrays.asList("APP-10", "APP-20"), Arrays.asList(
				integracions.get(0).getCodi(),
				integracions.get(1).getCodi()));
		assertEquals(255, integracions.get(0).getNom().length());
		assertTrue(integracions.get(0).getNom().endsWith("..."));
		assertEquals("Aplicació B", integracions.get(1).getNom());
	}

	@Test
	void getIntegracionsInfoAfegeixSufixNumericAlsCodisDuplicats() {
		AplicacioServiceImpl service = new AplicacioServiceImpl();
		ScspCoreEmAplicacionRepository repository = mock(ScspCoreEmAplicacionRepository.class);
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ReflectionTestUtils.setField(service, "scspCoreEmAplicacionRepository", repository);
		ReflectionTestUtils.setField(service, "serveiRepository", serveiRepository);

		when(repository.findAll()).thenReturn(Arrays.asList(
				aplicacio(30, "C12345678", "PLATAFORMA DE INTERMEDIACION - C"),
				aplicacio(10, "A12345678", "PLATAFORMA DE INTERMEDIACION - A"),
				aplicacio(20, "B12345678", "PLATAFORMA DE INTERMEDIACION - B")));
		when(serveiRepository.findByActiuTrue()).thenReturn(Collections.emptyList());

		List<IntegracioInfo> integracions = service.getIntegracionsInfo();

		assertEquals(Arrays.asList("PID", "PID2", "PID3"), Arrays.asList(
				integracions.get(0).getCodi(),
				integracions.get(1).getCodi(),
				integracions.get(2).getCodi()));
	}

	@Test
	void getIntegracionsInfoInclouServeisActius() {
		AplicacioServiceImpl service = new AplicacioServiceImpl();
		ScspCoreEmAplicacionRepository repository = mock(ScspCoreEmAplicacionRepository.class);
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ReflectionTestUtils.setField(service, "scspCoreEmAplicacionRepository", repository);
		ReflectionTestUtils.setField(service, "serveiRepository", serveiRepository);

		when(repository.findAll()).thenReturn(Collections.emptyList());
		when(serveiRepository.findByActiuTrue()).thenReturn(Arrays.asList(
				serveiActiu("SRV_B", "Servei B"),
				serveiActiu("SRV_A", "Servei A")));

		List<IntegracioInfo> integracions = service.getIntegracionsInfo();

		assertEquals(Arrays.asList("SRV_A", "SRV_B"), Arrays.asList(
				integracions.get(0).getCodi(),
				integracions.get(1).getCodi()));
		assertEquals("Servei A", integracions.get(0).getNom());
		assertEquals("Servei B", integracions.get(1).getNom());
	}

	@Test
	void getSubsistemesInfoNoInclouServeisActius() {
		AplicacioServiceImpl service = new AplicacioServiceImpl();

		List<String> codis = service.getSubsistemesInfo().stream()
				.map(SubsistemaInfo::getCodi)
				.collect(Collectors.toList());

		assertEquals(Arrays.asList("BCK_SYN", "BCK_AS", "BCK_SR", "ENR_S", "ENR_M"), codis);
		assertFalse(codis.contains("SRV_A"));
	}

	@Test
	void addSubsistemaAmbServeiNoRegistraElServeiComASubsistema() {
		SalutHelper.addSubsistemaExit(SubsistemesEnum.BCK_SYN, "SRV_A", 100);

		List<String> codis = SalutHelper.getSubsistemesSalut().stream()
				.map(SubsistemaSalut::getCodi)
				.collect(Collectors.toList());

		assertTrue(codis.contains("BCK_SYN"));
		assertFalse(codis.contains("SRV_A"));
	}

	@Test
	void getIntegracionsSalutCalculaPeticionsTotalsIPerEntorn() {
		AplicacioServiceImpl service = new AplicacioServiceImpl();
		ScspCoreEmAplicacionRepository aplicacioRepository = mock(ScspCoreEmAplicacionRepository.class);
		ScspCoreEmAutorizacionCertificadoRepository autoritzacioRepository = mock(ScspCoreEmAutorizacionCertificadoRepository.class);
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ReflectionTestUtils.setField(service, "scspCoreEmAplicacionRepository", aplicacioRepository);
		ReflectionTestUtils.setField(service, "scspCoreEmAutorizacionCertificadoRepository", autoritzacioRepository);
		ReflectionTestUtils.setField(service, "serveiRepository", serveiRepository);

		ScspCoreEmAplicacionEntity aplicacioA = aplicacio(1, "A12345678", "Aplicació A");
		ScspCoreEmAplicacionEntity aplicacioB = aplicacio(2, "B12345678", "Aplicació B");
		ScspCoreServicioEntity serveiA = servei("SRV_A", "Servei A", "https://servei-a.test");
		ScspCoreServicioEntity serveiB = servei("SRV_B", "Servei B", null);

		when(aplicacioRepository.findAll()).thenReturn(Arrays.asList(aplicacioB, aplicacioA));
		when(autoritzacioRepository.findAll()).thenReturn(Arrays.asList(
				autoritzacio(aplicacioA, serveiA),
				autoritzacio(aplicacioB, serveiB)));
		when(serveiRepository.findByActiuTrue()).thenReturn(Collections.emptyList());

		// Injecta estadístiques en memòria per a aplicacioA/SRV_A (solicitantId="S1234567A")
		for (int i = 0; i < 10; i++) SalutHelper.addIntegracioExit("S1234567A", "SRV_A", 100);
		for (int i = 0; i < 5; i++) SalutHelper.addIntegracioError("S1234567A", "SRV_A");

		List<IntegracioSalut> integracions = service.getIntegracionsSalut(
				OffsetDateTime.parse("2026-05-11T10:00:00+02:00"),
				null);

		assertEquals(Arrays.asList("APP-1", "APP-2"), Arrays.asList(
				integracions.get(0).getCodi(),
				integracions.get(1).getCodi()));

		IntegracioPeticions peticionsA = integracions.get(0).getPeticions();
		assertEquals(10L, peticionsA.getTotalOk());
		assertEquals(5L, peticionsA.getTotalError());
		assertEquals(10L, peticionsA.getPeticionsOkUltimPeriode());
		assertEquals(5L, peticionsA.getPeticionsErrorUltimPeriode());
		assertEquals(EstatSalutEnum.DEGRADED, integracions.get(0).getEstat());
		assertTrue(peticionsA.getPeticionsPerEntorn().containsKey("SRV_A|Organisme"));
		assertEquals("https://servei-a.test", peticionsA.getPeticionsPerEntorn().get("SRV_A|Organisme").getEndpoint());
		assertEquals(10L, peticionsA.getPeticionsPerEntorn().get("SRV_A|Organisme").getTotalOk());

		IntegracioPeticions peticionsB = integracions.get(1).getPeticions();
		assertEquals(EstatSalutEnum.UNKNOWN, integracions.get(1).getEstat());
		assertTrue(peticionsB.getPeticionsPerEntorn().containsKey("SRV_B|Organisme"));
		assertEquals("SRV_B", peticionsB.getPeticionsPerEntorn().get("SRV_B|Organisme").getEndpoint());
	}

	@Test
	void getIntegracionsSalutInclouServeisActiusIStatsDeServei() {
		AplicacioServiceImpl service = new AplicacioServiceImpl();
		ScspCoreEmAplicacionRepository aplicacioRepository = mock(ScspCoreEmAplicacionRepository.class);
		ScspCoreEmAutorizacionCertificadoRepository autoritzacioRepository = mock(ScspCoreEmAutorizacionCertificadoRepository.class);
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ReflectionTestUtils.setField(service, "scspCoreEmAplicacionRepository", aplicacioRepository);
		ReflectionTestUtils.setField(service, "scspCoreEmAutorizacionCertificadoRepository", autoritzacioRepository);
		ReflectionTestUtils.setField(service, "serveiRepository", serveiRepository);

		when(aplicacioRepository.findAll()).thenReturn(Collections.emptyList());
		when(autoritzacioRepository.findAll()).thenReturn(Collections.emptyList());
		when(serveiRepository.findByActiuTrue()).thenReturn(Arrays.asList(
				serveiActiu("SRV_R", "Servei Enrutador", ServeiTipusEnumDto.ENRUTADOR, "https://enrutador.test"),
				serveiActiu("SRV_B", "Servei Backoffice", ServeiTipusEnumDto.BACKOFFICE, "https://backoffice.test")));

		for (int i = 0; i < 3; i++) SalutHelper.addIntegracioExit("S1234567A", "SRV_B", 100);
		SalutHelper.addIntegracioError(null, "SRV_R");

		List<IntegracioSalut> integracions = service.getIntegracionsSalut();

		assertEquals(Arrays.asList("SRV_B", "SRV_R"), Arrays.asList(
				integracions.get(0).getCodi(),
				integracions.get(1).getCodi()));

		IntegracioPeticions peticionsBackoffice = integracions.get(0).getPeticions();
		assertEquals("https://backoffice.test", peticionsBackoffice.getEndpoint());
		assertEquals(3L, peticionsBackoffice.getTotalOk());
		assertEquals(0L, peticionsBackoffice.getTotalError());

		IntegracioPeticions peticionsEnrutador = integracions.get(1).getPeticions();
		assertEquals("https://enrutador.test", peticionsEnrutador.getEndpoint());
		assertEquals(0L, peticionsEnrutador.getTotalOk());
		assertEquals(1L, peticionsEnrutador.getTotalError());
	}

	@Test
	void getIntegracionsSalutAfegeixSufixNumericAlsCodisDuplicats() {
		AplicacioServiceImpl service = new AplicacioServiceImpl();
		ScspCoreEmAplicacionRepository aplicacioRepository = mock(ScspCoreEmAplicacionRepository.class);
		ScspCoreEmAutorizacionCertificadoRepository autoritzacioRepository = mock(ScspCoreEmAutorizacionCertificadoRepository.class);
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ReflectionTestUtils.setField(service, "scspCoreEmAplicacionRepository", aplicacioRepository);
		ReflectionTestUtils.setField(service, "scspCoreEmAutorizacionCertificadoRepository", autoritzacioRepository);
		ReflectionTestUtils.setField(service, "serveiRepository", serveiRepository);

		when(aplicacioRepository.findAll()).thenReturn(Arrays.asList(
				aplicacio(30, "C12345678", "PLATAFORMA DE INTERMEDIACION - C"),
				aplicacio(10, "A12345678", "PLATAFORMA DE INTERMEDIACION - A"),
				aplicacio(20, "B12345678", "PLATAFORMA DE INTERMEDIACION - B")));
		when(autoritzacioRepository.findAll()).thenReturn(Collections.emptyList());
		when(serveiRepository.findByActiuTrue()).thenReturn(Collections.emptyList());

		List<IntegracioSalut> integracions = service.getIntegracionsSalut();

		assertEquals(Arrays.asList("PID", "PID2", "PID3"), Arrays.asList(
				integracions.get(0).getCodi(),
				integracions.get(1).getCodi(),
				integracions.get(2).getCodi()));
	}

	private ScspCoreEmAplicacionEntity aplicacio(Integer id, String nif, String cn) {
		ScspCoreEmAplicacionEntity aplicacio = ScspCoreEmAplicacionEntity
				.getBuilder(nif, "numero-serie-" + id, cn, null)
				.build();
		ReflectionTestUtils.setField(aplicacio, "idAplicacion", id);
		return aplicacio;
	}

	private ScspCoreServicioEntity servei(String codi, String nom, String urlSincrona) {
		ScspCoreServicioEntity servei = ScspCoreServicioEntity.getBuilder(codi).build();
		ReflectionTestUtils.setField(servei, "descripcion", nom);
		ReflectionTestUtils.setField(servei, "urlSincrona", urlSincrona);
		return servei;
	}

	private ServeiEntity serveiActiu(String codi, String nom) {
		return serveiActiu(codi, nom, ServeiTipusEnumDto.BACKOFFICE, null);
	}

	private ServeiEntity serveiActiu(String codi, String nom, ServeiTipusEnumDto tipus, String endpoint) {
		ServeiEntity.Builder builder = ServeiEntity
				.getBuilder(codi, nom, tipus);
		if (ServeiTipusEnumDto.BACKOFFICE.equals(tipus)) {
			builder.backofficeCaibUrl(endpoint);
		} else {
			builder.urlPerDefecte(endpoint);
		}
		return builder.build();
	}

	private ScspCoreEmAutorizacionCertificadoEntity autoritzacio(
			ScspCoreEmAplicacionEntity aplicacio,
			ScspCoreServicioEntity servei) {
		ScspCoreEmAutorizacionOrganismoEntity organisme = ScspCoreEmAutorizacionOrganismoEntity
				.getBuilder("S1234567A", "Organisme", new Date())
				.build();
		return ScspCoreEmAutorizacionCertificadoEntity
				.getBuilder(servei, aplicacio, organisme)
				.build();
	}

}
