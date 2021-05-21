/**
 * 
 */
package es.caib.emiserv.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus serveiRutaDesti.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiRutaDestiRepository extends JpaRepository<ServeiRutaDestiEntity, Long> {

	public Page<ServeiRutaDestiEntity> findByServei(
			ServeiEntity servei,
			Pageable pageable);

	public ServeiRutaDestiEntity findByServeiAndEntitatCodi(
			ServeiEntity servei,
			String entitatCodi);

	/** Troba totes les rutes per a un servei ordenades per l'ordre donat a la columna ordre. */
	public List<ServeiRutaDestiEntity> findByServeiOrderByOrdreAsc(ServeiEntity servei);

	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( r.ordre), -1) + 1 " +
			"from ServeiRutaDestiEntity r " +
			"where " +
			"    r.servei.id = :serveiId " )	
	public Long getNextOrdre(@Param("serveiId") Long serveiId);

}
