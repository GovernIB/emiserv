--------------------------------------------------------------------------
-- EMISERV Emmagatzematge de LOBs (moviment a tablespace propi)
--------------------------------------------------------------------------
-- Script generat a partir dels changelogs de Liquibase del projecte
-- (emiserv-persistence/src/main/resources/db/changelog), executant-los
-- amb el motor real de Liquibase 4.4.3 en mode "offline:oracle" contra
-- una base de dades buida. Representa la creacio completa de l'esquema
-- EMISERV des de zero, versio 2.0.6.
--
-- Execucio: com a usuari propietari de l'esquema (no WWW_EMISERV).
-- Requereix que el tablespace EMISERV_LOB ja existeixi (creat pel DBA)
-- i que emiserv_01_taules.sql s'hagi executat abans.
--------------------------------------------------------------------------

SET DEFINE OFF;
WHENEVER SQLERROR EXIT SQL.SQLCODE;

-- db/changelog/initial_schema_lob.yaml::lob-1
ALTER TABLE ems_backoffice_com MOVE LOB(peticio_xml) STORE AS ems_backcom_petxml_lob(TABLESPACE emiserv_lob INDEX ems_backcom_petxml_lob_i);

ALTER TABLE ems_backoffice_com MOVE LOB(resposta_xml) STORE AS ems_backcom_respxml_lob(TABLESPACE emiserv_lob INDEX ems_backcom_respxml_lob_i);

ALTER TABLE ems_backoffice_com MOVE LOB(error) STORE AS ems_backcom_error_lob(TABLESPACE emiserv_lob INDEX ems_backcom_error_lob_i);

-- db/changelog/initial_schema_lob.yaml::lob-2
ALTER TABLE ems_redir_missatge MOVE LOB(xml) STORE AS ems_redmis_xml_lob(TABLESPACE emiserv_lob INDEX ems_redmis_xml_lob_i);

-- db/changelog/initial_schema_lob.yaml::lob-3
ALTER TABLE ems_redir_peticio MOVE LOB(error) STORE AS ems_redpet_error_lob(TABLESPACE emiserv_lob INDEX ems_redpet_error_lob_i);

-- db/changelog/initial_schema_lob.yaml::lob-4
ALTER TABLE ems_redir_solicitud MOVE LOB(error) STORE AS ems_redsol_error_lob(TABLESPACE emiserv_lob INDEX ems_redsol_error_lob_i);

-- db/changelog/initial_schema_lob.yaml::lob-5
ALTER TABLE core_organismo_cesionario MOVE LOB(logo) STORE AS core_orgces_logo_lob(TABLESPACE emiserv_lob INDEX core_orgces_logo_lob_i);

-- db/changelog/initial_schema_lob.yaml::lob-6
ALTER TABLE core_token_data MOVE LOB(datos) STORE AS core_tokdat_datos_lob(TABLESPACE emiserv_lob INDEX core_tokdat_datos_lob_i);

-- db/changelog/initial_schema_lob.yaml::lob-7
ALTER TABLE core_transmision MOVE LOB(xmltransmision) STORE AS core_transm_xmltran_lob(TABLESPACE emiserv_lob INDEX core_transm_xmltran_lob_i);

-- db/changelog/initial_schema_lob.yaml::lob-8
ALTER TABLE core_em_codigo_error_secun MOVE LOB(descripcion) STORE AS core_emcoderrsec_desc_lob(TABLESPACE emiserv_lob INDEX core_emcoderrsec_desc_lob_i);

