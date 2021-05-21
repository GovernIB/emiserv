/**
 * 
 */
package es.caib.emiserv.persist.entity.scsp;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Classe de model de dades per a la taula CORE_EM_AUTORIZACION_CERT.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_em_autorizacion_cert")
public class ScspCoreEmAutorizacionCertificadoEntity {

	@Id
	@GenericGenerator(
			name = "sequence_scsp_autcer",
			strategy = "native",
			parameters = {
					@org.hibernate.annotations.Parameter(
							name = "sequence_name",
							value = "ID_AUTORIZACION_CERTIFIC_SEQ")
			})
	@GeneratedValue(generator="sequence_scsp_autcer")
	private Long id;
	@ManyToOne
	@JoinColumn(
			name = "idcertificado",
			foreignKey = @ForeignKey(name = "aut_cert_servicio"),
			nullable = false)
	private ScspCoreServicioEntity servicio;
	@ManyToOne
	@JoinColumn(
			name = "idaplicacion",
			foreignKey = @ForeignKey(name = "aut_cert_aplicacion"),
			nullable = false)
	private ScspCoreEmAplicacionEntity aplicacion;
	@ManyToOne
	@JoinColumn(
			name = "idorganismo",
			foreignKey = @ForeignKey(name = "aut_cert_organismo"),
			nullable = false)
	private ScspCoreEmAutorizacionOrganismoEntity organismo;
	@Column(name = "fechaalta")
	private Date fechaAlta;
	@Column(name = "fechabaja")
	private Date fechaBaja;

	public Long getId() {
		return id;
	}
	public ScspCoreServicioEntity getServicio() {
		return servicio;
	}
	public ScspCoreEmAplicacionEntity getAplicacion() {
		return aplicacion;
	}
	public ScspCoreEmAutorizacionOrganismoEntity getOrganismo() {
		return organismo;
	}
	public Date getFechaAlta() {
		return fechaAlta;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void update(
			ScspCoreServicioEntity servicio,
			ScspCoreEmAplicacionEntity aplicacion,
			ScspCoreEmAutorizacionOrganismoEntity organismo,
			Date fechaAlta,
			Date fechaBaja) {
		this.servicio = servicio;
		this.aplicacion = aplicacion;
		this.organismo = organismo;
		this.fechaAlta = fechaAlta;
		this.fechaBaja = fechaBaja;
	}

	public static Builder getBuilder(
			ScspCoreServicioEntity servicio,
			ScspCoreEmAplicacionEntity aplicacion,
			ScspCoreEmAutorizacionOrganismoEntity organismo) {
		return new Builder(
				servicio,
				aplicacion,
				organismo);
	}

	public static class Builder {
		ScspCoreEmAutorizacionCertificadoEntity built;
		Builder(
				ScspCoreServicioEntity servicio,
				ScspCoreEmAplicacionEntity aplicacion,
				ScspCoreEmAutorizacionOrganismoEntity organismo) {
			built = new ScspCoreEmAutorizacionCertificadoEntity();
			built.servicio = servicio;
			built.aplicacion = aplicacion;
			built.organismo = organismo;
		}
		public Builder fechaAlta(Date fechaAlta) {
			built.fechaAlta = fechaAlta;
			return this;
		}
		public Builder fechaBaja(Date fechaBaja) {
			built.fechaBaja = fechaBaja;
			return this;
		}
		public ScspCoreEmAutorizacionCertificadoEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aplicacion == null) ? 0 : aplicacion.hashCode());
		result = prime * result + ((organismo == null) ? 0 : organismo.hashCode());
		result = prime * result + ((servicio == null) ? 0 : servicio.hashCode());
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
		ScspCoreEmAutorizacionCertificadoEntity other = (ScspCoreEmAutorizacionCertificadoEntity) obj;
		if (aplicacion == null) {
			if (other.aplicacion != null)
				return false;
		} else if (!aplicacion.equals(other.aplicacion))
			return false;
		if (organismo == null) {
			if (other.organismo != null)
				return false;
		} else if (!organismo.equals(other.organismo))
			return false;
		if (servicio == null) {
			if (other.servicio != null)
				return false;
		} else if (!servicio.equals(other.servicio))
			return false;
		return true;
	}

}
