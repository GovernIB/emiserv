package es.caib.emiserv.logic.config;

import es.caib.emiserv.logic.intf.service.BackofficeService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer, EnvironmentAware {

    @Autowired
    private BackofficeService backofficeService;

    private final int POOL_SIZE = 10;

    @Setter
    private Environment environment;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("emiserv-scheduled-task-pool-");
        threadPoolTaskScheduler.initialize();

        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);

        scheduledTaskRegistrar.addTriggerTask(
                () -> backofficeService.peticioBackofficeAsyncProcessarPendents(),
                triggerContext -> {
                    Long periode = 10000L;
                    try {
                        periode = Long.parseLong(environment.getProperty("es.caib.emiserv.tasca.backoffice.async.processar.pendents", "10000"));
                    } catch (NumberFormatException ex) {
                        log.error("No s'ha configurat un valor enter vàlid per a la propietat 'es.caib.emiserv.tasca.backoffice.async.processar.pendents'.");
                    }
                    PeriodicTrigger trigger = new PeriodicTrigger(periode, TimeUnit.MILLISECONDS);
                    trigger.setFixedRate(true);
                    trigger.setInitialDelay(0L);
                    return trigger.nextExecutionTime(triggerContext);
                }
        );
    }
}
