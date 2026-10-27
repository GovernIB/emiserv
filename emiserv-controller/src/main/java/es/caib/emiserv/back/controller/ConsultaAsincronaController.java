/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.ConsultaAsincronaFiltreCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.back.helper.MissatgeHelper;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.ServeiDto;
import es.caib.emiserv.logic.intf.service.ConsultaAsincronaService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador per a la gestió de les consultes asíncrones processades com a peticions síncrones a
 * cada destí: progrés, pausa, represa, reintents, cancel·lació i pausa dels cedents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/consultaAsincrona")
public class ConsultaAsincronaController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ConsultaAsincronaController.session.filtre";
	private static final String REDIRECT_LLISTAT = "redirect:/consultaAsincrona";

	@Autowired
	private ConsultaAsincronaService consultaAsincronaService;
	@Autowired
	private ServeiService serveiService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.afegirOrdre("codi", OrdreDireccioDto.ASCENDENT);
		List<ServeiDto> serveis = serveiService.findAllPaginat(null, paginacioParams).getContingut().stream().
				filter(s -> s.isAsyncSincron() || s.getTipus() == es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto.BACKOFFICE).
				collect(Collectors.toList());
		model.addAttribute("serveis", serveis);
		model.addAttribute("cedents", consultaAsincronaService.cedentFindAll());
		model.addAttribute(getFiltreCommand(request));
		return "consultaAsincronaList";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@RequestParam("accio") String accio,
			ConsultaAsincronaFiltreCommand filtreCommand) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return REDIRECT_LLISTAT;
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		return DatatablesHelper.getDatatableResponse(
				request,
				consultaAsincronaService.findByFiltrePaginat(
						ConsultaAsincronaFiltreCommand.toDto(getFiltreCommand(request)),
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}
	@RequestMapping(value = "/{id}/solicituds", method = RequestMethod.GET)
	@ResponseBody
	public List<ConsultaAsincronaSolicitudDto> solicituds(
			@PathVariable Long id) {
		return consultaAsincronaService.solicitudFindByConsulta(id);
	}

	@RequestMapping(value = "/{id}/pausar", method = RequestMethod.GET)
	public String pausar(HttpServletRequest request, @PathVariable Long id) {
		return accio(request, () -> consultaAsincronaService.pausar(id), "consulta.asincrona.controller.pausada.ok");
	}
	@RequestMapping(value = "/{id}/reprendre", method = RequestMethod.GET)
	public String reprendre(HttpServletRequest request, @PathVariable Long id) {
		return accio(request, () -> consultaAsincronaService.reprendre(id), "consulta.asincrona.controller.represa.ok");
	}
	@RequestMapping(value = "/{id}/reintentar", method = RequestMethod.GET)
	public String reintentar(HttpServletRequest request, @PathVariable Long id) {
		int[] num = new int[1];
		return accio(request, () -> num[0] = consultaAsincronaService.reintentar(id), "consulta.asincrona.controller.reintentada.ok", num);
	}
	@RequestMapping(value = "/{id}/solicitud/{solicitudId}/reintentar", method = RequestMethod.GET)
	public String reintentarSolicitud(HttpServletRequest request, @PathVariable Long id, @PathVariable Long solicitudId) {
		return accio(request, () -> consultaAsincronaService.reintentarSolicitud(id, solicitudId), "consulta.asincrona.controller.solicitud.reintentada.ok");
	}
	@RequestMapping(value = "/{id}/cancelar", method = RequestMethod.GET)
	public String cancelar(HttpServletRequest request, @PathVariable Long id) {
		int[] num = new int[1];
		return accio(request, () -> num[0] = consultaAsincronaService.cancelar(id), "consulta.asincrona.controller.cancelada.ok", num);
	}
	@RequestMapping(value = "/cedent/{rutaId}/pausar", method = RequestMethod.GET)
	public String pausarCedent(HttpServletRequest request, @PathVariable Long rutaId) {
		return accio(request, () -> consultaAsincronaService.updateCedentPausat(rutaId, true), "consulta.asincrona.controller.cedent.pausat.ok");
	}
	@RequestMapping(value = "/cedent/{rutaId}/reprendre", method = RequestMethod.GET)
	public String reprendreCedent(HttpServletRequest request, @PathVariable Long rutaId) {
		return accio(request, () -> consultaAsincronaService.updateCedentPausat(rutaId, false), "consulta.asincrona.controller.cedent.repres.ok");
	}

	private String accio(HttpServletRequest request, Runnable accio, String missatgeOk) {
		return accio(request, accio, missatgeOk, null);
	}
	private String accio(HttpServletRequest request, Runnable accio, String missatgeOk, int[] num) {
		try {
			accio.run();
			return getAjaxControllerReturnValueSuccess(
					request,
					REDIRECT_LLISTAT,
					missatgeOk,
					num != null ? new Object[] {num[0]} : null);
		} catch (Exception ex) {
			// Els errors de validació arriben embolcallats a una EJBException
			Throwable causa = ExceptionUtils.getRootCause(ex) != null ? ExceptionUtils.getRootCause(ex) : ex;
			MissatgeHelper.error(
					request,
					getMessage(request, "consulta.asincrona.controller.accio.error", new Object[] {causa.getMessage()}));
			return REDIRECT_LLISTAT;
		}
	}

	private ConsultaAsincronaFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ConsultaAsincronaFiltreCommand filtreCommand = (ConsultaAsincronaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ConsultaAsincronaFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
