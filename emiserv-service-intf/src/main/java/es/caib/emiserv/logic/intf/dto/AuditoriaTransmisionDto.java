/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'una auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class AuditoriaTransmisionDto implements Serializable {

	private String peticionId;
	private String solicitudId;
	private String transmisionId;
	private String solicitanteId;
	private String solicitanteNombre;
	private String titularDocumento;
	private String titularNombre;
	private String titularApellido1;
	private String titularApellido2;
	private String titularNombreCompleto;
	private String funcionarioDocumento;
	private String funcionarioNombre;
	private Date fechaGeneracion;
	private String unidadTramitadora;
	private String procedimientoCodigo;
	private String procedimientoNombre;
	private String expediente;
	private String finalidad;
	private String consentimiento;
	private String estado;
	private String error;
	private BackofficeSolicitudEstatEnumDto backofficeEstat;
	private boolean comunicacioBackofficeDisponible;
	private boolean comunicacioBackofficeError;

	public String getPeticionIdAbreujada() {
		return abreujar(peticionId);
	}
	public String getSolicitudIdAbreujada() {
		return abreujar(solicitudId);
	}

	private static final String abreujar(String id) {
		if (id != null && id.indexOf("000000") != -1) {
			return id.replaceAll("(0)\\1+", "$1\\.\\.\\.$1");
		} else {
			return null;
		}
	}

	public String getProcedimientoCodigoNombre() {
		if ((procedimientoCodigo == null || procedimientoCodigo.isBlank()) && (procedimientoNombre == null || procedimientoNombre.isBlank()))
			return null;
		if (procedimientoCodigo == null || procedimientoCodigo.isBlank())
			return procedimientoNombre;
		if ((procedimientoNombre == null || procedimientoNombre.isBlank()))
			return procedimientoCodigo;
		return procedimientoCodigo + " - " + procedimientoNombre;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
