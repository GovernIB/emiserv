/**
 * 
 */
package es.caib.emiserv.core.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.emiserv.core.api.dto.AuditoriaFiltreDto;
import es.caib.emiserv.core.api.dto.AuditoriaPeticioDto;
import es.caib.emiserv.core.api.dto.AuditoriaSolicitudDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.dto.ProcedimentDto;
import es.caib.emiserv.core.api.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.exception.ScspParseException;

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
			byte[] xml) throws Exception;

	/** 
	 * Analitza diferents missatges XML de resposta arribats del servei de redirecció múltiple i retorna la clau
	 * del missatge escollit pel ResponseResolver.
	 * 
	 * @param atributPeticioId
	 * @param atributCodigoCertificado
	 * @param respostesAProcessar
	 * @see ResponseResolver
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
	 * @param peticionId
	 *            L'atribut id de la petició.
	 * @return La llista de transmissions.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<AuditoriaSolicitudDto> solicitudFindByPeticioId(
			Long peticioId);

	/**
	 * Retorna el missatge XML rebut associat a la petició.
	 * 
	 * @param peticionId
	 *            Atribut peticionId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String peticioXmlPeticio(
			Long peticioId);

	/**
	 * Retorna el missatge XML retornat associat a la petició.
	 * 
	 * @param peticionId
	 *            Atribut peticionId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String peticioXmlResposta(
			Long peticioId);

}
