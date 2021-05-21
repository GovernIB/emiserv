/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

import java.util.ArrayList;

/**
 * Definició de l'estructura Transmisiones per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Transmisiones {

	private ArrayList<TransmisionDatos> transmisionDatos;

	public ArrayList<TransmisionDatos> getTransmisionDatos() {
		return this.transmisionDatos;
	}

	public void setTransmisionDatos(ArrayList<TransmisionDatos> transmisionDatos) {
		this.transmisionDatos = transmisionDatos;
	}

}
