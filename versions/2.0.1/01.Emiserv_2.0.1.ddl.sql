-- #17
RENAME hibernate_sequence TO ems_hibernate_sequence;
ALTER TABLE ems_usuari DROP COLUMN nom;
ALTER TABLE ems_usuari DROP COLUMN nif;
ALTER TABLE ems_usuari DROP COLUMN email;
ALTER TABLE ems_usuari DROP COLUMN inicialitzat;

