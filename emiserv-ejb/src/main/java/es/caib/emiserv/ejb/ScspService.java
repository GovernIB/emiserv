/**
 * 
 */
package es.caib.emiserv.ejb;

import es.caib.emiserv.logic.intf.dto.*;
import es.caib.emiserv.logic.intf.exception.NotFoundException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Optional;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed("EMS_ADMIN")
public class ScspService extends AbstractService<es.caib.emiserv.logic.intf.service.ScspService> implements es.caib.emiserv.logic.intf.service.ScspService {

	@Override
	public AplicacioDto aplicacioCreate(
			AplicacioDto organismo) {
		return getDelegateService().aplicacioCreate(organismo);
	}

	@Override
	public void aplicacioUpdate(
			AplicacioDto organismo) {
		getDelegateService().aplicacioUpdate(organismo);
	}

	@Override
	public void aplicacioDelete(
			Integer id) {
		getDelegateService().aplicacioDelete(id);
	}

	@Override
	public AplicacioDto aplicacioFindById(
			Integer id) throws NotFoundException {
		return getDelegateService().aplicacioFindById(id);
	}

	@Override
	public PaginaDto<AplicacioDto> aplicacioFindByFiltrePaginat(
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().aplicacioFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	public OrganismeDto organismeCreate(
			OrganismeDto organismo) {
		return getDelegateService().organismeCreate(organismo);
	}

	@Override
	public void organismeUpdate(
			OrganismeDto organismo) {
		getDelegateService().organismeUpdate(organismo);
	}

	@Override
	public void organismeDelete(
			Long id) {
		getDelegateService().organismeDelete(id);
	}

	@Override
	public OrganismeDto organismeFindById(
			Long id) throws NotFoundException {
		return getDelegateService().organismeFindById(id);
	}

	@Override
	public OrganismeDto organismeFindByCif(String cif) {
		return getDelegateService().organismeFindByCif(cif);
	}

	@Override
	public PaginaDto<OrganismeDto> organismeFindByFiltrePaginat(
			OrganismeFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().organismeFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	public OrganismeDto organismeCessionariCreate(
			OrganismeDto organismo) throws NotFoundException {
		return getDelegateService().organismeCessionariCreate(organismo);
	}

	@Override
	public void organismeCessionariUpdate(
			OrganismeDto organismo) throws NotFoundException {
		getDelegateService().organismeCessionariUpdate(organismo);
	}

	@Override
	public void organismeCessionariDelete(
			Long id) throws NotFoundException {
		getDelegateService().organismeCessionariDelete(id);
	}

	@Override
	public OrganismeDto organismeCessionariFindById(
			Long id) throws NotFoundException {
		return getDelegateService().organismeCessionariFindById(id);
	}

	@Override
	public OrganismeDto organismeCessionariFindByCif(
			String cif) {
		return getDelegateService().organismeCessionariFindByCif(cif);
	}

	@Override
	public PaginaDto<OrganismeDto> organismeCessionariFindByFiltrePaginat(
			OrganismeFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().organismeCessionariFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	public AutoritzacioDto autoritzacioCreate(
			AutoritzacioDto autoritzacio) {
		return getDelegateService().autoritzacioCreate(autoritzacio);
	}

	@Override
	public void autoritzacioUpdate(
			AutoritzacioDto autoritzacio) {
		getDelegateService().autoritzacioUpdate(autoritzacio);
	}

	@Override
	public void autoritzacioDelete(
			Long id) throws NotFoundException {
		getDelegateService().autoritzacioDelete(id);
	}

	@Override
	public AutoritzacioDto autoritzacioFindById(
			Long id) throws NotFoundException {
		return getDelegateService().autoritzacioFindById(id);
	}

	@Override
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
	public List<OrganismeCessionariDto> findAllOrganismeCessionari() {
		return getDelegateService().findAllOrganismeCessionari();
	}

	@Override
	public PaginaDto<ClauPublicaDto> clauPublicaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().clauPublicaFindByFiltrePaginat(paginacioParams);
	}

	@Override
	public ClauPublicaDto findClauPublicaById(Long id) throws NotFoundException {
		return getDelegateService().findClauPublicaById(id);
	}

	@Override
	public ClauPublicaDto clauPublicaCreate(ClauPublicaDto item) {
		return getDelegateService().clauPublicaCreate(item);
	}

	@Override
	public ClauPublicaDto clauPublicaUpdate(ClauPublicaDto item) throws NotFoundException {
		return getDelegateService().clauPublicaUpdate(item);
	}

	@Override
	public void clauPublicaDelete(Long id) throws NotFoundException {
		getDelegateService().clauPublicaDelete(id);
		
	}

	@Override
	public PaginaDto<ClauPrivadaDto> clauPrivadaFindByFiltrePaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().clauPrivadaFindByFiltrePaginat(paginacioParams);
	}

	@Override
	public ClauPrivadaDto findClauPrivadaById(Long id) throws NotFoundException {
		return getDelegateService().findClauPrivadaById(id);
	}

	@Override
	public ClauPrivadaDto clauPrivadaCreate(ClauPrivadaDto item) {
		return getDelegateService().clauPrivadaCreate(item);
	}

	@Override
	public ClauPrivadaDto clauPrivadaUpdate(ClauPrivadaDto item) throws NotFoundException {
		return getDelegateService().clauPrivadaUpdate(item);
	}

	@Override
	public void clauPrivadaDelete(Long id) throws NotFoundException {
		getDelegateService().clauPrivadaDelete(id);		
	}

	@Override
	public PaginaDto<AutoritatCertificacioDto> autoritatCertificacioFindByFiltrePaginat(
			AutoritatCertificacioFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().autoritatCertificacioFindByFiltrePaginat(filtre, paginacioParams);
	}

	@Override
	public AutoritatCertificacioDto autoritatCertificacioFindById(Long id) throws NotFoundException {
		return getDelegateService().autoritatCertificacioFindById(id);
	}

	@Override
	public AutoritatCertificacioDto autoritatCertificacioCreate(AutoritatCertificacioDto item) {
		return getDelegateService().autoritatCertificacioCreate(item);
	}

	@Override
	public AutoritatCertificacioDto autoritatCertificacioUpdate(
			AutoritatCertificacioDto item) throws NotFoundException {
		return getDelegateService().autoritatCertificacioUpdate(item);
	}

	@Override
	public void autoritatCertificacioDelete(Long id) throws NotFoundException {
		getDelegateService().autoritatCertificacioDelete(id);
	}

//	@Override
//	@PermitAll
//	public void propagateScspPropertiesToDb() {
//		getDelegateService().propagateScspPropertiesToDb();
//	}

	@Override
	public PaginaDto<ScspModulDto> getScspModuls(PaginacioParamsDto paginacioParams) {
		return getDelegateService().getScspModuls(paginacioParams);
	}

	@Override
	public ScspModulDto getScspModul(String nom) throws NotFoundException {
		return getDelegateService().getScspModul(nom);
	}

	@Override
	public void updateScspModul(ScspModulDto modulDto) throws NotFoundException {
		getDelegateService().updateScspModul(modulDto);
	}

	@Override
	public PaginaDto<ScspParametreDto> getScspParametres(PaginacioParamsDto paginacioDtoFromRequest) {
		return getDelegateService().getScspParametres(paginacioDtoFromRequest);
	}

	@Override
	public ScspParametreDto getScspParametre(String nom) {
		return getDelegateService().getScspParametre(nom);
	}

	@Override
	public Optional<ScspParametreDto> getOptionalScspParametre(String nom) {
		return getDelegateService().getOptionalScspParametre(nom);
	}

	@Override
	public void updateScspParametre(ScspParametreDto parametreDto) {
		getDelegateService().updateScspParametre(parametreDto);
	}

	@Override
	public void createScspParametre(ScspParametreDto parametreDto) {
		getDelegateService().createScspParametre(parametreDto);
	}

	@Override
	public void deleteParametre(String nom) {
		getDelegateService().deleteParametre(nom);
	}

}
