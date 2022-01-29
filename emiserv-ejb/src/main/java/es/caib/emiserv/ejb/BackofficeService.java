/**
 * 
 */
package es.caib.emiserv.ejb;

import es.caib.emiserv.logic.intf.dto.*;
import es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de BackofficeService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class BackofficeService extends AbstractService<es.caib.emiserv.logic.intf.service.BackofficeService> implements es.caib.emiserv.logic.intf.service.BackofficeService {

//	@Override
//	public ServeiDto serveiFindByCodi(
//			String codi) {
//		return getDelegateService().serveiFindByCodi(codi);
//	}
//
//	@Override
//	public ServeiConfigScspDto serveiFindConfiguracioScsp(
//			String codi) throws NotFoundException {
//		return getDelegateService().serveiFindConfiguracioScsp(codi);
//	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public PaginaDto<AuditoriaPeticioDto> peticioFindByFiltrePaginat(
			AuditoriaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().peticioFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public AuditoriaPeticioDto peticioFindById(
			String peticioId) {
		return getDelegateService().peticioFindById(peticioId);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<AuditoriaTransmisionDto> solicitudFindByPeticio(
			String peticioId) {
		return getDelegateService().solicitudFindByPeticio(peticioId);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public AuditoriaTransmisionDto solicitudFindById(
			String peticioId,
			String solicitudId) {
		return getDelegateService().solicitudFindById(
				peticioId,
				solicitudId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlPeticioScsp(String peticioId) {
		return getDelegateService().peticioXmlPeticioScsp(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlRespostaScsp(String peticioId) {
		return getDelegateService().peticioXmlRespostaScsp(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlPeticioBackoffice(String peticioId) {
		return getDelegateService().peticioXmlPeticioBackoffice(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlRespostaBackoffice(String peticioId) {
		return getDelegateService().peticioXmlRespostaBackoffice(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioErrorComunicacioBackoffice(String peticioId) {
		return getDelegateService().peticioErrorComunicacioBackoffice(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String solicitudXmlPeticioBackoffice(
			String peticioId,
			String solicitudId) {
		return getDelegateService().solicitudXmlPeticioBackoffice(
				peticioId,
				solicitudId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String solicitudXmlRespostaBackoffice(
			String peticioId,
			String solicitudId) {
		return getDelegateService().solicitudXmlRespostaBackoffice(
				peticioId,
				solicitudId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String solicitudErrorComunicacioBackoffice(
			String peticioId,
			String solicitudId) {
		return getDelegateService().solicitudErrorComunicacioBackoffice(
				peticioId,
				solicitudId);
	}

	@Override
	@PermitAll
	public Respuesta peticioBackofficeSincrona(
			Peticion peticion) {
		return getDelegateService().peticioBackofficeSincrona(peticion);
	}

	@Override
	@PermitAll
	public ConfirmacionPeticion peticioBackofficeAsincrona(
			Peticion peticion) {
		return getDelegateService().peticioBackofficeAsincrona(peticion);
	}

	@Override
	@PermitAll
	public Respuesta peticioBackofficeSolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) {
		return getDelegateService().peticioBackofficeSolicitudRespuesta(solicitudRespuesta);
	}

	@Override
	@PermitAll
	public void peticioBackofficeAsyncProcessarPendents() {
		getDelegateService().peticioBackofficeAsyncProcessarPendents();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<String> getBackofficeClasses() {
		return getDelegateService().getBackofficeClasses();
	}

}
