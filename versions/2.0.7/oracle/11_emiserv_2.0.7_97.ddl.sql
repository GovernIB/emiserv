-- #97 La propietat es.caib.emiserv.log.dir no ha de ser editable
-- Changeset db/changelog/changes/2.0.7/2.0.7_008_log_dir_no_editable.yaml::2.0.7_001::limit
UPDATE ems_config SET source_property = 'FILE' WHERE key = 'es.caib.emiserv.log.dir';
