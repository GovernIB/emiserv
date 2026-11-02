package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.AutoritatCertificacioResource;
import es.caib.emiserv.logic.intf.service.AutoritatCertificacioResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs de configuració AutoritatCertificacioResource. Només EMS_ADMIN, com els mètodes de la JSP.
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "AutoritatCertificacioResourceService")
@RolesAllowed("EMS_ADMIN")
public class AutoritatCertificacioResourceServiceEjb
		extends AbstractResourceServiceEjb<AutoritatCertificacioResourceService, AutoritatCertificacioResource, Long>
		implements AutoritatCertificacioResourceService {

}
