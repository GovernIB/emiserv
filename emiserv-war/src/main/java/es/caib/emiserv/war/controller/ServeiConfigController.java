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

import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.service.ScspService;
import es.caib.emiserv.core.api.service.ServeiService;
import es.caib.emiserv.war.command.ServeiConfigScspCommand;

/**
 * Controlador per al manteniment de permisos de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei")
public class ServeiConfigController extends BaseController {

	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ScspService scspService;



	@RequestMapping(value = "/{id}/configScsp", method = RequestMethod.GET)
	public String configScspGet(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		ServeiDto servei = omplirModelPerFormulariConfigScsp(
				request,
				id,
				model);
		if (servei.isConfigurat()) {
			ServeiConfigScspCommand command = ServeiConfigScspCommand.toCommand(
					serveiService.configuracioScspFindByServei(id));
			if (servei.isXsdGestioActiva()) {
				command.setXsdGestioActiva(true);
				command.setEsquemas(servei.getXsdEsquemaBackup());
			}
			model.addAttribute(command);
		} else {
			ServeiConfigScspCommand command = new ServeiConfigScspCommand();
			command.setCodigoCertificado(servei.getCodi());
			model.addAttribute(command);
		}
		return "serveiConfigScsp";
	}

	@RequestMapping(value = "/{id}/configScsp", method = RequestMethod.POST)
	public String configScspPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Valid ServeiConfigScspCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			omplirModelPerFormulariConfigScsp(
					request,
					id,
					model);
			return "serveiConfigScsp";
		}
		serveiService.configuracioScspUpdate(
				id,
				ServeiConfigScspCommand.toDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:servei",
				"servei.controller.config.configurat.ok");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}



	private ServeiDto omplirModelPerFormulariConfigScsp(
			HttpServletRequest request,
			Long id,
			Model model) {
		model.addAttribute(
				"tipoSeguridadItems",
				new String[] {
						"XMLSignature",
						"WS-Security"
				});
		model.addAttribute(
				"emissors",
				scspService.emissorFindAll());
		model.addAttribute(
				"clausPubliques",
				scspService.clauPublicaFindAll());
		model.addAttribute(
				"clausPrivades",
				scspService.clauPrivadaFindAll());
		ServeiDto servei = serveiService.findById(id);
		model.addAttribute(
				"servei",
				servei);
		return servei;
	}

}
