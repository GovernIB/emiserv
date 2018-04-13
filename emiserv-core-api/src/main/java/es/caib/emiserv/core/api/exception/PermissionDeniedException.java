/**
 * 
 */
package es.caib.emiserv.core.api.exception;

/**
 * Excepció que es llança quan l'usuari no te permisos per accedir a un objecte.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PermissionDeniedException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	private String userName;
	private String permissionName;
	
	public PermissionDeniedException(
			Object objectId,
			Class<?> objectClass,
			String userName,
			String permissionName) {
		super();
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.userName = userName;
		this.permissionName = permissionName;
	}

	public Object getObjectId() {
		return objectId;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}
	public String getUserName() {
		return userName;
	}
	public String getPermissionName() {
		return permissionName;
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
