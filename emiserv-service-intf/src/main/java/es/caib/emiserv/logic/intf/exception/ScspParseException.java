/**
 * 
 */
package es.caib.emiserv.logic.intf.exception;

/**
 * Excepció que es llança quan es troben errors al processar una petició
 * SCSP en el proxy.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ScspParseException extends RuntimeException {

	public ScspParseException() {
		super();
	}

	public ScspParseException(Throwable cause) {
		super(cause);
	}

	public ScspParseException(String message) {
		super(message);
	}

	public ScspParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
