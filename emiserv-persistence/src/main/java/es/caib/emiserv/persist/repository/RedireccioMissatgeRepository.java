/**
 * 
 */
package es.caib.emiserv.persist.repository;

import es.caib.emiserv.persist.entity.RedireccioMissatgeEntity;
import es.caib.emiserv.persist.entity.RedireccioPeticioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus redireccioMissatge.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RedireccioMissatgeRepository extends JpaRepository<RedireccioMissatgeEntity, Long> {

//	RedireccioMissatgeEntity findByPeticioAndTipus(
//			RedireccioPeticioEntity peticio,
//			int tipus);

	List<RedireccioMissatgeEntity> findByPeticioAndTipus(
			RedireccioPeticioEntity peticio,
			int tipus);

}
