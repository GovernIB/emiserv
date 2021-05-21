/**
 * 
 */
package es.caib.emiserv.logic.resolver;

/**
 * Excepció que es llança quan no es pot discriminar la resposta correcta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ResponseResolverException extends RuntimeException {

	public ResponseResolverException() {
		super();
	}

	public ResponseResolverException(Throwable cause) {
		super(cause);
	}

	public ResponseResolverException(String message) {
		super(message);
	}

	public ResponseResolverException(String message, Throwable cause) {
		super(message, cause);
	}

}
