/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

/**
 * Definició de l'estructura SolicitudRespuesta per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SolicitudRespuesta {

	private Atributos atributos;

	public Atributos getAtributos() {
		return this.atributos;
	}

	public void setAtributos(Atributos atributos) {
		this.atributos = atributos;
	}

}
