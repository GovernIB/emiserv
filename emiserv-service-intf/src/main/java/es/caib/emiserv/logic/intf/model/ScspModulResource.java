package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

/**
 * Mòdul de la llibreria SCSP (l'identificador és el nom). Només es poden activar o desactivar l'entrada i la sortida, com a la JSP.
 * <p/>
 * Recurs del manteniment de configuració de la interfície REACT (scspModulList.jsp / ScspModulController). No té entitat pròpia:
 * les operacions deleguen als mateixos mètodes de servei que la JSP. Només EMS_ADMIN.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ScspModulResource.Fields.nom,
		defaultSortFields = { @ResourceConfig.ResourceSort(field = ScspModulResource.Fields.nom, direction = Sort.Direction.ASC) },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE })
		})
public class ScspModulResource extends BaseResource<String> {

	private String nom;
	private String descripcio;
	private boolean actiuEntrada;
	private boolean actiuSortida;

	private static final long serialVersionUID = 1L;

}
