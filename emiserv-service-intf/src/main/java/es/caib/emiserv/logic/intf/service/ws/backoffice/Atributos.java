/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

/**
 * Definició de l'estructura Atributos per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Atributos {

	private String idPeticion;
	private String numElementos;
	private String timeStamp;
	private String codigoCertificado;
	private Estado estado;

	public String getCodigoCertificado() {
		return this.codigoCertificado;
	}

	public Estado getEstado() {
		return this.estado;
	}

	public String getIdPeticion() {
		return this.idPeticion;
	}

	public String getNumElementos() {
		return this.numElementos;
	}

	public String getTimeStamp() {
		return this.timeStamp;
	}

	public void setCodigoCertificado(String codigoCertificado) {
		this.codigoCertificado = codigoCertificado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public void setIdPeticion(String idPeticion) {
		this.idPeticion = idPeticion;
	}

	public void setNumElementos(String numElementos) {
		this.numElementos = numElementos;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

}
