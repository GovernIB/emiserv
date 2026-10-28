package es.caib.emiserv.back.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.emiserv.logic.intf.dto.InterficieUsuariEnumDto;
import es.caib.emiserv.logic.intf.service.AplicacioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Comprova els endpoints de la sessio d'usuari que empra la interficie REACT.
 */
public class SessioUsuariControllerTest {

	private AplicacioService aplicacioService;
	private MockMvc mockMvc;

	@BeforeEach
	public void configurar() {
		aplicacioService = mock(AplicacioService.class);
		mockMvc = MockMvcBuilders.standaloneSetup(
				new SessioUsuariController(aplicacioService, " EMS_ADMIN, EMS_RESP ")).build();
		SecurityContextHolder.clearContext();
	}

	@AfterEach
	public void netejar() {
		SecurityContextHolder.clearContext();
	}

	private void autenticar(String codi, String... autoritats) {
		List<SimpleGrantedAuthority> authorities = Arrays.stream(autoritats).
				map(SimpleGrantedAuthority::new).
				collect(Collectors.toList());
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(codi, "N/A", authorities));
	}

	@Test
	public void sessioRetornaCodiIRols() throws Exception {
		autenticar("u1", "EMS_RESP", "ALTRE_ROL");
		when(aplicacioService.getInterficieEfectiva()).thenReturn(InterficieUsuariEnumDto.REACT);
		when(aplicacioService.getInterficiePreferida()).thenReturn(null);
		String cos = mockMvc.perform(get("/api/sessioUsuari")).
				andExpect(status().isOk()).
				andReturn().getResponse().getContentAsString();
		JsonNode json = new ObjectMapper().readTree(cos);
		assertEquals("u1", json.get("codi").asText());
		assertEquals(1, json.get("rols").size());
		assertEquals("EMS_RESP", json.get("rols").get(0).asText());
		assertEquals("REACT", json.get("interficie").asText());
		assertTrue(json.get("interficiePreferida").isNull());
	}

	@Test
	public void senseSessio401() throws Exception {
		mockMvc.perform(get("/api/sessioUsuari")).
				andExpect(status().isUnauthorized());
	}

	@Test
	public void loginNoRedirigeixAOrigenExtern() throws Exception {
		for (String extern: new String[] {"https://evil.example", "//evil.example/x", "/\\evil.example", "javascript:alert(1)"}) {
			String location = mockMvc.perform(get("/sessioUsuari/login").param("redirect", extern)).
					andExpect(status().isFound()).
					andReturn().getResponse().getHeader("Location");
			assertTrue(location.endsWith("/reactapp"), extern + " -> " + location);
		}
	}

	@Test
	public void loginRedirigeixAOrigensPermesos() throws Exception {
		for (String permes: new String[] {"/reactapp/servei", "http://localhost:5173/reactapp", "http://localhost/reactapp/x"}) {
			String location = mockMvc.perform(get("/sessioUsuari/login").param("redirect", permes)).
					andExpect(status().isFound()).
					andReturn().getResponse().getHeader("Location");
			assertEquals(permes, location);
		}
	}

	@Test
	public void canviInterficie204() throws Exception {
		autenticar("u1", "EMS_ADMIN");
		mockMvc.perform(put("/api/sessioUsuari/interficie").
				contentType(MediaType.APPLICATION_JSON).
				content("{\"interficie\":\"REACT\"}")).
				andExpect(status().isNoContent());
		verify(aplicacioService).updateInterficie(InterficieUsuariEnumDto.REACT);
		mockMvc.perform(put("/api/sessioUsuari/interficie").
				contentType(MediaType.APPLICATION_JSON).
				content("{\"interficie\":null}")).
				andExpect(status().isNoContent());
		verify(aplicacioService).updateInterficie(null);
	}

}
