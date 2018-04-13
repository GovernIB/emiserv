/**
 * 
 */
package es.caib.emiserv.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.emiserv.core.entity.RedireccioPeticioEntity;
import es.caib.emiserv.core.entity.RedireccioSolicitudEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus redireccioSolicitud.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RedireccioSolicitudRepository extends JpaRepository<RedireccioSolicitudEntity, Long> {

	@Query(	"from " +
			"    RedireccioSolicitudEntity rds " +
			"where " +
			"    (:esNullProcediment = true or rds.procedimentCodi = :procediment) " +
			"and (:esNullServei = true or rds.peticio.serveiCodi = :servei) " +
			"and (:esNullEstat = true or (:esError = true and substring(rds.peticio.estat, 1, 2) != '00') or (:esError = false and rds.peticio.estat = :estat)) " +
			"and (:esNullDataInici = true or rds.peticio.dataPeticio >= :dataInici) " +
			"and (:esNullDataFi = true or rds.peticio.dataPeticio <= :dataFi) " +
			"and (:esNullFuncionariNom = true or lower(rds.funcionariNom) like lower(:funcionariNom)) " +
			"and (:esNullFuncionariDocument = true or lower(rds.funcionariDocument) like lower(:funcionariDocument))")
	Page<RedireccioSolicitudEntity> findByFiltrePaginat(
			@Param("esNullProcediment") boolean esNullProcediment,
			@Param("procediment") String procediment,
			@Param("esNullServei") boolean esNullServei,
			@Param("servei") String servei,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("esError") boolean esError,
			@Param("estat") String estat,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullFuncionariNom") boolean esNullFuncionariNom,
			@Param("funcionariNom") String funcionariNom,
			@Param("esNullFuncionariDocument") boolean esNullFuncionariDocument,
			@Param("funcionariDocument") String funcionariDocument,
			Pageable pageable);

	List<RedireccioSolicitudEntity> findByPeticioOrderBySolicitudIdAsc(
			RedireccioPeticioEntity peticio);

	@Query(	"select " +
			"    distinct rds.procedimentCodi, " +
			"    rds.procedimentNom " +
			"from " +
			"    RedireccioSolicitudEntity rds")
	List<Object[]> findProcedimentDistint();

}
