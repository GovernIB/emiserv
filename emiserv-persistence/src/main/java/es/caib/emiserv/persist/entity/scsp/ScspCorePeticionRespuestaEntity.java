/**
 * 
 */
package es.caib.emiserv.persist.entity.scsp;

import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 *  Classe de model de dades per a la taula CORE_PETICIONRESPUESTA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_peticion_respuesta")
public class ScspCorePeticionRespuestaEntity {

	@Id
	@Column(name = "idpeticion", length = 26, nullable = false)
	private String peticionId;
	@Column(name = "certificado")
	private Long certificado;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechapeticion", nullable = false)
	private Date fechaPeticion;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecharespuesta")
	private Date fechaRespuesta;
	@Column(name = "estado", length = 4)
	private String estado;
	@Lob
	@Column(name = "error")
	private String error;
	@Column(name = "numeroenvios")
	private int numEnvios;
	@Column(name = "numerotransmisiones")
	private int numTransmisiones;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechaultimosondeo")
	private Date fechaUltimoSondeo;
	@Column(name = "transmisionsincrona")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean sincrona;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ter")
	private Date fechaEstimadaRespuesta;
	@ManyToOne(
			optional = true,
			fetch = FetchType.LAZY)
	@JoinColumn(name = "certificado", insertable = false, updatable = false)
	protected ScspCoreServicioEntity servicio;

	// Dades afegides
	@Column(name = "emscodiprocediment")
	private String codiProcediment;
	@Column(name = "emsnomprocediment")
	private String nomProcediment;
	@Column(name = "emscodiservei")
	private String codiServei;
	@Column(name = "emsnomservei")
	private String nomServei;
//	@Column(name = "emsprocessadestotal")
//	private Integer processadesTotal;
	@Column(name = "emsestat")
	@Enumerated(EnumType.ORDINAL)
	private PeticioEstatEnumDto estat;



	public String getPeticionId() {
		return peticionId;
	}
	public Long getCertificado() {
		return certificado;
	}
	public Date getFechaPeticion() {
		return fechaPeticion;
	}
	public Date getFechaRespuesta() {
		return fechaRespuesta;
	}
	public String getEstado() {
		return estado;
	}
	public String getError() {
		return error;
	}
	public int getNumEnvios() {
		return numEnvios;
	}
	public int getNumTransmisiones() {
		return numTransmisiones;
	}
	public Date getFechaUltimoSondeo() {
		return fechaUltimoSondeo;
	}
	public boolean isSincrona() {
		return sincrona;
	}
	public Date getFechaEstimadaRespuesta() {
		return fechaEstimadaRespuesta;
	}
	public ScspCoreServicioEntity getServicio() {
		return servicio;
	}
	public String getCodiProcediment() {
		return codiProcediment;
	}
	public String getNomProcediment() {
		return nomProcediment;
	}
	public String getCodiServei() {
		return codiServei;
	}
	public String getNomServei() {
		return nomServei;
	}
	public PeticioEstatEnumDto getEstat() {
		return estat;
	}
//	public Integer getProcessadesTotal() {
//		return processadesTotal;
//	}

	public void updateEstat(PeticioEstatEnumDto estat) {
		this.estat = estat;
	}

//	public void updateProcessadesTotal(Integer processadesTotal) {
//		this.processadesTotal = processadesTotal;
//	}

	public static Builder getBuilder(
			Long certificado,
			String peticionId,
			String estado) {
		return new Builder(
				certificado,
				peticionId,
				estado);
	}

	public static class Builder {
		ScspCorePeticionRespuestaEntity built;
		Builder(
				Long certificado,
				String peticionId,
				String estado) {
			built = new ScspCorePeticionRespuestaEntity();
			built.certificado = certificado;
			built.peticionId = peticionId;
			built.estado = estado;
			built.fechaPeticion = new Date();
		}
		public ScspCorePeticionRespuestaEntity build() {
			return built;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((peticionId == null) ? 0 : peticionId.hashCode());
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
		ScspCorePeticionRespuestaEntity other = (ScspCorePeticionRespuestaEntity) obj;
		if (peticionId == null) {
			if (other.peticionId != null)
				return false;
		} else if (!peticionId.equals(other.peticionId))
			return false;
		return true;
	}

	public class ScspCoreTransmisionPk implements Serializable {
		private String peticionId;
		private String solicitudId;
		public ScspCoreTransmisionPk() {}
		public ScspCoreTransmisionPk(String peticionId, String solicitudId) {
			this.peticionId = peticionId;
			this.solicitudId = solicitudId;
		}
		public String getPeticionId() {
			return peticionId;
		}
		public String getSolicitudId() {
			return solicitudId;
		}
		private static final long serialVersionUID = 7890819734930461845L;
	}

}
