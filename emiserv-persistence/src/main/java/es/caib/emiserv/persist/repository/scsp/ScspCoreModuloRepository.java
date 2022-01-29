/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import es.caib.emiserv.persist.entity.scsp.ScspCoreModuloEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar
 * la informació d'activació relativa a un mòdul SCSP
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreModuloRepository extends JpaRepository<ScspCoreModuloEntity, String> {

	public List<ScspCoreModuloEntity> findAll();
	
	public Page<ScspCoreModuloEntity> findAll(Pageable pageable);

}
