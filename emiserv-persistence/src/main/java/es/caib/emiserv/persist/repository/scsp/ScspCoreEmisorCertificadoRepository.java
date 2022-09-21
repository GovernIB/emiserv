/**
 * 
 */
package es.caib.emiserv.persist.repository.scsp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.scsp.ScspCoreEmisorCertificadoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 *  Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_EMISOR_CERTIFICADO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspCoreEmisorCertificadoRepository extends JpaRepository<ScspCoreEmisorCertificadoEntity, Long> {

    @Query(	"from " +
            "    ScspCoreEmisorCertificadoEntity scec " +
            "where " +
            "    (:esNullNom = true or lower(scec.nombre) like lower('%'||:nom||'%')) " +
            "and (:esNullCif = true or lower(scec.cif) like lower('%'||:cif||'%'))")
    Page<ScspCoreEmisorCertificadoEntity> findByFiltrePaginat(
            @Param("esNullNom") boolean esNullNom,
            @Param("nom") String nom,
            @Param("esNullCif") boolean esNullCif,
            @Param("cif") String cif,
            Pageable toSpringDataPageable);

    Optional<ScspCoreEmisorCertificadoEntity> findByCif(String cif);
}
