/**
 * 
 */
package es.caib.emiserv.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.emiserv.core.api.dto.AuditoriaFiltreDto;
import es.caib.emiserv.core.api.dto.AuditoriaPeticioDto;
import es.caib.emiserv.core.api.dto.AuditoriaTransmisionDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.dto.ServeiConfigScspDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.exception.BackofficeException;
import es.caib.emiserv.core.api.exception.NotActiveException;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Peticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Respuesta;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudRespuesta;

/**
 * Declaració dels mètodes del servei de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BackofficeService {

	/**
	 * Consulta la informació del servei donat el seu codi.
	 * 
	 * @param codi
	 *            el codi del servei.
	 * @return la informació del servei
	 * @throws NotFoundException
	 *             si no existeix cap servei amb el codi especificat.
	 * @throws NotActiveException
	 *             si el servei no està actiu.
	 */
	public ServeiDto serveiFindByCodi(String codi) throws NotFoundException, NotActiveException;

	/**
	 * Consulta la configuració SCSP del servei.
	 * 
	 * @param codi
	 *            el codi del servei.
	 * @return la configuració del servei
	 * @throws NotFoundException
	 *             si no existeix el servei especificat.
	 * @throws NotActiveException
	 *             si el servei no està actiu.
	 */
	public ServeiConfigScspDto serveiFindConfiguracioScsp(
			String codi) throws NotFoundException, NotActiveException;

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
	 * Retorna els detalls de la petició.
	 * 
	 * @param peticioId
	 *            Atribut peticioId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public AuditoriaPeticioDto peticioFindById(
			String peticioId);

	/**
	 * Consulta el llistat de sol·licituds associades a una petició.
	 * 
	 * @param peticioId
	 *            L'atribut id de la petició.
	 * @return La llista de sol·licituds.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public List<AuditoriaTransmisionDto> solicitudFindByPeticio(
			String peticioId);

	/**
	 * Consulta una sol·licitud donat el seu id.
	 * 
	 * @param peticioId
	 *            L'atribut id de la petició.
	 * @param solicitudId
	 *            L'atribut id de la sol·licitud.
	 * @return La sol·licitud trobada.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN') or hasRole('EMS_RESP')")
	public AuditoriaTransmisionDto solicitudFindById(
			String peticioId,
			String solicitudId);

	/**
	 * Retorna la petició SCSP associada a la petició.
	 * 
	 * @param peticioId
	 *            Atribut peticioId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String peticioXmlPeticioScsp(
			String peticioId);

	/**
	 * Retorna la resposta SCSP associada a la petició.
	 * 
	 * @param peticioId
	 *            Atribut peticioId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String peticioXmlRespostaScsp(
			String peticioId);

	/**
	 * Retorna la petició al backoffice associada a la petició.
	 * 
	 * @param peticioId
	 *            Atribut peticioId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String peticioXmlPeticioBackoffice(
			String peticioId);

	/**
	 * Retorna la resposta del backoffice associada a la petició.
	 * 
	 * @param peticioId
	 *            Atribut peticioId per a identificar la petició.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String peticioXmlRespostaBackoffice(
			String peticioId);

	/**
	 * Retorna l'error de comunicació amb el backoffice associat a la petició.
	 * 
	 * @param peticioId
	 *            Atribut peticioId per a identificar la petició.
	 * @return La traça de l'error.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String peticioErrorComunicacioBackoffice(
			String peticioId);

	/**
	 * Retorna la petició al backoffice associada a la sol·licitud.
	 * 
	 * @param peticioId
	 *            Atribut peticioId per a identificar la petició.
	 * @param solicitudId
	 *            L'atribut id de la sol·licitud.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String solicitudXmlPeticioBackoffice(
			String peticioId,
			String solicitudId);

	/**
	 * Retorna la resposta del backoffice associada a la sol·licitud.
	 * 
	 * @param peticioId
	 *            Atribut peticioId per a identificar la petició.
	 * @param solicitudId
	 *            L'atribut id de la sol·licitud.
	 * @return El missatge XML.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String solicitudXmlRespostaBackoffice(
			String peticioId,
			String solicitudId);

	/**
	 * Retorna l'error de comunicació amb el backoffice associat
	 * a la sol·licitud.
	 * 
	 * @param peticioId
	 *            Atribut peticioId per a identificar la petició.
	 * @param solicitudId
	 *            L'atribut id de la sol·licitud.
	 * @return La traça d'error.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public String solicitudErrorComunicacioBackoffice(
			String peticioId,
			String solicitudId);

	/**
	 * Realitza una petició síncrona al backoffice.
	 * 
	 * @param peticion
	 *            Petició a realitzar al backoffice.
	 * @return La resposta retornada pel backoffice.
	 * @throws BackofficeException
	 *            si es produeix algun error en la comunicació amb
	 *            el backoffice..
	 */
	public Respuesta peticioBackofficeSincrona(
			Peticion peticion) throws BackofficeException;

	/**
	 * Realitza una petició asíncrona al backoffice.
	 * 
	 * @param peticion
	 *            Petició a realitzar al backoffice.
	 * @return La confirmació de la petició retornada pel backoffice.
	 * @throws BackofficeException
	 *            si es produeix algun error en la comunicació amb
	 *            el backoffice.
	 */
	public ConfirmacionPeticion peticioBackofficeAsincrona(
			Peticion peticion) throws BackofficeException;

	/**
	 * Sol·licita la resposta a una petició asíncrona al backoffice.
	 * 
	 * @param solicitudRespuesta
	 *            La sol·licitud de resposta a enviar al backoffice.
	 * @return La resposta retornada pel backoffice.
	 * @throws BackofficeException
	 *            si es produeix algun error en la comunicació amb
	 *            el backoffice.
	 */
	public Respuesta peticioBackofficeSolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) throws BackofficeException;

	/**
	 * Processa les peticions asíncrones a backoffice pendents.
	 */
	public void peticioBackofficeAsyncProcessarPendents();

}
