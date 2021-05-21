/**
 * 
 */
package es.caib.emiserv.logic.intf.service.ws.backoffice;

/**
 * Interfície a implementar per als backoffices dels
 * emissors SCSP de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EmiservBackoffice {

	public static final String SERVICE_NAME = "EmiservBackoffice";
	public static final String NAMESPACE_URI = "http://caib.es/emiserv/backoffice";

	/**
	 * Petició de tipus síncron.
	 * 
	 * @param peticion La petició rebuda.
	 * @return La resposta a la petició.
	 */
	public Respuesta peticionSincrona(Peticion peticion);

	/**
	 * Petició de tipus asíncron.
	 * 
	 * @param peticion La petició rebuda.
	 * @return La confirmació de la petició realitzada.
	 */
	public ConfirmacionPeticion peticionAsincrona(Peticion peticion);

	/**
	 * Sol·licitud de resposta per a una petició asíncrona.
	 * 
	 * @param solicitudRespuesta La sol·licitud rebuda.
	 * @return La resposta a la petició.
	 */
	public Respuesta solicitarRespuesta(SolicitudRespuesta solicitudRespuesta);

}
