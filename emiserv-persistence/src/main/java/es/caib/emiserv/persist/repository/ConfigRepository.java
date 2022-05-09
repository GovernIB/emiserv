/**
 * 
 */
package es.caib.emiserv.persist.repository;

import es.caib.emiserv.logic.intf.dto.ConfigSourceEnumDto;
import es.caib.emiserv.persist.entity.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<ConfigEntity, String> {

    List<ConfigEntity> findBySourceProperty(ConfigSourceEnumDto sourceProperty);

}