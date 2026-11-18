package es.caib.emiserv.back.controller;

import es.caib.emiserv.logic.intf.dto.RedireccioRespostaFakeDto;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Comprova l'endpoint intern /scspFake que fan servir els enrutadors dels serveis en mode fake.
 */
public class ScspFakeControllerTest {

	private RedireccioService redireccioService;
	private MockMvc mockMvc;

	@BeforeEach
	public void configurar() {
		redireccioService = mock(RedireccioService.class);
		ScspFakeController controller = new ScspFakeController();
		ReflectionTestUtils.setField(controller, "redireccioService", redireccioService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void escriuCodiIXmlDelServei() throws Exception {
		byte[] peticio = "<peticio/>".getBytes("UTF-8");
		when(redireccioService.generarRespostaFake(eq("SVDCCAACPASWS01"), eq("ENT"), any(), eq(false)))
				.thenReturn(new RedireccioRespostaFakeDto(200, "<resposta>à</resposta>"));
		MvcResult res = mockMvc.perform(post("/scspFake/SVDCCAACPASWS01/ENT/operacio").content(peticio)).andReturn();
		assertEquals(200, res.getResponse().getStatus());
		assertEquals("<resposta>à</resposta>", res.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
		assertEquals(true, res.getResponse().getContentType().startsWith("text/xml"));
		verify(redireccioService).generarRespostaFake("SVDCCAACPASWS01", "ENT", peticio, false);
	}

	@Test
	public void entitatGuioEsNull() throws Exception {
		when(redireccioService.generarRespostaFake(eq("S"), eq(null), any(), eq(false)))
				.thenReturn(new RedireccioRespostaFakeDto(500, "<fault/>"));
		MvcResult res = mockMvc.perform(post("/scspFake/S/-/").content("<p/>".getBytes())).andReturn();
		assertEquals(500, res.getResponse().getStatus());
		assertEquals("<fault/>", res.getResponse().getContentAsString());
	}

	@Test
	public void servei404TeCosBuit() throws Exception {
		when(redireccioService.generarRespostaFake(eq("S"), eq("E"), any(), eq(false)))
				.thenReturn(new RedireccioRespostaFakeDto(404, "ignorat"));
		MvcResult res = mockMvc.perform(post("/scspFake/S/E/").content("<p/>".getBytes())).andReturn();
		assertEquals(404, res.getResponse().getStatus());
		assertEquals("", res.getResponse().getContentAsString());
	}

	@Test
	public void operacioAsincronaPassaElFlag() throws Exception {
		when(redireccioService.generarRespostaFake(eq("S"), eq("E"), any(), eq(true)))
				.thenReturn(new RedireccioRespostaFakeDto(500, "<fault/>"));
		MvcResult res = mockMvc.perform(post("/scspFake/S/E/peticionAsincrona").content("<p/>".getBytes())).andReturn();
		assertEquals(500, res.getResponse().getStatus());
		verify(redireccioService).generarRespostaFake(eq("S"), eq("E"), any(), eq(true));
	}

	@Test
	public void adrecaNoLoopbackRetorna404SenseCridarElServei() throws Exception {
		MvcResult res = mockMvc.perform(post("/scspFake/S/E/").content("<p/>".getBytes())
				.with(request -> {
					request.setRemoteAddr("10.0.0.5");
					return request;
				})).andReturn();
		assertEquals(404, res.getResponse().getStatus());
		assertEquals("", res.getResponse().getContentAsString());
		verifyNoInteractions(redireccioService);
	}

	@Test
	public void adrecesLoopbackIPv4IIPv6Deleguen() throws Exception {
		when(redireccioService.generarRespostaFake(eq("S"), eq("E"), any(), eq(false)))
				.thenReturn(new RedireccioRespostaFakeDto(200, "<ok/>"));
		for (String adreca : new String[] {"127.0.0.1", "127.1.2.3", "::1", "0:0:0:0:0:0:0:1"}) {
			MvcResult res = mockMvc.perform(post("/scspFake/S/E/").content("<p/>".getBytes())
					.with(request -> {
						request.setRemoteAddr(adreca);
						return request;
					})).andReturn();
			assertEquals(200, res.getResponse().getStatus(), adreca);
		}
		verify(redireccioService, times(4)).generarRespostaFake(eq("S"), eq("E"), any(), eq(false));
	}
}
