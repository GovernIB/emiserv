-- 78
CREATE TABLE ems_entitat (
    id BIGINT NOT NULL,
    codi VARCHAR(64) NOT NULL,
    nom VARCHAR(255) NOT NULL,
    cif VARCHAR(9) NOT NULL,
    unitat_arrel VARCHAR(9) NOT NULL,
    CONSTRAINT ems_entitat_pk PRIMARY KEY (id),
    UNIQUE (codi));

ALTER TABLE ems_redir_solicitud ADD solicitant_codi VARCHAR(64);
ALTER TABLE ems_redir_solicitud ADD titular_tipus_doc VARCHAR(16);

ALTER TABLE core_transmision ADD codigosolicitante VARCHAR(64);
ALTER TABLE core_transmision ADD tipodoctitular VARCHAR(16);

-- 83
ALTER TABLE ems_redir_missatge ADD entitat_codi VARCHAR(64);
ALTER TABLE ems_redir_peticio ADD entitat_codi VARCHAR(64);