RENAME hibernate_sequence TO ems_hibernate_sequence;

ALTER TABLE ems_usuari DROP COLUMN nom;
ALTER TABLE ems_usuari DROP COLUMN nif;
ALTER TABLE ems_usuari DROP COLUMN email;
ALTER TABLE ems_usuari DROP COLUMN inicialitzat;

UPDATE EMS_ACL_CLASS SET class='es.caib.emiserv.persist.entity.ServeiEntity' WHERE class='es.caib.emiserv.core.entity.ServeiEntity';