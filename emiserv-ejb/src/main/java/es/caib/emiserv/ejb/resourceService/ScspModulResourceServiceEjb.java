package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.ScspModulResource;
import es.caib.emiserv.logic.intf.service.ScspModulResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració ScspModulResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "ScspModulResourceService")
@RolesAllowed("EMS_ADMIN")
public class ScspModulResourceServiceEjb
		extends AbstractResourceServiceEjb<ScspModulResourceService, ScspModulResource, String>
		implements ScspModulResourceService {

}
