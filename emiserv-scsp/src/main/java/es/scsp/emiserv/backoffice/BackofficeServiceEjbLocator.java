/**
 * 
 */
package es.scsp.emiserv.backoffice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import es.caib.emiserv.logic.intf.service.BackofficeService;

/**
 * Localitzador de l'EJB corresponent al servei BackofficeService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Configuration
public class BackofficeServiceEjbLocator implements ApplicationContextAware {

	private static final String EJB_JNDI_PREFIX = "java:app/emiserv-ejb/";
	private static final String EJB_JNDI_SUFFIX = "";
	private static ApplicationContext applicationContext;

	public static BackofficeService getBackofficeService() {
		return applicationContext.getBean(BackofficeService.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		BackofficeServiceEjbLocator.applicationContext = applicationContext;
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean serveiService() {
		return getLocalEjbFactoyBean(BackofficeService.class);
	}

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX;
		LOGGER.debug("Creating EJB proxy for serviceClass with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factory = new LocalStatelessSessionProxyFactoryBean();
		factory.setBusinessInterface(serviceClass);
		factory.setJndiName(jndiName);
		return factory;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(BackofficeServiceEjbLocator.class);

}
