package es.caib.emiserv.war.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.caib.emiserv.core.api.service.AplicacioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

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
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_USUARI_ACTUAL,
					nomUsuariActual);
		}		
		return nomUsuariActual;
	}
	
	public static String getUsuariActual(HttpServletRequest request) {
		return (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_USUARI_ACTUAL);
	}
	
	public static String getVersioActual(HttpServletRequest request) {
		return (String)request.getSession().getServletContext().getAttribute(
				APPLICATION_ATTRIBUTE_VERSIO_ACTUAL);
	}

	public static String processarLocale(
			HttpServletRequest request,
			HttpServletResponse response,
			AplicacioService aplicacioService) {
		if (request.getUserPrincipal() != null) {
			try {
				String idioma_usuari = aplicacioService.getUsuariActual().getIdioma();

				LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
				localeResolver.setLocale(
						request,
						response,
						StringUtils.parseLocaleString(idioma_usuari));
				return idioma_usuari;
			} catch (Exception e) {
				LOGGER.error("Error establint l'idioma de l'usuari " + request.getUserPrincipal(), e);
			}
		}
		return RequestContextUtils.getLocale(request).getLanguage();
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

	private static final Logger LOGGER = LoggerFactory.getLogger(AplicacioHelper.class);
}
