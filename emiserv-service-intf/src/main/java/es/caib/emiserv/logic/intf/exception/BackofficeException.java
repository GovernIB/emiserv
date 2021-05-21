/**
 * 
 */
package es.caib.emiserv.logic.intf.exception;

/**
 * Excepció que es llança quan es dona un error en els mètodes
 * que gestionen les peticions al backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class BackofficeException extends RuntimeException {

	public BackofficeException() {
		super();
	}

	public BackofficeException(Throwable cause) {
		super(cause);
	}

	public BackofficeException(String message) {
		super(message);
	}

	public BackofficeException(String message, Throwable cause) {
		super(message, cause);
	}

}
