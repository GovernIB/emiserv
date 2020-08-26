/**
 * 
 */
package es.caib.emiserv.war.helper;

import javax.servlet.http.HttpServletRequest;

import es.caib.emiserv.core.api.service.AplicacioService;

/**
 * Utilitat per a gestionar accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioHelper {

	public static final String APPLICATION_ATTRIBUTE_VERSIO_ACTUAL = "AplicacioHelper.versioActual";
	public static final String SESSION_ATTRIBUTE_AUTH_PROCESSADA = "SessioHelper.autenticacioProcessada";
	private static final String SESSION_ATTRIBUTE_USUARI_ACTUAL = "AplicacioHelper.usuari.nom";

	public static void comprovarVersioActual(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		if (request.getUserPrincipal() != null) {
			String versioActual = (String)request.getSession().getServletContext().getAttribute(
					APPLICATION_ATTRIBUTE_VERSIO_ACTUAL);
			if (versioActual == null) {
				versioActual = aplicacioService.getVersioActual();
				request.getSession().getServletContext().setAttribute(
						APPLICATION_ATTRIBUTE_VERSIO_ACTUAL,
						versioActual);
			}
		}
	}
	
	public static String getUsuariActual(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		String nomUsuariActual = getUsuariActual(request);
		if (request.getUserPrincipal() != null && nomUsuariActual == null) {
			nomUsuariActual = aplicacioService.getUsuariActual().getNom();
			request.getSession().getServletContext().setAttribute(
					SESSION_ATTRIBUTE_USUARI_ACTUAL,
					nomUsuariActual);
		}		
		return nomUsuariActual;
	}
	
	public static String getUsuariActual(HttpServletRequest request) {
		String nomUsuariActual = (String)request.getSession().getServletContext().getAttribute(
				SESSION_ATTRIBUTE_USUARI_ACTUAL);
		return nomUsuariActual;
	}
	
	public static String getVersioActual(HttpServletRequest request) {
		return (String)request.getSession().getServletContext().getAttribute(
				APPLICATION_ATTRIBUTE_VERSIO_ACTUAL);
	}

	public static void processarAutenticacio(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		if (request.getUserPrincipal() != null) {
			Boolean autenticacioProcessada = (Boolean)request.getSession().getAttribute(
					SESSION_ATTRIBUTE_AUTH_PROCESSADA);
			if (autenticacioProcessada == null) {
				aplicacioService.processarAutenticacioUsuari();
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_AUTH_PROCESSADA,
						new Boolean(true));
			}
		}
	}
	public static boolean isAutenticacioProcessada(HttpServletRequest request) {
		return request.getSession().getAttribute(SESSION_ATTRIBUTE_AUTH_PROCESSADA) != null;
	}

}
