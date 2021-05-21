/**
 * 
 */
package es.caib.emiserv;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Aplicació Spring Boot de EMISERV.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication
public class EmiservApp {

	public static void main(String[] args) {
		new SpringApplicationBuilder(EmiservApp.class).run();
	}

}
