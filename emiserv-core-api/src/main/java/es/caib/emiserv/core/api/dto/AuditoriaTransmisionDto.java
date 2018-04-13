/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	public String getPeticionId() {
		return peticionId;
	}
	public void setPeticionId(String peticionId) {
		this.peticionId = peticionId;
	}
	public String getSolicitudId() {
		return solicitudId;
	}
	public void setSolicitudId(String solicitudId) {
		this.solicitudId = solicitudId;
	}
	public String getTransmisionId() {
		return transmisionId;
	}
	public void setTransmisionId(String transmisionId) {
		this.transmisionId = transmisionId;
	}
	public String getSolicitanteId() {
		return solicitanteId;
	}
	public void setSolicitanteId(String solicitanteId) {
		this.solicitanteId = solicitanteId;
	}
	public String getSolicitanteNombre() {
		return solicitanteNombre;
	}
	public void setSolicitanteNombre(String solicitanteNombre) {
		this.solicitanteNombre = solicitanteNombre;
	}
	public String getTitularDocumento() {
		return titularDocumento;
	}
	public void setTitularDocumento(String titularDocumento) {
		this.titularDocumento = titularDocumento;
	}
	public String getTitularNombre() {
		return titularNombre;
	}
	public void setTitularNombre(String titularNombre) {
		this.titularNombre = titularNombre;
	}
	public String getTitularApellido1() {
		return titularApellido1;
	}
	public void setTitularApellido1(String titularApellido1) {
		this.titularApellido1 = titularApellido1;
	}
	public String getTitularApellido2() {
		return titularApellido2;
	}
	public void setTitularApellido2(String titularApellido2) {
		this.titularApellido2 = titularApellido2;
	}
	public String getTitularNombreCompleto() {
		return titularNombreCompleto;
	}
	public void setTitularNombreCompleto(String titularNombreCompleto) {
		this.titularNombreCompleto = titularNombreCompleto;
	}
	public String getFuncionarioDocumento() {
		return funcionarioDocumento;
	}
	public void setFuncionarioDocumento(String funcionarioDocumento) {
		this.funcionarioDocumento = funcionarioDocumento;
	}
	public String getFuncionarioNombre() {
		return funcionarioNombre;
	}
	public void setFuncionarioNombre(String funcionarioNombre) {
		this.funcionarioNombre = funcionarioNombre;
	}
	public Date getFechaGeneracion() {
		return fechaGeneracion;
	}
	public void setFechaGeneracion(Date fechaGeneracion) {
		this.fechaGeneracion = fechaGeneracion;
	}
	public String getUnidadTramitadora() {
		return unidadTramitadora;
	}
	public void setUnidadTramitadora(String unidadTramitadora) {
		this.unidadTramitadora = unidadTramitadora;
	}
	public String getProcedimientoCodigo() {
		return procedimientoCodigo;
	}
	public void setProcedimientoCodigo(String procedimientoCodigo) {
		this.procedimientoCodigo = procedimientoCodigo;
	}
	public String getProcedimientoNombre() {
		return procedimientoNombre;
	}
	public void setProcedimientoNombre(String procedimientoNombre) {
		this.procedimientoNombre = procedimientoNombre;
	}
	public String getExpediente() {
		return expediente;
	}
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}
	public String getFinalidad() {
		return finalidad;
	}
	public void setFinalidad(String finalidad) {
		this.finalidad = finalidad;
	}
	public String getConsentimiento() {
		return consentimiento;
	}
	public void setConsentimiento(String consentimiento) {
		this.consentimiento = consentimiento;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public BackofficeSolicitudEstatEnumDto getBackofficeEstat() {
		return backofficeEstat;
	}
	public void setBackofficeEstat(BackofficeSolicitudEstatEnumDto backofficeEstat) {
		this.backofficeEstat = backofficeEstat;
	}
	public boolean isComunicacioBackofficeDisponible() {
		return comunicacioBackofficeDisponible;
	}
	public void setComunicacioBackofficeDisponible(boolean comunicacioBackofficeDisponible) {
		this.comunicacioBackofficeDisponible = comunicacioBackofficeDisponible;
	}
	public boolean isComunicacioBackofficeError() {
		return comunicacioBackofficeError;
	}
	public void setComunicacioBackofficeError(boolean comunicacioBackofficeError) {
		this.comunicacioBackofficeError = comunicacioBackofficeError;
	}

	public String getPeticionIdAbreujada() {
		return abreujar(peticionId);
	}
	public String getSolicitudIdAbreujada() {
		return abreujar(solicitudId);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final String abreujar(String id) {
		if (id != null && id.indexOf("000000") != -1) {
			return id.replaceAll("(0)\\1+", "$1\\.\\.\\.$1");
		} else {
			return null;
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
