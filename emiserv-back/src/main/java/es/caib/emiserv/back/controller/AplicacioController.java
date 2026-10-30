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
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.back.command.AplicacioCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.logic.intf.dto.AplicacioDto;
import es.caib.emiserv.logic.intf.service.ScspService;

/**
 * Controlador per a la gestió de les aplicacions SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/aplicacio")
public class AplicacioController extends BaseController {

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "aplicacioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				scspService.aplicacioFindByFiltrePaginat(
						null,
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
			@PathVariable Integer id,
			Model model) {
		AplicacioDto aplicacio = null;
		if (id != null) {
			aplicacio = scspService.aplicacioFindById(id);
			model.addAttribute(AplicacioCommand.toCommand(aplicacio));
		} else {
			model.addAttribute(new AplicacioCommand());
		}
		emplenarModelForm(model);
		return "aplicacioForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid AplicacioCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			emplenarModelForm(model);
			return "aplicacioForm";
		}
		if (command.getId() != null) {
			scspService.aplicacioUpdate(AplicacioCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:aplicacio",
					"aplicacio.controller.modificat.ok");
		} else {
			scspService.aplicacioCreate(AplicacioCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:aplicacio",
					"aplicacio.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Integer id) {
		scspService.aplicacioDelete(id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../aplicacio",
				"aplicacio.controller.esborrat.ok");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	private void emplenarModelForm(
			Model model) {
		model.addAttribute(
				"autoritatsCertificacio",
				scspService.autoridadCertificacionFindAll());
	}

}
