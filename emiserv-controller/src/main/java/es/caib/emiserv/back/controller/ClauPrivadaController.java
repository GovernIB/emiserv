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

import es.caib.emiserv.back.command.ClauPrivadaCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.back.helper.MissatgeHelper;
import es.caib.emiserv.logic.intf.dto.ClauPrivadaDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.ScspService;

/**
 * Controlador per al manteniment de claus privades SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/clauprivada")
public class ClauPrivadaController extends BaseController {

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		return "clauPrivadaList";
	}

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		PaginaDto<ClauPrivadaDto> page = scspService.clauPrivadaFindByFiltrePaginat(DatatablesHelper.getPaginacioDtoFromRequest(request));	
		return DatatablesHelper.getDatatableResponse(
				request,
				page);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) {
		return get((Long)null, model);
	}

	@RequestMapping(value = "/{clauPrivadaId:.+}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long clauPrivadaId,
			Model model) {
		ClauPrivadaDto dto = null;
		if (clauPrivadaId != null) {
			dto = scspService.findClauPrivadaById(clauPrivadaId);
			model.addAttribute(ClauPrivadaCommand.asCommand(dto));
		} else {
			model.addAttribute(new ClauPrivadaCommand());
		}
		emplenarModelForm(model);
		return "clauPrivadaForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			Model model,
			@Valid ClauPrivadaCommand command,
			BindingResult bindingResult) throws NotFoundException {
		if (bindingResult.hasErrors()) {
			emplenarModelForm(model);
			return "clauPrivadaForm";
		}
		if (command.getId() == null) {
			scspService.clauPrivadaCreate(ClauPrivadaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:clauprivada",
					"clau.privada.controller.creat.ok");
		} else {
			scspService.clauPrivadaUpdate(ClauPrivadaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:clauprivada",
					"clau.privada.controller.modificat.ok");
		}
	}

	@RequestMapping(value = "/{clauPrivadaId:.+}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long clauPrivadaId) throws NotFoundException {
		scspService.clauPrivadaDelete(clauPrivadaId);
		MissatgeHelper.success(
				request, 
				getMessage(
						request, 
						"clau.privada.controller.esborrat.ok"));
		return "redirect:/scsp/clauprivada";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	private void emplenarModelForm(
			Model model) {
		model.addAttribute("organismes", scspService.findAllOrganismeCessionari());
	}

}
