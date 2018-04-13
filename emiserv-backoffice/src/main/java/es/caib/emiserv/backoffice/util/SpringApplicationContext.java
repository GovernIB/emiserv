/**
 * 
 */
package es.caib.emiserv.backoffice.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Utilitat per a obtenir beans de l'applicationContext de Spring.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SpringApplicationContext implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public static Object getBean(
			String beanName) {
		return applicationContext.getBean(beanName);
	}
	public static <T> T getBean(
			Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	@Override
	public void setApplicationContext(
			ApplicationContext appContext) throws BeansException {
		applicationContext = appContext;
	}

}
