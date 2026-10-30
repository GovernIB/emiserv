package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.ServeiPermisResource;
import es.caib.emiserv.logic.intf.service.ServeiPermisResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de permisos dels serveis. Els permisos es comproven al controlador REST
 * (hasPermission) i al servei.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "ServeiPermisResourceService")
@RolesAllowed("**")
public class ServeiPermisResourceServiceEjb
		extends AbstractResourceServiceEjb<ServeiPermisResourceService, ServeiPermisResource, String>
		implements ServeiPermisResourceService {

}
