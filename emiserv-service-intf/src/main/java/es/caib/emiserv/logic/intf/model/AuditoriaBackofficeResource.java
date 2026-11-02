package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceArtifact;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.model.ResourceArtifactType;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

import java.util.Date;

/**
 * Petició d'un servei backoffice per a l'auditoria de la interfície REACT
 * (auditoriaBackofficeList.jsp). L'identificador és el número de petició SCSP.
 * <p/>
 * Recurs de només lectura sense entitat pròpia: delega a BackofficeService, que aplica els permisos
 * (EMS_RESP només veu els serveis on té permís) i pagina al servidor. El filtre de la graella admet
 * {@code procediment}, {@code servei} (id), {@code estat}, {@code numeroPeticio} i
 * {@code dataPeticio} (amb {@code >:} i {@code <:}); el filtre ràpid cerca pel número de petició.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { AuditoriaBackofficeResource.Fields.peticioId },
		descriptionField = AuditoriaBackofficeResource.Fields.peticioId,
		defaultSortFields = {
				@ResourceConfig.ResourceSort(field = AuditoriaBackofficeResource.Fields.dataPeticio, direction = Sort.Direction.DESC)
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
						code = AuditoriaBackofficeResource.REPORT_EXPORTAR_CODE,
						formClass = ExportacioAuditoriaForm.class,
						accessConstraints = @ResourceAccessConstraint(
								type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
								roles = { ServeiResource.ROL_ADMIN, ServeiResource.ROL_RESP }))
		})
public class AuditoriaBackofficeResource extends BaseResource<String> {

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
	private PeticioEstatEnumDto estat;
	private String estatScsp;
	/** L'error ve de l'emissor SCSP (estat SCSP que no comença per 00); si no, del backoffice. */
	private boolean estatScspError;
	private String error;
	private boolean sincrona;
	private Date ter;
	private int numEnviaments;
	private int numTransmissions;
	private Date dataDarreraComprovacio;
	private int processadesTotal;
	private int processadesError;
	/** Percentatge de sol·licituds processades (peticions asíncrones). */
	private int processadesPercent;
	private boolean comunicacioBackofficeDisponible;
	private boolean comunicacioBackofficeError;

	private static final long serialVersionUID = 1L;

}
