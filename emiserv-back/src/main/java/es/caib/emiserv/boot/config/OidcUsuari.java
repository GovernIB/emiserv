package es.caib.emiserv.boot.config;

import es.caib.emiserv.logic.intf.keycloak.KeycloakUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

/**
 * Usuari autenticat amb OpenID Connect a l'execució des de l'IDE. Implementa
 * {@link KeycloakUserDetails} perquè {@code KeycloakHelper} en tregui el nom i el correu igual que
 * de l'usuari de JBoss.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OidcUsuari extends DefaultOidcUser implements KeycloakUserDetails {

	public OidcUsuari(
			Collection<? extends GrantedAuthority> authorities,
			OidcIdToken idToken,
			OidcUserInfo userInfo,
			String nameAttributeKey) {
		super(authorities, idToken, userInfo, nameAttributeKey);
	}

	@Override
	public String getGivenName() {
		return super.getGivenName();
	}
	@Override
	public String getFamilyName() {
		return super.getFamilyName();
	}
	@Override
	public String getFullName() {
		return super.getFullName();
	}
	@Override
	public String getEmail() {
		return super.getEmail();
	}
	@Override
	public Object getKeycloakPrincipal() {
		// No hi ha adaptador Keycloak: la informació de l'usuari surt dels claims OIDC
		return null;
	}

	private static final long serialVersionUID = 1L;

}
