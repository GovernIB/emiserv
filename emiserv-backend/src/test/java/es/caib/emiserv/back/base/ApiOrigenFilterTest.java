package es.caib.emiserv.back.base;

import es.caib.emiserv.back.base.config.ApiOrigenFilter;
import es.caib.emiserv.back.base.config.BaseWebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Protecció CSRF de les accions de l'API: comprovació de les capçaleres Origin i Referer.
 *
 * @author Limit Tecnologies
 */
class ApiOrigenFilterTest {

	private final ApiOrigenFilter filter = new ApiOrigenFilter(
			BaseWebSecurityConfig.apiRequestMatcher(),
			"http://localhost:5173, https://altre.example/");

	@Test
	void unaAccioDUnAltreLlocEsRebutja() throws Exception {
		MockHttpServletResponse response = executar(peticio("POST", "/api/servei/1/artifacts/action/ACTIVAR").
				origin("https://evil.example"));
		assertEquals(403, response.getStatus());
		assertTrue(response.getContentAsString().contains("\"status\":403"));
	}

	@Test
	void unaAccioDelMateixLlocPassa() throws Exception {
		assertPassa(peticio("POST", "/api/servei/1/artifacts/action/ACTIVAR").origin("http://emiserv.caib.es:8080"));
		assertPassa(peticio("DELETE", "/api/servei/1").origin("http://EMISERV.caib.es"));
	}

	@Test
	void senseOriginNiRefererPassa() throws Exception {
		assertPassa(peticio("POST", "/api/servei/1/artifacts/action/ACTIVAR"));
	}

	@Test
	void senseOriginEsComprovaElReferer() throws Exception {
		assertPassa(peticio("PUT", "/api/sessioUsuari/interficie").referer("http://emiserv.caib.es:8080/emiservback/reactapp/serveis"));
		assertEquals(403, executar(peticio("PUT", "/api/sessioUsuari/interficie").referer("https://evil.example/pagina")).getStatus());
	}

	@Test
	void elsOrigensDeLaPropietatPassen() throws Exception {
		assertPassa(peticio("POST", "/api/servei/1/artifacts/action/ACTIVAR").origin("http://localhost:5173"));
		assertPassa(peticio("POST", "/api/servei/1/artifacts/action/ACTIVAR").origin("https://altre.example"));
		// El port forma part de l'origen
		assertEquals(403, executar(peticio("POST", "/api/servei/1/artifacts/action/ACTIVAR").origin("http://localhost:5174")).getStatus());
	}

	@Test
	void darrereDUnProxyEsComparaAmbElHostPublic() throws Exception {
		MockHttpServletRequest request = peticio("POST", "/api/servei/1/artifacts/action/ACTIVAR").
				origin("https://emiserv.caib.es").
				build();
		request.setServerName("10.0.0.5");
		request.addHeader("Host", "10.0.0.5:8080");
		assertEquals(403, executar(request).getStatus());
		request.addHeader("X-Forwarded-Host", "emiserv.caib.es, proxy.intern");
		assertPassa(request);
	}

	@Test
	void originNullEsRebutja() throws Exception {
		assertEquals(403, executar(peticio("POST", "/api/servei/1/artifacts/action/ACTIVAR").origin("null")).getStatus());
	}

	@Test
	void lesConsultesINoApiNoEsComproven() throws Exception {
		assertPassa(peticio("GET", "/api/servei").origin("https://evil.example"));
		assertPassa(peticio("POST", "/servei/1/activar").origin("https://evil.example"));
	}

	private void assertPassa(Peticio peticio) throws Exception {
		assertPassa(peticio.build());
	}

	private void assertPassa(MockHttpServletRequest request) throws Exception {
		MockFilterChain chain = new MockFilterChain();
		MockHttpServletResponse response = new MockHttpServletResponse();
		filter.doFilter(request, response, chain);
		assertNotNull(chain.getRequest(), "La petició s'ha rebutjat (" + response.getStatus() + ")");
		assertEquals(200, response.getStatus());
	}

	private MockHttpServletResponse executar(Peticio peticio) throws Exception {
		return executar(peticio.build());
	}

	private MockHttpServletResponse executar(MockHttpServletRequest request) throws Exception {
		MockFilterChain chain = new MockFilterChain();
		MockHttpServletResponse response = new MockHttpServletResponse();
		filter.doFilter(request, response, chain);
		if (response.getStatus() == 403) {
			assertNull(chain.getRequest());
		}
		return response;
	}

	private static Peticio peticio(String metode, String cami) {
		return new Peticio(metode, cami);
	}

	private static class Peticio {
		private final MockHttpServletRequest request;
		Peticio(String metode, String cami) {
			request = new MockHttpServletRequest(metode, cami);
			request.setServletPath(cami);
			request.setServerName("emiserv.caib.es");
			request.setServerPort(8080);
		}
		Peticio origin(String origin) {
			request.addHeader("Origin", origin);
			return this;
		}
		Peticio referer(String referer) {
			request.addHeader("Referer", referer);
			return this;
		}
		MockHttpServletRequest build() {
			return request;
		}
	}

}
