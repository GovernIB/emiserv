/**
 * 
 */
package es.caib.emiserv.logic.intf.exception;

/**
 * Excepció que es llança quan l'objecte especificat no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotActiveException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	
	public NotActiveException(
			Object objectId,
			Class<?> objectClass) {
		super();
		this.objectId = objectId;
		this.objectClass = objectClass;
	}

	public Object getObjectId() {
		return objectId;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}

	public String getObjectInfo() {
		StringBuilder sb = new StringBuilder();
		if (objectClass != null)
			sb.append(objectClass.getClass().getName());
		else
			sb.append("null");
		sb.append("#");
		if (objectId != null)
			sb.append(objectId.toString());
		else
			sb.append("null");
		return sb.toString();
	}

}
