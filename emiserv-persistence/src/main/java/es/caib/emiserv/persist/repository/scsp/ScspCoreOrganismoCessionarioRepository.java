/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.persist.entity.scsp.ScspCoreOrganismoCessionarioEntity;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar
 * la informació relativa a l'organisme cessionari que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreOrganismoCessionarioRepository extends JpaRepository<ScspCoreOrganismoCessionarioEntity, Long> {

	@Query(	"from " +
			"    ScspCoreOrganismoCessionarioEntity scoc " +
			"where " +
			"    (:esNullNom = true or lower(scoc.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullCif = true or lower(scoc.cif) like lower('%'||:cif||'%'))")
	Page<ScspCoreOrganismoCessionarioEntity> findByFiltrePaginat(
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullCif") boolean esNullCif,
			@Param("cif") String cif,
			Pageable pageable);

	@Query(	"from " +
			"    ScspCoreOrganismoCessionarioEntity scoc " +
			"where " +
			"    (lower(scoc.cif) = lower(:cif))")
	ScspCoreOrganismoCessionarioEntity findByCif(
			@Param("cif") String cif);

}
