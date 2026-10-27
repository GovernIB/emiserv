-- Rutes dels serveis enrutadors amb consulta directa al backoffice del cedent
-- desti_tipus: 0 = EMISERV (reenviament a l'emisor SCSP del cedent), 1 = BACKOFFICE (consulta directa al backoffice)
ALTER TABLE ems_servei_ruta_desti ADD COLUMN desti_tipus INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE ems_servei_ruta_desti ADD COLUMN backcaib_autenticacio INTEGER;

-- Entitat (organisme cedent) que ha respost la petició
ALTER TABLE ems_backoffice_pet ADD COLUMN entitat_codi VARCHAR(64);
