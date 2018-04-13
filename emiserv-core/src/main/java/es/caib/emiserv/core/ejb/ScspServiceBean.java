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

import es.caib.emiserv.core.api.dto.AplicacioDto;
import es.caib.emiserv.core.api.dto.AutoritatCertificacioDto;
import es.caib.emiserv.core.api.dto.AutoritzacioDto;
import es.caib.emiserv.core.api.dto.ClauPrivadaDto;
import es.caib.emiserv.core.api.dto.ClauPublicaDto;
import es.caib.emiserv.core.api.dto.EmisorDto;
import es.caib.emiserv.core.api.dto.OrganismeDto;
import es.caib.emiserv.core.api.dto.OrganismeFiltreDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.service.ScspService;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ScspServiceBean implements ScspService {

	@Autowired
	ScspService delegate;

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AplicacioDto aplicacioCreate(
			AplicacioDto organismo) {
		return delegate.aplicacioCreate(organismo);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void aplicacioUpdate(
			AplicacioDto organismo) {
		delegate.aplicacioUpdate(organismo);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void aplicacioDelete(
			Integer id) {
		delegate.aplicacioDelete(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AplicacioDto aplicacioFindById(
			Integer id) throws NotFoundException {
		return delegate.aplicacioFindById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<AplicacioDto> aplicacioFindByFiltrePaginat(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.aplicacioFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public OrganismeDto organismeCreate(
			OrganismeDto organismo) {
		return delegate.organismeCreate(organismo);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void organismeUpdate(
			OrganismeDto organismo) {
		delegate.organismeUpdate(organismo);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void organismeDelete(
			Long id) {
		delegate.organismeDelete(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public OrganismeDto organismeFindById(
			Long id) throws NotFoundException {
		return delegate.organismeFindById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<OrganismeDto> organismeFindByFiltrePaginat(
			OrganismeFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.organismeFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritzacioDto autoritzacioCreate(
			AutoritzacioDto autoritzacio) {
		return delegate.autoritzacioCreate(autoritzacio);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void autoritzacioUpdate(
			AutoritzacioDto autoritzacio) {
		delegate.autoritzacioUpdate(autoritzacio);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void autoritzacioDelete(
			Long id) throws NotFoundException {
		delegate.autoritzacioDelete(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritzacioDto autoritzacioFindById(
			Long id) throws NotFoundException {
		return delegate.autoritzacioFindById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<AutoritzacioDto> autoritzacioFindByServeiPaginat(
			Long serveiId,
			PaginacioParamsDto paginacioParams) {
		return delegate.autoritzacioFindByServeiPaginat(
				serveiId,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<EmisorDto> emissorFindAll() {
		return delegate.emissorFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<ClauPublicaDto> clauPublicaFindAll() {
		return delegate.clauPublicaFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<ClauPrivadaDto> clauPrivadaFindAll() {
		return delegate.clauPrivadaFindAll();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<AutoritatCertificacioDto> autoridadCertificacionFindAll() {
		return delegate.autoridadCertificacionFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<AplicacioDto> aplicacioFindAll() {
		return delegate.aplicacioFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<OrganismeDto> organismeFindAll() {
		return delegate.organismeFindAll();
	}

}
