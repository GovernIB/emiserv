package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.ServeiRutaResource;
import es.caib.emiserv.logic.intf.service.ServeiRutaResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de rutes dels serveis enrutadors. Els permisos es comproven al controlador REST
 * (hasPermission) i al servei.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "ServeiRutaResourceService")
@RolesAllowed("**")
public class ServeiRutaResourceServiceEjb
		extends AbstractResourceServiceEjb<ServeiRutaResourceService, ServeiRutaResource, Long>
		implements ServeiRutaResourceService {

}
