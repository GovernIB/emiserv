package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.annotation.ResourceField;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Autorització d'una aplicació d'un organisme per a consultar un servei SCSP, per a la interfície
 * REACT (pestanya «Autoritzacions» del servei).
 * <p/>
 * No té entitat pròpia: les operacions deleguen a ScspService (autoritzacio*), com la JSP
 * (AutoritzacioController). La graella filtra per servei amb {@code serveiId:<id>}; el filtre
 * ràpid cerca per aplicació, com el filtre de la JSP.
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
		quickFilterFields = { ServeiAutoritzacioResource.Fields.aplicacioNom },
		descriptionField = ServeiAutoritzacioResource.Fields.aplicacioNom,
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		})
public class ServeiAutoritzacioResource extends BaseResource<Long> {

	@NotNull
	private Long serveiId;
	/** Aplicació (certificat) autoritzada (opcions: ScspService.aplicacioFindAll). */
	@NotNull
	@ResourceField(enumType = true)
	private Long aplicacioId;
	private String aplicacioNom;
	/** Organisme autoritzat (opcions: ScspService.organismeFindAll). */
	@NotNull
	@ResourceField(enumType = true)
	private Long organismeId;
	private String organismeNom;
	private Date dataAlta;
	private Date dataBaixa;

	private static final long serialVersionUID = 1L;

}
