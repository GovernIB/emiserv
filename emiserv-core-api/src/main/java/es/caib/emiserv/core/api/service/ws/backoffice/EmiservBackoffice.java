/**
 * 
 */
package es.caib.emiserv.core.api.service.ws.backoffice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

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
	 */
	@WebMethod(operationName="peticionSincrona")
    @WebResult(name="respuesta")
	public Respuesta peticionSincrona(
			@WebParam(name = "peticion") Peticion peticion);

	/**
	 * Petició de tipus asíncron.
	 * 
	 * @param peticion La petició rebuda.
	 * @return La confirmació de la petició realitzada.
	 */
	@WebMethod(operationName="peticionAsincrona")
    @WebResult(name="confirmacionPeticion")
	public ConfirmacionPeticion peticionAsincrona(
			@WebParam(name = "peticion") Peticion peticion);

	/**
	 * Sol·licitud de resposta per a una petició asíncrona.
	 * 
	 * @param solicitudRespuesta La sol·licitud rebuda.
	 * @return La resposta a la petició.
	 */
	@WebMethod(operationName="solicitarRespuesta")
    @WebResult(name="respuesta")
	public Respuesta solicitarRespuesta(
			@WebParam(name = "solicitudRespuesta") SolicitudRespuesta solicitudRespuesta);

}
