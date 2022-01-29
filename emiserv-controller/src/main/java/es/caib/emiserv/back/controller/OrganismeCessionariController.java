/**
 * 
 */
package es.caib.emiserv.back.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
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

import es.caib.emiserv.back.command.OrganismeCessionariCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import es.caib.emiserv.logic.intf.dto.OrganismeFiltreDto;
import es.caib.emiserv.logic.intf.service.ScspService;

/**
 * Controlador per a la gestió dels organismes SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organismeCessionari")
public class OrganismeCessionariController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "OrganismeCessionariController.session.filtre";

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				getFiltreCommand(request));
		return "organismeList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@RequestParam("accio") String accio,
			@Valid OrganismeFiltreDto filtre,
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
		return "redirect:organismeCessionari";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		OrganismeFiltreDto filtre = (OrganismeFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				scspService.organismeCessionariFindByFiltrePaginat(
						filtre,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(
				request,
				null,
				model);
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		emplenarModelForm(
				id,
				model);
		OrganismeDto organisme = null;
		if (id != null) {
			organisme = scspService.organismeCessionariFindById(id);
			model.addAttribute(OrganismeCessionariCommand.toCommand(organisme));
		} else {
			model.addAttribute(new OrganismeCessionariCommand());
		}
		return "organismeForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid OrganismeCessionariCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			emplenarModelForm(
					command.getId(),
					model);
			return "organismeForm";
		}
		if (command.getId() != null) {
			scspService.organismeCessionariUpdate(OrganismeCessionariCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../organismeCessionari",
					"organisme.controller.modificat.ok");
		} else {
			scspService.organismeCessionariCreate(OrganismeCessionariCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../organismeCessionari",
					"organisme.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id) {
		scspService.organismeCessionariDelete(id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../organismeCessionari",
				"organisme.controller.esborrat.ok");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	private OrganismeFiltreDto getFiltreCommand(
			HttpServletRequest request) {
		OrganismeFiltreDto filtreCommand = (OrganismeFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new OrganismeFiltreDto();
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
