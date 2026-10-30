package es.caib.emiserv.back.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import es.caib.emiserv.back.controller.api.ServeiApiController;

/**
 * Resolució del Pageable: els controladors de l'API REST (base-boot) reben null sense paràmetres de
 * paginació (i llavors el find torna només els enllaços del recurs, que necessita base-react); la
 * resta de controladors (JSP) mantenen el Pageable no paginat per defecte.
 */
class WebMvcConfigPageableTest {

	private final WebMvcConfig.ApiPageableHandlerMethodArgumentResolver apiResolver =
			new WebMvcConfig.ApiPageableHandlerMethodArgumentResolver();
	private final WebMvcConfig.CustomPageableHandlerMethodArgumentResolver jspResolver =
			new WebMvcConfig.CustomPageableHandlerMethodArgumentResolver();

	@Test
	void apiSensePaginacioRepNull() throws Exception {
		MethodParameter parameter = apiFindPageableParameter();
		assertTrue(apiResolver.supportsParameter(parameter));
		assertFalse(jspResolver.supportsParameter(parameter));
		assertNull(apiResolver.resolveArgument(parameter, null, new ServletWebRequest(new MockHttpServletRequest()), null));
	}

	@Test
	void apiAmbPaginacioRepLaPagina() throws Exception {
		MethodParameter parameter = apiFindPageableParameter();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("page", "2");
		request.addParameter("size", "5");
		Pageable pageable = apiResolver.resolveArgument(parameter, null, new ServletWebRequest(request), null);
		assertEquals(2, pageable.getPageNumber());
		assertEquals(5, pageable.getPageSize());
	}

	@Test
	void controladorsNoApiMantenenNoPaginat() throws Exception {
		MethodParameter parameter = new MethodParameter(
				Controladors.class.getDeclaredMethod("llistat", Pageable.class),
				0);
		assertFalse(apiResolver.supportsParameter(parameter));
		assertTrue(jspResolver.supportsParameter(parameter));
		jspResolver.setFallbackPageable(Pageable.unpaged());
		Pageable pageable = jspResolver.resolveArgument(parameter, null, new ServletWebRequest(new MockHttpServletRequest()), null);
		assertTrue(pageable.isUnpaged());
	}

	private MethodParameter apiFindPageableParameter() {
		for (Method method : ServeiApiController.class.getMethods()) {
			if ("find".equals(method.getName())) {
				Class<?>[] types = method.getParameterTypes();
				for (int i = 0; i < types.length; i++) {
					if (Pageable.class.equals(types[i])) {
						return new MethodParameter(method, i).withContainingClass(ServeiApiController.class);
					}
				}
			}
		}
		throw new IllegalStateException("No s'ha trobat el find de ServeiApiController");
	}

	static class Controladors {
		public void llistat(Pageable pageable) {
		}
	}

}
