/**
 * 
 */
package es.caib.emiserv.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.emiserv.logic.intf.dto.AplicacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritzacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritzacioFiltreDto;
import es.caib.emiserv.logic.intf.dto.ClauPrivadaDto;
import es.caib.emiserv.logic.intf.dto.ClauPublicaDto;
import es.caib.emiserv.logic.intf.dto.EmisorDto;
import es.caib.emiserv.logic.intf.dto.OrganismeCessionariDto;
import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import es.caib.emiserv.logic.intf.dto.OrganismeFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.ScspService;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ScspServiceBean extends AbstractServiceBean<ScspService> implements ScspService {

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AplicacioDto aplicacioCreate(
			AplicacioDto organismo) {
		return getDelegateService().aplicacioCreate(organismo);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void aplicacioUpdate(
			AplicacioDto organismo) {
		getDelegateService().aplicacioUpdate(organismo);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void aplicacioDelete(
			Integer id) {
		getDelegateService().aplicacioDelete(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AplicacioDto aplicacioFindById(
			Integer id) throws NotFoundException {
		return getDelegateService().aplicacioFindById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<AplicacioDto> aplicacioFindByFiltrePaginat(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().aplicacioFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public OrganismeDto organismeCreate(
			OrganismeDto organismo) {
		return getDelegateService().organismeCreate(organismo);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void organismeUpdate(
			OrganismeDto organismo) {
		getDelegateService().organismeUpdate(organismo);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void organismeDelete(
			Long id) {
		getDelegateService().organismeDelete(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public OrganismeDto organismeFindById(
			Long id) throws NotFoundException {
		return getDelegateService().organismeFindById(id);
	}
	
	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<OrganismeDto> organismeFindByCif(String cif) throws NotFoundException {
		return getDelegateService().organismeFindByCif(cif);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<OrganismeDto> organismeFindByFiltrePaginat(
			OrganismeFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().organismeFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritzacioDto autoritzacioCreate(
			AutoritzacioDto autoritzacio) {
		return getDelegateService().autoritzacioCreate(autoritzacio);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void autoritzacioUpdate(
			AutoritzacioDto autoritzacio) {
		getDelegateService().autoritzacioUpdate(autoritzacio);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void autoritzacioDelete(
			Long id) throws NotFoundException {
		getDelegateService().autoritzacioDelete(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritzacioDto autoritzacioFindById(
			Long id) throws NotFoundException {
		return getDelegateService().autoritzacioFindById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<AutoritzacioDto> autoritzacioFindByFiltrePaginat(
			AutoritzacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().autoritzacioFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<EmisorDto> emissorFindAll() {
		return getDelegateService().emissorFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<ClauPublicaDto> clauPublicaFindAll() {
		return getDelegateService().clauPublicaFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<ClauPrivadaDto> clauPrivadaFindAll() {
		return getDelegateService().clauPrivadaFindAll();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<AutoritatCertificacioDto> autoridadCertificacionFindAll() {
		return getDelegateService().autoridadCertificacionFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<AplicacioDto> aplicacioFindAll() {
		return getDelegateService().aplicacioFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<OrganismeDto> organismeFindAll() {
		return getDelegateService().organismeFindAll();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<OrganismeCessionariDto> findAllOrganismeCessionari() {
		return getDelegateService().findAllOrganismeCessionari();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<ClauPublicaDto> clauPublicaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().clauPublicaFindByFiltrePaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPublicaDto findClauPublicaById(Long id) throws NotFoundException {
		return getDelegateService().findClauPublicaById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPublicaDto clauPublicaCreate(ClauPublicaDto item) {
		return getDelegateService().clauPublicaCreate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPublicaDto clauPublicaUpdate(ClauPublicaDto item) throws NotFoundException {
		return getDelegateService().clauPublicaUpdate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void clauPublicaDelete(Long id) throws NotFoundException {
		getDelegateService().clauPublicaDelete(id);
		
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<ClauPrivadaDto> clauPrivadaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().clauPrivadaFindByFiltrePaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPrivadaDto findClauPrivadaById(Long id) throws NotFoundException {
		return getDelegateService().findClauPrivadaById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPrivadaDto clauPrivadaCreate(ClauPrivadaDto item) {
		return getDelegateService().clauPrivadaCreate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public ClauPrivadaDto clauPrivadaUpdate(ClauPrivadaDto item) throws NotFoundException {
		return getDelegateService().clauPrivadaUpdate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void clauPrivadaDelete(Long id) throws NotFoundException {
		getDelegateService().clauPrivadaDelete(id);		
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public PaginaDto<AutoritatCertificacioDto> autoritatCertificacioFindByFiltrePaginat(
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().autoritatCertificacioFindByFiltrePaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritatCertificacioDto autoritatCertificacioFindById(Long id) throws NotFoundException {
		return getDelegateService().autoritatCertificacioFindById(id);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritatCertificacioDto autoritatCertificacioCreate(AutoritatCertificacioDto item) {
		return getDelegateService().autoritatCertificacioCreate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public AutoritatCertificacioDto autoritatCertificacioUpdate(
			AutoritatCertificacioDto item) throws NotFoundException {
		return getDelegateService().autoritatCertificacioUpdate(item);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public void autoritatCertificacioDelete(Long id) throws NotFoundException {
		getDelegateService().autoritatCertificacioDelete(id);
	}

	@Override
	public void propagateScspPropertiesToDb() {
		getDelegateService().propagateScspPropertiesToDb();
	}

}
