/**
 * 
 */
package es.caib.emiserv.war.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.emiserv.core.api.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.core.api.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.dto.ServeiTipusEnumDto;
import es.caib.emiserv.core.api.service.ServeiService;
import es.caib.emiserv.war.command.ServeiCommand;
import es.caib.emiserv.war.command.ServeiCommand.TipusBackoffice;
import es.caib.emiserv.war.command.ServeiCommand.TipusEnrutador;
import es.caib.emiserv.war.command.ServeiCommand.TipusEnrutadorMultiple;
import es.caib.emiserv.war.helper.DatatablesHelper;
import es.caib.emiserv.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.emiserv.war.helper.HtmlSelectOptionHelper;
import es.caib.emiserv.war.validation.AditionalGroupValidator;

/**
 * Controlador per al manteniment de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei")
public class ServeiController extends BaseController {

	@Autowired
	private ServeiService serveiService;

	@Autowired(required = true)
	private Validator validator;



	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "serveiList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				serveiService.findAllPaginat(
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
	@RequestMapping(method = RequestMethod.POST)
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
				findClassesBackoffice());
		model.addAttribute(
				"classesResponseResolver",
				serveiService.responseResolverClassesFindAll());
	}

	private List<String> findClassesBackoffice() {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AbstractClassTestingTypeFilter() {
			@Override
			protected boolean match(ClassMetadata metadata) {
				for (String interfaceName: metadata.getInterfaceNames()) {
					if ("es.scsp.common.backoffice.BackOffice".equals(interfaceName))
					return true;
				}
				return false;
			}
		});
		Set<BeanDefinition> components = provider.findCandidateComponents("es/caib/emiserv/backoffice");
		List<String> resposta = new ArrayList<String>();
		for (BeanDefinition component: components) {
			resposta.add(component.getBeanClassName());
		}
		return resposta;
	}

}
