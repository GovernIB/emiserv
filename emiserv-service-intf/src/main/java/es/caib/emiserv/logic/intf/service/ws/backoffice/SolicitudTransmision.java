/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

/**
 * Definició de l'estructura SolicitudTransmision per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SolicitudTransmision {

	private DatosGenericos datosGenericos;
	private Object datosEspecificos;
	private String id;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getDatosEspecificos() {
		return this.datosEspecificos;
	}

	public DatosGenericos getDatosGenericos() {
		return this.datosGenericos;
	}

	public void setDatosEspecificos(Object datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

	public void setDatosGenericos(DatosGenericos datosGenericos) {
		this.datosGenericos = datosGenericos;
	}

}
