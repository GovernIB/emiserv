/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

/**
 * Definició de l'estructura TransmisionDatos per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TransmisionDatos {

	private DatosGenericos datosGenericos;
	private Object datosEspecificos;
	private String id;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DatosGenericos getDatosGenericos() {
		return this.datosGenericos;
	}

	public void setDatosGenericos(DatosGenericos datosGenericos) {
		this.datosGenericos = datosGenericos;
	}

	public Object getDatosEspecificos() {
		return this.datosEspecificos;
	}

	public void setDatosEspecificos(Object datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

}