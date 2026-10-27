-- Consultes asíncrones dels serveis enrutadors processades com a peticions síncrones a cada cedent
ALTER TABLE ems_servei ADD COLUMN async_sincron BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE ems_servei ADD COLUMN async_interval INTEGER;
ALTER TABLE ems_servei ADD COLUMN async_concurrencia INTEGER;

ALTER TABLE ems_servei_ruta_desti ADD COLUMN pausada BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE ems_backoffice_pet ADD COLUMN pausada BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE ems_backoffice_sol ADD COLUMN entitat_codi VARCHAR(64);
ALTER TABLE ems_backoffice_sol ADD COLUMN peticio_desti_id VARCHAR(26);
ALTER TABLE ems_backoffice_sol ADD COLUMN intents INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE ems_backoffice_sol ADD COLUMN data_enviament TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE ems_backoffice_sol ADD COLUMN data_proper_intent TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE ems_backoffice_sol ADD COLUMN darrer_error VARCHAR(4000);
ALTER TABLE ems_backoffice_sol ADD COLUMN error_codi VARCHAR(16);

-- IdPeticion de les peticions enviades als cedents
CREATE SEQUENCE ems_async_idpet_seq START WITH 1;
