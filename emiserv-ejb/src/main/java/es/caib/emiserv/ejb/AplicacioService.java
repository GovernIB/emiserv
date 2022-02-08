/**
 * 
 */
package es.caib.emiserv.ejb;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
public class AplicacioService extends AbstractService<es.caib.emiserv.logic.intf.service.AplicacioService> implements es.caib.emiserv.logic.intf.service.AplicacioService {

	@Override
	public String getIdiomaUsuariActual() {
		return getDelegateService().getIdiomaUsuariActual();
	}

	@Override
	public void updateIdiomaUsuariActual(String idioma) {
		getDelegateService().updateIdiomaUsuariActual(idioma);
	}

    @Override
	@PermitAll
    public void propagateDbProperties() {
        getDelegateService().propagateDbProperties();
    }

}
