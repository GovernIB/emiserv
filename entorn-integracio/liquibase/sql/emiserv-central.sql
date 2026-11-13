-- EMISERV central (CAIB): emissor S0711001H. PINBAL hi envia les consultes de padró
-- (SCDCPAJU, encaminades segons el municipi) i les del backoffice propi de la CAIB
-- (SVDCCAACPCWS01). Ids a partir de 900000 per no topar amb les seqüències.

-- Paràmetres SCSP: keystore muntat pel compose, sense @firma (no hi ha cap servei de validació)
UPDATE core_parametro_configuracion SET valor = 'file:/home/jboss/keystores/interoperabilitat.jks' WHERE nombre = 'keystoreFile';
UPDATE core_parametro_configuracion SET valor = 'tecnologies' WHERE nombre = 'keystorePass';
UPDATE core_parametro_configuracion SET valor = 'false' WHERE nombre = 'afirma.enabled';
UPDATE core_parametro_configuracion SET valor = 'es.scsp.common.certificates.revocation.IgnoreRevocationManager' WHERE nombre = 'custom.cert.validation.class';
UPDATE core_parametro_configuracion SET valor = 'S0711001H' WHERE nombre = 'cif.emisor';
UPDATE core_parametro_configuracion SET valor = 'Govern de les Illes Balears' WHERE nombre = 'nombre.emisor';
UPDATE core_parametro_configuracion SET valor = 'EMS' WHERE nombre = 'prefijo.idtransmision';
UPDATE core_parametro_configuracion SET valor = 'false' WHERE nombre = 'validate.nif.emisor.enabled';

INSERT INTO core_emisor_certificado (id, nombre, cif, fechaalta)
VALUES (900001, 'Govern de les Illes Balears', 'S0711001H', TIMESTAMP '2024-01-01 00:00:00');

-- Clau de signatura de les respostes: certificat limit_emiserv del keystore d'interoperabilitat
INSERT INTO core_clave_privada (id, alias, nombre, password, numeroserie, fechaalta, interoperabilidad, bloqueado)
VALUES (900001, 'limit_emiserv', 'EMISERV central (limit_emiserv)', 'tecnologies', '6044277137025042875', TIMESTAMP '2024-01-01 00:00:00', 0, 0);

-- Autoritat dels certificats de prova (autosignats), per si s'activa el mòdul ValidarCertificado
INSERT INTO core_em_autorizacion_ca (id, codca, nombre)
VALUES (900001, 'CN=Limit Tecnologies, OU=Departament de programari, O=Limit, L=Manacor, ST=Illes Balears, C=ES', 'Limit Tecnologies (proves)');

-- Padró: enrutador per municipi (provincia + municipi INE, EmpadronamentEntitatResolver)
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios,
    maxsolicitudespeticion, emisor, clavefirma, fechaalta, caducidad, timeout, validacionfirma)
VALUES (900001, 'SCDCPAJU', 'Servei de consulta de padró de convivència', 'V3', 'XMLSignature', 0,
    0, 900001, 900001, TIMESTAMP '2024-01-01 00:00:00', 0, 60, 'estricto');
-- Les llibreries SCSP necessiten el backoffice també als serveis d'enrutador (EMISERV hi encamina)
INSERT INTO core_em_backoffice (certificado, classname, ter)
VALUES (900001, 'es.caib.emiserv.backoffice.CaibBackoffice', 1);
INSERT INTO ems_servei (id, codi, nom, tipus, descripcio, configurat, actiu, resolver_class, backcaib_async_ter,
    xsd_activa, async_sincron, rutes_backoffice, createdby_codi, createddate, version)
VALUES (900001, 'SCDCPAJU', 'Servei de consulta de padró de convivència', 1,
    'Encaminat a l''ajuntament del municipi consultat', 1, 1, 'es.caib.emiserv.logic.resolver.EmpadronamentEntitatResolver', 0,
    0, 0, 1, 'integracio', SYSDATE, 0);
-- 07040 Manacor: EMISERV de l'ajuntament (cedent amb backoffice propi)
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, pausada, createdby_codi, createddate, version)
VALUES (900001, 900001, '07040', 0, 'http://emiserv-cedent:8080/emiservapi/interna/scsp/services/peticionSincrona', 0, 0, 'integracio', SYSDATE, 0);
-- 07026 Inca: emissor SCSP d'un altre fabricant (simulat)
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, pausada, createdby_codi, createddate, version)
VALUES (900002, 900001, '07026', 1, 'http://emiserv-scsp-fake:18080/scsp/SCDCPAJU/07026', 0, 0, 'integracio', SYSDATE, 0);
-- 07011 Calvià: connexió directa al backoffice de l'ajuntament (sense emissor SCSP)
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, backcaib_autenticacio, pausada, createdby_codi, createddate, version)
VALUES (900003, 900001, '07011', 2, 'http://emiserv-scsp-fake:18080/ws/EmiservBackoffice', 1, 0, 0, 'integracio', SYSDATE, 0);
-- 07033 Llucmajor: emissor que respon amb error (SOAP Fault)
INSERT INTO ems_servei_ruta_desti (id, servei_id, entitat_codi, ordre, url, desti_tipus, pausada, createdby_codi, createddate, version)
VALUES (900004, 900001, '07033', 3, 'http://emiserv-scsp-fake:18080/scsp/SCDCPAJU/07033/ko', 0, 0, 'integracio', SYSDATE, 0);

-- Backoffice propi de la CAIB (simulat pel fake)
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios,
    maxsolicitudespeticion, emisor, clavefirma, fechaalta, caducidad, timeout, validacionfirma)
VALUES (900002, 'SVDCCAACPCWS01', 'Estar al corrent de pagament per contractacions', 'V3', 'WS-Security', 0,
    0, 900001, 900001, TIMESTAMP '2024-01-01 00:00:00', 0, 60, 'estricto');
INSERT INTO core_em_backoffice (certificado, classname, ter)
VALUES (900002, 'es.caib.emiserv.backoffice.CaibBackoffice', 1);
INSERT INTO ems_servei (id, codi, nom, tipus, descripcio, configurat, actiu, backcaib_url, backcaib_autenticacio,
    backcaib_async_tipus, backcaib_async_ter, backoffice_class, xsd_activa, async_sincron, createdby_codi, createddate, version)
VALUES (900002, 'SVDCCAACPCWS01', 'Estar al corrent de pagament per contractacions', 0,
    'Backoffice de la CAIB', 1, 1, 'http://emiserv-scsp-fake:18080/ws/EmiservBackoffice', 0,
    0, 0, 'es.caib.emiserv.backoffice.CaibBackoffice', 0, 0, 'integracio', SYSDATE, 0);
