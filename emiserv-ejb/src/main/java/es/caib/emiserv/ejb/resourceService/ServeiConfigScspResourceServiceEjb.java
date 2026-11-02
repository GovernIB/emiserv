package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.ServeiConfigScspResource;
import es.caib.emiserv.logic.intf.service.ServeiConfigScspResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs ServeiConfigScspResource (administrador i responsable; ServeiService
 * comprova el permís sobre el servei).
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "ServeiConfigScspResourceService")
@RolesAllowed({"EMS_ADMIN", "EMS_RESP"})
public class ServeiConfigScspResourceServiceEjb
		extends AbstractResourceServiceEjb<ServeiConfigScspResourceService, ServeiConfigScspResource, Long>
		implements ServeiConfigScspResourceService {

}
