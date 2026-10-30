/**
 * 
 */
package es.caib.emiserv.client.informe;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.caib.emiserv.client.comu.Entitat;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Test de les estadístiques de càrrega.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Ignore
public class InformeTest {

	private static final String URL_BASE = "http://localhost:8080/emiservapi";
	private static final String USUARI = "u999001";
	private static final String CONTRASENYA = "u999001";

	private ClientInforme client = new ClientInforme(URL_BASE, USUARI, CONTRASENYA, null, null);

	@Test
	public void general() throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date dataFi = cal.getTime();
		cal.add(Calendar.MONTH, -6);
		Date dataInici = cal.getTime();

		List<Entitat> resposta = client.general(dataInici, dataFi, null);
		assertNotNull(resposta);
		System.out.println("-> entitats: " + objectToJsonString(resposta));
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
