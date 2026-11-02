package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.base.controller.BaseMutableResourceController;
import es.caib.emiserv.logic.intf.model.EmisorResource;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * API REST del recurs de configuració EmisorResource de la interfície REACT.
 *
 * @author Limit Tecnologies
 */
@RestController
@RequestMapping("/api/emisor")
public class EmisorApiController extends BaseMutableResourceController<EmisorResource, Long> {

	/** Nom del recurs a l'índex de l'API (resourceName de la interfície REACT). */
	public static final String RESOURCE_NAME = "emisor";

	@Override
	protected Link getIndexLink() {
		return linkTo(getClass()).withRel(RESOURCE_NAME);
	}

	@Override
	public boolean forbiddenPatchLogic() {
		return true;
	}

}
