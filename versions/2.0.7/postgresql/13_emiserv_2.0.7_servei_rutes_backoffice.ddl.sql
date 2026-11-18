-- Enrutadors que admeten rutes cap al backoffice dels cedents (consulta directa)
ALTER TABLE ems_servei ADD COLUMN rutes_backoffice BOOLEAN DEFAULT FALSE NOT NULL;
