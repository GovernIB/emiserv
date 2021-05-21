/**
 * 
 */
package es.caib.emiserv.logic.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.emiserv.logic.intf.dto.AplicacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritzacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritzacioFiltreDto;
import es.caib.emiserv.logic.intf.dto.ClauPrivadaDto;
import es.caib.emiserv.logic.intf.dto.ClauPublicaDto;
import es.caib.emiserv.logic.intf.dto.EmisorDto;
import es.caib.emiserv.logic.intf.dto.OrganismeCessionariDto;
import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import es.caib.emiserv.logic.intf.dto.OrganismeFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;

/**
 * Mètodes per a gestionar else manteniments d'informació SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspService {

	public static final String APP_PROPERTIES = "es.caib.emiserv.properties";
	public static final String APP_SYSTEM_PROPERTIES = "es.caib.emiserv.system.properties";

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

	
	
	// Funcions de la taula de organisme cessionari
	
	
	/**
	 * Llistat amb totes els organismes cessionaris
	 * 
	 * @return Un llistat dels organismes cessionaris
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<OrganismeCessionariDto> findAllOrganismeCessionari();

	
	
	// Funcions de la taula de claus públiques.
	
	
	/**
	 * Consulta les claus públiques SCSP disponibles.
	 * 
	 * @return El llistat de claus públiques SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<ClauPublicaDto> clauPublicaFindAll();

	/**
	 * Consulta les claus públiques SCSP disponibles amb paginació.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a la paginació.
	 * @return La pàgina de claus públiques SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	PaginaDto<ClauPublicaDto> clauPublicaFindByFiltrePaginat(PaginacioParamsDto paginacioParams);

	ClauPublicaDto findClauPublicaById(Long id) throws NotFoundException;
	ClauPublicaDto clauPublicaCreate(ClauPublicaDto item);
	ClauPublicaDto clauPublicaUpdate(ClauPublicaDto item) throws NotFoundException;
	void clauPublicaDelete(Long id) throws NotFoundException;

	

	// Funcions de la taula de clau privada

	
	/**
	 * Consulta les claus privades SCSP disponibles amb paginació.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a la paginació.
	 * @return La pàgina de claus privades SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	PaginaDto<ClauPrivadaDto> clauPrivadaFindByFiltrePaginat(PaginacioParamsDto paginacioParams);
	
	ClauPrivadaDto findClauPrivadaById(Long id) throws NotFoundException;
	ClauPrivadaDto clauPrivadaCreate(ClauPrivadaDto item);
	ClauPrivadaDto clauPrivadaUpdate(ClauPrivadaDto item) throws NotFoundException;
	void clauPrivadaDelete(Long id) throws NotFoundException;
	
	/**
	 * Consulta les claus privades SCSP disponibles.
	 * 
	 * @return El llistat de claus privades SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<ClauPrivadaDto> clauPrivadaFindAll();

	// Autoritats de certificació

	/**
	 * Consulta les autoritats de certificació SCSP disponibles.
	 * 
	 * @return El llistat d'autoritats de certificació SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<AutoritatCertificacioDto> autoridadCertificacionFindAll();

	/**
	 * Consulta les claus privades SCSP disponibles amb paginació.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a la paginació.
	 * @return La pàgina de claus privades SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	PaginaDto<AutoritatCertificacioDto> autoritatCertificacioFindByFiltrePaginat(PaginacioParamsDto paginacioParams);
	
	AutoritatCertificacioDto autoritatCertificacioFindById(Long id) throws NotFoundException;
	AutoritatCertificacioDto autoritatCertificacioCreate(AutoritatCertificacioDto item);
	AutoritatCertificacioDto autoritatCertificacioUpdate(AutoritatCertificacioDto item) throws NotFoundException;
	void autoritatCertificacioDelete(Long id) throws NotFoundException;
	
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

	/**
	 * Propaga les propietats dels fitxers de properties de EMISERV a la
	 * taula core_parametro_configuracion.
	 */
	public void propagateScspPropertiesToDb();

}
