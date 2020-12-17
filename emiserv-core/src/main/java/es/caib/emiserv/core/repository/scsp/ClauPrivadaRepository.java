/**
 * 
 */
package es.caib.emiserv.core.repository.scsp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.core.entity.scsp.ClauPrivadaEntity;


/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar
 * la informació relativa a una clau privada que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ClauPrivadaRepository extends JpaRepository<ClauPrivadaEntity, Long> {

	public List<ClauPrivadaEntity> findAll();
	
	public Page<ClauPrivadaEntity> findAll(Pageable pageable);

	public ClauPrivadaEntity findById(Long id);

}
