package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.EntitatResource;
import es.caib.emiserv.logic.intf.service.EntitatResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració EntitatResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "EntitatResourceService")
@RolesAllowed("EMS_ADMIN")
public class EntitatResourceServiceEjb
		extends AbstractResourceServiceEjb<EntitatResourceService, EntitatResource, Long>
		implements EntitatResourceService {

}
