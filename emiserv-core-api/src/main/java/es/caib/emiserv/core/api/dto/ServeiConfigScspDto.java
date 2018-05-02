/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Configuració SCSP d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiConfigScspDto extends ObjecteAmbPermisosDto {

	private long id;
	private String codigoCertificado;
	private Long emisorId;
	private Date fechaAlta;
	private Date fechaBaja;
	private int caducidad = 0;
	private String urlSincrona;
	private String urlAsincrona;
	private String actionSincrona;
	private String actionAsincrona;
	private String actionSolicitud;
	private String versionEsquema;
	private String tipoSeguridad;
	private Long claveFirma;
	private Long claveCifrado;
	private String xpathCifradoSincrono;
	private String xpathCifradoAsincrono;
	private String algoritmoCifrado;
	private String validacionFirma;
	private String prefijoPeticion;
	private String esquemas;
	private int numeroMaximoReenvios = 0;
	private int maxSolicitudesPeticion = 0;
	private String prefijoIdTransmision;
	private String xpathCodigoError;
	private String xpathLiteralError;
	private int timeout;
	private boolean xsdGestioActiva;



	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
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
	public String getEsquemas() {
		return esquemas;
	}
	public void setEsquemas(String esquemas) {
		this.esquemas = esquemas;
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
	public boolean isXsdGestioActiva() {
		return xsdGestioActiva;
	}
	public void setXsdGestioActiva(boolean xsdGestioActiva) {
		this.xsdGestioActiva = xsdGestioActiva;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
