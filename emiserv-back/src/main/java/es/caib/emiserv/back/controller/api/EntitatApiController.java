package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.base.controller.BaseMutableResourceController;
import es.caib.emiserv.logic.intf.model.EntitatResource;
import es.caib.emiserv.logic.intf.service.EntitatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * API REST del recurs de configuració EntitatResource de la interfície REACT, amb la
 * sincronització de les entitats amb PINBAL (botó «Sincronitzar» de la JSP, EntitatController).
 *
 * @author Limit Tecnologies
 */
@Slf4j
@RestController
@RequestMapping("/api/entitat")
public class EntitatApiController extends BaseMutableResourceController<EntitatResource, Long> {

	/** Nom del recurs a l'índex de l'API (resourceName de la interfície REACT). */
	public static final String RESOURCE_NAME = "entitat";

	@Override
	protected Link getIndexLink() {
		return linkTo(getClass()).withRel(RESOURCE_NAME);
	}

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private MessageSource messageSource;

	/** Sincronitza les entitats amb les de PINBAL. Els errors tornen el missatge traduït de la JSP. */
	@PostMapping("/sincronitzar")
	public ResponseEntity<Map<String, String>> sincronitzar() {
		try {
			entitatService.sincronitzar();
			return ResponseEntity.ok(Map.of("message", traduir("entitat.controller.sincronitzar.ok")));
		} catch (Exception ex) {
			log.error("Error sincronitzant les entitats amb PINBAL", ex);
			// Com la JSP: per l'EJB, el missatge (clau) és el de la causa
			String clau = "EJBException".equals(ex.getClass().getSimpleName()) && ex.getCause() != null ?
					ex.getCause().getMessage() :
					ex.getMessage();
			String missatge = clau != null && clau.startsWith("entitat.controller.") ?
					traduir(clau) :
					traduir("entitat.controller.sincronitzar.error");
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", missatge));
		}
	}

	private String traduir(String clau) {
		return messageSource.getMessage(clau, null, clau, LocaleContextHolder.getLocale());
	}

	@Override
	public boolean forbiddenPatchLogic() {
		return true;
	}

}
