/**
 * 
 */
package es.caib.emiserv.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.core.entity.ScspCoreEmAutorizacionCertificadoEntity;
import es.caib.emiserv.core.entity.ScspCoreServicioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_EM_AUTORIZACION_CERT.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreEmAutorizacionCertificadoRepository extends JpaRepository<ScspCoreEmAutorizacionCertificadoEntity, Long> {

	Page<ScspCoreEmAutorizacionCertificadoEntity> findByServicio(
			ScspCoreServicioEntity servicio,
			Pageable pageable);

}
