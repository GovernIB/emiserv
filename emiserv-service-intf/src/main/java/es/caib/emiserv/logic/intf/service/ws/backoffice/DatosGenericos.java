/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

/**
 * Definició de l'estructura DatosGenericos per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DatosGenericos {

	private Emisor emisor;
	private Solicitante solicitante;
	private Titular titular;
	private Transmision transmision;

	public Emisor getEmisor() {
		return this.emisor;
	}

	public Solicitante getSolicitante() {
		return this.solicitante;
	}

	public Titular getTitular() {
		return this.titular;
	}

	public Transmision getTransmision() {
		return this.transmision;
	}

	public void setEmisor(Emisor emisor) {
		this.emisor = emisor;
	}

	public void setSolicitante(Solicitante solicitante) {
		this.solicitante = solicitante;
	}

	public void setTitular(Titular titular) {
		this.titular = titular;
	}

	public void setTransmision(Transmision transmision) {
		this.transmision = transmision;
	}

}
