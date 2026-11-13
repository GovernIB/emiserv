-- Padró històric (SCDHPAJU) de l'Ajuntament de Manacor, amb el backoffice municipal (simulat pel
-- fake): hi arriben les peticions que l'EMISERV central encamina per al municipi 07040.
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios,
    maxsolicitudespeticion, emisor, clavefirma, fechaalta, caducidad, timeout, validacionfirma)
VALUES (900002, 'SCDHPAJU', 'Servei de consulta de padró històric', 'V3', 'XMLSignature', 0,
    0, 900001, 900001, TIMESTAMP '2024-01-01 00:00:00', 0, 60, 'estricto');
INSERT INTO core_em_backoffice (certificado, classname, ter)
VALUES (900002, 'es.caib.emiserv.backoffice.CaibBackoffice', 1);
INSERT INTO ems_servei (id, codi, nom, tipus, descripcio, configurat, actiu, backcaib_url, backcaib_autenticacio,
    backcaib_async_tipus, backcaib_async_ter, backoffice_class, xsd_activa, async_sincron, createdby_codi, createddate, version)
VALUES (900002, 'SCDHPAJU', 'Servei de consulta de padró històric', 0,
    'Backoffice del padró municipal', 1, 1, 'http://emiserv-scsp-fake:18080/ws/EmiservBackoffice', 0,
    0, 0, 'es.caib.emiserv.backoffice.CaibBackoffice', 0, 0, 'integracio', SYSDATE, 0);
