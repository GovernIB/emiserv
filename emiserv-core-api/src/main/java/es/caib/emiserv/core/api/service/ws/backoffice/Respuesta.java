/**
 * 
 */
package es.caib.emiserv.core.api.service.ws.backoffice;

/**
 * Definició de l'estructura Respuesta per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Respuesta {
	private Atributos atributos;
	private Transmisiones transmisiones;
	//public static final String JiBX_bindingList = "|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV3Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV2Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV3Factory|es.scsp.bean.common.JiBX_PeticionGenericV2Factory|es.scsp.bean.common.JiBX_PeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV3Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV2Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV3Factory|es.scsp.bean.common.JiBX_RespuestaGenericV2Factory|es.scsp.bean.common.JiBX_RespuestaGenericV3Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV3Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV2Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV3Factory|";

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
