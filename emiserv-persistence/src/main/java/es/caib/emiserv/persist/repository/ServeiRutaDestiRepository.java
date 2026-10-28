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

	public List<ServeiRutaDestiEntity> findByServeiAndEntitatCodiOrderByOrdreAsc(
			ServeiEntity servei,
			String entitatCodi);

	/**
	 * Ruta d'una entitat. Si n'hi ha més d'una per a la mateixa entitat (dades duplicades), la primera
	 * per ordre (findByServeiAndEntitatCodi falla amb un resultat no únic).
	 */
	public default ServeiRutaDestiEntity findPrimeraByServeiAndEntitatCodi(
			ServeiEntity servei,
			String entitatCodi) {
		List<ServeiRutaDestiEntity> rutes = findByServeiAndEntitatCodiOrderByOrdreAsc(servei, entitatCodi);
		return rutes.isEmpty() ? null : rutes.get(0);
	}

	/** Troba totes les rutes per a un servei ordenades per l'ordre donat a la columna ordre. */
	public List<ServeiRutaDestiEntity> findByServeiOrderByOrdreAsc(ServeiEntity servei);

	/** Totes les rutes d'un conjunt de serveis (màxim 1000 ids per l'IN d'Oracle). */
	public List<ServeiRutaDestiEntity> findByServeiIdIn(java.util.Collection<Long> serveiIds);

	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( r.ordre), -1) + 1 " +
			"from ServeiRutaDestiEntity r " +
			"where " +
			"    r.servei.id = :serveiId " )	
	public Long getNextOrdre(@Param("serveiId") Long serveiId);

}
