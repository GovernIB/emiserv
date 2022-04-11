/**
 * 
 */
package es.caib.emiserv.client.dadesobertes;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.emiserv.client.comu.ClientBase;
import es.caib.emiserv.client.comu.ServeiTipus;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client amb la lògica bàsica per a accedir al servei de consulta
 * d'estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientDadesObertes extends ClientBase {

	private static final String BASE_URL_SUFIX = "/api/externa/";

	public ClientDadesObertes(String urlBase) {
		super(urlBase + BASE_URL_SUFIX);
	}

	public ClientDadesObertes(
			String urlBase,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase + BASE_URL_SUFIX, timeoutConnect, timeoutRead);
	}

	public List<DadesObertesRespostaConsulta> opendata(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi,
			ServeiTipus tipus) throws IOException {
		Map<String, String> params = new HashMap<>();
		if (entitatCodi != null) {
			params.put("entitatCodi", entitatCodi);
		}
		if (dataInici != null) {
			params.put("dataInici", dateToString(dataInici));
		}
		if (dataFi != null) {
			params.put("dataFi", dateToString(dataFi));
		}
		if (procedimentCodi != null) {
			params.put("procedimentCodi", procedimentCodi);
		}
		if (serveiCodi != null) {
			params.put("serveiCodi", serveiCodi);
		}
		if (tipus != null) {
			params.put("tipus", tipus.name());
		}
		String json = restPeticioGetString("opendata", params);
		return Arrays.asList(
				new ObjectMapper().readValue(
						json,
						DadesObertesRespostaConsulta[].class));
	}

}
