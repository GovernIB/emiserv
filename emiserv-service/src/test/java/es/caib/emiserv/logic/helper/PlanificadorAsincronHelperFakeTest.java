package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Els serveis backoffice en mode fake processen les peticions asíncrones com a síncrones encara
 * que estiguin configurats per reenviar-les al backoffice.
 */
class PlanificadorAsincronHelperFakeTest {

	private PlanificadorAsincronHelper planificador;
	private MockEnvironment environment;
	private ServeiEntity servei;

	@BeforeEach
	void setUp() {
		planificador = new PlanificadorAsincronHelper();
		environment = new MockEnvironment();
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ScspCoreServicioRepository scspCoreServicioRepository = mock(ScspCoreServicioRepository.class);
		ReflectionTestUtils.setField(planificador, "serveiRepository", serveiRepository);
		ReflectionTestUtils.setField(planificador, "scspCoreServicioRepository", scspCoreServicioRepository);
		ReflectionTestUtils.setField(planificador, "serveiFakeHelper", new ServeiFakeHelper(environment));
		servei = ServeiEntity.getBuilder("SCDCPAJU", "Padró", ServeiTipusEnumDto.BACKOFFICE).
				backofficeCaibAsyncTipus(BackofficeAsyncTipusEnumDto.REENVIAR_BACKOFFICE).
				build();
		servei.updateFake(true, ServeiFakeResultatEnumDto.OK, null);
		when(serveiRepository.findByBackofficeCaibAsyncTipus(BackofficeAsyncTipusEnumDto.PROCESSAR_SINCRONES)).thenReturn(Collections.emptyList());
		when(serveiRepository.findByTipusIn(anyList())).thenReturn(Collections.emptyList());
		when(serveiRepository.findByFakeTrue()).thenReturn(List.of(servei));
		ScspCoreServicioEntity servicio = ScspCoreServicioEntity.getBuilder("SCDCPAJU").build();
		ReflectionTestUtils.setField(servicio, "id", 10L);
		when(scspCoreServicioRepository.findByCodigoCertificadoIn(anyList())).thenReturn(List.of(servicio));
	}

	@Test
	void serveiFakeEsProcessaComASincron() {
		environment.setProperty(ServeiFakeHelper.PROPIETAT_PERMES, "true");

		Map<Long, ServeiEntity> serveis = planificador.getServeisPerCertificat();

		assertSame(servei, serveis.get(10L));
	}

	@Test
	void ambElFakeNoPermesNoEsProcessa() {
		environment.setProperty(ServeiFakeHelper.PROPIETAT_PERMES, "false");

		assertTrue(planificador.getServeisPerCertificat().isEmpty());
	}

}
