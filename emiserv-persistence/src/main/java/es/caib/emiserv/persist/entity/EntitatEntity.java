/**
 * 
 */
package es.caib.emiserv.persist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa un Servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = AbstractAuditableEntity.TABLE_PREFIX + "_entitat")
public class EntitatEntity extends AbstractPersistable<Long> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 255, nullable = false)
	private String nom;
	@Column(name = "cif", length = 9, nullable = false, unique = true)
	private String cif;
	@Column(name = "unitat_arrel", length = 9)
	private String unitatArrel;

	public void update(String codi, String nom, String cif, String unitatArrel) {
		this.codi = codi;
		this.nom = nom;
		this.cif = cif;
		this.unitatArrel = unitatArrel;
	}

}
