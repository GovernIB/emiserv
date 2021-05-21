/**
 * 
 */
package es.caib.emiserv.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.emiserv.logic.intf.service.AplicacioService;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
public class AplicacioServiceBean extends AbstractServiceBean<AplicacioService> implements AplicacioService {

	@Override
	public String getIdiomaUsuariActual() {
		return getDelegateService().getIdiomaUsuariActual();
	}

	@Override
	public void updateIdiomaUsuariActual(String idioma) {
		getDelegateService().updateIdiomaUsuariActual(idioma);
	}

}
