package es.caib.emiserv.persist.resourcerepository;

import es.caib.emiserv.persist.base.repository.BaseRepository;
import es.caib.emiserv.persist.resourceentity.ServeiResourceEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repositori del recurs de serveis.
 *
 * @author Limit Tecnologies
 */
public interface ServeiResourceRepository extends BaseRepository<ServeiResourceEntity, Long> {

	@Query("select s.id from ServeiResourceEntity s")
	List<Long> findAllIds();

}
