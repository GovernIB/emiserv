package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceArtifact;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.model.ResourceArtifactType;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

import java.util.Date;

/**
 * Petició redirigida per l'enrutador per a l'auditoria de la interfície REACT
 * (auditoriaEnrutadorList.jsp).
 * <p/>
 * Recurs de només lectura sense entitat pròpia: delega a RedireccioService, que aplica els permisos
 * (EMS_RESP només veu els serveis on té permís) i pagina al servidor. El filtre de la graella admet
 * {@code procediment}, {@code serveiCodi}, {@code estat}, {@code numeroPeticio} i
 * {@code dataPeticio} (amb {@code >:} i {@code <:}); el filtre ràpid cerca pel número de petició.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { AuditoriaEnrutadorResource.Fields.peticioId },
		descriptionField = AuditoriaEnrutadorResource.Fields.peticioId,
		defaultSortFields = {
				@ResourceConfig.ResourceSort(field = AuditoriaEnrutadorResource.Fields.dataPeticio, direction = Sort.Direction.DESC)
		},
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN, ServeiResource.ROL_RESP },
						grantedPermissions = { PermissionEnum.READ })
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = AuditoriaEnrutadorResource.REPORT_EXPORTAR_CODE,
						formClass = ExportacioAuditoriaForm.class,
						accessConstraints = @ResourceAccessConstraint(
								type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
								roles = { ServeiResource.ROL_ADMIN, ServeiResource.ROL_RESP }))
		})
public class AuditoriaEnrutadorResource extends BaseResource<Long> {

	public static final String REPORT_EXPORTAR_CODE = "EXPORTAR";

	private String peticioId;
	private Date dataPeticio;
	private Date dataResposta;
	private String procedimentCodi;
	private String procedimentNom;
	private String procedimentCodiNom;
	private String serveiCodi;
	private String serveiDescripcio;
	/** Id del servei (per obrir-ne la finestra); null si el servei ja no existeix. */
	private Long serveiId;
	private ServeiTipusEnumDto serveiTipus;
	/** Entitat que ha retornat la resposta. */
	private String entitatCodi;
	private PeticioEstatEnumDto estat;
	private String error;
	/** Enrutador múltiple: hi ha les respostes de cada entitat. */
	private boolean teRespostes;

	private static final long serialVersionUID = 1L;

}
