/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.AutoritzacioCaCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.back.helper.MissatgeHelper;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controlador per al manteniment d'autoritats de certificació SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/autoritatca")
public class AutoritatCertificacioController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "AutoritatCertificacioController.session.filtre";

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) throws Exception {
		model.addAttribute(getFiltreCommand(request));
		return "autoritatcaList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@RequestParam("accio") String accio,
			@Valid AutoritatCertificacioFiltreDto filtre,
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
					filtre);
		}
		return "redirect:autoritatca";
	}

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		AutoritatCertificacioFiltreDto filtre = (AutoritatCertificacioFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		PaginaDto<AutoritatCertificacioDto> page = scspService.autoritatCertificacioFindByFiltrePaginat(filtre, DatatablesHelper.getPaginacioDtoFromRequest(request));
		return DatatablesHelper.getDatatableResponse(
				request,
				page);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) {
		return get((Long)null, model);
	}

	@RequestMapping(value = "/{autoritatcaId}", method = RequestMethod.GET)
	public String get(@PathVariable Long autoritatcaId, Model model)  throws NotFoundException {
		AutoritatCertificacioDto dto = null;
		if (autoritatcaId != null) {
			dto = scspService.autoritatCertificacioFindById(autoritatcaId);
			model.addAttribute(AutoritzacioCaCommand.asCommand(dto));
		} else {
			model.addAttribute(new AutoritzacioCaCommand());
		}
		return "autoritatcaForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Model model, @Valid AutoritzacioCaCommand command,
			BindingResult bindingResult) throws NotFoundException {
		if (bindingResult.hasErrors()) {
			return "autoritatcaForm";
		}
		if (command.getId() == null) {
			scspService.autoritatCertificacioCreate(AutoritzacioCaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:autoritatca",
					"autoritatca.controller.creat.ok");
		} else {
			scspService.autoritatCertificacioUpdate(AutoritzacioCaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:autoritatca",
					"autoritatca.controller.modificat.ok");
		}
	}

	@RequestMapping(value = "/{autoritatcaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long autoritatcaId)
			throws NotFoundException {
		scspService.autoritatCertificacioDelete(autoritatcaId);
		MissatgeHelper.success(request, getMessage(request, "autoritatca.controller.esborrat.ok"));
		return "redirect:/scsp/autoritatca";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	private AutoritatCertificacioFiltreDto getFiltreCommand(
			HttpServletRequest request) {
		AutoritatCertificacioFiltreDto filtreCommand = (AutoritatCertificacioFiltreDto) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AutoritatCertificacioFiltreDto();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
