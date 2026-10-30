/**
 * 
 */
package es.caib.emiserv.client.dadesobertes;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test de les estadístiques de dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Ignore
public class DadesObertesV2Test {

	private static final String URL_BASE = "http://localhost:8080/emiservapi";

	@Test
	public void opendata() throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, 1);
		DadesObertesResposta resposta = getClient().opendata(
				null,
				cal.getTime(),
				new Date(),
				null,
				null,
				null,
				null,
				null);
		assertNotNull(resposta);
		assertNotNull(resposta.getTotalElements());
		assertNotNull(resposta.getPaginaActual());
		assertNotNull(resposta.getTotalPagines());
		assertEquals(1, resposta.getPaginaActual().intValue());
		System.out.println("-> opendata: " + resposta.getDades().size());
		for (DadesObertesRespostaConsulta item: resposta.getDades().subList(0, 5)) {
			System.out.println("    - " + objectToJsonString(item));
		}
	}

	@Test
	public void opendataPaging() throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, 1);
		DadesObertesResposta resposta = getClient().opendata(
				null,
				cal.getTime(),
				new Date(),
				null,
				null,
				null,
				2,
				5);
		assertNotNull(resposta);
		assertNotNull(resposta.getTotalElements());
		assertNotNull(resposta.getPaginaActual());
		assertNotNull(resposta.getTotalPagines());
		assertNotNull(resposta.getProperaPagina());
		assertEquals(3, resposta.getPaginaActual().intValue());
		assertEquals(5, resposta.getDades().size());
		System.out.println("-> opendata: " + resposta.getDades().size());
		for (DadesObertesRespostaConsulta item: resposta.getDades().subList(0, 5)) {
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

	private ClientDadesObertesV2 getClient() {
		ClientDadesObertesV2 client = new ClientDadesObertesV2(
				URL_BASE,
				null,
				null);
		client.enableLogginFilter();
		return client;
	}

}
