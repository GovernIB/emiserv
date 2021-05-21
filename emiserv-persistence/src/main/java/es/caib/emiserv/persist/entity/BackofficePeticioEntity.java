/**
 * 
 */
package es.caib.emiserv.persist.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;

/**
 * Classe del model de dades que representa un missatge de petició
 * a algun backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = AbstractAuditableEntity.TABLE_PREFIX + "_backoffice_pet")
@EntityListeners(AuditingEntityListener.class)
public class BackofficePeticioEntity extends AbstractPersistable<Long> {

	@Column(name = "estat", nullable = false)
	private PeticioEstatEnumDto estat;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ter_data", nullable = false)
	private Date tempsEstimatRespostaData;
	@Column(name = "darrera_sol_id", length = 256)
	private String darreraSolicitudId;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "darrera_sol_data")
	private Date darreraSolicitudData;
	@Column(name = "processades_total")
	private int processadesTotal;
	@Column(name = "processades_error")
	private int processadesError;
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinColumn(
			name = "peticio_id",
			foreignKey = @ForeignKey(name = "ems_backpet_scsppet_fk"))
	private ScspCorePeticionRespuestaEntity scspPeticionRespuesta;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "comunicacio_id",
			foreignKey = @ForeignKey(name = "ems_backpet_backcom_fk"))
	private BackofficeComunicacioEntity comunicacioDarrera;
	@OneToMany(
			mappedBy = "peticio",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<BackofficeComunicacioEntity> comunicacions = new ArrayList<BackofficeComunicacioEntity>();
	@Version
	private long version = 0;



	public PeticioEstatEnumDto getEstat() {
		return estat;
	}
	public Date getTempsEstimatRespostaData() {
		return tempsEstimatRespostaData;
	}
	public String getDarreraSolicitudId() {
		return darreraSolicitudId;
	}
	public Date getDarreraSolicitudData() {
		return darreraSolicitudData;
	}
	public int getProcessadesTotal() {
		return processadesTotal;
	}
	public int getProcessadesError() {
		return processadesError;
	}
	public ScspCorePeticionRespuestaEntity getScspPeticionRespuesta() {
		return scspPeticionRespuesta;
	}
	public BackofficeComunicacioEntity getComunicacioDarrera() {
		return comunicacioDarrera;
	}

	public void updateSolicitudProcessada(
			String darreraSolicitudId,
			boolean error) {
		this.darreraSolicitudId = darreraSolicitudId;
		this.darreraSolicitudData = new Date();
		processadesTotal++;
		if (error)
			processadesError++;
	}
	public void updateEstat(
			PeticioEstatEnumDto estat) {
		this.estat = estat;
	}
	public void updateComunicacioDarrera(
			BackofficeComunicacioEntity comunicacioDarrera) {
		this.comunicacioDarrera = comunicacioDarrera;
	}

	public static Builder getBuilder(
			ScspCorePeticionRespuestaEntity scspPeticionRespuesta,
			Date tempsEstimatRespostaData) {
		return new Builder(
				scspPeticionRespuesta,
				tempsEstimatRespostaData);
	}

	public static class Builder {
		BackofficePeticioEntity built;
		Builder(
				ScspCorePeticionRespuestaEntity scspPeticionRespuesta,
				Date tempsEstimatRespostaData) {
			built = new BackofficePeticioEntity();
			built.scspPeticionRespuesta = scspPeticionRespuesta;
			built.tempsEstimatRespostaData = tempsEstimatRespostaData;
			built.estat = PeticioEstatEnumDto.PENDENT;
		}
		public Builder estat(PeticioEstatEnumDto estat) {
			built.estat = estat;
			return this;
		}
		public BackofficePeticioEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((darreraSolicitudData == null) ? 0 : darreraSolicitudData.hashCode());
		result = prime * result + ((darreraSolicitudId == null) ? 0 : darreraSolicitudId.hashCode());
		result = prime * result + ((estat == null) ? 0 : estat.hashCode());
		result = prime * result + ((scspPeticionRespuesta == null) ? 0 : scspPeticionRespuesta.hashCode());
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
		BackofficePeticioEntity other = (BackofficePeticioEntity) obj;
		if (darreraSolicitudData == null) {
			if (other.darreraSolicitudData != null)
				return false;
		} else if (!darreraSolicitudData.equals(other.darreraSolicitudData))
			return false;
		if (darreraSolicitudId == null) {
			if (other.darreraSolicitudId != null)
				return false;
		} else if (!darreraSolicitudId.equals(other.darreraSolicitudId))
			return false;
		if (estat != other.estat)
			return false;
		if (scspPeticionRespuesta == null) {
			if (other.scspPeticionRespuesta != null)
				return false;
		} else if (!scspPeticionRespuesta.equals(other.scspPeticionRespuesta))
			return false;
		return true;
	}

}
