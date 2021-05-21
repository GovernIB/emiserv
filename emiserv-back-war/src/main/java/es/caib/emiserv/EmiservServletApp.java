/**
 * 
 */
package es.caib.emiserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicació Spring Boot de EMISERV per a ser executada des de Tomcat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication
public class EmiservServletApp extends EmiservApp {

	public static void main(String[] args) {
		SpringApplication.run(EmiservServletApp.class, args);
	}

}
