package es.caib.emiserv.logic.intf.model;

import es.caib.emiserv.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.emiserv.logic.intf.base.annotation.ResourceArtifact;
import es.caib.emiserv.logic.intf.base.annotation.ResourceConfig;
import es.caib.emiserv.logic.intf.base.model.BaseResource;
import es.caib.emiserv.logic.intf.base.model.ResourceArtifactType;
import es.caib.emiserv.logic.intf.base.permission.PermissionEnum;
import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Ruta de destí d'un servei enrutador per a la interfície REACT (pestanya «Rutes» del servei).
 * <p/>
 * No té entitat pròpia: les operacions deleguen a ServeiService (rutaDesti*), com la JSP
 * (ServeiRutesController). La graella filtra per servei amb {@code serveiId:<id>}.
 * <p/>
 * Permisos: EMS_ADMIN i EMS_RESP (aquest, només als serveis on té permís), com a la JSP.
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ServeiRutaResource.Fields.entitatCodi,
		defaultSortFields = {
				@ResourceConfig.ResourceSort(field = ServeiRutaResource.Fields.ordre, direction = Sort.Direction.ASC)
		},
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN, ServeiResource.ROL_RESP },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE })
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ServeiRutaResource.ACTION_MOURE_CODE,
						requiresId = true,
						formClass = ServeiRutaResource.MoureForm.class,
						accessConstraints = @ResourceAccessConstraint(
								type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
								roles = { ServeiResource.ROL_ADMIN, ServeiResource.ROL_RESP })),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ServeiRutaResource.ACTION_PROVAR_CODE,
						requiresId = true,
						accessConstraints = @ResourceAccessConstraint(
								type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
								roles = { ServeiResource.ROL_ADMIN, ServeiResource.ROL_RESP }))
		})
public class ServeiRutaResource extends BaseResource<Long> {

	/** Canvia la posició de la ruta (paràmetre {@link MoureForm#getPosicio()}, començant per 0). */
	public static final String ACTION_MOURE_CODE = "MOURE";
	/** Prova la connexió amb el destí de la ruta (resultat {@link ProvaResultat}). */
	public static final String ACTION_PROVAR_CODE = "PROVAR";

	@NotNull
	private Long serveiId;
	/** Ordre de la ruta dins el servei (el posa el servidor; es canvia amb l'acció MOURE). */
	private Long ordre;
	@NotEmpty
	@Size(max = 64)
	private String entitatCodi;
	@NotEmpty
	@Size(max = 256)
	private String url;
	@NotNull
	private ServeiRutaDestiTipusEnumDto destiTipus = ServeiRutaDestiTipusEnumDto.EMISERV;
	/** Autenticació amb el backoffice (rutes BACKOFFICE). */
	private BackofficeAutenticacioTipusEnumDto backofficeAutenticacio;
	/** Prefix de les propietats on es configuren les credencials del backoffice (només lectura). */
	private String credencialsPropietats;
	/** Si les credencials estan configurades; null si la ruta no en necessita (només lectura). */
	private Boolean credencialsConfigurades;
	/** Enviaments al cedent pausats (consultes asíncrones; només lectura). */
	private boolean pausada;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class MoureForm implements Serializable {
		@NotNull
		private Integer posicio;
		private static final long serialVersionUID = 1L;
	}

	/** Resultat de l'acció PROVAR (ServeiRutaDestiProvaDto). */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ProvaResultat implements Serializable {
		private boolean correcta;
		private String url;
		private Integer codiHttp;
		private String missatge;
		private long tempsMs;
		private boolean credencialsPendents;
		private static final long serialVersionUID = 1L;
	}

	private static final long serialVersionUID = 1L;

}
