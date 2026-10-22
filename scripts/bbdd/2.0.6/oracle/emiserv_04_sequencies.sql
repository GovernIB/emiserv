--------------------------------------------------------------------------
-- EMISERV Sequencies
--------------------------------------------------------------------------
-- Script generat a partir dels changelogs de Liquibase del projecte
-- (emiserv-persistence/src/main/resources/db/changelog), executant-los
-- amb el motor real de Liquibase 4.4.3 en mode "offline:oracle" contra
-- una base de dades buida. Representa la creacio completa de l'esquema
-- EMISERV des de zero, versio 2.0.6.
--
-- Execucio: com a usuari propietari de l'esquema (no WWW_EMISERV).
--------------------------------------------------------------------------

SET DEFINE OFF;
WHENEVER SQLERROR EXIT SQL.SQLCODE;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-1
CREATE SEQUENCE ems_hibernate_sequence START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-2
CREATE SEQUENCE ems_acl_sid_seq START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-3
CREATE SEQUENCE ems_acl_class_seq START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-4
CREATE SEQUENCE ems_acl_oid_seq START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-5
CREATE SEQUENCE ems_acl_entry_seq START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-6
CREATE SEQUENCE id_clave_privada_sequence START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-7
CREATE SEQUENCE id_clave_publica_sequence START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-8
CREATE SEQUENCE id_emisor_sequence START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-9
CREATE SEQUENCE id_autorizacion_organismo_seq START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-10
CREATE SEQUENCE id_organismo_cesionario_seq START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-11
CREATE SEQUENCE id_servicio_sequence START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-12
CREATE SEQUENCE id_aplicacion_sequence START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-13
CREATE SEQUENCE id_autorizacion_ca_seq START WITH 18;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-14
CREATE SEQUENCE id_autorizacion_certific_seq START WITH 1;

-- db/changelog/initial_schema_sequence.yaml::init-sequence-15
CREATE SEQUENCE id_servicio_cesionario_seq START WITH 1;

