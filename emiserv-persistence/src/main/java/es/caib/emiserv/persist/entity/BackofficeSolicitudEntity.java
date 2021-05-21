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
import javax.persistence.Version;

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
