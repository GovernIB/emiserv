package es.caib.emiserv.boot.config;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprova la lectura dels rols de realm del token d'accés de Keycloak a l'execució des de l'IDE.
 */
public class SecurityConfigTest {

	@Test
	public void llegeixElsRolsDeRealm() {
		String token = new PlainJWT(new JWTClaimsSet.Builder().
				claim("preferred_username", "ems_admin").
				claim("realm_access", Map.of("roles", Arrays.asList("EMS_ADMIN", "offline_access"))).
				build()).serialize();
		assertEquals(Set.of("EMS_ADMIN", "offline_access"), SecurityConfig.getRolsRealm(token));
	}

	@Test
	public void senseRolsDeRealmNoEnTorna() {
		String token = new PlainJWT(new JWTClaimsSet.Builder().claim("sub", "x").build()).serialize();
		assertTrue(SecurityConfig.getRolsRealm(token).isEmpty());
		assertTrue(SecurityConfig.getRolsRealm("no-es-un-jwt").isEmpty());
	}

}
