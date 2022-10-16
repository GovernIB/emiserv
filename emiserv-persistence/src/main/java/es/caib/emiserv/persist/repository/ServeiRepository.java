/**
 * 
 */
package es.caib.emiserv.persist.repository;

import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.persist.entity.ServeiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
			"    sen in (:serveisPermesos) " +
			"and (:esNullCodi = true or lower(sen.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(sen.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullTipus = true or sen.tipus = :tipus) " +
			"and (:esNullActiu = true or sen.actiu = :actiu) ")
	public Page<ServeiEntity> findPermesosPaginat(
			@Param("serveisPermesos") List<ServeiEntity> serveisPermesos,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullTipus") boolean esNullTipus,
			@Param("tipus") ServeiTipusEnumDto tipus,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			Pageable pageable);

	@Query(	"select " +
			"    sen " +
			"from " +
			"    ServeiEntity sen " +
			"where " +
			"    sen in (:serveisPermesos) " +
			"and (:esNullCodi = true or lower(sen.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(sen.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullTipus = true or sen.tipus = :tipus) " +
			"and (:esNullActiu = true or sen.actiu = :actiu) ")
	public List<ServeiEntity> findPermesosOrdenat(
			@Param("serveisPermesos") List<ServeiEntity> serveisPermesos,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullTipus") boolean esNullTipus,
			@Param("tipus") ServeiTipusEnumDto tipus,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
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
