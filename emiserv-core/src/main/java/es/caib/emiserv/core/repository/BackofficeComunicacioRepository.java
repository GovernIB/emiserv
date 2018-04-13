/**
 * 
 */
package es.caib.emiserv.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.core.entity.BackofficeComunicacioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus BackofficeComunicacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BackofficeComunicacioRepository extends JpaRepository<BackofficeComunicacioEntity, Long> {

}
