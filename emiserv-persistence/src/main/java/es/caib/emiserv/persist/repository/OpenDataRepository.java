package es.caib.emiserv.persist.repository;

import es.caib.emiserv.persist.entity.OpenDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface OpenDataRepository extends JpaRepository<OpenDataEntity, Long> {

	@Query("select count(o) " +
			"from OpenDataEntity o " +
			" where (:esNullEntitatNif = true or o.solicitantId = :entitatNif) " +
			"	and (:esNullProcedimentId = true or o.procedimentCodi = :procedimentId) " +
			"	and (:esNullServeiCodi = true or o.serveiCodi = :serveiCodi) " +
			"	and (:esNullTipus = true or " +
			"		((:esTipusBackoffice = true and o.tipus = es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.BACKOFFICE) " +
			"		or (:esTipusBackoffice = false and o.tipus <> es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.BACKOFFICE))) " +
			"	and (:esNullDataInici = true or o.dataPeticio >= :dataInici) " +
			"	and (:esNullDataFi = true or o.dataPeticio <= :dataFi) ")
	Integer countByFiltre(
			@Param("esNullEntitatNif") boolean esNullEntitatNif,
			@Param("entitatNif") String entitatNif,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") String procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullTipus") boolean esNullTipus,
			@Param("esTipusBackoffice") boolean esTipusBackoffice,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

    @Query("from OpenDataEntity o" +
            " where (:esNullEntitatNif = true or o.solicitantId = :entitatNif) " +
			"	and (:esNullProcedimentId = true or o.procedimentCodi = :procedimentId) " +
			"	and (:esNullServeiCodi = true or o.serveiCodi = :serveiCodi) " +
			"	and (:esNullTipus = true or " +
			"		((:esTipusBackoffice = true and o.tipus = es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.BACKOFFICE) " +
			"		or (:esTipusBackoffice = false and o.tipus <> es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.BACKOFFICE))) " +
			"	and (:esNullDataInici = true or o.dataPeticio >= :dataInici) " +
			"	and (:esNullDataFi = true or o.dataPeticio <= :dataFi) ")
    Page<OpenDataEntity> findByFiltre(
			@Param("esNullEntitatNif") boolean esNullEntitatNif,
			@Param("entitatNif") String entitatNif,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") String procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullTipus") boolean esNullTipus,
			@Param("esTipusBackoffice") boolean esTipusBackoffice,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			Pageable pageable);

}
