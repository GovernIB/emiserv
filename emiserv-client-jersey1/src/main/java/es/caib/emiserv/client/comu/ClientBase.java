/**
 * 
 */
package es.caib.emiserv.client.comu;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
			Class<R> responseType) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				requestParams.add(key, queryParams.get(key));
			}
		}
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (" +
				"url=" + urlAmbMetode + ", " +
				"queryParams=" + queryParams + ")");
		R response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(responseType);
		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP GET de EMISERV (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	protected <R> List<R> restPeticioGetList(
			String metode,
			Map<String, String> queryParams,
			Class<R> responseType) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				requestParams.add(key, queryParams.get(key));
			}
		}
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (" +
				"url=" + urlAmbMetode + ", " +
				"queryParams=" + queryParams + ")");
		List<R> response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(new GenericType<List<R>>(){});
		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP GET de EMISERV (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	protected String restPeticioGetString(
			String metode,
			Map<String, String> queryParams) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				requestParams.add(key, queryParams.get(key));
			}
		}
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (" +
				"url=" + urlAmbMetode + ", " +
				"queryParams=" + queryParams + ")");
		String response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				get(String.class);
		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP GET de EMISERV (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	protected ClientResponse restPeticioGetResponse(
			String metode,
			Map<String, String> queryParams,
			MediaType acceptedMediaType) throws UniformInterfaceException, ClientHandlerException, IOException {
		MultivaluedMap<String, String> requestParams = new MultivaluedMapImpl();
		if (queryParams != null) {
			for (String key: queryParams.keySet()) {
				requestParams.add(key, queryParams.get(key));
			}
		}
		String urlAmbMetode = getUrlAmbMetode(metode);
		log.debug("Enviant petició HTTP GET a EMISERV (" +
				"url=" + urlAmbMetode + ", " +
				"queryParams=" + queryParams + ")");
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				queryParams(requestParams).
				type("application/json").
				accept(acceptedMediaType).
				get(ClientResponse.class);
		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP GET de EMISERV (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
		}
		return response;
	}

	protected <R> R restPeticioPost(
			String metode,
			Object request,
			Class<R> responseType,
			MediaType acceptedMediaType) throws UniformInterfaceException, ClientHandlerException, IOException {
		String urlAmbMetode = getUrlAmbMetode(metode);
		String body = mapper.writeValueAsString(request);
		log.debug("Enviant petició HTTP POST a EMISERV (" +
				"url=" + urlAmbMetode + ", " +
				"body=" + body + ")");
		R response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				accept(acceptedMediaType).
				post(responseType, body);
		if (log.isDebugEnabled()) {
			log.debug("Rebuda resposta HTTP POST de EMISERV (" +
					"url=" + urlAmbMetode + ", " +
					"body=" + mapper.writeValueAsString(response) + ")");
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
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses().add(JacksonJsonProvider.class);
		jerseyClient = Client.create(clientConfig);
		if (timeoutConnect != null) {
			jerseyClient.setConnectTimeout(timeoutConnect);
		}
		if (timeoutRead != null) {
			jerseyClient.setReadTimeout(timeoutRead);
		}
		jerseyClient.addFilter(
				new ClientFilter() {
					private ArrayList<Object> cookies;
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						if (cookies != null) {
							request.getHeaders().put("Cookie", cookies);
						}
						ClientResponse response = getNext().handle(request);
						if (response.getCookies() != null) {
							if (cookies == null) {
								cookies = new ArrayList<Object>();
							}
							cookies.addAll(response.getCookies());
						}
						return response;
					}
				}
		);
		jerseyClient.addFilter(
				new ClientFilter() {
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						ClientHandler ch = getNext();
				        ClientResponse resp = ch.handle(request);
				        if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
				            return resp;
				        } else {
				            String redirectTarget = resp.getHeaders().getFirst("Location");
				            request.setURI(UriBuilder.fromUri(redirectTarget).build());
				            return ch.handle(request);
				        }
					}
				}
		);
		if (usuari != null && !usuari.isEmpty()) {
			log.debug(
					"Autenticant REST amb autenticació de tipus HTTP basic (" +
							"usuari=" + usuari + ", " +
							"contrasenya=********)");
			jerseyClient.addFilter(new HTTPBasicAuthFilter(usuari, contrasenya));
		}
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
		jerseyClient.addFilter(new LoggingFilter(System.out));
	}

}
