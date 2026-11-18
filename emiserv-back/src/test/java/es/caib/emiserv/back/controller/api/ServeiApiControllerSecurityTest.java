package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.base.config.BaseWebSecurityConfig;
import es.caib.emiserv.back.base.config.HalFormsConfig;
import es.caib.emiserv.back.base.config.HalFormsTemplateBuilderConfig;
import es.caib.emiserv.back.base.config.MethodSecurityConfig;
import es.caib.emiserv.back.base.error.ResourceGlobalExceptionHandler;
import es.caib.emiserv.back.base.util.ResourceServiceLocator;
import es.caib.emiserv.back.config.WebMvcConfig;
import es.caib.emiserv.logic.base.helper.AuthenticationHelper;
import es.caib.emiserv.logic.base.service.PermissionEvaluatorService;
import es.caib.emiserv.logic.helper.PermissionHelper;
import es.caib.emiserv.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.emiserv.logic.intf.base.model.ResourceArtifactType;
import es.caib.emiserv.logic.intf.base.permission.ResourcePermissions;
import es.caib.emiserv.logic.intf.base.service.ResourceApiService;
import es.caib.emiserv.logic.intf.exception.ExportacioMassaGranException;
import es.caib.emiserv.logic.intf.exception.OrdenacioNoValidaException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import es.caib.emiserv.logic.intf.service.ServeiResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprova l'autorització dels endpoints del recurs {@code servei} a través del camí real de
 * seguretat de mètodes: MethodSecurityConfig (pointcut sobre els BaseController) +
 * hasPermission resolt pel bean {@code permissionEvaluatorService} (PermissionEvaluatorService i
 * PermissionHelper reals, que llegeixen les restriccions de ServeiResource). El servei del recurs
 * és un mock, de manera que una crida que arriba al servei vol dir que l'autorització l'ha deixada
 * passar. També comprova el mapatge de les excepcions de negoci a ResourceGlobalExceptionHandler.
 */
@WebMvcTest
class ServeiApiControllerSecurityTest {

	@SpringBootConfiguration
	@Import({
			ServeiApiController.class,
			ResourceGlobalExceptionHandler.class,
			MethodSecurityConfig.class,
			HalFormsConfig.class,
			HalFormsTemplateBuilderConfig.class,
			ResourceServiceLocator.class,
			PermissionHelper.class,
			AuthenticationHelper.class })
	static class TestApp {
		@Bean
		public PermissionEvaluatorService permissionEvaluatorService() {
			return new PermissionEvaluatorService();
		}
		@Bean
		public StaticMessageSource messageSource() {
			StaticMessageSource ms = new StaticMessageSource();
			ms.addMessage("error.exportacio.massa.gran", new Locale("ca"), "L''exportació té {0} files i supera el màxim permès ({1})");
			// Com base-boot-messages: els missatges només existeixen en català i castellà
			ms.addMessage("es.caib.emiserv.error.handling.MethodArgumentNotValidException.message", new Locale("ca"), "S''han produït errors de validació. Revisi el camp {0} per a més detalls.");
			return ms;
		}
		@Bean
		public LocaleResolver localeResolver() {
			// El mateix resolutor d'idioma que l'aplicació (WebMvcConfig)
			return new WebMvcConfig().localeResolver();
		}
		@Bean
		public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
			BaseWebSecurityConfig.configureApi(http, new LoginUrlAuthenticationEntryPoint("/login"), "EMS_ADMIN,EMS_RESP", null);
			http.authorizeRequests().anyRequest().authenticated();
			return http.build();
		}
	}

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ServeiResourceService serveiResourceService;
	@MockBean
	private ResourceApiService resourceApiService;

	@BeforeEach
	void setUp() {
		when(serveiResourceService.findPage(any(), any(), any(), any(), any(Pageable.class))).
				thenReturn(new PageImpl<>(Collections.emptyList(), Pageable.ofSize(10), 0));
		when(serveiResourceService.artifactFindAll(any())).thenReturn(Collections.emptyList());
		when(serveiResourceService.artifactGetOne(any(ResourceArtifactType.class), anyString())).
				thenAnswer(inv -> new es.caib.emiserv.logic.intf.base.model.ResourceArtifact(
						inv.getArgument(0),
						inv.getArgument(1),
						true,
						null));
		when(resourceApiService.permissionsCurrentUser(any(), any())).thenReturn(ResourcePermissions.readOnly());
	}

	@Test
	@WithMockUser(username = "resp", authorities = "EMS_RESP")
	void respPotLlistar() throws Exception {
		mockMvc.perform(get("/api/servei").accept(MediaTypes.HAL_FORMS_JSON)).
				andExpect(status().isOk());
		verify(serveiResourceService).findPage(any(), any(), any(), any(), any(Pageable.class));
	}

	@Test
	@WithMockUser(username = "resp", authorities = "EMS_RESP")
	void respNoPotActivar() throws Exception {
		mockMvc.perform(post("/api/servei/1/artifacts/action/" + ServeiResource.ACTION_ACTIVAR_CODE).
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).content("{}")).
				andExpect(status().isForbidden());
		verify(serveiResourceService, never()).artifactActionExec(any(), any(), any());
	}

	@Test
	@WithMockUser(username = "resp", authorities = "EMS_RESP")
	void respNoPotExportar() throws Exception {
		mockMvc.perform(post("/api/servei/artifacts/report/" + ServeiResource.REPORT_EXPORTAR_CODE).
						param("fileType", "CSV").
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).content("{}")).
				andExpect(status().isForbidden());
		verify(serveiResourceService, never()).artifactReportGenerateData(any(), any(), any());
	}

	@Test
	@WithMockUser(username = "admin", authorities = "EMS_ADMIN")
	void adminPotActivar() throws Exception {
		mockMvc.perform(post("/api/servei/1/artifacts/action/" + ServeiResource.ACTION_ACTIVAR_CODE).
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).content("{}")).
				andExpect(status().is2xxSuccessful());
		verify(serveiResourceService).artifactActionExec(eq(1L), eq(ServeiResource.ACTION_ACTIVAR_CODE), any());
	}

	@Test
	@WithMockUser(username = "admin", authorities = "EMS_ADMIN")
	void esborratGenericNoPermes() throws Exception {
		mockMvc.perform(delete("/api/servei/1").with(csrf())).
				andExpect(status().isForbidden());
		verify(serveiResourceService, never()).delete(any(), any());
	}

	@Test
	@WithMockUser(username = "altre", authorities = "ALTRE_ROL")
	void senseRolEmiservNoPotLlistar() throws Exception {
		mockMvc.perform(get("/api/servei").accept(MediaTypes.HAL_FORMS_JSON)).
				andExpect(status().isForbidden());
		verify(serveiResourceService, never()).findPage(any(), any(), any(), any(), any(Pageable.class));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "EMS_ADMIN")
	void errorNegociEsborrarEs409AmbMissatge() throws Exception {
		when(serveiResourceService.artifactActionExec(eq(2L), eq(ServeiResource.ACTION_ESBORRAR_CODE), any())).
				thenThrow(new ValidationException("Aquest servei te peticions de backoffice associades i no es pot esborrar"));
		mockMvc.perform(post("/api/servei/2/artifacts/action/" + ServeiResource.ACTION_ESBORRAR_CODE).
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).content("{}")).
				andExpect(status().isConflict()).
				andExpect(jsonPath("$.message").value("Aquest servei te peticions de backoffice associades i no es pot esborrar"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "EMS_ADMIN")
	void exportacioMassaGranEs422AmbMissatgeTraduit() throws Exception {
		when(serveiResourceService.artifactReportGenerateData(any(), eq(ServeiResource.REPORT_EXPORTAR_CODE), any())).
				thenThrow(new ExportacioMassaGranException(12345, 10000));
		mockMvc.perform(post("/api/servei/artifacts/report/" + ServeiResource.REPORT_EXPORTAR_CODE).
						param("fileType", "XLSX").
						header("Accept-Language", "ca").
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).content("{}")).
				andExpect(status().isUnprocessableEntity()).
				andExpect(jsonPath("$.message").value("L'exportació té 12.345 files i supera el màxim permès (10.000)"));
	}


	@Test
	@WithMockUser(username = "resp", authorities = "EMS_RESP")
	void respServeiAlieEs404() throws Exception {
		// El servei del recurs filtra per ACL (ADMINISTRATION) i no troba els serveis aliens
		when(serveiResourceService.getOne(eq(2L), any())).
				thenThrow(new ResourceNotFoundException(ServeiResource.class, "2"));
		mockMvc.perform(get("/api/servei/2").accept(MediaTypes.HAL_FORMS_JSON)).
				andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin", authorities = "EMS_ADMIN")
	void ordenacioNoValidaEs400() throws Exception {
		when(serveiResourceService.artifactReportGenerateData(any(), eq(ServeiResource.REPORT_EXPORTAR_CODE), any())).
				thenThrow(new OrdenacioNoValidaException("diagnostic", Arrays.asList("codi", "nom")));
		mockMvc.perform(post("/api/servei/artifacts/report/" + ServeiResource.REPORT_EXPORTAR_CODE).
						param("fileType", "CSV").
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).content("{\"sort\":[\"diagnostic\"]}")).
				andExpect(status().isBadRequest()).
				andExpect(jsonPath("$.message").value("No es pot ordenar pel camp 'diagnostic' (camps permesos: codi, nom)"));
	}

	@Test
	@WithMockUser(username = "admin", authorities = "EMS_ADMIN")
	void errorNoPrevistNoMostraElDetall() throws Exception {
		when(serveiResourceService.artifactActionExec(eq(3L), eq(ServeiResource.ACTION_ACTIVAR_CODE), any())).
				thenThrow(new IllegalStateException("ORA-00942: table or view does not exist"));
		mockMvc.perform(post("/api/servei/3/artifacts/action/" + ServeiResource.ACTION_ACTIVAR_CODE).
						param("trace", "true").
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).content("{}")).
				andExpect(status().isInternalServerError()).
				andExpect(jsonPath("$.message").value("Internal Server Error")).
				andExpect(jsonPath("$.stackTrace").doesNotExist());
	}

	@Test
	@WithMockUser(username = "admin", authorities = "EMS_ADMIN")
	void accioDUnAltreLlocEs403() throws Exception {
		mockMvc.perform(post("/api/servei/1/artifacts/action/" + ServeiResource.ACTION_ACTIVAR_CODE).
						header("Origin", "https://evil.example").
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).content("{}")).
				andExpect(status().isForbidden());
		verify(serveiResourceService, never()).artifactActionExec(any(), any(), any());
		mockMvc.perform(post("/api/servei/1/artifacts/action/" + ServeiResource.ACTION_ACTIVAR_CODE).
						header("Origin", "http://localhost").
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).content("{}")).
				andExpect(status().is2xxSuccessful());
	}

	@Test
	@WithMockUser(username = "admin", authorities = "EMS_ADMIN")
	void errorValidacioEs422JsonEnQualsevolIdioma() throws Exception {
		// Servei BACKOFFICE sense tipus de processament asíncron (ServeiValid). Amb un idioma del
		// navegador sense missatges (en) el gestor d'errors no ha de fallar: si falla, Spring envia
		// l'error al contenidor, que el reenvia a la JSP d'error i un PUT acaba en 405.
		mockMvc.perform(put("/api/servei/900002").
						header("Accept-Language", "en-US,en;q=0.9").
						with(csrf()).
						contentType(MediaType.APPLICATION_JSON).
						content("{\"codi\":\"SVDCCAACPCWS01\",\"nom\":\"Prova\",\"tipus\":\"BACKOFFICE\"," +
								"\"backofficeClass\":\"es.caib.emiserv.backoffice.CaibBackoffice\"," +
								"\"backofficeCaibUrl\":\"http://localhost/ws\",\"backofficeCaibAutenticacio\":\"NINGUNA\"}")).
				andExpect(status().isUnprocessableEntity()).
				andExpect(content().contentTypeCompatibleWith("application/problem+json")).
				andExpect(jsonPath("$.message").value("S'han produït errors de validació. Revisi el camp validationErrors per a més detalls.")).
				andExpect(jsonPath("$.validationErrors[0].field").value("backofficeCaibAsyncTipus"));
		verify(serveiResourceService, never()).update(any(), any(), any());
	}

}
