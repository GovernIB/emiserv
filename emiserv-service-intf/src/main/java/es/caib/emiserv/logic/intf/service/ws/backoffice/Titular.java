/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

/**
 * Definició de l'estructura Titular per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Titular {

	private TipoDocumentacion tipoDocumentacion;
	private String documentacion;
	private String nombreCompleto;
	private String nombre;
	private String apellido1;
	private String apellido2;

	public String getApellido1() {
		return this.apellido1;
	}

	public String getApellido2() {
		return this.apellido2;
	}

	public String getDocumentacion() {
		return this.documentacion;
	}

	public String getNombre() {
		return this.nombre;
	}

	public String getNombreCompleto() {
		return this.nombreCompleto;
	}

	public TipoDocumentacion getTipoDocumentacion() {
		return this.tipoDocumentacion;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public void setDocumentacion(String documentacion) {
		this.documentacion = documentacion;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public void setTipoDocumentacion(TipoDocumentacion tipoDocumentacion) {
		this.tipoDocumentacion = tipoDocumentacion;
	}

}
