/**
 * 
 */
package es.caib.emiserv.logic.intf.service;

import es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaPeticioDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.ProcedimentDto;
import es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.logic.intf.dto.RedireccioRespostaDto;
import es.caib.emiserv.logic.intf.dto.ServeiDto;
import es.caib.emiserv.logic.intf.exception.ScspParseException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

/**
 * Declaració dels mètodes per a la gestió de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RedireccioService {

	/**
	 * Processa un missatge XML de petició arribat al servel de redirecció.
	 * Emmagatzema el missatge a la BBDD i calcula la URL de destí.
	 * 
	 * @param xml
	 *            El missatge XML de la petició.
	 * @return la URL del servei destí o la informació per a generar un SOAPFault.
	 * @throws ScspParseException
	 *             Si hi ha hagut error processant la petició SCSP.	
	 */
	public RedireccioProcessarResultatDto processarPeticio(
			byte[] xml) throws ScspParseException;

	/**
	 * Processa un missatge XML de resposta arribat al servel de redirecció.
	 * 
	 * @param peticioId
	 *            L'identificador de la petició rebuda.
	 * @param serveiCodi
	 *            El codi del servei de la petició rebuda.
	 * @param xml
	 *            El missatge XML de la resposta.
	 */
	public void processarResposta(
			String peticioId,
			String serveiCodi,
			byte[] xml,
			String entitatCodiRedireccio) throws Exception;

	/** 
	 * Analitza diferents missatges XML de resposta arribats del servei de redirecció múltiple i retorna la clau
	 * del missatge escollit pel ResponseResolver.
	 * 
	 * @param resultat
	 * @param xmls
	 * @return
	 */
	public String escollirResposta(
			RedireccioProcessarResultatDto resultat,
			Map<String, byte[]> xmls);

	/**
	 * Genera un missatge SOAPFault a partir del valor retornat en
	 * una cridada al mètode processarPeticio.
	 * 
	 * @param redireccioProcessarResultat
	 *            el valor retornat per la cridada a processarPeticio.
	 */
	public String generarSoapFault(
			RedireccioProcessarResultatDto redireccioProcessarResultat);

	/**
	 * Consulta els procediments rebuts en peticions d'enrutament.
	 * 
	 * @return El llistat de procediments.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<ProcedimentDto> procedimentFindAll();

	/**
	 * Consulta els serveis rebuts en peticions d'enrutament.
	 * 
	 * @return El llistat de serveis.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<ServeiDto> serveiFindAll();

	/**
	 * Consulta el llistat de peticions rebudes als backoffices.
	 * 
	 * @param filtre
	 *            El filtre per a la consulta.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació.
	 * @return La pàgina de peticions.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public PaginaDto<AuditoriaPeticioDto> peticioFindByFiltrePaginat(
			AuditoriaFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta el llistat de transmissions associades a una petició.
	 * 
	 * @param peticioId
	 *            L'atribut id de la petició.
	 * @return La llista de transmissions.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<AuditoriaSolicitudDto> solicitudFindByPeticioId(
			Long peticioId);

	/**
	 * Retorna el missatge XML rebut associat a la petició.
	 * 
	 * @param peticioId
	 *            Atribut peticionId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String peticioXmlPeticio(
			Long peticioId);

	/**
	 * Retorna el missatge XML retornat associat a la petició.
	 * 
	 * @param peticioId
	 *            Atribut peticionId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String peticioXmlResposta(
			Long peticioId);

	/**
	 * Retorna el missatge XML retornat associat a la petició.
	 *
	 * @param peticioId
	 *            Atribut peticionId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<RedireccioRespostaDto> peticioXmlRespostes(
			Long peticioId);

}
