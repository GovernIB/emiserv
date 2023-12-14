/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.AuditoriaFiltreCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import es.caib.emiserv.logic.intf.dto.AuditoriaPeticioDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.emiserv.logic.intf.service.RedireccioService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controlador per a l'auditoria de peticions realitzades als serveis
 * de tipus enrutador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/auditoriaEnrutador")
public class AuditoriaEnrutadorController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "AuditoriaEnrutadorController.session.filtre";

	@Autowired
	private RedireccioService redireccioService;

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
				redireccioService.procedimentFindAll());
		model.addAttribute(
				"serveis",
				redireccioService.serveiFindAll());
		model.addAttribute(
				getFiltreCommand(request));
		return "auditoriaEnrutadorList";
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
		return "redirect:auditoriaEnrutador";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				redireccioService.peticioFindByFiltrePaginat(
						AuditoriaFiltreCommand.toDto(
								getFiltreCommand(request)),
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/solicituds/{peticioId}", method = RequestMethod.GET)
	@ResponseBody
	public List<AuditoriaSolicitudDto> transmissions(
			HttpServletRequest request,
			@PathVariable Long peticioId,
			Model model) {
		return redireccioService.solicitudFindByPeticioId(peticioId);
	}

	@RequestMapping(value = "/{idPeticio}/detall", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long idPeticio,
			Model model) {
		AuditoriaPeticioDto peticio = redireccioService.peticioFindById(idPeticio);
		model.addAttribute("peticio", peticio);
		if (peticio.isTeRespostes()) {
			model.addAttribute("missatgesXml", redireccioService.peticioXmlRespostes(idPeticio));
		}
		return "auditoriaEnrutadorPeticioDetall";
	}

	@RequestMapping(value = "/{peticioId}/xmlPeticio", method = RequestMethod.GET)
	public String xmlPeticio(
			HttpServletRequest request,
			@PathVariable Long peticioId,
			Model model) {
		model.addAttribute(
				"missatgeXml",
				redireccioService.peticioXmlPeticio(peticioId));
		return "missatgeXml";
	}
	@RequestMapping(value = "/{peticioId}/xmlPeticioRest", method = RequestMethod.GET)
	@ResponseBody
	public String xmlPeticioRest(
			HttpServletRequest request,
			@PathVariable Long peticioId) {
		return redireccioService.peticioXmlPeticio(peticioId);
	}

	@RequestMapping(value = "/{peticioId}/xmlResposta", method = RequestMethod.GET)
	public String xmlResposta(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long peticioId,
			Model model) {
		model.addAttribute(
				"missatgeXml",
				redireccioService.peticioXmlResposta(peticioId));
		return "missatgeXml";
	}
	@RequestMapping(value = "/{peticioId}/xmlRespostaRest", method = RequestMethod.GET)
	@ResponseBody
	public String xmlRespostaRest(
			HttpServletRequest request,
			@PathVariable Long peticioId) {
		return redireccioService.peticioXmlResposta(peticioId);
	}
	@RequestMapping(value = "/{peticioId}/xmlRespostes", method = RequestMethod.GET)
	public String xmlRespostes(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long peticioId,
			Model model) {
		var missatges = redireccioService.peticioXmlRespostes(peticioId);
		model.addAttribute("missatgesXml", missatges);
		return "missatgesXml";
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
