package es.caib.emiserv.logic.intf.base.exception;

import lombok.Getter;

/**
 * Excepció que es llança quan s'ha intentat sincronitzar un recurs però no s'ha
 * pogut per algun motiu.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class ResourceNotSyncedException extends RuntimeException {

	private final Class<?> clazz;
	private final String reason;

	public ResourceNotSyncedException(Class<?> clazz, String reason) {
		super("Couldn't sync resource " + getResourceId(clazz) + ": " + reason);
		this.clazz = clazz;
		this.reason = reason;
	}

	public ResourceNotSyncedException(Class<?> clazz, String reason, Throwable t) {
		super("Couldn't sync resource " + getResourceId(clazz) + ": " + reason, t);
		this.clazz = clazz;
		this.reason = reason;
	}

	private static String getResourceId(Class<?> clazz) {
		return "(class=" + clazz.getName() + ")";
	}

}
