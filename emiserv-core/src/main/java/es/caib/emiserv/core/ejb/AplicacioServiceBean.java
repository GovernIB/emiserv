/**
 * 
 */
package es.caib.emiserv.core.ejb;

import java.io.IOException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.core.api.dto.UsuariDto;
import es.caib.emiserv.core.api.service.AplicacioService;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AplicacioServiceBean implements AplicacioService {

	@Autowired
	AplicacioService delegate;



	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public String getVersioActual() throws IOException {
		logger.debug("Obtenint versió actual de l'aplicació (EJB)");
		return delegate.getVersioActual();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public void processarAutenticacioUsuari() {
		logger.debug("Processant autenticació (EJB)");
		delegate.processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public UsuariDto getUsuariActual() {
		logger.debug("Obtenint usuari actual (EJB)");
		return delegate.getUsuariActual();
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public UsuariDto findUsuariAmbCodi(String codi) {
		logger.debug("Obtenint usuari amb codi (EJB) (codi=" + codi + ")");
		return delegate.findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
	public List<UsuariDto> findUsuariAmbText(String text) {
		logger.debug("Consultant usuaris amb text (EJB) (text=" + text + ")");
		return delegate.findUsuariAmbText(text);
	}

	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceBean.class);

}
