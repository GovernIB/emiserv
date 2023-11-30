CREATE TABLE ems_entitat (
     id BIGINT NOT NULL,
     codi VARCHAR(64) NOT NULL,
     nom VARCHAR(255) NOT NULL,
     cif VARCHAR(9) NOT NULL,
     unitat_arrel VARCHAR(9),
     CONSTRAINT ems_entitat_pk PRIMARY KEY (id),
     UNIQUE (codi));

ALTER TABLE ems_redir_solicitud ADD solicitant_codi VARCHAR(64);
ALTER TABLE ems_redir_solicitud ADD titular_tipus_doc VARCHAR(16);
ALTER TABLE ems_redir_missatge ADD entitat_codi VARCHAR(64);
ALTER TABLE ems_redir_peticio ADD entitat_codi VARCHAR(64);

ALTER TABLE core_transmision ADD codigosolicitante VARCHAR(64);
ALTER TABLE core_transmision ADD tipodoctitular VARCHAR(16);

ALTER TABLE core_peticion_respuesta ADD emscodiprocediment VARCHAR(256);
ALTER TABLE core_peticion_respuesta ADD emsnomprocediment VARCHAR(256);
ALTER TABLE core_peticion_respuesta ADD emsestat INTEGER;
ALTER TABLE core_peticion_respuesta ADD emscodiservei VARCHAR(64);
ALTER TABLE core_peticion_respuesta ADD emsnomservei VARCHAR(512);

ALTER TABLE ems_redir_peticio ADD procediment_codi VARCHAR(256);
ALTER TABLE ems_redir_peticio ADD procediment_nom VARCHAR(256);
ALTER TABLE ems_redir_peticio ADD estatenum INTEGER;

