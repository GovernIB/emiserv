-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 18/9/23 16:33
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/2.0.5/78.yaml::1693570839415-1::limit
CREATE TABLE ems_entitat (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64 CHAR) NOT NULL, nom VARCHAR2(255 CHAR) NOT NULL, cif VARCHAR2(9 CHAR) NOT NULL, unitat_arrel VARCHAR2(9 CHAR) NOT NULL, CONSTRAINT ems_entitat_pk PRIMARY KEY (id), UNIQUE (codi));

ALTER TABLE ems_redir_solicitud ADD solicitant_codi VARCHAR2(64 CHAR);

ALTER TABLE ems_redir_solicitud ADD titular_tipus_doc VARCHAR2(16 CHAR);

ALTER TABLE core_transmision ADD codigosolicitante VARCHAR2(64 CHAR);

ALTER TABLE core_transmision ADD tipodoctitular VARCHAR2(16 CHAR);

INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('PINBAL', 'GENERAL', '0', 'propietat.grup.pinbal');

INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.url', 'propietat.pinbal.url', 'PINBAL', '0', 'FILE', 'TEXT');

INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.username', 'propietat.pinbal.username', 'PINBAL', '1', 'FILE', 'TEXT');

INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.password', 'propietat.pinbal.password', 'PINBAL', '2', 'FILE', 'PASS');

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_entitat TO www_emiserv;

-- Changeset db/changelog/changes/2.0.5/83.yaml::1693570839415-2::limit
ALTER TABLE ems_redir_missatge ADD entitat_codi VARCHAR2(64 CHAR);

ALTER TABLE ems_redir_peticio ADD entitat_codi VARCHAR2(64 CHAR);

