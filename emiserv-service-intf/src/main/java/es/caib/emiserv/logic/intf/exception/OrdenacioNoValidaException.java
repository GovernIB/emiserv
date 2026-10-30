package es.caib.emiserv.logic.intf.exception;

/**
 * Excepció que es llança quan es demana ordenar per un camp que no es pot ordenar (camp
 * desconegut o calculat). L'API REST la torna com a error 400.
 *
 * @author Limit Tecnologies
 */
public class OrdenacioNoValidaException extends RuntimeException {

	private final String camp;

	public OrdenacioNoValidaException(String camp, java.util.Collection<String> campsOrdenables) {
		super("No es pot ordenar pel camp '" + camp + "' (camps permesos: " + String.join(", ", campsOrdenables) + ")");
		this.camp = camp;
	}

	public String getCamp() {
		return camp;
	}

	private static final long serialVersionUID = 1L;

}
