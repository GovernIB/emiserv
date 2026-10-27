package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.helper.BackofficeHelper.PeticioEmisorCedent;
import es.caib.emiserv.logic.helper.BackofficeHelper.RespostaBackofficeEntitat;
import es.caib.emiserv.logic.helper.ClientScspHelper.ScspCedentException;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos;
import es.caib.emiserv.persist.entity.BackofficeComunicacioEntity;
import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.BackofficeSolicitudEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreTokenDataEntity;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTokenDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Peticions a l'emisor SCSP dels cedents (rutes de tipus EMISERV): petició d'una sola sol·licitud
 * amb les dades originals i resposta convertida al format del backoffice.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class BackofficeHelperEmisorCedentTest {

	private static final String NS_PETICION = "http://intermediacion.redsara.es/scsp/esquemas/V3/peticion";
	private static final String NS_DATESP = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";
	private static final String PETICIO =
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body>" +
			"<ns3:Peticion xmlns:ns3=\"" + NS_PETICION + "\" xmlns:ns2=\"" + NS_DATESP + "\">" +
			"<ns3:Atributos><ns3:IdPeticion>PBLPROVA0001</ns3:IdPeticion><ns3:NumElementos>2</ns3:NumElementos></ns3:Atributos>" +
			"<ns3:Solicitudes>" + solicitud("000001", "11111111H", "001") + solicitud("000002", "22222222J", "040") +
			"</ns3:Solicitudes></ns3:Peticion></soapenv:Body></soapenv:Envelope>";

	private BackofficeHelper backofficeHelper;
	private ClientScspHelper clientScspHelper;
	private MockEnvironment environment;
	private BackofficePeticioEntity peticio;
	private BackofficeSolicitudEntity solicitud;
	private ServeiEntity servei;
	private ServeiRutaDestiEntity ruta;

	@BeforeEach
	void setUp() {
		backofficeHelper = new BackofficeHelper();
		clientScspHelper = mock(ClientScspHelper.class);
		environment = new MockEnvironment();
		ConfiguracioAsincronHelper configuracio = new ConfiguracioAsincronHelper();
		ReflectionTestUtils.setField(configuracio, "environment", environment);
		BackofficeDestiHelper backofficeDestiHelper = new BackofficeDestiHelper();
		ReflectionTestUtils.setField(backofficeDestiHelper, "environment", environment);
		ScspCoreTokenDataRepository tokenDataRepository = mock(ScspCoreTokenDataRepository.class);
		when(tokenDataRepository.findByPeticionIdAndTipoMensaje("PBLPROVA0001", 0)).thenReturn(
				ScspCoreTokenDataEntity.getBuilder("PBLPROVA0001", 0, PETICIO).build());
		ScspCoreServicioRepository servicioRepository = mock(ScspCoreServicioRepository.class);
		ScspCoreServicioEntity servicio = ScspCoreServicioEntity.getBuilder("SCDCPAJU").build();
		ReflectionTestUtils.setField(servicio, "versionEsquema", "V3");
		when(servicioRepository.findByCodigoCertificado("SCDCPAJU")).thenReturn(servicio);
		ReflectionTestUtils.setField(backofficeHelper, "clientScspHelper", clientScspHelper);
		ReflectionTestUtils.setField(backofficeHelper, "configuracioAsincronHelper", configuracio);
		ReflectionTestUtils.setField(backofficeHelper, "backofficeDestiHelper", backofficeDestiHelper);
		ReflectionTestUtils.setField(backofficeHelper, "scspCoreTokenDataRepository", tokenDataRepository);
		ReflectionTestUtils.setField(backofficeHelper, "scspCoreServicioRepository", servicioRepository);
		ReflectionTestUtils.setField(backofficeHelper, "xmlHelper", new XmlHelper());
		ReflectionTestUtils.setField(backofficeHelper, "environment", environment);
		peticio = BackofficePeticioEntity.getBuilder(
				ScspCorePeticionRespuestaEntity.getBuilder(10L, "PBLPROVA0001", "0002").build(),
				new Date()).build();
		solicitud = BackofficeSolicitudEntity.getBuilder(peticio, "000002").build();
		servei = ServeiEntity.getBuilder("SCDCPAJU", "Padró", ServeiTipusEnumDto.ENRUTADOR).build();
		ruta = ServeiRutaDestiEntity.getBuilder(servei, "7040", "https://cedent.es/emisor/services/peticionSincrona", 1L).
				desti(ServeiRutaDestiTipusEnumDto.EMISERV, null).
				build();
	}

	@Test
	void peticioDUnaSolaSolicitudAmbLesDadesOriginals() throws Exception {
		environment.setProperty("es.caib.emiserv.backoffice.SCDCPAJU.7040.emisor.nif", "P0704000A");

		PeticioEmisorCedent peticioCedent = backofficeHelper.prepararPeticioEmisorCedent(peticio, solicitud, servei, ruta, "EMS00000000000000000000042", null);

		Element peticion = peticioCedent.getPeticion();
		assertEquals(NS_PETICION, peticion.getNamespaceURI());
		assertEquals("EMS00000000000000000000042", text(peticion, "IdPeticion"));
		assertEquals("1", text(peticion, "NumElementos"));
		assertEquals("SCDCPAJU", text(peticion, "CodigoCertificado"));
		assertEquals(1, peticion.getElementsByTagNameNS(NS_PETICION, "SolicitudTransmision").getLength());
		// Dades de la sol·licitud original (no de la primera de la petició)
		assertEquals("000002", text(peticion, "IdSolicitud"));
		assertEquals("040", text(peticion, "MunicipioSolicitud"));
		assertEquals(NS_DATESP, peticion.getElementsByTagNameNS("*", "DatosEspecificos").item(0).getNamespaceURI());
		// Emisor del cedent configurat a la ruta
		assertEquals("P0704000A", text(peticion, "NifEmisor"));
		assertEquals("https://cedent.es/emisor/services/peticionSincrona", peticioCedent.getUrl());
		assertTrue(peticioCedent.isValidarSignatura());
	}

	@Test
	void laRespostaDelCedentEsConverteixAlFormatDelBackoffice() throws Exception {
		when(clientScspHelper.enviarPeticioSincrona(anyString(), anyString(), any(), any(), anyInt(), anyBoolean())).thenReturn(
				respostaCedent("EMS00000000000000000000042", "0003"));

		RespostaBackofficeEntitat resposta = backofficeHelper.enviarPeticioSincronaEmisorCedent(peticioCedent());

		assertNull(resposta.getException());
		Respuesta respuesta = resposta.getRespuesta();
		TransmisionDatos transmision = respuesta.getTransmisiones().getTransmisionDatos().get(0);
		assertEquals("000002", transmision.getDatosGenericos().getTransmision().getIdSolicitud());
		assertEquals("TR-CEDENT-1", transmision.getDatosGenericos().getTransmision().getIdTransmision());
		assertEquals("22222222J", transmision.getDatosGenericos().getTitular().getDocumentacion());
		assertEquals("Govern de les Illes Balears", transmision.getDatosGenericos().getSolicitante().getNombreSolicitante());
		assertEquals(NS_DATESP, ((Element)transmision.getDatosEspecificos()).getNamespaceURI());

		// La comunicació desada es llegeix igual que les respostes dels backoffices
		BackofficeComunicacioEntity comunicacio = BackofficeComunicacioEntity.getBuilder(peticio, resposta.getXmlPeticio().toString()).build();
		comunicacio.updateResposta(resposta.getXmlResposta().toString());
		solicitud.updateComunicacioDarrera(comunicacio);
		TransmisionDatos desada = ReflectionTestUtils.invokeMethod(backofficeHelper, "solicitudToTransmisionDatos", solicitud, "SCDCPAJU");
		assertEquals("000002", desada.getDatosGenericos().getTransmision().getIdSolicitud());
		assertEquals("22222222J", desada.getDatosGenericos().getTitular().getDocumentacion());
		Element datosEspecificos = (Element)desada.getDatosEspecificos();
		assertEquals("Empadronado", datosEspecificos.getElementsByTagNameNS("*", "Resultado").item(0).getTextContent());
	}

	@Test
	void unEstatDiferentDeTramitadaEsUnError() throws Exception {
		when(clientScspHelper.enviarPeticioSincrona(anyString(), anyString(), any(), any(), anyInt(), anyBoolean())).thenReturn(
				respostaCedent("EMS00000000000000000000042", "0233"));

		RespostaBackofficeEntitat resposta = backofficeHelper.enviarPeticioSincronaEmisorCedent(peticioCedent());

		assertTrue(resposta.getException() instanceof ScspCedentException);
		assertEquals("0233", ((ScspCedentException)resposta.getException()).getCodiEstat());
		// La resposta del cedent es desa igualment
		assertFalse(resposta.getXmlResposta().toString().isEmpty());
	}

	@Test
	void unaRespostaDUnaAltraPeticioEsRebutja() throws Exception {
		when(clientScspHelper.enviarPeticioSincrona(anyString(), anyString(), any(), any(), anyInt(), anyBoolean())).thenReturn(
				respostaCedent("EMS00000000000000000000099", "0003"));

		RespostaBackofficeEntitat resposta = backofficeHelper.enviarPeticioSincronaEmisorCedent(peticioCedent());

		assertTrue(resposta.getException().getMessage().contains("d'una altra petició"));
	}

	@Test
	void unErrorDeComunicacioEsDesaALaResposta() throws Exception {
		when(clientScspHelper.enviarPeticioSincrona(anyString(), anyString(), any(), any(), anyInt(), anyBoolean())).thenThrow(
				new ScspCedentException("0301", "No autorizado", "<peticio/>", "<fault/>"));

		RespostaBackofficeEntitat resposta = backofficeHelper.enviarPeticioSincronaEmisorCedent(peticioCedent());

		assertTrue(((ScspCedentException)resposta.getException()).isDefinitiu());
		assertEquals("<peticio/>", resposta.getXmlPeticio().toString());
		assertEquals("<fault/>", resposta.getXmlResposta().toString());
	}

	private PeticioEmisorCedent peticioCedent() throws Exception {
		return backofficeHelper.prepararPeticioEmisorCedent(peticio, solicitud, servei, ruta, "EMS00000000000000000000042", null);
	}

	private static ClientScspHelper.RespostaCedent respostaCedent(String idPeticion, String estat) throws Exception {
		String xml = "<Respuesta xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta\">" +
				"<Atributos><IdPeticion>" + idPeticion + "</IdPeticion><NumElementos>1</NumElementos><TimeStamp>2026-10-27T20:00:00.000+01:00</TimeStamp>" +
				"<Estado><CodigoEstado>" + estat + "</CodigoEstado><LiteralError>Tramitada</LiteralError></Estado>" +
				"<CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos>" +
				"<Transmisiones><TransmisionDatos><DatosGenericos>" +
				"<Emisor><NifEmisor>P0704000A</NifEmisor><NombreEmisor>Ajuntament</NombreEmisor></Emisor>" +
				"<Solicitante><IdentificadorSolicitante>S0711001H</IdentificadorSolicitante><NombreSolicitante>Govern de les Illes Balears</NombreSolicitante>" +
				"<Finalidad>Proves</Finalidad><Consentimiento>Si</Consentimiento></Solicitante>" +
				"<Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>22222222J</Documentacion></Titular>" +
				"<Transmision><CodigoCertificado>SCDCPAJU</CodigoCertificado><IdSolicitud>000002</IdSolicitud><IdTransmision>TR-CEDENT-1</IdTransmision>" +
				"<FechaGeneracion>2026-10-27T20:00:00.000+01:00</FechaGeneracion></Transmision></DatosGenericos>" +
				"<DatosEspecificos xmlns=\"" + NS_DATESP + "\"><Retorno><Resultado>Empadronado</Resultado></Retorno></DatosEspecificos>" +
				"</TransmisionDatos></Transmisiones></Respuesta>";
		Element respuesta = ClientScspHelper.parse(xml).getDocumentElement();
		Constructor<ClientScspHelper.RespostaCedent> constructor = ClientScspHelper.RespostaCedent.class.getDeclaredConstructor(String.class, String.class, Element.class);
		return constructor.newInstance("<peticio signada/>", xml, respuesta);
	}

	private static String text(Element element, String nom) {
		NodeList nodes = element.getElementsByTagNameNS("*", nom);
		return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : null;
	}

	private static String solicitud(String idSolicitud, String document, String municipi) {
		return "<ns3:SolicitudTransmision><ns3:DatosGenericos>" +
				"<ns3:Emisor><ns3:NifEmisor>S0711001H</ns3:NifEmisor><ns3:NombreEmisor>CAIB</ns3:NombreEmisor></ns3:Emisor>" +
				"<ns3:Solicitante><ns3:IdentificadorSolicitante>S0711001H</ns3:IdentificadorSolicitante><ns3:NombreSolicitante>Govern de les Illes Balears</ns3:NombreSolicitante>" +
				"<ns3:Finalidad>Proves</ns3:Finalidad><ns3:Consentimiento>Si</ns3:Consentimiento></ns3:Solicitante>" +
				"<ns3:Titular><ns3:TipoDocumentacion>NIF</ns3:TipoDocumentacion><ns3:Documentacion>" + document + "</ns3:Documentacion></ns3:Titular>" +
				"<ns3:Transmision><ns3:CodigoCertificado>SCDCPAJU</ns3:CodigoCertificado><ns3:IdSolicitud>" + idSolicitud + "</ns3:IdSolicitud></ns3:Transmision>" +
				"</ns3:DatosGenericos>" +
				"<ns2:DatosEspecificos><ns2:Solicitud><ns2:ProvinciaSolicitud>07</ns2:ProvinciaSolicitud><ns2:MunicipioSolicitud>" + municipi + "</ns2:MunicipioSolicitud></ns2:Solicitud>" +
				"</ns2:DatosEspecificos></ns3:SolicitudTransmision>";
	}

}
