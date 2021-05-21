package es.caib.emiserv.back.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.emiserv.back.command.UsuariCommand;
import es.caib.emiserv.back.helper.EnumHelper;
import es.caib.emiserv.back.helper.LocaleHelper;
import es.caib.emiserv.back.helper.MissatgeHelper;
import es.caib.emiserv.logic.intf.dto.IdiomaEnumDto;
import es.caib.emiserv.logic.intf.keycloak.KeycloakHelper;
import es.caib.emiserv.logic.intf.service.AplicacioService;

/**
 * Controlador per a gestionar la configuració dels usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuari")
public class UsuariController extends BaseController {

	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new UsuariCommand(
				request.getUserPrincipal().getName(),
				KeycloakHelper.getCurrentUserFullName(),
				KeycloakHelper.getCurrentUserEmail(),
				aplicacioService.getIdiomaUsuariActual()));
		addIdiomaOptionsToModel(model);
		return "usuariForm";
	}

	@RequestMapping(value = "/configuracio", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			HttpServletResponse response,
			@Valid UsuariCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			addIdiomaOptionsToModel(model);
			return "usuariForm";
		}
		aplicacioService.updateIdiomaUsuariActual(command.getIdioma());
		LocaleHelper.processarLocale(
				request,
				response,
				aplicacioService,
				true);
		MissatgeHelper.success(
				request, 
				getMessage(
						request, 
						"usuari.controller.modificat.ok"));
		return "redirect:/";
	}

	private void addIdiomaOptionsToModel(Model model) {
		model.addAttribute(
				"idiomaEnumOptions",
				EnumHelper.getOptionsForEnum(
						IdiomaEnumDto.class,
						"usuari.form.camp.idioma.enum."));
	}

}
