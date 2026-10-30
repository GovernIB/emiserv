package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceArtifact;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.annotation.ResourceField;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.model.ResourceArtifactType;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiDiagnosticEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.validation.ServeiValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Servei per a la interfície REACT (llistat i formulari de serveis).
 * <p/>
 * Permisos (idèntics als de la JSP):
 * <ul>
 *   <li>consulta i modificació: EMS_ADMIN (tots els serveis) i EMS_RESP (només els serveis amb
 *   permís ACL ADMINISTRATION, filtrats al servei);</li>
 *   <li>creació, accions ACTIVAR, DESACTIVAR, ESBORRAR i informe EXPORTAR: només EMS_ADMIN.</li>
 * </ul>
 * La creació i la modificació deleguen a ServeiService, com la JSP. L'esborrat genèric no està
 * permès: es fa amb l'acció ESBORRAR.
 * <p/>
 * Els camps obligatoris depenen del tipus ({@link ServeiValid}), com els grups de validació de
 * ServeiCommand de la JSP.
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
						roles = { ServeiResource.ROL_ADMIN },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE }),
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_RESP },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE })
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
@ServeiValid
public class ServeiResource extends BaseResource<Long> {

	public static final String ROL_ADMIN = "EMS_ADMIN";
	public static final String ROL_RESP = "EMS_RESP";

	public static final String ACTION_ACTIVAR_CODE = "ACTIVAR";
	public static final String ACTION_DESACTIVAR_CODE = "DESACTIVAR";
	public static final String ACTION_ESBORRAR_CODE = "ESBORRAR";
	public static final String REPORT_EXPORTAR_CODE = "EXPORTAR";

	@NotEmpty
	@Size(max = 64)
	private String codi;
	@NotEmpty
	@Size(max = 256)
	private String nom;
	@NotNull
	private ServeiTipusEnumDto tipus;
	@Size(max = 1024)
	private String descripcio;
	/** Backoffice: classe que processa les peticions (opcions: BackofficeService.getBackofficeClasses). */
	@Size(max = 256)
	@ResourceField(enumType = true)
	private String backofficeClass;
	@Size(max = 256)
	private String backofficeCaibUrl;
	private BackofficeAutenticacioTipusEnumDto backofficeCaibAutenticacio;
	private BackofficeAsyncTipusEnumDto backofficeCaibAsyncTipus;
	@Min(0)
	private Integer backofficeCaibAsyncTer;
	/** Enrutador: classe que tria la ruta (opcions: ServeiService.resolverClassesFindAll). */
	@Size(max = 256)
	@ResourceField(enumType = true)
	private String resolverClass;
	@Size(max = 256)
	private String urlPerDefecte;
	/** Enrutador múltiple: classe que combina les respostes (opcions: ServeiService.responseResolverClassesFindAll). */
	@Size(max = 256)
	@ResourceField(enumType = true)
	private String responseResolverClass;
	/** Enrutadors: acceptar peticions asíncrones encara que els cedents només admetin síncrones. */
	private boolean asyncSincron;
	/** Temps mínim entre peticions a un mateix cedent (ms); buit: el valor global. */
	@Min(0)
	private Integer asyncInterval;
	/** Peticions simultànies màximes a un mateix cedent; buit: el valor global. */
	@Min(1)
	private Integer asyncConcurrencia;
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
