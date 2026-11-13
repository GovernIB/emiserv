-- EMISERV de l'Ajuntament de Manacor (P0704000A): emissor del padró (SCDCPAJU) amb el
-- backoffice municipal (simulat pel fake). Hi arriben les peticions que l'EMISERV central
-- encamina per al municipi 07040.

UPDATE core_parametro_configuracion SET valor = 'file:/home/jboss/keystores/interoperabilitat.jks' WHERE nombre = 'keystoreFile';
UPDATE core_parametro_configuracion SET valor = 'tecnologies' WHERE nombre = 'keystorePass';
UPDATE core_parametro_configuracion SET valor = 'false' WHERE nombre = 'afirma.enabled';
UPDATE core_parametro_configuracion SET valor = 'es.scsp.common.certificates.revocation.IgnoreRevocationManager' WHERE nombre = 'custom.cert.validation.class';
UPDATE core_parametro_configuracion SET valor = 'P0704000A' WHERE nombre = 'cif.emisor';
UPDATE core_parametro_configuracion SET valor = 'Ajuntament de Manacor' WHERE nombre = 'nombre.emisor';
UPDATE core_parametro_configuracion SET valor = 'MAN' WHERE nombre = 'prefijo.idtransmision';
-- Les peticions duen el NIF de l'emissor que coneix PINBAL (la CAIB), no el de l'ajuntament
UPDATE core_parametro_configuracion SET valor = 'false' WHERE nombre = 'validate.nif.emisor.enabled';

INSERT INTO core_emisor_certificado (id, nombre, cif, fechaalta)
VALUES (900001, 'Ajuntament de Manacor', 'P0704000A', TIMESTAMP '2024-01-01 00:00:00');

INSERT INTO core_clave_privada (id, alias, nombre, password, numeroserie, fechaalta, interoperabilidad, bloqueado)
VALUES (900001, 'limit_emiserv', 'EMISERV Manacor (limit_emiserv)', 'tecnologies', '6044277137025042875', TIMESTAMP '2024-01-01 00:00:00', 0, 0);

INSERT INTO core_em_autorizacion_ca (id, codca, nombre)
VALUES (900001, 'CN=Limit Tecnologies, OU=Departament de programari, O=Limit, L=Manacor, ST=Illes Balears, C=ES', 'Limit Tecnologies (proves)');

INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios,
    maxsolicitudespeticion, emisor, clavefirma, fechaalta, caducidad, timeout, validacionfirma)
VALUES (900001, 'SCDCPAJU', 'Servei de consulta de padró de convivència', 'V3', 'XMLSignature', 0,
    0, 900001, 900001, TIMESTAMP '2024-01-01 00:00:00', 0, 60, 'estricto');
INSERT INTO core_em_backoffice (certificado, classname, ter)
VALUES (900001, 'es.caib.emiserv.backoffice.CaibBackoffice', 1);
INSERT INTO ems_servei (id, codi, nom, tipus, descripcio, configurat, actiu, backcaib_url, backcaib_autenticacio,
    backcaib_async_tipus, backcaib_async_ter, backoffice_class, xsd_activa, async_sincron, createdby_codi, createddate, version)
VALUES (900001, 'SCDCPAJU', 'Servei de consulta de padró de convivència', 0,
    'Backoffice del padró municipal', 1, 1, 'http://emiserv-scsp-fake:18080/ws/EmiservBackoffice', 0,
    0, 0, 'es.caib.emiserv.backoffice.CaibBackoffice', 0, 0, 'integracio', SYSDATE, 0);
