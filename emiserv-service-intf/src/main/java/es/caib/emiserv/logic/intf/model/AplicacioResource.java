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
 * Aplicació (certificat) autoritzada a consultar els serveis SCSP de l'emissor.
 * <p/>
 * Recurs del manteniment de configuració de la interfície REACT (aplicacioList.jsp / AplicacioController). No té entitat pròpia:
 * les operacions deleguen als mateixos mètodes de servei que la JSP. Només EMS_ADMIN.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { AplicacioResource.Fields.cn },
		descriptionField = AplicacioResource.Fields.cn,
		defaultSortFields = { @ResourceConfig.ResourceSort(field = AplicacioResource.Fields.cn, direction = Sort.Direction.ASC) },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		})
public class AplicacioResource extends BaseResource<Integer> {

	@NotEmpty
	@Size(max = 16)
	private String certificatNif;
	@NotEmpty
	@Size(max = 512)
	private String cn;
	@NotEmpty
	@Size(max = 64)
	private String numeroSerie;
	/** Autoritat de certificació (opcions: ScspService.autoridadCertificacionFindAll). Text: les opcions dels desplegables de base-react es comparen amb === amb el valor ("1" ≠ 1). */
	@NotNull
	@ResourceField(enumType = true)
	private String autoritatCertificacioId;
	private String autoritatCertificacioNom;
	private Date dataAlta;
	private Date dataBaixa;

	private static final long serialVersionUID = 1L;

}
