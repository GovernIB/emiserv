-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 9/11/23 12:37
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/2.0.5/78.yaml::1693570839415-1::limit
CREATE TABLE ems_entitat (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64 CHAR) NOT NULL, nom VARCHAR2(255 CHAR) NOT NULL, cif VARCHAR2(9 CHAR) NOT NULL, unitat_arrel VARCHAR2(9 CHAR) NOT NULL, CONSTRAINT ems_entitat_pk PRIMARY KEY (id), UNIQUE (codi));

ALTER TABLE ems_redir_solicitud ADD solicitant_codi VARCHAR2(64 CHAR);

ALTER TABLE ems_redir_solicitud ADD titular_tipus_doc VARCHAR2(16 CHAR);

ALTER TABLE core_transmision ADD codigosolicitante VARCHAR2(64 CHAR);

ALTER TABLE core_transmision ADD tipodoctitular VARCHAR2(16 CHAR);

ALTER TABLE core_peticion_respuesta ADD emscodiprocediment VARCHAR2(256 CHAR);

ALTER TABLE core_peticion_respuesta ADD emsnomprocediment VARCHAR2(256 CHAR);

ALTER TABLE core_peticion_respuesta ADD emsestat INTEGER;

ALTER TABLE core_peticion_respuesta ADD emscodiservei VARCHAR2(64 CHAR);

ALTER TABLE core_peticion_respuesta ADD emsnomservei VARCHAR2(512 CHAR);

ALTER TABLE ems_redir_peticio ADD procediment_codi VARCHAR2(256 CHAR);

ALTER TABLE ems_redir_peticio ADD procediment_nom VARCHAR2(256 CHAR);

ALTER TABLE ems_redir_peticio ADD estatenum INTEGER;

INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('PINBAL', 'GENERAL', '0', 'propietat.grup.pinbal');

INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.url', 'propietat.pinbal.url', 'PINBAL', '0', 'FILE', 'TEXT');

INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.username', 'propietat.pinbal.username', 'PINBAL', '1', 'FILE', 'TEXT');

INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.password', 'propietat.pinbal.password', 'PINBAL', '2', 'FILE', 'PASS');

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_entitat TO www_emiserv;

UPDATE CORE_PETICION_RESPUESTA p SET p.emscodiprocediment = (select distinct ct.codigoprocedimiento from core_transmision ct where ct.idpeticion = p.idpeticion and 0 = (select count(idsolicitud) from core_transmision t where ct.idpeticion=idpeticion) or ct.idsolicitud = (select max(idsolicitud) from core_transmision where idpeticion=p.idpeticion));

UPDATE CORE_PETICION_RESPUESTA p SET p.emsnomprocediment = (select distinct ct.nombreprocedimiento from core_transmision ct where ct.idpeticion = p.idpeticion and 0 = (select count(idsolicitud) from core_transmision t where ct.idpeticion=idpeticion) or ct.idsolicitud = (select max(idsolicitud) from core_transmision where idpeticion=p.idpeticion));

UPDATE CORE_PETICION_RESPUESTA p SET p.emscodiservei = (select s.codcertificado from core_servicio s where s.id = p.certificado);

UPDATE CORE_PETICION_RESPUESTA p SET p.emsnomservei = (select s.descripcion from core_servicio s where s.id = p.certificado);

UPDATE CORE_PETICION_RESPUESTA p SET p.emsestat = (select case when (bp.id is not null and p.estado like '00%') then bp.estat when p.estado like '00%' then case when p.estado = '0001' then 0 when p.estado = '0002' then 1 when p.estado = '0003' then 2 when p.estado = '0004' then 3 end else 4 end from ems_backoffice_pet bp where bp.peticio_id = p.idpeticion);

UPDATE EMS_REDIR_PETICIO p SET p.procediment_codi = (select distinct rs.procediment_codi from ems_redir_solicitud rs where rs.peticio_id = p.id and rs.id = (select max(id) from ems_redir_solicitud where peticio_id=p.id));

UPDATE EMS_REDIR_PETICIO p SET p.procediment_nom = (select distinct rs.procediment_nom from ems_redir_solicitud rs where rs.peticio_id = p.id and rs.id = (select max(id) from ems_redir_solicitud where peticio_id=p.id));

UPDATE EMS_REDIR_PETICIO p SET p.estatenum = case when p.estat like '00%' then case when p.estat = '0001' then 0 when p.estat = '0002' then 1 when p.estat = '0003' then 2 when p.estat = '0004' then 3 end else 4 end;

-- Changeset db/changelog/changes/2.0.5/83.yaml::1693570839415-2::limit
ALTER TABLE ems_redir_missatge ADD entitat_codi VARCHAR2(64 CHAR);

ALTER TABLE ems_redir_peticio ADD entitat_codi VARCHAR2(64 CHAR);

