package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.base.controller.BaseMutableResourceController;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * API REST del recurs de serveis de la interfície REACT.
 * <p/>
 * La creació i la modificació deleguen a ServeiService (vegeu ServeiResourceServiceImpl). L'esborrat
 * genèric no està disponible: es fa amb l'acció ESBORRAR, que delega a ServeiService igual que la JSP.
 *
 * @author Limit Tecnologies
 */
@RestController
@RequestMapping("/api/servei")
public class ServeiApiController extends BaseMutableResourceController<ServeiResource, Long> {

	/** Nom del recurs a l'índex de l'API (resourceName de la interfície REACT). */
	public static final String RESOURCE_NAME = "servei";

	@Override
	protected Link getIndexLink() {
		return linkTo(getClass()).withRel(RESOURCE_NAME);
	}

	@Override
	public boolean forbiddenPatchLogic() {
		return true;
	}
	@Override
	public boolean forbiddenDeleteLogic() {
		return true;
	}

}
