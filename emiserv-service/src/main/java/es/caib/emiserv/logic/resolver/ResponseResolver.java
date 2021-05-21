/**
 * 
 */
package es.caib.emiserv.logic.resolver;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

/**
 * Interfície per a discriminar la resposta correcta pel servei d'enrutament múltiple
 * a partir de diferents respostes a peticions SCSP.
 * Té un mètode que rep una llista de respostes i ha de retornar la clau de la entitat de la 
 * resposta escollida. S'ha de tenir en compte que l'ordre de la llista de les respostes
 * va segons la propietat d'ordre de les rutes. La primera resposta té més preferència que la darrera
 * en les mateixes condicions.
 * 
 */
public interface ResponseResolver {

	/** Rep un mapeig de codi d'emissor i resposta i retorna el codi de la resposta escollida
	 * que es correspon amb la entitat
	 * @param clausOrdenades Llistat de les claus ordenades segons la preferència en cas de
	 * coincidir la resposta.
	 * @param responses Mapeig de les diferents respostes amb les claus de les entitats.
	 * @return Retorna la resposta escollida.
	 * @throws ResponseResolverException
	 */
	public String resolve(
			List<String> clausOrdenades,
			Map<String, Document> responses) throws ResponseResolverException;

}
