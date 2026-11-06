package es.caib.emiserv.ejb.resourceService;

import es.caib.emiserv.ejb.base.AbstractResourceServiceEjb;
import es.caib.emiserv.logic.intf.model.AuditoriaBackofficeResource;
import es.caib.emiserv.logic.intf.service.AuditoriaBackofficeResourceService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * EJB del servei del recurs d'auditoria AuditoriaBackofficeResource. Els permisos es comproven al controlador
 * REST (hasPermission) i a la consulta delegada (serveis permesos de l'usuari).
 *
 * @author Limit Tecnologies
 */
@Stateless(name = "AuditoriaBackofficeResourceService")
@RolesAllowed("**")
public class AuditoriaBackofficeResourceServiceEjb
		extends AbstractResourceServiceEjb<AuditoriaBackofficeResourceService, AuditoriaBackofficeResource, String>
		implements AuditoriaBackofficeResourceService {

}
