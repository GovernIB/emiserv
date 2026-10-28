package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import es.caib.emiserv.logic.intf.service.ServeiResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de serveis. Els permisos es comproven al controlador REST
 * (hasPermission) i al servei (filtre ACL i rol EMS_ADMIN per a les accions).
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "ServeiResourceService")
@RolesAllowed("**")
public class ServeiResourceServiceEjb
		extends AbstractResourceServiceEjb<ServeiResourceService, ServeiResource, Long>
		implements ServeiResourceService {

}
