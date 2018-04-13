/**
 * 
 */
package es.caib.emiserv.backoffice.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import es.scsp.common.exceptions.ScspException;

/**
 * Interfície a implementar per als backoffices dels
 * emissors SCSP de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "EmiservBackoffice",
		serviceName = "EmiservBackofficeService",
		portName = "EmiservBackofficePort",
		targetNamespace = "http://caib.es/emiserv/backoffice")
public interface EmiservBackoffice {

	/**
	 * Petició de tipus síncron.
	 * 
	 * @param peticion La petició rebuda.
	 * @return La resposta a la petició.
	 * @throws ScspException
	 */
	@WebMethod(operationName="peticionSincrona")
    @WebResult(name="respuesta")
	public es.scsp.bean.common.Respuesta peticionSincrona(
			@WebParam(name = "peticion") es.scsp.bean.common.Peticion peticion);

	/**
	 * Petició de tipus asíncron.
	 * 
	 * @param peticion La petició rebuda.
	 * @return La confirmació de la petició realitzada.
	 * @throws ScspException
	 */
	@WebMethod(operationName="peticionAsincrona")
    @WebResult(name="confirmacionPeticion")
	public es.scsp.bean.common.ConfirmacionPeticion peticionAsincrona(
			@WebParam(name = "peticion") es.scsp.bean.common.Peticion peticion);

	/**
	 * Sol·licitud de resposta per a una petició asíncrona.
	 * 
	 * @param solicitudRespuesta La sol·licitud rebuda.
	 * @return La resposta a la petició.
	 * @throws ScspException
	 */
	@WebMethod(operationName="solicitarRespuesta")
    @WebResult(name="respuesta")
	public es.scsp.bean.common.Respuesta solicitarRespuesta(
			@WebParam(name = "solicitudRespuesta") es.scsp.bean.common.SolicitudRespuesta solicitudRespuesta);

}
