package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.base.controller.BaseMutableResourceController;
import es.caib.emiserv.logic.intf.model.ScspModulResource;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * API REST del recurs de configuració ScspModulResource de la interfície REACT.
 *
 * @author Limit Tecnologies
 */
@RestController
@RequestMapping("/api/scspModul")
public class ScspModulApiController extends BaseMutableResourceController<ScspModulResource, String> {

	/** Nom del recurs a l'índex de l'API (resourceName de la interfície REACT). */
	public static final String RESOURCE_NAME = "scspModul";

	@Override
	protected Link getIndexLink() {
		return linkTo(getClass()).withRel(RESOURCE_NAME);
	}

	@Override
	public boolean forbiddenPatchLogic() {
		return true;
	}

	@Override
	public boolean forbiddenCreateLogic() {
		return true;
	}

	@Override
	public boolean forbiddenDeleteLogic() {
		return true;
	}

}
