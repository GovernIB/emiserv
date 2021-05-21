/**
 * 
 */
package es.caib.emiserv.logic.resolver;

/**
 * Excepció que es llança quan no es pot resoldre l'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class EntitatResolverException extends RuntimeException {

	public EntitatResolverException() {
		super();
	}

	public EntitatResolverException(Throwable cause) {
		super(cause);
	}

	public EntitatResolverException(String message) {
		super(message);
	}

	public EntitatResolverException(String message, Throwable cause) {
		super(message, cause);
	}

}
