/**
 * 
 */
package es.caib.emiserv.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *  Classe de model de dades per a la taula CORE_SERVICIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_servicio")
public class ScspCoreServicioEntity {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="servicio_seq_gen")
	@SequenceGenerator(name="servicio_seq_gen", sequenceName="id_servicio_sequence")
	private long id;
	@Column(name = "codcertificado", length = 64, nullable = false)
	private String codigoCertificado;
	@Column(name = "descripcion", length = 512, nullable = false)
	private String descripcion;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "emisor")
	private ScspCoreEmisorCertificadoEntity emisor;
	@Temporal(TemporalType.DATE)
	@Column(name = "fechaalta", nullable = false)
	private Date fechaAlta;
	@Temporal(TemporalType.DATE)
	@Column(name = "fechabaja", nullable = false)
	private Date fechaBaja;
	@Column(name = "caducidad", length = 256)
	private Integer caducidad;
	@Column(name = "urlsincrona", length = 256)
	private String urlSincrona;
	@Column(name = "urlasincrona", length = 256)
	private String urlAsincrona;
	@Column(name = "actionsincrona", length = 256)
	private String actionSincrona;
	@Column(name = "actionasincrona", length = 256)
	private String actionAsincrona;
	@Column(name = "actionsolicitud", length = 256)
	private String actionSolicitud;
	@Column(name = "versionesquema", length = 32)
	private String versionEsquema;
	@Column(name = "tiposeguridad", length = 16)
	private String tipoSeguridad;
	@Column(name = "clavefirma")
	private Long claveFirma;
	@Column(name = "clavecifrado")
	private Long claveCifrado;
	@Column(name = "xpathcifradosincrono", length = 256)
	private String xpathCifradoSincrono;
	@Column(name = "xpathcifradoasincrono", length = 256)
	private String xpathCifradoAsincrono;
	@Column(name = "algoritmocifrado", length = 32)
	private String algoritmoCifrado;
	@Column(name = "validacionfirma", length = 32)
	private String validacionFirma;
	@Column(name = "prefijopeticion", length = 8)
	private String prefijoPeticion;
	@Column(name = "esquemas", length = 256)
	private String esquemas;
	@Column(name = "numeromaximoreenvios", length = 256)
	private Integer numeroMaximoReenvios;
	@Column(name = "maxsolicitudespeticion", length = 256)
	private Integer maxSolicitudesPeticion;
	@Column(name = "prefijoidtransmision", length = 8)
	private String prefijoIdTransmision;
	@Column(name = "xpathcodigoerror", length = 256)
	private String xpathCodigoError;
	@Column(name = "xpathliteralerror", length = 256)
	private String xpathLiteralError;
	@Column(name = "timeout")
	private Integer timeout;



	public long getId() {
		return id;
	}
	public String getCodigoCertificado() {
		return codigoCertificado;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public ScspCoreEmisorCertificadoEntity getEmisor() {
		return emisor;
	}
	public Date getFechaAlta() {
		return fechaAlta;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public Integer getCaducidad() {
		return caducidad;
	}
	public String getUrlSincrona() {
		return urlSincrona;
	}
	public String getUrlAsincrona() {
		return urlAsincrona;
	}
	public String getActionSincrona() {
		return actionSincrona;
	}
	public String getActionAsincrona() {
		return actionAsincrona;
	}
	public String getActionSolicitud() {
		return actionSolicitud;
	}
	public String getVersionEsquema() {
		return versionEsquema;
	}
	public String getTipoSeguridad() {
		return tipoSeguridad;
	}
	public Long getClaveFirma() {
		return claveFirma;
	}
	public Long getClaveCifrado() {
		return claveCifrado;
	}
	public String getXpathCifradoSincrono() {
		return xpathCifradoSincrono;
	}
	public String getXpathCifradoAsincrono() {
		return xpathCifradoAsincrono;
	}
	public String getAlgoritmoCifrado() {
		return algoritmoCifrado;
	}
	public String getValidacionFirma() {
		return validacionFirma;
	}
	public String getPrefijoPeticion() {
		return prefijoPeticion;
	}
	public String getEsquemas() {
		return esquemas;
	}
	public Integer getNumeroMaximoReenvios() {
		return numeroMaximoReenvios;
	}
	public Integer getMaxSolicitudesPeticion() {
		return maxSolicitudesPeticion;
	}
	public String getPrefijoIdTransmision() {
		return prefijoIdTransmision;
	}
	public String getXpathCodigoError() {
		return xpathCodigoError;
	}
	public String getXpathLiteralError() {
		return xpathLiteralError;
	}
	public Integer getTimeout() {
		return timeout;
	}

	public void update(
			String descripcion,
			ScspCoreEmisorCertificadoEntity emisor,
			Date fechaAlta,
			Date fechaBaja,
			Integer caducidad,
			String urlSincrona,
			String urlAsincrona,
			String actionSincrona,
			String actionAsincrona,
			String actionSolicitud,
			String versionEsquema,
			String tipoSeguridad,
			Long claveFirma,
			Long claveCifrado,
			String xpathCifradoSincrono,
			String xpathCifradoAsincrono,
			String algoritmoCifrado,
			String validacionFirma,
			String prefijoPeticion,
			String esquemas,
			Integer numeroMaximoReenvios,
			Integer maxSolicitudesPeticion,
			String prefijoIdTransmision,
			String xpathCodigoError,
			String xpathLiteralError,
			Integer timeout) {
		this.descripcion = descripcion;
		this.emisor = emisor;
		this.fechaAlta = fechaAlta;
		this.fechaBaja = fechaBaja;
		this.caducidad = caducidad;
		this.urlSincrona = urlSincrona;
		this.urlAsincrona = urlAsincrona;
		this.actionSincrona = actionSincrona;
		this.actionAsincrona = actionAsincrona;
		this.actionSolicitud = actionSolicitud;
		this.versionEsquema = versionEsquema;
		this.tipoSeguridad = tipoSeguridad;
		this.claveFirma = claveFirma;
		this.claveCifrado = claveCifrado;
		this.xpathCifradoSincrono = xpathCifradoSincrono;
		this.xpathCifradoAsincrono = xpathCifradoAsincrono;
		this.algoritmoCifrado = algoritmoCifrado;
		this.validacionFirma = validacionFirma;
		this.prefijoPeticion = prefijoPeticion;
		this.esquemas = esquemas;
		this.numeroMaximoReenvios = numeroMaximoReenvios;
		this.maxSolicitudesPeticion = maxSolicitudesPeticion;
		this.prefijoIdTransmision = prefijoIdTransmision;
		this.xpathCodigoError = xpathCodigoError;
		this.xpathLiteralError = xpathLiteralError;
		this.timeout = timeout;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus servicio.
	 * 
	 * @param codcertificado
	 *            El codcertificado.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String codigoCertificado) {
		return new Builder(
				codigoCertificado);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta entitat.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		ScspCoreServicioEntity built;
		Builder(String codigoCertificado) {
			built = new ScspCoreServicioEntity();
			built.codigoCertificado = codigoCertificado;
		}
		public ScspCoreServicioEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codigoCertificado == null) ? 0 : codigoCertificado.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScspCoreServicioEntity other = (ScspCoreServicioEntity) obj;
		if (codigoCertificado == null) {
			if (other.codigoCertificado != null)
				return false;
		} else if (!codigoCertificado.equals(other.codigoCertificado))
			return false;
		return true;
	}

}
