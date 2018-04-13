/**
 * 
 */
package es.caib.emiserv.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.core.api.dto.AuditoriaFiltreDto;
import es.caib.emiserv.core.api.dto.AuditoriaPeticioDto;
import es.caib.emiserv.core.api.dto.AuditoriaSolicitudDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.dto.ProcedimentDto;
import es.caib.emiserv.core.api.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.exception.ScspParseException;
import es.caib.emiserv.core.api.service.RedireccioService;

/**
 * Implementació de RedireccioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class RedireccioServiceBean implements RedireccioService {

	@Autowired
	RedireccioService delegate;

	@Override
	public RedireccioProcessarResultatDto processarPeticio(
			byte[] xml) throws ScspParseException {
		return delegate.processarPeticio(xml);
	}

	@Override
	public void processarResposta(
			String peticioId,
			String serveiCodi,
			byte[] xml) throws Exception {
		delegate.processarResposta(
				peticioId,
				serveiCodi,
				xml);
	}
	
	@Override
	public String escollirResposta(RedireccioProcessarResultatDto resultat,
			Map<String, byte[]> xmls) {
		return delegate.escollirResposta(resultat, xmls);
	}
	

	@Override
	public String generarSoapFault(
			RedireccioProcessarResultatDto redireccioProcessarResultat) {
		return delegate.generarSoapFault(redireccioProcessarResultat);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<ProcedimentDto> procedimentFindAll() {
		return delegate.procedimentFindAll();
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public List<ServeiDto> serveiFindAll() {
		return delegate.serveiFindAll();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public PaginaDto<AuditoriaPeticioDto> peticioFindByFiltrePaginat(
			AuditoriaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.peticioFindByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<AuditoriaSolicitudDto> solicitudFindByPeticioId(
			Long peticioId) {
		return delegate.solicitudFindByPeticioId(peticioId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlPeticio(Long peticionId) {
		return delegate.peticioXmlPeticio(peticionId);
	}

	@Override
	@RolesAllowed("EMS_ADMIN")
	public String peticioXmlResposta(Long peticionId) {
		return delegate.peticioXmlResposta(peticionId);
	}

}
