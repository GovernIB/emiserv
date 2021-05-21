/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_PETICION_RESPUESTA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCorePeticionRespuestaRepository extends JpaRepository<ScspCorePeticionRespuestaEntity, String> {

	@Query(	"from " +
			"    ScspCorePeticionRespuestaEntity scp " +
			"where " +
			"    (:esNullServei = true or scp.certificado = :servei) " +
			"and (:esNullProcediment = true or exists (from ScspCoreTransmisionEntity sct where sct.peticionId = scp.peticionId and sct.procedimientoCodigo = :procediment)) " +
			"and (:esNullEstat = true or (:esError = true and substring(scp.estado, 1, 2) != '00') or (:esError = false and scp.estado = :estat)) " +
			"and (:esNullDataInici = true or scp.fechaPeticion >= :dataInici) " +
			"and (:esNullDataFi = true or scp.fechaPeticion <= :dataFi) " +
			"and (:nomesServeisPermesos = false or scp.certificado in (:serveisPermesos))")
	Page<ScspCorePeticionRespuestaEntity> findByFiltrePaginat(
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
			Pageable pageable);

	ScspCorePeticionRespuestaEntity findByPeticionId(String peticionId);

	long countByCertificado(String certificado);

}
