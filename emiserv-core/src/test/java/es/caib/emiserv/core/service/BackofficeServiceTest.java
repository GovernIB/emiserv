/**
 * 
 */
package es.caib.emiserv.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.caib.emiserv.core.api.dto.AuditoriaTransmisionDto;
import es.caib.emiserv.core.api.dto.BackofficeSolicitudEstatEnumDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.dto.ServeiTipusEnumDto;
import es.caib.emiserv.core.api.service.BackofficeService;
import es.caib.emiserv.core.api.service.ServeiService;
import es.caib.emiserv.core.api.service.ws.backoffice.Atributos;
import es.caib.emiserv.core.api.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Consentimiento;
import es.caib.emiserv.core.api.service.ws.backoffice.DatosGenericos;
import es.caib.emiserv.core.api.service.ws.backoffice.Emisor;
import es.caib.emiserv.core.api.service.ws.backoffice.Estado;
import es.caib.emiserv.core.api.service.ws.backoffice.Funcionario;
import es.caib.emiserv.core.api.service.ws.backoffice.Peticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Procedimiento;
import es.caib.emiserv.core.api.service.ws.backoffice.Respuesta;
import es.caib.emiserv.core.api.service.ws.backoffice.Solicitante;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudRespuesta;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.core.api.service.ws.backoffice.Solicitudes;
import es.caib.emiserv.core.api.service.ws.backoffice.TipoDocumentacion;
import es.caib.emiserv.core.api.service.ws.backoffice.Titular;
import es.caib.emiserv.core.api.service.ws.backoffice.Transmision;
import es.caib.emiserv.core.helper.PropertiesHelper;

/**
 * Tests per al servei de suport als backoffices.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/emiserv/core/application-context-test.xml"})
public class BackofficeServiceTest extends BaseServiceTest {

	@Autowired
	private TestService testService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private BackofficeService backofficeService;

	private ServeiDto servei;



	@Before
	public void setUp() {
		PropertiesHelper.getProperties("classpath:es/caib/emiserv/core/test.properties");
		servei = new ServeiDto();
		servei.setCodi("SVDSCDWS01");
		servei.setNom("Comunicación de cambio de domicilio");
		servei.setTipus(ServeiTipusEnumDto.BACKOFFICE);
		servei.setBackofficeCaibAsyncTer(3600);
		servei.setActiu(true);
	}

	@Test
    public void testAsincrona() {
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<Object> elementsCreats) throws Exception {
						ServeiDto dto = serveiService.findByCodi(servei.getCodi());
						assertNotNull(dto);
						String serveiCodi = dto.getCodi();
						String peticionId = "SPP0000000121062";
						testService.novaPeticionRespuesta(
								dto.getCodi(),
								peticionId);
						Peticion peticion = generarPeticioAsincrona(
								peticionId,
								serveiCodi);
						ConfirmacionPeticion confirmacionPeticion = backofficeService.peticioBackofficeAsincrona(
								peticion);
						try {
							// Comprovació de la confirmacionPeticion
							assertNotNull(confirmacionPeticion);
							assertNotNull(confirmacionPeticion.getAtributos());
							assertEquals(
									serveiCodi,
									confirmacionPeticion.getAtributos().getCodigoCertificado());
							assertEquals(
									peticionId,
									confirmacionPeticion.getAtributos().getIdPeticion());
							assertEquals(
									"0",
									confirmacionPeticion.getAtributos().getNumElementos());
							Estado estado = confirmacionPeticion.getAtributos().getEstado();
							assertNotNull(estado);
							assertEquals(
									"0002",
									estado.getCodigoEstado());
							assertNull(estado.getCodigoEstadoSecundario());
							assertNull(estado.getLiteralError());
							assertEquals(
									new Integer(1),
									estado.getTiempoEstimadoRespuesta());
							// Prova de recepció d'una SolicitudRespuesta quan
							// encara no s'ha fet cap petició al backoffice.
							Respuesta respuesta = backofficeService.peticioBackofficeSolicitudRespuesta(
									generarSolicitudRespuesta(
											peticionId,
											serveiCodi));
							assertNotNull(respuesta);
							assertNotNull(respuesta.getAtributos());
							assertEquals(
									serveiCodi,
									respuesta.getAtributos().getCodigoCertificado());
							assertEquals(
									peticionId,
									respuesta.getAtributos().getIdPeticion());
							assertNotNull(respuesta.getAtributos().getEstado());
							assertEquals(
									"0002",
									respuesta.getAtributos().getEstado().getCodigoEstado());
							// Simular cridada al backoffice mitjançant una implementació
							// de test.
							testService.novaSolicitudTransmision(
									peticionId,
									peticionId,
									peticionId,
									peticion.getSolicitudes().getSolicitudTransmision().get(0));
							testService.nouTokenData(
									peticionId,
									0,
									obtenirTokenDataPeticio());
							testService.configurarBackofficesPerTests(
									obtenirBackofficePeticio(),
									obtenirBackofficeResposta());
							List<AuditoriaTransmisionDto> solicitudsAbans = backofficeService.solicitudFindByPeticio(peticionId);
							assertNotNull(solicitudsAbans);
							assertEquals(1, solicitudsAbans.size());
							AuditoriaTransmisionDto solicitudAbans = solicitudsAbans.get(0);
							assertEquals(
									BackofficeSolicitudEstatEnumDto.PENDENT,
									solicitudAbans.getBackofficeEstat());
							testService.processarPeticioPendent(peticionId);
							List<AuditoriaTransmisionDto> solicitudsDespres = backofficeService.solicitudFindByPeticio(peticionId);
							assertNotNull(solicitudsDespres);
							assertEquals(1, solicitudsDespres.size());
							AuditoriaTransmisionDto solicitudDespres = solicitudsDespres.get(0);
							assertEquals(
									BackofficeSolicitudEstatEnumDto.TRAMITADA,
									solicitudDespres.getBackofficeEstat());
							// Prova de recepció d'una SolicitudRespuesta quan
							// ja està disponible la resposta del backoffice.
							respuesta = backofficeService.peticioBackofficeSolicitudRespuesta(
									generarSolicitudRespuesta(
											peticionId,
											serveiCodi));
							assertNotNull(respuesta);
							assertNotNull(respuesta.getAtributos());
							assertEquals(
									serveiCodi,
									respuesta.getAtributos().getCodigoCertificado());
							assertEquals(
									peticionId,
									respuesta.getAtributos().getIdPeticion());
							assertNotNull(respuesta.getAtributos().getEstado());
							assertEquals(
									"0003",
									respuesta.getAtributos().getEstado().getCodigoEstado());
							System.out.println(">>> Resposta: " + imprimirObjecte(respuesta));
						} finally {
							testService.backofficeSolicitudDeleteAll();
							testService.backofficePeticioDeleteAll();
						}
					}
				},
				servei);
	}



	private Peticion generarPeticioAsincrona(
			String peticioId,
			String serveiCodi) throws IOException, ParserConfigurationException, SAXException {
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(peticioId);
		atributos.setCodigoCertificado(serveiCodi);
		atributos.setNumElementos("1");
		atributos.setTimeStamp(dataFormatScsp(new Date()));
		peticion.setAtributos(atributos);
		Solicitudes solicitudes = new Solicitudes();
		ArrayList<SolicitudTransmision> sts = new ArrayList<SolicitudTransmision>();
		SolicitudTransmision solicitud = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Emisor emisor = new Emisor();
		emisor.setNifEmisor("S0711001H");
		emisor.setNombreEmisor("Govern de les Illes Balears");
		datosGenericos.setEmisor(emisor);
		Solicitante solicitante = new Solicitante();
		solicitante.setIdentificadorSolicitante("S2833002E");
		solicitante.setNombreSolicitante("MINISTERIO DE HACIENDA Y AP");
		solicitante.setUnidadTramitadora("SG COORD ESTUDIOS E IMPULSO ADM ELECLT(MINHAP)");
		Procedimiento procedimiento = new Procedimiento();
		procedimiento.setCodProcedimiento("SVDR_20090505_000005");
		procedimiento.setNombreProcedimiento("SERVICIO DE COMUNICACION DEL CAMBIO DE DOMICILIO");
		solicitante.setProcedimiento(procedimiento);
		solicitante.setFinalidad("PRUEBAS DE INTEGRACION SCCD");
		solicitante.setConsentimiento(Consentimiento.Si);
		Funcionario funcionario = new Funcionario();
		funcionario.setNifFuncionario("99999999R");
		funcionario.setNombreCompletoFuncionario("JUAN ESPAÑOL ESPAÑOL");
		solicitante.setFuncionario(funcionario);
		solicitante.setIdExpediente("EXP/07042015");
		datosGenericos.setSolicitante(solicitante);
		Titular titular = new Titular();
		titular.setTipoDocumentacion(TipoDocumentacion.NIF);
		titular.setDocumentacion("00000000T");
		titular.setNombreCompleto("OLGA SAN MIGUEL CHAO");
		titular.setNombre("OLGA");
		titular.setApellido1("SAN MIGUEL");
		titular.setApellido2("CHAO");
		datosGenericos.setTitular(titular);
		Transmision transmision = new Transmision();
		transmision.setCodigoCertificado(serveiCodi);
		transmision.setIdSolicitud(peticioId);
		transmision.setIdTransmision(peticioId);
		transmision.setFechaGeneracion(dataFormatScsp(new Date()));
		datosGenericos.setTransmision(transmision);
		solicitud.setDatosGenericos(datosGenericos);
		solicitud.setDatosEspecificos(generarDatosEspecificos());
		sts.add(solicitud);
		solicitudes.setSolicitudTransmision(sts);
		peticion.setSolicitudes(solicitudes);
		return peticion;
	}

	private Object generarDatosEspecificos() throws IOException, ParserConfigurationException, SAXException {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/emiserv/core/service/datos-especificos-discapacitat.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		Document document = documentBuilderFactory.newDocumentBuilder().parse(is);
		return document.getDocumentElement();
	}
	private String obtenirTokenDataPeticio() throws IOException, ParserConfigurationException, SAXException {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/emiserv/core/service/token-data-peticio-test.xml");
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		return writer.toString();
	}
	private String obtenirBackofficePeticio() throws IOException, ParserConfigurationException, SAXException {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/emiserv/core/service/backoffice-peticio-test.xml");
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		return writer.toString();
	}
	private String obtenirBackofficeResposta() throws IOException, ParserConfigurationException, SAXException {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/emiserv/core/service/backoffice-resposta-test.xml");
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		return writer.toString();
	}

	private SolicitudRespuesta generarSolicitudRespuesta(
			String peticioId,
			String serveiCodi) {
		SolicitudRespuesta solicitudRespuesta = new SolicitudRespuesta();
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(peticioId);
		atributos.setCodigoCertificado(serveiCodi);
		atributos.setNumElementos("1");
		atributos.setTimeStamp(dataFormatScsp(new Date()));
		solicitudRespuesta.setAtributos(atributos);
		return solicitudRespuesta;
	}

	private String dataFormatScsp(Date data) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		return df.format(data);
	}

}
