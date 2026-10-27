/**
 * 
 */
package es.caib.emiserv.persist.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto;

import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.BackofficeSolicitudEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus BackofficeSolicitud.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BackofficeSolicitudRepository extends JpaRepository<BackofficeSolicitudEntity, Long> {

	List<BackofficeSolicitudEntity> findByPeticioOrderByIdAsc(
			BackofficePeticioEntity peticio);

	BackofficeSolicitudEntity findByPeticioAndSolicitudId(
			BackofficePeticioEntity peticio,
			String solicitudId);

	BackofficeSolicitudEntity findByPeticioScspPeticionRespuestaPeticionIdAndSolicitudId(
			String peticionId,
			String solicitudId);

	int countByPeticio(BackofficePeticioEntity peticio);

	int countByPeticioAndEstat(BackofficePeticioEntity peticio, BackofficeSolicitudEstatEnumDto estat);

	/**
	 * Condicions de les sol·licituds que es poden enviar: pendents, sense reintent programat per més
	 * endavant, d'una petició asíncrona pendent, no pausada i sense error SCSP.
	 */
	String ENVIABLES =
			"    s.estat = es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto.PENDENT " +
			"and (s.dataProperIntent is null or s.dataProperIntent <= :ara) " +
			"and s.peticio.estat = es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT " +
			"and s.peticio.pausada = false " +
			"and s.peticio.scspPeticionRespuesta.sincrona = false " +
			"and s.peticio.scspPeticionRespuesta.estado like '00%' " +
			"and s.peticio.scspPeticionRespuesta.certificado in (:certificats) ";

	/** Sol·licituds pendents d'enviar agrupades per destí (servei SCSP i entitat), la petició més antiga primer. */
	@Query(	"select " +
			"    s.peticio.scspPeticionRespuesta.certificado, s.entitatCodi, count(s), min(s.peticio.id) " +
			"from " +
			"    BackofficeSolicitudEntity s " +
			"where " + ENVIABLES +
			"group by " +
			"    s.peticio.scspPeticionRespuesta.certificado, s.entitatCodi " +
			"order by " +
			"    min(s.peticio.id) asc")
	List<Object[]> findDestinsAmbSolicitudsEnviables(
			@Param("certificats") List<Long> certificats,
			@Param("ara") Date ara);

	/** Sol·licituds enviables d'un destí, per ordre d'arribada. */
	@Query(	"select " +
			"    s.id " +
			"from " +
			"    BackofficeSolicitudEntity s " +
			"where " + ENVIABLES +
			"and ((:entitatCodi is null and s.entitatCodi is null) or s.entitatCodi = :entitatCodi) " +
			"order by " +
			"    s.peticio.id asc, s.id asc")
	List<Long> findIdsEnviablesDesti(
			@Param("certificats") List<Long> certificats,
			@Param("entitatCodi") String entitatCodi,
			@Param("ara") Date ara,
			Pageable pageable);

	/** Sol·licituds enviables a les quals encara no s'ha resolt l'entitat destí. */
	@Query(	"select " +
			"    s.id " +
			"from " +
			"    BackofficeSolicitudEntity s " +
			"where " + ENVIABLES +
			"and s.entitatCodi is null " +
			"order by " +
			"    s.peticio.id asc, s.id asc")
	List<Long> findIdsEnviablesSenseEntitat(
			@Param("certificats") List<Long> certificats,
			@Param("ara") Date ara);

	/**
	 * Reserva atòmica d'una sol·licitud per enviar-la: només un procés (o node) la pot passar de
	 * PENDENT a ENVIANT. Retorna 1 si s'ha reservat.
	 */
	@Modifying
	@Query(	"update " +
			"    BackofficeSolicitudEntity s " +
			"set " +
			"    s.estat = es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto.ENVIANT, " +
			"    s.intents = s.intents + 1, " +
			"    s.dataEnviament = :ara, " +
			"    s.dataProperIntent = null, " +
			"    s.version = s.version + 1 " +
			"where " +
			"    s.id = :id " +
			"and s.estat = es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto.PENDENT")
	int reservarEnviament(
			@Param("id") Long id,
			@Param("ara") Date ara);

	/** Enviaments no finalitzats (aturada de l'aplicació o del node) iniciats abans de la data indicada. */
	List<BackofficeSolicitudEntity> findByEstatAndDataEnviamentBefore(
			BackofficeSolicitudEstatEnumDto estat,
			Date data);

	long countByEstat(BackofficeSolicitudEstatEnumDto estat);

	/** Sol·licituds de les peticions indicades per estat (i les que tenen un reintent programat). */
	@Query(	"select " +
			"    s.peticio.id, s.estat, count(s), sum(case when s.dataProperIntent > :ara then 1 else 0 end) " +
			"from " +
			"    BackofficeSolicitudEntity s " +
			"where " +
			"    s.peticio.id in (:peticions) " +
			"group by " +
			"    s.peticio.id, s.estat")
	List<Object[]> countByPeticioAndEstat(
			@Param("peticions") List<Long> peticions,
			@Param("ara") Date ara);

	/**
	 * Sol·licituds per enviar de les peticions asíncrones pendents, per petició (de la més antiga a
	 * la més nova) i destí: per estimar quan finalitzarà cada consulta i la càrrega de cada cedent.
	 */
	@Query(	"select " +
			"    s.peticio.id, s.peticio.pausada, s.peticio.scspPeticionRespuesta.certificado, s.entitatCodi, s.estat, count(s), max(s.dataProperIntent) " +
			"from " +
			"    BackofficeSolicitudEntity s " +
			"where " +
			"    s.estat in (es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto.PENDENT, es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto.ENVIANT) " +
			"and s.peticio.estat = es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto.PENDENT " +
			"and s.peticio.scspPeticionRespuesta.sincrona = false " +
			"group by " +
			"    s.peticio.id, s.peticio.pausada, s.peticio.scspPeticionRespuesta.certificado, s.entitatCodi, s.estat " +
			"order by " +
			"    s.peticio.id asc")
	List<Object[]> findCuaPerEnviar();

	/** Sol·licituds pendents o en enviament d'un servei SCSP (per estimar el temps de resposta). */
	@Query(	"select " +
			"    count(s) " +
			"from " +
			"    BackofficeSolicitudEntity s " +
			"where " +
			"    s.estat in (es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto.PENDENT, es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto.ENVIANT) " +
			"and s.peticio.scspPeticionRespuesta.certificado = :certificado")
	long countPendentsCertificat(
			@Param("certificado") Long certificado);


}
