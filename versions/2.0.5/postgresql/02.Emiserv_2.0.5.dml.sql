-- 78
INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('PINBAL', 'GENERAL', 0, 'propietat.grup.pinbal');

INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.url', 'propietat.pinbal.url', 'PINBAL', 0, 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.username', 'propietat.pinbal.username', 'PINBAL', 1, 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.password', 'propietat.pinbal.password', 'PINBAL', 2, 'FILE', 'PASS');
