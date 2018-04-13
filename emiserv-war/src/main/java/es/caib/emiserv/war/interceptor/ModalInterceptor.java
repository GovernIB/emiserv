/**
 * 
 */
package es.caib.emiserv.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.emiserv.war.helper.ModalHelper;

/**
 * Interceptor per a redirigir les peticions a finestres modals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ModalInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		boolean resposta = ModalHelper.comprovarModalInterceptor(request, response);
		// System.out.println(">>> MODAL: " + request.getRequestURI() + ", " + AjaxHelper.isAjax(request));
		return resposta;
	}

}
