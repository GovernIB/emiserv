/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.EntitatCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import es.caib.emiserv.logic.intf.dto.EntitatDto;
import es.caib.emiserv.logic.intf.dto.EntitatFiltreDto;
import es.caib.emiserv.logic.intf.service.EntitatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controlador per al manteniment de entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/entitat")
public class EntitatController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "EntitatController.session.filtre";

	@Autowired
	private EntitatService entitatService;

	@GetMapping
	public String get(HttpServletRequest request, Model model) {
		model.addAttribute(getFiltreCommand(request));
		return "entitatList";
	}

	@PostMapping
	public String post(
			HttpServletRequest request,
			@RequestParam("accio") String accio,
			@Valid EntitatFiltreDto filtre,
			BindingResult bindingResult,
			Model model) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		} else if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtre);
		}
		return "redirect:entitat";
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatFiltreDto filtre = (EntitatFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		return DatatablesHelper.getDatatableResponse(
				request,
				entitatService.findAllPaginat(
						filtre,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}

	@GetMapping(value = "/new")
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(request, null, model);
	}

	@GetMapping(value = "/{id}")
	public String get(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		EntitatDto entitat = null;
		if (id != null) {
			entitat = entitatService.findById(id);
			model.addAttribute(EntitatCommand.toCommand(entitat));
		} else {
			model.addAttribute(new EntitatCommand());
		}
		return "entitatForm";
	}

	@PostMapping(value = "/save")
	public String save(
			HttpServletRequest request,
			@Valid EntitatCommand command,
			BindingResult bindingResult,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "entitatForm";
		}

		if (command.getId() != null) {
			entitatService.update(EntitatCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:entitat",
					"entitat.controller.modificat.ok");
		} else {
			entitatService.create(EntitatCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:entitat",
					"entitat.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id) {
		entitatService.delete(id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.esborrat.ok");
	}

	@GetMapping(value = "/sincronitzar")
	public String sincronitzar(
			HttpServletRequest request,
			Model model) {
		try {
			entitatService.sincronitzar();
		} catch (Exception ex) {
			log.error("Error actualitzant les entitats.", ex);
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../entitat",
					ex.getMessage());
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../entitat",
				"entitat.controller.sincronitzar.ok");
	}

	private EntitatFiltreDto getFiltreCommand(
			HttpServletRequest request) {
		EntitatFiltreDto filtreCommand = (EntitatFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new EntitatFiltreDto();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
