-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 24/05/21 15:51
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.3.3
-- *********************************************************************

-- Changeset db/changelog/initial_schema_table.yaml::init-1::limit (generated)
CREATE TABLE ems_backoffice_com (id NUMBER(38, 0) NOT NULL, peticio_id NUMBER(38, 0) NOT NULL, peticio_data TIMESTAMP NOT NULL, peticio_xml CLOB NOT NULL, solicitud_id NUMBER(38, 0), resposta_data TIMESTAMP, resposta_xml CLOB, error CLOB, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_backoffice_com_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-2::limit (generated)
CREATE TABLE ems_backoffice_pet (id NUMBER(38, 0) NOT NULL, peticio_id VARCHAR2(26) NOT NULL, estat INTEGER NOT NULL, ter_data TIMESTAMP NOT NULL, darrera_sol_data TIMESTAMP, darrera_sol_id VARCHAR2(256), processades_error INTEGER, processades_total INTEGER, comunicacio_id NUMBER(38, 0), version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_backoffice_pet_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-3::limit (generated)
CREATE TABLE ems_backoffice_sol (id NUMBER(38, 0) NOT NULL, peticio_id NUMBER(38, 0) NOT NULL, solicitud_id VARCHAR2(256), comunicacio_id NUMBER(38, 0), estat INTEGER, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_backoffice_sol_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-4::limit (generated)
CREATE TABLE ems_redir_missatge (id NUMBER(38, 0) NOT NULL, tipus INTEGER NOT NULL, peticio_id NUMBER(38, 0) NOT NULL, xml CLOB, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_redir_msg_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-5::limit (generated)
CREATE TABLE ems_redir_peticio (id NUMBER(38, 0) NOT NULL, data_comprovacio TIMESTAMP, data_peticio TIMESTAMP, data_resposta TIMESTAMP, emissor_codi INTEGER, error CLOB, estat VARCHAR2(16) NOT NULL, num_enviaments INTEGER NOT NULL, num_transmissions INTEGER NOT NULL, peticio_id VARCHAR2(128) NOT NULL, servei_codi VARCHAR2(256) NOT NULL, sincrona NUMBER(1), ter TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_redir_pet_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-6::limit (generated)
CREATE TABLE ems_redir_solicitud (id NUMBER(38, 0) NOT NULL, solicitud_id VARCHAR2(256) NOT NULL, solicitant_id VARCHAR2(40) NOT NULL, solicitant_nom VARCHAR2(256), consentiment VARCHAR2(3), data_generacio TIMESTAMP, error CLOB, estat VARCHAR2(4), finalitat VARCHAR2(256), funcionari_doc VARCHAR2(16), funcionari_nom VARCHAR2(160), procediment_codi VARCHAR2(256), procediment_nom VARCHAR2(256), titular_doc VARCHAR2(16), titular_llinatge1 VARCHAR2(160), titular_llinatge2 VARCHAR2(160), titular_nom VARCHAR2(160), titular_nom_sencer VARCHAR2(256), transmision_id VARCHAR2(256), unitat_tramitadora VARCHAR2(256), peticio_id NUMBER(38, 0) NOT NULL, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_redir_sol_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-7::limit (generated)
CREATE TABLE ems_servei (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, nom VARCHAR2(256) NOT NULL, tipus INTEGER NOT NULL, descripcio VARCHAR2(1024), configurat NUMBER(1), actiu NUMBER(1), backcaib_async_ter INTEGER, backcaib_async_tipus INTEGER, backcaib_autenticacio INTEGER, backcaib_url VARCHAR2(256), backoffice_class VARCHAR2(256), resolver_class VARCHAR2(256), response_resolver_class VARCHAR2(256), url_per_defecte VARCHAR2(256), xsd_activa NUMBER(1), xsd_esquema_bak VARCHAR2(256), createdby_codi VARCHAR2(64) NOT NULL, createddate TIMESTAMP NOT NULL, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_servei_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-8::limit (generated)
CREATE TABLE ems_servei_ruta_desti (id NUMBER(38, 0) NOT NULL, entitat_codi VARCHAR2(64) NOT NULL, ordre NUMBER(38, 0), url VARCHAR2(256) NOT NULL, servei_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64) NOT NULL, createddate TIMESTAMP NOT NULL, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_servei_rut_des_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-9::limit (generated)
CREATE TABLE ems_usuari (codi VARCHAR2(64) NOT NULL, nom VARCHAR2(200) NOT NULL, nif VARCHAR2(9) NOT NULL, email VARCHAR2(200), inicialitzat NUMBER(1), idioma VARCHAR2(2) DEFAULT 'CA', version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_usuari_pk PRIMARY KEY (codi));

-- Changeset db/changelog/initial_schema_table.yaml::init-10::limit (generated)
CREATE TABLE ems_acl_sid (id NUMBER(38, 0) NOT NULL, principal NUMBER(1) NOT NULL, sid VARCHAR2(100) NOT NULL, CONSTRAINT ems_acl_sid_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-11::limit (generated)
CREATE TABLE ems_acl_class (id NUMBER(38, 0) NOT NULL, class VARCHAR2(100) NOT NULL, CONSTRAINT ems_acl_class_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-12::limit (generated)
CREATE TABLE ems_acl_object_identity (id NUMBER(38, 0) NOT NULL, object_id_class NUMBER(38, 0) NOT NULL, object_id_identity NUMBER(38, 0) NOT NULL, parent_object NUMBER(38, 0), owner_sid NUMBER(38, 0) NOT NULL, entries_inheriting NUMBER(1) NOT NULL, CONSTRAINT ems_acl_object_identity_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-13::limit (generated)
CREATE TABLE ems_acl_entry (id NUMBER(38, 0) NOT NULL, acl_object_identity NUMBER(38, 0) NOT NULL, ace_order NUMBER(38, 0) NOT NULL, sid NUMBER(38, 0) NOT NULL, mask INTEGER NOT NULL, granting NUMBER(1) NOT NULL, audit_success NUMBER(1) NOT NULL, audit_failure NUMBER(1) NOT NULL, CONSTRAINT ems_acl_entry_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-14::limit (generated)
CREATE TABLE core_cache_certificados (numserie VARCHAR2(256) NOT NULL, autoridadcertif VARCHAR2(512) NOT NULL, tiempocomprobacion TIMESTAMP NOT NULL, revocado INTEGER DEFAULT 1 NOT NULL, CONSTRAINT core_cache_certificados_pk PRIMARY KEY (numserie, autoridadcertif));

COMMENT ON TABLE core_cache_certificados IS 'Tabla que registra el resultado de la validación de los certificados empleados en la firma de los mensajes recibidos por el requirente o por el emisor. Así mismo se registra la fecha en la que se realizó dicha validación para poder calcular el periodo de tiempo en la que dicha validación esta vigente.';

COMMENT ON COLUMN core_cache_certificados.numserie IS 'Número de serie del certificado';

COMMENT ON COLUMN core_cache_certificados.autoridadcertif IS 'Autoridad certificadora que emitió el certificado';

COMMENT ON COLUMN core_cache_certificados.tiempocomprobacion IS 'Fecha en la que se realizó el proceso de validación del certificado por última vez';

COMMENT ON COLUMN core_cache_certificados.revocado IS 'Valor booleano (1 o 0) que indicará: -1: si el proceso de validación revocó el certificado. -0: si el proceso de validación aceptó el certificado';

-- Changeset db/changelog/initial_schema_table.yaml::init-15::limit (generated)
CREATE TABLE core_clave_privada (id NUMBER(38, 0) NOT NULL, alias VARCHAR2(256) NOT NULL, nombre VARCHAR2(256) NOT NULL, passwd VARCHAR2(256) NOT NULL, numeroserie VARCHAR2(256) NOT NULL, fechabaja TIMESTAMP, fechaalta TIMESTAMP NOT NULL, organismo NUMBER(38, 0), interoperabilidad NUMBER(1), CONSTRAINT core_clave_privada_pk PRIMARY KEY (id));

COMMENT ON TABLE core_clave_privada IS 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves privadas disponibles en el almacén de certificados configurado. Las claves privadas aquí configuradas serán utilizadas para la firma de los mensajes emitidos';

COMMENT ON COLUMN core_clave_privada.alias IS 'Alias que identifica de manera unívoca a la clave privada dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';

COMMENT ON COLUMN core_clave_privada.nombre IS 'Nombre descriptivo de la clave privada';

COMMENT ON COLUMN core_clave_privada.passwd IS 'Password de la clave privada necesaria para hacer uso de la misma';

COMMENT ON COLUMN core_clave_privada.numeroserie IS 'Numero de serie de la clave privada';

-- Changeset db/changelog/initial_schema_table.yaml::init-15a::limit (generated)
ALTER TABLE core_clave_privada RENAME COLUMN passwd TO password;

-- Changeset db/changelog/initial_schema_table.yaml::init-16::limit (generated)
CREATE TABLE core_clave_publica (id NUMBER(38, 0) NOT NULL, alias VARCHAR2(256) NOT NULL, nombre VARCHAR2(256) NOT NULL, numeroserie VARCHAR2(256) NOT NULL, fechaalta TIMESTAMP NOT NULL, fechabaja TIMESTAMP, CONSTRAINT core_clave_publica_pk PRIMARY KEY (id));

COMMENT ON TABLE core_clave_publica IS 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves públicas disponibles en el almacén de certificados configurado. Las claves públicas aquí configuradas serán utilizadas para el posible cifrado de los  mensajes emitidos.';

COMMENT ON COLUMN core_clave_publica.alias IS 'Alias que identifica de manera unívoca a la clave pública dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';

COMMENT ON COLUMN core_clave_publica.nombre IS 'Nombre descriptivo de la clave pública';

COMMENT ON COLUMN core_clave_publica.numeroserie IS 'Numero de serie de la clave pública';

-- Changeset db/changelog/initial_schema_table.yaml::init-17::limit (generated)
CREATE TABLE core_codigo_error (codigo VARCHAR2(4) NOT NULL, descripcion VARCHAR2(1024) NOT NULL, CONSTRAINT core_codigo_error_pk PRIMARY KEY (codigo));

COMMENT ON TABLE core_codigo_error IS 'Tabla que registrarálos posibles errores genéricos que toda comunicación SCSP puede generar';

COMMENT ON COLUMN core_codigo_error.codigo IS 'Código identificativo del error';

COMMENT ON COLUMN core_codigo_error.descripcion IS 'Litreral descriptivo del error';

-- Changeset db/changelog/initial_schema_table.yaml::init-18::limit (generated)
CREATE TABLE core_emisor_certificado (id NUMBER(38, 0) NOT NULL, nombre VARCHAR2(50) NOT NULL, cif VARCHAR2(16) NOT NULL, fechaalta TIMESTAMP, fechabaja TIMESTAMP, CONSTRAINT core_emisor_certificado_pk PRIMARY KEY (id));

COMMENT ON TABLE core_emisor_certificado IS 'Esta tabla almacenará los diferentes emisores de servicio configurados';

COMMENT ON COLUMN core_emisor_certificado.nombre IS 'CIF identificativo del organismo emisor de servicios';

COMMENT ON COLUMN core_emisor_certificado.cif IS 'Nombre descriptivo del organismo emisor de servicios';

COMMENT ON COLUMN core_emisor_certificado.fechabaja IS 'Fecha de Baja de emisores de certificados';

-- Changeset db/changelog/initial_schema_table.yaml::init-19::limit (generated)
CREATE TABLE core_estado_peticion (codigo VARCHAR2(4) NOT NULL, mensaje VARCHAR2(256) NOT NULL, CONSTRAINT core_estado_peticion_pk PRIMARY KEY (codigo));

COMMENT ON TABLE core_estado_peticion IS 'Tabla que almacena los posibles estados en los que se puede encontrar una petición SCSP. Sus posibles valores serán: 0001 - Pendiente, 0002 - En proceso, 0003 - Tramitada';

COMMENT ON COLUMN core_estado_peticion.codigo IS 'Código identificativo del estado';

COMMENT ON COLUMN core_estado_peticion.mensaje IS 'Literal descriptivo del estado';

-- Changeset db/changelog/initial_schema_table.yaml::init-20::limit (generated)
CREATE TABLE core_modulo (nombre VARCHAR2(256) NOT NULL, descripcion VARCHAR2(512), activoentrada INTEGER DEFAULT 1 NOT NULL, activosalida INTEGER DEFAULT 1 NOT NULL, CONSTRAINT core_modulo_pk PRIMARY KEY (nombre));

COMMENT ON TABLE core_modulo IS 'Tabla que almacena la configuración de activación o desactivación de los módulos que componen el ciclo de ejecución de las librerías SCSP';

COMMENT ON COLUMN core_modulo.nombre IS 'Nombre del módulo';

COMMENT ON COLUMN core_modulo.descripcion IS 'Literal descriptivo del modulo a configurar su activación';

COMMENT ON COLUMN core_modulo.activoentrada IS 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo)';

COMMENT ON COLUMN core_modulo.activosalida IS 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo).';

-- Changeset db/changelog/initial_schema_table.yaml::init-21::limit (generated)
CREATE TABLE core_modulo_configuracion (certificado NUMBER(38, 0) NOT NULL, modulo VARCHAR2(256) NOT NULL, activoentrada INTEGER DEFAULT 1 NOT NULL, activosalida INTEGER DEFAULT 1 NOT NULL, CONSTRAINT core_modulo_config_pk PRIMARY KEY (certificado, modulo));

COMMENT ON TABLE core_modulo_configuracion IS 'Tabla que permite la sobreescritura de la configuración de activación de un módulo para un servicio concreto';

COMMENT ON COLUMN core_modulo_configuracion.certificado IS 'Código del certificado solicitado';

COMMENT ON COLUMN core_modulo_configuracion.modulo IS 'Nombre del módulo a configurar';

COMMENT ON COLUMN core_modulo_configuracion.activoentrada IS 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo).';

COMMENT ON COLUMN core_modulo_configuracion.activosalida IS 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo).';

-- Changeset db/changelog/initial_schema_table.yaml::init-22::limit (generated)
CREATE TABLE core_req_modulo_pdf (nombre VARCHAR2(256) NOT NULL, activo INTEGER DEFAULT 1 NOT NULL, orden INTEGER NOT NULL, CONSTRAINT core_req_modulo_pdf_pk PRIMARY KEY (nombre));

-- Changeset db/changelog/initial_schema_table.yaml::init-23::limit (generated)
CREATE TABLE core_req_modulo_pdf_cesionario (servicio NUMBER(38, 0) NOT NULL, organismo NUMBER(38, 0) NOT NULL, modulo VARCHAR2(256) NOT NULL, activo INTEGER DEFAULT 1 NOT NULL, CONSTRAINT core_req_modpdf_ces_pk PRIMARY KEY (servicio, organismo, modulo));

-- Changeset db/changelog/initial_schema_table.yaml::init-24::limit (generated)
CREATE TABLE core_em_autorizacion_organismo (id NUMBER(38, 0) NOT NULL, idorganismo VARCHAR2(16) NOT NULL, fechaalta TIMESTAMP NOT NULL, fechabaja TIMESTAMP, nombreorganismo VARCHAR2(64), CONSTRAINT core_em_autorizacion_org_pk PRIMARY KEY (id));

COMMENT ON TABLE core_em_autorizacion_organismo IS 'Organismos que están dados de alta en el emisor y que van a poder consultar los servicios ofrecidos por este a través de alguna de las aplicaciones que tienen autorización para consultar los servicios';

COMMENT ON COLUMN core_em_autorizacion_organismo.idorganismo IS 'Identificador del organismo';

COMMENT ON COLUMN core_em_autorizacion_organismo.fechaalta IS 'Fecha en la que se da de alta el organismo';

COMMENT ON COLUMN core_em_autorizacion_organismo.fechabaja IS 'Fecha a partir de la cual el organismo no va a poder enviar peticiones';

COMMENT ON COLUMN core_em_autorizacion_organismo.nombreorganismo IS 'Nombre descriptivo del organismo requirente de servicios';

-- Changeset db/changelog/initial_schema_table.yaml::init-25::limit (generated)
CREATE TABLE core_organismo_cesionario (id NUMBER(38, 0) NOT NULL, fechaalta TIMESTAMP NOT NULL, fechabaja TIMESTAMP, nombre VARCHAR2(50) NOT NULL, cif VARCHAR2(50) NOT NULL, bloqueado INTEGER DEFAULT 0 NOT NULL, logo BLOB, codigounidadtramitadora VARCHAR2(9), CONSTRAINT core_organismo_cesionario_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_table.yaml::init-26::limit (generated)
CREATE TABLE core_parametro_configuracion (nombre VARCHAR2(64) NOT NULL, valor VARCHAR2(512) NOT NULL, descripcion VARCHAR2(512), CONSTRAINT core_parametro_config_pk PRIMARY KEY (nombre));

COMMENT ON TABLE core_parametro_configuracion IS 'Tabla  que almacena aquellos parámetros de configuración que son globales a todos los servicios y entornos para un mismo cliente integrador de las librerias SCSP';

COMMENT ON COLUMN core_parametro_configuracion.nombre IS 'Nombre identificativo del parámetro';

COMMENT ON COLUMN core_parametro_configuracion.valor IS 'Valor del parámetro';

COMMENT ON COLUMN core_parametro_configuracion.descripcion IS 'Literal descriptivo de la utilidad del parámetro';

-- Changeset db/changelog/initial_schema_table.yaml::init-27::limit (generated)
CREATE TABLE core_peticion_respuesta (idpeticion VARCHAR2(26) NOT NULL, certificado NUMBER(38, 0) NOT NULL, estado VARCHAR2(4) NOT NULL, estadosecundario VARCHAR2(16), fechapeticion TIMESTAMP, fecharespuesta TIMESTAMP, ter TIMESTAMP, numeroenvios INTEGER DEFAULT 0 NOT NULL, numerotransmisiones INTEGER DEFAULT 1 NOT NULL, fechaultimosondeo TIMESTAMP, transmisionsincrona INTEGER DEFAULT 1 NOT NULL, descompuesta VARCHAR2(1), error VARCHAR2(4000), errorsecundario VARCHAR2(1024), CONSTRAINT core_peticion_respuesta_pk PRIMARY KEY (idpeticion));

COMMENT ON TABLE core_peticion_respuesta IS 'Esta tabla registrará un histórico de las peticiones y respuestas intercambiadas entre los requirentes y los emisores de servicios';

COMMENT ON COLUMN core_peticion_respuesta.idpeticion IS 'Identificador unívoco de la petición de servicio';

COMMENT ON COLUMN core_peticion_respuesta.estado IS 'Codigo identificativo del estado de la petición. Tomará sus valores de las tablas scsp_estado_peticion y scsp_codigo_error';

COMMENT ON COLUMN core_peticion_respuesta.fechapeticion IS 'Timestamp que indica la fecha en la que se generó la petición';

COMMENT ON COLUMN core_peticion_respuesta.fecharespuesta IS 'Timestamp que indica la fecha en la cual se recibió la respuesta a nuestra petición';

COMMENT ON COLUMN core_peticion_respuesta.ter IS 'Timestamp que indica la fecha a partir de la cual una petición de tipo asíncrono podrá solicitar una respuesta definitiva al servicio';

COMMENT ON COLUMN core_peticion_respuesta.numeroenvios IS 'Valor que indica el número de veces que se ha reenviado una petición al servicio';

COMMENT ON COLUMN core_peticion_respuesta.numerotransmisiones IS 'Número de Solicitudes de Transmisión que se enviaron en la petición';

COMMENT ON COLUMN core_peticion_respuesta.fechaultimosondeo IS 'Timestamp que indica la fecha del último reenvio de una petición de tipo asincrono';

COMMENT ON COLUMN core_peticion_respuesta.transmisionsincrona IS 'Valor binario que indica si la petición fue solicitada a un servicio de tipo síncrono o asincrono. Podrá tomar los valores: 0: La comunicación fue de tipo asíncrono, 1: La comunicación fue de tipo síncrono';

COMMENT ON COLUMN core_peticion_respuesta.descompuesta IS 'Caracter que indica el estado del procesamiento de las transmisiones de la respuesta recibida. Podrá tomar los siguientes valores:    - S: Se ha procesado correctamente la respuesta, habiendo obtenido todas las transmisiones en ella incluidas y registradas en la tabla core_transmision. - N: No ha sido procesadas las transmisiones de la respuesta. - E: La respuesta terminó correctamente  (estado 0003), pero se ha producido un error al procesar sus trasmisiones';

-- Changeset db/changelog/initial_schema_table.yaml::init-28::limit (generated)
CREATE TABLE core_servicio (id NUMBER(38, 0) NOT NULL, codcertificado VARCHAR2(64) NOT NULL, descripcion VARCHAR2(512) NOT NULL, urlsincrona VARCHAR2(256), urlasincrona VARCHAR2(256), actionsincrona VARCHAR2(256), actionasincrona VARCHAR2(256), actionsolicitud VARCHAR2(256), versionesquema VARCHAR2(32) NOT NULL, tiposeguridad VARCHAR2(16) NOT NULL, prefijopeticion VARCHAR2(9), xpathcifradosincrono VARCHAR2(256), xpathcifradoasincrono VARCHAR2(256), esquemas VARCHAR2(256), clavecifrado NUMBER(38, 0), clavefirma NUMBER(38, 0), algoritmocifrado VARCHAR2(32), numeromaximoreenvios INTEGER NOT NULL, maxsolicitudespeticion INTEGER NOT NULL, prefijoidtransmision VARCHAR2(9), emisor NUMBER(38, 0) NOT NULL, fechaalta TIMESTAMP NOT NULL, fechabaja TIMESTAMP, caducidad INTEGER DEFAULT 0 NOT NULL, xpathliteralerror VARCHAR2(256), xpathcodigoerror VARCHAR2(256), xpathcodigoerrorsecundario VARCHAR2(256 BYTE), timeout INTEGER DEFAULT 60 NOT NULL, validacionfirma VARCHAR2(32) DEFAULT 'estricto', plantillaxslt VARCHAR2(512), CONSTRAINT core_servicio_pk PRIMARY KEY (id));

COMMENT ON TABLE core_servicio IS 'Esta tabla registrará cada uno de los certificados SCSP que van a ser utilizados por el sistema tanto de la parte requirente como de la parte emisora';

COMMENT ON COLUMN core_servicio.codcertificado IS 'Código del certificado que lo identifica unívocamente en las comunicaciones SCSP';

COMMENT ON COLUMN core_servicio.descripcion IS 'Literal descriptivo del servicio a solicitar utilizando el código de certificado';

COMMENT ON COLUMN core_servicio.urlsincrona IS 'Endpoint de acceso al servicio de tipo síncrono';

COMMENT ON COLUMN core_servicio.urlasincrona IS 'Endpoint de acceso al servicio de tipo asíncrono del certificado';

COMMENT ON COLUMN core_servicio.actionsincrona IS 'Valor del Soap:Action de la petición síncrona utilizado por el servidor WS en el caso de que sea necesario';

COMMENT ON COLUMN core_servicio.actionasincrona IS 'Valor del Soap:Action de la petición asíncrona utilizado por el servidor WS en el caso de que sea necesario';

COMMENT ON COLUMN core_servicio.actionsolicitud IS 'Valor del Soap:Action de la solicitud asíncrona de respuesta utilizado por el servidor WS en el caso de que sea necesario';

COMMENT ON COLUMN core_servicio.versionesquema IS 'Indica la versión de esquema utilizado en los mensajes SCSP pudiendo tomar los valores V2 y V3 (existe la posibilidad de añadir otros valores utilizando configuraciones avandadas de binding)';

COMMENT ON COLUMN core_servicio.tiposeguridad IS 'Indica la politica de seguridad utilizada en la securización de los mensajes, pudiendo tomar los valores [XMLSignature| WS-Security]';

COMMENT ON COLUMN core_servicio.prefijopeticion IS 'Literal con una longitud máxima de 8 caracteres, el cual será utilizado para la construcción de los identificadores de petición, anteponiendose a un valor secuencial. Mediante este literal pueden personalizarse los identificadores de petición haciendolos más descriptivos';

COMMENT ON COLUMN core_servicio.xpathcifradosincrono IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresión xpath que permite localizar al nodo en el mensaje XML, presentará el siguiente formato //*[local-name()="NODO_A_CIFRAR"], donde "NODO_A_CIFRAR" es el local name del nodo que se desea cifrar. Este nodo será empleado en el caso de realizar comunicaciones síncronas';

COMMENT ON COLUMN core_servicio.xpathcifradoasincrono IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresión xpath que permite localizar al nodo en el mensaje XML, presentará el siguiente formato //*[local-name()="NODO_A_CIFRAR"], donde "NODO_A_CIFRAR" es el local name del nodo que se desea cifrar. Este nodo será empleado en el caso de realizar comunicaciones asíncronas';

COMMENT ON COLUMN core_servicio.esquemas IS 'Ruta que indica el directorio donde se encuentran los esquemas (*.xsd) con el que se validará el XML de los diferentes mensajes intercambiados. Esta ruta podrá tomar un valor relativo haciendo referencia al classpath de la aplicación o  un path absoluto';

COMMENT ON COLUMN core_servicio.algoritmocifrado IS 'Literal que identifica el algoritmo utilizado para el cifrado de los mensajes enviados al emisor. Debe poseer un valor reconocido por las librerías de Rampart: - Basic128Rsa15 - TripleDesRsa15  - Basic256Rsa15';

COMMENT ON COLUMN core_servicio.numeromaximoreenvios IS 'Valor que indica en el caso del requirente, el número máximo de reenvios que pueden llevarse a cabo sobre una petición asíncrona. En el caso del emisor, hace referencia al número máximo de veces que procesará y creará una respuesta para una petición con un mismo identificador';

COMMENT ON COLUMN core_servicio.maxsolicitudespeticion IS 'Número máximo de solicitudes de transmisión que se van a permitir por petición';

COMMENT ON COLUMN core_servicio.prefijoidtransmision IS 'Semilla empleada por el emisor para generar los identificadores de transmisión de las respuestas. Será un valor alfanumérico con un mínimo de 3 caracteres y un máximo de 8';

COMMENT ON COLUMN core_servicio.fechaalta IS 'Timestamp con la fecha en la cual el certificado se dio de alta en el sistema y por lo tanto a partir de la cual se podrán emitir peticiones al mismo';

COMMENT ON COLUMN core_servicio.fechabaja IS 'Timestamp con la fecha en la cual el certificado se dio de baja en el sistema y por tanto a partir de la cual no se podran emitir peticiones al mismo';

COMMENT ON COLUMN core_servicio.caducidad IS 'Número de dias que deberán sumarse a la fecharespuesta de una petición, para calcular la fecha a partir de la cual se podrá considerar que la respuesta esta caducada y se devolvera el error scsp correspondiente para indicar que la respuesta ha perdido su valor';

COMMENT ON COLUMN core_servicio.xpathliteralerror IS 'Xpath para recuperar el literal del error de los datos específicos';

COMMENT ON COLUMN core_servicio.xpathcodigoerror IS 'Xpath para recuperar el codigo de error de los datos específicos';

COMMENT ON COLUMN core_servicio.timeout IS 'Timeout que se establecerá para el envío de las peticiones a los servicios';

COMMENT ON COLUMN core_servicio.validacionfirma IS 'Parámetro que indica si se admite otro tipo de firma en el servicio además del configurado';

-- Changeset db/changelog/initial_schema_table.yaml::init-29::limit (generated)
CREATE TABLE core_tipo_mensaje (tipo INTEGER NOT NULL, descripcion VARCHAR2(32) NOT NULL, CONSTRAINT core_tipo_mensaje_pk PRIMARY KEY (tipo));

COMMENT ON TABLE core_tipo_mensaje IS 'Tabla maestra que almacena los diferentes tipos de mensajes que pueden ser intercambiados a lo largo de un ciclo de comunicación SCSP. Estos valores serán: 0: Peticion, 1: ConfirmacionPeticion, 2: SolicitudRespuesta, 3: Respuesta, 4: Fault';

COMMENT ON COLUMN core_tipo_mensaje.tipo IS 'Tipo identificativo del mensaje';

COMMENT ON COLUMN core_tipo_mensaje.descripcion IS 'Literal descriptivo del tipo de mensaje';

-- Changeset db/changelog/initial_schema_table.yaml::init-30::limit (generated)
CREATE TABLE core_token_data (idpeticion VARCHAR2(26) NOT NULL, tipomensaje INTEGER NOT NULL, datos CLOB NOT NULL, clave VARCHAR2(256), modoencriptacion VARCHAR2(32), algoritmoencriptacion VARCHAR2(32), CONSTRAINT core_token_data_pk PRIMARY KEY (idpeticion, tipomensaje));

COMMENT ON TABLE core_token_data IS 'Esta tabla almacenará el contenido de los mensajes intercambiados en un proceso de comunicación SCSP';

COMMENT ON COLUMN core_token_data.idpeticion IS 'Identificador de la petición a la cual est� asociado el XML';

COMMENT ON COLUMN core_token_data.tipomensaje IS 'Tipo de mensaje almacenado que podrá ser: Peticion, Respuesta, SolicitudRespuesta, ConfirmacionPeticion, Fault';

COMMENT ON COLUMN core_token_data.datos IS 'Bytes del mensaje almacenado';

COMMENT ON COLUMN core_token_data.clave IS 'Clave simétrica utilizada para el cifrado de los datos';

COMMENT ON COLUMN core_token_data.modoencriptacion IS 'Modo de encriptación utilizado para el proceso de cifrado. Por defecto será TransportKey';

COMMENT ON COLUMN core_token_data.algoritmoencriptacion IS 'Algoritmo empleado en la encriptación del mensaje. Podrá tomar los siguientes valores: - AES128 - AES256 -DESDe';

-- Changeset db/changelog/initial_schema_table.yaml::init-31::limit (generated)
CREATE TABLE core_transmision (idsolicitud VARCHAR2(64) NOT NULL, idpeticion VARCHAR2(26) NOT NULL, idtransmision VARCHAR2(64), idsolicitante VARCHAR2(10) NOT NULL, nombresolicitante VARCHAR2(256), doctitular VARCHAR2(30), nombretitular VARCHAR2(40), apellido1titular VARCHAR2(40), apellido2titular VARCHAR2(40), nombrecompletotitular VARCHAR2(122), docfuncionario VARCHAR2(16), nombrefuncionario VARCHAR2(128), seudonimofuncionario VARCHAR2(32), fechageneracion TIMESTAMP, unidadtramitadora VARCHAR2(250), codigounidadtramitadora VARCHAR2(9), codigoprocedimiento VARCHAR2(256), nombreprocedimiento VARCHAR2(256), expediente VARCHAR2(65), finalidad VARCHAR2(256), consentimiento VARCHAR2(3), error VARCHAR2(4000), xmltransmision CLOB, estado VARCHAR2(10), estadosecundario VARCHAR2(16), CONSTRAINT core_transmision_pk PRIMARY KEY (idsolicitud, idpeticion));

COMMENT ON TABLE core_transmision IS 'Esta tabla almacenará la información específica de cada transmisión que haya sido incluida en una respuesta de un servicio SCSP';

COMMENT ON COLUMN core_transmision.idsolicitud IS 'Indentificador de la solicitud de transmisión';

COMMENT ON COLUMN core_transmision.idpeticion IS 'Indentificador de la petición en la que se incluyó la solicitud de transmisión';

COMMENT ON COLUMN core_transmision.idtransmision IS 'Indentificador de la transmisión que responde a la petición de servicio de la Solicitud de transmisión identificada con idSolicitud';

COMMENT ON COLUMN core_transmision.idsolicitante IS 'CIF de organismo solicitante del servicio';

COMMENT ON COLUMN core_transmision.nombresolicitante IS 'Nombre del organismo solicitante de servicio';

COMMENT ON COLUMN core_transmision.doctitular IS 'Documento identificativo del titular sobre el cual se está realizando la petición de servicio';

COMMENT ON COLUMN core_transmision.nombretitular IS 'Nombre del titular sobre el que se realiza la petición del servicio';

COMMENT ON COLUMN core_transmision.apellido1titular IS 'Primer apellido del titular sobre el que se realiza la petición del servicio';

COMMENT ON COLUMN core_transmision.apellido2titular IS 'Segundo apellido del titular sobre el que se realiza la petición del servicio';

COMMENT ON COLUMN core_transmision.nombrecompletotitular IS 'Nombre completo del titular sobre el que se realiza la petición del servicio';

COMMENT ON COLUMN core_transmision.docfuncionario IS 'Documento identificativo del funcionario que generó la solicitud de transmisión';

COMMENT ON COLUMN core_transmision.nombrefuncionario IS 'Nombre del Funcionario que generó la solicitud de transmisión';

COMMENT ON COLUMN core_transmision.fechageneracion IS 'Fecha en la que se generó la transmisión';

COMMENT ON COLUMN core_transmision.unidadtramitadora IS 'Unidad Tramitadora asociada a la solicitud de transmisión';

COMMENT ON COLUMN core_transmision.codigoprocedimiento IS 'Código del procedimiento en base al cual se puede solicitar el servicio';

COMMENT ON COLUMN core_transmision.nombreprocedimiento IS 'Nombre del procedimiento en base al cual se puede solicitar el servicio';

COMMENT ON COLUMN core_transmision.expediente IS 'Expediente asociado a la solicitud de transmisión';

COMMENT ON COLUMN core_transmision.finalidad IS 'Finalidad por la cual se emiti� la solicitud de transmisión';

COMMENT ON COLUMN core_transmision.consentimiento IS 'Tipo de consentimiento asociado a la transmisión. Deberá tomar uno de los dos posibles valores: Si, Ley';

COMMENT ON COLUMN core_transmision.xmltransmision IS 'El XML de la transmisión. Su almacenamiento será opcional, dependiendo de un parametro global almacenado en la tabla core_parametro_configuracion';

COMMENT ON COLUMN core_transmision.estado IS 'Estado concreto en el que se encuentra la transmisión';

-- Changeset db/changelog/initial_schema_table.yaml::init-32::limit (generated)
CREATE TABLE core_em_aplicacion (idaplicacion INTEGER NOT NULL, nifcertificado VARCHAR2(16), numeroserie VARCHAR2(64) NOT NULL, cn VARCHAR2(512), tiempocomprobacion TIMESTAMP, autoridadcertif NUMBER(38, 0) NOT NULL, fechaalta TIMESTAMP, fechabaja TIMESTAMP, descripcion VARCHAR2(512), CONSTRAINT core_em_aplicacion_pk PRIMARY KEY (idaplicacion));

COMMENT ON TABLE core_em_aplicacion IS 'Aplicaciones dadas de alta en el emisor que van a emplear los organismos para poder realizar consultas a los servicios ofrecidos por este';

COMMENT ON COLUMN core_em_aplicacion.idaplicacion IS 'Identificador de la aplicación. Se va a generar automáticamente';

COMMENT ON COLUMN core_em_aplicacion.nifcertificado IS 'Nif del certificado con el que la aplicación firmara las peticiones';

COMMENT ON COLUMN core_em_aplicacion.numeroserie IS 'Numero de serie del certificado con el que la aplicación firmara las peticiones';

COMMENT ON COLUMN core_em_aplicacion.cn IS 'Common Name del certificado con el que la aplicación firmara las peticiones';

COMMENT ON COLUMN core_em_aplicacion.tiempocomprobacion IS 'Fecha en que se realizo la operación de validar el certificado';

COMMENT ON COLUMN core_em_aplicacion.fechaalta IS 'Fecha en la que se da de alta la aplicación';

COMMENT ON COLUMN core_em_aplicacion.fechabaja IS 'Fecha a partir de la cual la aplicación no va a poder acceder a ningún servicio';

-- Changeset db/changelog/initial_schema_table.yaml::init-33::limit (generated)
CREATE TABLE core_em_autorizacion_ca (id NUMBER(38, 0) NOT NULL, codca VARCHAR2(512) NOT NULL, nombre VARCHAR2(256), CONSTRAINT core_em_autorizacion_ca_pk PRIMARY KEY (id));

COMMENT ON TABLE core_em_autorizacion_ca IS 'Contiene la información relativa a las distintas autoridades de certificación reconocidas por la aplicación';

COMMENT ON COLUMN core_em_autorizacion_ca.codca IS 'Codigo de la autoridad de certificación (Ej: FNMT)';

COMMENT ON COLUMN core_em_autorizacion_ca.nombre IS 'Nombre de la autoridad de certificación  (Ej: Fabrica Nacional de Moneda y Timbre)';

-- Changeset db/changelog/initial_schema_table.yaml::init-34::limit (generated)
CREATE TABLE core_em_autorizacion_cert (id NUMBER(38, 0) NOT NULL, idcertificado NUMBER(38, 0) NOT NULL, idorganismo NUMBER(38, 0) NOT NULL, idaplicacion INTEGER NOT NULL, fechaalta TIMESTAMP, fechabaja TIMESTAMP, CONSTRAINT core_em_autorizacion_cert_pk PRIMARY KEY (id));

COMMENT ON TABLE core_em_autorizacion_cert IS 'Contiene la información relativa a las autorizaciones de los distintas aplicaciones para acceder a los servicios ofrecidos por el emisor';

COMMENT ON COLUMN core_em_autorizacion_cert.idorganismo IS 'Identificador del organismo';

COMMENT ON COLUMN core_em_autorizacion_cert.idaplicacion IS 'Identificador de la aplicación autorizada';

COMMENT ON COLUMN core_em_autorizacion_cert.fechaalta IS 'Fecha de alta de la autorización';

COMMENT ON COLUMN core_em_autorizacion_cert.fechabaja IS 'Fecha de baja de la autorización';

-- Changeset db/changelog/initial_schema_table.yaml::init-35::limit (generated)
CREATE TABLE core_em_codigo_error_secun (codigo VARCHAR2(4) NOT NULL, codigosecundario VARCHAR2(16) NOT NULL, descripcion CLOB NOT NULL, CONSTRAINT core_em_cod_error_secun_pk PRIMARY KEY (codigo, codigosecundario));

COMMENT ON TABLE core_em_codigo_error_secun IS 'Tabla de errores que permitirá almacenar aquellos mensajes de excepción gestionados por cada lógica de negocio específica de cada emisor, y que está asociado a un error genérico de SCSP';

COMMENT ON COLUMN core_em_codigo_error_secun.codigo IS 'Código identificativo del error especifico secundario';

COMMENT ON COLUMN core_em_codigo_error_secun.codigosecundario IS 'Codigo de error SCSP al que esta asociado el error secundario';

COMMENT ON COLUMN core_em_codigo_error_secun.descripcion IS 'Literal descriptivo del mensaje de error secundario';

-- Changeset db/changelog/initial_schema_table.yaml::init-36::limit (generated)
CREATE TABLE core_em_backoffice (certificado NUMBER(38, 0) NOT NULL, beanname VARCHAR2(256), classname VARCHAR2(256), ter INTEGER NOT NULL, CONSTRAINT core_em_backoffice_pk PRIMARY KEY (certificado));

COMMENT ON TABLE core_em_backoffice IS 'Tabla en la que se configura el módulo de backoffice del emisor que enlaza las comunicaciones SCSP con la logica de negocio especifica de cada organismo';

COMMENT ON COLUMN core_em_backoffice.beanname IS 'Identificador del bean del contexto de Spring que contiene la clase que ofrece la puerta de entrada al backoffice';

COMMENT ON COLUMN core_em_backoffice.classname IS 'Nombre completo  de la clase que ofrece la puerta de entrada al backoffice';

COMMENT ON COLUMN core_em_backoffice.ter IS 'Número de horas que harán esperar a un requirente para la generación de una respuesta definitiva ante una petición asíncrona';

-- Changeset db/changelog/initial_schema_table.yaml::init-37::limit (generated)
CREATE TABLE core_em_secuencia_idtrans (prefijo VARCHAR2(9) NOT NULL, secuencia VARCHAR2(26) NOT NULL, fechageneracio TIMESTAMP NOT NULL, CONSTRAINT core_em_secuencia_idtrans_pk PRIMARY KEY (prefijo));

COMMENT ON TABLE core_em_secuencia_idtrans IS 'Tabla utilizada para la generación de los valores de los nodos IdTransmisión en los emisores';

COMMENT ON COLUMN core_em_secuencia_idtrans.prefijo IS 'Prefijo de IdTransmisiones al que estará asociada la secuencia';

COMMENT ON COLUMN core_em_secuencia_idtrans.secuencia IS 'Valor actual de la secuencia alfanumérica asociada al prefijo';

COMMENT ON COLUMN core_em_secuencia_idtrans.fechageneracio IS 'Última fecha en la que se ha generado un valor de secuencia';

-- Changeset db/changelog/initial_schema_table.yaml::init-38::limit (generated)
CREATE TABLE core_req_secuencia_id_peticion (prefijo VARCHAR2(9) NOT NULL, secuencia VARCHAR2(23) NOT NULL, fechageneracion TIMESTAMP NOT NULL, CONSTRAINT core_req_secuencia_id_pet_pk PRIMARY KEY (prefijo));

COMMENT ON TABLE core_req_secuencia_id_peticion IS 'Tabla que almacena las semillas (secuenciales) utilizadas para la generación de los identificadores de peticion. Existirá un secuencial asociado a cada posible prefijo o número de serie de certificado digital firmante.';

COMMENT ON COLUMN core_req_secuencia_id_peticion.prefijo IS 'Prefijo utilizado para la construccion de los identificadores. Dicho valor podrá ser el prefijo especificado a ser utilizado para cada servicio o ante la no existencia del mismo, el número de serie del certificado digital firmante  de los mensajes.';

COMMENT ON COLUMN core_req_secuencia_id_peticion.secuencia IS 'Valor secuencial que será concatenado al prefijo para generar los identificadores de petición. Este secuencial será de tipo alfanumérico, de tal forma que el siguiente valor a 00000009 sería 0000000A.';

COMMENT ON COLUMN core_req_secuencia_id_peticion.fechageneracion IS 'Fecha en la que se registró el secuencial';

-- Changeset db/changelog/initial_schema_table.yaml::init-39::limit (generated)
CREATE TABLE core_req_cesionarios_servicios (id NUMBER(38, 0) NOT NULL, servicio NUMBER(38, 0), claveprivada NUMBER(38, 0), organismo NUMBER(38, 0), fechaalta TIMESTAMP(6) NOT NULL, fechabaja TIMESTAMP(6), bloqueado NUMBER(1) NOT NULL, sslflag NUMBER(1) NOT NULL, CONSTRAINT core_req_cesionarios_serv_pk PRIMARY KEY (id));

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-1::limit (generated)
ALTER TABLE ems_backoffice_com ADD CONSTRAINT ems_backcom_backpet_fk FOREIGN KEY (peticio_id) REFERENCES ems_backoffice_pet (id);

ALTER TABLE ems_backoffice_com ADD CONSTRAINT ems_backcom_backsol_fk FOREIGN KEY (solicitud_id) REFERENCES ems_backoffice_sol (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-2::limit (generated)
ALTER TABLE ems_backoffice_pet ADD CONSTRAINT ems_backpet_backcom_fk FOREIGN KEY (comunicacio_id) REFERENCES ems_backoffice_com (id);

ALTER TABLE ems_backoffice_pet ADD CONSTRAINT ems_backpet_scsppet_fk FOREIGN KEY (peticio_id) REFERENCES core_peticion_respuesta (idpeticion);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-3::limit (generated)
ALTER TABLE ems_backoffice_sol ADD CONSTRAINT ems_backsol_backcom_fk FOREIGN KEY (comunicacio_id) REFERENCES ems_backoffice_com (id);

ALTER TABLE ems_backoffice_sol ADD CONSTRAINT ems_backsol_backpet_fk FOREIGN KEY (peticio_id) REFERENCES ems_backoffice_pet (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-4::limit (generated)
ALTER TABLE ems_redir_missatge ADD CONSTRAINT ems_redmsg_peticio_fk FOREIGN KEY (peticio_id) REFERENCES ems_redir_peticio (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-5::limit (generated)
ALTER TABLE ems_redir_solicitud ADD CONSTRAINT ems_redsol_peticio_fk FOREIGN KEY (peticio_id) REFERENCES ems_redir_peticio (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-6::limit (generated)
ALTER TABLE ems_servei_ruta_desti ADD CONSTRAINT ems_serrutadesti_servei_fk FOREIGN KEY (servei_id) REFERENCES ems_servei (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-7::limit (generated)
ALTER TABLE ems_acl_sid ADD CONSTRAINT ems_acl_sid_uk UNIQUE (sid, principal);

ALTER TABLE ems_acl_class ADD CONSTRAINT ems_acl_class_uk UNIQUE (class);

ALTER TABLE ems_acl_object_identity ADD CONSTRAINT ems_acl_oid_uk UNIQUE (object_id_class, object_id_identity);

ALTER TABLE ems_acl_object_identity ADD CONSTRAINT ems_acl_oid_parent_fk FOREIGN KEY (parent_object) REFERENCES ems_acl_object_identity (id);

ALTER TABLE ems_acl_object_identity ADD CONSTRAINT ems_acl_oid_class_fk FOREIGN KEY (object_id_class) REFERENCES ems_acl_class (id);

ALTER TABLE ems_acl_object_identity ADD CONSTRAINT ems_acl_oid_owner_fk FOREIGN KEY (owner_sid) REFERENCES ems_acl_sid (id);

ALTER TABLE ems_acl_entry ADD CONSTRAINT ems_acl_entry_uk UNIQUE (acl_object_identity, ace_order);

ALTER TABLE ems_acl_entry ADD CONSTRAINT ems_acl_entry_object_fk FOREIGN KEY (acl_object_identity) REFERENCES ems_acl_object_identity (id);

ALTER TABLE ems_acl_entry ADD CONSTRAINT ems_acl_entry_acl_fk FOREIGN KEY (sid) REFERENCES ems_acl_sid (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-8::limit (generated)
ALTER TABLE core_clave_privada ADD CONSTRAINT clave_privada_organismo FOREIGN KEY (organismo) REFERENCES core_organismo_cesionario (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-9::limit (generated)
ALTER TABLE core_modulo_configuracion ADD CONSTRAINT mod_conf_modulo FOREIGN KEY (modulo) REFERENCES core_modulo (nombre);

ALTER TABLE core_modulo_configuracion ADD CONSTRAINT mod_conf_servicio FOREIGN KEY (certificado) REFERENCES core_servicio (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-10::limit (generated)
ALTER TABLE core_req_modulo_pdf_cesionario ADD CONSTRAINT fk_modulo_mod_pdf FOREIGN KEY (modulo) REFERENCES core_modulo (nombre);

ALTER TABLE core_req_modulo_pdf_cesionario ADD CONSTRAINT fk_servicio_mod_pdf FOREIGN KEY (servicio) REFERENCES core_servicio (id);

ALTER TABLE core_req_modulo_pdf_cesionario ADD CONSTRAINT fk_org_cesionario_mod_pdf FOREIGN KEY (organismo) REFERENCES core_organismo_cesionario (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-11::limit (generated)
ALTER TABLE core_peticion_respuesta ADD CONSTRAINT pet_resp_servicio FOREIGN KEY (certificado) REFERENCES core_servicio (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-12::limit (generated)
ALTER TABLE core_servicio ADD CONSTRAINT serv_emisor FOREIGN KEY (emisor) REFERENCES core_emisor_certificado (id);

ALTER TABLE core_servicio ADD CONSTRAINT serv_clavecifrado FOREIGN KEY (clavecifrado) REFERENCES core_clave_publica (id);

ALTER TABLE core_servicio ADD CONSTRAINT serv_clavefirma FOREIGN KEY (clavefirma) REFERENCES core_clave_privada (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-13::limit (generated)
ALTER TABLE core_token_data ADD CONSTRAINT token_peticion FOREIGN KEY (idpeticion) REFERENCES core_peticion_respuesta (idpeticion);

ALTER TABLE core_token_data ADD CONSTRAINT token_tipo FOREIGN KEY (tipomensaje) REFERENCES core_tipo_mensaje (tipo);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-14::limit (generated)
ALTER TABLE core_transmision ADD CONSTRAINT trans_peticion FOREIGN KEY (idpeticion) REFERENCES core_peticion_respuesta (idpeticion);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-15::limit (generated)
ALTER TABLE core_em_aplicacion ADD CONSTRAINT apli_codca FOREIGN KEY (autoridadcertif) REFERENCES core_em_autorizacion_ca (id);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-16::limit (generated)
ALTER TABLE core_em_autorizacion_cert ADD CONSTRAINT aut_cert_servicio FOREIGN KEY (idcertificado) REFERENCES core_servicio (id);

ALTER TABLE core_em_autorizacion_cert ADD CONSTRAINT aut_cert_organismo FOREIGN KEY (idorganismo) REFERENCES core_em_autorizacion_organismo (id);

ALTER TABLE core_em_autorizacion_cert ADD CONSTRAINT aut_cert_aplicacion FOREIGN KEY (idaplicacion) REFERENCES core_em_aplicacion (idaplicacion);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-17::limit (generated)
ALTER TABLE core_em_codigo_error_secun ADD CONSTRAINT codigosecundario_fk FOREIGN KEY (codigo) REFERENCES core_codigo_error (codigo);

-- Changeset db/changelog/initial_schema_constraint.yaml::init-constraint-18::limit (generated)
ALTER TABLE core_em_backoffice ADD CONSTRAINT emisor_bo_servicio FOREIGN KEY (certificado) REFERENCES core_servicio (id);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-1::limit (generated)
CREATE UNIQUE INDEX ems_servei_codi_i ON ems_servei(codi);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-2::limit (generated)
CREATE INDEX core_servicio_index_emisor ON core_servicio(emisor);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-3::limit (generated)
CREATE INDEX core_transmision_index_idpet ON core_transmision(idpeticion);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-4::limit (generated)
CREATE UNIQUE INDEX unique_serv_apl_org_baja ON core_em_autorizacion_cert(idcertificado, idorganismo, idaplicacion, fechabaja);

-- Changeset db/changelog/initial_schema_index.yaml::init-index-5::limit (generated)
CREATE INDEX cesionarios_index_organismo ON core_req_cesionarios_servicios(organismo);

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-1::limit (generated)
CREATE SEQUENCE hibernate_sequence START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-2::limit (generated)
CREATE SEQUENCE ems_acl_sid_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-3::limit (generated)
CREATE SEQUENCE ems_acl_class_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-4::limit (generated)
CREATE SEQUENCE ems_acl_oid_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-5::limit (generated)
CREATE SEQUENCE ems_acl_entry_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-6::limit (generated)
CREATE SEQUENCE id_clave_privada_sequence START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-7::limit (generated)
CREATE SEQUENCE id_clave_publica_sequence START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-8::limit (generated)
CREATE SEQUENCE id_emisor_sequence START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-9::limit (generated)
CREATE SEQUENCE id_autorizacion_organismo_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-10::limit (generated)
CREATE SEQUENCE id_organismo_cesionario_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-11::limit (generated)
CREATE SEQUENCE id_servicio_sequence START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-12::limit (generated)
CREATE SEQUENCE id_aplicacion_sequence START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-13::limit (generated)
CREATE SEQUENCE id_autorizacion_ca_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-14::limit (generated)
CREATE SEQUENCE id_autorizacion_certific_seq START WITH 1;

-- Changeset db/changelog/initial_schema_sequence.yaml::init-sequence-15::limit (generated)
CREATE SEQUENCE id_servicio_cesionario_seq START WITH 1;

-- Changeset db/changelog/initial_schema_trigger.yaml::init-trigger-1::limit (generated)
CREATE OR REPLACE TRIGGER ems_acl_sid_idgen BEFORE INSERT ON ems_acl_sid FOR EACH ROW BEGIN SELECT ems_acl_sid_seq.NEXTVAL INTO :NEW.ID FROM DUAL;

END;

CREATE OR REPLACE TRIGGER ems_acl_class_idgen BEFORE INSERT ON ems_acl_class FOR EACH ROW BEGIN SELECT ems_acl_class_seq.NEXTVAL INTO :NEW.ID FROM DUAL;

END;

CREATE OR REPLACE TRIGGER ems_acl_oid_idgen BEFORE INSERT ON ems_acl_object_identity FOR EACH ROW BEGIN SELECT ems_acl_oid_seq.NEXTVAL INTO :NEW.ID FROM DUAL;

END;

CREATE OR REPLACE TRIGGER ems_acl_entry_idgen BEFORE INSERT ON ems_acl_entry FOR EACH ROW BEGIN SELECT ems_acl_entry_seq.NEXTVAL INTO :NEW.ID FROM DUAL;

END;

-- Changeset db/changelog/initial_schema_lob.yaml::lob-1::limit (generated)
ALTER TABLE ems_backoffice_com MOVE LOB(peticio_xml) STORE AS ems_backcom_petxml_lob(TABLESPACE emiserv_lob INDEX ems_backcom_petxml_lob_i);

ALTER TABLE ems_backoffice_com MOVE LOB(resposta_xml) STORE AS ems_backcom_respxml_lob(TABLESPACE emiserv_lob INDEX ems_backcom_respxml_lob_i);

ALTER TABLE ems_backoffice_com MOVE LOB(error) STORE AS ems_backcom_error_lob(TABLESPACE emiserv_lob INDEX ems_backcom_error_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-2::limit (generated)
ALTER TABLE ems_redir_missatge MOVE LOB(xml) STORE AS ems_redmis_xml_lob(TABLESPACE emiserv_lob INDEX ems_redmis_xml_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-3::limit (generated)
ALTER TABLE ems_redir_peticio MOVE LOB(error) STORE AS ems_redpet_error_lob(TABLESPACE emiserv_lob INDEX ems_redpet_error_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-4::limit (generated)
ALTER TABLE ems_redir_solicitud MOVE LOB(error) STORE AS ems_redsol_error_lob(TABLESPACE emiserv_lob INDEX ems_redsol_error_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-5::limit (generated)
ALTER TABLE core_organismo_cesionario MOVE LOB(logo) STORE AS core_orgces_logo_lob(TABLESPACE emiserv_lob INDEX core_orgces_logo_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-6::limit (generated)
ALTER TABLE core_token_data MOVE LOB(datos) STORE AS core_tokdat_datos_lob(TABLESPACE emiserv_lob INDEX core_tokdat_datos_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-7::limit (generated)
ALTER TABLE core_transmision MOVE LOB(xmltransmision) STORE AS core_transm_xmltran_lob(TABLESPACE emiserv_lob INDEX core_transm_xmltran_lob_i);

-- Changeset db/changelog/initial_schema_lob.yaml::lob-8::limit (generated)
ALTER TABLE core_em_codigo_error_secun MOVE LOB(descripcion) STORE AS core_emcoderrsec_desc_lob(TABLESPACE emiserv_lob INDEX core_emcoderrsec_desc_lob_i);

-- Changeset db/changelog/initial_schema_grant.yaml::init-trigger-1::limit (generated)
GRANT SELECT, UPDATE, INSERT, DELETE ON ems_backoffice_com TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_backoffice_pet TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_backoffice_sol_RUTA_DESTI TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_redir_missatge TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_redir_peticio TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_redir_solicitud TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_servei TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_servei_ruta_desti TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_usuari TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_acl_sid TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_acl_class TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_acl_object_identity TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON ems_acl_entry TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_cache_certificados TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_clave_privada TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_clave_publica TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_codigo_error TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_emisor_certificado TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_estado_peticion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_modulo TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_modulo_configuracion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_req_modulo_pdf TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_req_modulo_pdf_cesionario TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_autorizacion_organismo TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_organismo_cesionario TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_parametro_configuracion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_peticion_respuesta TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_servicio TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_tipo_mensaje TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_token_data TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_transmision TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_aplicacion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_autorizacion_ca TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_autorizacion_cert TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_codigo_error_secun TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_backoffice TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_em_secuencia_idtrans TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_req_secuencia_id_peticion TO www_emiserv;

GRANT SELECT, UPDATE, INSERT, DELETE ON core_req_cesionarios_servicios TO www_emiserv;

GRANT SELECT ON ems_hibernate_sequence TO www_emiserv;

GRANT SELECT ON ems_acl_sid_seq TO www_emiserv;

GRANT SELECT ON ems_acl_class_seq TO www_emiserv;

GRANT SELECT ON ems_acl_oid_seq TO www_emiserv;

GRANT SELECT ON ems_acl_entry_seq TO www_emiserv;

GRANT SELECT ON id_clave_privada_sequence TO www_emiserv;

GRANT SELECT ON id_clave_publica_sequence TO www_emiserv;

GRANT SELECT ON id_emisor_sequence TO www_emiserv;

GRANT SELECT ON id_autorizacion_organismo_seq TO www_emiserv;

GRANT SELECT ON id_organismo_cesionario_seq TO www_emiserv;

GRANT SELECT ON id_servicio_sequence TO www_emiserv;

GRANT SELECT ON id_aplicacion_sequence TO www_emiserv;

GRANT SELECT ON id_autorizacion_ca_seq TO www_emiserv;

GRANT SELECT ON id_autorizacion_certific_seq TO www_emiserv;

GRANT SELECT ON id_servicio_cesionario_seq TO www_emiserv;

-- Changeset db/changelog/initial_data.yaml::init-data-scsp-1::limit
INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (0,'Peticion');

INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (1,'ConfirmacionPeticion');

INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (2,'SolicitudRespuesta');

INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (3,'Respuesta');

INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (4,'Fault');

INSERT INTO core_estado_peticion(codigo, mensaje) VALUES ('0001', 'Pendiente');

INSERT INTO core_estado_peticion(codigo, mensaje) VALUES ('0002', 'En proceso');

INSERT INTO core_estado_peticion(codigo, mensaje) VALUES ('0003', 'Tramitada');

INSERT INTO core_estado_peticion(codigo, mensaje) VALUES ('0004', 'En proceso Polling');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (1, 'OU=FNMT Clase 2 CA, O=FNMT, C=ES', 'Fabrica Nacional de Moneda y Timbre');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (2, 'OU=FNMT Clase 2 CA,O=FNMT,C=ES', 'Fabrica Nacional de Moneda y Timbre');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (3, 'OU=AC APE, O=FNMT-RCM, C=ES', 'Fabrica Nacional de Moneda y Timbre');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (4, 'OU=AC APE,O=FNMT-RCM,C=ES', 'Fabrica Nacional de Moneda y Timbre');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (5, 'CN=AC DGP 001, OU=CNP, O=DIRECCION GENERAL DE LA POLICIA, C=ES', 'DIRECCION GENERAL DE LA POLICIA');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (6, 'CN=EC-SAFP, OU=Secretaria d''Administracio i Funcio Publica, OU="Vegeu https://www.catcert.net/verCIC-2 [^] (c)03", OU=Serveis Publics de Certificacio ECV-2, L=Passatge de la Concepcio 11 08008 Barcelona, O=Agencia Catalana de Certificacio (NIF Q-0801176-I), C=ES', 'Secretaria d''Administracio i Funcio Publica');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (7, 'CN=EAEko HAetako langileen CA - CA personal de AAPP vascas (2), OU=AZZ Ziurtagiri publikoa - Certificado publico SCA, O=IZENPE S.A., C=ES', 'IZENPE S.A');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (8, 'CN=AC CAMERFIRMA AAPP, SERIALNUMBER=A82743287, OU=AC CAMERFIRMA, L=MADRID (Ver en https://www.camerfirma.com/address [^]), O=AC CAMERFIRMA S.A., C=ES', 'AC Camerfirma');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (9, 'CN=AC Administración Pública, SERIALNUMBER=Q2826004J, OU=CERES, O=FNMT-RCM, C=ES', 'Fabrica Nacional de Moneda y Timbre');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (10, 'CN=AC Camerfirma Express Corporate Server v3, O=AC Camerfirma SA, OU=http://www.camerfirma.com, [^] SERIALNUMBER=A82743287, L=Madrid (see current address at www.camerfirma.com/address), EMAILADDRESS=info@camerfirma.com, C=ES', 'AC Camerfirma');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (11, 'CN=AC Camerfirma Certificados Camerales, O=AC Camerfirma SA, SERIALNUMBER=A82743287, L=Madrid (see current address at www.camerfirma.com/address), EMAILADDRESS=ac_camerfirma_cc@camerfirma.com, C=ES', 'AC Camerfirma');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (12, 'CN=EC-AL, OU=Administracions Locals de Catalunya, OU="Vegeu https://www.catcert.net/verCIC-2 [^] (c)03", OU=Serveis Publics de Certificacio ECV-2, L=Passatge de la Concepcio 11 08008 Barcelona, O=Agencia Catalana de Certificacio (NIF Q-0801176-I), C=ES', 'Agència Catalana de Certificació CATCert');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (13, 'C=ES, O=Generalitat Valenciana, OU=PKIGVA, CN=ACCV-CA2', 'Autoritat de Certificació de la Comunitat Valenciana (ACCV)');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (14, 'C=ES,O=FNMT,OU=FNMT Clase 2 CA', 'Fabrica Nacional de Moneda y Timbre');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (15, 'C=ES, L=MADRID, O=MINISTERIO DE TRABAJO E INMIGRACION, OU=SUBDIRECCION GENERAL DE PROCESO DE DATOS, OU=PRESTADOR DE SERVICIOS DE CERTIFICACION MTIN, SERIALNUMBER=S2819001E, CN=AC1 RAIZ MTIN', 'Prestador de Servicios de Certificación del Ministerio de Trabajo e Inmigración (PSCMTIN).');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (16, 'C=ES,L=Madrid,E=ac@acabogacia.org,O=Consejo General de la Abogacia NIF:Q-2863006I,OU=Autoridad de Certificacion de la Abogacia,CN=ACA - Certificados Corporativos', 'Autoridad de Certificacion de la Abogacia');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (17, 'C=ES,O=Agencia Catalana de Certificacio (NIF Q-0801176-I),L=Passatge de la Concepcio 11 08008 Barcelona,OU=Serveis Publics de Certificacio ECV-2,OU=Vegeu https://www.catcert.net/verCIC-2 [^] (c)03,OU=Administracions Locals de Catalunya,CN=EC-AL ', 'Agència Catalana de Certificació CATCert');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0101','Error al contactar con el servicio Web especificado {0} {1}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0102','Comunicación sin respuesta {0} {1}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0103','Servidor responde mensaje que no es XML');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0201','Error al generar el identificativo de petición');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0202','Error al insertar la petición {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0203','Error al actualizar el estado {0} {1}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0204','Error al actualizar el TER {0} {1}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0205','Error al actualizar la fecha de último sondeo {0} {1}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0206','Error al actualizar el fichero de respuesta {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0207','Error al recuperar el estado de la petición {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0208','Fichero de respuesta caducado {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0209','Error al comprobar las transmisiones insertadas {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0210','Error al recuperar el CIF del Organismo Requirente {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0211','Error al recuperar el TER {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0212','Error al descomponer el fichero de petición {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0213','Error al recuperar peticiones pendientes. {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0214','Error al insertar las transmisiones. {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0215','Error al actualizar el campo error {0} {1}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0216','Error al actualizar el mensaje de respuesta {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0217','Error al recuperar la CADUCIDAD {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0218','Error al descomponer el mensaje de petición {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0219','Error al recuperar el fichero de respuesta {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0220','Error al enviar la alarma de la petición {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0221','Error al comprobar las peticiones pendientes.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0222','Error al borrar las respuestas caducadas.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0223','Error al escribir el fichero de errores {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0224','Error al borrar el fichero de error {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0225','Se ha alcanzado el número máximo de respuestas para la petición servidas.{0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0226','Error al parsear el XML {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0227','Error al generar la respuesta. {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0229','La petición ya ha sido tramitada o ya existe en el sistema, está repetida');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0230','El timestamp de la petición debe ser válido y de hoy o de ayer. {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0231','Documento Incorrecto {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0232','Documento con más de un identificador.{0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0233','Titular no identificado.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0234','{0}. No se ha encontrado en base de datos configuración alguna para algún certificado asociado al código pasado por parámetro.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0235','El NIF del certificado no coincide con el tag NifSolicitante.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0236','Consentimiento del solicitante inválido. {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0237','Tag NumElementos inválido. {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0238','Información no disponible.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0239','Error al tratar los datos específicos. {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0240','Formato de documento inválido para NIE');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0241','Certificado o Respuesta Caducada');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0242','Error Genérico del BackOffice');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0243','No todas las solicitudes de transmisión hacen referencia al mismo certificado especificado en nodo <Atributos>');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0244','No existe la petición {0} en el sistema');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0246','Error con los id transmision asignados por el backoffice. O todas las transmisiones poseen identificador o ninguna.{0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0247','Error Tag TER no valido');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0248','Error respuesta con un numero de transmisiones diferente a las solicitudes incluidas en la petición {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0253','No todas las solicitudes tienen el mismo identificador de solicitante');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0301','Organismo no autorizado {0} {1}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0302','Certificado caducado {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0303','Certificado revocado {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0304','El DN del Organismo Requirente no coincide con el almacenado para la petición {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0305','Firma no válida {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0306','Error al generar la firma del mensaje {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0307','No se ha encontrado el nodo firma.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0308','Error al obtener la firma del mensaje SOAP {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0309','Error general al verificar el certificado :{0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0310','No se ha podido verificar la CA del certificado.{0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0311','No se ha encontrado el certificado firmante en el documento XML.{0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0312','NIF del emisor especificado no coincide con el Organismo Emisor');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0313','Error al Cifrar o descifrar el mensaje');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0314','Procedimiento {0} No Autorizado a consultar el servicio {1}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0401','La estructura del fichero recibido no corresponde con el esquema.{0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0402','Falta informar campo obligatorio {0} {1}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0403','Imposible obtener el contenido XML del mensaje SOAP.{0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0404','Tipo de documento del titular inválido.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0405','Error al transformar el XML en texto plano a partir de la plantilla {0}.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0406','Contenido del mensaje SOAP no esperado');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0414','El número de elementos no coincide con el número de solicitudes recibidas');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0415','El número de solicitudes es mayor que 1, ejecute el servicio en modo asíncrono.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0419','Existen identificadores de solicitud repetidos.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0501','Error de Base de Datos: {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0502','Error de sistema: {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0504','Error en la configuración {0}');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0901','Servicio no disponible');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0902','Modo síncrono no soportado.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0903','Modo asíncrono no soportado.');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0904','Error general Indefinido {0}');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('cif.emisor','12345678Z','CIF del Emisor');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('nombre.emisor','Emisor1','Nombre del Emisor');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('nivelTraza','DEBUG','Nivel de traza a mostrar en los logs');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('pathLogs','.','Ruta del fichero donde guardaremos los logs');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('logDinamico.enabled','true','Parámetro que indica si se cargará de BBDD la configuración de logs o de fichero');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('core_log.enabled','true','Parámetro que indica si se guardarán trazas en CORE_LOG de los errores ocurridos');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('almacenamiento.ficheros','.','Directorio donde se guardan los ficheros de las peticiones');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('almacenamiento.transmisiones',1,'Parámetro que indica si se guardarán los nodos de las transmisiones');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('keystoreFile','.','Ruta del fichero keystore');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('keystoreType','jks','Tipo de keystore');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('keystorePass','.','Password del keystore');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.enabled','true','Parámetro que indica si @Firma está activo');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.idAplicacion','none','Id de aplicación para enviar a @Firma');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.url','.','URL de @Firma');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.modoValidacion',2,'Modo de validación del certificado con @Firma');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.autenticacion.aliasSignerCert','emisor1','Alias del certificado con el que firmamos la peticiones para @Firma');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('custom.cert.validation.class','none','Clase propia de validación de certificados');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('periodo.validacion.certificados',24,'Periodo de validez del certificado en caché');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('tipoId','long','Longitud del identificador de la petición');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('prefijo.idtransmision','em1','Cuando no haya indicado un prefijo asociado al servicio y este si está definido se utilizará este para la generación del id de transmisión.');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('version.datamodel.scsp','4.2.0','Especifica la versión del modelo de datos actual');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('validate.nif.emisor.enabled','true','Flag que indica si se valida el valor del nodo <NifEmisor> de la petición');

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AlmacenarBaseDatos','AlmacenarBaseDatos',1,1);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('ValidarCertificado','ValidarCertificado',0,0);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AlmacenarFichero','AlmacenarFichero',0,0);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('ValidarEsquema','ValidarEsquema',0,0);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AlmacenarFicheroPlain','AlmacenarFicheroPlain',0,0);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AutorizacionPeticion','AutorizacionPeticion',0,0);

SELECT id_autorizacion_ca_seq.nextval FROM core_em_autorizacion_ca;

