package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

/**
 * Autoritat de certificació dels certificats de les aplicacions.
 * <p/>
 * Recurs del manteniment de configuració de la interfície REACT (autoritatcaList.jsp / AutoritatCertificacioController). No té entitat pròpia:
 * les operacions deleguen als mateixos mètodes de servei que la JSP. Només EMS_ADMIN.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { AutoritatCertificacioResource.Fields.nombre },
		descriptionField = AutoritatCertificacioResource.Fields.nombre,
		defaultSortFields = { @ResourceConfig.ResourceSort(field = AutoritatCertificacioResource.Fields.nombre, direction = Sort.Direction.ASC) },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		})
public class AutoritatCertificacioResource extends BaseResource<Long> {

	@NotEmpty
	@Size(max = 256)
	private String codca;
	@NotEmpty
	@Size(max = 256)
	private String nombre;

	private static final long serialVersionUID = 1L;

}
