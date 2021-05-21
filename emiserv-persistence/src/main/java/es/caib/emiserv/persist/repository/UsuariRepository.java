/**
 * 
 */
package es.caib.emiserv.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.emiserv.persist.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariRepository extends JpaRepository<UsuariEntity, String> {

	public UsuariEntity findByCodi(String codi);

}
