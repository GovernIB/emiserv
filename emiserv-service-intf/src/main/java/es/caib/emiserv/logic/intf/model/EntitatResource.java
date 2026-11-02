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
 * Entitat (de PINBAL) que pot enviar peticions a l'enrutador.
 * <p/>
 * Recurs del manteniment de configuració de la interfície REACT (entitatList.jsp / EntitatController). No té entitat pròpia:
 * les operacions deleguen als mateixos mètodes de servei que la JSP. Només EMS_ADMIN.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { EntitatResource.Fields.nom },
		descriptionField = EntitatResource.Fields.nom,
		defaultSortFields = { @ResourceConfig.ResourceSort(field = EntitatResource.Fields.codi, direction = Sort.Direction.ASC) },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		})
public class EntitatResource extends BaseResource<Long> {

	@NotEmpty
	@Size(max = 64)
	private String codi;
	@NotEmpty
	@Size(max = 256)
	private String nom;
	@NotEmpty
	@Size(max = 9)
	private String cif;
	@Size(max = 9)
	private String unitatArrel;

	private static final long serialVersionUID = 1L;

}
