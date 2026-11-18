-- Mode fake dels serveis: respostes simulades sense consultar el cedent
ALTER TABLE ems_servei ADD fake NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE ems_servei ADD fake_resultat VARCHAR2(20 CHAR);
ALTER TABLE ems_servei ADD fake_dades_esp CLOB;
