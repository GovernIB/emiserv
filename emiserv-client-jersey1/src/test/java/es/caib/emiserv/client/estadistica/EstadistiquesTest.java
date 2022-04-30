/**
 * 
 */
package es.caib.emiserv.client.estadistica;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.caib.emiserv.client.comu.Entitat;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Test de les estadístiques de càrrega.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@Ignore
public class EstadistiquesTest {

	private static final String URL_BASE = "http://localhost:8080/emiservapi";
	private static final String USUARI = "u999001";
	private static final String CONTRASENYA = "u999001";

	private static final String ENTITAT_NIF = "12345678Z";

	private ClientEstadistica client = new ClientEstadistica(URL_BASE, USUARI, CONTRASENYA, null, null);

	@Test
	public void consultes() throws IOException {
		client.enableLogginFilter();
		Entitat resposta = client.consultes(
				ENTITAT_NIF,
				null,
				null,
				null,
				null,
				null,
				null);
		assertNotNull(resposta);
		System.out.println("-> consultes: " + objectToJsonString(resposta));
	}

	@Test
	public void carrega() throws IOException {
		client.enableLogginFilter();
		List<Entitat> resposta = client.carrega();
		assertNotNull(resposta);
		System.out.println("-> carrega: " + objectToJsonString(resposta));
	}

	private String objectToJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsString(obj);
	}

}
