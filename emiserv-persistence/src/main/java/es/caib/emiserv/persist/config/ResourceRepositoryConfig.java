/**
 * 
 */
package es.caib.emiserv.persist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import es.caib.emiserv.persist.base.repository.BaseRepositoryImpl;

/**
 * Configuració dels repositoris JPA dels recursos de l'API REST (base-boot).
 * 
 * Els repositoris del paquet es.caib.emiserv.persist.resourcerepository
 * estenen BaseRepository i necessiten BaseRepositoryImpl com a classe base
 * (implementa refresh, detach i merge). Reutilitza l'EntityManagerFactory i el
 * TransactionManager existents.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@EnableJpaRepositories(
		basePackages = "es.caib.emiserv.persist.resourcerepository",
		repositoryBaseClass = BaseRepositoryImpl.class)
public class ResourceRepositoryConfig {

}
