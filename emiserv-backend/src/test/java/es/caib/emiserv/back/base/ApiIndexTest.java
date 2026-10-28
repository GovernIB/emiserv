package es.caib.emiserv.back.base;

import es.caib.emiserv.logic.intf.base.service.ResourceApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de l'índex de l'API REST ({@code GET /api}).
 *
 * @author Limit Tecnologies
 */
@WebMvcTest
class ApiIndexTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ResourceApiService resourceApiService;

	@Test
	@WithMockUser(username = "admin", authorities = "EMS_ADMIN")
	void indexRetornaEnllacos() throws Exception {
		when(resourceApiService.resourceFindAllowed()).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api").accept(MediaTypes.HAL_FORMS_JSON)).
				andExpect(status().isOk()).
				andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_FORMS_JSON)).
				andExpect(jsonPath("$._links.self.href").exists());
	}

	@Test
	@WithMockUser(username = "resp", authorities = "EMS_RESP")
	void indexAmbRolResponsableRetorna200() throws Exception {
		when(resourceApiService.resourceFindAllowed()).thenReturn(Collections.emptyList());
		mockMvc.perform(get("/api").accept(MediaTypes.HAL_FORMS_JSON)).
				andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "altre", authorities = "ALTRE_ROL")
	void indexSenseRolEmiservRetorna403Json() throws Exception {
		mockMvc.perform(get("/api").accept(MediaType.TEXT_HTML, MediaType.ALL)).
				andExpect(status().isForbidden()).
				andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).
				andExpect(jsonPath("$.status").value(403));
	}

	@Test
	@WithMockUser(username = "altre", authorities = "ALTRE_ROL")
	void altresRutesNoExigeixenRolEmiserv() throws Exception {
		// Fora de l'API no s'aplica la restricció de rols (la resta de rutes es controlen com abans).
		mockMvc.perform(get("/servei").accept(MediaType.TEXT_HTML)).
				andExpect(status().isNotFound());
	}

	@Test
	void indexSenseSessioRetorna401() throws Exception {
		mockMvc.perform(get("/api").accept(MediaType.TEXT_HTML, MediaType.ALL)).
				andExpect(status().isUnauthorized()).
				andExpect(header().doesNotExist("Location")).
				andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).
				andExpect(jsonPath("$.status").value(401));
	}

	@Test
	void altresRutesSenseSessioRedirigeixenAlLogin() throws Exception {
		mockMvc.perform(get("/servei").accept(MediaType.TEXT_HTML)).
				andExpect(status().isFound()).
				andExpect(header().string("Location", endsWith(ApiTestApp.LOGIN_URL)));
	}

}
