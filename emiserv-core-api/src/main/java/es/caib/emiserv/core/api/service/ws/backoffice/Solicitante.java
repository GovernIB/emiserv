/**
 * 
 */
package es.caib.emiserv.core.api.service.ws.backoffice;

/**
 * Definició de l'estructura Solicitante per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Solicitante {
	private String identificadorSolicitante;
	private String nombreSolicitante;
	private String finalidad;
	private Consentimiento consentimiento;
	private Funcionario funcionario;
	private String unidadTramitadora;
	private String idExpediente;
	private Procedimiento procedimiento;
	//public static final String JiBX_bindingList = "|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV3Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV2Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV2Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV3Factory|es.scsp.bean.common.JiBX_PeticionGenericV2Factory|es.scsp.bean.common.JiBX_PeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV3Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV2Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV3Factory|es.scsp.bean.common.JiBX_RespuestaGenericV2Factory|es.scsp.bean.common.JiBX_RespuestaGenericV3Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV2Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV3Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV2Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV3Factory|";

	public String getUnidadTramitadora() {
		return this.unidadTramitadora;
	}

	public void setUnidadTramitadora(String unidadTramitadora) {
		this.unidadTramitadora = unidadTramitadora;
	}

	public String getIdExpediente() {
		return this.idExpediente;
	}

	public void setIdExpediente(String idExpediente) {
		this.idExpediente = idExpediente;
	}

	public Procedimiento getProcedimiento() {
		return this.procedimiento;
	}

	public void setProcedimiento(Procedimiento procedimiento) {
		this.procedimiento = procedimiento;
	}

	public Consentimiento getConsentimiento() {
		return this.consentimiento;
	}

	public String getFinalidad() {
		return this.finalidad;
	}

	public Funcionario getFuncionario() {
		return this.funcionario;
	}

	public String getIdentificadorSolicitante() {
		return this.identificadorSolicitante;
	}

	public String getNombreSolicitante() {
		return this.nombreSolicitante;
	}

	public void setConsentimiento(Consentimiento consentimiento) {
		this.consentimiento = consentimiento;
	}

	public void setFinalidad(String finalidad) {
		this.finalidad = finalidad;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public void setIdentificadorSolicitante(String identificadorSolicitante) {
		this.identificadorSolicitante = identificadorSolicitante;
	}

	public void setNombreSolicitante(String nombreSolicitante) {
		this.nombreSolicitante = nombreSolicitante;
	}
}
