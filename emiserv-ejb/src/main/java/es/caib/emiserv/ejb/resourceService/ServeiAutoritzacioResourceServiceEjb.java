package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.ServeiAutoritzacioResource;
import es.caib.emiserv.logic.intf.service.ServeiAutoritzacioResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de autoritzacions dels serveis. Els permisos es comproven al controlador REST
 * (hasPermission) i al servei.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "ServeiAutoritzacioResourceService")
@RolesAllowed("**")
public class ServeiAutoritzacioResourceServiceEjb
		extends AbstractResourceServiceEjb<ServeiAutoritzacioResourceService, ServeiAutoritzacioResource, Long>
		implements ServeiAutoritzacioResourceService {

}
