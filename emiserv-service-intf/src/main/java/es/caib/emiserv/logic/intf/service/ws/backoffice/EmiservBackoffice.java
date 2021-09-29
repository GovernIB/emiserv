/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * Interfície a implementar pels backoffices dels emissors SCSP de
 * la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = EmiservBackoffice.SERVICE_NAME,
		serviceName = EmiservBackoffice.SERVICE_NAME + "Service",
		portName = EmiservBackoffice.SERVICE_NAME + "Port",
		targetNamespace = EmiservBackoffice.NAMESPACE_URI)
public interface EmiservBackoffice {

	public static final String SERVICE_NAME = "EmiservBackoffice";
	public static final String NAMESPACE_URI = "http://caib.es/emiserv/backoffice";

	/**
	 * Petició de tipus síncron.
	 * 
	 * @param peticion La petició rebuda.
	 * @return La resposta a la petició.
	 */
	@WebMethod
	@WebResult(name = "respuesta")
	public Respuesta peticionSincrona(
			@WebParam(name = "peticion") Peticion peticion);

	/**
	 * Petició de tipus asíncron.
	 * 
	 * @param peticion La petició rebuda.
	 * @return La confirmació de la petició realitzada.
	 */
	@WebMethod
	@WebResult(name = "respuesta")
	public ConfirmacionPeticion peticionAsincrona(
			@WebParam(name = "peticion") Peticion peticion);

	/**
	 * Sol·licitud de resposta per a una petició asíncrona.
	 * 
	 * @param solicitudRespuesta La sol·licitud rebuda.
	 * @return La resposta a la petició.
	 */
	@WebMethod
	@WebResult(name = "respuesta")
	public Respuesta solicitarRespuesta(
			@WebParam(name = "solicitudRespuesta") SolicitudRespuesta solicitudRespuesta);

}
