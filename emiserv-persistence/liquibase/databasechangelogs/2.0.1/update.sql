-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 15/09/21 15:23
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/changes/2.0.1/17.yaml::1618396425533-1::limit
RENAME hibernate_sequence TO ems_hibernate_sequence;

-- Changeset db/changelog/changes/2.0.1/17.yaml::1618396425533-2::limit
ALTER TABLE ems_usuari DROP COLUMN nom;

ALTER TABLE ems_usuari DROP COLUMN nif;

ALTER TABLE ems_usuari DROP COLUMN email;

ALTER TABLE ems_usuari DROP COLUMN inicialitzat;

-- Changeset db/changelog/changes/2.0.1/17.yaml::1618396425533-3::limit
UPDATE EMS_ACL_CLASS SET class='es.caib.emiserv.persist.entity.ServeiEntity' WHERE class='es.caib.emiserv.core.entity.ServeiEntity';

