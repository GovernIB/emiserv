/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.scsp.ScspCoreOrganismoCessionarioEntity;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar
 * la informació relativa a l'organisme cessionari que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreOrganismoCessionarioRepository extends JpaRepository<ScspCoreOrganismoCessionarioEntity, Long> {

	public List<ScspCoreOrganismoCessionarioEntity> findAll();
	
	public ScspCoreOrganismoCessionarioEntity findByCif(String id);

}
