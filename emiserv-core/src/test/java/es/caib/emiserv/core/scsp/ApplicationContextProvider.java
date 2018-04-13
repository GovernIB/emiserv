/**
 * 
 */
package es.caib.emiserv.core.scsp;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Permet accedir al context de Spring.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		ApplicationContextProvider.applicationContext = applicationContext;
	}

}
