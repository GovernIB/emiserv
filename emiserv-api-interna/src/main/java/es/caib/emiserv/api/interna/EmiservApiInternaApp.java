/**
 * 
 */
package es.caib.emiserv.api.interna;

import net.rakugakibox.spring.boot.orika.OrikaAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


/**
 * Aplicació Spring Boot de EMISERV per a ser executada des de JBoss.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		TransactionAutoConfiguration.class,
		FreeMarkerAutoConfiguration.class,
		WebSocketServletAutoConfiguration.class,
		SecurityAutoConfiguration.class,
		OrikaAutoConfiguration.class,
//		SpringDocWebMvcConfiguration.class,
//		MultipleOpenApiSupportConfiguration.class,
		SpringDataWebAutoConfiguration.class
})
@ComponentScan(
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = {
						"es\\.caib\\.emiserv\\.logic\\..*",
						"es\\.caib\\.emiserv\\.persist\\..*",
						"es\\.caib\\.emiserv\\.ejb\\..*",
						"es\\.caib\\.emiserv\\.backoffice\\..*",
						"es\\.caib\\.emiserv\\.back\\..*",
						"es\\.caib\\.emiserv\\.war\\..*"}))
public class EmiservApiInternaApp extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(EmiservApiInternaApp.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(EmiservApiInternaApp.class, args);
	}

}
