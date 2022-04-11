/**
 * 
 */
package es.caib.emiserv.client.comu;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Client amb la lògica bàsica per a accedir al servei de consulta
 * d'estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class ClientBase {

	private String urlBase;
	private Client jerseyClient;
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
		WebTarget webTarget = jerseyClient.target(urlAmbMetode);
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				webTarget.queryParam(key, queryParams.get(key));
			}
		}
		R response = webTarget.request(MediaType.APPLICATION_JSON).get(responseType);

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
		WebTarget webTarget = jerseyClient.target(urlAmbMetode);
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				webTarget.queryParam(key, queryParams.get(key));
			}
		}
		List<R> response = webTarget
				.request(MediaType.APPLICATION_JSON)
				.get(Response.class)
				.readEntity(new GenericType<List<R>>() {});

		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP GET de EMISERV (url={}, body={})", urlAmbMetode, mapper.writeValueAsString(response));
		}
		return response;
	}

	protected String restPeticioGetString(
			String metode,
			Map<String, String> queryParams) throws Exception {
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (url={}, queryParams={})", urlAmbMetode, queryParams);
		WebTarget webTarget = jerseyClient.target(urlAmbMetode);
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				webTarget.queryParam(key, queryParams.get(key));
			}
		}
		String response = webTarget.request(MediaType.APPLICATION_JSON).get(String.class);

		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP GET de EMISERV (url={}, body={})", urlAmbMetode, mapper.writeValueAsString(response));
		}
		return response;
	}

	protected Response restPeticioGetResponse(
			String metode,
			Map<String, String> queryParams,
			MediaType acceptedMediaType) throws Exception {
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (url={}, queryParams={})", urlAmbMetode, queryParams);
		WebTarget webTarget = jerseyClient.target(urlAmbMetode);
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				webTarget.queryParam(key, queryParams.get(key));
			}
		}
		Response response = webTarget.request(MediaType.APPLICATION_JSON).accept(acceptedMediaType).get(Response.class);

		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP GET de EMISERV (url={}, body={})", urlAmbMetode, mapper.writeValueAsString(response));
		}
		return response;
	}

	protected <R> R restPeticioPost(
			String metode,
			Object request,
			Class<R> responseType,
			MediaType acceptedMediaType) throws Exception {
		String urlAmbMetode = getUrlAmbMetode(metode);
		String body = mapper.writeValueAsString(request);
		log.debug("Enviant petició HTTP GET a EMISERV (url={}, body={})", urlAmbMetode, body);
		WebTarget webTarget = jerseyClient.target(urlAmbMetode);
		R response = webTarget
				.request(MediaType.APPLICATION_JSON)
				.accept(acceptedMediaType)
				.post(Entity.json(request)).readEntity(responseType);

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
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(JacksonFeature.class);
		if (usuari != null && !usuari.isBlank()) {
			log.debug("Autenticant REST amb autenticació de tipus HTTP basic (usuari= {}, contrasenya=********)", usuari);
			clientConfig.register(HttpAuthenticationFeature.basic(usuari, contrasenya));
		}
		ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(clientConfig);

		if (timeoutConnect != null) {
			clientBuilder.connectTimeout(timeoutConnect, TimeUnit.MILLISECONDS);
		}
		if (timeoutRead != null) {
			clientBuilder.readTimeout(timeoutRead, TimeUnit.MILLISECONDS);
		}
		jerseyClient = clientBuilder.build();

		mapper = new ObjectMapper();
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	public void enableLogginFilter() {
		jerseyClient.register(LoggingFeature.class);
		jerseyClient.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
		jerseyClient.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_CLIENT, "DEBUG");
	}

}
