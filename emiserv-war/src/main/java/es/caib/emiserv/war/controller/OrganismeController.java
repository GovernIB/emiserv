/**
 * 
 */
package es.caib.emiserv.war.controller;

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

import es.caib.emiserv.core.api.dto.OrganismeDto;
import es.caib.emiserv.core.api.dto.OrganismeFiltreDto;
import es.caib.emiserv.core.api.service.ScspService;
import es.caib.emiserv.war.command.OrganismeCommand;
import es.caib.emiserv.war.helper.DatatablesHelper;
import es.caib.emiserv.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.war.helper.RequestSessionHelper;

/**
 * Controlador per a la gestió dels organismes SCSP.
 * 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organisme")
public class OrganismeController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "OrganismeController.session.filtre";

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
		return "redirect:organisme";
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
				scspService.organismeFindByFiltrePaginat(
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
			organisme = scspService.organismeFindById(id);
			model.addAttribute(OrganismeCommand.toCommand(organisme));
		} else {
			model.addAttribute(new OrganismeCommand());
		}
		return "organismeForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid OrganismeCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			emplenarModelForm(
					command.getId(),
					model);
			return "organismeForm";
		}
		if (command.getId() != null) {
			scspService.organismeUpdate(OrganismeCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:organisme",
					"organisme.controller.modificat.ok");
		} else {
			scspService.organismeCreate(OrganismeCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:organisme",
					"organisme.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id) {
		scspService.organismeDelete(id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../organisme",
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
