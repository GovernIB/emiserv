/**
 * 
 */
package es.caib.emiserv.back.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.back.command.AuditoriaFiltreCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.logic.intf.dto.AuditoriaTransmisionDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.caib.emiserv.logic.intf.service.ServeiService;

/**
 * Controlador per a l'auditoria de peticions realitzades als serveis
 * de tipus backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/auditoriaBackoffice")
public class AuditoriaBackofficeController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "AuditoriaBackofficeController.session.filtre";

	@Autowired
	private BackofficeService backofficeService;
	@Autowired
	private ServeiService serveiService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.afegirOrdre(
				"nom",
				OrdreDireccioDto.ASCENDENT);
		model.addAttribute(
				"procediments",
				serveiService.procedimentFindAll());
		model.addAttribute(
				"serveis",
				serveiService.findAllPaginat(paginacioParams).getContingut());
		model.addAttribute(
				getFiltreCommand(request));
		return "auditoriaBackofficeList";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@RequestParam("accio") String accio,
			@Valid AuditoriaFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		} else if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return "redirect:auditoriaBackoffice";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				backofficeService.peticioFindByFiltrePaginat(
						AuditoriaFiltreCommand.toDto(
								getFiltreCommand(request)),
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/{peticioId}/solicituds", method = RequestMethod.GET)
	@ResponseBody
	public List<AuditoriaTransmisionDto> transmissions(
			HttpServletRequest request,
			@PathVariable String peticioId,
			Model model) {
		return backofficeService.solicitudFindByPeticio(peticioId);
	}

	@RequestMapping(value = "/{peticioId}/detall", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String peticioId,
			Model model) {
		model.addAttribute(
				"peticio",
				backofficeService.peticioFindById(peticioId));
		return "auditoriaBackofficePeticioDetall";
	}

	@RequestMapping(value = "/{peticioId}/xmlPeticioScsp", method = RequestMethod.GET)
	@ResponseBody
	public String xmlPeticioScsp(
			HttpServletRequest request,
			@PathVariable String peticioId) {
		return backofficeService.peticioXmlPeticioScsp(peticioId);
	}
	@RequestMapping(value = "/{peticioId}/xmlRespostaScsp", method = RequestMethod.GET)
	@ResponseBody
	public String xmlRespostaScsp(
			HttpServletRequest request,
			@PathVariable String peticioId) {
		return backofficeService.peticioXmlRespostaScsp(peticioId);
	}

	@RequestMapping(value = "/{peticioId}/xmlPeticioBackoffice", method = RequestMethod.GET)
	@ResponseBody
	public String xmlPeticioBackoffice(
			HttpServletRequest request,
			@PathVariable String peticioId) {
		return backofficeService.peticioXmlPeticioBackoffice(peticioId);
	}
	@RequestMapping(value = "/{peticioId}/xmlRespostaBackoffice", method = RequestMethod.GET)
	@ResponseBody
	public String xmlRespostaBackoffice(
			HttpServletRequest request,
			@PathVariable String peticioId) {
		return backofficeService.peticioXmlRespostaBackoffice(peticioId);
	}
	@RequestMapping(value = "/{peticioId}/errorComunicacioBackoffice", method = RequestMethod.GET)
	@ResponseBody
	public String errorComunicacioBackoffice(
			HttpServletRequest request,
			@PathVariable String peticioId) {
		return backofficeService.peticioErrorComunicacioBackoffice(peticioId);
	}

	@RequestMapping(value = "/{peticioId}/solicitud/{solicitudId}/detall", method = RequestMethod.GET)
	public String solicitudDetall(
			HttpServletRequest request,
			@PathVariable String peticioId,
			@PathVariable String solicitudId,
			Model model) {
		model.addAttribute(
				"solicitud",
				backofficeService.solicitudFindById(peticioId, solicitudId));
		return "auditoriaBackofficeSolicitudDetall";
	}

	@RequestMapping(value = "/{peticioId}/solicitud/{solicitudId}/xmlPeticioBackoffice", method = RequestMethod.GET)
	@ResponseBody
	public String xmlPeticioBackoffice(
			HttpServletRequest request,
			@PathVariable String peticioId,
			@PathVariable String solicitudId) {
		return backofficeService.solicitudXmlPeticioBackoffice(
				peticioId,
				solicitudId);
	}
	@RequestMapping(value = "/{peticioId}/solicitud/{solicitudId}/xmlRespostaBackoffice", method = RequestMethod.GET)
	@ResponseBody
	public String xmlRespostaBackoffice(
			HttpServletRequest request,
			@PathVariable String peticioId,
			@PathVariable String solicitudId) {
		return backofficeService.solicitudXmlRespostaBackoffice(
				peticioId,
				solicitudId);
	}
	@RequestMapping(value = "/{peticioId}/solicitud/{solicitudId}/errorComunicacioBackoffice", method = RequestMethod.GET)
	@ResponseBody
	public String errorComunicacioBackoffice(
			HttpServletRequest request,
			@PathVariable String peticioId,
			@PathVariable String solicitudId) {
		return backofficeService.solicitudErrorComunicacioBackoffice(
				peticioId,
				solicitudId);
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(
						new SimpleDateFormat("dd/MM/yyyy"),
						true));
	}

	private AuditoriaFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		AuditoriaFiltreCommand filtreCommand = (AuditoriaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AuditoriaFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
