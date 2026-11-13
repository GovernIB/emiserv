-- ============================================================================
-- EMISERV 2.0.7 -- Marxa enrere de 11_emiserv_2.0.7_xifrat_scsp.dml.sql (PostgreSQL)
-- ============================================================================
--
-- Torna a deixar les parts a xifrar amb el format XPath de les llibreries SCSP 4.
-- Nomes cal executar-lo si es torna a desplegar una versio 2.0.6 (SCSP 4, Axis2).
-- ============================================================================

BEGIN;

UPDATE core_servicio
   SET xpathcifradosincrono = '//*[local-name()=''DatosEspecificos'']'
 WHERE xpathcifradosincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos';

UPDATE core_servicio
   SET xpathcifradoasincrono = '//*[local-name()=''DatosEspecificos'']'
 WHERE xpathcifradoasincrono = '{Content}{http://intermediacion.redsara.es/scsp/esquemas/datosespecificos}DatosEspecificos';

COMMIT;

SELECT codcertificado, xpathcifradosincrono, xpathcifradoasincrono
  FROM core_servicio
 WHERE xpathcifradosincrono IS NOT NULL
    OR xpathcifradoasincrono IS NOT NULL
 ORDER BY codcertificado;
