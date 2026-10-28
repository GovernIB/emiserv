/**
 * 
 */
package es.caib.emiserv.ejb.base;

import es.caib.emiserv.ejb.AbstractService;
import es.caib.emiserv.logic.intf.base.model.Resource;
import es.caib.emiserv.logic.intf.base.permission.ResourcePermissions;
import es.caib.emiserv.logic.intf.base.service.ResourceApiService;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;

/**
 * Implementació de ResourceApiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless(name = "ResourceApiService")
@PermitAll
public class ResourceApiServiceEjb extends AbstractService<ResourceApiService> implements ResourceApiService {

	@Override
	public void resourceRegister(Class<? extends Resource<?>> resourceClass) {
		getDelegateService().resourceRegister(resourceClass);
	}

	@Override
	public List<Class<? extends Resource<?>>> resourceFindAllowed() {
		return getDelegateService().resourceFindAllowed();
	}

	@Override
	public ResourcePermissions permissionsCurrentUser(Class<?> resourceClass, Serializable resourceId) {
		return getDelegateService().permissionsCurrentUser(resourceClass, resourceId);
	}

}
