/**
 * 
 */
package es.caib.emiserv.war.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import es.caib.emiserv.logic.intf.service.AplicacioService;
import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import es.caib.emiserv.logic.intf.service.ScspService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
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

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX;
		log.debug("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

}
