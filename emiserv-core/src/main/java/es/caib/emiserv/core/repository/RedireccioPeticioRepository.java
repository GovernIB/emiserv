/**
 * 
 */
package es.caib.emiserv.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.core.api.dto.InformeGeneralEstatDto;
import es.caib.emiserv.core.entity.RedireccioPeticioEntity;

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
			"and (:esNullProcediment = true or exists (from RedireccioSolicitudEntity rs where rs.peticio.peticioId = rp.peticioId and rs.procedimentCodi = :procediment)) " +
			"and (:esNullEstat = true or (:esError = true and substring(rp.estat, 1, 2) != '00') or (:esError = false and rp.estat = :estat)) " +
			"and (:esNullDataInici = true or rp.dataPeticio >= :dataInici) " +
			"and (:esNullDataFi = true or rp.dataPeticio <= :dataFi)")
	Page<RedireccioPeticioEntity> findByFiltrePaginat(
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
	@Query( "select new es.caib.emiserv.core.api.dto.InformeGeneralEstatDto( " +
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
			"		and s.tipus != es.caib.emiserv.core.api.dto.ServeiTipusEnumDto.BACKOFFICE " + 
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

}
