package es.caib.emiserv.back.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.emiserv.back.base.error.ErrorResponse;
import es.caib.emiserv.logic.intf.base.config.BaseBootConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

/**
 * Configuració de Spring Security per a l'API REST.
 * <p/>
 * Adaptació d'EMISERV: a diferència de base-boot, aquesta classe no defineix cap
 * {@code SecurityFilterChain} ni activa {@code @EnableWebSecurity}. Les aplicacions
 * (emiserv-back i emiserv-boot) ja tenen la seva pròpia configuració de seguretat basada
 * en {@code WebSecurityConfigurerAdapter} (que no pot conviure amb un
 * {@code SecurityFilterChain}) i l'API REST empra la mateixa sessió que la interfície JSP.
 * Per això aquí només hi ha les peces que aquestes configuracions han d'aplicar a les
 * peticions de l'API: el punt d'entrada que respon 401 amb un JSON en lloc de redirigir
 * al login.
 *
 * @author Limit Tecnologies
 */
public final class BaseWebSecurityConfig {

	/** Patró de les peticions de l'API REST. */
	public static final String API_PATTERN = BaseBootConfig.API_PATH + "/**";

	/**
	 * Retorna el matcher de les peticions de l'API REST.
	 *
	 * @return el matcher.
	 */
	public static RequestMatcher apiRequestMatcher() {
		return new AntPathRequestMatcher(API_PATTERN);
	}

	/**
	 * Retorna un punt d'entrada que, per a les peticions de l'API REST, respon amb un
	 * error 401 en format JSON i, per a la resta, delega en el punt d'entrada per defecte
	 * de l'aplicació (p. ex. la redirecció al login).
	 *
	 * @param defaultEntryPoint
	 *            el punt d'entrada per a les peticions que no són de l'API.
	 * @return el punt d'entrada.
	 */
	public static AuthenticationEntryPoint apiAuthenticationEntryPoint(AuthenticationEntryPoint defaultEntryPoint) {
		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
		entryPoints.put(apiRequestMatcher(), jsonUnauthorizedEntryPoint());
		DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
		entryPoint.setDefaultEntryPoint(defaultEntryPoint);
		return entryPoint;
	}

	/**
	 * Retorna un punt d'entrada que respon amb un error 401 en format JSON.
	 *
	 * @return el punt d'entrada.
	 */
	public static AuthenticationEntryPoint jsonUnauthorizedEntryPoint() {
		ObjectMapper objectMapper = new ObjectMapper();
		return (request, response, authException) -> {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			objectMapper.writeValue(
					response.getOutputStream(),
					new ErrorResponse(
							HttpStatus.UNAUTHORIZED.value(),
							HttpStatus.UNAUTHORIZED.getReasonPhrase()));
		};
	}

	private BaseWebSecurityConfig() {
	}

}
