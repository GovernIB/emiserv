/**
 *
 */
package es.caib.emiserv.boot.config;

import com.nimbusds.jwt.JWTParser;
import es.caib.emiserv.back.base.config.ApiOrigenFilter;
import es.caib.emiserv.back.base.config.BaseWebSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configuració de seguretat de l'execució des de l'IDE ({@link es.caib.emiserv.boot.EmiservBackBootApp}).
 * <p>
 * L'usuari s'autentica amb OAuth2/OpenID Connect contra Keycloak (Spring Security OAuth2 Client,
 * registre {@value #REGISTRATION_ID}) i la sessió és la del servidor, com a JBoss. Els rols són els
 * de realm del token d'accés que també són a {@code es.caib.emiserv.security.mappableRoles}, igual
 * que a JBoss ({@code war.config.SecurityConfig}).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@ConditionalOnNotWarDeployment
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/** Registre del client OAuth2 (spring.security.oauth2.client.registration.keycloak.*). */
	public static final String REGISTRATION_ID = "keycloak";

	@Value("${es.caib.emiserv.security.mappableRoles:EMS_ADMIN,EMS_RESP}")
	private String mappableRoles;
	@Value("${" + ApiOrigenFilter.PROPIETAT_ORIGENS + ":}")
	private String origensPermesos;
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

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
		// Tanca la sessió a Keycloak i torna a l'aplicació
		OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
				clientRegistrationRepository);
		logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
		http.logout().
		logoutRequestMatcher(new AntPathRequestMatcher("/logout")).
		invalidateHttpSession(true).
		clearAuthentication(true).
		logoutSuccessHandler(logoutSuccessHandler).
		permitAll(false);
		http.oauth2Login().
		userInfoEndpoint().oidcUserService(oidcUserService());
		// API REST: requereix algun rol d'Emiserv (403 JSON altrament) i sense autenticar respon
		// 401 JSON en lloc de redirigir al login de Keycloak.
		BaseWebSecurityConfig.configureApi(
				http,
				new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/" + REGISTRATION_ID),
				mappableRoles,
				origensPermesos);
		http.authorizeRequests().
		antMatchers(AUTH_WHITELIST).permitAll().
		anyRequest().authenticated();
		http.cors();
		http.csrf().disable();
		http.headers().frameOptions().disable();
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	/**
	 * Usuari OpenID Connect amb els rols d'EMISERV del token d'accés (realm_access.roles).
	 */
	private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		OidcUserService delegate = new OidcUserService();
		Set<String> rolsAplicacio = parseRols(mappableRoles);
		return userRequest -> {
			OidcUser oidcUser = delegate.loadUser(userRequest);
			Set<GrantedAuthority> authorities = getRolsRealm(userRequest.getAccessToken().getTokenValue()).stream().
					filter(rolsAplicacio::contains).
					map(SimpleGrantedAuthority::new).
					collect(Collectors.toCollection(HashSet::new));
			log.debug("Rols de l'usuari {}: {}", oidcUser.getName(), authorities);
			String userNameAttribute = userRequest.getClientRegistration().getProviderDetails().
					getUserInfoEndpoint().getUserNameAttributeName();
			return new OidcUsuari(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), userNameAttribute);
		};
	}

	@SuppressWarnings("unchecked")
	static Set<String> getRolsRealm(String accessToken) {
		try {
			Map<String, Object> realmAccess = (Map<String, Object>)JWTParser.parse(accessToken).getJWTClaimsSet().getClaim("realm_access");
			Object roles = realmAccess != null ? realmAccess.get("roles") : null;
			if (roles instanceof Collection) {
				return ((Collection<Object>)roles).stream().map(String::valueOf).collect(Collectors.toSet());
			}
		} catch (ParseException ex) {
			log.warn("No s'han pogut llegir els rols del token d'accés", ex);
		}
		return new HashSet<>();
	}

	private static Set<String> parseRols(String rols) {
		return List.of(rols.split(",")).stream().
				map(String::trim).
				filter(r -> !r.isEmpty()).
				collect(Collectors.toSet());
	}

}
