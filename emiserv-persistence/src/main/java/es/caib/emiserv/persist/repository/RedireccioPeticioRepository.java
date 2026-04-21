/**
 * 
 */
package es.caib.emiserv.persist.repository;

import es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.emiserv.logic.intf.dto.CarregaDto;
import es.caib.emiserv.logic.intf.dto.EstadisticaDto;
import es.caib.emiserv.logic.intf.dto.InformeEmisorEnrutatDto;
import es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.persist.entity.RedireccioPeticioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus redireccioPeticio.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RedireccioPeticioRepository extends JpaRepository<RedireccioPeticioEntity, Long> {

	@Query(	"from " +
			"    RedireccioPeticioEntity rp " +
			"where " +
			"    (:esNullServei = true or rp.serveiCodi = :servei) " +
			"and (:esNullProcediment = true or rp.procedimentCodi  = :procediment) " +
			"and (:esNullEstat = true or rp.estatEnum = :estat) " +
			"and (:esNullDataInici = true or rp.dataPeticio >= :dataInici) " +
			"and (:esNullDataFi = true or rp.dataPeticio <= :dataFi)" +
			"and (:esNullPeticioId = true or lower(rp.peticioId) like lower('%'||:peticioId||'%'))")
	Page<RedireccioPeticioEntity> findByFiltrePaginat(
			@Param("esNullProcediment") boolean esNullProcediment,
			@Param("procediment") String procediment,
			@Param("esNullServei") boolean esNullServei,
			@Param("servei") String servei,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") PeticioEstatEnumDto estat,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullPeticioId") boolean esNullPeticioId,
			@Param("peticioId") String peticioId,
			Pageable pageable);

	List<RedireccioPeticioEntity> findByPeticioIdAndServeiCodi(
			@Param("peticioId") String peticioId,
			@Param("serveiCodi") String serveiCodi);

	long countByServeiCodi(String serveiCodi);

	@Query(	"select " +
			"    distinct rdp.serveiCodi " +
			"from " +
			"    RedireccioPeticioEntity rdp")
	List<String> findServeiDistint();


	/** Consulta els enrutadors simples i múltiples filtrant per tipus != 0
	 *
	 * @param dataInici
	 * @param dataFi
	 * @return
	 */
	@Query( "select new es.caib.emiserv.logic.intf.dto.InformeGeneralEstatDto( " +
			"		s.tipus, " +
			"		rs.solicitantNom, " +
			"		rs.solicitantId, " +
			"		rs.unitatTramitadora, " +
			"		rs.procedimentCodi, " +
			"		rs.procedimentNom, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		rp.emissorCodi, " +
			"		sum(case " +
			"			when substring(rp.estat,0,2) = '00' then 1 " +
			"			else 0 " +
			"		end) as correcte, " +
			"		sum(case " +
			"			when substring(rp.estat,0,2) = '00' then 0 " +
			"			else 1 " +
			"		end) as error " +
			"	) " +
			"	from " +
			"		RedireccioSolicitudEntity as rs " +
			"			inner join rs.peticio as rp, " +
			"		ServeiEntity s " +
			"	where rp.serveiCodi = s.codi " +
			"		and s.tipus != es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.BACKOFFICE " +
 			"		and rp.dataPeticio >= :dataInici " +
			"		and rp.dataPeticio < :dataFi " +
			"group by " +
			"	s.tipus, " +
			"	rs.solicitantNom, " +
			"	rs.solicitantId, " +
			"	rs.unitatTramitadora, " +
			"	rs.procedimentCodi, " +
			"	rs.procedimentNom, " +
			"	rp.serveiCodi, " +
			"	s.nom, " +
			"	rp.emissorCodi " +
			"order by " +
			"	rs.solicitantNom, " +
			"	rs.procedimentCodi, " +
			"	rp.serveiCodi "
			)
	List<InformeGeneralEstatDto> informeGeneralEstat(
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);

	@Query( "select new es.caib.emiserv.logic.intf.dto.InformeEmisorEnrutatDto( " +
			"		rp.emissorCodi, " +
			"		s.tipus, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		rm.entitatCodi, " +
			"		coalesce(rm.url, rd.url, s.urlPerDefecte), " +
			"		count(distinct rp.id) as total, " +
			"		count(distinct case " +
			"			when substring(rp.estat,0,2) = '00' then rp.id " +
			"			else null " +
			"		end) as correcte, " +
			"		count(distinct case " +
			"			when substring(rp.estat,0,2) = '00' then null " +
			"			else rp.id " +
			"		end) as error, " +
			"		sum(case " +
			"			when s.tipus = es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.ENRUTADOR_MULTIPLE and rp.entitatCodiRedireccio = rm.entitatCodi then 1 " +
			"			else 0 " +
			"		end) as respostaEsperada, " +
			"		(select count(distinct rp2.id) " +
			"			from RedireccioPeticioEntity rp2 " +
			"				inner join ServeiEntity s2 on rp2.serveiCodi = s2.codi " +
			"			where rp2.emissorCodi = rp.emissorCodi " +
			"				and rp2.serveiCodi = rp.serveiCodi " +
			"				and s2.tipus = es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.ENRUTADOR_MULTIPLE " +
			"				and rp2.estatEnum not in (es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT, es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.EN_PROCES) " +
			"				and rp2.dataPeticio >= :dataInici " +
			"				and rp2.dataPeticio < :dataFi), " +
			"		(select count(distinct rp2.id) " +
			"			from RedireccioPeticioEntity rp2 " +
			"			where rp2.emissorCodi = rp.emissorCodi " +
			"				and rp2.serveiCodi = rp.serveiCodi " +
			"				and rp2.estatEnum not in (es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT, es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.EN_PROCES) " +
			"				and substring(rp2.estat,0,2) = '00' " +
			"				and rp2.dataPeticio >= :dataInici " +
			"				and rp2.dataPeticio < :dataFi), " +
			"		(select count(distinct rp2.id) " +
			"			from RedireccioPeticioEntity rp2 " +
			"			where rp2.emissorCodi = rp.emissorCodi " +
			"				and rp2.serveiCodi = rp.serveiCodi " +
			"				and rp2.estatEnum not in (es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT, es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.EN_PROCES) " +
			"				and substring(rp2.estat,0,2) <> '00' " +
			"				and rp2.dataPeticio >= :dataInici " +
			"				and rp2.dataPeticio < :dataFi) " +
			"		, (select count(distinct rp2.id) " +
			"			from RedireccioPeticioEntity rp2 " +
			"				inner join ServeiEntity s2 on rp2.serveiCodi = s2.codi " +
			"			where rp2.emissorCodi = rp.emissorCodi " +
			"				and rp2.serveiCodi = rp.serveiCodi " +
			"				and s2.tipus = es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.ENRUTADOR_MULTIPLE " +
			"				and rp2.estatEnum not in (es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT, es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.EN_PROCES) " +
			"				and rp2.entitatCodiRedireccio is not null " +
			"				and rp2.dataPeticio >= :dataInici " +
			"				and rp2.dataPeticio < :dataFi) " +
			"	) " +
			"	from " +
			"		RedireccioMissatgeEntity rm " +
			"			inner join rm.peticio rp on rp.id = rm.peticio.id and rp.estatEnum not in (es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT, es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.EN_PROCES) " +
			"			inner join ServeiEntity s on rp.serveiCodi = s.codi " +
			"			left join ServeiRutaDestiEntity rd on rd.servei = s and rd.entitatCodi = rm.entitatCodi " +
			"	where s.tipus != es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.BACKOFFICE " +
//			"		and rp.estatEnum != es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT " +
//			"		and rm.entitatCodi is not null " +
			"		and rm.tipus <> " + es.caib.emiserv.persist.entity.RedireccioMissatgeEntity.TIPUS_PETICION +
			"		and ( " +
			"			(s.tipus = es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.ENRUTADOR_MULTIPLE " +
			"				and rm.tipus in (" + es.caib.emiserv.persist.entity.RedireccioMissatgeEntity.TIPUS_RESPOSTA_ENTITAT + ", " + es.caib.emiserv.persist.entity.RedireccioMissatgeEntity.TIPUS_FAULT + ", " + es.caib.emiserv.persist.entity.RedireccioMissatgeEntity.TIPUS_FAULT_LOCAL + ")) " +
			"			or " +
			"			(s.tipus <> es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.ENRUTADOR_MULTIPLE " +
			"				and rm.tipus in (" + es.caib.emiserv.persist.entity.RedireccioMissatgeEntity.TIPUS_RESPUESTA + ", " + es.caib.emiserv.persist.entity.RedireccioMissatgeEntity.TIPUS_FAULT + ", " + es.caib.emiserv.persist.entity.RedireccioMissatgeEntity.TIPUS_FAULT_LOCAL + ")) " +
			"		) " +
			"		and rp.dataPeticio >= :dataInici " +
			"		and rp.dataPeticio < :dataFi " +
			"group by " +
			"		rp.emissorCodi, " +
			"		s.tipus, " +
			"		rm.entitatCodi, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		rm.url, " +
			"		rd.url, " +
			"		s.urlPerDefecte " +
			"order by " +
			"		rp.emissorCodi, " +
			"		rp.serveiCodi, " +
			"		rm.entitatCodi")
	List<InformeEmisorEnrutatDto> informeEmisorEnrutat(
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);

	/** Consulta les peticions als enrutadors per a la consulta de dades obertes
	 *
	 * @param dataInici
	 * @param dataFi
	 * @return
	 */
	@Query( "select new es.caib.emiserv.client.dadesobertes.DadesObertesRespostaConsulta( " +
			"		rs.solicitantCodi, " +
			"		rs.solicitantNom, " +
			"		rs.solicitantId, " +
			"		rs.unitatTramitadora, " +
			"		rs.procedimentCodi, " +
			"		rs.procedimentNom, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		'', " +
			"		rp.emissorCodi, " +
			"		rs.consentiment, " +
			"		rs.finalitat, " +
			"		rs.titularTipusDoc, " +
			"		rs.solicitudId, " +
			"		rp.dataPeticio, " +
			"		s.tipus, " +
			"		rp.estat " +
			"	) " +
			"	from " +
			"		RedireccioSolicitudEntity as rs " +
			"			inner join rs.peticio as rp, " +
			"		ServeiEntity s " +
			"	where rp.serveiCodi = s.codi " +
			"    	and (:esNullEntitatNif = true or rs.solicitantId = :entitatNif) " +
			"		and (:esNullProcedimentId = true or rs.procedimentCodi = :procedimentId) " +
			"		and (:esNullServeiCodi = true or rp.serveiCodi = :serveiCodi) " +
			"		and (:esNullDataInici = true or rp.dataPeticio >= :dataInici) " +
			"		and (:esNullDataFi = true or rp.dataPeticio <= :dataFi) " +
			"order by " +
			"	rp.dataPeticio "
	)
	List<DadesObertesRespostaConsulta> findByOpendata(
			@Param("esNullEntitatNif") boolean esNullEntitatNif,
			@Param("entitatNif") String entitatNif,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") String procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

	@Query(	"select " +
			"	new es.caib.emiserv.logic.intf.dto.CarregaDto( " +
			"		count(rs.id), " +
			"		rs.solicitantNom, " +
			"		rs.solicitantId, " +
			"		rs.unitatTramitadora, " +
			"		rs.procedimentCodi, " +
			"		rs.procedimentNom, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		s.tipus, " +
			"		rp.emissorCodi) " +
			"	from " +
			"		RedireccioSolicitudEntity as rs " +
			"			inner join rs.peticio as rp, " +
			"		ServeiEntity s " +
			"	where rp.serveiCodi = s.codi " +
			"	 	and rp.dataPeticio >= :dataInici " +
			"group by " +
			"		rs.solicitantNom, " +
			"		rs.solicitantId, " +
			"		rs.unitatTramitadora, " +
			"		rs.procedimentCodi, " +
			"		rs.procedimentNom, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		s.tipus, " +
			"		rp.emissorCodi " +
			"order by " +
			"	rs.solicitantNom, " +
			"	rs.unitatTramitadora, " +
			"	rs.procedimentNom, " +
			"	s.nom")
	List<CarregaDto> findCarrega(@Param("dataInici") Date dataInici);

	@Query(	"select " +
			"	new es.caib.emiserv.logic.intf.dto.EstadisticaDto( " +
			"		count(rs.id), " +
			"		rs.solicitantNom, " +
			"		rs.solicitantId, " +
			"		rs.unitatTramitadora, " +
			"		rs.procedimentCodi, " +
			"		rs.procedimentNom, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		s.tipus, " +
			"		rp.emissorCodi, " +
			"		sum(case " +
			"			when substring(rp.estat,0,2) = '00' then 1 " +
			"			else 0 " +
			"		end) as correcte, " +
			"		sum(case " +
			"			when substring(rp.estat,0,2) = '00' then 0 " +
			"			else 1 " +
			"		end) as error) " +
			"	from " +
			"		RedireccioSolicitudEntity as rs " +
			"			inner join rs.peticio as rp, " +
			"		ServeiEntity s " +
			"	where rp.serveiCodi = s.codi " +
			"    	and rs.solicitantId = :entitatNif " +
			"		and (:esNullProcedimentCodi = true or rs.procedimentCodi = :procedimentCodi) " +
			"		and (:esNullServeiCodi = true or rp.serveiCodi = :serveiCodi) " +
			"		and (:esNullEstat = true " +
			"			or (:esPendent = true and rp.estat = '0001') " +
			"			or (:esProcessant = true and (rp.estat = '0002' or rp.estat = '0004')) " +
			"			or (:esTramitada = true and rp.estat = '0003') " +
			"			or (:esError = true and substring(rp.estat,0,2) != '00')) " +
			"		and (:esNullDataInici = true or rp.dataPeticio >= :dataInici) " +
			"		and (:esNullDataFi = true or rp.dataPeticio <= :dataFi) " +
			"group by " +
			"		rs.solicitantNom, " +
			"		rs.solicitantId, " +
			"		rs.unitatTramitadora, " +
			"		rs.procedimentCodi, " +
			"		rs.procedimentNom, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		s.tipus, " +
			"		rp.emissorCodi " +
			"order by " +
			"	rs.solicitantNom, " +
			"	rs.unitatTramitadora, " +
			"	rs.procedimentNom, " +
			"	s.nom")
	List<EstadisticaDto> findEstadistiques(
			@Param("entitatNif") String entitatNif,
			@Param("esNullProcedimentCodi") boolean esNullProcedimentCodi,
			@Param("procedimentCodi") String procedimentCodi,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("esPendent") boolean esPendent,
			@Param("esProcessant") boolean esProcessant,
			@Param("esTramitada") boolean esTramitada,
			@Param("esError") boolean esError,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

	@Query(	"select " +
			"	new es.caib.emiserv.logic.intf.dto.EstadisticaDto( " +
			"		count(rs.id), " +
			"		rs.solicitantNom, " +
			"		rs.solicitantId, " +
			"		rs.unitatTramitadora, " +
			"		rs.procedimentCodi, " +
			"		rs.procedimentNom, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		s.tipus, " +
			"		rp.emissorCodi, " +
			"		sum(case " +
			"			when substring(rp.estat,0,2) = '00' then 1 " +
			"			else 0 " +
			"		end) as correcte, " +
			"		sum(case " +
			"			when substring(rp.estat,0,2) = '00' then 0 " +
			"			else 1 " +
			"		end) as error) " +
			"	from " +
			"		RedireccioSolicitudEntity as rs " +
			"			inner join rs.peticio as rp, " +
			"		ServeiEntity s " +
			"	where rp.serveiCodi = s.codi " +
			"		and rp.dataPeticio >= :dataInici " +
			"		and rp.dataPeticio <= :dataFi " +
			"group by " +
			"		rs.solicitantNom, " +
			"		rs.solicitantId, " +
			"		rs.unitatTramitadora, " +
			"		rs.procedimentCodi, " +
			"		rs.procedimentNom, " +
			"		rp.serveiCodi, " +
			"		s.nom, " +
			"		s.tipus, " +
			"		rp.emissorCodi " +
			"order by " +
			"	rs.solicitantNom, " +
			"	rs.unitatTramitadora, " +
			"	rs.procedimentNom, " +
			"	s.nom")
	List<EstadisticaDto> findEstadistiques(
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);

	@Query("SELECT distinct procedimentCodi FROM RedireccioSolicitudEntity")
	List<String> findAllProcediments();

	@Query("SELECT distinct unitatTramitadora FROM RedireccioSolicitudEntity")
	List<String> findAllDepartaments();

	@Query("SELECT distinct emissorCodi FROM RedireccioPeticioEntity")
	List<String> findAllEmisors();
}
