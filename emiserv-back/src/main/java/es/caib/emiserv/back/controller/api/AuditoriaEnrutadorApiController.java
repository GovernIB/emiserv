package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.base.controller.BaseMutableResourceController;
import es.caib.emiserv.logic.intf.dto.AuditoriaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.ProcedimentDto;
import es.caib.emiserv.logic.intf.dto.RedireccioRespostaDto;
import es.caib.emiserv.logic.intf.model.AuditoriaEnrutadorResource;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * API REST de l'auditoria de les peticions de l'enrutador de la interfície REACT. El recurs és de
 * només lectura (llistat, detall i exportació); les sol·licituds, els missatges XML, les respostes de
 * les entitats (enrutador múltiple) i els procediments del filtre es consulten amb els mateixos mètodes
 * de RedireccioService i ServeiService que la JSP (AuditoriaEnrutadorController).
 *
 * @author Limit Tecnologies
 */
@RestController
@RequestMapping("/api/auditoriaEnrutador")
public class AuditoriaEnrutadorApiController extends BaseMutableResourceController<AuditoriaEnrutadorResource, Long> {

	public static final String RESOURCE_NAME = "auditoriaEnrutador";

	/** Missatges d'una petició. */
	public enum MissatgePeticio { PETICIO, RESPOSTA }

	@Autowired
	private RedireccioService redireccioService;
	@Autowired
	private ServeiService serveiService;

	@Override
	protected Link getIndexLink() {
		return linkTo(getClass()).withRel(RESOURCE_NAME);
	}

	@GetMapping("/procediments")
	public List<ProcedimentDto> procediments() {
		// RedireccioService.procedimentFindAll només és per a EMS_ADMIN; el de ServeiService, també per a EMS_RESP
		return serveiService.procedimentFindAll();
	}

	@GetMapping("/{id}/solicituds")
	public List<AuditoriaSolicitudDto> solicituds(@PathVariable Long id) {
		comprovarAcces(id);
		return redireccioService.solicitudFindByPeticioId(id);
	}

	@GetMapping("/{id}/missatges/{missatge}")
	public ResponseEntity<String> missatge(@PathVariable Long id, @PathVariable MissatgePeticio missatge) {
		comprovarAcces(id);
		return AuditoriaBackofficeApiController.text(MissatgePeticio.PETICIO.equals(missatge) ?
				redireccioService.peticioXmlPeticio(id) :
				redireccioService.peticioXmlResposta(id));
	}

	/** Respostes de cada entitat d'un enrutador múltiple. */
	@GetMapping("/{id}/respostes")
	public List<RedireccioRespostaDto> respostes(@PathVariable Long id) {
		comprovarAcces(id);
		return redireccioService.peticioXmlRespostes(id);
	}

	/** El detall comprova que la petició existeix i que l'usuari pot veure el seu servei (404 si no). */
	private void comprovarAcces(Long id) {
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
