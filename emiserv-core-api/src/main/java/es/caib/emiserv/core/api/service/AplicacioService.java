/**
 * 
 */
package es.caib.emiserv.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.emiserv.core.api.dto.UsuariDto;
import es.caib.emiserv.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

	/**
	 * Obté la versió actual de l'aplicació.
	 * 
	 * @return La versió actual.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public String getVersioActual();

	/**
	 * Processa l'autenticació d'un usuari.
	 * 
	 * @throws NotFoundException
	 *             Si l'usuari no es troba al plugin d'usuaris.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void processarAutenticacioUsuari() throws NotFoundException;

	/**
	 * Obté l'usuari actual.
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public UsuariDto getUsuariActual();

	/**
	 * Modifica la configuració de l'usuari actual
	 *
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	UsuariDto updateUsuariActual(UsuariDto dto);

	/**
	 * Obté un usuari donat el seu codi.
	 * 
	 * @param codi
	 *            codi de l'usuari a cercar.
	 * @return L'usuari obtingut o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public UsuariDto findUsuariAmbCodi(String codi);

	/**
	 * Consulta els usuaris donat un text.
	 * 
	 * @param text
	 *            text per a fer la consulta.
	 * @return La llista d'usuaris.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<UsuariDto> findUsuariAmbText(String text);

}
