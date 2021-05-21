/**
 * 
 */
package es.caib.emiserv.persist.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Classe del model de dades que representa una comunicació entre
 * EMISERV i el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = AbstractAuditableEntity.TABLE_PREFIX + "_backoffice_com")
@EntityListeners(AuditingEntityListener.class)
public class BackofficeComunicacioEntity extends AbstractPersistable<Long> {

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "peticio_data", nullable = false)
	private Date peticioData;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "resposta_data")
	private Date respostaData;
	@Lob
	@Column(name = "peticio_xml", nullable = false)
	private String peticioXml;
	@Lob
	@Column(name = "resposta_xml")
	private String respostaXml;
	@Lob
	@Column(name = "error")
	private String error;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "peticio_id",
			foreignKey = @ForeignKey(name = "ems_backcom_backpet_fk"))
	private BackofficePeticioEntity peticio;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "solicitud_id",
			foreignKey = @ForeignKey(name = "ems_backcom_backsol_fk"))
	private BackofficeSolicitudEntity solicitud;
	@Version
	private long version = 0;



	public BackofficePeticioEntity getPeticio() {
		return peticio;
	}
	public BackofficeSolicitudEntity getSolicitud() {
		return solicitud;
	}
	public Date getPeticioData() {
		return peticioData;
	}
	public Date getRespostaData() {
		return respostaData;
	}
	public String getPeticioXml() {
		return peticioXml;
	}
	public String getRespostaXml() {
		return respostaXml;
	}
	public String getError() {
		return error;
	}

	public void updatePeticio(
			String peticioXml) {
		this.peticioXml = peticioXml;
		this.peticioData = new Date();
	}
	public void updateResposta(
			String respostaXml) {
		this.respostaData = new Date();
		this.respostaXml = respostaXml;
	}
	public void updateError(
			String error) {
		this.error = error;
	}

	public static Builder getBuilder(
			BackofficePeticioEntity peticio,
			String peticioXml) {
		return new Builder(
				peticio,
				peticioXml);
	}

	public static class Builder {
		BackofficeComunicacioEntity built;
		Builder(
				BackofficePeticioEntity peticio,
				String peticioXml) {
			built = new BackofficeComunicacioEntity();
			built.peticio = peticio;
			built.peticioXml = peticioXml;
			built.peticioData = new Date();
		}
		public Builder solicitud(BackofficeSolicitudEntity solicitud) {
			built.solicitud = solicitud;
			return this;
		}
		public Builder respostaData(Date respostaData) {
			built.respostaData = respostaData;
			return this;
		}
		public Builder respostaXml(String respostaXml) {
			built.respostaXml = respostaXml;
			return this;
		}
		public BackofficeComunicacioEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((peticio == null) ? 0 : peticio.hashCode());
		result = prime * result + ((peticioData == null) ? 0 : peticioData.hashCode());
		result = prime * result + ((peticioXml == null) ? 0 : peticioXml.hashCode());
		result = prime * result + ((respostaData == null) ? 0 : respostaData.hashCode());
		result = prime * result + ((respostaXml == null) ? 0 : respostaXml.hashCode());
		result = prime * result + ((solicitud == null) ? 0 : solicitud.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BackofficeComunicacioEntity other = (BackofficeComunicacioEntity) obj;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (peticio == null) {
			if (other.peticio != null)
				return false;
		} else if (!peticio.equals(other.peticio))
			return false;
		if (peticioData == null) {
			if (other.peticioData != null)
				return false;
		} else if (!peticioData.equals(other.peticioData))
			return false;
		if (peticioXml == null) {
			if (other.peticioXml != null)
				return false;
		} else if (!peticioXml.equals(other.peticioXml))
			return false;
		if (respostaData == null) {
			if (other.respostaData != null)
				return false;
		} else if (!respostaData.equals(other.respostaData))
			return false;
		if (respostaXml == null) {
			if (other.respostaXml != null)
				return false;
		} else if (!respostaXml.equals(other.respostaXml))
			return false;
		if (solicitud == null) {
			if (other.solicitud != null)
				return false;
		} else if (!solicitud.equals(other.solicitud))
			return false;
		return true;
	}

}
