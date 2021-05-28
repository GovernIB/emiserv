/**
 * 
 */
package es.caib.emiserv.back.command;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.intf.dto.ServeiConfigScspDto;

/**
 * Command pel formulari de configuració d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiConfigScspCommand {

	@NotEmpty @Size(max=64)
	private String codigoCertificado;
	@NotNull
	private Long emisorId;
	@NotNull
	private Date fechaAlta;
	private Date fechaBaja;
	private int caducidad = 0;
	@Size(max=256)
	private String urlSincrona;
	@Size(max=256)
	private String urlAsincrona;
	@Size(max=256)
	private String actionSincrona;
	@Size(max=256)
	private String actionAsincrona;
	@Size(max=256)
	private String actionSolicitud;
	@NotEmpty @Size(max=32)
	private String versionEsquema;
	@NotEmpty @Size(max=16)
	private String tipoSeguridad;
	@NotNull
	private Long claveFirma;
	private Long claveCifrado;
	@Size(max=256)
	private String xpathCifradoSincrono;
	@Size(max=256)
	private String xpathCifradoAsincrono;
	@Size(max=32)
	private String algoritmoCifrado;
	@Size(max=32)
	private String validacionFirma;
	@Size(max=8)
	private String prefijoPeticion;
	@NotNull
	private int numeroMaximoReenvios = 0;
	@NotNull
	private int maxSolicitudesPeticion = 0;
	@Size(max=8)
	private String prefijoIdTransmision;
	@Size(max=256)
	private String xpathCodigoError;
	@Size(max=256)
	private String xpathLiteralError;
	@NotNull
	private int timeout;
	@Size(max=256)
	private String esquemas;
	private boolean xsdGestioActiva;

	public String getCodigoCertificado() {
		return codigoCertificado;
	}
	public void setCodigoCertificado(String codigoCertificado) {
		this.codigoCertificado = codigoCertificado;
	}
	public Long getEmisorId() {
		return emisorId;
	}
	public void setEmisorId(Long emisorId) {
		this.emisorId = emisorId;
	}
	public Date getFechaAlta() {
		return fechaAlta;
	}
	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	public int getCaducidad() {
		return caducidad;
	}
	public void setCaducidad(int caducidad) {
		this.caducidad = caducidad;
	}
	public String getUrlSincrona() {
		return urlSincrona;
	}
	public void setUrlSincrona(String urlSincrona) {
		this.urlSincrona = urlSincrona;
	}
	public String getUrlAsincrona() {
		return urlAsincrona;
	}
	public void setUrlAsincrona(String urlAsincrona) {
		this.urlAsincrona = urlAsincrona;
	}
	public String getActionSincrona() {
		return actionSincrona;
	}
	public void setActionSincrona(String actionSincrona) {
		this.actionSincrona = actionSincrona;
	}
	public String getActionAsincrona() {
		return actionAsincrona;
	}
	public void setActionAsincrona(String actionAsincrona) {
		this.actionAsincrona = actionAsincrona;
	}
	public String getActionSolicitud() {
		return actionSolicitud;
	}
	public void setActionSolicitud(String actionSolicitud) {
		this.actionSolicitud = actionSolicitud;
	}
	public String getVersionEsquema() {
		return versionEsquema;
	}
	public void setVersionEsquema(String versionEsquema) {
		this.versionEsquema = versionEsquema;
	}
	public String getTipoSeguridad() {
		return tipoSeguridad;
	}
	public void setTipoSeguridad(String tipoSeguridad) {
		this.tipoSeguridad = tipoSeguridad;
	}
	public Long getClaveFirma() {
		return claveFirma;
	}
	public void setClaveFirma(Long claveFirma) {
		this.claveFirma = claveFirma;
	}
	public Long getClaveCifrado() {
		return claveCifrado;
	}
	public void setClaveCifrado(Long claveCifrado) {
		this.claveCifrado = claveCifrado;
	}
	public String getXpathCifradoSincrono() {
		return xpathCifradoSincrono;
	}
	public void setXpathCifradoSincrono(String xpathCifradoSincrono) {
		this.xpathCifradoSincrono = xpathCifradoSincrono;
	}
	public String getXpathCifradoAsincrono() {
		return xpathCifradoAsincrono;
	}
	public void setXpathCifradoAsincrono(String xpathCifradoAsincrono) {
		this.xpathCifradoAsincrono = xpathCifradoAsincrono;
	}
	public String getAlgoritmoCifrado() {
		return algoritmoCifrado;
	}
	public void setAlgoritmoCifrado(String algoritmoCifrado) {
		this.algoritmoCifrado = algoritmoCifrado;
	}
	public String getValidacionFirma() {
		return validacionFirma;
	}
	public void setValidacionFirma(String validacionFirma) {
		this.validacionFirma = validacionFirma;
	}
	public String getPrefijoPeticion() {
		return prefijoPeticion;
	}
	public void setPrefijoPeticion(String prefijoPeticion) {
		this.prefijoPeticion = prefijoPeticion;
	}
	public int getNumeroMaximoReenvios() {
		return numeroMaximoReenvios;
	}
	public void setNumeroMaximoReenvios(int numeroMaximoReenvios) {
		this.numeroMaximoReenvios = numeroMaximoReenvios;
	}
	public int getMaxSolicitudesPeticion() {
		return maxSolicitudesPeticion;
	}
	public void setMaxSolicitudesPeticion(int maxSolicitudesPeticion) {
		this.maxSolicitudesPeticion = maxSolicitudesPeticion;
	}
	public String getPrefijoIdTransmision() {
		return prefijoIdTransmision;
	}
	public void setPrefijoIdTransmision(String prefijoIdTransmision) {
		this.prefijoIdTransmision = prefijoIdTransmision;
	}
	public String getXpathCodigoError() {
		return xpathCodigoError;
	}
	public void setXpathCodigoError(String xpathCodigoError) {
		this.xpathCodigoError = xpathCodigoError;
	}
	public String getXpathLiteralError() {
		return xpathLiteralError;
	}
	public void setXpathLiteralError(String xpathLiteralError) {
		this.xpathLiteralError = xpathLiteralError;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getEsquemas() {
		return esquemas;
	}
	public void setEsquemas(String esquemas) {
		this.esquemas = esquemas;
	}
	public boolean isXsdGestioActiva() {
		return xsdGestioActiva;
	}
	public void setXsdGestioActiva(boolean xsdGestioActiva) {
		this.xsdGestioActiva = xsdGestioActiva;
	}

	public static ServeiConfigScspCommand toCommand(ServeiConfigScspDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ServeiConfigScspCommand.class);
	}
	public static ServeiConfigScspDto toDto(ServeiConfigScspCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ServeiConfigScspDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
