/**
 * 
 */
package es.caib.emiserv.core.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.core.api.dto.InformeGeneralEstatDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.dto.PermisDto;
import es.caib.emiserv.core.api.dto.ProcedimentDto;
import es.caib.emiserv.core.api.dto.ServeiConfigScspDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.dto.ServeiRutaDestiDto;
import es.caib.emiserv.core.api.dto.ServeiTipusEnumDto;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.service.ServeiService;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ServeiServiceBean implements ServeiService {

	@Autowired
	ServeiService delegate;



	@Override
	@RolesAllowed("EMS_ADMIN")
	public ServeiDto create(ServeiDto servei) {
		return delegate.create(servei);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiDto update(ServeiDto servei) {
		return delegate.update(servei);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ServeiDto updateActiu(
			Long id,
			boolean actiu) {
		return delegate.updateActiu(id, actiu);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ServeiDto delete(Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiDto findByCodi(String codi) {
		return delegate.findByCodi(codi);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public PaginaDto<ServeiDto> findAllPaginat(
			PaginacioParamsDto paginacioParams) {
		return delegate.findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<String> resolverClassesFindAll() {
		return delegate.resolverClassesFindAll();
	}
	
	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<String> responseResolverClassesFindAll() {
		return delegate.responseResolverClassesFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiConfigScspDto configuracioScspFindByServei(
			Long id) throws NotFoundException {
		return delegate.configuracioScspFindByServei(id);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public void configuracioScspUpdate(
			Long id,
			ServeiConfigScspDto configuracio) {
		delegate.configuracioScspUpdate(id, configuracio);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiRutaDestiDto rutaDestiCreate(
			Long id,
			ServeiRutaDestiDto rutaDesti) {
		return delegate.rutaDestiCreate(id, rutaDesti);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiRutaDestiDto rutaDestiUpdate(
			Long id,
			ServeiRutaDestiDto rutaDesti) {
		return delegate.rutaDestiUpdate(id, rutaDesti);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiRutaDestiDto rutaDestiDelete(
			Long id,
			Long rutaDestiId) {
		return delegate.rutaDestiDelete(id, rutaDestiId);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public PaginaDto<ServeiRutaDestiDto> rutaDestiFindByServei(
			Long id,
			PaginacioParamsDto paginacioParams) {
		return delegate.rutaDestiFindByServei(
				id,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public boolean rutaDestiMourePosicio(Long rutaId, int posicio) {
		return delegate.rutaDestiMourePosicio(rutaId, posicio);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<ProcedimentDto> procedimentFindAll() {
		return delegate.procedimentFindAll();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<PermisDto> permisFindByServei(
			Long id) throws NotFoundException {
		return delegate.permisFindByServei(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void permisUpdate(
			Long id,
			PermisDto permis) {
		delegate.permisUpdate(id, permis);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void permisDelete(
			Long id,
			Long permisId) {
		delegate.permisDelete(id, permisId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<InformeGeneralEstatDto> informeGeneralEstat(
			Date dataInici, 
			Date dataFi,
			ServeiTipusEnumDto tipusPeticio) {
		return delegate.informeGeneralEstat(dataInici, dataFi, tipusPeticio);
	}

}
