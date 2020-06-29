/**
 * 
 */
package es.caib.emiserv.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.core.entity.ScspCoreEmAutorizacionOrganismoEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_EM_AUTORIZACION_ORGANISMO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreEmAutorizacionOrganismoRepository extends JpaRepository<ScspCoreEmAutorizacionOrganismoEntity, Long> {

	@Query(	"from " +
			"    ScspCoreEmAutorizacionOrganismoEntity scao " +
			"where " +
			"    (:esNullNom = true or lower(scao.nombreOrganismo) like lower('%'||:nom||'%')) " +
			"and (:esNullCif = true or lower(scao.idorganismo) like lower('%'||:cif||'%'))")
	Page<ScspCoreEmAutorizacionOrganismoEntity> findByFiltrePaginat(
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullCif") boolean esNullCif,
			@Param("cif") String cif,
			Pageable pageable);

}
