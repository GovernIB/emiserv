/**
 * 
 */
package es.caib.emiserv.logic.helper;

import java.io.Serializable;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import es.caib.emiserv.logic.base.helper.BasePermissionHelper;
import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.model.ResourceArtifactType;
import es.caib.emiserv.logic.intf.base.service.PermissionEvaluatorService;

/**
 * Comprovació de permisos dels recursos de l'API REST (base-boot).
 * 
 * De moment no hi ha cap restricció d'accés de tipus CUSTOM i, per tant, es
 * deneguen. Les restriccions per rol es resolen a BasePermissionHelper.
 * 
 * @author Limit Tecnologies
 */
@Component
public class PermissionHelper extends BasePermissionHelper {

	@Override
	protected boolean checkCustomResourceAccessConstraint(
			Authentication auth,
			Serializable resourceId,
			Class<?> resourceClass,
			ResourceAccessConstraint resourceAccessConstraint,
			PermissionEvaluatorService.RestApiOperation restapiOperation,
			BasePermission[] permissions) {
		return false;
	}

	@Override
	protected boolean checkCustomResourceArtifactAccessConstraint(
			Authentication auth,
			Class<?> resourceClass,
			ResourceArtifactType type,
			String code,
			ResourceAccessConstraint resourceAccessConstraint) {
		return false;
	}

}
