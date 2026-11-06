package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.base.controller.BaseMutableResourceController;
import es.caib.emiserv.logic.intf.dto.AuditoriaTransmisionDto;
import es.caib.emiserv.logic.intf.dto.ProcedimentDto;
import es.caib.emiserv.logic.intf.model.AuditoriaBackofficeResource;
import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * API REST de l'auditoria de les peticions de backoffice de la interfície REACT. El recurs és de
 * només lectura (llistat, detall i exportació); les sol·licituds, els missatges XML i els procediments
 * del filtre es consulten amb els mateixos mètodes de BackofficeService i ServeiService que la JSP
 * (AuditoriaBackofficeController), que en comproven els permisos.
 *
 * @author Limit Tecnologies
 */
@RestController
@RequestMapping("/api/auditoriaBackoffice")
public class AuditoriaBackofficeApiController extends BaseMutableResourceController<AuditoriaBackofficeResource, String> {

	public static final String RESOURCE_NAME = "auditoriaBackoffice";

	/** Missatges d'una petició. */
	public enum MissatgePeticio { PETICIO_SCSP, RESPOSTA_SCSP, PETICIO_BACKOFFICE, RESPOSTA_BACKOFFICE, ERROR_COMUNICACIO }
	/** Missatges d'una sol·licitud. */
	public enum MissatgeSolicitud { PETICIO_BACKOFFICE, RESPOSTA_BACKOFFICE, ERROR_COMUNICACIO }

	@Autowired
	private BackofficeService backofficeService;
	@Autowired
	private ServeiService serveiService;

	@Override
	protected Link getIndexLink() {
		return linkTo(getClass()).withRel(RESOURCE_NAME);
	}

	@GetMapping("/procediments")
	public List<ProcedimentDto> procediments() {
		return serveiService.procedimentFindAll();
	}

	@GetMapping("/{id}/solicituds")
	public List<AuditoriaTransmisionDto> solicituds(@PathVariable String id) {
		comprovarAcces(id);
		return backofficeService.solicitudFindByPeticio(id);
	}

	@GetMapping("/{id}/missatges/{missatge}")
	public ResponseEntity<String> missatge(@PathVariable String id, @PathVariable MissatgePeticio missatge) {
		comprovarAcces(id);
		String text;
		switch (missatge) {
			case PETICIO_SCSP:
				text = backofficeService.peticioXmlPeticioScsp(id);
				break;
			case RESPOSTA_SCSP:
				text = backofficeService.peticioXmlRespostaScsp(id);
				break;
			case PETICIO_BACKOFFICE:
				text = backofficeService.peticioXmlPeticioBackoffice(id);
				break;
			case RESPOSTA_BACKOFFICE:
				text = backofficeService.peticioXmlRespostaBackoffice(id);
				break;
			default:
				text = backofficeService.peticioErrorComunicacioBackoffice(id);
		}
		return text(text);
	}

	@GetMapping("/{id}/solicituds/{solicitudId}/missatges/{missatge}")
	public ResponseEntity<String> missatgeSolicitud(
			@PathVariable String id,
			@PathVariable String solicitudId,
			@PathVariable MissatgeSolicitud missatge) {
		comprovarAcces(id);
		String text;
		switch (missatge) {
			case PETICIO_BACKOFFICE:
				text = backofficeService.solicitudXmlPeticioBackoffice(id, solicitudId);
				break;
			case RESPOSTA_BACKOFFICE:
				text = backofficeService.solicitudXmlRespostaBackoffice(id, solicitudId);
				break;
			default:
				text = backofficeService.solicitudErrorComunicacioBackoffice(id, solicitudId);
		}
		return text(text);
	}

	/** Missatge com a text (204 si no n'hi ha). */
	static ResponseEntity<String> text(String text) {
		if (text == null || text.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok().contentType(new MediaType(MediaType.TEXT_PLAIN, java.nio.charset.StandardCharsets.UTF_8)).body(text);
	}

	/** El detall comprova que la petició existeix i que l'usuari pot veure el seu servei (404 si no). */
	private void comprovarAcces(String id) {
		getReadonlyResourceService().getOne(id, null);
	}

	@Override
	public boolean forbiddenCreateLogic() {
		return true;
	}
	@Override
	public boolean forbiddenUpdateLogic() {
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
