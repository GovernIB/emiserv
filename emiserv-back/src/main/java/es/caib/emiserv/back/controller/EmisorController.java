/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.EmisorCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import es.caib.emiserv.logic.intf.dto.EmisorDto;
import es.caib.emiserv.logic.intf.dto.EmisorFiltreDto;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controlador per a la gestió dels emisors SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/emisor")
public class EmisorController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "EmisorController.session.filtre";

	@Autowired
	private ScspService scspService;

	@GetMapping
	public String get(HttpServletRequest request, Model model) {
		model.addAttribute(getFiltreCommand(request));
		return "emisorList";
	}

	@PostMapping
	public String post(
			HttpServletRequest request,
			@RequestParam("accio") String accio,
			@Valid EmisorFiltreDto filtre,
			BindingResult bindingResult,
			Model model) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		} else if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtre);
		}
		return "redirect:emisor";
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		EmisorFiltreDto filtre = (EmisorFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				scspService.emisorFindByFiltrePaginat(
						filtre,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@GetMapping(value = "/new")
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}
	@GetMapping(value = "/{id}")
	public String get(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		emplenarModelForm(id, model);
		EmisorDto emisor = null;
		if (id != null) {
			emisor = scspService.emisorFindById(id);
			model.addAttribute(EmisorCommand.toCommand(emisor));
		} else {
			model.addAttribute(new EmisorCommand());
		}
		return "emisorForm";
	}

	@PostMapping(value = "/save")
	public String save(
			HttpServletRequest request,
			@Valid EmisorCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			emplenarModelForm(
					command.getId(),
					model);
			return "emisorForm";
		}
		if (command.getId() != null) {
			scspService.emisorUpdate(EmisorCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../emisor",
					"emisor.controller.modificat.ok");
		} else {
			scspService.emisorCreate(EmisorCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../emisor",
					"emisor.controller.creat.ok");
		}
	}

	@GetMapping(value = "/{id}/delete")
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id) {
		scspService.emisorDelete(id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../emisor",
				"emisor.controller.esborrat.ok");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	private EmisorFiltreDto getFiltreCommand(
			HttpServletRequest request) {
		EmisorFiltreDto filtreCommand = (EmisorFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new EmisorFiltreDto();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	private void emplenarModelForm(
			Long id,
			Model model) {
		
	}

}
