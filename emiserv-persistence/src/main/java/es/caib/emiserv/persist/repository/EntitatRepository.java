/**
 * 
 */
package es.caib.emiserv.persist.repository;

import es.caib.emiserv.persist.entity.EntitatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatRepository extends JpaRepository<EntitatEntity, Long> {

	public Optional<EntitatEntity> findByCodi(String codi);

	public Optional<EntitatEntity> findByCif(String cif);

	@Query(	"select " +
			"    en " +
			"from " +
			"    EntitatEntity en " +
			"where " +
			"    (:esNullCodi = true or lower(en.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(en.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullCif = true or lower(en.cif) like lower('%'||:cif||'%')) " +
			"and (:esNullUnitatArrel = true or lower(en.unitatArrel) like lower('%'||:unitatArrel||'%')) ")
	public Page<EntitatEntity> findPermesosPaginat(
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullCif") boolean esNullCif,
			@Param("cif") String cif,
			@Param("esNullUnitatArrel") boolean esNullUnitatArrel,
			@Param("unitatArrel") String unitatArrel,
			Pageable pageable);
}
