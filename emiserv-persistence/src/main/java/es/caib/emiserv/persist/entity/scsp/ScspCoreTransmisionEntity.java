/**
 * 
 */
package es.caib.emiserv.persist.entity.scsp;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 *  Classe de model de dades per a la taula CORE_TRANSMISION.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_transmision")
public class ScspCoreTransmisionEntity {

	@Id
	@Column(name = "idpeticion", length = 26, nullable = false)
	private String peticionId;
	@Column(name = "idsolicitud", length = 64, nullable = false)
	private String solicitudId;
	@Column(name = "idtransmision", length = 64)
	private String transmisionId;
	@Column(name = "idsolicitante", length = 10, nullable = false)
	private String solicitanteId;
	@Column(name = "nombresolicitante", length = 256)
	private String solicitanteNombre;
	@Column(name = "doctitular", length = 16)
	private String titularDocumento;
	@Column(name = "nombretitular", length = 40)
	private String titularNombre;
	@Column(name = "apellido1titular", length = 40)
	private String titularApellido1;
	@Column(name = "apellido2titular", length = 40)
	private String titularApellido2;
	@Column(name = "nombrecompletotitular", length = 40)
	private String titularNombreCompleto;
	@Column(name = "docfuncionario", length = 16)
	private String funcionarioDocumento;
	@Column(name = "nombrefuncionario", length = 128)
	private String funcionarioNombre;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fechageneracion", nullable = false)
	private Date fechaGeneracion;
	@Column(name = "unidadtramitadora", length = 256)
	private String unidadTramitadora;
	@Column(name = "codigoprocedimiento", length = 256)
	private String procedimientoCodigo;
	@Column(name = "nombreprocedimiento", length = 256)
	private String procedimientoNombre;
	@Column(name = "expediente", length = 256)
	private String expediente;
	@Column(name = "finalidad", length = 256)
	private String finalidad;
	@Column(name = "consentimiento", length = 256)
	private String consentimiento;
	@Column(name = "estado", length = 256)
	private String estado;
	@Column(name = "error", length = 256)
	private String error;
	@Lob
	@Column(name = "xmltransmision")
	private String xmltransmision;
	@ManyToOne(
			optional = true,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "idpeticion", insertable = false, updatable = false)
	protected ScspCorePeticionRespuestaEntity peticionRespuesta;



	public String getPeticionId() {
		return peticionId;
	}
	public String getSolicitudId() {
		return solicitudId;
	}
	public String getTransmisionId() {
		return transmisionId;
	}
	public String getSolicitanteId() {
		return solicitanteId;
	}
	public String getSolicitanteNombre() {
		return solicitanteNombre;
	}
	public String getTitularDocumento() {
		return titularDocumento;
	}
	public String getTitularNombre() {
		return titularNombre;
	}
	public String getTitularApellido1() {
		return titularApellido1;
	}
	public String getTitularApellido2() {
		return titularApellido2;
	}
	public String getTitularNombreCompleto() {
		return titularNombreCompleto;
	}
	public String getFuncionarioDocumento() {
		return funcionarioDocumento;
	}
	public String getFuncionarioNombre() {
		return funcionarioNombre;
	}
	public Date getFechaGeneracion() {
		return fechaGeneracion;
	}
	public String getUnidadTramitadora() {
		return unidadTramitadora;
	}
	public String getProcedimientoCodigo() {
		return procedimientoCodigo;
	}
	public String getProcedimientoNombre() {
		return procedimientoNombre;
	}
	public String getExpediente() {
		return expediente;
	}
	public String getFinalidad() {
		return finalidad;
	}
	public String getConsentimiento() {
		return consentimiento;
	}
	public String getEstado() {
		return estado;
	}
	public String getError() {
		return error;
	}
	public String getXmltransmision() {
		return xmltransmision;
	}
	public ScspCorePeticionRespuestaEntity getPeticionRespuesta() {
		return peticionRespuesta;
	}

	public static Builder getBuilder(
			String certificado,
			String peticionId,
			String estado) {
		return new Builder(
				certificado,
				peticionId,
				estado);
	}

	public static class Builder {
		ScspCoreTransmisionEntity built;
		Builder(
				String peticionId,
				String solicitudId,
				String transmisionId) {
			built = new ScspCoreTransmisionEntity();
			built.peticionId = peticionId;
			built.solicitudId = solicitudId;
			built.transmisionId = transmisionId;
			built.fechaGeneracion = new Date();
		}
		public Builder solicitanteId(String solicitanteId) {
			built.solicitanteId = solicitanteId;
			return this;
		}
		public Builder solicitanteNombre(String solicitanteNombre) {
			built.solicitanteNombre = solicitanteNombre;
			return this;
		}
		public Builder titularDocumento(String titularDocumento) {
			built.titularDocumento = titularDocumento;
			return this;
		}
		public Builder titularNombre(String titularNombre) {
			built.titularNombre = titularNombre;
			return this;
		}
		public Builder titularApellido1(String titularApellido1) {
			built.titularApellido1 = titularApellido1;
			return this;
		}
		public Builder titularApellido2(String titularApellido2) {
			built.titularApellido2 = titularApellido2;
			return this;
		}
		public Builder titularNombreCompleto(String titularNombreCompleto) {
			built.titularNombreCompleto = titularNombreCompleto;
			return this;
		}
		public Builder funcionarioDocumento(String funcionarioDocumento) {
			built.funcionarioDocumento = funcionarioDocumento;
			return this;
		}
		public Builder funcionarioNombre(String funcionarioNombre) {
			built.funcionarioNombre = funcionarioNombre;
			return this;
		}
		public Builder fechaGeneracion(Date fechaGeneracion) {
			built.fechaGeneracion = fechaGeneracion;
			return this;
		}
		public Builder unidadTramitadora(String unidadTramitadora) {
			built.unidadTramitadora = unidadTramitadora;
			return this;
		}
		public Builder procedimientoCodigo(String procedimientoCodigo) {
			built.procedimientoCodigo = procedimientoCodigo;
			return this;
		}
		public Builder procedimientoNombre(String procedimientoNombre) {
			built.procedimientoNombre = procedimientoNombre;
			return this;
		}
		public Builder expediente(String expediente) {
			built.expediente = expediente;
			return this;
		}
		public Builder finalidad(String finalidad) {
			built.finalidad = finalidad;
			return this;
		}
		public Builder consentimiento(String consentimiento) {
			built.consentimiento = consentimiento;
			return this;
		}
		public ScspCoreTransmisionEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((peticionId == null) ? 0 : peticionId.hashCode());
		result = prime * result
				+ ((solicitudId == null) ? 0 : solicitudId.hashCode());
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
		ScspCoreTransmisionEntity other = (ScspCoreTransmisionEntity) obj;
		if (peticionId == null) {
			if (other.peticionId != null)
				return false;
		} else if (!peticionId.equals(other.peticionId))
			return false;
		if (solicitudId == null) {
			if (other.solicitudId != null)
				return false;
		} else if (!solicitudId.equals(other.solicitudId))
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
