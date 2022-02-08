/**
 * 
 */
package es.caib.emiserv.ejb;

import es.caib.emiserv.logic.intf.dto.ConfigDto;
import es.caib.emiserv.logic.intf.dto.ConfigGroupDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed("EMS_ADMIN")
public class ConfigService extends AbstractService<es.caib.emiserv.logic.intf.service.ConfigService> implements es.caib.emiserv.logic.intf.service.ConfigService {

	@Override
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return getDelegateService().updateProperty(property);
	}
	@Override
	public List<ConfigGroupDto> findAll(){
		return getDelegateService().findAll();
	}

	@Override
	public List<String> syncFromJBossProperties(){
		return getDelegateService().syncFromJBossProperties();
	}
}
