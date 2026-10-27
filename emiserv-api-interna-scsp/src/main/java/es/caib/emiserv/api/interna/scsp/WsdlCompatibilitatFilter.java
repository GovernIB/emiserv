package es.caib.emiserv.api.interna.scsp;

import org.slf4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manté les URLs de consulta del WSDL de l'emisor SCSP amb Axis2 (llibreries SCSP 4):
 *
 * - GET .../services/peticionSincrona?wsdl (o qualsevol altra URL acabada en ?wsdl): retorna el
 *   WSDL de l'emisor SCSP 5 (esquemas/service.wsdl) amb l'adreça del servei igual a la URL
 *   consultada.
 * - GET ...?xsd=peticion.xsd: retorna els esquemes importats pel WSDL (esquemas/*.xsd).
 *
 * Les referències schemaLocation es reescriuen perquè apuntin a la mateixa URL amb ?xsd=, de
 * manera que el WSDL es pot resoldre complet des de qualsevol URL del servei.
 *
 * També normalitza les URLs del servei que acceptava Axis2 i que l'emisor SCSP 5 rebutja (error
 * 0901), ja que aquest tria l'operació segons el final de la URL (peticionSincrona | peticionAsincrona):
 *
 * - Adreces dels endpoints del WSDL d'Axis2: .../services/peticionSincrona.peticionSincronaHttpSoap11Endpoint/
 * - URLs acabades en barra: .../services/peticionSincrona/
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class WsdlCompatibilitatFilter implements Filter {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WsdlCompatibilitatFilter.class);

    private static final String ESQUEMES_PATH = "esquemas/";
    private static final String WSDL_RESOURCE = ESQUEMES_PATH + "service.wsdl";
    private static final Pattern XSD_NAME_PATTERN = Pattern.compile("[A-Za-z0-9_\\-]+(\\.[A-Za-z0-9_\\-]+)*\\.xsd");
    private static final Pattern SCHEMA_LOCATION_PATTERN = Pattern.compile("schemaLocation=\"([^\"]+)\"");
    private static final Pattern SOAP_ADDRESS_PATTERN = Pattern.compile("(<soap:address\\s+location=\")[^\"]*(\")");
    // Sufix dels endpoints d'Axis2 (p.ex. .peticionSincronaHttpSoap11Endpoint, .peticionAsincronaHttpsSoap12Endpoint) i barres finals
    private static final Pattern AXIS2_ENDPOINT_PATTERN = Pattern.compile("(\\.[A-Za-z0-9_]+Https?Soap1[12]Endpoint)?/*$");
    private static final Pattern OPERACIO_PATTERN = Pattern.compile(".*/(peticionSincrona|peticionAsincrona)$");

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = normalitzarUrl((HttpServletRequest) request);
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if ("GET".equalsIgnoreCase(httpRequest.getMethod()) && httpRequest.getQueryString() != null) {
            String queryString = httpRequest.getQueryString();
            if ("wsdl".equalsIgnoreCase(queryString)) {
                sendWsdl(httpRequest, httpResponse);
                return;
            }
            String xsd = httpRequest.getParameter("xsd");
            if (xsd != null) {
                sendXsd(xsd, httpRequest, httpResponse);
                return;
            }
        }
        chain.doFilter(httpRequest, response);
    }

    @Override
    public void destroy() {
    }

    /**
     * Si la URL és una URL d'Axis2 d'una operació de l'emisor, retorna la petició amb la URL
     * normalitzada (.../peticionSincrona o .../peticionAsincrona). Si no, retorna la mateixa petició.
     */
    static HttpServletRequest normalitzarUrl(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (requestUri == null) {
            return request;
        }
        String uriNormalitzada = normalitzarPath(requestUri);
        if (uriNormalitzada.equals(requestUri)) {
            return request;
        }
        log.debug("Es normalitza la URL del servei {} a {}", requestUri, uriNormalitzada);
        String requestUrl = request.getRequestURL().toString();
        String urlNormalitzada = requestUrl.substring(0, requestUrl.length() - requestUri.length()) + uriNormalitzada;
        String pathInfo = request.getPathInfo();
        String pathInfoNormalitzat = pathInfo != null ? normalitzarPath(pathInfo) : null;
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getRequestURI() {
                return uriNormalitzada;
            }
            @Override
            public StringBuffer getRequestURL() {
                return new StringBuffer(urlNormalitzada);
            }
            @Override
            public String getPathInfo() {
                return pathInfoNormalitzat;
            }
        };
    }

    static String normalitzarPath(String path) {
        String normalitzat = AXIS2_ENDPOINT_PATTERN.matcher(path).replaceFirst("");
        return OPERACIO_PATTERN.matcher(normalitzat).matches() ? normalitzat : path;
    }

    private void sendWsdl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String serviceUrl = request.getRequestURL().toString();
        String wsdl = readResource(WSDL_RESOURCE);
        if (wsdl == null) {
            log.error("No s'ha trobat el WSDL de l'emisor SCSP ({})", WSDL_RESOURCE);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        wsdl = SOAP_ADDRESS_PATTERN.matcher(wsdl).replaceAll("$1" + Matcher.quoteReplacement(serviceUrl) + "$2");
        sendXml(rewriteSchemaLocations(wsdl, serviceUrl), response);
    }

    private void sendXsd(String xsd, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String xsdContent = XSD_NAME_PATTERN.matcher(xsd).matches() ? readResource(ESQUEMES_PATH + xsd) : null;
        if (xsdContent == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        sendXml(rewriteSchemaLocations(xsdContent, request.getRequestURL().toString()), response);
    }

    private String rewriteSchemaLocations(String xml, String serviceUrl) {
        Matcher matcher = SCHEMA_LOCATION_PATTERN.matcher(xml);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String schemaLocation = matcher.group(1);
            String replacement = matcher.group(0);
            if (XSD_NAME_PATTERN.matcher(schemaLocation).matches()) {
                replacement = "schemaLocation=\"" + serviceUrl + "?xsd=" + schemaLocation + "\"";
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private void sendXml(String xml, HttpServletResponse response) throws IOException {
        byte[] content = xml.getBytes(StandardCharsets.UTF_8);
        response.setContentType("text/xml;charset=UTF-8");
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
    }

    private String readResource(String resource) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = WsdlCompatibilitatFilter.class.getClassLoader();
        }
        try (InputStream is = classLoader.getResourceAsStream(resource)) {
            if (is == null) return null;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

}
