/**
 * 
 */
package es.caib.emiserv.core.ejb.ws;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.core.api.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.core.api.service.ws.backoffice.EmiservBackoffice;
import es.caib.emiserv.core.api.service.ws.backoffice.Peticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Respuesta;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudRespuesta;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "EmiservBackoffice",
		serviceName = "EmiservBackofficeService",
		portName = "EmiservBackofficePort",
		targetNamespace = "http://caib.es/emiserv/backoffice")
@WebContext(
		contextRoot = "/emiserv/ws",
		urlPattern = "/EmiservBackoffice",
		//authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EmiservBackofficeBean implements EmiservBackoffice {

	@Autowired
	EmiservBackoffice delegate;



	@Override
	public Respuesta peticionSincrona(
			Peticion peticion) {
		return delegate.peticionSincrona(peticion);
	}

	@Override
	public ConfirmacionPeticion peticionAsincrona(
			Peticion peticion) {
		return delegate.peticionAsincrona(peticion);
	}

	@Override
	public Respuesta solicitarRespuesta(
			SolicitudRespuesta solicitudRespuesta) {
		return delegate.solicitarRespuesta(solicitudRespuesta);
	}

}
