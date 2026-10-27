-- Rutes dels serveis enrutadors amb consulta directa al backoffice del cedent
-- desti_tipus: 0 = EMISERV (reenviament a l'emisor SCSP del cedent), 1 = BACKOFFICE (consulta directa al backoffice)
ALTER TABLE ems_servei_ruta_desti ADD desti_tipus INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE ems_servei_ruta_desti ADD backcaib_autenticacio INTEGER;

-- Entitat (organisme cedent) que ha respost la petició
ALTER TABLE ems_backoffice_pet ADD entitat_codi VARCHAR2(64 CHAR);
