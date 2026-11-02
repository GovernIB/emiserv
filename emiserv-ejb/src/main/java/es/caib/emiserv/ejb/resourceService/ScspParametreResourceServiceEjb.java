package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.ScspParametreResource;
import es.caib.emiserv.logic.intf.service.ScspParametreResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració ScspParametreResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "ScspParametreResourceService")
@RolesAllowed("EMS_ADMIN")
public class ScspParametreResourceServiceEjb
		extends AbstractResourceServiceEjb<ScspParametreResourceService, ScspParametreResource, String>
		implements ScspParametreResourceService {

}
