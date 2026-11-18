/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionCertificadoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionOrganismoEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;

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

	/**
	 * Nombre d'autoritzacions per codi de servei (codcertificado), per als serveis donats (màxim
	 * 1000 codis per l'IN d'Oracle). Cada fila: [codi, nombre].
	 */
	@Query(	"select scac.servicio.codigoCertificado, count(scac) " +
			"from ScspCoreEmAutorizacionCertificadoEntity scac " +
			"where scac.servicio.codigoCertificado in (:codis) " +
			"group by scac.servicio.codigoCertificado")
	List<Object[]> countByServicioCodigoCertificadoIn(
			@Param("codis") java.util.Collection<String> codis);

}
