/**
 * 
 */
package es.caib.emiserv.logic.intf.service;

import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaCedentDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaFiltreDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Gestió de les consultes asíncrones processades com a peticions síncrones a cada destí: progrés,
 * pausa, represa, reintents i cancel·lació, i pausa dels cedents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConsultaAsincronaService {

	/** Consultes asíncrones amb el progrés i la data prevista de finalització. */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public PaginaDto<ConsultaAsincronaDto> findByFiltrePaginat(
			ConsultaAsincronaFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<ConsultaAsincronaSolicitudDto> solicitudFindByConsulta(
			Long consultaId) throws NotFoundException;

	/** Destins amb sol·licituds pendents i rutes dels enrutadors que accepten consultes asíncrones. */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public List<ConsultaAsincronaCedentDto> cedentFindAll();

	/** Atura l'enviament de les sol·licituds de la consulta (les que s'estan enviant acaben). */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void pausar(Long consultaId) throws NotFoundException, ValidationException;

	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void reprendre(Long consultaId) throws NotFoundException, ValidationException;

	/**
	 * Torna a enviar ara les sol·licituds amb error o amb un reintent programat d'una consulta que
	 * encara no ha finalitzat (els intents tornen a començar).
	 *
	 * @return el nombre de sol·licituds que es tornaran a enviar.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public int reintentar(Long consultaId) throws NotFoundException, ValidationException;

	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void reintentarSolicitud(Long consultaId, Long solicitudId) throws NotFoundException, ValidationException;

	/**
	 * Cancel·la les sol·licituds que no s'han enviat: finalitzen amb el codi de cancel·lació
	 * configurat i la consulta finalitza (el requeridor rep la resposta amb les ja tramitades).
	 *
	 * @return el nombre de sol·licituds cancel·lades.
	 */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public int cancelar(Long consultaId) throws NotFoundException, ValidationException;

	/** Pausa o represa de l'enviament a un cedent (ruta d'un enrutador) de totes les consultes. */
	@PreAuthorize("hasRole('EMS_ADMIN')")
	public void updateCedentPausat(Long rutaId, boolean pausada) throws NotFoundException;

}
