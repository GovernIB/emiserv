/**
 * 
 */
package es.caib.emiserv.logic.intf.exception;

/**
 * Excepció que es llança quan es dona un error a un plugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SistemaExternException extends RuntimeException {

	public SistemaExternException() {
		super();
	}

	public SistemaExternException(Throwable cause) {
		super(cause);
	}

	public SistemaExternException(String message) {
		super(message);
	}

	public SistemaExternException(String message, Throwable cause) {
		super(message, cause);
	}

}
