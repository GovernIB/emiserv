/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

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
