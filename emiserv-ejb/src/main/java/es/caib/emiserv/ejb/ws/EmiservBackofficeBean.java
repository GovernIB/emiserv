/**
 * 
 */
package es.caib.emiserv.ejb.ws;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.ws.api.annotation.WebContext;

import es.caib.emiserv.ejb.AbstractService;
import es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.EmiservBackoffice;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebContext(
		contextRoot = "/emiservback/ws",
		urlPattern = "/EmiservBackoffice")
@WebService(
		name = EmiservBackoffice.SERVICE_NAME,
		serviceName = EmiservBackoffice.SERVICE_NAME + "Service",
		portName = EmiservBackoffice.SERVICE_NAME + "Port",
		targetNamespace = EmiservBackoffice.NAMESPACE_URI)
public class EmiservBackofficeBean extends AbstractService<EmiservBackoffice> implements EmiservBackoffice {

	@Override
	@WebMethod
	@WebResult
	public Respuesta peticionSincrona(
			@WebParam(name = "peticion") Peticion peticion) {
		return getDelegateService().peticionSincrona(peticion);
	}

	@Override
	@WebMethod
	@WebResult
	public ConfirmacionPeticion peticionAsincrona(
			@WebParam(name = "peticion") Peticion peticion) {
		return getDelegateService().peticionAsincrona(peticion);
	}

	@Override
	@WebMethod
	@WebResult
	public Respuesta solicitarRespuesta(
			@WebParam(name = "solicitudRespuesta") SolicitudRespuesta solicitudRespuesta) {
		return getDelegateService().solicitarRespuesta(solicitudRespuesta);
	}

}
