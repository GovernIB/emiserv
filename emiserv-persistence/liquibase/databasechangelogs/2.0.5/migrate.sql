-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 1/9/23 14:26
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/2.0.5/83.yaml::1693570839415-1::limit
ALTER TABLE ems_redir_missatge ADD entitat_codi VARCHAR2(64 CHAR);
ALTER TABLE ems_redir_peticio ADD entitat_codi VARCHAR2(64 CHAR);

