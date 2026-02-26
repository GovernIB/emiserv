/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.command.ScspParametreCommand;
import es.caib.emiserv.back.helper.DatatablesHelper;
import es.caib.emiserv.back.helper.MissatgeHelper;
import es.caib.emiserv.back.helper.RequestSessionHelper;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.ScspParametreDto;
import es.caib.emiserv.logic.intf.dto.ScspParametreFiltreDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controlador per al manteniment de mòduls SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/parametres")
public class ScspParametresController extends BaseController {

    private static final String SESSION_ATTRIBUTE_FILTRE = "ScspParametresController.session.filtre";

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
        model.addAttribute(getFiltreCommand(request));
		return "scspParametresList";
	}

    @RequestMapping(method = RequestMethod.POST)
    public String post(
            HttpServletRequest request,
            @RequestParam("accio") String accio,
            @Valid ScspParametreFiltreDto filtre,
            BindingResult bindingResult,
            Model model) {
        if ("netejar".equals(accio)) {
            RequestSessionHelper.esborrarObjecteSessio(
                    request,
                    SESSION_ATTRIBUTE_FILTRE);
        } else if (!bindingResult.hasErrors()) {
            RequestSessionHelper.actualitzarObjecteSessio(
                    request,
                    SESSION_ATTRIBUTE_FILTRE,
                    filtre);
        }
        return "redirect:parametres";
    }

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesHelper.DatatablesResponse datatable(HttpServletRequest request, Model model) {
        ScspParametreFiltreDto filtre = (ScspParametreFiltreDto)RequestSessionHelper.obtenirObjecteSessio(
                request,
                SESSION_ATTRIBUTE_FILTRE);
		PaginaDto<ScspParametreDto> scspParametres = scspService.getScspParametres(DatatablesHelper.getPaginacioDtoFromRequest(request), filtre);
		scspParametres.getContingut().stream().filter(p -> p.getNombre().toUpperCase().contains("PASS")).forEach(p -> p.setValor("********"));
		DatatablesHelper.DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				scspParametres,
				"nombre");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {
		return get(request, (String)null, model);
	}

	@RequestMapping(value="/{nom}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable String nom,  Model model) {
		ScspParametreCommand command = ScspParametreCommand.toCommand((nom == null ? ScspParametreDto.builder().build() : scspService.getScspParametre(nom)));
		if (nom == null)
			command.setNou(true);
		model.addAttribute(command);
		return "scspParametreForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Model model, @Valid ScspParametreCommand command,
			BindingResult bindingResult) throws NotFoundException {
		if (bindingResult.hasErrors()) {
			return "scspParametreForm";
		}
		String messageKey = "parametres.controller.modificat.ok";
		if (command.isNou()) {
			scspService.createScspParametre(ScspParametreCommand.toDto(command));
			messageKey = "parametres.controller.creat.ok";
		} else {
			scspService.updateScspParametre(ScspParametreCommand.toDto(command));
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:parametres",
				messageKey);
	}

	@RequestMapping(value = "/{nom}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable String nom)
			throws NotFoundException {
		scspService.deleteParametre(nom);
		MissatgeHelper.success(request, getMessage(request, "parametres.controller.esborrat.ok"));
		return "redirect:../../parametres";
	}

    private ScspParametreFiltreDto getFiltreCommand(
            HttpServletRequest request) {
        ScspParametreFiltreDto filtreCommand = (ScspParametreFiltreDto) RequestSessionHelper.obtenirObjecteSessio(
                request,
                SESSION_ATTRIBUTE_FILTRE);
        if (filtreCommand == null) {
            filtreCommand = new ScspParametreFiltreDto();
            RequestSessionHelper.actualitzarObjecteSessio(
                    request,
                    SESSION_ATTRIBUTE_FILTRE,
                    filtreCommand);
        }
        return filtreCommand;
    }

}
