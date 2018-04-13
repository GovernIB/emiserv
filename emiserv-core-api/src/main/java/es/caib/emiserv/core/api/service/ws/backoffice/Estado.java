/**
 * 
 */
package es.caib.emiserv.core.api.service.ws.backoffice;

/**
 * Definició de l'estructura Estado per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Estado {
	private String codigoEstado;
	private String codigoEstadoSecundario;
	private String literalError;
	private Integer tiempoEstimadoRespuesta;
	//public static final String JiBX_bindingList = "|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV3Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV2Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV3Factory|es.scsp.bean.common.JiBX_PeticionGenericV2Factory|es.scsp.bean.common.JiBX_PeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV3Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV2Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV3Factory|es.scsp.bean.common.JiBX_RespuestaGenericV2Factory|es.scsp.bean.common.JiBX_RespuestaGenericV3Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV3Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV2Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV3Factory|";

	public String getCodigoEstado() {
		return this.codigoEstado;
	}

	public String getCodigoEstadoSecundario() {
		return this.codigoEstadoSecundario;
	}

	public String getLiteralError() {
		return this.literalError;
	}

	public Integer getTiempoEstimadoRespuesta() {
		return this.tiempoEstimadoRespuesta;
	}

	public void setCodigoEstado(String codigoEstado) {
		this.codigoEstado = codigoEstado;
	}

	public void setCodigoEstadoSecundario(String codigoEstadoSecundario) {
		this.codigoEstadoSecundario = codigoEstadoSecundario;
	}

	public void setLiteralError(String literalError) {
		this.literalError = literalError;
	}

	public void setTiempoEstimadoRespuesta(Integer tiempoEstimadoRespuesta) {
		this.tiempoEstimadoRespuesta = tiempoEstimadoRespuesta;
	}
}
