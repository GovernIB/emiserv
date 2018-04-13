/**
 * 
 */
package es.caib.emiserv.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.core.entity.BackofficePeticioEntity;
import es.caib.emiserv.core.entity.BackofficeSolicitudEntity;

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

}
