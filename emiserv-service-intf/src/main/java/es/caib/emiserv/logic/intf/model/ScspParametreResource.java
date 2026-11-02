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
 * Paràmetre de configuració de la llibreria SCSP (l'identificador és el nom; no es pot canviar).
 * <p/>
 * Recurs del manteniment de configuració de la interfície REACT (scspParametresList.jsp / ScspParametresController). No té entitat pròpia:
 * les operacions deleguen als mateixos mètodes de servei que la JSP. Només EMS_ADMIN.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { ScspParametreResource.Fields.nombre },
		descriptionField = ScspParametreResource.Fields.nombre,
		defaultSortFields = { @ResourceConfig.ResourceSort(field = ScspParametreResource.Fields.nombre, direction = Sort.Direction.ASC) },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		})
public class ScspParametreResource extends BaseResource<String> {

	@NotEmpty
	@Size(max = 64)
	private String nombre;
	@Size(max = 512)
	private String valor;
	@Size(max = 512)
	private String descripcion;

	private static final long serialVersionUID = 1L;

}
