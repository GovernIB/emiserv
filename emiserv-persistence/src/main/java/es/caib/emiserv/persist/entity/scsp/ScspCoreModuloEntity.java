/**
 * 
 */
package es.caib.emiserv.persist.entity.scsp;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Classe de model de dades que conté la informació d'activació d'un mòdul
 *
 * @author Limit Tecnologies <limit@limit.es>
 */

@SuppressWarnings("deprecation")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "core_modulo")
@EntityListeners(AuditingEntityListener.class)
public class ScspCoreModuloEntity implements Serializable {
	
	@Id
	@Column(name = "nombre", length = 256, nullable = false)
	private String nom;
	
	@Column(name = "descripcion", length = 256)
	private String descripcio;
	
	@Column(name = "activoentrada")
	private boolean actiuEntrada;

	@Column(name = "activosalida")
	private boolean actiuSortida;
	
	public void update(
			boolean actiuEntrada,
			boolean actiuSortida) {
		this.actiuEntrada = actiuEntrada;
		this.actiuSortida = actiuSortida;
	}

	private static final long serialVersionUID = 6778107174821528682L;
	
}
