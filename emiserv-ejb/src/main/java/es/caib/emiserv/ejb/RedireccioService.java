/**
 * 
 */
package es.caib.emiserv.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaPeticioDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.ProcedimentDto;
import es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.logic.intf.dto.ServeiDto;
import es.caib.emiserv.logic.intf.exception.ScspParseException;

/**
 * Implementació de RedireccioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class RedireccioService extends AbstractService<es.caib.emiserv.logic.intf.service.RedireccioService> implements es.caib.emiserv.logic.intf.service.RedireccioService {

	@Autowired
	RedireccioService delegate;

	@Override
	public RedireccioProcessarResultatDto processarPeticio(
			byte[] xml) throws ScspParseException {
		return getDelegateService().processarPeticio(xml);
	}

	@Override
	public void processarResposta(
			String peticioId,
			String serveiCodi,
			byte[] xml) throws Exception {
		getDelegateService().processarResposta(
				peticioId,
				serveiCodi,
				xml);
	}
	
	@Override
	public String escollirResposta(RedireccioProcessarResultatDto resultat,
			Map<String, byte[]> xmls) {
		return getDelegateService().escollirResposta(resultat, xmls);
	}
	

	@Override
	public String generarSoapFault(
			RedireccioProcessarResultatDto redireccioProcessarResultat) {
		return getDelegateService().generarSoapFault(redireccioProcessarResultat);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<ProcedimentDto> procedimentFindAll() {
		return getDelegateService().procedimentFindAll();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<ServeiDto> serveiFindAll() {
		return getDelegateService().serveiFindAll();
	}

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
	public List<AuditoriaSolicitudDto> solicitudFindByPeticioId(
			Long peticioId) {
		return getDelegateService().solicitudFindByPeticioId(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlPeticio(Long peticionId) {
		return getDelegateService().peticioXmlPeticio(peticionId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlResposta(Long peticionId) {
		return getDelegateService().peticioXmlResposta(peticionId);
	}

}
