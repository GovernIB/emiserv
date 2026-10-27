-- URL de l'emisor SCSP d'EMISERV per a les rutes de tipus BACKOFFICE dels enrutadors
-- (si no s'informa: http://localhost:8080/emiservapi/interna/scsp/services/)
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.enrutador.emisor.url', 'propietat.enrutador.emisor.url', 'GENERAL', '12', 'DATABASE', 'TEXT');
COMMIT;
