/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

/**
 * Definició de l'estructura Respuesta per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Respuesta {

	private Atributos atributos;
	private Transmisiones transmisiones;

	public Atributos getAtributos() {
		return this.atributos;
	}

	public void setAtributos(Atributos atributos) {
		this.atributos = atributos;
	}

	public Transmisiones getTransmisiones() {
		return this.transmisiones;
	}

	public void setTransmisiones(Transmisiones transmisiones) {
		this.transmisiones = transmisiones;
	}

}
