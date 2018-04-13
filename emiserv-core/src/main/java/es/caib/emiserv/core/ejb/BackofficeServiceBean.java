/**
 * 
 */
package es.caib.emiserv.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.core.api.dto.AuditoriaFiltreDto;
import es.caib.emiserv.core.api.dto.AuditoriaPeticioDto;
import es.caib.emiserv.core.api.dto.AuditoriaTransmisionDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.dto.ServeiConfigScspDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.service.BackofficeService;
import es.caib.emiserv.core.api.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Peticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Respuesta;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudRespuesta;

/**
 * Implementació de BackofficeService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class BackofficeServiceBean implements BackofficeService {

	@Autowired
	BackofficeService delegate;

	@Override
	public ServeiDto serveiFindByCodi(
			String codi) {
		return delegate.serveiFindByCodi(codi);
	}

	@Override
	public ServeiConfigScspDto serveiFindConfiguracioScsp(
			String codi) throws NotFoundException {
		return delegate.serveiFindConfiguracioScsp(codi);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public PaginaDto<AuditoriaPeticioDto> peticioFindByFiltrePaginat(
			AuditoriaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.peticioFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public AuditoriaPeticioDto peticioFindById(
			String peticioId) {
		return delegate.peticioFindById(peticioId);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<AuditoriaTransmisionDto> solicitudFindByPeticio(
			String peticioId) {
		return delegate.solicitudFindByPeticio(peticioId);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public AuditoriaTransmisionDto solicitudFindById(
			String peticioId,
			String solicitudId) {
		return delegate.solicitudFindById(
				peticioId,
				solicitudId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlPeticioScsp(String peticioId) {
		return delegate.peticioXmlPeticioScsp(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlRespostaScsp(String peticioId) {
		return delegate.peticioXmlRespostaScsp(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlPeticioBackoffice(String peticioId) {
		return delegate.peticioXmlPeticioBackoffice(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlRespostaBackoffice(String peticioId) {
		return delegate.peticioXmlRespostaBackoffice(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioErrorComunicacioBackoffice(String peticioId) {
		return delegate.peticioErrorComunicacioBackoffice(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String solicitudXmlPeticioBackoffice(
			String peticioId,
			String solicitudId) {
		return delegate.solicitudXmlPeticioBackoffice(
				peticioId,
				solicitudId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String solicitudXmlRespostaBackoffice(
			String peticioId,
			String solicitudId) {
		return delegate.solicitudXmlRespostaBackoffice(
				peticioId,
				solicitudId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String solicitudErrorComunicacioBackoffice(
			String peticioId,
			String solicitudId) {
		return delegate.solicitudErrorComunicacioBackoffice(
				peticioId,
				solicitudId);
	}

	@Override
	public Respuesta peticioBackofficeSincrona(
			Peticion peticion) {
		return delegate.peticioBackofficeSincrona(peticion);
	}

	@Override
	public ConfirmacionPeticion peticioBackofficeAsincrona(
			Peticion peticion) {
		return delegate.peticioBackofficeAsincrona(peticion);
	}

	@Override
	public Respuesta peticioBackofficeSolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) {
		return delegate.peticioBackofficeSolicitudRespuesta(solicitudRespuesta);
	}

	@Override
	public void peticioBackofficeAsyncProcessarPendents() {
		delegate.peticioBackofficeAsyncProcessarPendents();
	}

}
