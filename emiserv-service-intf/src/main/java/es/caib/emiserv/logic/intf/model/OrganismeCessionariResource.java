package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

/**
 * Organisme cessionari (el que fa les consultes SCSP).
 * <p/>
 * Recurs del manteniment de configuració de la interfície REACT (organismeCessionariList.jsp / OrganismeCessionariController). No té entitat pròpia:
 * les operacions deleguen als mateixos mètodes de servei que la JSP. Només EMS_ADMIN.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { OrganismeCessionariResource.Fields.nom },
		descriptionField = OrganismeCessionariResource.Fields.nom,
		defaultSortFields = { @ResourceConfig.ResourceSort(field = OrganismeCessionariResource.Fields.nom, direction = Sort.Direction.ASC) },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		})
public class OrganismeCessionariResource extends BaseResource<Long> {

	@NotEmpty
	@Size(max = 64)
	private String nom;
	@NotEmpty
	@Size(max = 16)
	private String cif;
	@NotNull
	private Date dataAlta;
	private Date dataBaixa;
	@Size(max = 9)
	private String codiUnitatTramitadora;
	private boolean bloquejat;

	private static final long serialVersionUID = 1L;

}
