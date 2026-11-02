package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.AplicacioResource;
import es.caib.emiserv.logic.intf.service.AplicacioResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració AplicacioResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "AplicacioResourceService")
@RolesAllowed("EMS_ADMIN")
public class AplicacioResourceServiceEjb
		extends AbstractResourceServiceEjb<AplicacioResourceService, AplicacioResource, Integer>
		implements AplicacioResourceService {

}
