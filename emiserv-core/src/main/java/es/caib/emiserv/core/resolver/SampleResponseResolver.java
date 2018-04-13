/**
 * 
 */
package es.caib.emiserv.core.resolver;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

/**
 * Mediator de prova. Sempre retornara la mateixa entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SampleResponseResolver implements ResponseResolver {

	@Override
	public String resolve(
			List<String> clausOrdenades,
			Map<String, Document> responses) {
		if (responses != null && responses.size() > 0)
			// retorna la primera resposta
			return responses.keySet().iterator().next();
		else
			return null;
	}

}
