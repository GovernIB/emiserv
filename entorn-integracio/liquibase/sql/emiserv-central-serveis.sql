-- La resta de serveis de l'EMISERV de proves de la CAIB (proves.caib.es), amb el mateix tipus i la
-- mateixa configuració, però amb els destins simulats: un enrutador més (SCDHPAJU), un enrutador
-- múltiple (SVDSCTFNWS01) i els backoffices. Tipus de servei: 0 BACKOFFICE, 1 ENRUTADOR,
-- 2 ENRUTADOR_MULTIPLE; autenticació: 0 NINGUNA, 2 TEXT_CLAR (credencials a
-- es.caib.emiserv.backoffice.caib.auth.*, al compose); asíncrones: 0 REENVIAR_BACKOFFICE,
-- 1 PROCESSAR_SINCRONES; ruta: 0 emissor SCSP, 1 backoffice.

-- SCDCPAJU: el resolver de respostes de producció
UPDATE ems_servei SET response_resolver_class = 'es.caib.emiserv.logic.resolver.SampleResponseResolver' WHERE codi = 'SCDCPAJU';
-- SVDCCAACPCWS01: com a producció, signatura XML i autenticació en text clar
UPDATE core_servicio SET tiposeguridad = 'XMLSignature' WHERE codcertificado = 'SVDCCAACPCWS01';
UPDATE ems_servei SET backcaib_autenticacio = 2 WHERE codi = 'SVDCCAACPCWS01';

-- SCDHPAJU: padró històric, enrutat per municipi amb les mateixes rutes que SCDCPAJU
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios,
    maxsolicitudespeticion, emisor, clavefirma, fechaalta, caducidad, timeout, validacionfirma)
VALUES (900003, 'SCDHPAJU', 'Servei de consulta de padró històric', 'V3', 'XMLSignature', 0,
    0, 900001, 900001, TIMESTAMP '2024-01-01 00:00:00', 0, 60, 'estricto');
INSERT INTO core_em_backoffice (certificado, classname, ter)
VALUES (900003, 'es.caib.emiserv.backoffice.CaibBackoffice', 1);
INSERT INTO ems_servei (id, codi, nom, tipus, descripcio, configurat, actiu, resolver_class, response_resolver_class,
    backcaib_async_ter, xsd_activa, async_sincron, rutes_backoffice, createdby_codi, createddate, version)
VALUES (900003, 'SCDHPAJU', 'Servei de consulta de padró històric', 1,
    'Encaminat a l''ajuntament del municipi consultat', 1, 1, 'es.caib.emiserv.logic.resolver.EmpadronamentEntitatResolver',
    'es.caib.emiserv.logic.resolver.SampleResponseResolver', 0, 0, 0, 1, 'integracio', SYSDATE, 0);
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, pausada, createdby_codi, createddate, version)
VALUES (900011, 900003, '07040', 0, 'http://emiserv-cedent:8080/emiservapi/interna/scsp/services/peticionSincrona', 0, 0, 'integracio', SYSDATE, 0);
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, pausada, createdby_codi, createddate, version)
VALUES (900012, 900003, '07026', 1, 'http://emiserv-scsp-fake:18080/scsp/SCDHPAJU/07026', 0, 0, 'integracio', SYSDATE, 0);
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, backcaib_autenticacio, pausada, createdby_codi, createddate, version)
VALUES (900013, 900003, '07011', 2, 'http://emiserv-scsp-fake:18080/ws/EmiservBackoffice', 1, 0, 0, 'integracio', SYSDATE, 0);
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, pausada, createdby_codi, createddate, version)
VALUES (900014, 900003, '07033', 3, 'http://emiserv-scsp-fake:18080/scsp/SCDHPAJU/07033/ko', 0, 0, 'integracio', SYSDATE, 0);

-- SVDSCTFNWS01: família nombrosa, enrutador múltiple. Es consulten tots els destins i
-- FamiliaNombrosaResponseResolver tria la resposta (la del títol vigent que caduca més tard)
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios,
    maxsolicitudespeticion, emisor, clavefirma, fechaalta, caducidad, timeout, validacionfirma)
VALUES (900004, 'SVDSCTFNWS01', 'Servei de consulta de familia nombrosa', 'V3', 'XMLSignature', 0,
    0, 900001, 900001, TIMESTAMP '2024-01-01 00:00:00', 0, 60, 'estricto');
INSERT INTO core_em_backoffice (certificado, classname, ter)
VALUES (900004, 'es.caib.emiserv.backoffice.CaibBackoffice', 1);
INSERT INTO ems_servei (id, codi, nom, tipus, descripcio, configurat, actiu, resolver_class, response_resolver_class,
    backcaib_async_ter, xsd_activa, async_sincron, rutes_backoffice, createdby_codi, createddate, version)
VALUES (900004, 'SVDSCTFNWS01', 'Servei de consulta de familia nombrosa', 2,
    'Consulta als consells insulars', 1, 1, 'es.caib.emiserv.logic.resolver.SampleEntitatResolver',
    'es.caib.emiserv.logic.resolver.FamiliaNombrosaResponseResolver', 0, 0, 0, 1, 'integracio', SYSDATE, 0);
-- MALLORCA: emissor SCSP amb títol; MENORCA: emissor SCSP sense dades; EIVISSA: backoffice directe
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, pausada, createdby_codi, createddate, version)
VALUES (900021, 900004, 'MALLORCA', 0, 'http://emiserv-scsp-fake:18080/scsp/SVDSCTFNWS01/MALLORCA', 0, 0, 'integracio', SYSDATE, 0);
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, pausada, createdby_codi, createddate, version)
VALUES (900022, 900004, 'MENORCA', 1, 'http://emiserv-scsp-fake:18080/scsp/SVDSCTFNWS01/MENORCA/no', 0, 0, 'integracio', SYSDATE, 0);
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, backcaib_autenticacio, pausada, createdby_codi, createddate, version)
VALUES (900023, 900004, 'EIVISSA', 2, 'http://emiserv-scsp-fake:18080/ws/EmiservBackoffice/EIVISSA', 1, 0, 0, 'integracio', SYSDATE, 0);

-- Backoffices de la CAIB (tots simulats pel backoffice del fake)
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios,
    maxsolicitudespeticion, emisor, clavefirma, fechaalta, caducidad, timeout, validacionfirma)
SELECT 900000 + n, codi, nom, 'V3', 'XMLSignature', 0, 0, 900001, 900001, TIMESTAMP '2024-01-01 00:00:00', 0, 60, 'estricto'
FROM (
    SELECT 5 n, 'SCMCEDU' codi, 'Servei de consulta d''escolarització' nom FROM dual UNION ALL
    SELECT 6, 'SVDCCAACPASWS01', 'Estar al corrent de pagament per ajudes' FROM dual UNION ALL
    SELECT 7, 'SVDCCAAIRPHWS01', 'Consulta dades de parelles de fet' FROM dual UNION ALL
    SELECT 8, 'SVDNIVCAT', 'Consulta de nivell de Català' FROM dual UNION ALL
    SELECT 9, 'SVDSCDDWS01', 'Servei de consulta de dades de discapacitat' FROM dual UNION ALL
    SELECT 10, 'SVDSCDDWS02', 'Servei de consulta de dades de discapacitat V2' FROM dual UNION ALL
    SELECT 11, 'SVDSCDWS01', 'Servei de Canvi de Domicili' FROM dual);
INSERT INTO core_em_backoffice (certificado, classname, ter)
SELECT id, 'es.caib.emiserv.backoffice.CaibBackoffice', 1 FROM core_servicio WHERE id BETWEEN 900005 AND 900011;
INSERT INTO ems_servei (id, codi, nom, tipus, descripcio, configurat, actiu, backcaib_url, backcaib_autenticacio,
    backcaib_async_tipus, backcaib_async_ter, backoffice_class, xsd_activa, async_sincron, createdby_codi, createddate, version)
SELECT s.id, s.codcertificado, s.descripcion, 0, 'Backoffice de la CAIB', 1, 1,
    'http://emiserv-scsp-fake:18080/ws/EmiservBackoffice', c.autenticacio, c.async_tipus, c.async_ter,
    'es.caib.emiserv.backoffice.CaibBackoffice', 0, 0, 'integracio', SYSDATE, 0
FROM core_servicio s JOIN (
    SELECT 'SCMCEDU' codi, 0 autenticacio, 0 async_tipus, 0 async_ter FROM dual UNION ALL
    SELECT 'SVDCCAACPASWS01', 2, 0, 0 FROM dual UNION ALL
    SELECT 'SVDCCAAIRPHWS01', 0, 0, 0 FROM dual UNION ALL
    SELECT 'SVDNIVCAT', 2, 0, 0 FROM dual UNION ALL
    SELECT 'SVDSCDDWS01', 2, 0, 0 FROM dual UNION ALL
    SELECT 'SVDSCDDWS02', 2, 0, 0 FROM dual UNION ALL
    -- Les peticions asíncrones es processen com a síncrones al cap de 60 segons
    SELECT 'SVDSCDWS01', 0, 1, 60 FROM dual) c ON c.codi = s.codcertificado;
