/**
 * 
 */
package es.caib.emiserv.ejb;

import es.caib.emiserv.logic.intf.dto.EntitatDto;
import es.caib.emiserv.logic.intf.dto.EntitatFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed("EMS_ADMIN")
public class EntitatService extends AbstractService<es.caib.emiserv.logic.intf.service.EntitatService> implements es.caib.emiserv.logic.intf.service.EntitatService {

	@Override
	public EntitatDto create(EntitatDto entitat) {
		return getDelegateService().create(entitat);
	}

	@Override
	public EntitatDto update(EntitatDto entitat) throws NotFoundException {
		return getDelegateService().update(entitat);
	}

	@Override
	public void delete(Long id) throws NotFoundException {
		getDelegateService().delete(id);
	}

	@Override
	public EntitatDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	public EntitatDto findByCodi(String codi) {
		return getDelegateService().findByCodi(codi);
	}

	@Override
	public PaginaDto<EntitatDto> findAllPaginat(EntitatFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAllPaginat(filtre, paginacioParams);
	}

	@Override
	public void sincronitzar() throws Exception {
		getDelegateService().sincronitzar();
	}
}
