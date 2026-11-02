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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Configuració SCSP d'un servei (taula core_servicio), per a la pestanya «SCSP» de la finestra del
 * servei de la interfície REACT. És el formulari de serveiConfigScsp.jsp (ServeiConfigController),
 * amb les mateixes validacions que ServeiConfigScspCommand.
 * <p/>
 * L'identificador és el del servei. Un servei sense configurar torna els valors per defecte (amb
 * {@link #configurat} a false); desar-lo el crea. Les operacions deleguen a ServeiService, que només
 * deixa a EMS_RESP els serveis on té permís; per això el recurs és per als dos rols, com a la JSP.
 * <p/>
 * Els ids dels desplegables (emissor i claus) són text: base-react compara les opcions amb el valor
 * amb {@code ===}, i un número (1) no coincidiria amb l'opció ("1").
 *
 * @author Limit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ServeiConfigScspResource.Fields.codigoCertificado,
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { ServeiResource.ROL_ADMIN, ServeiResource.ROL_RESP },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE })
		})
public class ServeiConfigScspResource extends BaseResource<Long> {

	/** El servei ja té configuració SCSP (si no, es crea en desar). */
	private boolean configurat;
	/** Codi del servei (codi de certificat SCSP); no es pot canviar. */
	@NotEmpty @Size(max = 64)
	private String codigoCertificado;
	/** Emissor (opcions: ScspService.emissorFindAll). Els ids de les opcions són text: veure {@link ServeiConfigScspResource}. */
	@NotEmpty
	@ResourceField(enumType = true)
	private String emisorId;
	@NotNull
	private Date fechaAlta;
	private Date fechaBaja;
	private int caducidad;
	@Size(max = 256)
	private String urlSincrona;
	@Size(max = 256)
	private String urlAsincrona;
	@Size(max = 256)
	private String actionSincrona;
	@Size(max = 256)
	private String actionAsincrona;
	@Size(max = 256)
	private String actionSolicitud;
	/** Gestió dels fitxers XSD des d'EMISERV (l'esquema es calcula; no s'edita). */
	private boolean xsdGestioActiva;
	@NotEmpty @Size(max = 32)
	private String versionEsquema;
	@Size(max = 256)
	private String esquemas;
	/** XMLSignature o WS-Security. */
	@NotEmpty @Size(max = 16)
	@ResourceField(enumType = true)
	private String tipoSeguridad;
	/** Clau privada de firma (opcions: ScspService.clauPrivadaFindAll). */
	@NotEmpty
	@ResourceField(enumType = true)
	private String claveFirma;
	/** Clau pública de xifrat (opcions: ScspService.clauPublicaFindAll). */
	@ResourceField(enumType = true)
	private String claveCifrado;
	@Size(max = 256)
	private String xpathCifradoSincrono;
	@Size(max = 256)
	private String xpathCifradoAsincrono;
	@Size(max = 32)
	private String algoritmoCifrado;
	@Size(max = 32)
	private String validacionFirma;
	@Size(max = 8)
	private String prefijoPeticion;
	@NotNull
	private Integer numeroMaximoReenvios = 0;
	@NotNull
	private Integer maxSolicitudesPeticion = 0;
	@Size(max = 8)
	private String prefijoIdTransmision;
	@Size(max = 256)
	private String xpathCodigoError;
	@Size(max = 256)
	private String xpathLiteralError;
	@NotNull
	private Integer timeout;

	private static final long serialVersionUID = 1L;

}
