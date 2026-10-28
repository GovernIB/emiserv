/**
 * 
 */
package es.caib.emiserv.ejb.base;

import es.caib.emiserv.ejb.AbstractService;
import es.caib.emiserv.logic.intf.base.service.PermissionEvaluatorService;
import org.springframework.security.core.Authentication;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import java.io.Serializable;

/**
 * Implementació de PermissionEvaluatorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless(name = "PermissionEvaluatorService")
@PermitAll
public class PermissionEvaluatorServiceEjb extends AbstractService<PermissionEvaluatorService> implements PermissionEvaluatorService {

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		return getDelegateService().hasPermission(authentication, targetDomainObject, permission);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		return getDelegateService().hasPermission(authentication, targetId, targetType, permission);
	}

}
