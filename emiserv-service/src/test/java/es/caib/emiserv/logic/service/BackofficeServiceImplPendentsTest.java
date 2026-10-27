package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.config.SchedulingConfig;
import es.caib.emiserv.logic.helper.PlanificadorAsincronHelper;
import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.repository.BackofficePeticioRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Processament periòdic de les peticions asíncrones pendents (processar com a síncrones).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class BackofficeServiceImplPendentsTest {

	@Test
	void laTascaNomesEsRegistraUnaVegada() throws Exception {
		SchedulingConfig schedulingConfig = new SchedulingConfig();
		ReflectionTestUtils.setField(schedulingConfig, "backofficeService", mock(BackofficeService.class));
		schedulingConfig.setEnvironment(new MockEnvironment());
		ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();

		schedulingConfig.configureTasks(registrar);

		assertEquals(1, registrar.getTriggerTaskList().size());
		// Amb @Scheduled al mètode, @EnableScheduling el tornaria a registrar: dues execucions
		// simultànies processarien les mateixes peticions i cridarien dues vegades els backoffices
		assertNull(BackofficeServiceImpl.class.getMethod("peticioBackofficeAsyncProcessarPendents").getAnnotation(Scheduled.class));
		((org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler)registrar.getScheduler()).shutdown();
	}

	@Test
	void laTascaDelegaAlPlanificador() {
		BackofficeServiceImpl service = new BackofficeServiceImpl();
		PlanificadorAsincronHelper planificador = mock(PlanificadorAsincronHelper.class);
		ReflectionTestUtils.setField(service, "planificadorAsincronHelper", planificador);

		service.peticioBackofficeAsyncProcessarPendents();

		verify(planificador).planificar();
	}


}
