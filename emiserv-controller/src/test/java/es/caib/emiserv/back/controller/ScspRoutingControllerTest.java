package es.caib.emiserv.back.controller;

import com.sun.net.httpserver.HttpServer;
import es.caib.emiserv.back.helper.HeaderCopier;
import es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.AplicacioService;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Comprova que l'enrutador retorna els SOAP Fault amb HTTP 500. Els clients SCSP 5.x (Spring-WS)
 * només tracten una resposta com a Fault si arriba amb HTTP 500 i Content-Type text/xml.
 */
public class ScspRoutingControllerTest {

	private static final String SOAP_FAULT_EMISSOR =
			"<?xml version='1.0' encoding='UTF-8'?>" +
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
			"<soapenv:Fault><faultcode>0301</faultcode><faultstring>Organismo no autorizado</faultstring></soapenv:Fault>" +
			"</soapenv:Body></soapenv:Envelope>";

	private HttpServer emissor;
	private RedireccioService redireccioService;
	private MockMvc mockMvc;

	@BeforeEach
	public void configurar() throws Exception {
		emissor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		emissor.createContext("/services/SCDHPAJU", exchange -> {
			byte[] cos = SOAP_FAULT_EMISSOR.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "text/xml;charset=UTF-8");
			exchange.sendResponseHeaders(500, cos.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(cos);
			}
		});
		emissor.start();

		redireccioService = mock(RedireccioService.class);
		ScspRoutingController controller = new ScspRoutingController();
		ReflectionTestUtils.setField(controller, "redireccioService", redireccioService);
		ReflectionTestUtils.setField(controller, "aplicacioService", mock(AplicacioService.class));
		ReflectionTestUtils.setField(controller, "headerCopier", mock(HeaderCopier.class));
		ReflectionTestUtils.setField(controller, "environment", mock(Environment.class));
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@AfterEach
	public void aturar() {
		emissor.stop(0);
	}

	private RedireccioProcessarResultatDto resultat() {
		RedireccioProcessarResultatDto resultat = new RedireccioProcessarResultatDto();
		resultat.setTipus(ServeiTipusEnumDto.ENRUTADOR);
		resultat.setAtributPeticioId("PET1");
		resultat.setAtributCodigoCertificado("SCDHPAJU");
		return resultat;
	}

	@Test
	public void faultDeLEmissorEsRetornaAmbElCodiHttpDeLEmissor() throws Exception {
		RedireccioProcessarResultatDto resultat = resultat();
		resultat.setUrlDesti("http://localhost:" + emissor.getAddress().getPort() + "/services/SCDHPAJU");
		when(redireccioService.processarPeticio(any())).thenReturn(resultat);

		MockHttpServletResponse response = mockMvc.perform(
				post("/scspRouting/SCDHPAJU").content("<peticio/>".getBytes(StandardCharsets.UTF_8)))
				.andReturn().getResponse();

		assertEquals(500, response.getStatus());
		assertEquals(SOAP_FAULT_EMISSOR, response.getContentAsString(StandardCharsets.UTF_8));
	}

	@Test
	public void faultGeneratPerEmiservEsRetornaAmbHttp500ITextXml() throws Exception {
		RedireccioProcessarResultatDto resultat = resultat();
		resultat.setError(true);
		resultat.setErrorCodi("0301");
		resultat.setErrorDescripcio("Organismo no autorizado");
		when(redireccioService.processarPeticio(any())).thenReturn(resultat);
		when(redireccioService.generarSoapFault(any())).thenReturn(SOAP_FAULT_EMISSOR);

		MockHttpServletResponse response = mockMvc.perform(
				post("/scspRouting/SCDHPAJU").content("<peticio/>".getBytes(StandardCharsets.UTF_8)))
				.andReturn().getResponse();

		assertEquals(500, response.getStatus());
		assertTrue(response.getContentType().startsWith("text/xml"));
		assertEquals(SOAP_FAULT_EMISSOR, response.getContentAsString(StandardCharsets.UTF_8));
	}
}
