/**
 * 
 */
package es.caib.emiserv.logic.resolver;

import org.w3c.dom.Element;

/**
 * Mediator de prova. Sempre retornara la mateixa entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SampleEntitatResolver implements EntitatResolver {

	@Override
	public String resolve(Element firstElement) {
		return "07033";
	}

}
