/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Permisos d'un objecte.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ObjecteAmbPermisosDto implements Serializable {

	private List<PermisDto> permisos;
	private boolean usuariActualRead;
	private boolean usuariActualWrite;
	private boolean usuariActualCreate;
	private boolean usuariActualDelete;
	private boolean usuariActualAdministration;

	public List<PermisDto> getPermisos() {
		return permisos;
	}
	public void setPermisos(List<PermisDto> permisos) {
		this.permisos = permisos;
	}
	public boolean isUsuariActualRead() {
		return usuariActualRead;
	}
	public void setUsuariActualRead(boolean usuariActualRead) {
		this.usuariActualRead = usuariActualRead;
	}
	public boolean isUsuariActualWrite() {
		return usuariActualWrite;
	}
	public void setUsuariActualWrite(boolean usuariActualWrite) {
		this.usuariActualWrite = usuariActualWrite;
	}
	public boolean isUsuariActualCreate() {
		return usuariActualCreate;
	}
	public void setUsuariActualCreate(boolean usuariActualCreate) {
		this.usuariActualCreate = usuariActualCreate;
	}
	public boolean isUsuariActualDelete() {
		return usuariActualDelete;
	}
	public void setUsuariActualDelete(boolean usuariActualDelete) {
		this.usuariActualDelete = usuariActualDelete;
	}
	public boolean isUsuariActualAdministration() {
		return usuariActualAdministration;
	}
	public void setUsuariActualAdministration(boolean usuariActualAdministration) {
		this.usuariActualAdministration = usuariActualAdministration;
	}

	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 1059960899293918922L;

}
