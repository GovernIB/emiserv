package es.caib.emiserv.persist.repository;

import es.caib.emiserv.persist.entity.RedireccioListEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface RedireccioListRepository extends JpaRepository<RedireccioListEntity, String> {

	@Query(	"from RedireccioListEntity rl " +
			"where " +
			"    (:esNullServei = true or rl.serveiCodi = :servei) " +
			"and (:esNullProcediment = true or rl.procedimentCodi = :procediment) " +
			"and (:esNullEstat = true or (:esError = true and substring(rl.estatScsp, 1, 2) <> '00') or (:esError = false and rl.estatScsp = :estat)) " +
			"and (:esNullDataInici = true or rl.dataPeticio >= :dataInici) " +
			"and (:esNullDataFi = true or rl.dataPeticio <= :dataFi) " +
			"and (:esNullPeticioId = true or lower(rl.peticioId) like lower('%'||:peticioId||'%'))")
    Page<RedireccioListEntity> findByFiltrePaginat(
			@Param("esNullProcediment") boolean esNullProcediment,
			@Param("procediment") String procediment,
			@Param("esNullServei") boolean esNullServei,
			@Param("servei") String servei,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("esError") boolean esError,
			@Param("estat") String estat,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullPeticioId") boolean esNullPeticioId,
			@Param("peticioId") String peticioId,
			Pageable pageable);

}
