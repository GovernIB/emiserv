-- Consultes asíncrones dels serveis enrutadors processades com a peticions síncrones a cada cedent
ALTER TABLE ems_servei ADD async_sincron NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE ems_servei ADD async_interval INTEGER;
ALTER TABLE ems_servei ADD async_concurrencia INTEGER;

ALTER TABLE ems_servei_ruta_desti ADD pausada NUMBER(1) DEFAULT 0 NOT NULL;

ALTER TABLE ems_backoffice_pet ADD pausada NUMBER(1) DEFAULT 0 NOT NULL;

ALTER TABLE ems_backoffice_sol ADD entitat_codi VARCHAR2(64 CHAR);
ALTER TABLE ems_backoffice_sol ADD peticio_desti_id VARCHAR2(26 CHAR);
ALTER TABLE ems_backoffice_sol ADD intents INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE ems_backoffice_sol ADD data_enviament TIMESTAMP;
ALTER TABLE ems_backoffice_sol ADD data_proper_intent TIMESTAMP;
ALTER TABLE ems_backoffice_sol ADD darrer_error VARCHAR2(4000 CHAR);
ALTER TABLE ems_backoffice_sol ADD error_codi VARCHAR2(16 CHAR);

-- IdPeticion de les peticions enviades als cedents
CREATE SEQUENCE ems_async_idpet_seq START WITH 1;
GRANT SELECT ON ems_async_idpet_seq TO www_emiserv;
