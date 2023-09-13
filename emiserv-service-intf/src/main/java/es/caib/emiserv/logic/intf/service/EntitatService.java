/**
 * 
 */
package es.caib.emiserv.logic.intf.service;

import es.caib.emiserv.logic.intf.dto.EntitatDto;
import es.caib.emiserv.logic.intf.dto.EntitatFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.PermissionDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Declaració dels mètodes per a la gestió de entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatService {

	/**
	 * Crea un nou servei.
	 * 
	 * @param entitat
	 *            Informació de l'entitat a crear.
	 * @return El servei creat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public EntitatDto create(EntitatDto entitat);

	/**
	 * Actualitza la informació de l'entitat que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitat
	 *            Informació de l'entitat a modificar.
	 * @return L'entitat modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per accedir al servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public EntitatDto update(EntitatDto entitat) throws NotFoundException;

	/**
	 * Esborra el serveil'entitat amb l'id especificat per a un usuari administrador.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a esborrar.
	 * @return L'entitat esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap entitat amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void delete(Long id) throws NotFoundException;

	/**
	 * Consulta un servei donat el seu id.
	 * 
	 * @param id
	 *            Atribut id del servei a trobar.
	 * @return El servei amb l'id especificat o null si no s'ha trobat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per accedir al servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public EntitatDto findById(Long id);

	/**
	 * Consulta una entitat donat el seu codi.
	 * 
	 * @param codi
	 *            Atribut codi de l'entitat a trobar.
	 * @return L'entitat amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public EntitatDto findByCodi(String codi);

	/**
	 * Llistat amb totes les entitats accessibles.
	 *
	 * @param filtre
	 * @param paginacioParams Paràmetres per a paginar i ordenar el llistat.
	 * @return El llistat de entitats.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public PaginaDto<EntitatDto> findAllPaginat(EntitatFiltreDto filtre, PaginacioParamsDto paginacioParams);

	/**
	 * Consulta una entitat donat el seu codi.
	 *
	 * @param codi
	 *            Atribut codi de l'entitat a trobar.
	 * @return L'entitat amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void sincronitzar() throws Exception;

}
