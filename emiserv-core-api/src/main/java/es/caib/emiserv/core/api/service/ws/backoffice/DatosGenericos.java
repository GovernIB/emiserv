/**
 * 
 */
package es.caib.emiserv.core.api.service.ws.backoffice;

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
	//public static final String JiBX_bindingList = "|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV3Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV2Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV3Factory|es.scsp.bean.common.JiBX_PeticionGenericV2Factory|es.scsp.bean.common.JiBX_PeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV3Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV2Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV3Factory|es.scsp.bean.common.JiBX_RespuestaGenericV2Factory|es.scsp.bean.common.JiBX_RespuestaGenericV3Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV3Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV2Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV3Factory|";

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
