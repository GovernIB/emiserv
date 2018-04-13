/**
 * 
 */
package es.caib.emiserv.core.security;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

/**
 * Permisos addicionals pel suport d'ACLs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtendedPermission extends BasePermission {

	private static final long serialVersionUID = 1L;

	public static final Permission READ = new ExtendedPermission(1 << 0, 'R'); // 1
	public static final Permission WRITE = new ExtendedPermission(1 << 1, 'W'); // 2
	public static final Permission CREATE = new ExtendedPermission(1 << 2, 'C'); // 4
	public static final Permission DELETE = new ExtendedPermission(1 << 3, 'D'); // 8
	public static final Permission ADMINISTRATION = new ExtendedPermission(1 << 4, 'A'); // 16

	protected ExtendedPermission(int mask) {
		super(mask);
	}
	protected ExtendedPermission(int mask, char code) {
		super(mask, code);
	}

}
