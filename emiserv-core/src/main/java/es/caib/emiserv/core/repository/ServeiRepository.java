/**
 * 
 */
package es.caib.emiserv.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.core.api.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.core.entity.ServeiEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiRepository extends JpaRepository<ServeiEntity, Long> {

	public ServeiEntity findByCodi(String codi);

	@Query(	"select " +
			"    sen " +
			"from " +
			"    ServeiEntity sen " +
			"where " +
			"    sen in (:serveisPermesos)")
	public Page<ServeiEntity> findPermesosPaginat(
			@Param("serveisPermesos") List<ServeiEntity> serveisPermesos,
			Pageable pageable);

	@Query(	"select " +
			"    sen " +
			"from " +
			"    ServeiEntity sen " +
			"where " +
			"    sen in (:serveisPermesos)")
	public List<ServeiEntity> findPermesosOrdenat(
			@Param("serveisPermesos") List<ServeiEntity> serveisPermesos,
			Sort sort);

	@Query(	"select " +
			"    sen " +
			"from " +
			"    ServeiEntity sen " +
			"where " +
			"    sen.codi in (:serveiCodis) " +
			"order by " +
			"    sen.nom asc")
	public List<ServeiEntity> findServeisPerCodis(
			@Param("serveiCodis") List<String> serveiCodis);

	public List<ServeiEntity> findByBackofficeCaibAsyncTipus(
			BackofficeAsyncTipusEnumDto backofficeCaibAsyncTipus);

}
