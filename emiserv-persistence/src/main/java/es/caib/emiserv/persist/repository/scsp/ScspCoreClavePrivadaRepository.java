/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.scsp.ScspCoreClavePrivadaEntity;


/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar
 * la informació relativa a una clau privada que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreClavePrivadaRepository extends JpaRepository<ScspCoreClavePrivadaEntity, Long> {

	public List<ScspCoreClavePrivadaEntity> findAll();
	
	public Page<ScspCoreClavePrivadaEntity> findAll(Pageable pageable);

}
