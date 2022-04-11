/**
 * 
 */
package es.caib.emiserv.ejb;

import es.caib.emiserv.logic.intf.dto.*;
import es.caib.emiserv.logic.intf.exception.NotFoundException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.List;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed("EMS_ADMIN")
public class ServeiService extends AbstractService<es.caib.emiserv.logic.intf.service.ServeiService> implements es.caib.emiserv.logic.intf.service.ServeiService {

	@Override
	public ServeiDto create(ServeiDto servei) {
		return getDelegateService().create(servei);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiDto update(ServeiDto servei) {
		return getDelegateService().update(servei);
	}

	@Override
	public ServeiDto updateActiu(
			Long id,
			boolean actiu) {
		return getDelegateService().updateActiu(id, actiu);
	}

	@Override
	public ServeiDto delete(Long id) {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiDto findByCodi(String codi) {
		return getDelegateService().findByCodi(codi);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public PaginaDto<ServeiDto> findAllPaginat(
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<String> resolverClassesFindAll() {
		return getDelegateService().resolverClassesFindAll();
	}
	
	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<String> responseResolverClassesFindAll() {
		return getDelegateService().responseResolverClassesFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiConfigScspDto configuracioScspFindByServei(
			Long id) throws NotFoundException {
		return getDelegateService().configuracioScspFindByServei(id);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public void configuracioScspUpdate(
			Long id,
			ServeiConfigScspDto configuracio) {
		getDelegateService().configuracioScspUpdate(id, configuracio);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiRutaDestiDto rutaDestiCreate(
			Long id,
			ServeiRutaDestiDto rutaDesti) {
		return getDelegateService().rutaDestiCreate(id, rutaDesti);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public ServeiRutaDestiDto rutaDestiUpdate(
			Long id,
			ServeiRutaDestiDto rutaDesti) {
		return getDelegateService().rutaDestiUpdate(id, rutaDesti);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public void rutaDestiDelete(
			Long id,
			Long rutaDestiId) {
		getDelegateService().rutaDestiDelete(id, rutaDestiId);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public PaginaDto<ServeiRutaDestiDto> rutaDestiFindByServei(
			Long id,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().rutaDestiFindByServei(
				id,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public boolean rutaDestiMourePosicio(Long rutaId, int posicio) {
		return getDelegateService().rutaDestiMourePosicio(rutaId, posicio);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public void xsdCreate(
			Long id,
			ServeiXsdDto xsd,
			byte[] contingut) throws IOException {
		getDelegateService().xsdCreate(id, xsd, contingut);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public void xsdUpdate(
			Long id,
			ServeiXsdDto xsd,
			byte[] contingut) throws IOException {
		getDelegateService().xsdUpdate(id, xsd, contingut);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public void xsdDelete(
			Long id,
			XsdTipusEnumDto tipus) throws IOException {
		getDelegateService().xsdDelete(id, tipus);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public FitxerDto xsdDescarregarFitxer(
			Long id,
			XsdTipusEnumDto tipus) throws IOException {
		return getDelegateService().xsdDescarregarFitxer(id, tipus);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<ServeiXsdDto> xsdFindByServei(
			Long id) throws IOException {
		return getDelegateService().xsdFindByServei(id);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<ProcedimentDto> procedimentFindAll() {
		return getDelegateService().procedimentFindAll();
	}

	@Override
	public List<PermisDto> permisFindByServei(
			Long id) throws NotFoundException {
		return getDelegateService().permisFindByServei(id);
	}

	@Override
	public void permisUpdate(
			Long id,
			PermisDto permis) {
		getDelegateService().permisUpdate(id, permis);
	}

	@Override
	public void permisDelete(
			Long id,
			Long permisId) {
		getDelegateService().permisDelete(id, permisId);
	}

}
