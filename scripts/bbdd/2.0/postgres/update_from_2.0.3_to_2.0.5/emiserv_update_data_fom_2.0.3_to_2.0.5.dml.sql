INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('PINBAL', 'GENERAL', 0, 'propietat.grup.pinbal');

INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.url', 'propietat.pinbal.url', 'PINBAL', 0, 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.username', 'propietat.pinbal.username', 'PINBAL', 1, 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.password', 'propietat.pinbal.password', 'PINBAL', 2, 'FILE', 'PASS');

UPDATE CORE_PETICION_RESPUESTA p SET p.emscodiprocediment = (select distinct ct.codigoprocedimiento from core_transmision ct where ct.idpeticion = p.idpeticion and 0 = (select count(idsolicitud) from core_transmision t where ct.idpeticion=idpeticion) or ct.idsolicitud = (select max(idsolicitud) from core_transmision where idpeticion=p.idpeticion));
UPDATE CORE_PETICION_RESPUESTA p SET p.emsnomprocediment = (select distinct ct.nombreprocedimiento from core_transmision ct where ct.idpeticion = p.idpeticion and 0 = (select count(idsolicitud) from core_transmision t where ct.idpeticion=idpeticion) or ct.idsolicitud = (select max(idsolicitud) from core_transmision where idpeticion=p.idpeticion));
UPDATE CORE_PETICION_RESPUESTA p SET p.emscodiservei = (select s.codcertificado from core_servicio s where s.id = p.certificado);
UPDATE CORE_PETICION_RESPUESTA p SET p.emsnomservei = (select s.descripcion from core_servicio s where s.id = p.certificado);
UPDATE CORE_PETICION_RESPUESTA p SET p.emsestat = (select case when (bp.id is not null and p.estado like '00%') then bp.estat when p.estado like '00%' then case when p.estado = '0001' then 0 when p.estado = '0002' then 1 when p.estado = '0003' then 2 when p.estado = '0004' then 3 end else 4 end from ems_backoffice_pet bp where bp.peticio_id = p.idpeticion);

UPDATE EMS_REDIR_PETICIO p SET p.procediment_codi = (select distinct rs.procediment_codi from ems_redir_solicitud rs where rs.peticio_id = p.id and rs.id = (select max(id) from ems_redir_solicitud where peticio_id=p.id));
UPDATE EMS_REDIR_PETICIO p SET p.procediment_nom = (select distinct rs.procediment_nom from ems_redir_solicitud rs where rs.peticio_id = p.id and rs.id = (select max(id) from ems_redir_solicitud where peticio_id=p.id));
UPDATE EMS_REDIR_PETICIO p SET p.estatenum = case when p.estat like '00%' then case when p.estat = '0001' then 0 when p.estat = '0002' then 1 when p.estat = '0003' then 2 when p.estat = '0004' then 3 end else 4 end;
