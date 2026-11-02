package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.ClauPrivadaResource;
import es.caib.emiserv.logic.intf.service.ClauPrivadaResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració ClauPrivadaResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "ClauPrivadaResourceService")
@RolesAllowed("EMS_ADMIN")
public class ClauPrivadaResourceServiceEjb
		extends AbstractResourceServiceEjb<ClauPrivadaResourceService, ClauPrivadaResource, Long>
		implements ClauPrivadaResourceService {

}
