/**
 * 
 */
package es.caib.emiserv.core.helper;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * Helper per a les comprovacions de seguretat de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class SecurityHelper {

	public boolean hasRole(String role) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	    return isRolePresent(authorities, role);
	}

	public String getUserName() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return null;
		}
		return auth.getName();
	}



	private boolean isRolePresent(
			Collection<? extends GrantedAuthority> authorities,
			String role) {
		boolean isRolePresent = false;
		for (GrantedAuthority grantedAuthority : authorities) {
			isRolePresent = grantedAuthority.getAuthority().equals(role);
			if (isRolePresent) break;
		}
		return isRolePresent;
	}

}
