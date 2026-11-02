/**
 * 
 */
package es.caib.emiserv.war.config;

import es.caib.emiserv.logic.intf.base.service.PermissionEvaluatorService;
import es.caib.emiserv.logic.intf.base.service.ResourceApiService;
import es.caib.emiserv.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
@ConditionalOnWarDeployment
public class EjbClientConfig {

	private static final String EJB_JNDI_PREFIX = "java:app/emiserv-ejb/";
	private static final String EJB_JNDI_SUFFIX = "";

	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean backofficeService() {
		return getLocalEjbFactoyBean(BackofficeService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean redireccioService() {
		return getLocalEjbFactoyBean(RedireccioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean scspService() {
		return getLocalEjbFactoyBean(ScspService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiService() {
		return getLocalEjbFactoyBean(ServeiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatService() {
		return getLocalEjbFactoyBean(EntitatService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean consultaAsincronaService() {
		return getLocalEjbFactoyBean(ConsultaAsincronaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean explotacioService() {
		return getLocalEjbFactoyBean(ExplotacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean configService() {
		return getLocalEjbFactoyBean(ConfigService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean resourceApiService() {
		return getLocalEjbFactoyBean(ResourceApiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean permissionEvaluatorService() {
		return getLocalEjbFactoyBean(PermissionEvaluatorService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiResourceService() {
		return getLocalEjbFactoyBean(ServeiResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiRutaResourceService() {
		return getLocalEjbFactoyBean(ServeiRutaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiPermisResourceService() {
		return getLocalEjbFactoyBean(ServeiPermisResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiAutoritzacioResourceService() {
		return getLocalEjbFactoyBean(ServeiAutoritzacioResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiConfigScspResourceService() {
		return getLocalEjbFactoyBean(ServeiConfigScspResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean auditoriaBackofficeResourceService() {
		return getLocalEjbFactoyBean(AuditoriaBackofficeResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean auditoriaEnrutadorResourceService() {
		return getLocalEjbFactoyBean(AuditoriaEnrutadorResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioResourceService() {
		return getLocalEjbFactoyBean(AplicacioResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean emisorResourceService() {
		return getLocalEjbFactoyBean(EmisorResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean organismeResourceService() {
		return getLocalEjbFactoyBean(OrganismeResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean organismeCessionariResourceService() {
		return getLocalEjbFactoyBean(OrganismeCessionariResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean clauPublicaResourceService() {
		return getLocalEjbFactoyBean(ClauPublicaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean clauPrivadaResourceService() {
		return getLocalEjbFactoyBean(ClauPrivadaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean autoritatCertificacioResourceService() {
		return getLocalEjbFactoyBean(AutoritatCertificacioResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean scspModulResourceService() {
		return getLocalEjbFactoyBean(ScspModulResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean scspParametreResourceService() {
		return getLocalEjbFactoyBean(ScspParametreResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatResourceService() {
		return getLocalEjbFactoyBean(EntitatResourceService.class);
	}

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX;
		log.info("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

}
