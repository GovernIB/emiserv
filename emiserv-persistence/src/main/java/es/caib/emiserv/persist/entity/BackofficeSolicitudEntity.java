/**
 * 
 */
package es.caib.emiserv.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import java.util.Date;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto;

/**
 * Classe del model de dades que representa un missatge de sol·licitud
 * per enviar a un backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = AbstractAuditableEntity.TABLE_PREFIX + "_backoffice_sol")
@EntityListeners(AuditingEntityListener.class)
public class BackofficeSolicitudEntity extends AbstractPersistable<Long> {

	@Column(name = "solicitud_id", length = 256)
	private String solicitudId;
	@Column(name = "estat")
	private BackofficeSolicitudEstatEnumDto estat;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "peticio_id",
			foreignKey = @ForeignKey(name = "ems_backsol_backpet_fk"))
	private BackofficePeticioEntity peticio;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "comunicacio_id",
			foreignKey = @ForeignKey(name = "ems_backsol_backcom_fk"))
	private BackofficeComunicacioEntity comunicacioDarrera;
	/** Entitat (cedent) a la qual s'envia la sol·licitud (serveis enrutadors). */
	@Column(name = "entitat_codi", length = 64)
	private String entitatCodi;
	/** IdPeticion de la petició enviada al cedent (consultes asíncrones processades com a síncrones). */
	@Column(name = "peticio_desti_id", length = 26)
	private String peticioDestiId;
	@Column(name = "intents")
	private int intents;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_enviament")
	private Date dataEnviament;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_proper_intent")
	private Date dataProperIntent;
	@Column(name = "darrer_error", length = 4000)
	private String darrerError;
	/** Codi d'estat SCSP de les sol·licituds finalitzades amb error (es retorna a la transmissió). */
	@Column(name = "error_codi", length = 16)
	private String errorCodi;
	@Version
	private long version = 0;



	public BackofficePeticioEntity getPeticio() {
		return peticio;
	}
	public String getSolicitudId() {
		return solicitudId;
	}
	public BackofficeSolicitudEstatEnumDto getEstat() {
		return estat;
	}
	public BackofficeComunicacioEntity getComunicacioDarrera() {
		return comunicacioDarrera;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public String getPeticioDestiId() {
		return peticioDestiId;
	}
	public int getIntents() {
		return intents;
	}
	public Date getDataEnviament() {
		return dataEnviament;
	}
	public Date getDataProperIntent() {
		return dataProperIntent;
	}
	public String getDarrerError() {
		return darrerError;
	}
	public String getErrorCodi() {
		return errorCodi;
	}
	public boolean isFinalitzada() {
		return BackofficeSolicitudEstatEnumDto.TRAMITADA.equals(estat) || BackofficeSolicitudEstatEnumDto.ERROR.equals(estat);
	}
	/** El destí ha retornat la resposta de la sol·licitud. */
	public void marcarTramitada() {
		this.estat = BackofficeSolicitudEstatEnumDto.TRAMITADA;
		this.dataProperIntent = null;
		this.errorCodi = null;
	}
	/** Error definitiu (sense més reintents): la transmissió es retorna amb aquest codi d'estat. */
	public void finalitzarAmbError(String errorCodi, String error) {
		this.estat = BackofficeSolicitudEstatEnumDto.ERROR;
		this.errorCodi = errorCodi;
		this.darrerError = truncar(error);
		this.dataProperIntent = null;
	}
	public void updateEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public void updatePeticioDestiId(String peticioDestiId) {
		this.peticioDestiId = peticioDestiId;
	}
	/** Inici d'un intent d'enviament: la sol·licitud queda reservada (ENVIANT) per a aquest procés. */
	public void iniciarEnviament(Date ara) {
		this.estat = BackofficeSolicitudEstatEnumDto.ENVIANT;
		this.dataEnviament = ara;
		this.dataProperIntent = null;
		this.intents++;
	}
	/** Intent fallit que es tornarà a fer a partir de la data indicada. */
	public void reprogramar(String error, Date dataProperIntent) {
		this.estat = BackofficeSolicitudEstatEnumDto.PENDENT;
		this.darrerError = truncar(error);
		this.dataProperIntent = dataProperIntent;
	}
	public void updateDarrerError(String error) {
		this.darrerError = truncar(error);
	}
	/** Reintent manual: torna a PENDENT i reinicia el comptador d'intents. */
	public void reiniciarIntents() {
		this.estat = BackofficeSolicitudEstatEnumDto.PENDENT;
		this.intents = 0;
		this.dataProperIntent = null;
		this.errorCodi = null;
	}
	private static String truncar(String text) {
		return text != null && text.length() > 4000 ? text.substring(0, 4000) : text;
	}
	public void updateComunicacioDarrera(
			BackofficeComunicacioEntity comunicacioDarrera) {
		this.comunicacioDarrera = comunicacioDarrera;
	}
	public void updateEstat(
			BackofficeSolicitudEstatEnumDto estat) {
		this.estat = estat;
	}

	public static Builder getBuilder(
			BackofficePeticioEntity peticio,
			String solicitudId) {
		return new Builder(
				peticio,
				solicitudId);
	}

	public static class Builder {
		BackofficeSolicitudEntity built;
		Builder(
				BackofficePeticioEntity peticio,
				String solicitudId) {
			built = new BackofficeSolicitudEntity();
			built.peticio = peticio;
			built.solicitudId = solicitudId;
			built.estat = BackofficeSolicitudEstatEnumDto.PENDENT;
		}
		public Builder estat(BackofficeSolicitudEstatEnumDto estat) {
			built.estat = estat;
			return this;
		}
		public BackofficeSolicitudEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((comunicacioDarrera == null) ? 0 : comunicacioDarrera.hashCode());
		result = prime * result + ((estat == null) ? 0 : estat.hashCode());
		result = prime * result + ((peticio == null) ? 0 : peticio.hashCode());
		result = prime * result + ((solicitudId == null) ? 0 : solicitudId.hashCode());
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
		BackofficeSolicitudEntity other = (BackofficeSolicitudEntity) obj;
		if (comunicacioDarrera == null) {
			if (other.comunicacioDarrera != null)
				return false;
		} else if (!comunicacioDarrera.equals(other.comunicacioDarrera))
			return false;
		if (estat != other.estat)
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
		return true;
	}

}
