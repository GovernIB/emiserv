package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.ClauPublicaResource;
import es.caib.emiserv.logic.intf.service.ClauPublicaResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració ClauPublicaResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "ClauPublicaResourceService")
@RolesAllowed("EMS_ADMIN")
public class ClauPublicaResourceServiceEjb
		extends AbstractResourceServiceEjb<ClauPublicaResourceService, ClauPublicaResource, Long>
		implements ClauPublicaResourceService {

}
