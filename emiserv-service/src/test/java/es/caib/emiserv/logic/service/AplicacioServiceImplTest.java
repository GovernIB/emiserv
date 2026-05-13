package es.caib.emiserv.logic.service;

import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.IntegracioPeticions;
import es.caib.comanda.model.server.monitoring.IntegracioSalut;
import es.caib.comanda.ms.salut.helper.SalutComponentsHelper;
import es.caib.comanda.ms.salut.helper.components.MonitorComponentsMemoria;
import es.caib.emiserv.logic.helper.SalutHelper;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAplicacionEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionCertificadoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionOrganismoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AplicacioServiceImplTest {

	@BeforeEach
	void resetIntegracioStats() throws Exception {
		Field field = SalutHelper.class.getDeclaredField("integracioComponentHelper");
		field.setAccessible(true);
		field.set(null, new SalutComponentsHelper(
				new MonitorComponentsMemoria(20),
				(codi) -> false,
				Collections.emptyList()));
	}

	@Test
	void getIntegracionsInfoRetornaAplicacionsConfiguradesOrdenadesPerId() {
		AplicacioServiceImpl service = new AplicacioServiceImpl();
		ScspCoreEmAplicacionRepository repository = mock(ScspCoreEmAplicacionRepository.class);
		ReflectionTestUtils.setField(service, "scspCoreEmAplicacionRepository", repository);

		when(repository.findAll()).thenReturn(Arrays.asList(
				aplicacio(20, "B12345678", "Aplicació B"),
				aplicacio(10, "A12345678", "Nom molt llarg ".repeat(25))));

		List<IntegracioInfo> integracions = service.getIntegracionsInfo();

		assertEquals(Arrays.asList("APP-10", "APP-20"), Arrays.asList(
				integracions.get(0).getCodi(),
				integracions.get(1).getCodi()));
		assertEquals(255, integracions.get(0).getNom().length());
		assertTrue(integracions.get(0).getNom().endsWith("..."));
		assertEquals("Aplicació B", integracions.get(1).getNom());
	}

	@Test
	void getIntegracionsSalutCalculaPeticionsTotalsIPerEntorn() {
		AplicacioServiceImpl service = new AplicacioServiceImpl();
		ScspCoreEmAplicacionRepository aplicacioRepository = mock(ScspCoreEmAplicacionRepository.class);
		ScspCoreEmAutorizacionCertificadoRepository autoritzacioRepository = mock(ScspCoreEmAutorizacionCertificadoRepository.class);
		ReflectionTestUtils.setField(service, "scspCoreEmAplicacionRepository", aplicacioRepository);
		ReflectionTestUtils.setField(service, "scspCoreEmAutorizacionCertificadoRepository", autoritzacioRepository);

		ScspCoreEmAplicacionEntity aplicacioA = aplicacio(1, "A12345678", "Aplicació A");
		ScspCoreEmAplicacionEntity aplicacioB = aplicacio(2, "B12345678", "Aplicació B");
		ScspCoreServicioEntity serveiA = servei("SRV_A", "Servei A", "https://servei-a.test");
		ScspCoreServicioEntity serveiB = servei("SRV_B", "Servei B", null);

		when(aplicacioRepository.findAll()).thenReturn(Arrays.asList(aplicacioB, aplicacioA));
		when(autoritzacioRepository.findAll()).thenReturn(Arrays.asList(
				autoritzacio(aplicacioA, serveiA),
				autoritzacio(aplicacioB, serveiB)));

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
		assertTrue(peticionsA.getPeticionsPerEntorn().containsKey("Organisme|SRV_A"));
		assertEquals("https://servei-a.test", peticionsA.getPeticionsPerEntorn().get("Organisme|SRV_A").getEndpoint());
		assertEquals(10L, peticionsA.getPeticionsPerEntorn().get("Organisme|SRV_A").getTotalOk());

		IntegracioPeticions peticionsB = integracions.get(1).getPeticions();
		assertEquals(EstatSalutEnum.UNKNOWN, integracions.get(1).getEstat());
		assertTrue(peticionsB.getPeticionsPerEntorn().containsKey("Organisme|SRV_B"));
		assertEquals("SRV_B", peticionsB.getPeticionsPerEntorn().get("Organisme|SRV_B").getEndpoint());
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
