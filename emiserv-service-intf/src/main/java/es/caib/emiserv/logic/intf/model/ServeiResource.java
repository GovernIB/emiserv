package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceArtifact;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.model.ResourceArtifactType;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import es.caib.emiserv.logic.intf.dto.ServeiDiagnosticEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * Informació d'un servei per a la interfície REACT (llistat de serveis).
 * <p/>
 * Permisos (idèntics als de la JSP):
 * <ul>
 *   <li>consulta: EMS_ADMIN (tots els serveis) i EMS_RESP (només els serveis amb permís ACL
 *   ADMINISTRATION, filtrats al servei);</li>
 *   <li>accions ACTIVAR, DESACTIVAR, ESBORRAR i informe EXPORTAR: només EMS_ADMIN.</li>
 * </ul>
 * Les operacions genèriques de creació, modificació i esborrat no estan permeses: les accions
 * deleguen a ServeiService perquè les regles de negoci i l'auditoria siguin les de la JSP.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { ServeiResource.Fields.codi, ServeiResource.Fields.nom },
		descriptionField = ServeiResource.Fields.nom,
		defaultSortFields = {
				@ResourceConfig.ResourceSort(field = ServeiResource.Fields.codi, direction = Sort.Direction.ASC)
		},
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN, ServeiResource.ROL_RESP },
						grantedPermissions = { PermissionEnum.READ })
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ServeiResource.ACTION_ACTIVAR_CODE,
						requiresId = true,
						accessConstraints = @ResourceAccessConstraint(
								type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
								roles = { ServeiResource.ROL_ADMIN })),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ServeiResource.ACTION_DESACTIVAR_CODE,
						requiresId = true,
						accessConstraints = @ResourceAccessConstraint(
								type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
								roles = { ServeiResource.ROL_ADMIN })),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ServeiResource.ACTION_ESBORRAR_CODE,
						requiresId = true,
						accessConstraints = @ResourceAccessConstraint(
								type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
								roles = { ServeiResource.ROL_ADMIN })),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = ServeiResource.REPORT_EXPORTAR_CODE,
						formClass = ServeiResource.ExportacioForm.class,
						accessConstraints = @ResourceAccessConstraint(
								type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
								roles = { ServeiResource.ROL_ADMIN }))
		})
public class ServeiResource extends BaseResource<Long> {

	public static final String ROL_ADMIN = "EMS_ADMIN";
	public static final String ROL_RESP = "EMS_RESP";

	public static final String ACTION_ACTIVAR_CODE = "ACTIVAR";
	public static final String ACTION_DESACTIVAR_CODE = "DESACTIVAR";
	public static final String ACTION_ESBORRAR_CODE = "ESBORRAR";
	public static final String REPORT_EXPORTAR_CODE = "EXPORTAR";

	private String codi;
	private String nom;
	private ServeiTipusEnumDto tipus;
	private boolean actiu;
	private boolean configurat;
	/** Configurat a l'emissor SCSP (mateixa regla que ServeiDto.configuratScsp). */
	private boolean configuratScsp;
	private boolean xsdGestioActiva;
	/** Nombre de principals (usuaris o rols) amb permisos ACL sobre el servei, com a la JSP. */
	private long permisosCount;
	/** Nombre de rutes de destí del servei. */
	private long rutesCount;
	/** Motius pels quals el servei no està ben configurat (buit si no n'hi ha cap). */
	private List<ServeiDiagnosticEnumDto> diagnostic;

	/**
	 * Paràmetres de l'informe EXPORTAR: el mateix filtre i ordre que la graella. El format del
	 * fitxer (CSV o XLSX) és el paràmetre fileType de la petició de l'informe.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ExportacioForm implements Serializable {
		private String quickFilter;
		private String filter;
		/** Ordenació en format Spring Data ("camp" o "camp,asc|desc"). */
		private String[] sort;
		private static final long serialVersionUID = 1L;
	}

	private static final long serialVersionUID = 1L;

}
