/**
 * 
 */
package es.caib.emiserv.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Classe del model de dades que representa una petició d'una
 * redirecció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ems_redir_peticio")
@EntityListeners(AuditingEntityListener.class)
public class RedireccioPeticioEntity extends AbstractPersistable<Long> {

	@Column(name = "peticio_id", length = 128, nullable = false)
	private String peticioId;
	@Column(name = "servei_codi", length = 256, nullable = false)
	private String serveiCodi;
	@Column(name = "estat", length = 16, nullable = false)
	private String estat;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_peticio")
	private Date dataPeticio;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_resposta")
	private Date dataResposta;
	@Lob
	@Column(name = "error")
	private String error;
	@Column(name = "sincrona")
	private boolean sincrona;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ter")
	private Date ter;
	@Column(name = "num_enviaments", nullable = false)
	private int numEnviaments;
	@Column(name = "num_transmissions", nullable = false)
	private int numTransmissions;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_comprovacio")
	private Date dataDarreraComprovacio;
	@OneToMany(
			mappedBy = "servei",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<ServeiRutaDestiEntity> rutes = new ArrayList<ServeiRutaDestiEntity>();
	@Version
	private long version = 0;
	@Column(name = "emissor_codi", length = 10, nullable = true)
	private String emissorCodi;



	public String getPeticioId() {
		return peticioId;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public String getEstat() {
		return estat;
	}
	public Date getDataPeticio() {
		return dataPeticio;
	}
	public Date getDataResposta() {
		return dataResposta;
	}
	public String getError() {
		return error;
	}
	public boolean isSincrona() {
		return sincrona;
	}
	public Date getTer() {
		return ter;
	}
	public int getNumEnviaments() {
		return numEnviaments;
	}
	public int getNumTransmissions() {
		return numTransmissions;
	}
	public Date getDataDarreraComprovacio() {
		return dataDarreraComprovacio;
	}
	public List<ServeiRutaDestiEntity> getRutes() {
		return rutes;
	}
	public String getEmissorCodi() {
		return emissorCodi;
	}
	
	public void updateResposta(
			String estat,
			String error) {
		this.dataResposta = new Date();
		this.estat = estat;
		this.error = error;
	}

	public static Builder getBuilder(
			String peticioId,
			String serveiCodi,
			String estat,
			int numEnviaments,
			int numTransmissions, 
			String emissorCodi) {
		return new Builder(
				peticioId,
				serveiCodi,
				estat,
				numEnviaments,
				numTransmissions,
				emissorCodi);
	}

	public static class Builder {
		RedireccioPeticioEntity built;
		Builder(
				String peticioId,
				String serveiCodi,
				String estat,
				int numEnviaments,
				int numTransmissions, 
				String emissorCodi) {
			built = new RedireccioPeticioEntity();
			built.dataPeticio = new Date();
			built.peticioId = peticioId;
			built.serveiCodi = serveiCodi;
			built.estat = estat;
			built.numEnviaments = numEnviaments;
			built.numTransmissions = numTransmissions;
			built.emissorCodi = emissorCodi;
		}
		public Builder dataPeticio(Date dataPeticio) {
			built.dataPeticio = dataPeticio;
			return this;
		}
		public Builder dataResposta(Date dataResposta) {
			built.dataResposta = dataResposta;
			return this;
		}
		public Builder error(String error) {
			built.error = error;
			return this;
		}
		public Builder sincrona(boolean sincrona) {
			built.sincrona = sincrona;
			return this;
		}
		public Builder ter(Date ter) {
			built.ter = ter;
			return this;
		}
		public RedireccioPeticioEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dataPeticio == null) ? 0 : dataPeticio.hashCode());
		result = prime * result + ((peticioId == null) ? 0 : peticioId.hashCode());
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
		RedireccioPeticioEntity other = (RedireccioPeticioEntity) obj;
		if (dataPeticio == null) {
			if (other.dataPeticio != null)
				return false;
		} else if (!dataPeticio.equals(other.dataPeticio))
			return false;
		if (peticioId == null) {
			if (other.peticioId != null)
				return false;
		} else if (!peticioId.equals(other.peticioId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RedireccioPeticioEntity [peticioId=" + peticioId + ", serveiCodi=" + serveiCodi + ", estat=" + estat
				+ ", dataPeticio=" + dataPeticio + "]";
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
