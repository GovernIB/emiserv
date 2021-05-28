/**
 * 
 */
package es.caib.emiserv.back.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.back.command.PermisCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.HtmlSelectOptionHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.logic.intf.dto.PermisDto;
import es.caib.emiserv.logic.intf.dto.PrincipalTipusEnumDto;
import es.caib.emiserv.logic.intf.service.ServeiService;

/**
 * Controlador per al manteniment de permisos de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei")
public class ServeiPermisController extends BaseController {

	@Autowired
	private ServeiService serveiService;

	@RequestMapping(value = "/{id}/permis", method = RequestMethod.GET)
	public String permis(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		model.addAttribute(
				"servei",
				serveiService.findById(id));
		return "serveiPermis";
	}
	@RequestMapping(value = "/{id}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long id) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				serveiService.permisFindByServei(id));
		return dtr;
	}
	/*public DatatablesPagina<PermisDto> datatable(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		return PaginacioHelper.getPaginaPerDatatables(
				request,
				serveiService.permisFindByServei(id));
	}*/

	@RequestMapping(value = "/{id}/permis/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		return get(
				request,
				id,
				null,
				model);
	}
	@RequestMapping(value = "/{id}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long permisId,
			Model model) {
		populateModelAmbServei(request, model, id);
		PermisDto permis = null;
		if (permisId != null) {
			List<PermisDto> permisos = serveiService.permisFindByServei(id);
			for (PermisDto p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		if (permis != null)
			model.addAttribute(PermisCommand.toCommand(permis));
		else
			model.addAttribute(new PermisCommand());
		return "serveiPermisForm";
	}

	@RequestMapping(value = "/{id}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long id,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			populateModelAmbServei(request, model, id);
			return "serveiPermisForm";
		}
		serveiService.permisUpdate(
				id,
				PermisCommand.toDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../servei/" + id + "/permis",
				"servei.controller.permis.modificat.ok");
	}

	@RequestMapping(value = "/{id}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long permisId,
			Model model) {
		serveiService.permisDelete(id, permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../servei/" + id + "/permis",
				"servei.controller.permis.esborrat.ok");
	}

	private void populateModelAmbServei(
			HttpServletRequest request,
			Model model,
			Long id) {
		model.addAttribute(
				"principalTipusEnumOptions",
				HtmlSelectOptionHelper.getOptionsForEnum(
						PrincipalTipusEnumDto.class,
						"principal.tipus.enum."));
		model.addAttribute(
				"servei",
				serveiService.findById(id));
	}

}
