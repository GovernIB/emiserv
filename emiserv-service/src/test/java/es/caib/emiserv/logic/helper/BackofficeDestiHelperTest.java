package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.helper.BackofficeDestiHelper.BackofficeDesti;
import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitudes;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import com.sun.net.httpserver.HttpServer;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiProvaDto;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Resolució del backoffice al qual s'envia una petició: el del servei o, per als enrutadors,
 * el de la ruta de l'entitat (rutes de tipus BACKOFFICE).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class BackofficeDestiHelperTest {

	private static final String URL_BACKOFFICE_CEDENT = "https://ajuntament.es/backoffice/ws/EmiservBackoffice";
	private static final String DATOS_ESPECIFICOS_PADRO =
			"<DatosEspecificos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">" +
			"<Solicitud><ProvinciaSolicitud>7</ProvinciaSolicitud><MunicipioSolicitud>040</MunicipioSolicitud></Solicitud>" +
			"</DatosEspecificos>";

	private BackofficeDestiHelper helper;
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	private ServeiEntity enrutador;

	@BeforeEach
	void setUp() {
		helper = new BackofficeDestiHelper();
		serveiRutaDestiRepository = mock(ServeiRutaDestiRepository.class);
		ReflectionTestUtils.setField(helper, "serveiRutaDestiRepository", serveiRutaDestiRepository);
		enrutador = ServeiEntity.getBuilder("SCDCPAJU", "Padró de convivència", ServeiTipusEnumDto.ENRUTADOR).
				resolverClass("es.caib.emiserv.logic.resolver.EmpadronamentEntitatResolver").
				build();
	}

	@Test
	void serveiBackofficeFaServirElBackofficeDelServei() {
		ServeiEntity servei = ServeiEntity.getBuilder("SVDSCDWS01", "Canvi de domicili", ServeiTipusEnumDto.BACKOFFICE).
				backofficeCaibUrl("https://caib.es/backoffice").
				backofficeCaibAutenticacio(BackofficeAutenticacioTipusEnumDto.TEXT_CLAR).
				build();
		servei.updateConfigurat(true);

		BackofficeDesti desti = helper.getDesti(servei, null);

		assertEquals("https://caib.es/backoffice", desti.getUrl());
		assertEquals(BackofficeAutenticacioTipusEnumDto.TEXT_CLAR, desti.getAutenticacio());
		assertNull(desti.getEntitatCodi());
		assertArrayEquals(
				new String[] {"es.caib.emiserv.backoffice.SVDSCDWS01.", "es.caib.emiserv.backoffice."},
				desti.getPrefixosPropietats());
	}

	@Test
	void serveiBackofficeNoConfigurat() {
		ServeiEntity servei = ServeiEntity.getBuilder("SVDSCDWS01", "Canvi de domicili", ServeiTipusEnumDto.BACKOFFICE).build();

		assertThrows(ValidationException.class, () -> helper.getDesti(servei, null));
	}

	@Test
	void enrutadorFaServirElBackofficeDeLaRutaDeLEntitat() throws Exception {
		when(serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(enrutador, "7040")).thenReturn(
				ruta(ServeiRutaDestiTipusEnumDto.BACKOFFICE, BackofficeAutenticacioTipusEnumDto.TEXT_CLAR));

		BackofficeDesti desti = helper.getDesti(enrutador, peticion(element(DATOS_ESPECIFICOS_PADRO)));

		assertEquals("7040", desti.getEntitatCodi());
		assertEquals(URL_BACKOFFICE_CEDENT, desti.getUrl());
		assertEquals(BackofficeAutenticacioTipusEnumDto.TEXT_CLAR, desti.getAutenticacio());
		assertArrayEquals(
				new String[] {
						"es.caib.emiserv.backoffice.SCDCPAJU.7040.",
						"es.caib.emiserv.backoffice.SCDCPAJU.",
						"es.caib.emiserv.backoffice."},
				desti.getPrefixosPropietats());
	}

	@Test
	void enrutadorAmbDadesEspecifiquesComATextIClientQueEnviaElCodiIneComplet() {
		// Alguns clients envien el codi INE complet al municipi (p.ex. 7040 en lloc de 040)
		when(serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(eq(enrutador), eq("7040"))).thenReturn(
				ruta(ServeiRutaDestiTipusEnumDto.BACKOFFICE, null));

		BackofficeDesti desti = helper.getDesti(enrutador, peticion(DATOS_ESPECIFICOS_PADRO.replace(">040<", ">7040<")));

		assertEquals("7040", desti.getEntitatCodi());
		assertEquals(BackofficeAutenticacioTipusEnumDto.NINGUNA, desti.getAutenticacio());
	}

	@Test
	void enrutadorAmbRutaEmiserv() throws Exception {
		when(serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(enrutador, "7040")).thenReturn(
				ruta(ServeiRutaDestiTipusEnumDto.EMISERV, null));

		ValidationException ex = assertThrows(
				ValidationException.class,
				() -> helper.getDesti(enrutador, peticion(element(DATOS_ESPECIFICOS_PADRO))));
		assertEquals(true, ex.getMessage().contains("no és de tipus BACKOFFICE"));
	}

	@Test
	void enrutadorSenseRutaPerALEntitat() throws Exception {
		when(serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(any(), any())).thenReturn(null);

		assertThrows(ValidationException.class, () -> helper.getDesti(enrutador, peticion(element(DATOS_ESPECIFICOS_PADRO))));
	}

	@Test
	void enrutadorSensePeticio() {
		assertThrows(ValidationException.class, () -> helper.getDesti(enrutador, null));
	}

	@Test
	void enrutadorMultipleNoTeUnUnicDesti() throws Exception {
		ServeiEntity multiple = ServeiEntity.getBuilder("SVDSCTFNWS01", "Família nombrosa", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).build();

		assertThrows(ValidationException.class, () -> helper.getDesti(multiple, peticion(element(DATOS_ESPECIFICOS_PADRO))));
	}

	@Test
	void destinsEnrutadorMultiple() {
		ServeiEntity multiple = ServeiEntity.getBuilder("SVDSCTFNWS01", "Família nombrosa", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).build();
		when(serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(multiple)).thenReturn(Arrays.asList(
				ServeiRutaDestiEntity.getBuilder(multiple, "MALLORCA", "https://mallorca.es/emisor/", 1L).build(),
				ServeiRutaDestiEntity.getBuilder(multiple, "MENORCA", "https://menorca.es/backoffice", 2L).
						desti(ServeiRutaDestiTipusEnumDto.BACKOFFICE, BackofficeAutenticacioTipusEnumDto.TEXT_CLAR).build(),
				ServeiRutaDestiEntity.getBuilder(multiple, "EIVISSA", "https://eivissa.es/backoffice", 3L).
						desti(ServeiRutaDestiTipusEnumDto.BACKOFFICE, null).build()));

		List<BackofficeDesti> destins = helper.getDestinsEnrutadorMultiple(multiple);

		assertEquals(2, destins.size());
		assertEquals("MENORCA", destins.get(0).getEntitatCodi());
		assertEquals("https://menorca.es/backoffice", destins.get(0).getUrl());
		assertEquals(BackofficeAutenticacioTipusEnumDto.TEXT_CLAR, destins.get(0).getAutenticacio());
		assertEquals("EIVISSA", destins.get(1).getEntitatCodi());
	}

	@Test
	void enrutadorMultipleSenseRutesBackoffice() {
		ServeiEntity multiple = ServeiEntity.getBuilder("SVDSCTFNWS01", "Família nombrosa", ServeiTipusEnumDto.ENRUTADOR_MULTIPLE).build();
		when(serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(multiple)).thenReturn(Arrays.asList(
				ServeiRutaDestiEntity.getBuilder(multiple, "MALLORCA", "https://mallorca.es/emisor/", 1L).build()));

		assertThrows(ValidationException.class, () -> helper.getDestinsEnrutadorMultiple(multiple));
	}

	@Test
	void credencialsPerEntitatPerServeiIGlobals() {
		MockEnvironment environment = new MockEnvironment();
		ReflectionTestUtils.setField(helper, "environment", environment);
		BackofficeDesti desti = new BackofficeDesti("SCDCPAJU", "7040", URL_BACKOFFICE_CEDENT, BackofficeAutenticacioTipusEnumDto.TEXT_CLAR);

		assertEquals(false, helper.isCredencialsConfigurades(desti));

		environment.setProperty("es.caib.emiserv.backoffice.caib.auth.username", "global");
		environment.setProperty("es.caib.emiserv.backoffice.SCDCPAJU.caib.auth.password", "servei");
		environment.setProperty("es.caib.emiserv.backoffice.SCDCPAJU.7040.caib.auth.username", "entitat");

		assertEquals(true, helper.isCredencialsConfigurades(desti));
		assertEquals("entitat", helper.getPropietat(desti, BackofficeDestiHelper.PROPIETAT_USUARI));
		assertEquals("servei", helper.getPropietat(desti, BackofficeDestiHelper.PROPIETAT_SECRET));
		assertEquals(true, helper.isCredencialsConfigurades(
				new BackofficeDesti("SCDCPAJU", "7001", URL_BACKOFFICE_CEDENT, BackofficeAutenticacioTipusEnumDto.NINGUNA)));
	}

	@Test
	void provarConnexioAmbElBackoffice() throws Exception {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("es.caib.emiserv.backoffice.SCDCPAJU.7040.caib.auth.username", "usuari");
		environment.setProperty("es.caib.emiserv.backoffice.SCDCPAJU.7040.caib.auth.password", "secret");
		ReflectionTestUtils.setField(helper, "environment", environment);
		AtomicReference<String> autoritzacio = new AtomicReference<>();
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/ws/EmiservBackoffice", exchange -> {
			autoritzacio.set(exchange.getRequestHeaders().getFirst("Authorization"));
			byte[] wsdl = "<wsdl:definitions name=\"EmiservBackofficeService\"/>".getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(200, wsdl.length);
			exchange.getResponseBody().write(wsdl);
			exchange.close();
		});
		server.createContext("/emisor", exchange -> {
			exchange.sendResponseHeaders(404, -1);
			exchange.close();
		});
		server.start();
		try {
			String base = "http://127.0.0.1:" + server.getAddress().getPort();

			ServeiRutaDestiProvaDto correcta = helper.provarConnexio(
					new BackofficeDesti("SCDCPAJU", "7040", base + "/ws/EmiservBackoffice", BackofficeAutenticacioTipusEnumDto.TEXT_CLAR),
					true);
			ServeiRutaDestiProvaDto noEsBackoffice = helper.provarConnexio(
					new BackofficeDesti("SCDCPAJU", "7040", base + "/emisor", BackofficeAutenticacioTipusEnumDto.NINGUNA),
					true);
			ServeiRutaDestiProvaDto inaccessible = helper.provarConnexio(
					new BackofficeDesti("SCDCPAJU", "7040", "http://127.0.0.1:1/ws", BackofficeAutenticacioTipusEnumDto.NINGUNA),
					true);

			assertEquals(true, correcta.isCorrecta(), correcta.getMissatge());
			assertEquals(false, correcta.isCredencialsPendents());
			assertEquals(base + "/ws/EmiservBackoffice?wsdl", correcta.getUrl());
			assertEquals("Basic " + Base64.getEncoder().encodeToString("usuari:secret".getBytes(StandardCharsets.UTF_8)), autoritzacio.get());
			assertEquals(false, noEsBackoffice.isCorrecta());
			assertEquals(Integer.valueOf(404), noEsBackoffice.getCodiHttp());
			assertEquals(false, inaccessible.isCorrecta());
		} finally {
			server.stop(0);
		}
	}

	private ServeiRutaDestiEntity ruta(
			ServeiRutaDestiTipusEnumDto tipus,
			BackofficeAutenticacioTipusEnumDto autenticacio) {
		return ServeiRutaDestiEntity.getBuilder(enrutador, "7040", URL_BACKOFFICE_CEDENT, 1L).
				desti(tipus, autenticacio).
				build();
	}

	private static Peticion peticion(Object datosEspecificos) {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setIdPeticion("PBLPROVA0001");
		atributos.setCodigoCertificado("SCDCPAJU");
		peticion.setAtributos(atributos);
		SolicitudTransmision solicitud = new SolicitudTransmision();
		solicitud.setDatosEspecificos(datosEspecificos);
		Solicitudes solicitudes = new Solicitudes();
		solicitudes.setSolicitudTransmision(new ArrayList<>());
		solicitudes.getSolicitudTransmision().add(solicitud);
		peticion.setSolicitudes(solicitudes);
		return peticion;
	}

	private static Element element(String xml) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement();
	}

}
