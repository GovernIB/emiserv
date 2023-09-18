CREATE TABLE ems_entitat (
     id NUMBER(38, 0) NOT NULL,
     codi VARCHAR2(64 CHAR) NOT NULL,
     nom VARCHAR2(255 CHAR) NOT NULL,
     cif VARCHAR2(9 CHAR) NOT NULL,
     unitat_arrel VARCHAR2(9 CHAR) NOT NULL,
     CONSTRAINT ems_entitat_pk PRIMARY KEY (id),
     UNIQUE (codi));

ALTER TABLE core_transmision ADD codigosolicitante VARCHAR2(64 CHAR);
ALTER TABLE core_transmision ADD tipodoctitular VARCHAR2(16 CHAR);

ALTER TABLE ems_redir_solicitud ADD solicitant_codi VARCHAR2(64 CHAR);
ALTER TABLE ems_redir_solicitud ADD titular_tipus_doc VARCHAR2(16 CHAR);
ALTER TABLE ems_redir_missatge ADD entitat_codi VARCHAR2(64 CHAR);
ALTER TABLE ems_redir_peticio ADD entitat_codi VARCHAR2(64 CHAR);

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_entitat TO www_emiserv;
