/**
 * 
 */
package es.caib.emiserv.persist.entity;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
import java.util.Date;

/**
 * Classe del model de dades que representa una sol·licitud d'una
 * petició d'una redirecció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = AbstractAuditableEntity.TABLE_PREFIX + "_redir_solicitud")
@EntityListeners(AuditingEntityListener.class)
public class RedireccioSolicitudEntity extends AbstractPersistable<Long> {

	@Column(name = "solicitud_id", length = 256, nullable = false)
	private String solicitudId;
	@Column(name = "transmision_id", length = 256)
	private String transmisioId;
	@Column(name = "solicitant_id", length = 40, nullable = false)
	private String solicitantId;
	@Column(name = "solicitant_codi", length = 64)
	private String solicitantCodi;
	@Column(name = "solicitant_nom", length = 256)
	private String solicitantNom;
	@Column(name = "titular_tipus_doc", length = 16)
	private String titularTipusDoc;
	@Column(name = "titular_doc", length = 16)
	private String titularDocument;
	@Column(name = "titular_nom", length = 160)
	private String titularNom;
	@Column(name = "titular_llinatge1", length = 160)
	private String titularLlinatge1;
	@Column(name = "titular_llinatge2", length = 160)
	private String titularLlinatge2;
	@Column(name = "titular_nom_sencer", length = 256)
	private String titularNomSencer;
	@Column(name = "funcionari_doc", length = 16)
	private String funcionariDocument;
	@Column(name = "funcionari_nom", length = 160)
	private String funcionariNom;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_generacio")
	private Date dataGeneracio;
	@Column(name = "procediment_codi", length = 256)
	private String procedimentCodi;
	@Column(name = "procediment_nom", length = 256)
	private String procedimentNom;
	@Column(name = "unitat_tramitadora", length = 256)
	private String unitatTramitadora;
	@Column(name = "finalitat", length = 256)
	private String finalitat;
	@Column(name = "consentiment", length = 3)
	private String consentiment;
	@Column(name = "estat", length = 4)
	private String estat;
	@Lob
	@Column(name = "error")
	private String error;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "peticio_id",
			foreignKey = @ForeignKey(name = "ems_redsol_peticio_fk"))
	private RedireccioPeticioEntity peticio;
	@Version
	private long version = 0;



	public String getSolicitudId() {
		return solicitudId;
	}
	public String getTransmisioId() {
		return transmisioId;
	}
	public String getSolicitantId() {
		return solicitantId;
	}
	public String getSolicitantCodi() {
		return solicitantCodi;
	}
	public String getSolicitantNom() {
		return solicitantNom;
	}
	public String getTitularTipusDoc() {
		return titularTipusDoc;
	}
	public String getTitularDocument() {
		return titularDocument;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public String getTitularLlinatge1() {
		return titularLlinatge1;
	}
	public String getTitularLlinatge2() {
		return titularLlinatge2;
	}
	public String getTitularNomSencer() {
		return titularNomSencer;
	}
	public String getFuncionariDocument() {
		return funcionariDocument;
	}
	public String getFuncionariNom() {
		return funcionariNom;
	}
	public Date getDataGeneracio() {
		return dataGeneracio;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public String getUnitatTramitadora() {
		return unitatTramitadora;
	}
	public String getFinalitat() {
		return finalitat;
	}
	public String getConsentiment() {
		return consentiment;
	}
	public String getEstat() {
		return estat;
	}
	public String getError() {
		return error;
	}
	public RedireccioPeticioEntity getPeticio() {
		return peticio;
	}

	public static Builder getBuilder(
			RedireccioPeticioEntity peticio,
			String solicitudId,
			String solicitantId) {
		return new Builder(
				peticio,
				solicitudId,
				solicitantId);
	}

	public static class Builder {
		RedireccioSolicitudEntity built;
		Builder(
				RedireccioPeticioEntity peticio,
				String solicitudId,
				String solicitantId) {
			built = new RedireccioSolicitudEntity();
			built.peticio = peticio;
			built.solicitudId = solicitudId;
			built.solicitantId = solicitantId;
		}
		public Builder transmisionId(String transmisioId) {
			built.transmisioId = transmisioId;
			return this;
		}
		public Builder solicitantCodi(String solicitantCodi) {
			built.solicitantCodi = solicitantCodi;
			return this;
		}
		public Builder solicitantNom(String solicitantNom) {
			built.solicitantNom = solicitantNom;
			return this;
		}
		public Builder titularTipusDoc(String titularTipusDoc) {
			built.titularTipusDoc = titularTipusDoc;
			return this;
		}
		public Builder titularDocument(String titularDocument) {
			built.titularDocument = titularDocument;
			return this;
		}
		public Builder titularNom(String titularNom) {
			built.titularNom = titularNom;
			return this;
		}
		public Builder titularLlinatge1(String titularLlinatge1) {
			built.titularLlinatge1 = titularLlinatge1;
			return this;
		}
		public Builder titularLlinatge2(String titularLlinatge2) {
			built.titularLlinatge2 = titularLlinatge2;
			return this;
		}
		public Builder titularNomSencer(String titularNomSencer) {
			built.titularNomSencer = titularNomSencer;
			return this;
		}
		public Builder funcionariDocument(String funcionariDocument) {
			built.funcionariDocument = funcionariDocument;
			return this;
		}
		public Builder funcionariNom(String funcionariNom) {
			built.funcionariNom = funcionariNom;
			return this;
		}
		public Builder dataGeneracio(Date dataGeneracio) {
			built.dataGeneracio = dataGeneracio;
			return this;
		}
		public Builder procedimentCodi(String procedimentCodi) {
			built.procedimentCodi = procedimentCodi;
			return this;
		}
		public Builder procedimentNom(String procedimentNom) {
			built.procedimentNom = procedimentNom;
			return this;
		}
		public Builder unitatTramitadora(String unitatTramitadora) {
			built.unitatTramitadora = unitatTramitadora;
			return this;
		}
		public Builder finalitat(String finalitat) {
			built.finalitat = finalitat;
			return this;
		}
		public Builder consentiment(String consentiment) {
			built.consentiment = consentiment;
			return this;
		}
		public Builder estat(String estat) {
			built.estat = estat;
			return this;
		}
		public Builder error(String error) {
			built.error = error;
			return this;
		}
		public RedireccioSolicitudEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dataGeneracio == null) ? 0 : dataGeneracio.hashCode());
		result = prime * result + ((peticio == null) ? 0 : peticio.hashCode());
		result = prime * result + ((solicitudId == null) ? 0 : solicitudId.hashCode());
		result = prime * result + ((transmisioId == null) ? 0 : transmisioId.hashCode());
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
		RedireccioSolicitudEntity other = (RedireccioSolicitudEntity) obj;
		if (dataGeneracio == null) {
			if (other.dataGeneracio != null)
				return false;
		} else if (!dataGeneracio.equals(other.dataGeneracio))
			return false;
		if (peticio == null) {
			if (other.peticio != null)
				return false;
		} else if (!peticio.equals(other.peticio))
			return false;
		if (solicitudId == null) {
			if (other.solicitudId != null)
				return false;
		} else if (!solicitudId.equals(other.solicitudId))
			return false;
		if (transmisioId == null) {
			if (other.transmisioId != null)
				return false;
		} else if (!transmisioId.equals(other.transmisioId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RedireccioSolicitudEntity [peticio=" + peticio + ", solicitudId=" + solicitudId + ", transmisioId="
				+ transmisioId + ", solicitantId=" + solicitantId + ", solicitantNom=" + solicitantNom
				+ ", titularDocument=" + titularDocument + ", titularNom=" + titularNom + ", titularLlinatge1="
				+ titularLlinatge1 + ", titularLlinatge2=" + titularLlinatge2 + ", titularNomSencer=" + titularNomSencer
				+ ", funcionariDocument=" + funcionariDocument + ", funcionariNom=" + funcionariNom + ", dataGeneracio="
				+ dataGeneracio + ", procedimentCodi=" + procedimentCodi + ", procedimentNom=" + procedimentNom + "]";
	}

}
