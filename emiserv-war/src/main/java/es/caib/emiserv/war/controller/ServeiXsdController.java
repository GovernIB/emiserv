/**
 * 
 */
package es.caib.emiserv.war.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.core.api.dto.FitxerDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.dto.XsdTipusEnumDto;
import es.caib.emiserv.core.api.service.ServeiService;
import es.caib.emiserv.war.command.ServeiXsdCommand;
import es.caib.emiserv.war.helper.AjaxHelper;
import es.caib.emiserv.war.helper.AjaxHelper.AjaxFormResponse;
import es.caib.emiserv.war.helper.DatatablesHelper;
import es.caib.emiserv.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.war.helper.HtmlSelectOptionHelper;

/**
 * Controlador per al manteniment de fitxer XSD d'un servei de tipus backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei")
public class ServeiXsdController extends BaseController {

	@Autowired
	private ServeiService serveiService;



	@RequestMapping(value = "/{id}/xsd", method = RequestMethod.GET)
	public String rutesGet(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		ServeiDto servei = serveiService.findById(id);
		model.addAttribute(
				"servei",
				servei);
		model.addAttribute(
				"xsdTipusEnumOptions",
				HtmlSelectOptionHelper.getOptionsForEnum(
						XsdTipusEnumDto.class,
						"xsd.tipus.enum."));
		ServeiXsdCommand command = new ServeiXsdCommand();
		model.addAttribute(command);
		return "serveiXsd";
	}

	@RequestMapping(value = "/{id}/xsd/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long id) throws IOException {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				serveiService.xsdFindByServei(
						id),
				"tipus");
		return dtr;
	}

	@RequestMapping(value = "/{id}/xsd/new", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse xsdNew(
			HttpServletRequest request,
			@PathVariable Long id,
			@Valid ServeiXsdCommand command,
			BindingResult bindingResult) throws IOException {
		if (bindingResult.hasErrors()) {
    		return AjaxHelper.generarAjaxFormErrors(command, bindingResult);
		} else {
			byte[] contingut = null;
			if (command.getContingut() != null) {
				contingut = command.getContingut().getBytes();
			}
			serveiService.xsdCreate(
					id,
					ServeiXsdCommand.toDto(command),
					contingut);
    		return AjaxHelper.generarAjaxFormOk(command);
		}
	}
	@RequestMapping(value = "/{id}/xsd/{rutaId}", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse xsdUpdate(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable Long rutaId,
			@Valid ServeiXsdCommand command,
			BindingResult bindingResult) throws IOException {
		if (bindingResult.hasErrors()) {
			return AjaxHelper.generarAjaxFormErrors(command, bindingResult);
		} else {
			byte[] contingut = null;
			if (command.getContingut() != null) {
				contingut = command.getContingut().getBytes();
			}
			command.setId(rutaId);
			serveiService.xsdUpdate(
					id,
					ServeiXsdCommand.toDto(command),
					contingut);
			return AjaxHelper.generarAjaxFormOk(command);
		}
	}
	@RequestMapping(value = "/{id}/xsd/{tipus}/delete")
	@ResponseBody
	public void xsdDelete(
			HttpServletRequest request,
			@PathVariable Long id,
			@PathVariable XsdTipusEnumDto tipus) throws IOException {
		serveiService.xsdDelete(
				id,
				tipus);
	}
	@RequestMapping(value = "/{id}/xsd/{tipus}/download", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long id,
			@PathVariable XsdTipusEnumDto tipus) throws IOException {
		FitxerDto fitxer = serveiService.xsdDescarregarFitxer(
				id,
				tipus);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}

}
