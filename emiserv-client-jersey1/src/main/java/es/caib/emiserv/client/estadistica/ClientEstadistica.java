/**
 * 
 */
package es.caib.emiserv.client.estadistica;

import es.caib.emiserv.client.comu.ClientBase;
import es.caib.emiserv.client.comu.Entitat;
import es.caib.emiserv.client.comu.EstatTipus;

import java.io.IOException;
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
public class ClientEstadistica extends ClientBase {

	private static final String BASE_URL_SUFIX = "api/interna/stats/";

	public ClientEstadistica(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya);
	}

	public ClientEstadistica(
			String urlBase,
			String usuari,
			String contrasenya,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, timeoutConnect, timeoutRead);
	}

	public Entitat consultes(
			String entitatNif,
			String procedimentCodi,
			String serveiCodi,
			EstatTipus estat,
			Date dataInici,
			Date dataFi,
			EstatTipus tipus) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		if (entitatNif != null) {
			params.put("entitatNif", entitatNif);
		}
		if (procedimentCodi != null) {
			params.put("procedimentCodi", procedimentCodi);
		}
		if (serveiCodi != null) {
			params.put("serveiCodi", serveiCodi);
		}
		if (estat != null) {
			params.put("estat", estat.name());
		}
		if (dataInici != null) {
			params.put("dataInici", dateToString(dataInici));
		}
		if (dataFi != null) {
			params.put("dataFi", dateToString(dataFi));
		}
		if (tipus != null) {
			params.put("tipus", tipus.name());
		}
		return restPeticioGet(
				"consultes",
				params,
				Entitat.class);
	}

	public List<Entitat> carrega() throws IOException {
		return restPeticioGetList(
				"carrega",
				null,
				Entitat.class);
	}

}
