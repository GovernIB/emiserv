package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.base.controller.BaseMutableResourceController;
import es.caib.emiserv.logic.intf.model.ServeiAutoritzacioResource;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * API REST del recurs de autoritzacions dels serveis (pestanya Autoritzacions del servei) de la interfície REACT. La graella filtra per servei amb
 * {@code serveiId:<id>}.
 *
 * @author Limit Tecnologies
 */
@RestController
@RequestMapping("/api/serveiAutoritzacio")
public class ServeiAutoritzacioApiController extends BaseMutableResourceController<ServeiAutoritzacioResource, Long> {

	/** Nom del recurs a l'índex de l'API (resourceName de la interfície REACT). */
	public static final String RESOURCE_NAME = "serveiAutoritzacio";

	@Override
	protected Link getIndexLink() {
		return linkTo(getClass()).withRel(RESOURCE_NAME);
	}

	@Override
	public boolean forbiddenPatchLogic() {
		return true;
	}

}
