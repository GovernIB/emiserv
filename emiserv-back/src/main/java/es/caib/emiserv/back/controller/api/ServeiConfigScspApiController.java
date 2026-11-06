package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.base.controller.BaseMutableResourceController;
import es.caib.emiserv.logic.intf.model.ServeiConfigScspResource;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * API REST de la configuració SCSP dels serveis (pestanya «SCSP» de la finestra del servei de la
 * interfície REACT). L'identificador és el del servei; només es consulta i es modifica.
 *
 * @author Limit Tecnologies
 */
@RestController
@RequestMapping("/api/serveiConfigScsp")
public class ServeiConfigScspApiController extends BaseMutableResourceController<ServeiConfigScspResource, Long> {

	/** Nom del recurs a l'índex de l'API (resourceName de la interfície REACT). */
	public static final String RESOURCE_NAME = "serveiConfigScsp";

	@Override
	protected Link getIndexLink() {
		return linkTo(getClass()).withRel(RESOURCE_NAME);
	}

	@Override
	public boolean forbiddenCreateLogic() {
		return true;
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
