/**
 * 
 */
package es.caib.emiserv.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.core.entity.ScspCoreEmAutorizacionCertificadoEntity;
import es.caib.emiserv.core.entity.ScspCoreEmAutorizacionOrganismoEntity;
import es.caib.emiserv.core.entity.ScspCoreServicioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_EM_AUTORIZACION_CERT.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreEmAutorizacionCertificadoRepository extends JpaRepository<ScspCoreEmAutorizacionCertificadoEntity, Long> {


	@Query(	"from " +
			"    ScspCoreEmAutorizacionCertificadoEntity scac " +
			"where " +
			"     (scac.servicio = :servicio) " +
			" and (:esNullAplicacio = true or lower(scac.aplicacion.cn) like lower('%'||:aplicacio||'%')) " +
			" and (:esNullOrganisme = true or scac.organismo = :organisme)"
			)
	Page<ScspCoreEmAutorizacionCertificadoEntity> findByFiltre(
			@Param("servicio") ScspCoreServicioEntity servicio,
			@Param("esNullAplicacio") boolean esNullAplicacio,
			@Param("aplicacio") String aplicacio,
			@Param("esNullOrganisme") boolean esNullOrganisme,
			@Param("organisme") ScspCoreEmAutorizacionOrganismoEntity organisme,
			Pageable pageable);
	
	Page<ScspCoreEmAutorizacionCertificadoEntity> findByServicio(
			ScspCoreServicioEntity servicio,
			Pageable pageable);

}
