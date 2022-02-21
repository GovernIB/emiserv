-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 21/2/22 8:48
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************


-- Changeset db/changelog/changes/2.0.3/36.yaml::1643536278328-1::limit
CREATE TABLE ems_config_group (code VARCHAR2(128) NOT NULL, parent_code VARCHAR2(128), position NUMBER(3) NOT NULL, description_key VARCHAR2(512) NOT NULL);
ALTER TABLE ems_config_group ADD PRIMARY KEY (code);
grant select, update, insert, delete on ems_config_group to www_emiserv;

INSERT INTO ems_config_group (code, position, description_key) VALUES ('GENERAL', '0', 'propietat.grup.general');
INSERT INTO ems_config_group (code, position, description_key) VALUES ('BACKOFFICES', '1', 'propietat.grup.backoffices');
INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('BACKOFFICE_SVDCCAACPASWS01', 'BACKOFFICES', '0', 'propietat.grup.backoffice.SVDCCAACPASWS01');
INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('BACKOFFICE_SVDCCAACPCWS01', 'BACKOFFICES', '1', 'propietat.grup.backoffice.SVDCCAACPCWS01');
INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('BACKOFFICE_SVDSCDDWS01', 'BACKOFFICES', '2', 'propietat.grup.backoffice.SVDSCDDWS01');

CREATE TABLE ems_config_type (code VARCHAR2(128) NOT NULL, value VARCHAR2(2048));
ALTER TABLE ems_config_type ADD PRIMARY KEY (code);
grant select, update, insert, delete on ems_config_type to www_emiserv;

INSERT INTO ems_config_type (code) VALUES ('BOOL');
INSERT INTO ems_config_type (code) VALUES ('TEXT');
INSERT INTO ems_config_type (code) VALUES ('INT');
INSERT INTO ems_config_type (code) VALUES ('FLOAT');
INSERT INTO ems_config_type (code) VALUES ('PASS');
INSERT INTO ems_config_type (code) VALUES ('CRON');
INSERT INTO ems_config_type (code, value) VALUES ('DIALECTE', 'es.caib.emiserv.persist.dialect.OracleCaibDialect,es.caib.emiserv.persist.dialect.PostgreSqlCaibDialect');

CREATE TABLE ems_config (key VARCHAR2(256) NOT NULL, value VARCHAR2(2048), description_key VARCHAR2(2048), group_code VARCHAR2(128) NOT NULL, position NUMBER(3) NOT NULL, source_property VARCHAR2(16) NOT NULL, requereix_reinici NUMBER(1) DEFAULT 0, type_code VARCHAR2(128), lastmodifiedby VARCHAR2(64), lastmodifieddate TIMESTAMP);
ALTER TABLE ems_config ADD PRIMARY KEY (key);
ALTER TABLE ems_config ADD CONSTRAINT ems_config_group_fk FOREIGN KEY (group_code) REFERENCES ems_config_group (code);
grant select, update, insert, delete on ems_config to www_emiserv;

INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.xsd.base.path', '/app/caib/emiserv/xsd', 'propietat.xsd.base.path', 'GENERAL', '3', 'FILE', 'TEXT');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.tasca.backoffice.async.processar.pendents', '30000', 'propietat.tasca.backoffice.async.processar.pendents', 'GENERAL', '4', 'DATABASE', 'INT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code, requereix_reinici) VALUES ('es.caib.emiserv.security.mappableRoles', 'propietat.security.mappableRoles', 'GENERAL', '5', 'DATABASE', 'TEXT', '1');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code, requereix_reinici) VALUES ('es.caib.emiserv.security.useResourceRoleMappings', 'propietat.security.useResourceRoleMappings', 'GENERAL', '6', 'DATABASE', 'BOOL', '1');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.default.auditor', 'propietat.default.auditor', 'GENERAL', '7', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.jar.path', 'propietat.backoffice.jar.path', 'GENERAL', '8', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.mock', 'propietat.backoffice.mock', 'GENERAL', '9', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.caib.auth.username', 'propietat.backoffice.usuari', 'BACKOFFICES', '0', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.caib.auth.password', 'propietat.backoffice.secret', 'BACKOFFICES', '1', 'FILE', 'PASS');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.caib.soap.action', 'propietat.backoffice.soap.action', 'BACKOFFICES', '2', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.processar.datos.especificos.peticio', 'propietat.backoffice.processar.datosespecificos', 'BACKOFFICES', '3', 'DATABASE', 'BOOL');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.username', 'SIC_CERDEUTE', 'propietat.backoffice.servei.usuari', 'BACKOFFICE_SVDCCAACPASWS01', '0', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.password', 'propietat.backoffice.servei.secret', 'BACKOFFICE_SVDCCAACPASWS01', '1', 'FILE', 'PASS');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.soap.action', 'http://sap.com/xi/WebService/soap1.1', 'propietat.backoffice.servei.soap.action', 'BACKOFFICE_SVDCCAACPASWS01', '2', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPASWS01.processar.datos.especificos.peticio', 'propietat.backoffice.servei.processar.datosespecificos', 'BACKOFFICE_SVDCCAACPASWS01', '3', 'DATABASE', 'BOOL');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.username', 'SIC_CERDEUTE', 'propietat.backoffice.servei.usuari', 'BACKOFFICE_SVDCCAACPCWS01', '0', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.password', 'propietat.backoffice.servei.secret', 'BACKOFFICE_SVDCCAACPCWS01', '1', 'FILE', 'PASS');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.soap.action', 'http://sap.com/xi/WebService/soap1.1', 'propietat.backoffice.servei.soap.action', 'BACKOFFICE_SVDCCAACPCWS01', '2', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPCWS01.processar.datos.especificos.peticio', 'propietat.backoffice.servei.processar.datosespecificos', 'BACKOFFICE_SVDCCAACPCWS01', '3', 'DATABASE', 'BOOL');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.username', '$emiserv_sisdepen', 'propietat.backoffice.servei.usuari', 'BACKOFFICE_SVDSCDDWS01', '0', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.password', 'propietat.backoffice.servei.secret', 'BACKOFFICE_SVDSCDDWS01', '1', 'FILE', 'PASS');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDSCDDWS01.caib.soap.action', 'propietat.backoffice.servei.soap.action', 'BACKOFFICE_SVDSCDDWS01', '2', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDSCDDWS01.processar.datos.especificos.peticio', 'propietat.backoffice.servei.processar.datosespecificos', 'BACKOFFICE_SVDSCDDWS01', '3', 'DATABASE', 'BOOL');

UPDATE core_parametro_configuracion SET descripcion = 'Alias del certificado con el que firmamos la peticiones para @Firma' WHERE nombre = 'afirma.autenticacion.aliasSignerCert';
UPDATE core_parametro_configuracion SET descripcion = 'Parámetro que indica si @Firma está activo' WHERE nombre = 'afirma.enabled';
UPDATE core_parametro_configuracion SET descripcion = 'Id de aplicación para enviar a @Firma' WHERE nombre = 'afirma.idAplicacion';
UPDATE core_parametro_configuracion SET descripcion = 'Modo de validación del certificado con @Firma' WHERE nombre = 'afirma.modoValidacion';
UPDATE core_parametro_configuracion SET descripcion = 'URL de @Firma' WHERE nombre = 'afirma.url';
UPDATE core_parametro_configuracion SET descripcion = 'Directorio donde se guardan los ficheros de las peticiones' WHERE nombre = 'almacenamiento.ficheros';
UPDATE core_parametro_configuracion SET descripcion = 'Parámetro que indica si se guardarán los nodos de las transmisiones' WHERE nombre = 'almacenamiento.transmisiones';
UPDATE core_parametro_configuracion SET descripcion = 'CIF del Emisor' WHERE nombre = 'cif.emisor';
UPDATE core_parametro_configuracion SET descripcion = 'Parámetro que indica si se guardarán trazas en CORE_LOG de los errores ocurridos' WHERE nombre = 'core_log.enabled';
UPDATE core_parametro_configuracion SET descripcion = 'Clase propia de validación de certificados' WHERE nombre = 'custom.cert.validation.class';
UPDATE core_parametro_configuracion SET descripcion = 'Ruta del fichero keystore' WHERE nombre = 'keystoreFile';
UPDATE core_parametro_configuracion SET descripcion = 'Password del keystore' WHERE nombre = 'keystorePass';
UPDATE core_parametro_configuracion SET descripcion = 'Tipo de keystore' WHERE nombre = 'keystoreType';
UPDATE core_parametro_configuracion SET descripcion = 'Parámetro que indica si se cargará de BBDD la configuración de logs o de fichero' WHERE nombre = 'logDinamico.enabled';
UPDATE core_parametro_configuracion SET descripcion = 'Nivel de traza a mostrar en los logs' WHERE nombre = 'nivelTraza';
UPDATE core_parametro_configuracion SET descripcion = 'Nombre del Emisor' WHERE nombre = 'nombre.emisor';
UPDATE core_parametro_configuracion SET descripcion = 'Ruta del fichero donde guardaremos los logs' WHERE nombre = 'pathLogs';
UPDATE core_parametro_configuracion SET descripcion = 'Periodo de validez del certificado en caché' WHERE nombre = 'periodo.validacion.certificados';
UPDATE core_parametro_configuracion SET descripcion = 'Parámetro que indica si el ciclo de pooling de solicitudes pendientes está activo' WHERE nombre = 'polling.enabled';
UPDATE core_parametro_configuracion SET descripcion = 'Cuando no haya indicado un prefijo asociado al servicio y este si está definido se utilizará este para la generación del id de transmisión' WHERE nombre = 'prefijo.idtransmision';
UPDATE core_parametro_configuracion SET descripcion = 'Tiempo de espera antes de comenzar a ejecutar la tarea de pooling' WHERE nombre = 'task.polling.espera';
UPDATE core_parametro_configuracion SET descripcion = 'Intervalo entre ejecuciones de la tarea de pooling' WHERE nombre = 'task.polling.intervalo';
UPDATE core_parametro_configuracion SET descripcion = 'Longitud del identificador de la petición' WHERE nombre = 'tipoId';
UPDATE core_parametro_configuracion SET descripcion = 'Flag que indica si se valida el valor del nodo <NifEmisor> de la petición' WHERE nombre = 'validate.nif.emisor.enabled';
UPDATE core_parametro_configuracion SET descripcion = 'Especifica la versión del modelo de datos actual' WHERE nombre = 'version.datamodel.scsp';
