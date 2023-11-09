/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

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
			"    (:esNullServei = true or scp.codiServei = :servei) " +
			"and (:esNullProcediment = true or scp.codiProcediment = :procediment) " +
			"and (:esNullEstat = true or scp.estat = :estat or (:esDesconegutEstat = true and scp.estat is null)) " +
			"and (:esNullDataInici = true or scp.fechaPeticion >= :dataInici) " +
			"and (:esNullDataFi = true or scp.fechaPeticion <= :dataFi) " +
			"and (:nomesServeisPermesos = false or scp.certificado in (:serveisPermesos)) " +
			"and (:esNullPeticioId = true or lower(scp.peticionId) like lower('%'||:peticioId||'%'))")
	Page<ScspCorePeticionRespuestaEntity> findByFiltrePaginat(
			@Param("esNullProcediment") boolean esNullProcediment,
			@Param("procediment") String procediment,
			@Param("esNullServei") boolean esNullServei,
			@Param("servei") String servei,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("esDesconegutEstat") boolean esDesconegutEstat,
			@Param("estat") PeticioEstatEnumDto estat,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("nomesServeisPermesos") boolean nomesServeisPermesos,
			@Param("serveisPermesos") List<String> serveisPermesos,
			@Param("esNullPeticioId") boolean esNullPeticioId,
			@Param("peticioId") String peticioId,
			Pageable pageable);

	ScspCorePeticionRespuestaEntity findByPeticionId(String peticionId);

	long countByCertificado(Long certificado);

}
