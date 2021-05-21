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

import es.caib.emiserv.back.command.ClauPublicaCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.back.helper.MissatgeHelper;
import es.caib.emiserv.logic.intf.dto.ClauPublicaDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.ScspService;

/**
 * Controlador per al manteniment de claus públiques SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/claupublica")
public class ClauPublicaController extends BaseController {

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) throws Exception {
		return "clauPublicaList";
	}

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {
		PaginaDto<ClauPublicaDto> page = scspService.clauPublicaFindByFiltrePaginat(DatatablesHelper.getPaginacioDtoFromRequest(request));	
		return DatatablesHelper.getDatatableResponse(
				request,
				page);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(Model model) {
		return get((Long)null, model);
	}

	@RequestMapping(value = "/{clauPublicaId}", method = RequestMethod.GET)
	public String get(@PathVariable Long clauPublicaId, Model model)  throws NotFoundException {
		ClauPublicaDto dto = null;
		if (clauPublicaId != null) {
			dto = scspService.findClauPublicaById(clauPublicaId);
			model.addAttribute(ClauPublicaCommand.asCommand(dto));
		} else {
			model.addAttribute(new ClauPublicaCommand());
		}
		return "clauPublicaForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Model model, @Valid ClauPublicaCommand command,
			BindingResult bindingResult) throws NotFoundException {
		if (bindingResult.hasErrors()) {
			return "clauPublicaForm";
		}
		if (command.getId() == null) {
			scspService.clauPublicaCreate(ClauPublicaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:claupublica",
					"claupublica.controller.creat.ok");
		} else {
			scspService.clauPublicaUpdate(ClauPublicaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:claupublica",
					"claupublica.controller.modificat.ok");
		}
	}

	@RequestMapping(value = "/{clauPublicaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long clauPublicaId)
			throws NotFoundException {
		scspService.clauPublicaDelete(clauPublicaId);
		MissatgeHelper.success(request, getMessage(request, "claupublica.controller.esborrat.ok"));
		return "redirect:../../claupublica";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

}
