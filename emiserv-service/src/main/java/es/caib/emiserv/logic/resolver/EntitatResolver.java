/**
 * 
 */
package es.caib.emiserv.logic.resolver;

import org.w3c.dom.Element;

/**
 * Interfície per a resoldre un codi d'entitat a partir
 * d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatResolver {

	public String resolve(Element firstElement) throws EntitatResolverException;

}
