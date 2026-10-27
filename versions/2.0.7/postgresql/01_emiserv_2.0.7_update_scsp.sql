-- UpdateEmisor-4.19.0 a 4.25.0
ALTER TABLE core_transmision alter COLUMN apellido1Titular type character varying(50);
ALTER TABLE core_transmision alter COLUMN apellido2Titular type character varying(50);
ALTER TABLE core_transmision alter COLUMN nombreTitular type character varying(50);
ALTER TABLE core_transmision alter COLUMN nombreCompletoTitular type character varying(160);
ALTER TABLE core_transmision alter COLUMN nombrefuncionario type character varying(160);
ALTER TABLE core_transmision ADD automatizado smallint;
ALTER TABLE core_transmision ADD clasetramite smallint;
COMMENT ON COLUMN core_transmision.automatizado IS 'Indica si el procedimiento es o no automatizado';
COMMENT ON COLUMN core_transmision.claseTramite IS 'Indica la clase del trámite';
update core_parametro_configuracion set valor='4.25.0' where nombre='version.datamodel.scsp';

-- UpdateEmisor-4.25.0 a 4.28.0

update core_parametro_configuracion set valor='4.27.3' where nombre='version.datamodel.scsp';
commit;

-- UpdateEmisor-4.28.0 a 4.32.0

-- Emisor_01
update core_parametro_configuracion set valor='4.32.0' where nombre='version.datamodel.scsp';
ALTER TABLE core_clave_privada ADD COLUMN  bloqueado bool DEFAULT FALSE;
COMMIT;

-- Emisor_02
update core_transmision set clasetramite = 35 where clasetramite = 1;
update core_transmision set clasetramite = 36 where clasetramite = 2;
update core_transmision set clasetramite = 37 where clasetramite = 3;
update core_transmision set clasetramite = 38 where clasetramite = 4;
update core_transmision set clasetramite = 39 where clasetramite = 5;
update core_transmision set clasetramite = 40 where clasetramite = 6;
update core_transmision set clasetramite = 41 where clasetramite = 7;
update core_transmision set clasetramite = 42 where clasetramite = 8;
update core_transmision set clasetramite = 43 where clasetramite = 9;
update core_transmision set clasetramite = 44 where clasetramite = 10;
update core_transmision set clasetramite = 45 where clasetramite = 11;
update core_transmision set clasetramite = 46 where clasetramite = 12;
update core_transmision set clasetramite = 47 where clasetramite = 13;
update core_transmision set clasetramite = 48 where clasetramite = 14;
update core_transmision set clasetramite = 49 where clasetramite = 15;
update core_transmision set clasetramite = 50 where clasetramite = 16;
update core_transmision set clasetramite = 51 where clasetramite = 17;
update core_transmision set clasetramite = 52 where clasetramite = 18;
update core_transmision set clasetramite = 53 where clasetramite = 19;

commit;

update core_transmision set clasetramite = 34 where clasetramite = 35;
update core_transmision set clasetramite = 19 where clasetramite = 36;
update core_transmision set clasetramite = 20 where clasetramite = 37;
update core_transmision set clasetramite = 21 where clasetramite = 38;
update core_transmision set clasetramite = 22 where clasetramite = 39;
update core_transmision set clasetramite = 23 where clasetramite = 40;
update core_transmision set clasetramite = 24 where clasetramite = 41;
update core_transmision set clasetramite = 25 where clasetramite = 42;
update core_transmision set clasetramite = 26 where clasetramite = 43;
update core_transmision set clasetramite = 27 where clasetramite = 44;
update core_transmision set clasetramite = 28 where clasetramite = 45;
update core_transmision set clasetramite = 29 where clasetramite = 46;
update core_transmision set clasetramite = 2 where clasetramite = 47;
update core_transmision set clasetramite = 30 where clasetramite = 48;
update core_transmision set clasetramite = 31 where clasetramite = 49;
update core_transmision set clasetramite = 32 where clasetramite = 50;
update core_transmision set clasetramite = 14 where clasetramite = 51;
update core_transmision set clasetramite = 33 where clasetramite = 52;
update core_transmision set clasetramite = 3 where clasetramite = 53;

commit;

-- UpdateEmisor-4.32.0 a 4.35.0

-- Generacion de la tabla que controlara aquellos certificados de firma que residan en un almacen ajeno a las librerias SCSP
CREATE TABLE  core_clave_privada_externa
(
    clave_privada bigint NOT NULL,
    handler character varying(512) COLLATE pg_catalog."default" NOT NULL,

    CONSTRAINT core_clave_privada_externa_pkey PRIMARY KEY (clave_privada),
    CONSTRAINT claveprivext_clavepriv FOREIGN KEY (clave_privada)
        REFERENCES core_clave_privada (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
        ) ;

update core_parametro_Configuracion set valor ='4.35.0' where  nombre='version.datamodel.scsp';
commit;

-- UpdateEmisor-4.35.0 a 4.38.0

-- Se añade una columna para especificar un posible manejador de firma externo para las peticiones al servicio
-- 0021543: Se debe permitir que la firma de las peticiones se realice en un módulo externo a las librerías
ALTER TABLE core_servicio ADD COLUMN signer VARCHAR(512);
insert into core_parametro_configuracion(nombre,valor,descripcion) values('external.signer.validate.sign','false','Flag que indica si se debe validar la firma generada desde un manejador externo');
insert into core_parametro_configuracion(nombre,valor,descripcion) values('external.signer.afirma','none','Manejador externo que realizara la firma de las firmas enviadas a @firma');

update core_parametro_configuracion set valor ='4.38.0' where nombre='version.datamodel.scsp';
commit;

-- UpdateEmisor-4.38.0 a 4.63.3

UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Ley de contratos' 				WHERE CODCERTIFICADO='ECOT101I';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Autorización de transportes' 	WHERE CODCERTIFICADO='ECOT102I';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Ayudas y subvenciones'			WHERE CODCERTIFICADO='ECOT103I';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Residencia y trabajo' 			WHERE CODCERTIFICADO='ECOT104I';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Genérico' 						WHERE CODCERTIFICADO='ECOTGENI';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Nivel de renta intermediado' 	WHERE CODCERTIFICADO='NIVRENTI';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Ley de contratos' 				WHERE CODCERTIFICADO='ECOT101';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Autorización de transportes' 	WHERE CODCERTIFICADO='ECOT102';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Ayudas y subvenciones' 		WHERE CODCERTIFICADO='ECOT103';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Residencia y trabajo' 			WHERE CODCERTIFICADO='ECOT104';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Genérico' 						WHERE CODCERTIFICADO='ECOTGEN';
COMMIT;

-- UpdateEmisor-4.63.3 a 4.70.3

INSERT INTO core_parametro_configuracion (nombre, valor,descripcion )
VALUES('afirma.protocolo','TLSv1.2',
       'Protocolo TLS para el establecimiento del SSL' );

update core_parametro_configuracion set valor='5.0.12' where nombre='version.datamodel.scsp';
COMMIT;
