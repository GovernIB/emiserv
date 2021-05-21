/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

/**
 * Definició de l'estructura Emisor per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Emisor {

	private String nifEmisor;
	private String nombreEmisor;

	public String getNifEmisor() {
		return this.nifEmisor;
	}

	public String getNombreEmisor() {
		return this.nombreEmisor;
	}

	public void setNifEmisor(String nifEmisor) {
		this.nifEmisor = nifEmisor;
	}

	public void setNombreEmisor(String nombreEmisor) {
		this.nombreEmisor = nombreEmisor;
	}

}
