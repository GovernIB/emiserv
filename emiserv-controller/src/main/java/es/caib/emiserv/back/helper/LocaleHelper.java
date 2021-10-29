package es.caib.emiserv.back.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.emiserv.logic.intf.service.AplicacioService;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a gestionar l'idioma (locale) de l'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
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
			log.debug("Refrescant locale de la sessió d'usuari (" +
					"request.userPrincipal=" + request.getUserPrincipal().getName() + ", " +
					"sessionLocale=" + sessionLocale + ", " +
					"forsarRefresc=" + forsarRefresc + ")");
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			String idiomaUsuariActual = aplicacioService.getIdiomaUsuariActual();
			if (idiomaUsuariActual == null) {
				idiomaUsuariActual = localeResolver.resolveLocale(request).getLanguage().substring(0, 2);
				log.debug(
						"No hi ha idioma configurat per l'usuari " + request.getUserPrincipal().getName() + ", " +
						"configurant idioma obtingut del localeResolver: " + idiomaUsuariActual);
			} else {
				log.debug("Idioma configurat a les preferències de l'usuari " + request.getUserPrincipal() + ": " + idiomaUsuariActual);
				localeResolver.setLocale(
						request,
						response,
						StringUtils.parseLocaleString(idiomaUsuariActual.toLowerCase()));
			}
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_LOCALE,
					idiomaUsuariActual);
		}
	}

}
