-- Activa les rutes cap al backoffice dels enrutadors que ja en tenen o que estan configurats a l'emisor SCSP
-- tipus: 1 = ENRUTADOR, 2 = ENRUTADOR_MULTIPLE; desti_tipus: 1 = BACKOFFICE
UPDATE ems_servei SET rutes_backoffice = 1
 WHERE tipus IN (1, 2)
   AND (EXISTS (SELECT 1 FROM ems_servei_ruta_desti r WHERE r.servei_id = ems_servei.id AND r.desti_tipus = 1)
     OR EXISTS (SELECT 1 FROM core_servicio cs WHERE cs.codcertificado = ems_servei.codi));
COMMIT;
