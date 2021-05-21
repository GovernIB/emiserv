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

import es.caib.emiserv.back.command.ServeiRutaDestiCommand;
import es.caib.emiserv.back.helper.AjaxHelper;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.AjaxHelper.AjaxFormResponse;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.logic.intf.dto.ServeiDto;
import es.caib.emiserv.logic.intf.service.ServeiService;

/**
 * Controlador per al manteniment de rutes d'un servei de tipus enrutador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei")
public class ServeiRutesController extends BaseController {

	@Autowired
	private ServeiService serveiService;

	@RequestMapping(value = "/{id}/rutes", method = RequestMethod.GET)
	public String rutesGet(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		ServeiDto servei = serveiService.findById(id);
		model.addAttribute(
				"servei",
				servei);
		ServeiRutaDestiCommand command = new ServeiRutaDestiCommand();
		model.addAttribute(command);
		return "serveiRutes";
	}

	@RequestMapping(value = "/{id}/rutes/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long id) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				serveiService.rutaDestiFindByServei(
						id,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{id}/rutes/new", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse rutesNew(
			HttpServletRequest request,
			@PathVariable Long id,
			@Valid ServeiRutaDestiCommand command,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return AjaxHelper.generarAjaxFormErrors(command, bindingResult);
		} else {
			serveiService.rutaDestiCreate(
					id,
					ServeiRutaDestiCommand.toDto(command));
			return AjaxHelper.generarAjaxFormOk(command);
		}
	}
	@RequestMapping(value = "/{id}/rutes/{rutaId}", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse rutesUpdate(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long rutaId,
			@Valid ServeiRutaDestiCommand command,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return AjaxHelper.generarAjaxFormErrors(command, bindingResult);
		} else {
			command.setId(rutaId);
			serveiService.rutaDestiUpdate(
					id,
					ServeiRutaDestiCommand.toDto(command));
			return AjaxHelper.generarAjaxFormOk(command);
		}
	}
	@RequestMapping(value = "/{id}/rutes/{rutaId}/delete")
	@ResponseBody
	public void rutesDelete(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long rutaId) {
		serveiService.rutaDestiDelete(
				id,
				rutaId);
	}

	/**
	 *  Mètode Ajax per moure una ruta d'un servei de posició.
	 * 
	 * @param request
	 * @param serveiId
	 * @param rutaId
	 * @param posicio
	 * @return
	 */
	@RequestMapping(value = "/{id}/rutes/{rutaId}/moure/{posicio}")
	@ResponseBody
	public boolean rutesMove(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long rutaId,
			@PathVariable int posicio) {
		
		return serveiService.rutaDestiMourePosicio(rutaId, posicio);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

}
