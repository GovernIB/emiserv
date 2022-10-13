/**
 * 
 */
package es.caib.emiserv.api.externa.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken.Access;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuració de seguretat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${es.caib.emiserv.security.mappableRoles:EMS_ADMIN,EMS_REPORT}")
	private String mappableRoles;
	@Value("${es.caib.emiserv.security.useResourceRoleMappings:false}")
	private boolean useResourceRoleMappings;

	private static final String ROLE_PREFIX = "";

	private static final String[] AUTH_WHITELIST = {
			"/swagger-resources/**",
			"/swagger-ui/**",
			"/rest",
			"/api-docs",
			"/api-docs/**",
			"/opendata",
			"/v2/opendata"
//			"/webjars/**"
	};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authenticationProvider(preauthAuthProvider()).
		jee().j2eePreAuthenticatedProcessingFilter(preAuthenticatedProcessingFilter());
		http.logout().
		addLogoutHandler(getLogoutHandler()).
		logoutRequestMatcher(new AntPathRequestMatcher("/logout")).
		invalidateHttpSession(true).
		logoutSuccessUrl("/").
		permitAll(false);
		http.authorizeRequests().
		//antMatchers("/test").hasRole("tothom").
		//antMatchers("/api/**/*").permitAll().
//		antMatchers("/scspRouting/**/*").permitAll().
		antMatchers(AUTH_WHITELIST).permitAll().
		anyRequest().authenticated();
		http.cors();
		http.csrf().disable();
		http.headers().frameOptions().disable();
	}

	@Bean
	public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
		PreAuthenticatedAuthenticationProvider preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
		preauthAuthProvider.setPreAuthenticatedUserDetailsService(
				preAuthenticatedGrantedAuthoritiesUserDetailsService());
		return preauthAuthProvider;
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	@Bean
	public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService() {
		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService() {
			protected UserDetails createUserDetails(
					Authentication token,
					Collection<? extends GrantedAuthority> authorities) {
				if (token.getDetails() instanceof KeycloakWebAuthenticationDetails) {
					KeycloakWebAuthenticationDetails keycloakWebAuthenticationDetails = (KeycloakWebAuthenticationDetails)token.getDetails();
					return new PreauthKeycloakUserDetails(
							token.getName(),
							"N/A",
							true,
							true,
							true,
							true,
							authorities,
							keycloakWebAuthenticationDetails.getKeycloakPrincipal());
				} else {
					return new User(token.getName(), "N/A", true, true, true, true, authorities);
				}
			}
		};
	}

	@Bean
	public J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {
		J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter = new J2eePreAuthenticatedProcessingFilter();
		preAuthenticatedProcessingFilter.setAuthenticationDetailsSource(authenticationDetailsSource());
		preAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());
		preAuthenticatedProcessingFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
		return preAuthenticatedProcessingFilter;
	}

	@Bean
	public AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource() {
		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				Collection<String> j2eeUserRoles = getUserRoles(context);
				logger.debug("Roles from ServletRequest for " + context.getUserPrincipal().getName() + ": " + j2eeUserRoles);
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
					KeycloakPrincipal<?> keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
					Set<String> roles = new HashSet<>();
					roles.addAll(j2eeUserRoles);
					Access realmAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess();
					if (realmAccess != null && realmAccess.getRoles() != null) {
						logger.debug("Keycloak token realm roles: " + realmAccess.getRoles());
						roles.addAll(realmAccess.getRoles());
					}
					if (useResourceRoleMappings) {
						Access resourceAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getResourceAccess(
								keycloakPrincipal.getKeycloakSecurityContext().getToken().getIssuedFor());
						if (resourceAccess != null && resourceAccess.getRoles() != null) {
							logger.debug("Keycloak token resource roles: " + resourceAccess.getRoles());
							roles.addAll(resourceAccess.getRoles());
						}
					}
					logger.debug("Creating WebAuthenticationDetails for " + keycloakPrincipal.getName() + " with roles " + roles);
					result = new KeycloakWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
							keycloakPrincipal);
				} else {
					logger.debug("Creating WebAuthenticationDetails for " + context.getUserPrincipal().getName() + " with roles " + j2eeUserRoles);
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(j2eeUserRoles));
				}
				return result;
			}
		};
		SimpleMappableAttributesRetriever mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
		mappableAttributesRetriever.setMappableAttributes(
				new HashSet<String>(Arrays.asList(mappableRoles.split(","))));
		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
		SimpleAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}

	@Bean
	public LogoutHandler getLogoutHandler() {
		return new LogoutHandler() {
			@Override
			public void logout(
					HttpServletRequest request,
					HttpServletResponse response,
					Authentication authentication) {
				try {
					request.logout();
				} catch (ServletException ex) {
					log.error("Error al sortir de l'aplicació", ex);
				}
			}
		};
	}

	@SuppressWarnings("serial")
	public static class KeycloakWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
		private KeycloakPrincipal<?> keycloakPrincipal;
		public KeycloakWebAuthenticationDetails(
				HttpServletRequest request,
				Collection<? extends GrantedAuthority> authorities,
				KeycloakPrincipal<?> keycloakPrincipal) {
			super(request, authorities);
			this.keycloakPrincipal = keycloakPrincipal;
		}
		public KeycloakPrincipal<?> getKeycloakPrincipal() {
			return keycloakPrincipal;
		}
	}
	@SuppressWarnings("serial")
	public static class PreauthKeycloakUserDetails extends User implements es.caib.emiserv.logic.intf.keycloak.KeycloakUserDetails {
		private KeycloakPrincipal<?> keycloakPrincipal;
		public PreauthKeycloakUserDetails(
				String username,
				String password,
				boolean enabled,
				boolean accountNonExpired,
				boolean credentialsNonExpired,
				boolean accountNonLocked,
				Collection<? extends GrantedAuthority> authorities,
				KeycloakPrincipal<?> keycloakPrincipal) {
			super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			this.keycloakPrincipal = keycloakPrincipal;
		}
		public KeycloakPrincipal<?> getKeycloakPrincipal() {
			return keycloakPrincipal;
		}
		public String getGivenName() {
			if (keycloakPrincipal instanceof KeycloakPrincipal) {
				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getGivenName();
			} else {
				return null;
			}
		}
		public String getFamilyName() {
			if (keycloakPrincipal instanceof KeycloakPrincipal) {
				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getFamilyName();
			} else {
				return null;
			}
		}
		public String getFullName() {
			if (keycloakPrincipal instanceof KeycloakPrincipal) {
				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getName();
			} else {
				return null;
			}
		}
		public String getEmail() {
			if (keycloakPrincipal instanceof KeycloakPrincipal) {
				return ((KeycloakPrincipal<?>)keycloakPrincipal).getKeycloakSecurityContext().getToken().getEmail();
			} else {
				return null;
			}
		}
	}

}
