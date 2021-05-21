-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 21/05/21 09:52
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

