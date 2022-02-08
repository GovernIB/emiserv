/**
 * 
 */
package es.caib.emiserv.logic.intf.service;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

	/**
	 * Obté l'usuari actual.
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public String getIdiomaUsuariActual();

	/**
	 * Configura l'idioma de l'usuari actual.
	 * 
	 * @param idioma
	 *            idioma de l'usuari.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void updateIdiomaUsuariActual(String idioma);


	/**
	 * Carrega les propietats de la base de dades a l'environment
	 */
    public void propagateDbProperties();
}
