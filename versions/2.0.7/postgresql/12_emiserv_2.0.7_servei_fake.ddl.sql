-- Mode fake dels serveis: respostes simulades sense consultar el cedent
ALTER TABLE ems_servei ADD COLUMN fake BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE ems_servei ADD COLUMN fake_resultat VARCHAR(20);
ALTER TABLE ems_servei ADD COLUMN fake_dades_esp TEXT;
