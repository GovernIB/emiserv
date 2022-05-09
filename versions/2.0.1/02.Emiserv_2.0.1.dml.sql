-- #17
UPDATE EMS_ACL_CLASS SET class='es.caib.emiserv.persist.entity.ServeiEntity' WHERE class='es.caib.emiserv.core.entity.ServeiEntity';
UPDATE ems_servei SET resolver_class = 'es.caib.emiserv.logic.resolver.SampleEntitatResolver' WHERE resolver_class = 'es.caib.emiserv.core.resolver.SampleEntitatResolver';
UPDATE ems_servei SET resolver_class = 'es.caib.emiserv.logic.resolver.EmpadronamentEntitatResolver' WHERE resolver_class = 'es.caib.emiserv.core.resolver.EmpadronamentEntitatResolver';
UPDATE ems_servei SET response_resolver_class = 'es.caib.emiserv.logic.resolver.SampleResponseResolver' WHERE resolver_class = 'es.caib.emiserv.core.resolver.SampleResponseResolver';
UPDATE ems_servei SET response_resolver_class = 'es.caib.emiserv.logic.resolver.FamiliaNombrosaResponseResolver' WHERE resolver_class = 'es.caib.emiserv.core.resolver.FamiliaNombrosaResponseResolver';
