-- SVDSCDDWS01: servei amb les dades específiques xifrades (WS-Security). PINBAL xifra el
-- DatosEspecificos de la petició amb el certificat de l'EMISERV central (limit_emiserv, la clau de
-- xifratge del servei) i desxifra el de la resposta amb la seva clau privada (limit_pinbal).
-- Les parts a xifrar tenen el format de WSS4J (llibreries SCSP 5), no el d'XPath de SCSP 4.
INSERT INTO core_clave_publica (id, alias, nombre, numeroserie, fechaalta)
VALUES (909901, 'limit_emiserv', 'EMISERV central (limit_emiserv)', '6044277137025042875', TIMESTAMP '2024-01-01 00:00:00');
UPDATE core_servicio SET
    tiposeguridad = 'WS-Security',
    clavecifrado = 909901,
    algoritmocifrado = 'AES128',
    xpathcifradosincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos',
    xpathcifradoasincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos'
WHERE codcertificado = 'SVDSCDDWS01';
