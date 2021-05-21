/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.scsp.ScspCoreEmBackofficeEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreEmBackofficeRepository extends JpaRepository<ScspCoreEmBackofficeEntity, Long> {

}
