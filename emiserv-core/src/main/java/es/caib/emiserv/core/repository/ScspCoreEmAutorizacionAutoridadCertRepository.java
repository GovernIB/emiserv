/**
 * 
 */
package es.caib.emiserv.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.core.entity.ScspCoreEmAutorizacionAutoridadCertEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_EM_AUTORIZACION_CA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreEmAutorizacionAutoridadCertRepository extends JpaRepository<ScspCoreEmAutorizacionAutoridadCertEntity, String> {

}
