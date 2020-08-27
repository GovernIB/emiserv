/**
 * 
 */
package es.caib.emiserv.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Classe de model de dades per a la taula CORE_EM_APLICACION.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_em_aplicacion")
public class ScspCoreEmAutorizacionAplicacionEntity {

	@Id
	@GenericGenerator(
			name = "sequence",
			strategy = "native",
			parameters = {
					@org.hibernate.annotations.Parameter(
							name = "sequence_name",
							value = "ID_APLICACION_SEQUENCE")
			})
	@GeneratedValue(generator = "sequence")
	private Integer idAplicacion;
	
	@Column(length = 16)
	private String nifCertificado;
	
	@Column(length = 64, nullable = false)
	private String numeroSerie;
	
	@Column(length = 512)
	private String cn;
	
	private Date tiempoComprobacion;
	
	private Long autoridadcertif;
	
	@Column(name = "fechaalta")
	private Date fechaAlta;
	
	@Column(name = "fechabaja")
	private Date fechaBaja;

	public Integer getIdAplicacion() {
		return idAplicacion;
	}
	public String getNifCertificado() {
		return nifCertificado;
	}
	public String getNumeroSerie() {
		return numeroSerie;
	}
	public String getCn() {
		return cn;
	}
	public Date getTiempoComprobacion() {
		return tiempoComprobacion;
	}
	public Long getAutoridadcertif() {
		return autoridadcertif;
	}
	public Date getFechaAlta() {
		return fechaAlta;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void update(
			String nifCertificado,
			String numeroSerie,
			String cn,
			Long autoridadcertif,
			Date fechaAlta,
			Date fechaBaja) {
		this.nifCertificado = nifCertificado;
		this.numeroSerie = numeroSerie;
		this.cn = cn;
		this.autoridadcertif = autoridadcertif;
		this.fechaAlta = fechaAlta;
		this.fechaBaja = fechaBaja;
	}

	public static Builder getBuilder(
			String nifCertificado,
			String numeroSerie,
			String cn,
			Long autoridadcertif) {
		return new Builder(
				nifCertificado,
				numeroSerie,
				cn,
				autoridadcertif);
	}

	public static class Builder {
		ScspCoreEmAutorizacionAplicacionEntity built;
		Builder(
				String nifCertificado,
				String numeroSerie,
				String cn,
				Long autoridadcertif) {
			built = new ScspCoreEmAutorizacionAplicacionEntity();
			built.nifCertificado = nifCertificado;
			built.numeroSerie = numeroSerie;
			built.cn = cn;
			built.autoridadcertif = autoridadcertif;
		}
		public Builder fechaAlta(Date fechaAlta) {
			built.fechaAlta = fechaAlta;
			return this;
		}
		public Builder fechaBaja(Date fechaBaja) {
			built.fechaBaja = fechaBaja;
			return this;
		}
		public ScspCoreEmAutorizacionAplicacionEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cn == null) ? 0 : cn.hashCode());
		result = prime * result + ((autoridadcertif == null) ? 0 : autoridadcertif.hashCode());
		result = prime * result + ((nifCertificado == null) ? 0 : nifCertificado.hashCode());
		result = prime * result + ((numeroSerie == null) ? 0 : numeroSerie.hashCode());
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
		ScspCoreEmAutorizacionAplicacionEntity other = (ScspCoreEmAutorizacionAplicacionEntity) obj;
		if (cn == null) {
			if (other.cn != null)
				return false;
		} else if (!cn.equals(other.cn))
			return false;
		if (autoridadcertif == null) {
			if (other.autoridadcertif != null)
				return false;
		} else if (!autoridadcertif.equals(other.autoridadcertif))
			return false;
		if (nifCertificado == null) {
			if (other.nifCertificado != null)
				return false;
		} else if (!nifCertificado.equals(other.nifCertificado))
			return false;
		if (numeroSerie == null) {
			if (other.numeroSerie != null)
				return false;
		} else if (!numeroSerie.equals(other.numeroSerie))
			return false;
		return true;
	}

}
