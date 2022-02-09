/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import es.caib.emiserv.persist.entity.scsp.ScspCoreEmAutorizacionAutoridadCertEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

	@Query("from " +
			" 	ScspCoreEmAutorizacionAutoridadCertEntity scac " +
			"where " +
			" 	(:esNullNom = true or lower(scac.nombre) like lower('%'||:nom||'%')) " +
			"and (:esNullCodi = true or lower(scac.codca) like lower('%'||:codi||'%'))")
    Page<ScspCoreEmAutorizacionAutoridadCertEntity> findByFiltrePaginat(
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi")String codi,
			Pageable pageable);
}
