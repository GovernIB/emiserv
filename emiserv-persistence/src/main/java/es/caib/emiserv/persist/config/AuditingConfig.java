/**
 * 
 */
package es.caib.emiserv.persist.config;

import lombok.Setter;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuració per a les entitats de base de dades auditables.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig implements EnvironmentAware {

	@Setter
	private Environment environment;

//	@Value("${es.caib.emiserv.default.auditor:#{null}}")
//	private String defaultAuditor;

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAware<String>() {
			@Override
			public java.util.Optional<String> getCurrentAuditor() {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null && authentication.isAuthenticated()) {
					return Optional.of(authentication.getName());
				}
				return Optional.ofNullable(environment.getProperty("es.caib.emiserv.default.auditor", "anonymous"));
//						(defaultAuditor != null) ? defaultAuditor : "anonymous");
			}
		};
	}

}
