/**
 * 
 */
package es.caib.emiserv.core.api.service.ws.backoffice;

import java.util.ArrayList;

/**
 * Definició de l'estructura Transmisiones per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Transmisiones {
	private ArrayList<TransmisionDatos> transmisionDatos;
	//public static final String JiBX_bindingList = "|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV3Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV2Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV3Factory|es.scsp.bean.common.JiBX_PeticionGenericV2Factory|es.scsp.bean.common.JiBX_PeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV3Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV2Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV3Factory|es.scsp.bean.common.JiBX_RespuestaGenericV2Factory|es.scsp.bean.common.JiBX_RespuestaGenericV3Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV3Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV2Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV3Factory|";

	public ArrayList<TransmisionDatos> getTransmisionDatos() {
		return this.transmisionDatos;
	}

	public void setTransmisionDatos(ArrayList<TransmisionDatos> transmisionDatos) {
		this.transmisionDatos = transmisionDatos;
	}
}
