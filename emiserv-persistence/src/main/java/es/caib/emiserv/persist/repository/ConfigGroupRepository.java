package es.caib.emiserv.persist.repository;

import es.caib.emiserv.persist.entity.ConfigGroupEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigGroupRepository extends JpaRepository<ConfigGroupEntity, String> {

    List<ConfigGroupEntity> findByParentCodeIsNull(Sort sort);
}