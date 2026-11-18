package es.caib.emiserv.back.controller;

import es.caib.emiserv.logic.intf.dto.RedireccioRespostaFakeDto;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Endpoint intern que fan servir els enrutadors dels serveis en mode fake: en lloc de cridar
 * l'emissor real, l'enrutador crida aquest endpoint, que genera la resposta SCSP simulada.
 * No requereix autenticació (com /scspRouting), però només contesta peticions que vénen del
 * mateix servidor (adreça loopback): les respostes es signen amb la clau real del servei.
 */
@Controller
public class ScspFakeController {

	@Autowired
	private RedireccioService redireccioService;

	@RequestMapping(
			value = {
					"/scspFake/{serveiCodi}/{entitatCodi}",
					"/scspFake/{serveiCodi}/{entitatCodi}/**"},
			method = RequestMethod.POST)
	public void doPost(
			@PathVariable("serveiCodi") String serveiCodi,
			@PathVariable("entitatCodi") String entitatCodi,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (!isLoopback(request.getRemoteAddr())) {
			// Com si el servei no fos fake: sense cos i sense cridar el servei
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		byte[] xml = IOUtils.toByteArray(request.getInputStream());
		RedireccioRespostaFakeDto resposta = redireccioService.generarRespostaFake(
				serveiCodi,
				"-".equals(entitatCodi) ? null : entitatCodi,
				xml,
				ScspRoutingController.isOperacioAsincrona(request));
		response.setStatus(resposta.getCodiHttp());
		response.setContentType("text/xml; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		if (resposta.getCodiHttp() != HttpServletResponse.SC_NOT_FOUND && resposta.getXml() != null) {
			response.getOutputStream().write(resposta.getXml().getBytes(StandardCharsets.UTF_8));
		}
	}

	/** Adreça IPv4 127.0.0.0/8 o IPv6 ::1 (en qualsevol de les seves formes). */
	static boolean isLoopback(String adreca) {
		if (adreca == null || adreca.isEmpty()) {
			return false;
		}
		String a = adreca.trim();
		if (a.startsWith("127.")) {
			return a.matches("127(\\.\\d{1,3}){3}");
		}
		return a.equals("::1") || a.equals("0:0:0:0:0:0:0:1") || a.equals("[::1]");
	}

}
