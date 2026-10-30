/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.ServeiCommand;
import es.caib.emiserv.back.command.ServeiCommand.TipusBackoffice;
import es.caib.emiserv.back.command.ServeiCommand.TipusEnrutador;
import es.caib.emiserv.back.command.ServeiCommand.TipusEnrutadorMultiple;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.back.helper.HtmlSelectOptionHelper;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import es.caib.emiserv.back.validation.AditionalGroupValidator;
import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.CodiValorDto;
import es.caib.emiserv.logic.intf.dto.ServeiDto;
import es.caib.emiserv.logic.intf.dto.ServeiFiltreDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;

/**
 * Controlador per al manteniment de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei")
public class ServeiController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ServeiController.session.filtre";

	@Autowired
	private ServeiService serveiService;
	@Autowired
	private BackofficeService backofficeService;

	@Autowired(required = true)
	private Validator validator;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		model.addAttribute(getFiltreCommand(request));
		getActius(model);
		return "serveiList";
	}
	private void getActius(Model model) {
		List<CodiValorDto> actius = List.of(
				CodiValorDto.builder().codi("true").valor("comu.actiu").build(),
				CodiValorDto.builder().codi("false").valor("comu.inactiu").build()
		);
		model.addAttribute("actius", actius);
	}

	@PostMapping
	public String post(
			HttpServletRequest request,
			@RequestParam("accio") String accio,
			@Valid ServeiFiltreDto filtre,
			BindingResult bindingResult,
			Model model) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		} else if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtre);
		}
		return "redirect:servei";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		ServeiFiltreDto filtre = (ServeiFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				serveiService.findAllPaginat(
						filtre,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(
				request,
				null,
				model);
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long id,
			Model model) {
		emplenarModelForm(
				id,
				model);
		ServeiDto servei = null;
		if (id != null) {
			servei = serveiService.findById(id);
			model.addAttribute(ServeiCommand.toCommand(servei));
		} else {
			model.addAttribute(new ServeiCommand());
		}
		return "serveiForm";
	}
	@PostMapping(value = "/save")
	public String save(
			HttpServletRequest request,
			@Valid ServeiCommand command,
			BindingResult bindingResult,
			Model model) {
		if (ServeiTipusEnumDto.BACKOFFICE.equals(command.getTipus())) {
			new AditionalGroupValidator(
					validator,
					TipusBackoffice.class).validate(
							command,
							bindingResult);
		} else if (ServeiTipusEnumDto.ENRUTADOR.equals(command.getTipus())) {
			new AditionalGroupValidator(
					validator,
					TipusEnrutador.class).validate(
							command,
							bindingResult);
		} else if (ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(command.getTipus())) {
			new AditionalGroupValidator(
					validator,
					TipusEnrutadorMultiple.class).validate(
							command, 
							bindingResult);
		}
		if (bindingResult.hasErrors()) {
			emplenarModelForm(
					command.getId(),
					model);
			return "serveiForm";
		}
		if (command.getId() != null) {
			serveiService.update(ServeiCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:servei",
					"servei.controller.modificat.ok");
		} else {
			serveiService.create(ServeiCommand.toDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:servei",
					"servei.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{id}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long id) {
		serveiService.updateActiu(id, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../servei",
				"servei.controller.activat.ok");
	}
	@RequestMapping(value = "/{id}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long id) {
		serveiService.updateActiu(id, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../servei",
				"servei.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long id) {
		serveiService.delete(id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../servei",
				"servei.controller.esborrat.ok");
	}

	private void emplenarModelForm(
			Long id,
			Model model) {
		model.addAttribute(
				"serveiTipusEnumOptions",
				HtmlSelectOptionHelper.getOptionsForEnum(
						ServeiTipusEnumDto.class,
						"servei.tipus.enum."));
		model.addAttribute(
				"backofficeAutenticacioTipusEnumOptions",
				HtmlSelectOptionHelper.getOptionsForEnum(
						BackofficeAutenticacioTipusEnumDto.class,
						"backoffice.auth.tipus.enum."));
		model.addAttribute(
				"backofficeAsyncTipusEnumOptions",
				HtmlSelectOptionHelper.getOptionsForEnum(
						BackofficeAsyncTipusEnumDto.class,
						"backoffice.async.tipus.enum."));
		model.addAttribute(
				"classesResolver",
				serveiService.resolverClassesFindAll());
		model.addAttribute(
				"classesBackoffice",
				backofficeService.getBackofficeClasses());
		model.addAttribute(
				"classesResponseResolver",
				serveiService.responseResolverClassesFindAll());
	}

	private ServeiFiltreDto getFiltreCommand(
			HttpServletRequest request) {
		ServeiFiltreDto filtreCommand = (ServeiFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ServeiFiltreDto();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
