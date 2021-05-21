/**
 * 
 */
package es.caib.emiserv.logic.intf.exception;

/**
 * Excepció que es llança quan falla alguna de les comprovacions
 * al executar un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ValidationException extends RuntimeException {

	public ValidationException() {
		super();
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}
