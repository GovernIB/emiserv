/**
 * 
 */
package es.caib.emiserv.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.emiserv.core.api.service.AplicacioService;
import es.caib.emiserv.war.helper.AplicacioHelper;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioInterceptor extends HandlerInterceptorAdapter {

	public static final String REQUEST_ATTRIBUTE_LOCALE = "requestLocale";

	@Autowired
	private AplicacioService aplicacioService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		AplicacioHelper.comprovarVersioActual(request, aplicacioService);
		AplicacioHelper.getUsuariActual(request, aplicacioService);
		String locale = AplicacioHelper.processarLocale(request,response, aplicacioService);
		request.setAttribute(
				REQUEST_ATTRIBUTE_LOCALE,
				locale);
		return true;
	}

}
