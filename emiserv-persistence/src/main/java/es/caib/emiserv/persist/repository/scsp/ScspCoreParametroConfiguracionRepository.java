/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.scsp.ScspCoreParametroConfiguracionEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_PARAMETRO_CONFIGURACION.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreParametroConfiguracionRepository extends JpaRepository<ScspCoreParametroConfiguracionEntity, String> {

}
