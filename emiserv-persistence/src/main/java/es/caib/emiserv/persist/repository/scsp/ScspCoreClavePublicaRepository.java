/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.scsp.ScspCoreClavePublicaEntity;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una clau públicaque està emmagatzemada
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreClavePublicaRepository extends JpaRepository<ScspCoreClavePublicaEntity, Long> {

	public List<ScspCoreClavePublicaEntity> findAll();

}
