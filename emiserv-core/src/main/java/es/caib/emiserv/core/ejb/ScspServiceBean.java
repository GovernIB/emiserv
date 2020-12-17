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
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.emiserv.core.api.dto.AplicacioDto;
import es.caib.emiserv.core.api.dto.AutoritatCertificacioDto;
import es.caib.emiserv.core.api.dto.AutoritzacioDto;
import es.caib.emiserv.core.api.dto.AutoritzacioFiltreDto;
import es.caib.emiserv.core.api.dto.ClauPrivadaDto;
import es.caib.emiserv.core.api.dto.ClauPublicaDto;
import es.caib.emiserv.core.api.dto.EmisorDto;
import es.caib.emiserv.core.api.dto.OrganismeCessionariDto;
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
	public List<OrganismeDto> organismeFindByCif(String cif) throws NotFoundException {
		return delegate.organismeFindByCif(cif);
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
	public PaginaDto<AutoritzacioDto> autoritzacioFindByFiltrePaginat(
			AutoritzacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.autoritzacioFindByFiltrePaginat(
				filtre,
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

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<OrganismeCessionariDto> findAllOrganismeCessionari() {
		return delegate.findAllOrganismeCessionari();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<ClauPublicaDto> clauPublicaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		return delegate.clauPublicaFindByFiltrePaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPublicaDto findClauPublicaById(Long id) throws NotFoundException {
		return delegate.findClauPublicaById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPublicaDto clauPublicaCreate(ClauPublicaDto item) {
		return delegate.clauPublicaCreate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPublicaDto clauPublicaUpdate(ClauPublicaDto item) throws NotFoundException {
		return delegate.clauPublicaUpdate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void clauPublicaDelete(Long id) throws NotFoundException {
		delegate.clauPublicaDelete(id);
		
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<ClauPrivadaDto> clauPrivadaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		return delegate.clauPrivadaFindByFiltrePaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPrivadaDto findClauPrivadaById(Long id) throws NotFoundException {
		return delegate.findClauPrivadaById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPrivadaDto clauPrivadaCreate(ClauPrivadaDto item) {
		return delegate.clauPrivadaCreate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPrivadaDto clauPrivadaUpdate(ClauPrivadaDto item) throws NotFoundException {
		return delegate.clauPrivadaUpdate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void clauPrivadaDelete(Long id) throws NotFoundException {
		delegate.clauPrivadaDelete(id);		
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<AutoritatCertificacioDto> autoritatCertificacioFindByFiltrePaginat(
			PaginacioParamsDto paginacioParams) {
		return delegate.autoritatCertificacioFindByFiltrePaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritatCertificacioDto autoritatCertificacioFindById(Long id) throws NotFoundException {
		return delegate.autoritatCertificacioFindById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritatCertificacioDto autoritatCertificacioCreate(AutoritatCertificacioDto item) {
		return delegate.autoritatCertificacioCreate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritatCertificacioDto autoritatCertificacioUpdate(
			AutoritatCertificacioDto item) throws NotFoundException {
		return delegate.autoritatCertificacioUpdate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void autoritatCertificacioDelete(Long id) throws NotFoundException {
		delegate.autoritatCertificacioDelete(id);
	}

}
