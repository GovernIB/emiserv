--------------------------------------------------------------------------
-- EMISERV Triggers
--------------------------------------------------------------------------
-- Script generat a partir dels changelogs de Liquibase del projecte
-- (emiserv-persistence/src/main/resources/db/changelog), executant-los
-- amb el motor real de Liquibase 4.4.3 en mode "offline:oracle" contra
-- una base de dades buida. Representa la creacio completa de l'esquema
-- EMISERV des de zero, versio 2.0.6.
--
-- Execucio: com a usuari propietari de l'esquema (no WWW_EMISERV).
-- Requereix que emiserv_04_sequencies.sql s'hagi executat abans (usen NEXTVAL).
--------------------------------------------------------------------------

SET DEFINE OFF;
WHENEVER SQLERROR EXIT SQL.SQLCODE;

-- db/changelog/initial_schema_trigger.yaml::init-trigger-1
CREATE OR REPLACE TRIGGER ems_acl_sid_idgen BEFORE INSERT ON ems_acl_sid FOR EACH ROW BEGIN SELECT ems_acl_sid_seq.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER ems_acl_class_idgen BEFORE INSERT ON ems_acl_class FOR EACH ROW BEGIN SELECT ems_acl_class_seq.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER ems_acl_oid_idgen BEFORE INSERT ON ems_acl_object_identity FOR EACH ROW BEGIN SELECT ems_acl_oid_seq.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER ems_acl_entry_idgen BEFORE INSERT ON ems_acl_entry FOR EACH ROW BEGIN SELECT ems_acl_entry_seq.NEXTVAL INTO :NEW.ID FROM DUAL;
END;
/

