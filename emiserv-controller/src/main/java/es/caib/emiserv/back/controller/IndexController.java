/**
 * 
 */
package es.caib.emiserv.back.controller;

import es.caib.emiserv.back.helper.AjaxHelper;
import es.caib.emiserv.back.helper.ModalHelper;
import es.caib.emiserv.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Controlador amb utilitats per a l'aplicació EMISERV.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class IndexController {

	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root(HttpServletRequest request) {
		return "redirect:/servei";
	}

	@RequestMapping(value = ModalHelper.ACCIO_MODAL_TANCAR, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void modalTancar() {
	}

	@RequestMapping(value = AjaxHelper.ACCIO_AJAX_OK, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void ajaxOk() {
	}

	@RequestMapping(value = "/missatges", method = RequestMethod.GET)
	public String get() {
		return "util/missatges";
	}

//	@PostConstruct
//	public void propagateScspPropertiesToDb() {
//		scspService.propagateScspPropertiesToDb();
//	}
	@PostConstruct
	public void propagateDbProperties() {
		aplicacioService.propagateDbProperties();
	}

//	@GetMapping(value = "/properties")
//	@ResponseBody
//	public Map<String, String> getPRoperties() {
//		return aplicacioService.readProperties();
//	}

//	@GetMapping(value = "/api/swagger-ui/api-docs/swagger-config")
//	public String getSwaggerCongig() {
//		return "redirect:/api-docs-swagger-config";
//	}
}
