/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionAutoridadCertEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_EM_AUTORIZACION_CA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreEmAutorizacionAutoridadCertRepository extends JpaRepository<ScspCoreEmAutorizacionAutoridadCertEntity, String> {
	public List<ScspCoreEmAutorizacionAutoridadCertEntity> findAll();
	
	public Page<ScspCoreEmAutorizacionAutoridadCertEntity> findAll(Pageable pageable);

	public ScspCoreEmAutorizacionAutoridadCertEntity findById(Long id);
}
