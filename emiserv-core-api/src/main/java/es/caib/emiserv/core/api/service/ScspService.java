/**
 * 
 */
package es.caib.emiserv.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.emiserv.core.api.dto.AplicacioDto;
import es.caib.emiserv.core.api.dto.AutoritatCertificacioDto;
import es.caib.emiserv.core.api.dto.AutoritzacioDto;
import es.caib.emiserv.core.api.dto.AutoritzacioFiltreDto;
import es.caib.emiserv.core.api.dto.ClauPrivadaDto;
import es.caib.emiserv.core.api.dto.ClauPublicaDto;
import es.caib.emiserv.core.api.dto.EmisorDto;
import es.caib.emiserv.core.api.dto.OrganismeDto;
import es.caib.emiserv.core.api.dto.OrganismeFiltreDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.exception.NotFoundException;

/**
 * Mètodes per a gestionar else manteniments d'informació SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspService {

	/**
	 * Crea una nova aplicació per a les autoritzacions.
	 * 
	 * @param aplicacio
	 *            Informació de l'aplicació a crear.
	 * @return L'aplicació creada.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public AplicacioDto aplicacioCreate(
			AplicacioDto aplicacio) throws NotFoundException;

	/**
	 * Modifica una aplicació per a les autoritzacions.
	 * 
	 * @param aplicacio
	 *            Informació de l'aplicació a crear.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap aplicació amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void aplicacioUpdate(
			AplicacioDto aplicacio) throws NotFoundException;

	/**
	 * Esborra una aplicació per a les autoritzacions.
	 * 
	 * @param id
	 *            Atribut id de l'aplicació a esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap aplicació amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void aplicacioDelete(
			Integer id) throws NotFoundException;

	/**
	 * Consulta una aplicació donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'aplicació a trobar.
	 * @return L'aplicació amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap aplicació amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public AplicacioDto aplicacioFindById(Integer id) throws NotFoundException;

	/**
	 * Consulta de les aplicacions amb paginació.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a la paginació.
	 * @return La pàgina d'aplicacions.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public PaginaDto<AplicacioDto> aplicacioFindByFiltrePaginat(
			String filtre,
			PaginacioParamsDto paginacioParams);

	/**
	 * Crea un nou organisme per a les autoritzacions.
	 * 
	 * @param organismo
	 *            Informació de l'organisme a crear.
	 * @return L'organisme creat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public OrganismeDto organismeCreate(
			OrganismeDto organismo) throws NotFoundException;

	/**
	 * Modifica un organisme per a les autoritzacions.
	 * 
	 * @param organismo
	 *            Informació de l'organisme a crear.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap organisme amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void organismeUpdate(
			OrganismeDto organismo) throws NotFoundException;

	/**
	 * Esborra un organisme per a les autoritzacions.
	 * 
	 * @param id
	 *            Atribut id de l'organisme a esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap organisme amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void organismeDelete(
			Long id) throws NotFoundException;

	/**
	 * Consulta un organisme donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'organisme a trobar.
	 * @return L'organisme amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap organisme amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public OrganismeDto organismeFindById(Long id) throws NotFoundException;

	/**
	 * Consulta un organisme donat el seu cif.
	 * 
	 * @param id
	 *            Atribut id de l'organisme a trobar.
	 * @return L'organisme amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap organisme amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<OrganismeDto> organismeFindByCif(String cif) throws NotFoundException;
	
	/**
	 * Consulta dels organismes amb filtre i paginació.
	 * 
	 * @param filtre
	 *            El filtre per a la consulta.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació.
	 * @return La pàgina d'organismes.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public PaginaDto<OrganismeDto> organismeFindByFiltrePaginat(
			OrganismeFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	/**
	 * Crea una nova autorització per a accedir a un servei.
	 * 
	 * @param aplicacio
	 *            Informació de l'autorització a crear.
	 * @return L'autorització creada.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public AutoritzacioDto autoritzacioCreate(
			AutoritzacioDto autoritzacio) throws NotFoundException;

	/**
	 * Modifica una autorització per a accedir a un servei.
	 * 
	 * @param aplicacio
	 *            Informació de l'autorització a crear.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap autorització amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void autoritzacioUpdate(
			AutoritzacioDto autoritzacio) throws NotFoundException;

	/**
	 * Esborra una autorització per a accedir a un servei.
	 * 
	 * @param id
	 *            Atribut id de l'autorització a esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap autorització amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void autoritzacioDelete(
			Long id) throws NotFoundException;

	/**
	 * Consulta una autorització donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'autorització a trobar.
	 * @return L'autorització amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap autorització amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public AutoritzacioDto autoritzacioFindById(
			Long id) throws NotFoundException;

	/**
	 * Consulta de les autoritzacions amb paginació.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a la paginació.
	 * @return La pàgina d'autoritzacions.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public PaginaDto<AutoritzacioDto> autoritzacioFindByFiltrePaginat(
			AutoritzacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta els emissors SCSP disponibles.
	 * 
	 * @return El llistat d'emissors SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<EmisorDto> emissorFindAll();

	/**
	 * Consulta les claus públiques SCSP disponibles.
	 * 
	 * @return El llistat de claus públiques SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<ClauPublicaDto> clauPublicaFindAll();

	/**
	 * Consulta les claus privades SCSP disponibles.
	 * 
	 * @return El llistat de claus privades SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<ClauPrivadaDto> clauPrivadaFindAll();

	/**
	 * Consulta les autoritats de certificació SCSP disponibles.
	 * 
	 * @return El llistat d'autoritats de certificació SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<AutoritatCertificacioDto> autoridadCertificacionFindAll();

	/**
	 * Consulta les aplicacions disponibles.
	 * 
	 * @return El llistat d'aplicacions SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<AplicacioDto> aplicacioFindAll();

	/**
	 * Consulta els organismes disponibles.
	 * 
	 * @return El llistat d'organismes SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<OrganismeDto> organismeFindAll();

}
