/**
 * 
 */
package es.caib.emiserv.core.repository.scsp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.core.entity.scsp.ClauPublicaEntity;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una clau públicaque està emmagatzemada
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ClauPublicaRepository extends JpaRepository<ClauPublicaEntity, Long> {

	public List<ClauPublicaEntity> findAll();

	public ClauPublicaEntity findById(Long id);

}
