package es.caib.emiserv.back.base;

import es.caib.emiserv.back.base.config.BaseWebSecurityConfig;
import es.caib.emiserv.back.base.config.HalFormsConfig;
import es.caib.emiserv.back.base.config.HalFormsTemplateBuilderConfig;
import es.caib.emiserv.back.base.controller.RestApiController;
import es.caib.emiserv.back.base.util.ResourceServiceLocator;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Configuració mínima per als tests de la capa REST: l'índex de l'API amb la configuració
 * HAL-FORMS de base-boot. Reprodueix la seguretat de les
 * aplicacions (tot autenticat i redirecció al login com a punt d'entrada per defecte)
 * aplicant-hi el punt d'entrada de l'API de {@link BaseWebSecurityConfig}.
 *
 * @author Limit Tecnologies
 */
@SpringBootConfiguration
@Import({
		RestApiController.class,
		HalFormsConfig.class,
		HalFormsTemplateBuilderConfig.class,
		ResourceServiceLocator.class })
public class ApiTestApp {

	public static final String LOGIN_URL = "/login";
	public static final String MAPPABLE_ROLES = "EMS_ADMIN,EMS_RESP";
	public static final String DEV_ORIGIN = "http://localhost:5173";

	@Bean
	public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
		BaseWebSecurityConfig.configureApi(http, new LoginUrlAuthenticationEntryPoint(LOGIN_URL), MAPPABLE_ROLES, DEV_ORIGIN);
		http.authorizeRequests().anyRequest().authenticated();
		return http.build();
	}

}
