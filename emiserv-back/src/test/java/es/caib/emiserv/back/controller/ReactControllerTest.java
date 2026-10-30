package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.helper.ReactAppHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprova que el SPA es pot servir des del classpath (META-INF/resources/reactapp) quan
 * l'aplicacio web no el te. Els fitxers de prova son a
 * src/test/resources/META-INF/resources/reactapp.
 */
public class ReactControllerTest {

	private ReactController controller;

	@BeforeEach
	public void configurar() {
		controller = controlador("META-INF/resources");
	}

	private static ReactController controlador(String classpathPrefix) {
		ReactController controller = new ReactController();
		// Aplicacio web sense cap fitxer: tot s'ha de trobar al classpath
		MockServletContext servletContext = new MockServletContext("file:/no-existeix");
		ReflectionTestUtils.setField(controller, "servletContext", servletContext);
		ReflectionTestUtils.setField(controller, "reactAppHelper", new ReactAppHelper(servletContext, classpathPrefix));
		return controller;
	}

	@Test
	public void rutaDelSpaRetornaIndexHtmlDelClasspath() throws IOException {
		ResponseEntity<?> resposta = controller.serveReact(peticio("/reactapp/serveis/123"));
		assertEquals(200, resposta.getStatusCodeValue());
		assertEquals(MediaType.TEXT_HTML, resposta.getHeaders().getContentType());
		assertTrue(cos(resposta).contains("<div id=\"root\">"));
	}

	@Test
	public void fitxerEstaticDelClasspath() throws IOException {
		ResponseEntity<?> resposta = controller.serveReact(peticio("/reactapp/assets/test.js"));
		assertEquals(200, resposta.getStatusCodeValue());
		assertTrue(cos(resposta).contains("console.log"));
	}

	@Test
	public void fitxerEstaticInexistentRetorna404() {
		assertEquals(404, controller.serveReact(peticio("/reactapp/assets/no-existeix.js")).getStatusCodeValue());
	}

	@Test
	public void carpetaNoEsServeixComAFitxer() throws IOException {
		ResponseEntity<?> resposta = controller.serveReact(peticio("/reactapp/assets"));
		assertEquals(200, resposta.getStatusCodeValue());
		assertEquals(MediaType.TEXT_HTML, resposta.getHeaders().getContentType());
		assertTrue(cos(resposta).contains("<div id=\"root\">"));
	}

	@Test
	public void senseSpaLesRutesRedirigeixenALaJsp() {
		ReactController senseSpa = controlador("no-hi-ha-spa");
		ResponseEntity<?> resposta = senseSpa.serveReact(peticio("/reactapp/serveis/123"));
		assertEquals(302, resposta.getStatusCodeValue());
		assertEquals("/emiservback/servei", resposta.getHeaders().getLocation().toString());
		assertEquals(302, senseSpa.serveReact(peticio("/reactapp")).getStatusCodeValue());
		// Els estatics inexistents continuen donant 404
		assertEquals(404, senseSpa.serveReact(peticio("/reactapp/assets/test.js")).getStatusCodeValue());
	}

	private static MockHttpServletRequest peticio(String path) {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/emiservback" + path);
		request.setContextPath("/emiservback");
		return request;
	}

	private static String cos(ResponseEntity<?> resposta) throws IOException {
		return new String(((InputStreamResource)resposta.getBody()).getInputStream().readAllBytes(), StandardCharsets.UTF_8);
	}

}
