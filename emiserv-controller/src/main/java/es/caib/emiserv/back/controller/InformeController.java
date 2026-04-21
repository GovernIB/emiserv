/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.InformeCommand;
import es.caib.emiserv.back.helper.HtmlSelectOptionHelper;
import es.caib.emiserv.back.view.InformeEmisorEnrutatExcelView;
import es.caib.emiserv.back.view.InformeGeneralEstatExcelView;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador pels informes de peticions rebudes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/informe")
public class InformeController extends BaseController {

	@Autowired
	private ExplotacioService explotacioService;
	@Autowired
	private InformeGeneralEstatExcelView informeView;
	@Autowired
	private InformeEmisorEnrutatExcelView informeEmisorEnrutatView;

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "informeList";
	}

	@RequestMapping(value = "/generalEstat", method = RequestMethod.GET)
	public String generalForm(
			HttpServletRequest request,
			Model model) {
		emplenarModelForm(request, model);
		model.addAttribute(new InformeCommand());
		return "informeGeneralForm";
	}

	@RequestMapping(value = "/generalEstat", method = RequestMethod.POST)
	public String generalPost(
			HttpServletRequest request,
			HttpServletResponse response,
			@Valid InformeCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			emplenarModelForm(request, model);
			return "informeGeneralForm";
		}
		//return "informeGeneralEstatExcelView";
		Map<String, Object> viewModel = new HashMap<String, Object>();
		viewModel.put(
				"informeDades",
				explotacioService.informeGeneralEstat(
						command.getDataInici(), 
						command.getDataFi(),
						command.getTipusPeticio()));
		informeView.render(viewModel, request, response);
		return null;
	}

	@RequestMapping(value = "/emisorEnrutat", method = RequestMethod.GET)
	public String emisorEnrutatForm(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new InformeCommand());
		return "informeEmisorEnrutatForm";
	}

	@RequestMapping(value = "/emisorEnrutat", method = RequestMethod.POST)
	public String emisorEnrutatPost(
			HttpServletRequest request,
			HttpServletResponse response,
			@Valid InformeCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			return "informeEmisorEnrutatForm";
		}
		Map<String, Object> viewModel = new HashMap<String, Object>();
		viewModel.put(
				"informeDades",
				explotacioService.informeEmisorEnrutat(
						command.getDataInici(),
						command.getDataFi()));
		informeEmisorEnrutatView.render(viewModel, request, response);
		return null;
	}

	private void emplenarModelForm(
			HttpServletRequest request,
			Model model) {
		
		// Opcions per als tipus de petició (Backoffices, enrutador, tots)
		String[] tipusPeticioOptionsValues = new String[] {
				ServeiTipusEnumDto.BACKOFFICE.toString(),
				ServeiTipusEnumDto.ENRUTADOR.toString(),
				""
		};
		String[] tipusPeticioOptionsTexts = new String[] {
				"informe.general.estat.filtre.tipus.peticio.backoffices",
				"informe.general.estat.filtre.tipus.peticio.enrutador",
				"informe.general.estat.filtre.tipus.peticio.tots"
		};
		model.addAttribute(
				"tipusPeticioOptions",
				HtmlSelectOptionHelper.getOptionsForArray(
						tipusPeticioOptionsValues,
						tipusPeticioOptionsTexts));
		
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(
						new SimpleDateFormat("dd/MM/yyyy"),
						true));
	}

}
