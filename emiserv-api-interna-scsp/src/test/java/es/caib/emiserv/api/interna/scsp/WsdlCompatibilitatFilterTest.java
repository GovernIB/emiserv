package es.caib.emiserv.api.interna.scsp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Normalització de les URLs del servei que acceptava Axis2 (llibreries SCSP 4).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class WsdlCompatibilitatFilterTest {

	private static final String CONTEXT = "/emiservapi/interna/scsp";

	@ParameterizedTest
	@CsvSource({
			"/services/peticionSincrona.peticionSincronaHttpSoap11Endpoint/,   /services/peticionSincrona",
			"/services/peticionSincrona.peticionSincronaHttpSoap11Endpoint,    /services/peticionSincrona",
			"/services/peticionSincrona.peticionSincronaHttpsSoap11Endpoint/,  /services/peticionSincrona",
			"/services/peticionSincrona.peticionSincronaHttpSoap12Endpoint/,   /services/peticionSincrona",
			"/services/peticionAsincrona.peticionAsincronaHttpSoap11Endpoint/, /services/peticionAsincrona",
			"/services/peticionSincrona/,                                      /services/peticionSincrona",
			"/services/peticionAsincrona//,                                    /services/peticionAsincrona",
			"/peticionSincrona/,                                               /peticionSincrona"
	})
	void urlsAxis2(String path, String esperat) {
		assertEquals(CONTEXT + esperat, WsdlCompatibilitatFilter.normalitzarPath(CONTEXT + path));
	}

	@ParameterizedTest
	@CsvSource({
			"/services/peticionSincrona",
			"/services/peticionAsincrona",
			"/peticionSincrona",
			"/",
			"/index.html",
			"/services/altreServei/",
			"/services/altreServei.altreServeiHttpSoap11Endpoint/"
	})
	void urlsSenseCanvis(String path) {
		assertEquals(CONTEXT + path, WsdlCompatibilitatFilter.normalitzarPath(CONTEXT + path));
	}

	@Test
	void peticioNormalitzada() {
		HttpServletRequest request = request(
				CONTEXT + "/services/peticionSincrona.peticionSincronaHttpSoap11Endpoint/",
				"/services/peticionSincrona.peticionSincronaHttpSoap11Endpoint/");

		HttpServletRequest normalitzada = WsdlCompatibilitatFilter.normalitzarUrl(request);

		assertEquals(CONTEXT + "/services/peticionSincrona", normalitzada.getRequestURI());
		assertEquals("https://proves.caib.es" + CONTEXT + "/services/peticionSincrona", normalitzada.getRequestURL().toString());
		assertEquals("/services/peticionSincrona", normalitzada.getPathInfo());
	}

	@Test
	void peticioSenseCanvis() {
		HttpServletRequest request = request(CONTEXT + "/services/peticionSincrona", "/services/peticionSincrona");

		assertSame(request, WsdlCompatibilitatFilter.normalitzarUrl(request));
	}

	private static HttpServletRequest request(String requestUri, String pathInfo) {
		return (HttpServletRequest)Proxy.newProxyInstance(
				WsdlCompatibilitatFilterTest.class.getClassLoader(),
				new Class<?>[] {HttpServletRequest.class},
				(proxy, method, args) -> {
					switch (method.getName()) {
						case "getRequestURI": return requestUri;
						case "getRequestURL": return new StringBuffer("https://proves.caib.es" + requestUri);
						case "getPathInfo": return pathInfo;
						default: return null;
					}
				});
	}

}
