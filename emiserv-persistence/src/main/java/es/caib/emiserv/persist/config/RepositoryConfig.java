/**
 * 
 */
package es.caib.emiserv.persist.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuració dels repositoris JPA d'EMISERV.
 * 
 * Registra tots els repositoris del paquet es.caib.emiserv.persist excepte els
 * dels recursos (es.caib.emiserv.persist.resourcerepository), que es registren
 * a {@link ResourceRepositoryConfig} amb una classe base diferent. Els
 * repositoris existents es comporten igual que abans (SimpleJpaRepository).
 * 
 * Reutilitza l'EntityManagerFactory i el TransactionManager existents
 * (beans entityManagerFactory i transactionManager).
 * 
 * @author Limit Tecnologies
 */
@Configuration
@EnableJpaRepositories(
		basePackages = "es.caib.emiserv.persist",
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = "es\\.caib\\.emiserv\\.persist\\.resourcerepository\\..*"))
public class RepositoryConfig {

}
