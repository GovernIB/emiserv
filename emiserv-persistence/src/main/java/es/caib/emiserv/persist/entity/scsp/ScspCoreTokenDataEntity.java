/**
 * 
 */
package es.caib.emiserv.persist.entity.scsp;

import javax.persistence.*;

/**
 *  Classe de model de dades per a la taula CORE_TOKEN_DATA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_token_data")
public class ScspCoreTokenDataEntity {

	@Id
	@Column(name = "idpeticion", length = 26, nullable = false)
	private String peticionId;
	// SCSP tipo mensaje: 0-peticion, 1-confirmacionPeticion, 2-solicitudRespuesta, 3-respuesta, 4-fault
	@Column(name = "tipomensaje", length = 64, nullable = false)
	private int tipoMensaje;
	@Lob
	@Column(name = "datos")
	private String datos;



	public String getPeticionId() {
		return peticionId;
	}
	public int getTipoMensaje() {
		return tipoMensaje;
	}
	public String getDatos() {
		return datos;
	}
	public void setDatos(String datos) {
		this.datos = datos;
	}

	public static Builder getBuilder(
			String peticionId,
			int tipoMensaje,
			String datos) {
		return new Builder(
				peticionId,
				tipoMensaje,
				datos);
	}

	public static class Builder {
		ScspCoreTokenDataEntity built;
		Builder(
				String peticionId,
				int tipoMensaje,
				String datos) {
			built = new ScspCoreTokenDataEntity();
			built.peticionId = peticionId;
			built.tipoMensaje = tipoMensaje;
			built.datos = datos;
		}
		public ScspCoreTokenDataEntity build() {
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
		ScspCoreTokenDataEntity other = (ScspCoreTokenDataEntity) obj;
		if (peticionId == null) {
			if (other.peticionId != null)
				return false;
		} else if (!peticionId.equals(other.peticionId))
			return false;
		return true;
	}

}
