package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.XmlHelper;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import es.caib.emiserv.persist.repository.BackofficePeticioRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Destí de les peticions de l'enrutador segons el tipus de ruta: les rutes de tipus EMISERV
 * reenvien la petició a l'emisor del cedent i les de tipus BACKOFFICE a l'emisor SCSP d'EMISERV.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class RedireccioServiceImplRutaBackofficeTest {

	private static final String URL_EMISOR_CEDENT = "https://ajuntament.es/emisor/services/peticionSincrona";

	private RedireccioServiceImpl redireccioService;
	private MockEnvironment environment;

	@BeforeEach
	void setUp() {
		redireccioService = new RedireccioServiceImpl();
		environment = new MockEnvironment();
		ReflectionTestUtils.setField(redireccioService, "environment", environment);
	}

	@Test
	void rutaEmiservReenviaAlCedent() {
		assertEquals(URL_EMISOR_CEDENT, redireccioService.getUrlDesti(ruta(ServeiRutaDestiTipusEnumDto.EMISERV)));
	}

	@Test
	void rutaBackofficeEnviaALEmisorDEmiserv() {
		assertEquals(
				RedireccioServiceImpl.EMISOR_URL_PER_DEFECTE,
				redireccioService.getUrlDesti(ruta(ServeiRutaDestiTipusEnumDto.BACKOFFICE)));
	}

	@Test
	void rutaBackofficeAmbUrlConfigurada() {
		// Sense barra final: l'enrutador hi ha d'afegir l'operació (peticionSincrona, peticionAsincrona)
		environment.setProperty(RedireccioServiceImpl.PROPIETAT_EMISOR_URL, "https://emiserv.caib.es/emiservapi/interna/scsp/services");

		assertEquals(
				"https://emiserv.caib.es/emiservapi/interna/scsp/services/",
				redireccioService.getUrlDesti(ruta(ServeiRutaDestiTipusEnumDto.BACKOFFICE)));
	}

	@Test
	void enrutadorMultipleAgrupaLesRutesBackoffice() {
		List<ServeiRutaDestiEntity> rutes = Arrays.asList(
				ruta("MALLORCA", ServeiRutaDestiTipusEnumDto.EMISERV, "https://mallorca.es/emisor/"),
				ruta("MENORCA", ServeiRutaDestiTipusEnumDto.BACKOFFICE, "https://menorca.es/backoffice"),
				ruta("EIVISSA", ServeiRutaDestiTipusEnumDto.EMISERV, "https://eivissa.es/emisor/"),
				ruta("FORMENTERA", ServeiRutaDestiTipusEnumDto.BACKOFFICE, "https://formentera.es/backoffice"));

		Map<String, String> urlDestins = redireccioService.getUrlDestinsEnrutadorMultiple(rutes);

		// Una única petició a l'emisor d'EMISERV per a totes les rutes de tipus BACKOFFICE
		assertEquals(Arrays.asList("MALLORCA", RedireccioServiceImpl.CLAU_BACKOFFICES, "EIVISSA"), new ArrayList<>(urlDestins.keySet()));
		assertEquals("https://mallorca.es/emisor/", urlDestins.get("MALLORCA"));
		assertEquals(RedireccioServiceImpl.EMISOR_URL_PER_DEFECTE, urlDestins.get(RedireccioServiceImpl.CLAU_BACKOFFICES));
		// La resposta dels backoffices ocupa la posició de la primera ruta de tipus BACKOFFICE
		assertEquals(
				Arrays.asList("MALLORCA", RedireccioServiceImpl.CLAU_BACKOFFICES, "EIVISSA"),
				RedireccioServiceImpl.getClausOrdenadesEnrutadorMultiple(rutes));
	}

	@Test
	void enrutadorMultipleSenseRutesBackoffice() {
		List<ServeiRutaDestiEntity> rutes = Arrays.asList(
				ruta("MALLORCA", ServeiRutaDestiTipusEnumDto.EMISERV, "https://mallorca.es/emisor/"),
				ruta("MENORCA", ServeiRutaDestiTipusEnumDto.EMISERV, "https://menorca.es/emisor/"));

		assertEquals(Arrays.asList("MALLORCA", "MENORCA"), new ArrayList<>(redireccioService.getUrlDestinsEnrutadorMultiple(rutes).keySet()));
	}

	@Test
	void traduccioDeLaClauBackofficesALEntitatQueHaRespost() {
		BackofficePeticioRepository backofficePeticioRepository = mock(BackofficePeticioRepository.class);
		ReflectionTestUtils.setField(redireccioService, "backofficePeticioRepository", backofficePeticioRepository);
		when(backofficePeticioRepository.findEntitatCodiByPeticionId("PBLPROVA0001")).thenReturn(Arrays.asList("MENORCA"));
		when(backofficePeticioRepository.findEntitatCodiByPeticionId("PBLPROVA0002")).thenReturn(Arrays.asList((String)null));

		assertEquals("MENORCA", redireccioService.traduirClauBackoffices("PBLPROVA0001", RedireccioServiceImpl.CLAU_BACKOFFICES));
		assertEquals("MALLORCA", redireccioService.traduirClauBackoffices("PBLPROVA0001", "MALLORCA"));
		assertEquals(RedireccioServiceImpl.CLAU_BACKOFFICES, redireccioService.traduirClauBackoffices("PBLPROVA0002", RedireccioServiceImpl.CLAU_BACKOFFICES));
	}

	@Test
	void soapFaultEmisorScsp5() throws Exception {
		// SOAP Fault de l'emisor SCSP 5 (llibreries SCSP 5.0.12)
		String fault = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body>" +
				"<soapenv:Fault><faultcode>soapenv:Server</faultcode><faultstring>Error</faultstring><detail>" +
				"<fault xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/fault\"><TimeStamp>2026-10-27T15:55:07.000+01:00</TimeStamp>" +
				"<CodigoEstado>0227</CodigoEstado><LiteralError>Error al generar la respuesta</LiteralError></fault>" +
				"</detail></soapenv:Fault></soapenv:Body></soapenv:Envelope>";
		XmlHelper xmlHelper = new XmlHelper();
		Document document = xmlHelper.bytesToDocument(fault.getBytes());

		assertEquals(1, xmlHelper.countNodes(document, RedireccioServiceImpl.XPATH_FAULT_SCSP5));
		assertEquals("0227", xmlHelper.getTextFromFirstNode(document, RedireccioServiceImpl.XPATH_FAULT_SCSP5 + "/*[local-name()='CodigoEstado']", true));
		assertEquals("Error al generar la respuesta", xmlHelper.getTextFromFirstNode(document, RedireccioServiceImpl.XPATH_FAULT_SCSP5 + "/*[local-name()='LiteralError']", true));
	}

	private static ServeiRutaDestiEntity ruta(ServeiRutaDestiTipusEnumDto tipus) {
		return ServeiRutaDestiEntity.getBuilder(null, "7040", URL_EMISOR_CEDENT, 1L).
				desti(tipus, null).
				build();
	}

	private static ServeiRutaDestiEntity ruta(String entitat, ServeiRutaDestiTipusEnumDto tipus, String url) {
		return ServeiRutaDestiEntity.getBuilder(null, entitat, url, 1L).
				desti(tipus, null).
				build();
	}

}
