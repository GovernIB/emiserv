package es.caib.emiserv.back.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import es.caib.emiserv.logic.intf.service.AplicacioService;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a gestionar l'idioma (locale) de l'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class LocaleHelper {

	public static final String SESSION_ATTRIBUTE_CONFIGURAT = LocaleHelper.class.getName() + ".CONFIGURAT";

	public static void processarLocale(
			HttpServletRequest request,
			HttpServletResponse response,
			AplicacioService aplicacioService,
			boolean forsarRefresc) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (forsarRefresc || (request.getUserPrincipal() != null && !isConfigurat(request))) {
			log.debug("Refrescant locale de la sessió d'usuari (" +
					"request.userPrincipal=" + request.getUserPrincipal() + ", " +
					"forsarRefresc=" + forsarRefresc + ")");
			String idiomaActual = aplicacioService.getIdiomaUsuariActual();
			if (idiomaActual != null) {
				log.debug("L'usuari " + request.getUserPrincipal() + " te configurat com a preferència d'idioma: " + idiomaActual);
				localeResolver.setLocale(
						request,
						response,
						StringUtils.parseLocaleString(idiomaActual));
			} else {
				log.debug("L'usuari " + request.getUserPrincipal() + " no te cap preferència d'idioma configurada");
				localeResolver.setLocale(
						request,
						response,
						null);
			}
			WebUtils.setSessionAttribute(
					request,
					SESSION_ATTRIBUTE_CONFIGURAT,
					new Boolean(true));
		}
	}

	private static boolean isConfigurat(HttpServletRequest request) {
		Boolean configurat = (Boolean)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_CONFIGURAT);
		return configurat != null && configurat.booleanValue();
	}

}
