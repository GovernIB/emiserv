/**
 *
 */
package es.caib.emiserv.client.comu;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Client amb la lògica bàsica per a accedir als serveis REST d'EMISERV (RESTEasy).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class ClientBase {

	private String urlBase;
	private Client restClient;
	private ObjectMapper mapper;

	protected ClientBase(String urlBase) {
		init(urlBase, null, null, null, null);
	}

	protected ClientBase(
			String urlBase,
			Integer timeoutConnect,
			Integer timeoutRead) {
		init(urlBase, null, null, timeoutConnect, timeoutRead);
	}

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya) {
		init(urlBase, usuari, contrasenya, null, null);
	}

	protected ClientBase(
			String urlBase,
			String usuari,
			String contrasenya,
			Integer timeoutConnect,
			Integer timeoutRead) {
		init(urlBase, usuari, contrasenya, timeoutConnect, timeoutRead);
	}

	protected <R> R restPeticioGet(
			String metode,
			Map<String, String> queryParams,
			Class<R> responseType) throws Exception {
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (url={}, queryParams={})", urlAmbMetode, queryParams);
		R response = getWebTarget(urlAmbMetode, queryParams).request(MediaType.APPLICATION_JSON).get(responseType);
		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP GET de EMISERV (url={}, body={})", urlAmbMetode, mapper.writeValueAsString(response));
		}
		return response;
	}

	protected <R> List<R> restPeticioGetList(
			String metode,
			Map<String, String> queryParams,
			Class<R> responseType) throws Exception {
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (url={}, queryParams={})", urlAmbMetode, queryParams);
		String json = getWebTarget(urlAmbMetode, queryParams).request(MediaType.APPLICATION_JSON).get(String.class);
		// Amb el tipus de la llista explícit: un GenericType<List<R>> perdria el tipus R (esborrat de tipus)
		// i els elements serien mapes en lloc d'objectes R.
		List<R> response = mapper.readValue(
				json,
				mapper.getTypeFactory().constructCollectionType(List.class, responseType));
		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP GET de EMISERV (url={}, body={})", urlAmbMetode, json);
		}
		return response;
	}

	protected String restPeticioGetString(
			String metode,
			Map<String, String> queryParams) throws Exception {
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (url={}, queryParams={})", urlAmbMetode, queryParams);
		String response = getWebTarget(urlAmbMetode, queryParams).request(MediaType.APPLICATION_JSON).get(String.class);
		log.debug("Rebuda resposta HTTP GET de EMISERV (url={}, body={})", urlAmbMetode, response);
		return response;
	}

	protected Response restPeticioGetResponse(
			String metode,
			Map<String, String> queryParams,
			MediaType acceptedMediaType) throws Exception {
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (url={}, queryParams={})", urlAmbMetode, queryParams);
		Response response = getWebTarget(urlAmbMetode, queryParams).request(MediaType.APPLICATION_JSON).accept(acceptedMediaType).get();
		log.debug("Rebuda resposta HTTP GET de EMISERV (url={}, status={})", urlAmbMetode, response.getStatus());
		return response;
	}

	protected <R> R restPeticioPost(
			String metode,
			Object request,
			Class<R> responseType,
			MediaType acceptedMediaType) throws Exception {
		String urlAmbMetode = getUrlAmbMetode(metode);
		if (log.isDebugEnabled()) {
			log.debug("Enviant petició HTTP POST a EMISERV (url={}, body={})", urlAmbMetode, mapper.writeValueAsString(request));
		}
		R response = restClient.target(urlAmbMetode).
				request(MediaType.APPLICATION_JSON).
				accept(acceptedMediaType).
				post(Entity.json(request)).
				readEntity(responseType);
		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP POST de EMISERV (url={}, body={})", urlAmbMetode, mapper.writeValueAsString(response));
		}
		return response;
	}

	protected String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("CET"));
		return sdf.format(date);
	}

	protected String dateTimeToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		sdf.setTimeZone(TimeZone.getTimeZone("CET"));
		return sdf.format(date);
	}

	/**
	 * Escriu al log (nivell INFO) cada petició i resposta HTTP, amb el cos de la resposta.
	 */
	public void enableLogginFilter() {
		restClient.register(new LoggingFilter());
	}

	private WebTarget getWebTarget(String url, Map<String, String> queryParams) {
		WebTarget webTarget = restClient.target(url);
		if (queryParams != null) {
			for (Map.Entry<String, String> param: queryParams.entrySet()) {
				webTarget = webTarget.queryParam(param.getKey(), param.getValue());
			}
		}
		return webTarget;
	}

	private String getUrlAmbMetode(String metode) {
		return urlBase + metode;
	}

	private void init(
			String urlBase,
			String usuari,
			String contrasenya,
			Integer timeoutConnect,
			Integer timeoutRead) {
		this.urlBase = urlBase;
		mapper = new ObjectMapper();
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Els camps nous que afegeixi el servidor no fan fallar els clients d'una versió anterior.
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder)ResteasyClientBuilder.newBuilder();
		// El proveïdor JSON (resteasy-jackson2-provider) fa servir aquest ObjectMapper
		clientBuilder.register(new ContextResolver<ObjectMapper>() {
			@Override
			public ObjectMapper getContext(Class<?> type) {
				return mapper;
			}
		});
		if (usuari != null && !usuari.isBlank()) {
			log.debug("Autenticant REST amb autenticació de tipus HTTP basic (usuari= {}, contrasenya=********)", usuari);
			clientBuilder.register(new BasicAuthFilter(usuari, contrasenya));
		}
		if (timeoutConnect != null) {
			clientBuilder.connectTimeout(timeoutConnect, TimeUnit.MILLISECONDS);
		}
		if (timeoutRead != null) {
			clientBuilder.readTimeout(timeoutRead, TimeUnit.MILLISECONDS);
		}
		restClient = clientBuilder.build();
	}

	/** Afegeix la capçalera d'autenticació HTTP basic a totes les peticions. */
	private static class BasicAuthFilter implements ClientRequestFilter {
		private final String authorization;
		BasicAuthFilter(String usuari, String contrasenya) {
			String credencials = usuari + ":" + (contrasenya != null ? contrasenya : "");
			this.authorization = "Basic " + Base64.getEncoder().encodeToString(credencials.getBytes(StandardCharsets.UTF_8));
		}
		@Override
		public void filter(ClientRequestContext requestContext) {
			requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, authorization);
		}
	}

	/** Registra les peticions i les respostes (amb el cos) al log. */
	private static class LoggingFilter implements ClientRequestFilter, ClientResponseFilter {
		@Override
		public void filter(ClientRequestContext requestContext) {
			log.info("Petició HTTP {} {}", requestContext.getMethod(), requestContext.getUri());
		}
		@Override
		public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
			String body = null;
			if (responseContext.hasEntity()) {
				byte[] bytes;
				try (InputStream entity = responseContext.getEntityStream()) {
					bytes = entity.readAllBytes();
				}
				body = new String(bytes, StandardCharsets.UTF_8);
				responseContext.setEntityStream(new ByteArrayInputStream(bytes));
			}
			log.info(
					"Resposta HTTP {} {}: {} {}",
					requestContext.getMethod(),
					requestContext.getUri(),
					responseContext.getStatus(),
					body != null ? body : "");
		}
	}

}
