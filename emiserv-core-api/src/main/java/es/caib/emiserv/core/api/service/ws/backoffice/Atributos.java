/**
 * 
 */
package es.caib.emiserv.core.api.service.ws.backoffice;

/**
 * Definició de l'estructura Atributos per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Atributos {
	private String idPeticion;
	private String numElementos;
	private String timeStamp;
	private String codigoCertificado;
	private Estado estado;
	//public static final String JiBX_bindingList = "|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV3Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV2Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV3Factory|es.scsp.bean.common.JiBX_PeticionGenericV2Factory|es.scsp.bean.common.JiBX_PeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV3Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV2Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV3Factory|es.scsp.bean.common.JiBX_RespuestaGenericV2Factory|es.scsp.bean.common.JiBX_RespuestaGenericV3Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV3Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV2Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV3Factory|";

	public String getCodigoCertificado() {
		return this.codigoCertificado;
	}

	public Estado getEstado() {
		return this.estado;
	}

	public String getIdPeticion() {
		return this.idPeticion;
	}

	public String getNumElementos() {
		return this.numElementos;
	}

	public String getTimeStamp() {
		return this.timeStamp;
	}

	public void setCodigoCertificado(String codigoCertificado) {
		this.codigoCertificado = codigoCertificado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public void setIdPeticion(String idPeticion) {
		this.idPeticion = idPeticion;
	}

	public void setNumElementos(String numElementos) {
		this.numElementos = numElementos;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
}
