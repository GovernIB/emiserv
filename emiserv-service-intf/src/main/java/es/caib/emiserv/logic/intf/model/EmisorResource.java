package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

/**
 * Emissor de certificats SCSP.
 * <p/>
 * Recurs del manteniment de configuració de la interfície REACT (emisorList.jsp / EmisorController). No té entitat pròpia:
 * les operacions deleguen als mateixos mètodes de servei que la JSP. Només EMS_ADMIN.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { EmisorResource.Fields.nom },
		descriptionField = EmisorResource.Fields.nom,
		defaultSortFields = { @ResourceConfig.ResourceSort(field = EmisorResource.Fields.nom, direction = Sort.Direction.ASC) },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		})
public class EmisorResource extends BaseResource<Long> {

	@NotEmpty
	@Size(max = 50)
	private String nom;
	@NotEmpty
	@Size(max = 16)
	private String cif;
	private Date dataAlta;
	private Date dataBaixa;

	private static final long serialVersionUID = 1L;

}
