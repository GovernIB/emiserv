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

/**
 * Interceptor per a les accions de context de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SessioInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;



	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		AplicacioHelper.processarAutenticacio(request, aplicacioService);
		return true;
	}

}
