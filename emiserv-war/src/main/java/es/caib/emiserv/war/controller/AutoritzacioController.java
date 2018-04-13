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
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.core.api.dto.AutoritzacioDto;
import es.caib.emiserv.core.api.service.ScspService;
import es.caib.emiserv.core.api.service.ServeiService;
import es.caib.emiserv.war.command.AutoritzacioCommand;
import es.caib.emiserv.war.helper.DatatablesHelper;
import es.caib.emiserv.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per a la gestió de les aplicacions SCSP.
 * 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei/{serveiId}/autoritzacio")
public class AutoritzacioController extends BaseController {

	@Autowired
	private ScspService scspService;
	@Autowired
	private ServeiService serveiService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			@PathVariable Long serveiId,
			Model model) {
		model.addAttribute(
				"servei",
				serveiService.findById(serveiId));
		return "serveiAutoritzacioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			@PathVariable Long serveiId,
			HttpServletRequest request) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				scspService.autoritzacioFindByServeiPaginat(
						serveiId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long serveiId,
			Model model) {
		return get(
				request,
				serveiId,
				null,
				model);
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long serveiId,
			@PathVariable Long id,
			Model model) {
		AutoritzacioDto autoritzacio = null;
		if (id != null) {
			autoritzacio = scspService.autoritzacioFindById(id);
			model.addAttribute(AutoritzacioCommand.toCommand(autoritzacio));
		} else {
			model.addAttribute(new AutoritzacioCommand());
		}
		emplenarModelForm(serveiId, model);
		return "serveiAutoritzacioForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long serveiId,
			@Valid AutoritzacioCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			emplenarModelForm(serveiId, model);
			return "serveiAutoritzacioForm";
		}
		AutoritzacioDto dto = AutoritzacioCommand.toDto(command);
		dto.setServeiId(serveiId);
		if (command.getId() != null) {
			scspService.autoritzacioUpdate(dto);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:autoritzacio",
					"autoritzacio.controller.modificat.ok");
		} else {
			scspService.autoritzacioCreate(dto);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:autoritzacio",
					"autoritzacio.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long serveiId,
			@PathVariable Long id) {
		scspService.autoritzacioDelete(id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../autoritzacio",
				"autoritzacio.controller.esborrat.ok");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}



	private void emplenarModelForm(
			Long serveiId,
			Model model) {
		model.addAttribute(
				"servei",
				serveiService.findById(serveiId));
		model.addAttribute(
				"aplicacions",
				scspService.aplicacioFindAll());
		model.addAttribute(
				"organismes",
				scspService.organismeFindAll());
	}

}
