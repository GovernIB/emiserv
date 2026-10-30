/**
 * 
 */
package es.caib.emiserv.config;

import es.caib.emiserv.back.base.config.ApiOrigenFilter;
import es.caib.emiserv.back.base.config.BaseWebSecurityConfig;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author limit
 *
 */
@KeycloakConfiguration
//@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

	@Value("${es.caib.emiserv.security.mappableRoles:EMS_ADMIN,EMS_RESP}")
	private String mappableRoles;
	@Value("${" + ApiOrigenFilter.PROPIETAT_ORIGENS + ":}")
	private String origensPermesos;

	private static final String ROLE_PREFIX = "";

	private static final String[] AUTH_WHITELIST = {
			"/scspRouting/**/*",
			"/swagger-resources/**",
			"/swagger-ui/**",
			"/api-docs",
			"/webjars/**"
	};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		http.logout().
		//addLogoutHandler(keycloakLogoutHandler()).
		logoutRequestMatcher(new AntPathRequestMatcher("/logout")).
		invalidateHttpSession(true).
		logoutSuccessUrl("/").
		permitAll(false);
		// API REST: requereix algun rol d'Emiserv (403 JSON altrament) i sense autenticar respon
		// 401 JSON en lloc de redirigir al login de Keycloak.
		BaseWebSecurityConfig.configureApi(http, authenticationEntryPoint(), mappableRoles, origensPermesos);
		http.authorizeRequests().
		//antMatchers("/test").hasRole("tothom").
		antMatchers(AUTH_WHITELIST).permitAll().
		anyRequest().authenticated();
		http.cors();
		http.csrf().disable();
		http.headers().frameOptions().disable();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
		authorityMapper.setPrefix(ROLE_PREFIX);
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(authorityMapper);
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

}
