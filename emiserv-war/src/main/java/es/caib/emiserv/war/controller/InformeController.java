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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.emiserv.core.api.dto.ServeiTipusEnumDto;
import es.caib.emiserv.core.api.service.ServeiService;
import es.caib.emiserv.war.command.InformeCommand;
import es.caib.emiserv.war.helper.HtmlSelectOptionHelper;

/**
 * Controlador pels informes de peticions rebudes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/informe")
public class InformeController extends BaseController {
	
	@Autowired
	ServeiService serveiService;

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "informeList";
	}
	
	/** Mostra el formulari del filtre per data i tipus de peticions per a l'informe d'estat 
	 * general.*/
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
			@Valid InformeCommand command,
			BindingResult bindingResult,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			emplenarModelForm(request, model);
			return "informeGeneralForm";
		}
		model.addAttribute(
				"informeDades",
				serveiService.informeGeneralEstat(
						command.getDataInici(), 
						command.getDataFi(),
						command.getTipusPeticio()));

		return "informeGeneralEstatExcelView";
	}
	
	/** Emplena els valors per a la selecció del tipus de petició.
	 * 
	 * @param model
	 */
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
