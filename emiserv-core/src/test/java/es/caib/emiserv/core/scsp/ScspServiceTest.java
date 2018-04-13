/**
 * 
 */
package es.caib.emiserv.core.scsp;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.caib.emiserv.core.helper.PropertiesHelper;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Emisor;
import es.scsp.bean.common.Funcionario;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Procedimiento;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudRespuesta;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.Solicitudes;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.Transmision;
import es.scsp.client.ClienteUnico;

/**
 * Tests amb enviament de peticions SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/emiserv/core/application-context-test.xml"})
public class ScspServiceTest {

	@Autowired
	ClienteUnico clienteUnico;



	@Before
	public void setUp() {
		PropertiesHelper.getProperties("classpath:es/caib/emiserv/core/test.properties");
	}

	/*@Test
    public void testPeticionSincrona() {
		String serveiCodi = "EMISERVTEST";
		String peticioId = "TST" + System.currentTimeMillis();
		try {
			Peticion peticion = new Peticion();
			Atributos atributos = new Atributos();
			atributos.setIdPeticion(peticioId);
			atributos.setCodigoCertificado(serveiCodi);
			atributos.setTimeStamp(dataFormatScsp(new Date()));
			atributos.setNumElementos("1");
			peticion.setAtributos(atributos);
			Solicitudes solicitudes = new Solicitudes();
			solicitudes.setSolicitudTransmision(new ArrayList<SolicitudTransmision>());
			SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
			DatosGenericos datosGenericos = new DatosGenericos();
			Emisor emisor = new Emisor();
			emisor.setNifEmisor("S0711001H");
			emisor.setNombreEmisor("Govern de les Illes Balears");
			datosGenericos.setEmisor(emisor);
			Solicitante solicitante = new Solicitante();
			solicitante.setIdentificadorSolicitante("B07167448");
			solicitante.setNombreSolicitante("Límit Tecnologies");
			solicitante.setUnidadTramitadora("Departament de programari");
			Procedimiento procedimiento = new Procedimiento();
			procedimiento.setCodProcedimiento("TEST_EMISERV");
			procedimiento.setNombreProcedimiento("Tests funcionalitat EMISERV");
			solicitante.setProcedimiento(procedimiento);
			solicitante.setFinalidad("TESTS EMISERV");
			solicitante.setConsentimiento(Consentimiento.Si);
			Funcionario funcionario = new Funcionario();
			funcionario.setNifFuncionario("99999999R");
			funcionario.setNombreCompletoFuncionario("Limit Test");
			solicitante.setFuncionario(funcionario);
			solicitante.setIdExpediente("EXP/07042015");
			datosGenericos.setSolicitante(solicitante);
			Titular titular = new Titular();
			titular.setTipoDocumentacion(TipoDocumentacion.NIF);
			titular.setDocumentacion("00000000T");
			datosGenericos.setTitular(titular);
			Transmision transmision = new Transmision();
			transmision.setCodigoCertificado(serveiCodi);
			transmision.setIdSolicitud(peticioId);
			transmision.setIdTransmision(peticioId);
			transmision.setFechaGeneracion(dataFormatScsp(new Date()));
			datosGenericos.setTransmision(transmision);
			solicitudTransmision.setDatosGenericos(datosGenericos);
			//solicitudTransmision.setDatosEspecificos(generarDatosEspecificos());
			solicitudes.getSolicitudTransmision().add(solicitudTransmision);
			peticion.setSolicitudes(solicitudes);
			Respuesta respuesta = clienteUnico.realizaPeticionSincrona(peticion);
			System.out.println(">>> respuesta: " + respuesta);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/

	//@Test
    public void testPeticionSincrona() {
		String serveiCodi = "SVDSCDDWS01";
		String peticioId = generarPeticioId();
		try {
			Peticion peticion = new Peticion();
			Atributos atributos = new Atributos();
			atributos.setIdPeticion(peticioId);
			atributos.setCodigoCertificado(serveiCodi);
			atributos.setTimeStamp(dataFormatScsp(new Date()));
			atributos.setNumElementos("1");
			peticion.setAtributos(atributos);
			Solicitudes solicitudes = new Solicitudes();
			solicitudes.setSolicitudTransmision(new ArrayList<SolicitudTransmision>());
			SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
			DatosGenericos datosGenericos = new DatosGenericos();
			Emisor emisor = new Emisor();
			emisor.setNifEmisor("S0711001H");
			emisor.setNombreEmisor("Govern de les Illes Balears");
			datosGenericos.setEmisor(emisor);
			Solicitante solicitante = new Solicitante();
			solicitante.setIdentificadorSolicitante("B07167448");
			solicitante.setNombreSolicitante("Límit Tecnologies");
			solicitante.setUnidadTramitadora("Departament de programari");
			Procedimiento procedimiento = new Procedimiento();
			procedimiento.setCodProcedimiento("TEST_EMISERV");
			procedimiento.setNombreProcedimiento("Tests funcionalitat EMISERV");
			solicitante.setProcedimiento(procedimiento);
			solicitante.setFinalidad("TESTS EMISERV");
			solicitante.setConsentimiento(Consentimiento.Si);
			Funcionario funcionario = new Funcionario();
			funcionario.setNifFuncionario("99999999R");
			funcionario.setNombreCompletoFuncionario("Limit Test");
			solicitante.setFuncionario(funcionario);
			solicitante.setIdExpediente("EXP/07042015");
			datosGenericos.setSolicitante(solicitante);
			Titular titular = new Titular();
			datosGenericos.setTitular(titular);
			Transmision transmision = new Transmision();
			transmision.setCodigoCertificado(serveiCodi);
			transmision.setIdSolicitud(peticioId);
			transmision.setIdTransmision(peticioId);
			transmision.setFechaGeneracion(dataFormatScsp(new Date()));
			datosGenericos.setTransmision(transmision);
			solicitudTransmision.setDatosGenericos(datosGenericos);
			solicitudTransmision.setDatosEspecificos(generarDatosEspecificosDiscapacitat());
			solicitudes.getSolicitudTransmision().add(solicitudTransmision);
			peticion.setSolicitudes(solicitudes);
			Respuesta respuesta = clienteUnico.realizaPeticionSincrona(peticion);
			System.out.println(">>> respuesta: " + respuesta);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
    public void testPeticionAsincrona() {
		String serveiCodi = "SVDSCDWS01";
		//String peticioId = "SPP0000000121064";
		String peticioId = generarPeticioId();
		try {
			Peticion peticion = new Peticion();
			Atributos atributos = new Atributos();
			atributos.setIdPeticion(peticioId);
			atributos.setCodigoCertificado(serveiCodi);
			atributos.setTimeStamp(dataFormatScsp(new Date()));
			atributos.setNumElementos("1");
			peticion.setAtributos(atributos);
			Solicitudes solicitudes = new Solicitudes();
			solicitudes.setSolicitudTransmision(new ArrayList<SolicitudTransmision>());
			SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
			DatosGenericos datosGenericos = new DatosGenericos();
			Emisor emisor = new Emisor();
			emisor.setNifEmisor("S2833002E");
			emisor.setNombreEmisor("MINHAP");
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
			solicitudTransmision.setDatosGenericos(datosGenericos);
			solicitudTransmision.setDatosEspecificos(generarDatosEspecificosCanvidom());
			solicitudes.getSolicitudTransmision().add(solicitudTransmision);
			peticion.setSolicitudes(solicitudes);
			ConfirmacionPeticion confirmacionPeticion = clienteUnico.realizaPeticionAsincrona(peticion);
			System.out.println(">>> confirmacionPeticion: " + confirmacionPeticion);
			SolicitudRespuesta solicitudRespuesta = new SolicitudRespuesta();
			Atributos atributosSr = new Atributos();
			atributosSr.setIdPeticion(peticioId);
			atributosSr.setCodigoCertificado(serveiCodi);
			atributosSr.setTimeStamp(dataFormatScsp(new Date()));
			atributosSr.setNumElementos("0");
			solicitudRespuesta.setAtributos(atributosSr);
			Respuesta respuesta = clienteUnico.realizaSolicitudRespuesta(solicitudRespuesta);
			System.out.println(">>> respuesta: " + respuesta);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//@Test
    public void testSolicitudRespuesta() {
    	String serveiCodi = "SVDSCDWS01";
		String peticioId = "TST1504246788886";
		try {
			SolicitudRespuesta solicitudRespuesta = new SolicitudRespuesta();
			Atributos atributos = new Atributos();
			atributos.setIdPeticion(peticioId);
			atributos.setCodigoCertificado(serveiCodi);
			atributos.setTimeStamp(dataFormatScsp(new Date()));
			atributos.setNumElementos("0");
			solicitudRespuesta.setAtributos(atributos);
			Respuesta respuesta = clienteUnico.realizaSolicitudRespuesta(solicitudRespuesta);
			System.out.println(">>> respuesta: " + respuesta);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



	private String generarPeticioId() {
		return "TST" + System.currentTimeMillis();
	}

	private Object generarDatosEspecificosCanvidom() throws IOException, ParserConfigurationException, SAXException {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/emiserv/core/service/datos-especificos-canvidom.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		Document document = documentBuilderFactory.newDocumentBuilder().parse(is);
		return document.getDocumentElement();
	}
	private Object generarDatosEspecificosDiscapacitat() throws IOException, ParserConfigurationException, SAXException {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/emiserv/core/service/datos-especificos-discapacitat.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		Document document = documentBuilderFactory.newDocumentBuilder().parse(is);
		return document.getDocumentElement();
	}

	private String dataFormatScsp(Date data) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		return df.format(data);
	}

}
