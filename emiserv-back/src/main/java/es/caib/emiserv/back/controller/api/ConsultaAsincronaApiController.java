package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaCedentDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaFiltreDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaFiltreEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.service.ConsultaAsincronaService;
import es.caib.emiserv.logic.intf.service.ServeiService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

/**
 * Consultes asíncrones processades com a peticions síncrones a cada destí per a la interfície
 * REACT, com la pantalla consultaAsincronaList.jsp (ConsultaAsincronaController): llistat amb el
 * progrés, sol·licituds de cada consulta (estat, intents i proper reintent), destins (cedents) amb
 * feina pendent i les mateixes accions (pausar, reprendre, reintentar, cancel·lar, pausar un
 * cedent). Només EMS_ADMIN, com el servei.
 * <p/>
 * No és un recurs HAL-FORMS: les dades són agregats calculats (recomptes, estimacions) i la pàgina
 * les consulta periòdicament.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@RestController
@RequestMapping("/api/consultesAsincrones")
public class ConsultaAsincronaApiController {

	private static final String ROL_ADMIN = "EMS_ADMIN";
	/** Camps ordenables (els que mapeja ConsultaAsincronaServiceImpl). */
	private static final Set<String> CAMPS_ORDENABLES = Set.of("dataPeticio", "peticioId", "terData");
	private static final int TAMANY_MAXIM = 100;

	@Autowired
	private ConsultaAsincronaService consultaAsincronaService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private MessageSource messageSource;

	@GetMapping
	public ResponseEntity<PaginaDto<ConsultaAsincronaDto>> consultes(
			@RequestParam(required = false) ConsultaAsincronaFiltreEstatEnumDto estat,
			@RequestParam(required = false) Long servei,
			@RequestParam(required = false) String peticioId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "dataPeticio") String sort,
			@RequestParam(defaultValue = "desc") String dir) {
		if (!esAdmin()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		ConsultaAsincronaFiltreDto filtre = new ConsultaAsincronaFiltreDto();
		filtre.setEstat(estat);
		filtre.setServei(servei);
		filtre.setPeticioId(peticioId);
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		paginacio.setPaginaNum(Math.max(page, 0));
		paginacio.setPaginaTamany(Math.min(Math.max(size, 1), TAMANY_MAXIM));
		paginacio.afegirOrdre(
				CAMPS_ORDENABLES.contains(sort) ? sort : "dataPeticio",
				"asc".equalsIgnoreCase(dir) ? OrdreDireccioDto.ASCENDENT : OrdreDireccioDto.DESCENDENT);
		return ResponseEntity.ok(consultaAsincronaService.findByFiltrePaginat(filtre, paginacio));
	}

	@GetMapping("/{id}/solicituds")
	public ResponseEntity<?> solicituds(@PathVariable Long id) {
		if (!esAdmin()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		try {
			List<ConsultaAsincronaSolicitudDto> solicituds = consultaAsincronaService.solicitudFindByConsulta(id);
			return ResponseEntity.ok(solicituds);
		} catch (Exception ex) {
			return error(ex);
		}
	}

	@GetMapping("/cedents")
	public ResponseEntity<List<ConsultaAsincronaCedentDto>> cedents() {
		if (!esAdmin()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.ok(consultaAsincronaService.cedentFindAll());
	}

	/** Serveis del filtre: els mateixos que la JSP (asíncrones processades com a síncrones o backoffice). */
	@GetMapping("/serveis")
	public ResponseEntity<List<Servei>> serveis() {
		if (!esAdmin()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		paginacio.afegirOrdre("codi", OrdreDireccioDto.ASCENDENT);
		return ResponseEntity.ok(serveiService.findAllPaginat(null, paginacio).getContingut().stream().
				filter(s -> s.isAsyncSincron() || s.getTipus() == ServeiTipusEnumDto.BACKOFFICE).
				map(s -> new Servei(s.getId(), s.getCodi(), s.getNom())).
				collect(Collectors.toList()));
	}

	@PostMapping("/{id}/pausar")
	public ResponseEntity<Map<String, String>> pausar(@PathVariable Long id) {
		return accio(() -> { consultaAsincronaService.pausar(id); return -1; }, "consulta.asincrona.controller.pausada.ok");
	}

	@PostMapping("/{id}/reprendre")
	public ResponseEntity<Map<String, String>> reprendre(@PathVariable Long id) {
		return accio(() -> { consultaAsincronaService.reprendre(id); return -1; }, "consulta.asincrona.controller.represa.ok");
	}

	@PostMapping("/{id}/reintentar")
	public ResponseEntity<Map<String, String>> reintentar(@PathVariable Long id) {
		return accio(() -> consultaAsincronaService.reintentar(id), "consulta.asincrona.controller.reintentada.ok");
	}

	@PostMapping("/{id}/solicituds/{solicitudId}/reintentar")
	public ResponseEntity<Map<String, String>> reintentarSolicitud(@PathVariable Long id, @PathVariable Long solicitudId) {
		return accio(() -> { consultaAsincronaService.reintentarSolicitud(id, solicitudId); return -1; }, "consulta.asincrona.controller.solicitud.reintentada.ok");
	}

	@PostMapping("/{id}/cancelar")
	public ResponseEntity<Map<String, String>> cancelar(@PathVariable Long id) {
		return accio(() -> consultaAsincronaService.cancelar(id), "consulta.asincrona.controller.cancelada.ok");
	}

	@PostMapping("/cedents/{rutaId}/pausar")
	public ResponseEntity<Map<String, String>> pausarCedent(@PathVariable Long rutaId) {
		return accio(() -> { consultaAsincronaService.updateCedentPausat(rutaId, true); return -1; }, "consulta.asincrona.controller.cedent.pausat.ok");
	}

	@PostMapping("/cedents/{rutaId}/reprendre")
	public ResponseEntity<Map<String, String>> reprendreCedent(@PathVariable Long rutaId) {
		return accio(() -> { consultaAsincronaService.updateCedentPausat(rutaId, false); return -1; }, "consulta.asincrona.controller.cedent.repres.ok");
	}

	/**
	 * Executa l'acció i retorna el missatge de la JSP. L'acció retorna el nombre de sol·licituds
	 * afectades (argument del missatge) o -1 si el missatge no en té.
	 */
	@SuppressWarnings("unchecked")
	private ResponseEntity<Map<String, String>> accio(IntSupplier accio, String missatgeOk) {
		if (!esAdmin()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		try {
			int num = accio.getAsInt();
			return ResponseEntity.ok(Map.of("message", traduir(missatgeOk, num >= 0 ? new Object[] {num} : null)));
		} catch (Exception ex) {
			return (ResponseEntity<Map<String, String>>)(ResponseEntity<?>)error(ex);
		}
	}

	/** Errors del servei (a JBoss arriben embolcallats a una EJBException): 404 o 409 amb el missatge. */
	private ResponseEntity<?> error(Exception ex) {
		Throwable causa = ExceptionUtils.getRootCause(ex) != null ? ExceptionUtils.getRootCause(ex) : ex;
		boolean noTrobat = causa instanceof NotFoundException;
		if (!noTrobat && !(causa instanceof ValidationException)) {
			log.error("Error en una acció de consultes asíncrones", ex);
		}
		return ResponseEntity.status(noTrobat ? HttpStatus.NOT_FOUND : HttpStatus.CONFLICT).body(Map.of(
				"message",
				traduir("consulta.asincrona.controller.accio.error", new Object[] {causa.getMessage()})));
	}

	private String traduir(String clau, Object[] args) {
		return messageSource.getMessage(clau, args, clau, LocaleContextHolder.getLocale());
	}

	private static boolean esAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.getAuthorities().stream().
				map(GrantedAuthority::getAuthority).
				anyMatch(ROL_ADMIN::equals);
	}

	@Getter
	@AllArgsConstructor
	public static class Servei {
		private Long id;
		private String codi;
		private String nom;
	}

}
