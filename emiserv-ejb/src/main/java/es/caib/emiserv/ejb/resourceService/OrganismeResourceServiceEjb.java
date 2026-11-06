package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.OrganismeResource;
import es.caib.emiserv.logic.intf.service.OrganismeResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració OrganismeResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "OrganismeResourceService")
@RolesAllowed("EMS_ADMIN")
public class OrganismeResourceServiceEjb
		extends AbstractResourceServiceEjb<OrganismeResourceService, OrganismeResource, Long>
		implements OrganismeResourceService {

}
