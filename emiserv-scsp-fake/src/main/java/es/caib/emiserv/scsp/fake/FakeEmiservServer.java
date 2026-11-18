package es.caib.emiserv.scsp.fake;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import es.caib.emiserv.scsp.fake.core.FakeRequest;
import es.caib.emiserv.scsp.fake.core.FakeResponses;
import es.caib.emiserv.scsp.fake.core.FakeSigner;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public final class FakeEmiservServer {

	private static final String DEFAULT_HOST = "0.0.0.0";
	private static final int DEFAULT_PORT = 18080;
	private static final String STATUS_PATH = "/__fake-emiserv";
	private static final String BACKOFFICE_PATH = "/ws/EmiservBackoffice";
	private static final String BACKOFFICE_WSDL_RESOURCE = "es/caib/emiserv/scsp/fake/EmiservBackoffice.wsdl";

	private FakeEmiservServer() {
	}

	public static void main(String[] args) throws Exception {
		String host = System.getProperty("fake.emiserv.host", DEFAULT_HOST);
		int port = Integer.parseInt(System.getProperty("fake.emiserv.port", String.valueOf(DEFAULT_PORT)));
		HttpServer server = createServer(host, port);
		server.start();
		System.out.println("Fake Emiserv escoltant a http://" + host + ":" + port);
		System.out.println("SCSP fake: POST http://" + host + ":" + port + "/scsp/<servei>/<entitat>");
		System.out.println("SCSP fake error: POST http://" + host + ":" + port + "/scsp/<servei>/<entitat>/ko");
		System.out.println("SCSP fake sense dades: POST http://" + host + ":" + port + "/scsp/<servei>/<entitat>/no");
		System.out.println("Backoffice fake: POST http://" + host + ":" + port + BACKOFFICE_PATH);
		System.out.println("Backoffice WSDL: GET  http://" + host + ":" + port + BACKOFFICE_PATH + "?wsdl");
	}

	static HttpServer createServer(String host, int port) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
		server.createContext("/", new FakeHandler());
		server.setExecutor(Executors.newCachedThreadPool());
		return server;
	}

	private static final class FakeHandler implements HttpHandler {
		private final FakeSigner signer = FakeSigner.fromSystemProperties();

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			try {
				String path = exchange.getRequestURI().getPath();
				if ("GET".equalsIgnoreCase(exchange.getRequestMethod()) && STATUS_PATH.equals(path)) {
					writeText(exchange, 200, statusBody());
					return;
				}
				if ("GET".equalsIgnoreCase(exchange.getRequestMethod()) && isBackofficeWsdlRequest(exchange)) {
					writeXml(exchange, 200, loadBackofficeWsdl(exchange));
					return;
				}
				if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
					writeText(exchange, 405, "Només s'accepten peticions GET a l'estat/WSDL i POST SOAP");
					return;
				}

				String requestXml = readBody(exchange.getRequestBody());
				FakeRequest requestInfo = FakeRequest.fromXml(requestXml);
				requestInfo = withPathInfo(requestInfo, ScspPathInfo.fromPath(path));
				log(exchange, requestInfo, requestXml);

				if (isBackofficePath(path)) {
					handleBackoffice(exchange, requestInfo, isBackofficeKoPath(path));
				} else {
					handleScsp(exchange, requestInfo, requestXml);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
				writeText(exchange, 500, "Error intern del fake: " + e.getMessage());
			} finally {
				exchange.close();
			}
		}

		private void handleBackoffice(HttpExchange exchange, FakeRequest requestInfo, boolean forceError) throws IOException {
			String operation = requestInfo.getBodyRoot();
			if ("peticionSincrona".equals(operation)) {
				writeXml(exchange, 200, FakeResponses.backofficeSyncResponse(requestInfo, forceError));
				return;
			}
			if ("solicitarRespuesta".equals(operation)) {
				writeXml(exchange, 200, FakeResponses.backofficeSolicitudResponse(requestInfo, forceError));
				return;
			}
			if ("peticionAsincrona".equals(operation)) {
				writeXml(exchange, 200, FakeResponses.backofficeAsyncResponse(requestInfo, forceError));
				return;
			}
			writeXml(exchange, 500, FakeResponses.soapFault("Server", "Operacio backoffice no suportada: " + operation));
		}

		private void handleScsp(HttpExchange exchange, FakeRequest requestInfo, String requestXml) throws IOException {
			String xmlLower = requestXml.toLowerCase();
			if (requestInfo.isForceError() || xmlLower.contains("<error") || xmlLower.contains(":error")) {
				writeXml(exchange, 500, signer.sign(
					FakeResponses.scspFault(
						requestInfo,
						"0101",
						"Error forcat pel fake SCSP"),
					requestInfo.getSignatureStyle()));
				return;
			}
			writeXml(exchange, 200, signer.sign(
				FakeResponses.scspResponse(requestInfo, FakeResponses.datosEspecificos(requestInfo, requestInfo.isNoData()), "OK"),
				requestInfo.getSignatureStyle()));
		}

		private String loadBackofficeWsdl(HttpExchange exchange) {
			try (InputStream input = getClass().getClassLoader().getResourceAsStream(BACKOFFICE_WSDL_RESOURCE)) {
				if (input == null) {
					throw new IOException("No s'ha trobat el WSDL de backoffice");
				}
				String wsdl = readBody(input);
				String address = resolveBaseUrl(exchange) + BACKOFFICE_PATH;
				return wsdl.replace(
					"location=\"http://localhost:8080/emiservback/ws/EmiservBackoffice\"",
					"location=\"" + address + "\"");
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		private boolean isBackofficeWsdlRequest(HttpExchange exchange) {
			String path = exchange.getRequestURI().getPath();
			String query = exchange.getRequestURI().getQuery();
			return isBackofficePath(path) && query != null && query.toLowerCase().contains("wsdl");
		}

		private boolean isBackofficePath(String path) {
			return isBackofficeBasePath(path) || isBackofficeKoPath(path);
		}

		private boolean isBackofficeBasePath(String path) {
			return BACKOFFICE_PATH.equals(path) || "/backoffice".equals(path) || "/backoffice/EmiservBackoffice".equals(path)
				|| path.startsWith(BACKOFFICE_PATH + "/");
		}

		private boolean isBackofficeKoPath(String path) {
			return (BACKOFFICE_PATH + "/ko").equals(path)
				|| "/backoffice/ko".equals(path)
				|| "/backoffice/EmiservBackoffice/ko".equals(path)
				|| (path.startsWith(BACKOFFICE_PATH + "/") && path.endsWith("/ko"));
		}

		private String resolveBaseUrl(HttpExchange exchange) {
			String host = exchange.getRequestHeaders().getFirst("Host");
			if (host == null || host.isBlank()) {
				host = exchange.getLocalAddress().getHostString() + ":" + exchange.getLocalAddress().getPort();
			}
			return "http://" + host;
		}

		private void log(HttpExchange exchange, FakeRequest requestInfo, String requestXml) {
			System.out.println("[FAKE-EMISERV] method=" + exchange.getRequestMethod()
				+ " path=" + exchange.getRequestURI().getPath()
				+ " bodyRoot=" + requestInfo.getBodyRoot()
				+ " servei=" + requestInfo.getServiceCode()
				+ " entitat=" + requestInfo.getEntityCode()
				+ " forceError=" + requestInfo.isForceError()
				+ " noData=" + requestInfo.isNoData()
				+ " idPeticion=" + requestInfo.getIdPeticion()
				+ " idSolicitudes=" + requestInfo.getIdSolicitudes());
			System.out.println(requestXml);
		}

		private String statusBody() {
			return "Fake Emiserv actiu\n"
				+ "- SCSP POST /scsp/<servei>/<entitat> o /scsp/<servei>/<entitat>/ko o /scsp/<servei>/<entitat>/no\n"
				+ "- Backoffice POST " + BACKOFFICE_PATH + "\n"
				+ "- WSDL GET " + BACKOFFICE_PATH + "?wsdl\n";
		}
	}

	static final class ScspPathInfo {
		private final String serviceCode;
		private final String entityCode;
		private final boolean forceError;
		private final boolean noData;

		private ScspPathInfo(String serviceCode, String entityCode, boolean forceError, boolean noData) {
			this.serviceCode = serviceCode;
			this.entityCode = entityCode;
			this.forceError = forceError;
			this.noData = noData;
		}

		static ScspPathInfo fromPath(String path) {
			if (path != null && path.startsWith(BACKOFFICE_PATH + "/")) {
				return fromBackofficePath(path);
			}
			if (path == null || !path.startsWith("/scsp/")) {
				return null;
			}
			String[] rawParts = path.split("/");
			List<String> parts = new ArrayList<>();
			for (String rawPart : rawParts) {
				if (rawPart != null && !rawPart.isBlank()) {
					parts.add(rawPart);
				}
			}
			if (parts.size() < 3 || !"scsp".equals(parts.get(0))) {
				return null;
			}
			String serviceCode = parts.get(1);
			String entityCode = parts.get(2);
			boolean forceError = parts.size() > 3 && "ko".equalsIgnoreCase(parts.get(3));
			boolean noData = parts.size() > 3 && "no".equalsIgnoreCase(parts.get(3));
			return new ScspPathInfo(serviceCode, entityCode, forceError, noData);
		}

		/**
		 * Backoffice d'una entitat (rutes de tipus BACKOFFICE dels enrutadors):
		 * /ws/EmiservBackoffice/<entitat>, /ws/EmiservBackoffice/<entitat>/no, /ws/EmiservBackoffice/<entitat>/ko
		 */
		static ScspPathInfo fromBackofficePath(String path) {
			List<String> parts = new ArrayList<>();
			for (String rawPart : path.substring(BACKOFFICE_PATH.length()).split("/")) {
				if (rawPart != null && !rawPart.isBlank()) {
					parts.add(rawPart);
				}
			}
			if (parts.isEmpty() || "ko".equalsIgnoreCase(parts.get(0))) {
				return null;
			}
			boolean forceError = parts.size() > 1 && "ko".equalsIgnoreCase(parts.get(1));
			boolean noData = parts.size() > 1 && "no".equalsIgnoreCase(parts.get(1));
			return new ScspPathInfo(null, parts.get(0), forceError, noData);
		}
	}

	private static FakeRequest withPathInfo(FakeRequest requestInfo, ScspPathInfo pathInfo) {
		if (pathInfo == null) {
			return requestInfo;
		}
		return requestInfo.withRuta(pathInfo.serviceCode, pathInfo.entityCode, pathInfo.forceError, pathInfo.noData);
	}

	private static String readBody(InputStream inputStream) throws IOException {
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	private static void writeText(HttpExchange exchange, int status, String body) throws IOException {
		writeResponse(exchange, status, "text/plain; charset=UTF-8", body);
	}

	private static void writeXml(HttpExchange exchange, int status, String body) throws IOException {
		writeResponse(exchange, status, "text/xml; charset=UTF-8", body);
	}

	private static void writeResponse(HttpExchange exchange, int status, String contentType, String body) throws IOException {
		byte[] payload = body.getBytes(StandardCharsets.UTF_8);
		Headers headers = exchange.getResponseHeaders();
		headers.set("Content-Type", contentType);
		exchange.sendResponseHeaders(status, payload.length);
		exchange.getResponseBody().write(payload);
	}
}
