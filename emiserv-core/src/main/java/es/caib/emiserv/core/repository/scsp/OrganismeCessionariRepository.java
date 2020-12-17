/**
 * 
 */
package es.caib.emiserv.core.repository.scsp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.core.entity.scsp.OrganismeCessionariEntity;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar
 * la informació relativa a l'organisme cessionari que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganismeCessionariRepository extends JpaRepository<OrganismeCessionariEntity, Long> {

	public List<OrganismeCessionariEntity> findAll();
	
	public OrganismeCessionariEntity findById(Long id);
	
	public OrganismeCessionariEntity findByCif(String id);

}
