/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

import java.io.Serializable;

/**
 * Definició de l'estructura Peticion per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Peticion implements Serializable {

	private static final long serialVersionUID = 1L;
	private Atributos atributos;
	private Solicitudes solicitudes;

	public Atributos getAtributos() {
		return this.atributos;
	}

	public Solicitudes getSolicitudes() {
		return this.solicitudes;
	}

	public void setAtributos(Atributos atributos) {
		this.atributos = atributos;
	}

	public void setSolicitudes(Solicitudes solicitudes) {
		this.solicitudes = solicitudes;
	}

}
