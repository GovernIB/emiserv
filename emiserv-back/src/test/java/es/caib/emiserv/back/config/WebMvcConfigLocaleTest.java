package es.caib.emiserv.back.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Comprova l'idioma de la interficie JSP segons la capcalera Accept-Language: els missatges nomes
 * existeixen en catala i castella, i qualsevol altre idioma ha de fer servir el catala.
 */
public class WebMvcConfigLocaleTest {

	private final LocaleResolver localeResolver = new WebMvcConfig().localeResolver();

	@Test
	public void idiomaNoSuportatFaServirElCatala() {
		assertEquals("ca", idioma("en-US,en;q=0.5"));
		assertEquals("ca", idioma("fr"));
	}

	@Test
	public void senseCapcaleraFaServirElCatala() {
		assertEquals("ca", idioma(null));
	}

	@Test
	public void idiomesSuportats() {
		assertEquals("es", idioma("es-ES,es;q=0.9"));
		assertEquals("ca", idioma("ca"));
		// El primer idioma suportat de la llista
		assertEquals("es", idioma("en-US,es;q=0.8"));
	}

	private String idioma(String acceptLanguage) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		if (acceptLanguage != null) {
			request.addHeader("Accept-Language", acceptLanguage);
			request.setPreferredLocales(Arrays.asList(
					Arrays.stream(acceptLanguage.split(",")).
							map(l -> Locale.forLanguageTag(l.split(";")[0].trim())).
							toArray(Locale[]::new)));
		}
		return localeResolver.resolveLocale(request).getLanguage();
	}

}
