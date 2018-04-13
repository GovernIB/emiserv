/**
 * 
 */
package es.caib.emiserv.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.core.api.dto.PeticioEstatEnumDto;
import es.caib.emiserv.core.entity.BackofficePeticioEntity;
import es.caib.emiserv.core.entity.ScspCorePeticionRespuestaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus BackofficePeticio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BackofficePeticioRepository extends JpaRepository<BackofficePeticioEntity, Long> {

	public BackofficePeticioEntity findByScspPeticionRespuesta(
			ScspCorePeticionRespuestaEntity scspPeticionRespuesta);

	public List<BackofficePeticioEntity> findByEstatOrderByIdAsc(
			PeticioEstatEnumDto estat);

	@Query(	"from " +
			"    BackofficePeticioEntity bp " +
			"where " +
			"    bp.estat = es.caib.emiserv.core.api.dto.PeticioEstatEnumDto.PENDENT " +
			"and bp.scspPeticionRespuesta.sincrona = false " +
			"and bp.scspPeticionRespuesta.estado like '00%' " +
			"and bp.scspPeticionRespuesta.certificado in (:ids) " +
			"order by " +
			"    bp.id asc")
	public List<BackofficePeticioEntity> findAsincronesPendentsDeProcessar(
			@Param("ids") List<Long> ids);

}
