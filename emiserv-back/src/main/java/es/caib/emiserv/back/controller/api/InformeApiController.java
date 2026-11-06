package es.caib.emiserv.back.controller.api;

import es.caib.emiserv.back.helper.RolHelper;
import es.caib.emiserv.back.view.InformeEmisorEnrutatExcelView;
import es.caib.emiserv.back.view.InformeGeneralEstatExcelView;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.ExplotacioService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Informes de la interfície REACT, com la pantalla d'informes de la JSP (InformeController): l'estat
 * general de les peticions i les peticions per emissor enrutat. Es generen amb les mateixes vistes
 * Excel (XLS) i els mateixos mètodes d'ExplotacioService (EMS_ADMIN o EMS_REPORT), que inclouen tot
 * el dia de la data final. Com a la JSP,
 * la pantalla només es mostra amb el rol actual d'administrador.
 *
 * @author Limit Tecnologies
 */
@RestController
@RequestMapping("/api/informes")
public class InformeApiController {

	@Autowired
	private ExplotacioService explotacioService;
	@Autowired
	private InformeGeneralEstatExcelView informeGeneralEstatView;
	@Autowired
	private InformeEmisorEnrutatExcelView informeEmisorEnrutatView;
	@Autowired
	private MessageSource messageSource;

	@PostMapping("/generalEstat")
	public void generalEstat(
			@RequestBody Parametres parametres,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!comprovar(parametres, request, response)) {
			return;
		}
		Map<String, Object> model = new HashMap<>();
		model.put(
				"informeDades",
				explotacioService.informeGeneralEstat(
						parametres.getDataInici(),
						parametres.getDataFi(),
						parametres.getTipusPeticio()));
		informeGeneralEstatView.render(model, request, response);
	}

	@PostMapping("/emisorEnrutat")
	public void emisorEnrutat(
			@RequestBody Parametres parametres,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!comprovar(parametres, request, response)) {
			return;
		}
		Map<String, Object> model = new HashMap<>();
		model.put(
				"informeDades",
				explotacioService.informeEmisorEnrutat(
						parametres.getDataInici(),
						parametres.getDataFi()));
		informeEmisorEnrutatView.render(model, request, response);
	}

	/** Rol i dates obligatòries; si no, escriu la resposta d'error (JSON) i torna false. */
	private boolean comprovar(Parametres parametres, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (!"EMS_ADMIN".equals(RolHelper.getRolActual(request, RolHelper.getRolsAutoritats()))) {
			error(response, HttpStatus.FORBIDDEN, "Forbidden");
			return false;
		}
		if (parametres.getDataInici() == null || parametres.getDataFi() == null) {
			error(response, HttpStatus.BAD_REQUEST, traduir("informe.validacio.dates"));
			return false;
		}
		if (parametres.getDataInici().after(parametres.getDataFi())) {
			error(response, HttpStatus.BAD_REQUEST, traduir("informe.validacio.ordre.dates"));
			return false;
		}
		return true;
	}

	private void error(HttpServletResponse response, HttpStatus status, String missatge) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write("{\"status\":" + status.value() + ",\"message\":\"" + missatge.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
	}

	private String traduir(String clau) {
		return messageSource.getMessage(clau, null, clau, LocaleContextHolder.getLocale());
	}

	@Data
	public static class Parametres {
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		private Date dataInici;
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		private Date dataFi;
		/** Només per a l'estat general: BACKOFFICE, ENRUTADOR o null (tots). */
		private ServeiTipusEnumDto tipusPeticio;
	}

}
