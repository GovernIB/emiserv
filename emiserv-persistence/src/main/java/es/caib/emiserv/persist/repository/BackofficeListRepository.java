package es.caib.emiserv.persist.repository;

import es.caib.emiserv.persist.entity.BackofficeListEntity;
import es.caib.emiserv.persist.entity.OpenDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BackofficeListRepository extends JpaRepository<BackofficeListEntity, String> {

	@Query(	"from BackofficeListEntity bl " +
			"where " +
			"    (:esNullServei = true or bl.serveiCodi = :servei) " +
			"and (:esNullProcediment = true or bl.procedimentCodi = :procediment) " +
			"and (:esNullEstat = true or (:esError = true and substring(bl.estatScsp, 1, 2) <> '00') or (:esError = false and bl.estatScsp = :estat)) " +
			"and (:esNullDataInici = true or bl.dataPeticio >= :dataInici) " +
			"and (:esNullDataFi = true or bl.dataPeticio <= :dataFi) " +
			"and (:nomesServeisPermesos = false or bl.serveiCodi in (:serveisPermesos)) " +
			"and (:esNullPeticioId = true or lower(bl.peticioId) like lower('%'||:peticioId||'%'))")
    Page<BackofficeListEntity> findByFiltrePaginat(
			@Param("esNullProcediment") boolean esNullProcediment,
			@Param("procediment") String procediment,
			@Param("esNullServei") boolean esNullServei,
			@Param("servei") Long servei,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("esError") boolean esError,
			@Param("estat") String estat,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("nomesServeisPermesos") boolean nomesServeisPermesos,
			@Param("serveisPermesos") List<String> serveisPermesos,
			@Param("esNullPeticioId") boolean esNullPeticioId,
			@Param("peticioId") String peticioId,
			Pageable pageable);

}
