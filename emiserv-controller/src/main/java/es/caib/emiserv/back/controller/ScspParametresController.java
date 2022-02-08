/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.ScspParametreCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.MissatgeHelper;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.ScspParametreDto;
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
import javax.validation.Valid;

/**
 * Controlador per al manteniment de mòduls SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/parametres")
public class ScspParametresController extends BaseController {

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "scspParametresList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesHelper.DatatablesResponse datatable(
			HttpServletRequest request) {
		PaginaDto<ScspParametreDto> scspParametres = scspService.getScspParametres(DatatablesHelper.getPaginacioDtoFromRequest(request));
		scspParametres.getContingut().stream().filter(p -> p.getNombre().toUpperCase().contains("PASS")).forEach(p -> p.setValor("********"));
		DatatablesHelper.DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				scspParametres,
				"nombre");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, (String)null, model);
	}

	@RequestMapping(value="/{nom}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable String nom,  Model model) {
		ScspParametreCommand command = ScspParametreCommand.toCommand((nom == null ? ScspParametreDto.builder().build() : scspService.getScspParametre(nom)));
		if (nom == null)
			command.setNou(true);
		model.addAttribute(command);
		return "scspParametreForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Model model, @Valid ScspParametreCommand command,
			BindingResult bindingResult) throws NotFoundException {
		if (bindingResult.hasErrors()) {
			return "scspParametreForm";
		}
		String messageKey = "parametres.controller.modificat.ok";
		if (command.isNou()) {
			scspService.createScspParametre(ScspParametreCommand.toDto(command));
			messageKey = "parametres.controller.creat.ok";
		} else {
			scspService.updateScspParametre(ScspParametreCommand.toDto(command));
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:parametres",
				messageKey);
	}

	@RequestMapping(value = "/{nom}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable String nom)
			throws NotFoundException {
		scspService.deleteParametre(nom);
		MissatgeHelper.success(request, getMessage(request, "parametres.controller.esborrat.ok"));
		return "redirect:../../parametres";
	}

}
