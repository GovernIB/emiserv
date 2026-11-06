package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.OrganismeCessionariResource;
import es.caib.emiserv.logic.intf.service.OrganismeCessionariResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració OrganismeCessionariResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "OrganismeCessionariResourceService")
@RolesAllowed("EMS_ADMIN")
public class OrganismeCessionariResourceServiceEjb
		extends AbstractResourceServiceEjb<OrganismeCessionariResourceService, OrganismeCessionariResource, Long>
		implements OrganismeCessionariResourceService {

}
