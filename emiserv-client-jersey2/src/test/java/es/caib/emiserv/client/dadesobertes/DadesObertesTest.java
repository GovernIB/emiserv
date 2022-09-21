/**
 * 
 */
package es.caib.emiserv.client.dadesobertes;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test de les estadístiques de dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Disabled
public class DadesObertesTest {

	private static final String URL_BASE = "http://localhost:8080/emiservapi";

	@Test
	public void opendata() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, 1);
		List<DadesObertesRespostaConsulta> resposta = getClient().opendata(
				null,
				cal.getTime(),
				new Date(),
				null,
				null,
				null);
		assertNotNull(resposta);
		System.out.println("-> opendata: " + resposta.size());
		for (DadesObertesRespostaConsulta item: resposta.subList(0, 5)) {
			System.out.println("    - " + objectToJsonString(item));
		}
	}

	private String objectToJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsString(obj);
	}

	private ClientDadesObertes getClient() {
		ClientDadesObertes client = new ClientDadesObertes(
				URL_BASE,
				null,
				null);
		client.enableLogginFilter();
		return client;
	}

}
