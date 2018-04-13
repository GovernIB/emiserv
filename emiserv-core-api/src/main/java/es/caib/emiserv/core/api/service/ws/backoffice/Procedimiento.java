/**
 * 
 */
package es.caib.emiserv.core.api.service.ws.backoffice;

import java.io.Serializable;

/**
 * Definició de l'estructura Procedimiento per a la comunicació
 * amb el backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Procedimiento implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nombreProcedimiento;
	private String codProcedimiento;
	//public static final String JiBX_bindingList = "|es.scsp.bean.aeat.JiBX_ConfirmacionPeticionAeatV3Factory|es.scsp.bean.common.JiBX_ConfirmacionPeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_PeticionAeatV3Factory|es.scsp.bean.common.JiBX_PeticionGenericV3Factory|es.scsp.bean.aeat.JiBX_RespuestaAeatV3Factory|es.scsp.bean.common.JiBX_RespuestaDomicilioFiscalV3Factory|es.scsp.bean.common.JiBX_RespuestaGenericV3Factory|es.scsp.bean.aeat.JiBX_SolicitudRespuestaAeatV3Factory|es.scsp.bean.common.JiBX_SolicitudRespuestaGenericV3Factory|";

	public Procedimiento() {
	}

	public Procedimiento(String nombreProcedimiento, String codProcedimiento) {
		this.nombreProcedimiento = nombreProcedimiento;
		this.codProcedimiento = codProcedimiento;
	}

	public void setNombreProcedimiento(String nombreProcedimiento) {
		this.nombreProcedimiento = nombreProcedimiento;
	}

	public String getNombreProcedimiento() {
		return this.nombreProcedimiento;
	}

	public void setCodProcedimiento(String codProcedimiento) {
		this.codProcedimiento = codProcedimiento;
	}

	public String getCodProcedimiento() {
		return this.codProcedimiento;
	}
}
