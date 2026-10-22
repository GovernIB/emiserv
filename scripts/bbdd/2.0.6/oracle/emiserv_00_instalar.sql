--------------------------------------------------------------------------
-- EMISERV - Instal·lacio completa de la base de dades (Oracle)
--------------------------------------------------------------------------
-- Executa, en l'ordre correcte, tots els scripts d'aquest directori per
-- crear l'esquema EMISERV des de zero en un entorn nou.
--
-- Us:
--   1. Connectar amb SQL*Plus com a usuari propietari de l'esquema
--      (NO com a WWW_EMISERV).
--   2. Assegurar-se que ja existeixen (creats pel DBA fora d'aquest
--      script):
--        - L'usuari/rol WWW_EMISERV (usat només per l'aplicacio, amb
--          permisos DML via emiserv_07_grants.sql).
--        - El tablespace EMISERV_LOB (usat per emiserv_06_lobs.sql).
--   3. Executar:
--        sqlplus USUARI/PASSWORD@BBDD @emiserv_00_instalar.sql
--------------------------------------------------------------------------

WHENEVER SQLERROR EXIT SQL.SQLCODE;

@@emiserv_01_taules.sql
@@emiserv_02_constraints.sql
@@emiserv_03_indexos.sql
@@emiserv_04_sequencies.sql
@@emiserv_05_triggers.sql
@@emiserv_06_lobs.sql
@@emiserv_07_grants.sql
@@emiserv_08_dades_inicials.sql

PROMPT Instal·lacio de la base de dades EMISERV completada.
