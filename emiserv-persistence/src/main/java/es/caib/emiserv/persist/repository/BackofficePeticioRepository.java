/**
 * 
 */
package es.caib.emiserv.persist.repository;

import javax.persistence.LockModeType;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus BackofficePeticio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BackofficePeticioRepository extends JpaRepository<BackofficePeticioEntity, Long> {

	public BackofficePeticioEntity findByScspPeticionRespuesta(
			ScspCorePeticionRespuestaEntity scspPeticionRespuesta);

	/** Entitat que ha respost la petició (sense carregar la petició SCSP). */
	@Query(	"select bp.entitatCodi from " +
			"    BackofficePeticioEntity bp " +
			"where " +
			"    bp.scspPeticionRespuesta.peticionId = :peticionId")
	public List<String> findEntitatCodiByPeticionId(
			@Param("peticionId") String peticionId);

	public List<BackofficePeticioEntity> findByEstatOrderByIdAsc(
			PeticioEstatEnumDto estat);

	@Query(	"from " +
			"    BackofficePeticioEntity bp " +
			"where " +
			"    bp.estat = es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT " +
			"and bp.scspPeticionRespuesta.sincrona = false " +
			"and bp.scspPeticionRespuesta.estado like '00%' " +
			"and bp.scspPeticionRespuesta.certificado in (:ids) " +
			"order by " +
			"    bp.id asc")
	public List<BackofficePeticioEntity> findAsincronesPendentsDeProcessar(
			@Param("ids") List<Long> ids);

	/** Petició bloquejada per recomptar les sol·licituds processades (enviaments en paral·lel). */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(	"from " +
			"    BackofficePeticioEntity bp " +
			"where " +
			"    bp.id = :id")
	public BackofficePeticioEntity findAmbBloqueig(
			@Param("id") Long id);

	/**
	 * Peticions asíncrones pendents que ja no tenen cap sol·licitud per enviar (p. ex. per una aturada
	 * entre l'enviament de la darrera sol·licitud i el recompte de la petició).
	 */
	@Query(	"select " +
			"    bp.id " +
			"from " +
			"    BackofficePeticioEntity bp " +
			"where " +
			"    bp.estat = es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT " +
			"and bp.scspPeticionRespuesta.sincrona = false " +
			"and bp.scspPeticionRespuesta.certificado in (:ids) " +
			"and not exists (" +
			"    select s.id from BackofficeSolicitudEntity s " +
			"    where s.peticio = bp " +
			"    and s.estat in (es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto.PENDENT, es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto.ENVIANT))")
	public List<Long> findIdsPendentsSenseSolicitudsPerEnviar(
			@Param("ids") List<Long> ids);

	/** Consultes asíncrones processades com a síncrones (pantalla de gestió). */
	@Query(	"from " +
			"    BackofficePeticioEntity bp " +
			"where " +
			"    bp.scspPeticionRespuesta.sincrona = false " +
			"and bp.scspPeticionRespuesta.certificado in (:certificats) " +
			"and (:totsEstats = true or bp.estat in (:estats)) " +
			"and (:nomesPausades = false or bp.pausada = true) " +
			"and (:esNullPeticioId = true or lower(bp.scspPeticionRespuesta.peticionId) like lower(concat('%', :peticioId, '%')))")
	public Page<BackofficePeticioEntity> findAsincronesByFiltre(
			@Param("certificats") List<Long> certificats,
			@Param("totsEstats") boolean totsEstats,
			@Param("estats") List<PeticioEstatEnumDto> estats,
			@Param("nomesPausades") boolean nomesPausades,
			@Param("esNullPeticioId") boolean esNullPeticioId,
			@Param("peticioId") String peticioId,
			Pageable pageable);

}
