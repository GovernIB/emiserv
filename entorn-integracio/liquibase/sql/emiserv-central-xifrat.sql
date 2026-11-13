-- SVDSCDDWS01: servei amb les dades específiques xifrades (WS-Security). PINBAL xifra el
-- DatosEspecificos de la petició amb el certificat de l'EMISERV (limit_emiserv), l'emissor el
-- desxifra amb la seva clau privada i xifra el de la resposta amb el certificat que signa la
-- petició (limit_pinbal, la clau de xifratge del servei).
-- Les parts a xifrar tenen el format de WSS4J (llibreries SCSP 5), no el d'XPath de SCSP 4.
INSERT INTO core_clave_publica (id, alias, nombre, numeroserie, fechaalta)
VALUES (900001, 'limit_pinbal', 'PINBAL (limit_pinbal)', '585528980575041553', TIMESTAMP '2024-01-01 00:00:00');
UPDATE core_servicio SET
    tiposeguridad = 'WS-Security',
    clavecifrado = 900001,
    algoritmocifrado = 'AES128',
    xpathcifradosincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos',
    xpathcifradoasincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos'
WHERE codcertificado = 'SVDSCDDWS01';
