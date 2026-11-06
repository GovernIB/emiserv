package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.annotation.ResourceField;
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
 * Clau privada del magatzem de claus SCSP. La contrasenya no es torna mai a la interfície: en modificar, si es deixa buida es manté l'actual.
 * <p/>
 * Recurs del manteniment de configuració de la interfície REACT (clauPrivadaList.jsp / ClauPrivadaController). No té entitat pròpia:
 * les operacions deleguen als mateixos mètodes de servei que la JSP. Només EMS_ADMIN.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ClauPrivadaResource.Fields.alies,
		defaultSortFields = { @ResourceConfig.ResourceSort(field = ClauPrivadaResource.Fields.alies, direction = Sort.Direction.ASC) },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		})
public class ClauPrivadaResource extends BaseResource<Long> {

	@NotEmpty
	@Size(max = 256)
	private String alies;
	@NotEmpty
	@Size(max = 256)
	private String nom;
	/** Contrasenya: obligatòria en l'alta; en modificar, buida vol dir que no canvia. No es torna mai. */
	@Size(max = 256)
	private String password;
	@NotEmpty
	@Size(max = 256)
	private String numSerie;
	@NotNull
	private Date dataAlta;
	private Date dataBaixa;
	private boolean interoperabilitat;
	/** Organisme cessionari (opcions: ScspService.findAllOrganismeCessionari). Text: les opcions dels desplegables de base-react es comparen amb === amb el valor ("1" ≠ 1). */
	@NotNull
	@ResourceField(enumType = true)
	private String organisme;

	private static final long serialVersionUID = 1L;

}
