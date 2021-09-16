/**
 * 
 */
package es.scsp.emiserv.backoffice;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import es.caib.emiserv.logic.intf.service.BackofficeService;
import lombok.extern.slf4j.Slf4j;

/**
 * Localitzador de l'EJB corresponent al servei BackofficeService.
 * 
 * Aquesta classe no està a dins el paquet es.caib perquè així s'inclou
 * automàticament a dins el context Spring de les llibreries SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
public class BackofficeServiceEjbLocator implements ApplicationContextAware {

	private static final String EJB_JNDI_PREFIX = "java:app/emiserv-ejb/";
	private static final String EJB_JNDI_SUFFIX = "";
	private static ApplicationContext applicationContext;

	public static BackofficeService getBackofficeService() {
		return applicationContext.getBean(BackofficeService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean backofficeService() {
		return getLocalEjbFactoyBean(BackofficeService.class);
	}

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX;
		log.debug("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		BackofficeServiceEjbLocator.applicationContext = applicationContext;
	}

}
