--------------------------------------------------------------------------
-- EMISERV Index addicionals
--------------------------------------------------------------------------
-- Script generat a partir dels changelogs de Liquibase del projecte
-- (emiserv-persistence/src/main/resources/db/changelog), executant-los
-- amb el motor real de Liquibase 4.4.3 en mode "offline:oracle" contra
-- una base de dades buida. Representa la creacio completa de l'esquema
-- EMISERV des de zero, versio 2.0.6.
--
-- Execucio: com a usuari propietari de l'esquema (no WWW_EMISERV).
-- Requereix que emiserv_01_taules.sql s'hagi executat abans.
--------------------------------------------------------------------------

SET DEFINE OFF;
WHENEVER SQLERROR EXIT SQL.SQLCODE;

-- db/changelog/initial_schema_index.yaml::init-index-1
CREATE UNIQUE INDEX ems_servei_codi_i ON ems_servei(codi);

-- db/changelog/initial_schema_index.yaml::init-index-2
CREATE INDEX core_servicio_index_emisor ON core_servicio(emisor);

-- db/changelog/initial_schema_index.yaml::init-index-3
CREATE INDEX core_transmision_index_idpet ON core_transmision(idpeticion);

-- db/changelog/initial_schema_index.yaml::init-index-4
CREATE UNIQUE INDEX unique_serv_apl_org_baja ON core_em_autorizacion_cert(idcertificado, idorganismo, idaplicacion, fechabaja);

-- db/changelog/initial_schema_index.yaml::init-index-5
CREATE INDEX cesionarios_index_organismo ON core_req_cesionarios_servicios(organismo);

