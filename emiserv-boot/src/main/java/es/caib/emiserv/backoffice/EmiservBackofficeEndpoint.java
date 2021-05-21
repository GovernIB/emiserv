/**
 * 
 */
package es.caib.emiserv.backoffice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.EmiservBackoffice;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta;

/**
 * @author limit
 *
 */
@Endpoint
public class EmiservBackofficeEndpoint {

	@Autowired
	private EmiservBackoffice emiservBackoffice;

	@PayloadRoot(namespace = EmiservBackoffice.NAMESPACE_URI, localPart = "peticionSincrona")
	@ResponsePayload
	public Respuesta peticionSincrona(@RequestPayload Peticion peticion) {
		return emiservBackoffice.peticionSincrona(peticion);
	}

	@PayloadRoot(namespace = EmiservBackoffice.NAMESPACE_URI, localPart = "peticionAsincrona")
	@ResponsePayload
	public ConfirmacionPeticion peticionAsincrona(@RequestPayload Peticion peticion) {
		return emiservBackoffice.peticionAsincrona(peticion);
	}

	@PayloadRoot(namespace = EmiservBackoffice.NAMESPACE_URI, localPart = "solicitarRespuesta")
	@ResponsePayload
	public Respuesta solicitarRespuesta(@RequestPayload SolicitudRespuesta solicitudRespuesta) {
		return emiservBackoffice.solicitarRespuesta(solicitudRespuesta);
	}

}
