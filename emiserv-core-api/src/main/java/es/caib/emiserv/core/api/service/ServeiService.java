/**
 * 
 */
package es.caib.emiserv.core.api.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.emiserv.core.api.dto.FitxerDto;
import es.caib.emiserv.core.api.dto.InformeGeneralEstatDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.dto.PermisDto;
import es.caib.emiserv.core.api.dto.ProcedimentDto;
import es.caib.emiserv.core.api.dto.ServeiConfigScspDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.dto.ServeiRutaDestiDto;
import es.caib.emiserv.core.api.dto.ServeiTipusEnumDto;
import es.caib.emiserv.core.api.dto.ServeiXsdDto;
import es.caib.emiserv.core.api.dto.XsdTipusEnumDto;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.exception.PermissionDeniedException;

/**
 * Declaració dels mètodes per a la gestió de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiService {

	/**
	 * Crea un nou servei.
	 * 
	 * @param servei
	 *            Informació del servei a crear.
	 * @return El servei creat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public ServeiDto create(ServeiDto servei);

	/**
	 * Actualitza la informació del servei que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param servei
	 *            Informació del servei a modificar.
	 * @return El servei modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per accedir al servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public ServeiDto update(
			ServeiDto servei) throws NotFoundException, PermissionDeniedException;

	/**
	 * Marca el servei amb l'id especificat com a actiu/inactiu.
	 * 
	 * @param id
	 *            Atribut id del servei a esborrar.
	 * @param actiu
	 *            true si es vol activar o false en cas contrari.
	 * @return El servei modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public ServeiDto updateActiu(
			Long id,
			boolean actiu) throws NotFoundException;

	/**
	 * Esborra el servei amb l'id especificat per a un usuari administrador.
	 * 
	 * @param id
	 *            Atribut id del servei a esborrar.
	 * @return El servei esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public ServeiDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta un servei donat el seu id.
	 * 
	 * @param id
	 *            Atribut id del servei a trobar.
	 * @return El servei amb l'id especificat o null si no s'ha trobat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per accedir al servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public ServeiDto findById(Long id) throws PermissionDeniedException;

	/**
	 * Consulta un servei donat el seu codi.
	 * 
	 * @param codi
	 *            Atribut codi del servei a trobar.
	 * @return El servei amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public ServeiDto findByCodi(String codi);

	/**
	 * Llistat amb tots els serveis accessibles.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a paginar i ordenar el llistat.
	 * @return El llistat de serveis.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public PaginaDto<ServeiDto> findAllPaginat(
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta les classes dels resolvers disponibles.
	 * 
	 * @return El llistat de classes.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<String> resolverClassesFindAll();

	/**
	 * Consulta les classes per discriminar respostes disponibles.
	 * 
	 * @return El llistat de classes.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<String> responseResolverClassesFindAll();

	
	/**
	 * Obté la configuració SCSP d'un servei per a un usuari
	 * administrador.
	 * 
	 * @param id
	 *            Atribut id del servei a trobar.
	 * @return La configuració SCSP del servei.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public ServeiConfigScspDto configuracioScspFindByServei(
			Long id) throws NotFoundException;

	/**
	 * Actualitza la configuració SCSP d'un servei.
	 * 
	 * @param id
	 *            Atribut id del servei a trobar.
	 * @param configuracio
	 *            La nova configuració SCSP del servei.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void configuracioScspUpdate(
			Long id,
			ServeiConfigScspDto configuracio) throws NotFoundException;

	/**
	 * Crea una ruta a dins un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @param rutaDesti
	 *            Ruta a crear.
	 * @return la ruta creada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per administrar el servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public ServeiRutaDestiDto rutaDestiCreate(
			Long id,
			ServeiRutaDestiDto rutaDesti) throws NotFoundException, PermissionDeniedException;

	/**
	 * Modifica una ruta d'un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @param rutaDesti
	 *            Ruta a modificar.
	 * @return la ruta actualitzada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per administrar el servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public ServeiRutaDestiDto rutaDestiUpdate(
			Long id,
			ServeiRutaDestiDto rutaDesti) throws NotFoundException, PermissionDeniedException;

	/**
	 * Esborra una ruta d'un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @param rutaDestiId
	 *            Id de la ruta a esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per administrar el servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void rutaDestiDelete(
			Long id,
			Long rutaDestiId) throws NotFoundException, PermissionDeniedException;

	/**
	 * Obté les rutes d'un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @return la llista paginada de rutes.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per administrar el servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public PaginaDto<ServeiRutaDestiDto> rutaDestiFindByServei(
			Long id,
			PaginacioParamsDto paginacioParams) throws NotFoundException, PermissionDeniedException;

	/** 
	 * Mou la ruta del servei amb id rutaId cap a la posició indicada
	 * reassignant el valor pel camp ordre.
	 * 
	 * @param rutaId
	 *            Atribut id de la ruta a moure.
	 * @param posicio
	 *            Nova posició de la ruta.
	 * @return true si ha anat bé o false si la posició no és correcta.
	 */
	public boolean rutaDestiMourePosicio(Long rutaId, int posicio);

	/**
	 * Crea una ruta a dins un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @param xsd
	 *            Fitxer XSD a crear.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per administrar el servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void xsdCreate(
			Long id,
			ServeiXsdDto xsd,
			byte[] contingut) throws IOException, NotFoundException, PermissionDeniedException;

	/**
	 * Modifica una ruta d'un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @param xsd
	 *            Fitxer XSD a modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per administrar el servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void xsdUpdate(
			Long id,
			ServeiXsdDto xsd,
			byte[] contingut) throws IOException, NotFoundException, PermissionDeniedException;

	/**
	 * Esborra una ruta d'un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @param tipus
	 *            Tipus del fitxer xsd a esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per administrar el servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public void xsdDelete(
			Long id,
			XsdTipusEnumDto tipus) throws IOException, NotFoundException, PermissionDeniedException;

	/**
	 * Descarrega un fitxer XSD d'un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @param tipus
	 *            Tipus del fitxer xsd per descarregar.
	 * @return el fitxer xsd.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per administrar el servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public FitxerDto xsdDescarregarFitxer(
			Long id,
			XsdTipusEnumDto tipus) throws IOException, NotFoundException, PermissionDeniedException;

	/**
	 * Obté tots els fitxers XSD associats a un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @return la llista de fitxers xsd paginada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 * @throws PermissionDeniedException
	 *             Si l'usuari no te permisos per administrar el servei.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<ServeiXsdDto> xsdFindByServei(
			Long id) throws IOException, NotFoundException, PermissionDeniedException;

	/**
	 * Consulta els procediments disponibles per administradors.
	 * 
	 * @return El llistat d'emissors SCSP.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<ProcedimentDto> procedimentFindAll();

	/**
	 * Consulta els permisos del servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<PermisDto> permisFindByServei(
			Long id) throws NotFoundException;

	/**
	 * Modifica els permisos d'un usuari o d'un rol per a un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @param permis
	 *            El permís que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void permisUpdate(
			Long id,
			PermisDto permis) throws NotFoundException;

	/**
	 * Esborra els permisos d'un usuari o d'un rol per a un servei.
	 * 
	 * @param id
	 *            Atribut id del servei.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap servei amb l'id especificat.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void permisDelete(
			Long id,
			Long permisId) throws NotFoundException;
	
	/** Retorna una llista amb la informació per generar l'informe d'estat general.
	 * 
	 * @param dataInici Filtra per data inici.
	 * @param dataFi Filtra per data fi.
	 * @param tipusPeticio Filtra per backoffices, enrutador o tots si és null.
	 * @return Retorna la llista amb la informació.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<InformeGeneralEstatDto> informeGeneralEstat(
			Date dataInici,
			Date dataFi,
			ServeiTipusEnumDto tipusPeticio);

}
