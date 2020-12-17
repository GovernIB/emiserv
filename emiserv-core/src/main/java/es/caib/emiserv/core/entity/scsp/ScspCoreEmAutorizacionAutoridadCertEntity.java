/**
 * 
 */
package es.caib.emiserv.core.entity.scsp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Classe de model de dades per a la taula CORE_EM_AUTORIZACION_CA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "core_em_autorizacion_ca")
public class ScspCoreEmAutorizacionAutoridadCertEntity {

	@Id
	@GenericGenerator(
			name = "sequence",
			strategy = "native",
			parameters = {
					@org.hibernate.annotations.Parameter(
							name = "sequence_name",
							value = "ID_AUTORIZACION_CA_SEQ")
			})
	@GeneratedValue(generator="sequence")
	private Long id;
	@Column(length = 512)
	private String codca;
	@Column(length = 512)
	private String nombre;

	public void update(String codca, String nombre) {
		this.codca = codca;
		this.nombre = nombre;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codca == null) ? 0 : codca.hashCode());
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
		ScspCoreEmAutorizacionAutoridadCertEntity other = (ScspCoreEmAutorizacionAutoridadCertEntity) obj;
		if (codca == null) {
			if (other.codca != null)
				return false;
		} else if (!codca.equals(other.codca))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}

}
