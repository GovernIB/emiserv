/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

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
