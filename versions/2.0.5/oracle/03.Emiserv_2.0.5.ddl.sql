-- 78
ALTER TABLE core_peticion_respuesta ADD emscodiprocediment VARCHAR2(256 CHAR);
ALTER TABLE core_peticion_respuesta ADD emsnomprocediment VARCHAR2(256 CHAR);
ALTER TABLE core_peticion_respuesta ADD emsestat INTEGER;
ALTER TABLE core_peticion_respuesta ADD emscodiservei VARCHAR2(64 CHAR);
ALTER TABLE core_peticion_respuesta ADD emsnomservei VARCHAR2(512 CHAR);

ALTER TABLE ems_redir_peticio ADD procediment_codi VARCHAR2(256 CHAR);
ALTER TABLE ems_redir_peticio ADD procediment_nom VARCHAR2(256 CHAR);
ALTER TABLE ems_redir_peticio ADD estatenum INTEGER;