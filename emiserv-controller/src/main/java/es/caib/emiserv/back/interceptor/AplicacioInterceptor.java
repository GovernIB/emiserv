/**
 * 
 */
package es.caib.emiserv.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.emiserv.back.helper.LocaleHelper;
import es.caib.emiserv.logic.intf.service.AplicacioService;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AplicacioInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		LocaleHelper.processarLocale(
				request,
				response,
				getAplicacioService(),
				false);
		return true;
	}

	private AplicacioService getAplicacioService() {
		return applicationContext.getBean(AplicacioService.class);
	}

}
