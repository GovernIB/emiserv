/**
 * 
 */
package es.caib.emiserv.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.emiserv.war.helper.NodecoHelper;

/**
 * Interceptor per a redirigir les peticions a finestres sense
 * decoració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NodecoInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		boolean resposta = NodecoHelper.comprovarNodecoInterceptor(request, response);
		//System.out.println(">>> NODECO: " + request.getRequestURI() + ", " + AjaxHelper.isAjax(request));
		return resposta;
	}

}
