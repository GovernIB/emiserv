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

import es.caib.emiserv.core.api.dto.AutoritatCertificacioDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.service.ScspService;
import es.caib.emiserv.war.command.AutoritzacioCaCommand;
import es.caib.emiserv.war.helper.DatatablesHelper;
import es.caib.emiserv.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.war.helper.MissatgeHelper;


/**
 * Controlador per al manteniment de claus públiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/autoritatca")
public class AutoritatCertificacioController extends BaseController {

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) throws Exception {
		return "autoritatcaList";
	}

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) {

		PaginaDto<AutoritatCertificacioDto> page = scspService.autoritatCertificacioFindByFiltrePaginat(DatatablesHelper.getPaginacioDtoFromRequest(request));	

		return DatatablesHelper.getDatatableResponse(
				request,
				page);
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) {
		model.addAttribute(new AutoritzacioCaCommand());
		return "autoritatcaForm";
	}

	@RequestMapping(value = "/{autoritatcaId}", method = RequestMethod.GET)
	public String get(@PathVariable Long autoritatcaId, Model model)  throws NotFoundException {

		AutoritatCertificacioDto dto = null;
		if (autoritatcaId != null)
			dto = scspService.autoritatCertificacioFindById(autoritatcaId);

		if (dto != null)
			model.addAttribute(AutoritzacioCaCommand.asCommand(dto));
		else
			model.addAttribute(new AutoritzacioCaCommand());

		return "autoritatcaForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Model model, @Valid AutoritzacioCaCommand command,
			BindingResult bindingResult) throws NotFoundException {

		if (bindingResult.hasErrors()) {
			return "autoritatcaForm";
		}

		if (command.getId() == null) {
			scspService.autoritatCertificacioCreate(AutoritzacioCaCommand.asDto(command));
			MissatgeHelper.success(request, getMessage(request, "autoritatca.controller.creat.ok"));
		} else {
			scspService.autoritatCertificacioUpdate(AutoritzacioCaCommand.asDto(command));
			MissatgeHelper.success(request, getMessage(request, "autoritatca.controller.modificat.ok"));
		}

		return "redirect:/scsp/autoritatca";
	}

	@RequestMapping(value = "/{autoritatcaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long autoritatcaId)
			throws NotFoundException {

		scspService.autoritatCertificacioDelete(autoritatcaId);

		MissatgeHelper.success(request, getMessage(request, "autoritatca.controller.esborrat.ok"));

		return "redirect:/scsp/autoritatca";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

}
