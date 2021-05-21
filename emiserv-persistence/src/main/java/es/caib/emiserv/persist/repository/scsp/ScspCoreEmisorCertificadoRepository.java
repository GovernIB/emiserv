/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.scsp.ScspCoreEmisorCertificadoEntity;

/**
 *  Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_EMISOR_CERTIFICADO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreEmisorCertificadoRepository extends JpaRepository<ScspCoreEmisorCertificadoEntity, Long> {

}
