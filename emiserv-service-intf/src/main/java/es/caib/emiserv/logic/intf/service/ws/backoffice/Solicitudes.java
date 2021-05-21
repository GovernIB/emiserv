/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

import java.util.ArrayList;

/**
 * Definició de l'estructura Solicitudes per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Solicitudes {

	private ArrayList<SolicitudTransmision> solicitudTransmision;

	public ArrayList<SolicitudTransmision> getSolicitudTransmision() {
		return this.solicitudTransmision;
	}

	public void setSolicitudTransmision(ArrayList<SolicitudTransmision> solicitudTransmision) {
		this.solicitudTransmision = solicitudTransmision;
	}

}
