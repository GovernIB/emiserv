/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAplicacionEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_EM_AUTORIZACION_ORGANISMO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreEmAplicacionRepository extends JpaRepository<ScspCoreEmAplicacionEntity, Integer> {

}
