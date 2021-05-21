/**
 * 
 */
package es.caib.emiserv.persist.entity.scsp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Classe de model de dades que conté la informació d'un
 * organisme cesionari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "core_parametro_configuracion")
public class ScspCoreParametroConfiguracionEntity {

	@Id
	@Column(length = 64, nullable = false)
	private String nombre;
	@Column(length = 512, nullable = false)
	private String valor;
	@Column(length = 512)
	private String descripcion;

	public void update(
			String valor,
			String descripcion) {
		this.valor = valor;
		this.descripcion = descripcion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
		ScspCoreParametroConfiguracionEntity other = (ScspCoreParametroConfiguracionEntity)obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
