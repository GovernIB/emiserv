package es.caib.emiserv.scsp.fake;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FakeEmiservServerTest {

	private final HttpClient httpClient = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(5))
		.build();

	private HttpServer server;

	@AfterEach
	void tearDown() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	void backofficeWsdlIsServed() throws Exception {
		server = FakeEmiservServer.createServer("127.0.0.1", 0);
		server.start();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(uri("/ws/EmiservBackoffice?wsdl"))
			.timeout(Duration.ofSeconds(5))
			.GET()
			.build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("EmiservBackofficeService"));
		assertTrue(response.body().contains("/ws/EmiservBackoffice"));
	}

	@Test
	void backofficeSyncReturnsSoapResponse() throws Exception {
		server = FakeEmiservServer.createServer("127.0.0.1", 0);
		server.start();

		String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://caib.es/emiserv/backoffice\">"
			+ "<soapenv:Body>"
			+ "<tns:peticionSincrona><peticion><atributos><codigoCertificado>SVDTEST</codigoCertificado><idPeticion>PET-1</idPeticion></atributos></peticion></tns:peticionSincrona>"
			+ "</soapenv:Body></soapenv:Envelope>";

		HttpResponse<String> response = post("/ws/EmiservBackoffice", requestXml);

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("peticionSincronaResponse"));
		assertTrue(response.body().contains("<idPeticion>PET-1</idPeticion>"));
		assertTrue(response.body().contains("<codigoCertificado>SVDTEST</codigoCertificado>"));
	}

	@Test
	void scspKoReturnsSoapFault() throws Exception {
		server = FakeEmiservServer.createServer("127.0.0.1", 0);
		server.start();

		String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:scsp=\"http://intermediacion.redsara.es/scsp/esquemas/solicitudrespuesta\">"
			+ "<soapenv:Body>"
			+ "<scsp:Peticion><scsp:Atributos><scsp:CodigoCertificado>SVDSCDDWS01</scsp:CodigoCertificado><scsp:IdPeticion>PET-2</scsp:IdPeticion></scsp:Atributos><error/></scsp:Peticion>"
			+ "</soapenv:Body></soapenv:Envelope>";

		HttpResponse<String> response = post("/scsp/SVDSCDDWS01/CAIB/ko", requestXml);

		assertEquals(500, response.statusCode());
		assertTrue(response.body().contains("Fault"));
	}

	@Test
	void scspMultiplePathReturnsOkResponse() throws Exception {
		server = FakeEmiservServer.createServer("127.0.0.1", 0);
		server.start();

		String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:scsp=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\">"
			+ "<soapenv:Body>"
			+ "<scsp:Peticion><scsp:Atributos><scsp:IdPeticion>PET-3</scsp:IdPeticion></scsp:Atributos></scsp:Peticion>"
			+ "</soapenv:Body></soapenv:Envelope>";

		HttpResponse<String> response = post("/scsp/SVDTEST/CAIB", requestXml);

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("<CodigoCertificado>SVDTEST</CodigoCertificado>"));
		assertTrue(response.body().contains("<IdPeticion>PET-3</IdPeticion>"));
		assertTrue(response.body().contains("http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta"));
		assertTrue(response.body().contains("<Emisor>"));
		assertTrue(response.body().contains("<Solicitante>"));
		assertTrue(response.body().contains("<Titular>"));
		assertTrue(response.body().contains("<CodigoEstado>0</CodigoEstado>"));
		assertTrue(response.body().contains("<FechaCaducidad>31/12/2035</FechaCaducidad>"));
		assertFalse(response.body().contains("<TransmisionDatos><Estado>"));
	}

	@Test
	void svdsctfnws01ReturnsMoreRealisticFamilyData() throws Exception {
		server = FakeEmiservServer.createServer("127.0.0.1", 0);
		server.start();

		String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:scsp=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\" xmlns:de=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">"
			+ "<soapenv:Body>"
			+ "<scsp:Peticion>"
			+ "<scsp:Atributos><scsp:CodigoCertificado>SVDSCTFNWS01</scsp:CodigoCertificado><scsp:IdPeticion>PET-FN-1</scsp:IdPeticion></scsp:Atributos>"
			+ "<scsp:Solicitudes><scsp:SolicitudTransmision><scsp:DatosGenericos><scsp:Titular><scsp:Documentacion>18225486X</scsp:Documentacion><scsp:Nombre>Sion</scsp:Nombre><scsp:Apellido1>Andreu</scsp:Apellido1></scsp:Titular></scsp:DatosGenericos>"
			+ "<scsp:DatosEspecificos><de:Consulta><de:TituloFamiliaNumerosa><de:CodigoComunidadAutonoma>04</de:CodigoComunidadAutonoma></de:TituloFamiliaNumerosa></de:Consulta></scsp:DatosEspecificos>"
			+ "</scsp:SolicitudTransmision></scsp:Solicitudes>"
			+ "</scsp:Peticion>"
			+ "</soapenv:Body></soapenv:Envelope>";

		HttpResponse<String> response = post("/scsp/SVDSCTFNWS01/mallorca", requestXml);

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("<NumeroTitulo>TFN-MALLORCA-18225486X-2026</NumeroTitulo>"));
		assertTrue(response.body().contains("<Categoria>G</Categoria>"));
		assertTrue(response.body().contains("<FechaExpedicion>01/01/2024</FechaExpedicion>"));
		assertTrue(response.body().contains("<NumeroHijos>3</NumeroHijos>"));
		assertTrue(response.body().contains("<ListaBeneficiariosRetorno>"));
		assertTrue(response.body().contains("<BeneficiarioRetorno>"));
		assertTrue(response.body().contains("<Nombre>Sion</Nombre>"));
		assertTrue(response.body().contains("<Apellido1>Andreu</Apellido1>"));
		assertTrue(response.body().contains("<TituloVigente>S</TituloVigente>"));
		assertTrue(response.body().contains("<ds:Signature"));
		assertTrue(response.body().contains("<ds:X509Certificate>"));
		assertFalse(response.body().contains("wsse:Security"));
		assertFalse(response.body().contains("<Provincia>"));
		assertFalse(response.body().contains("<DatosTitular>"));
		assertFalse(response.body().contains("<Codigo>0</Codigo>"));
	}

	@Test
	void wsSecurityRequestReturnsWsSecuritySignature() throws Exception {
		server = FakeEmiservServer.createServer("127.0.0.1", 0);
		server.start();

		String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:scsp=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"
			+ "<soapenv:Header><wsse:Security/></soapenv:Header>"
			+ "<soapenv:Body><scsp:Peticion><scsp:Atributos><scsp:CodigoCertificado>SVDTEST</scsp:CodigoCertificado><scsp:IdPeticion>PET-WSSE-1</scsp:IdPeticion></scsp:Atributos></scsp:Peticion></soapenv:Body>"
			+ "</soapenv:Envelope>";

		HttpResponse<String> response = post("/scsp/SVDTEST/CAIB", requestXml);

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("wsse:Security"));
		assertTrue(response.body().contains("BinarySecurityToken"));
	}

	private HttpResponse<String> post(String path, String body) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(uri(path))
			.timeout(Duration.ofSeconds(5))
			.header("Content-Type", "text/xml; charset=UTF-8")
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.build();
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private URI uri(String path) {
		return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + path);
	}
}
