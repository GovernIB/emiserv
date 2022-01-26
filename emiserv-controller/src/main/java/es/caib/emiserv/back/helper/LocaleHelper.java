package es.caib.emiserv.back.helper;

import es.caib.emiserv.logic.intf.service.AplicacioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

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

		// Logs per problemes amb idioma
		if ("/emiservback/servei".equals(request.getRequestURI())) {
			log.debug("[LOCALEH] Request language: '{}'", request.getLocale());
			log.debug("[LOCALEH] Response language: '{}'", response.getLocale());
		}

		String sessionLocale = (String)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_LOCALE);
		if (forsarRefresc || (request.getUserPrincipal() != null && sessionLocale == null)) {
			log.debug("[LOCALEH] Refrescant locale de la sessió d'usuari (" +
					"request.userPrincipal=" + request.getUserPrincipal().getName() + ", " +
					"sessionLocale=" + sessionLocale + ", " +
					"forsarRefresc=" + forsarRefresc + ")");
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			String idiomaUsuariActual = aplicacioService.getIdiomaUsuariActual();
			if (idiomaUsuariActual == null) {
				idiomaUsuariActual = localeResolver.resolveLocale(request).getLanguage().substring(0, 2);
				log.debug(
						"[LOCALEH] No hi ha idioma configurat per l'usuari " + request.getUserPrincipal().getName() + ", " +
						"idioma obtingut del localeResolver: " + idiomaUsuariActual);
			} else {
				log.debug("[LOCALEH] Idioma configurat a les preferències de l'usuari " + request.getUserPrincipal() + ": " + idiomaUsuariActual);
				Locale locale = StringUtils.parseLocaleString(idiomaUsuariActual.toLowerCase());
				log.debug("[LOCALEH] Configurant localeResolver amb locale " + locale);
				localeResolver.setLocale(
						request,
						response,
						locale);
			}
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_LOCALE,
					idiomaUsuariActual);
		}
	}

}
