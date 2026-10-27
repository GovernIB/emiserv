/**
 * 
 */
package es.caib.emiserv.ejb;

import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaCedentDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaFiltreDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ConsultaAsincronaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ConsultaAsincronaService extends AbstractService<es.caib.emiserv.logic.intf.service.ConsultaAsincronaService> implements es.caib.emiserv.logic.intf.service.ConsultaAsincronaService {

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<ConsultaAsincronaDto> findByFiltrePaginat(
			ConsultaAsincronaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findByFiltrePaginat(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<ConsultaAsincronaSolicitudDto> solicitudFindByConsulta(Long consultaId) {
		return getDelegateService().solicitudFindByConsulta(consultaId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<ConsultaAsincronaCedentDto> cedentFindAll() {
		return getDelegateService().cedentFindAll();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void pausar(Long consultaId) {
		getDelegateService().pausar(consultaId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void reprendre(Long consultaId) {
		getDelegateService().reprendre(consultaId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public int reintentar(Long consultaId) {
		return getDelegateService().reintentar(consultaId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void reintentarSolicitud(Long consultaId, Long solicitudId) {
		getDelegateService().reintentarSolicitud(consultaId, solicitudId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public int cancelar(Long consultaId) {
		return getDelegateService().cancelar(consultaId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void updateCedentPausat(Long rutaId, boolean pausada) {
		getDelegateService().updateCedentPausat(rutaId, pausada);
	}

}
