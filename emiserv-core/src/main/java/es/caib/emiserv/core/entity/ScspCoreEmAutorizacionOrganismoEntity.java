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
 * Classe de model de dades per a la taula CORE_EM_AUTORIZACION_ORGANISMO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_em_autorizacion_organismo")
public class ScspCoreEmAutorizacionOrganismoEntity {

	@Id
	@GenericGenerator(name="sequence", strategy="native", parameters={@org.hibernate.annotations.Parameter(name="sequence_name", value="ID_AUTORIZACION_ORGANISMO_SEQ")})
	@GeneratedValue(generator="sequence")
	private Long id;
	@Column(length = 16, nullable = false)
	private String idorganismo;
	@Column(nullable = false)
	private Date fechaAlta;
	private Date fechaBaja;
	@Column(length = 64)
	private String nombreOrganismo;

	public Long getId() {
		return id;
	}
	public String getIdorganismo() {
		return idorganismo;
	}
	public Date getFechaAlta() {
		return fechaAlta;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public String getNombreOrganismo() {
		return nombreOrganismo;
	}

	public void update(
			String idorganismo,
			String nombreOrganismo,
			Date fechaAlta,
			Date fechaBaja) {
		this.idorganismo = idorganismo;
		this.nombreOrganismo = nombreOrganismo;
		this.fechaAlta = fechaAlta;
		this.fechaBaja = fechaBaja;
	}

	public static Builder getBuilder(
			String idorganismo,
			String nombreOrganismo,
			Date fechaAlta) {
		return new Builder(
				idorganismo,
				nombreOrganismo,
				fechaAlta);
	}

	public static class Builder {
		ScspCoreEmAutorizacionOrganismoEntity built;
		Builder(
				String idorganismo,
				String nombreOrganismo,
				Date fechaAlta) {
			built = new ScspCoreEmAutorizacionOrganismoEntity();
			built.idorganismo = idorganismo;
			built.nombreOrganismo = nombreOrganismo;
			built.fechaAlta = fechaAlta;
		}
		public Builder fechaBaja(Date fechaBaja) {
			built.fechaBaja = fechaBaja;
			return this;
		}
		public ScspCoreEmAutorizacionOrganismoEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idorganismo == null) ? 0 : idorganismo.hashCode());
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
		ScspCoreEmAutorizacionOrganismoEntity other = (ScspCoreEmAutorizacionOrganismoEntity) obj;
		if (idorganismo == null) {
			if (other.idorganismo != null)
				return false;
		} else if (!idorganismo.equals(other.idorganismo))
			return false;
		return true;
	}

}
