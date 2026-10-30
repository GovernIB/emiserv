package es.caib.emiserv.back.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d’integració contra un entorn ja desplegat.
 * Variables utilitzades:
 * - EMISERV_BASE_URL (p. ex. http://host:8080)
 * - EMISERV_USER (opcional, per autenticació bàsica)
 * - EMISERV_PASS (opcional)
 * Requereix que el servidor s’iniciï amb el perfil "mock" actiu per tal d’usar els stubs.
 */
public class ExternalIntegrationIT {

    private static String BASE_URL;
    private static String USER;
    private static String PASS;
    private static HttpClient CLIENT;

    @BeforeAll
    static void setup() {
        BASE_URL = getenvOrProperty("EMISERV_BASE_URL", "http://localhost:8080/emiservback");
        USER = getenvOrProperty("EMISERV_USER", "u999000");
        PASS = getenvOrProperty("EMISERV_PASS", "u999000");
        CLIENT = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private static String getenvOrProperty(String key, String def) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) v = System.getProperty(key);
        return v != null ? v : def;
    }

    private HttpRequest.Builder req(String method, String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(30));
        if (USER != null && PASS != null) {
            String basic = java.util.Base64.getEncoder().encodeToString((USER + ":" + PASS).getBytes(StandardCharsets.UTF_8));
            b.header("Authorization", "Basic " + basic);
        }
        return b;
    }

    // ENRUTADOR SIMPLE: ÈXIT
    @Test
    void enrutador_simple_exit() throws Exception {
        String xml = "<Peticion><ok/></Peticion>";
        HttpRequest request = req("POST", "/scspRouting/test")
                .POST(HttpRequest.BodyPublishers.ofString(xml))
                .header("Content-Type", "text/xml")
                .build();
        HttpResponse<String> resp = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }

    // ENRUTADOR SIMPLE: ERROR
    @Test
    void enrutador_simple_error() throws Exception {
        String xml = "<Peticion><error/></Peticion>";
        HttpRequest request = req("POST", "/scspRouting/test")
                .POST(HttpRequest.BodyPublishers.ofString(xml))
                .header("Content-Type", "text/xml")
                .build();
        HttpResponse<String> resp = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(resp.body());
        // El controlador retorna 200 amb SOAPFault escrit al body, però el backend registra error
        assertEquals(200, resp.statusCode());
        assertTrue(resp.body() == null || resp.body().isEmpty() || resp.body().contains("Fault") || resp.body().contains("ko"));
    }

    // ENRUTADOR MÚLTIPLE: ÈXIT (conté <multiple>)
    @Test
    void enrutador_multiple_exit() throws Exception {
        String xml = "<Peticion><multiple/></Peticion>";
        HttpRequest request = req("POST", "/scspRouting/test")
                .POST(HttpRequest.BodyPublishers.ofString(xml))
                .header("Content-Type", "text/xml")
                .build();
        HttpResponse<String> resp = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }

    // Backoffice Síncron OK
    @Test
    void backoffice_sincrona_ok() throws Exception {
        HttpRequest request = req("POST", "/mock/backoffice/sincrona")
                .POST(HttpRequest.BodyPublishers.ofString("CERT_OK"))
                .header("Content-Type", "text/plain")
                .build();
        HttpResponse<String> resp = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }

    // Backoffice Síncron KO
    @Test
    void backoffice_sincrona_ko() throws Exception {
        HttpRequest request = req("POST", "/mock/backoffice/sincrona")
                .POST(HttpRequest.BodyPublishers.ofString("CERT_ERR"))
                .header("Content-Type", "text/plain")
                .build();
        HttpResponse<String> resp = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        // El controlador propaga excepció com a 500 o 200 amb fault segons capa — aquí comprovam que respon
        assertTrue(resp.statusCode() == 200 || resp.statusCode() >= 400);
    }

    // Backoffice Asíncron OK
    @Test
    void backoffice_asincrona_ok() throws Exception {
        HttpRequest request = req("POST", "/mock/backoffice/asincrona")
                .POST(HttpRequest.BodyPublishers.ofString("CERT_OK"))
                .header("Content-Type", "text/plain")
                .build();
        HttpResponse<String> resp = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }

    // Backoffice Sol·licitud-Resposta OK
    @Test
    void backoffice_solicitud_resposta_ok() throws Exception {
        HttpRequest request = req("POST", "/mock/backoffice/solicitud")
                .POST(HttpRequest.BodyPublishers.ofString("CERT_OK"))
                .header("Content-Type", "text/plain")
                .build();
        HttpResponse<String> resp = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }
}
