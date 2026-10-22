--------------------------------------------------------------------------
-- EMISERV Grants per a l'usuari WWW_EMISERV
--------------------------------------------------------------------------
-- Script generat a partir dels changelogs de Liquibase del projecte
-- (emiserv-persistence/src/main/resources/db/changelog), executant-los
-- amb el motor real de Liquibase 4.4.3 en mode "offline:oracle" contra
-- una base de dades buida. Representa la creacio completa de l'esquema
-- EMISERV des de zero, versio 2.0.6.
--
-- Execucio: com a usuari propietari de l'esquema (no WWW_EMISERV).
-- Requereix que l'usuari/rol WWW_EMISERV ja existeixi (creat pel DBA)
-- i que emiserv_01_taules.sql i emiserv_04_sequencies.sql s'hagin executat abans.
--------------------------------------------------------------------------

SET DEFINE OFF;
WHENEVER SQLERROR EXIT SQL.SQLCODE;

-- db/changelog/initial_schema_grant.yaml::init-trigger-1
GRANT SELECT, UPDATE, INSERT, DELETE ON ems_backoffice_com TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_backoffice_pet TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_backoffice_sol TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_redir_missatge TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_redir_peticio TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_redir_solicitud TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_servei TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_servei_ruta_desti TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_usuari TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_acl_sid TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_acl_class TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_acl_object_identity TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_acl_entry TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_cache_certificados TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_clave_privada TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_clave_publica TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_codigo_error TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_emisor_certificado TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_estado_peticion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_modulo TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_modulo_configuracion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_req_modulo_pdf TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_req_modulo_pdf_cesionario TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_autorizacion_organismo TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_organismo_cesionario TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_parametro_configuracion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_peticion_respuesta TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_servicio TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_tipo_mensaje TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_token_data TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_transmision TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_aplicacion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_autorizacion_ca TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_autorizacion_cert TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_codigo_error_secun TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_backoffice TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_secuencia_idtrans TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_req_secuencia_id_peticion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_req_cesionarios_servicios TO www_emiserv;

GRANT SELECT ON ems_hibernate_sequence TO www_emiserv;

GRANT SELECT ON ems_acl_sid_seq TO www_emiserv;

GRANT SELECT ON ems_acl_class_seq TO www_emiserv;

GRANT SELECT ON ems_acl_oid_seq TO www_emiserv;

GRANT SELECT ON ems_acl_entry_seq TO www_emiserv;

GRANT SELECT ON id_clave_privada_sequence TO www_emiserv;

GRANT SELECT ON id_clave_publica_sequence TO www_emiserv;

GRANT SELECT ON id_emisor_sequence TO www_emiserv;

GRANT SELECT ON id_autorizacion_organismo_seq TO www_emiserv;

GRANT SELECT ON id_organismo_cesionario_seq TO www_emiserv;

GRANT SELECT ON id_servicio_sequence TO www_emiserv;

GRANT SELECT ON id_aplicacion_sequence TO www_emiserv;

GRANT SELECT ON id_autorizacion_ca_seq TO www_emiserv;

GRANT SELECT ON id_autorizacion_certific_seq TO www_emiserv;

GRANT SELECT ON id_servicio_cesionario_seq TO www_emiserv;

-- db/changelog/changes/2.0.3/36.yaml::1643536278328-1
grant select, update, insert, delete on ems_config_group to www_emiserv;

grant select, update, insert, delete on ems_config_type to www_emiserv;

grant select, update, insert, delete on ems_config to www_emiserv;

-- db/changelog/changes/2.0.5/78.yaml::1693570839415-1
GRANT SELECT, UPDATE, INSERT, DELETE ON ems_entitat TO www_emiserv;

