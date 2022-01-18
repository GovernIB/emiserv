/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.ScspModulCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.MessageHelper;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.ScspModulDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador per al manteniment de mòduls SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/modul")
public class ScspModulController extends BaseController {

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "scspModulList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesHelper.DatatablesResponse datatable(
			HttpServletRequest request) {
		PaginaDto<ScspModulDto> scspModuls = scspService.getScspModuls(DatatablesHelper.getPaginacioDtoFromRequest(request));
		scspModuls.forEach(m -> {
			String desc = MessageHelper.getInstance().getMessage("modul.descripcio." + m.getNom());
			if (desc != null)
				m.setDescripcio(desc);
		});
		DatatablesHelper.DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				scspModuls);
		return dtr;
	}

	@RequestMapping(value="/{nom}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable String nom,  Model model) throws Exception {
		ScspModulDto modul = scspService.getScspModul(nom);
		String descripcio = MessageHelper.getInstance().getMessage("modul.descripcio." + modul.getNom());
		if (descripcio != null)
			modul.setDescripcio(descripcio);
		model.addAttribute(ScspModulCommand.toCommand(modul));
		return "scspModulForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Model model, ScspModulCommand command,
			BindingResult bindingResult) throws NotFoundException {
		scspService.updateScspModul(ScspModulCommand.toDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:modul",
				"modul.controller.modificat.ok");
	}

}
