package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import es.caib.emiserv.logic.intf.dto.PrincipalTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Permís d'un usuari o d'un rol sobre un servei per a la interfície REACT (pestanya «Permisos»).
 * <p/>
 * Són les entrades ACL del servei agrupades per principal, com a la JSP (ServeiPermisController).
 * L'identificador és el servei i el principal (vegeu ServeiPermisResourceServiceImpl.getId): els
 * identificadors de les entrades ACL canvien quan es modifiquen els permisos. Les operacions
 * deleguen a ServeiService (permis*).
 * La graella filtra per servei amb {@code serveiId:<id>}.
 * <p/>
 * Permisos: només EMS_ADMIN, com a la JSP.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ServeiPermisResource.Fields.principalNom,
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		})
public class ServeiPermisResource extends BaseResource<String> {

	@NotNull
	private Long serveiId;
	@NotNull
	private PrincipalTipusEnumDto principalTipus;
	@NotEmpty
	@Size(max = 64)
	private String principalNom;
	/** Permís d'administració del servei (l'únic que es gestiona, com a la JSP). */
	private boolean administration;

	private static final long serialVersionUID = 1L;

}
