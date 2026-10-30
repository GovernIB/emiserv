/**
 * 
 */
package es.caib.emiserv.client.informe;

import es.caib.emiserv.client.comu.ClientBase;
import es.caib.emiserv.client.comu.Entitat;
import es.caib.emiserv.client.comu.ServeiTipus;

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
public class ClientInforme extends ClientBase {

	private static final String BASE_URL_SUFIX = "/interna/reports/";

	public ClientInforme(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya);
	}

	public ClientInforme(
			String urlBase,
			String usuari,
			String contrasenya,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, timeoutConnect, timeoutRead);
	}

	public List<Entitat> general(
			Date dataInici,
			Date dataFi,
			ServeiTipus tipus) throws Exception {
		Map<String, String> params = new HashMap<>();
		if (dataInici != null) {
			params.put("dataInici", dateToString(dataInici));
		}
		if (dataFi != null) {
			params.put("dataFi", dateToString(dataFi));
		}
		if (tipus != null) {
			params.put("tipus", tipus.name());
		}
		return restPeticioGetList(
				"general",
				params,
				Entitat.class);
	}

}
