package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.EmisorResource;
import es.caib.emiserv.logic.intf.service.EmisorResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració EmisorResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "EmisorResourceService")
@RolesAllowed("EMS_ADMIN")
public class EmisorResourceServiceEjb
		extends AbstractResourceServiceEjb<EmisorResourceService, EmisorResource, Long>
		implements EmisorResourceService {

}
