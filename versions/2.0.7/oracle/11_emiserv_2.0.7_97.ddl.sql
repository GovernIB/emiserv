-- Tema de la interfície REACT per usuari (CLAR, OBSCUR, DRACULA o SISTEMA)
-- Changeset db/changelog/changes/2.0.7/97.yaml::2.0.7_001::limit
UPDATE ems_config SET source_property = 'FILE' WHERE key = 'es.caib.emiserv.log.dir';
