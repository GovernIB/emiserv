-- Enrutadors que admeten rutes cap al backoffice dels cedents (consulta directa)
ALTER TABLE ems_servei ADD rutes_backoffice NUMBER(1) DEFAULT 0 NOT NULL;
