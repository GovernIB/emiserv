/**
 * 
 */
package es.caib.emiserv.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.core.entity.ScspCoreTokenDataEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_TOKEN_DATA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreTokenDataRepository extends JpaRepository<ScspCoreTokenDataEntity, String> {

	ScspCoreTokenDataEntity findByPeticionIdAndTipoMensaje(
			String peticionId,
			int tipoMensaje);

}
