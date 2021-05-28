package es.caib.emiserv.back.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.emiserv.logic.intf.service.AplicacioService;

/**
 * Helper per a gestionar l'idioma (locale) de l'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LocaleHelper {

	public static final String SESSION_ATTRIBUTE_LOCALE = "AplicacioInterceptor.sessionLocale";

	public static void processarLocale(
			HttpServletRequest request,
			HttpServletResponse response,
			AplicacioService aplicacioService,
			boolean forsarRefresc) {
		String sessionLocale = (String)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_LOCALE);
		if (forsarRefresc || (request.getUserPrincipal() != null && sessionLocale == null)) {
			String idiomaUsuariActual = aplicacioService.getIdiomaUsuariActual();
			if (idiomaUsuariActual != null) {
				LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
				localeResolver.setLocale(
						request,
						response,
						StringUtils.parseLocaleString(idiomaUsuariActual));
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_LOCALE,
						idiomaUsuariActual);
			}
		}
	}

}
