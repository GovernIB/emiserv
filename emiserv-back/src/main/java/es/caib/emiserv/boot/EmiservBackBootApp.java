package es.caib.emiserv.boot;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Aplicació Spring Boot de EMISERV per a executar-la des de l'IDE (perfil Maven "boot"): els
 * serveis s'executen dins la mateixa aplicació en lloc d'accedir-hi per EJB, i l'autenticació la
 * fa l'adaptador Keycloak de Spring Security en lloc del de JBoss.
 * <p>
 * Les classes d'aquest paquet no s'inclouen al WAR (veure el maven-war-plugin d'emiserv-back).
 * A JBoss s'executa {@link es.caib.emiserv.EmiservEjbApp}.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication(scanBasePackages = "es.caib.emiserv")
// Paquet de les entitats JPA i dels repositoris: per defecte seria el d'aquesta classe
@AutoConfigurationPackage(basePackages = "es.caib.emiserv")
public class EmiservBackBootApp {

	public static void main(String[] args) {
		new SpringApplicationBuilder(EmiservBackBootApp.class).
				properties(propietatsPerDefecte()).
				run(args);
	}

	/**
	 * Propietats que només s'apliquen en aquesta execució. Són valors per defecte: les variables
	 * d'entorn, els arguments i els fitxers de propietats tenen preferència (p. ex.
	 * SPRING_LIQUIBASE_ENABLED=false).
	 */
	static Map<String, Object> propietatsPerDefecte() {
		Map<String, Object> propietats = new HashMap<>();
		// A JBoss la BBDD s'actualitza amb els scripts de versions/: Liquibase només s'executa aquí
		propietats.put("spring.liquibase.enabled", "true");
		// La galeta de sessió no viatja a les peticions d'altres llocs (protecció CSRF de l'API, a més
		// de la comprovació d'Origin/Referer de BaseWebSecurityConfig). A JBoss es configura al servidor.
		propietats.put("server.servlet.session.cookie.same-site", "lax");
		// Origen del servidor de desenvolupament de Vite (npm run dev), permès a l'API i al retorn del login
		propietats.put("es.caib.emiserv.front.dev.origins", "http://localhost:5173");
		return propietats;
	}

}
