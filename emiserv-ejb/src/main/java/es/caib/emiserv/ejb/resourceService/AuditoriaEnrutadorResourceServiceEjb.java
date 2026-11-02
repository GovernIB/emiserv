package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.AuditoriaEnrutadorResource;
import es.caib.emiserv.logic.intf.service.AuditoriaEnrutadorResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs d'auditoria AuditoriaEnrutadorResource. Els permisos es comproven al controlador
 * REST (hasPermission) i a la consulta delegada (serveis permesos de l'usuari).
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "AuditoriaEnrutadorResourceService")
@RolesAllowed("**")
public class AuditoriaEnrutadorResourceServiceEjb
		extends AbstractResourceServiceEjb<AuditoriaEnrutadorResourceService, AuditoriaEnrutadorResource, Long>
		implements AuditoriaEnrutadorResourceService {

}
