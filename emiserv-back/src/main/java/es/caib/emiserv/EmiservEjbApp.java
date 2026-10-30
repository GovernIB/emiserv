/**
 * 
 */
package es.caib.emiserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Aplicació Spring Boot de EMISERV per a ser executada des de JBoss: accedeix als serveis per EJB.
 * Per a executar-la des de l'IDE es fa servir {@link es.caib.emiserv.boot.EmiservBackBootApp}.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ConditionalOnWarDeployment
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class, 
		DataSourceTransactionManagerAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		TransactionAutoConfiguration.class,
		FreeMarkerAutoConfiguration.class,
		WebSocketServletAutoConfiguration.class
})
@ComponentScan(
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = {
						"es\\.caib\\.emiserv\\.logic\\..*",
						"es\\.caib\\.emiserv\\.persist\\..*",
						"es\\.caib\\.emiserv\\.ejb\\..*",
						"es\\.caib\\.emiserv\\.api\\..*",
						"es\\.caib\\.emiserv\\.boot\\..*"}))
public class EmiservEjbApp extends EmiservApp {

	public static void main(String[] args) {
		SpringApplication.run(EmiservEjbApp.class, args);
	}

}
