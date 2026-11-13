-- ============================================================================
-- EMISERV 2.0.7 -- Format de les parts a xifrar dels serveis SCSP (PostgreSQL)
-- Changeset db/changelog/changes/2.0.7/2.0.7_009_xifrat_scsp.yaml::1762905600000-1::limit
-- ============================================================================
--
-- Amb les llibreries SCSP 5 (WSS4J 2.4), CORE_SERVICIO.XPATHCIFRADOSINCRONO /
-- XPATHCIFRADOASINCRONO ja no s'avaluen com una expressio XPath: es passen a
-- WSS4J com a definicio de les parts a xifrar. L'emisor el fa servir per xifrar
-- les dades especifiques de la resposta. Amb el format antic
--
--   //*[local-name()='DatosEspecificos']
--
-- WSS4J no troba l'element, la resposta no es pot xifrar ("Error during
-- encryption" / "noEncElement" al log) i el requeridor rep un SOAP Fault
-- "Firma no valida". El format correcte es el de parts de WSS4J:
--
--   {Content}{<namespace>}DatosEspecificos
--
-- Aquest script converteix els serveis amb esquema V3 i l'XPath de
-- DatosEspecificos (sense tenir en compte espais ni el tipus de cometes), que fan
-- servir el namespace http://intermediacion.redsara.es/scsp/esquemas/datosespecificos.
-- La resta de valors els llista el pas 3 per revisar-los a ma (p.ex. serveis V2 o
-- amb dades especifiques d'un altre namespace).
--
-- Marxa enrere (si es torna a desplegar una versio 2.0.6 amb SCSP 4):
-- 11_emiserv_2.0.7_xifrat_scsp_rollback.dml.sql
-- ============================================================================

-- 1. Situacio actual: serveis amb xifratge configurat
SELECT codcertificado, versionesquema, clavecifrado, xpathcifradosincrono, xpathcifradoasincrono
  FROM core_servicio
 WHERE xpathcifradosincrono IS NOT NULL
    OR xpathcifradoasincrono IS NOT NULL
 ORDER BY codcertificado;

-- 2. Conversio al format de parts de WSS4J
BEGIN;
UPDATE core_servicio
   SET xpathcifradosincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos'
 WHERE REPLACE(REPLACE(xpathcifradosincrono, ' ', ''), '"', '''') = '//*[local-name()=''DatosEspecificos'']'
   AND versionesquema = 'V3';

UPDATE core_servicio
   SET xpathcifradoasincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos'
 WHERE REPLACE(REPLACE(xpathcifradoasincrono, ' ', ''), '"', '''') = '//*[local-name()=''DatosEspecificos'']'
   AND versionesquema = 'V3';

COMMIT;

-- 3. Valors que no s'han convertit i s'han de revisar a ma (han de quedar 0 files)
SELECT codcertificado, versionesquema, xpathcifradosincrono, xpathcifradoasincrono
  FROM core_servicio
 WHERE (xpathcifradosincrono IS NOT NULL AND xpathcifradosincrono NOT LIKE '{%')
    OR (xpathcifradoasincrono IS NOT NULL AND xpathcifradoasincrono NOT LIKE '{%')
 ORDER BY codcertificado;
