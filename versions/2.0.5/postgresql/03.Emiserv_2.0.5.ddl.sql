-- 78
ALTER TABLE core_peticion_respuesta ADD emscodiprocediment VARCHAR(256);
ALTER TABLE core_peticion_respuesta ADD emsnomprocediment VARCHAR(256);
ALTER TABLE core_peticion_respuesta ADD emsestat INTEGER;
ALTER TABLE core_peticion_respuesta ADD emscodiservei VARCHAR(64);
ALTER TABLE core_peticion_respuesta ADD emsnomservei VARCHAR(512);

ALTER TABLE ems_redir_peticio ADD procediment_codi VARCHAR(256);
ALTER TABLE ems_redir_peticio ADD procediment_nom VARCHAR(256);
ALTER TABLE ems_redir_peticio ADD estatenum INTEGER;