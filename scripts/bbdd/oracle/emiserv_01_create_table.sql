CREATE TABLE ems_backoffice_com (id NUMBER(38, 0) NOT NULL, peticio_id NUMBER(38, 0) NOT NULL, peticio_data TIMESTAMP NOT NULL, peticio_xml CLOB NOT NULL, solicitud_id NUMBER(38, 0), resposta_data TIMESTAMP, resposta_xml CLOB, error CLOB, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_backoffice_com_pk PRIMARY KEY (id));

CREATE TABLE ems_backoffice_pet (id NUMBER(38, 0) NOT NULL, peticio_id VARCHAR2(26) NOT NULL, estat INTEGER NOT NULL, ter_data TIMESTAMP NOT NULL, darrera_sol_data TIMESTAMP, darrera_sol_id VARCHAR2(256), processades_error INTEGER, processades_total INTEGER, comunicacio_id NUMBER(38, 0), version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_backoffice_pet_pk PRIMARY KEY (id));

CREATE TABLE ems_backoffice_sol (id NUMBER(38, 0) NOT NULL, peticio_id NUMBER(38, 0) NOT NULL, solicitud_id VARCHAR2(256), comunicacio_id NUMBER(38, 0), estat INTEGER, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_backoffice_sol_pk PRIMARY KEY (id));

CREATE TABLE ems_redir_missatge (id NUMBER(38, 0) NOT NULL, tipus INTEGER NOT NULL, peticio_id NUMBER(38, 0) NOT NULL, xml CLOB, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_redir_msg_pk PRIMARY KEY (id));

CREATE TABLE ems_redir_peticio (id NUMBER(38, 0) NOT NULL, data_comprovacio TIMESTAMP, data_peticio TIMESTAMP, data_resposta TIMESTAMP, emissor_codi INTEGER, error CLOB, estat VARCHAR2(16) NOT NULL, num_enviaments INTEGER NOT NULL, num_transmissions INTEGER NOT NULL, peticio_id VARCHAR2(128) NOT NULL, servei_codi VARCHAR2(256) NOT NULL, sincrona NUMBER(1), ter TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_redir_pet_pk PRIMARY KEY (id));

CREATE TABLE ems_redir_solicitud (id NUMBER(38, 0) NOT NULL, solicitud_id VARCHAR2(256) NOT NULL, solicitant_id VARCHAR2(40) NOT NULL, solicitant_nom VARCHAR2(256), consentiment VARCHAR2(3), data_generacio TIMESTAMP, error CLOB, estat VARCHAR2(4), finalitat VARCHAR2(256), funcionari_doc VARCHAR2(16), funcionari_nom VARCHAR2(160), procediment_codi VARCHAR2(256), procediment_nom VARCHAR2(256), titular_doc VARCHAR2(16), titular_llinatge1 VARCHAR2(160), titular_llinatge2 VARCHAR2(160), titular_nom VARCHAR2(160), titular_nom_sencer VARCHAR2(256), transmision_id VARCHAR2(256), unitat_tramitadora VARCHAR2(256), peticio_id NUMBER(38, 0) NOT NULL, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_redir_sol_pk PRIMARY KEY (id));

CREATE TABLE ems_servei (id NUMBER(38, 0) NOT NULL, codi VARCHAR2(64) NOT NULL, nom VARCHAR2(256) NOT NULL, tipus INTEGER NOT NULL, descripcio VARCHAR2(1024), configurat NUMBER(1), actiu NUMBER(1), backcaib_async_ter INTEGER, backcaib_async_tipus INTEGER, backcaib_autenticacio INTEGER, backcaib_url VARCHAR2(256), backoffice_class VARCHAR2(256), resolver_class VARCHAR2(256), response_resolver_class VARCHAR2(256), url_per_defecte VARCHAR2(256), xsd_activa NUMBER(1), xsd_esquema_bak VARCHAR2(256), createdby_codi VARCHAR2(64) NOT NULL, createddate TIMESTAMP NOT NULL, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_servei_pk PRIMARY KEY (id));

CREATE TABLE ems_servei_ruta_desti (id NUMBER(38, 0) NOT NULL, entitat_codi VARCHAR2(64) NOT NULL, ordre NUMBER(38, 0), url VARCHAR2(256) NOT NULL, servei_id NUMBER(38, 0) NOT NULL, createdby_codi VARCHAR2(64) NOT NULL, createddate TIMESTAMP NOT NULL, lastmodifiedby_codi VARCHAR2(64), lastmodifieddate TIMESTAMP, version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_servei_rut_des_pk PRIMARY KEY (id));

CREATE TABLE ems_usuari (codi VARCHAR2(64) NOT NULL, idioma VARCHAR2(2) DEFAULT 'CA', version NUMBER(38, 0) NOT NULL, CONSTRAINT ems_usuari_pk PRIMARY KEY (codi));

CREATE TABLE ems_acl_sid (id NUMBER(38, 0) NOT NULL, principal NUMBER(1) NOT NULL, sid VARCHAR2(100) NOT NULL, CONSTRAINT ems_acl_sid_pk PRIMARY KEY (id));

CREATE TABLE ems_acl_class (id NUMBER(38, 0) NOT NULL, class VARCHAR2(100) NOT NULL, CONSTRAINT ems_acl_class_pk PRIMARY KEY (id));

CREATE TABLE ems_acl_object_identity (id NUMBER(38, 0) NOT NULL, object_id_class NUMBER(38, 0) NOT NULL, object_id_identity NUMBER(38, 0) NOT NULL, parent_object NUMBER(38, 0), owner_sid NUMBER(38, 0) NOT NULL, entries_inheriting NUMBER(1) NOT NULL, CONSTRAINT ems_acl_object_identity_pk PRIMARY KEY (id));

CREATE TABLE ems_acl_entry (id NUMBER(38, 0) NOT NULL, acl_object_identity NUMBER(38, 0) NOT NULL, ace_order NUMBER(38, 0) NOT NULL, sid NUMBER(38, 0) NOT NULL, mask INTEGER NOT NULL, granting NUMBER(1) NOT NULL, audit_success NUMBER(1) NOT NULL, audit_failure NUMBER(1) NOT NULL, CONSTRAINT ems_acl_entry_pk PRIMARY KEY (id));

CREATE TABLE core_cache_certificados (numserie VARCHAR2(256) NOT NULL, autoridadcertif VARCHAR2(512) NOT NULL, tiempocomprobacion TIMESTAMP NOT NULL, revocado INTEGER DEFAULT 1 NOT NULL, CONSTRAINT core_cache_certificados_pk PRIMARY KEY (numserie, autoridadcertif));

COMMENT ON TABLE core_cache_certificados IS 'Tabla que registra el resultado de la validación de los certificados empleados en la firma de los mensajes recibidos por el requirente o por el emisor. Así mismo se registra la fecha en la que se realizó dicha validación para poder calcular el periodo de tiempo en la que dicha validación esta vigente.';

COMMENT ON COLUMN core_cache_certificados.numserie IS 'Número de serie del certificado';

COMMENT ON COLUMN core_cache_certificados.autoridadcertif IS 'Autoridad certificadora que emitió el certificado';

COMMENT ON COLUMN core_cache_certificados.tiempocomprobacion IS 'Fecha en la que se realizó el proceso de validación del certificado por última vez';

COMMENT ON COLUMN core_cache_certificados.revocado IS 'Valor booleano (1 o 0) que indicará: -1: si el proceso de validación revocó el certificado. -0: si el proceso de validación aceptó el certificado';

CREATE TABLE core_clave_privada (id NUMBER(38, 0) NOT NULL, alias VARCHAR2(256) NOT NULL, nombre VARCHAR2(256) NOT NULL, passwd VARCHAR2(256) NOT NULL, numeroserie VARCHAR2(256) NOT NULL, fechabaja TIMESTAMP, fechaalta TIMESTAMP NOT NULL, organismo NUMBER(38, 0), interoperabilidad NUMBER(1), CONSTRAINT core_clave_privada_pk PRIMARY KEY (id));

COMMENT ON TABLE core_clave_privada IS 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves privadas disponibles en el almacén de certificados configurado. Las claves privadas aquí configuradas serán utilizadas para la firma de los mensajes emitidos';

COMMENT ON COLUMN core_clave_privada.alias IS 'Alias que identifica de manera unívoca a la clave privada dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';

COMMENT ON COLUMN core_clave_privada.nombre IS 'Nombre descriptivo de la clave privada';

COMMENT ON COLUMN core_clave_privada.passwd IS 'Password de la clave privada necesaria para hacer uso de la misma';

COMMENT ON COLUMN core_clave_privada.numeroserie IS 'Numero de serie de la clave privada';

ALTER TABLE core_clave_privada RENAME COLUMN passwd TO password;

CREATE TABLE core_clave_publica (id NUMBER(38, 0) NOT NULL, alias VARCHAR2(256) NOT NULL, nombre VARCHAR2(256) NOT NULL, numeroserie VARCHAR2(256) NOT NULL, fechaalta TIMESTAMP NOT NULL, fechabaja TIMESTAMP, CONSTRAINT core_clave_publica_pk PRIMARY KEY (id));

COMMENT ON TABLE core_clave_publica IS 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves públicas disponibles en el almacén de certificados configurado. Las claves públicas aquí configuradas serán utilizadas para el posible cifrado de los  mensajes emitidos.';

COMMENT ON COLUMN core_clave_publica.alias IS 'Alias que identifica de manera unívoca a la clave pública dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';

COMMENT ON COLUMN core_clave_publica.nombre IS 'Nombre descriptivo de la clave pública';

COMMENT ON COLUMN core_clave_publica.numeroserie IS 'Numero de serie de la clave pública';

CREATE TABLE core_codigo_error (codigo VARCHAR2(4) NOT NULL, descripcion VARCHAR2(1024) NOT NULL, CONSTRAINT core_codigo_error_pk PRIMARY KEY (codigo));

COMMENT ON TABLE core_codigo_error IS 'Tabla que registrarálos posibles errores genéricos que toda comunicación SCSP puede generar';

COMMENT ON COLUMN core_codigo_error.codigo IS 'Código identificativo del error';

COMMENT ON COLUMN core_codigo_error.descripcion IS 'Litreral descriptivo del error';

CREATE TABLE core_emisor_certificado (id NUMBER(38, 0) NOT NULL, nombre VARCHAR2(50) NOT NULL, cif VARCHAR2(16) NOT NULL, fechaalta TIMESTAMP, fechabaja TIMESTAMP, CONSTRAINT core_emisor_certificado_pk PRIMARY KEY (id));

COMMENT ON TABLE core_emisor_certificado IS 'Esta tabla almacenará los diferentes emisores de servicio configurados';

COMMENT ON COLUMN core_emisor_certificado.nombre IS 'CIF identificativo del organismo emisor de servicios';

COMMENT ON COLUMN core_emisor_certificado.cif IS 'Nombre descriptivo del organismo emisor de servicios';

COMMENT ON COLUMN core_emisor_certificado.fechabaja IS 'Fecha de Baja de emisores de certificados';

CREATE TABLE core_estado_peticion (codigo VARCHAR2(4) NOT NULL, mensaje VARCHAR2(256) NOT NULL, CONSTRAINT core_estado_peticion_pk PRIMARY KEY (codigo));

COMMENT ON TABLE core_estado_peticion IS 'Tabla que almacena los posibles estados en los que se puede encontrar una petición SCSP. Sus posibles valores serán: 0001 - Pendiente, 0002 - En proceso, 0003 - Tramitada';

COMMENT ON COLUMN core_estado_peticion.codigo IS 'Código identificativo del estado';

COMMENT ON COLUMN core_estado_peticion.mensaje IS 'Literal descriptivo del estado';

CREATE TABLE core_modulo (nombre VARCHAR2(256) NOT NULL, descripcion VARCHAR2(512), activoentrada INTEGER DEFAULT 1 NOT NULL, activosalida INTEGER DEFAULT 1 NOT NULL, CONSTRAINT core_modulo_pk PRIMARY KEY (nombre));

COMMENT ON TABLE core_modulo IS 'Tabla que almacena la configuración de activación o desactivación de los módulos que componen el ciclo de ejecución de las librerías SCSP';

COMMENT ON COLUMN core_modulo.nombre IS 'Nombre del módulo';

COMMENT ON COLUMN core_modulo.descripcion IS 'Literal descriptivo del modulo a configurar su activación';

COMMENT ON COLUMN core_modulo.activoentrada IS 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo)';

COMMENT ON COLUMN core_modulo.activosalida IS 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo).';

CREATE TABLE core_modulo_configuracion (certificado NUMBER(38, 0) NOT NULL, modulo VARCHAR2(256) NOT NULL, activoentrada INTEGER DEFAULT 1 NOT NULL, activosalida INTEGER DEFAULT 1 NOT NULL, CONSTRAINT core_modulo_config_pk PRIMARY KEY (certificado, modulo));

COMMENT ON TABLE core_modulo_configuracion IS 'Tabla que permite la sobreescritura de la configuración de activación de un módulo para un servicio concreto';

COMMENT ON COLUMN core_modulo_configuracion.certificado IS 'Código del certificado solicitado';

COMMENT ON COLUMN core_modulo_configuracion.modulo IS 'Nombre del módulo a configurar';

COMMENT ON COLUMN core_modulo_configuracion.activoentrada IS 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo).';

COMMENT ON COLUMN core_modulo_configuracion.activosalida IS 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo).';

CREATE TABLE core_req_modulo_pdf (nombre VARCHAR2(256) NOT NULL, activo INTEGER DEFAULT 1 NOT NULL, orden INTEGER NOT NULL, CONSTRAINT core_req_modulo_pdf_pk PRIMARY KEY (nombre));

CREATE TABLE core_req_modulo_pdf_cesionario (servicio NUMBER(38, 0) NOT NULL, organismo NUMBER(38, 0) NOT NULL, modulo VARCHAR2(256) NOT NULL, activo INTEGER DEFAULT 1 NOT NULL, CONSTRAINT core_req_modpdf_ces_pk PRIMARY KEY (servicio, organismo, modulo));

CREATE TABLE core_em_autorizacion_organismo (id NUMBER(38, 0) NOT NULL, idorganismo VARCHAR2(16) NOT NULL, fechaalta TIMESTAMP NOT NULL, fechabaja TIMESTAMP, nombreorganismo VARCHAR2(64), CONSTRAINT core_em_autorizacion_org_pk PRIMARY KEY (id));

COMMENT ON TABLE core_em_autorizacion_organismo IS 'Organismos que están dados de alta en el emisor y que van a poder consultar los servicios ofrecidos por este a través de alguna de las aplicaciones que tienen autorización para consultar los servicios';

COMMENT ON COLUMN core_em_autorizacion_organismo.idorganismo IS 'Identificador del organismo';

COMMENT ON COLUMN core_em_autorizacion_organismo.fechaalta IS 'Fecha en la que se da de alta el organismo';

COMMENT ON COLUMN core_em_autorizacion_organismo.fechabaja IS 'Fecha a partir de la cual el organismo no va a poder enviar peticiones';

COMMENT ON COLUMN core_em_autorizacion_organismo.nombreorganismo IS 'Nombre descriptivo del organismo requirente de servicios';

CREATE TABLE core_organismo_cesionario (id NUMBER(38, 0) NOT NULL, fechaalta TIMESTAMP NOT NULL, fechabaja TIMESTAMP, nombre VARCHAR2(50) NOT NULL, cif VARCHAR2(50) NOT NULL, bloqueado INTEGER DEFAULT 0 NOT NULL, logo BLOB, codigounidadtramitadora VARCHAR2(9), CONSTRAINT core_organismo_cesionario_pk PRIMARY KEY (id));

CREATE TABLE core_parametro_configuracion (nombre VARCHAR2(64) NOT NULL, valor VARCHAR2(512) NOT NULL, descripcion VARCHAR2(512), CONSTRAINT core_parametro_config_pk PRIMARY KEY (nombre));

COMMENT ON TABLE core_parametro_configuracion IS 'Tabla  que almacena aquellos parámetros de configuración que son globales a todos los servicios y entornos para un mismo cliente integrador de las librerias SCSP';

COMMENT ON COLUMN core_parametro_configuracion.nombre IS 'Nombre identificativo del parámetro';

COMMENT ON COLUMN core_parametro_configuracion.valor IS 'Valor del parámetro';

COMMENT ON COLUMN core_parametro_configuracion.descripcion IS 'Literal descriptivo de la utilidad del parámetro';

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

CREATE TABLE core_tipo_mensaje (tipo INTEGER NOT NULL, descripcion VARCHAR2(32) NOT NULL, CONSTRAINT core_tipo_mensaje_pk PRIMARY KEY (tipo));

COMMENT ON TABLE core_tipo_mensaje IS 'Tabla maestra que almacena los diferentes tipos de mensajes que pueden ser intercambiados a lo largo de un ciclo de comunicación SCSP. Estos valores serán: 0: Peticion, 1: ConfirmacionPeticion, 2: SolicitudRespuesta, 3: Respuesta, 4: Fault';

COMMENT ON COLUMN core_tipo_mensaje.tipo IS 'Tipo identificativo del mensaje';

COMMENT ON COLUMN core_tipo_mensaje.descripcion IS 'Literal descriptivo del tipo de mensaje';

CREATE TABLE core_token_data (idpeticion VARCHAR2(26) NOT NULL, tipomensaje INTEGER NOT NULL, datos CLOB NOT NULL, clave VARCHAR2(256), modoencriptacion VARCHAR2(32), algoritmoencriptacion VARCHAR2(32), CONSTRAINT core_token_data_pk PRIMARY KEY (idpeticion, tipomensaje));

COMMENT ON TABLE core_token_data IS 'Esta tabla almacenará el contenido de los mensajes intercambiados en un proceso de comunicación SCSP';

COMMENT ON COLUMN core_token_data.idpeticion IS 'Identificador de la petición a la cual est� asociado el XML';

COMMENT ON COLUMN core_token_data.tipomensaje IS 'Tipo de mensaje almacenado que podrá ser: Peticion, Respuesta, SolicitudRespuesta, ConfirmacionPeticion, Fault';

COMMENT ON COLUMN core_token_data.datos IS 'Bytes del mensaje almacenado';

COMMENT ON COLUMN core_token_data.clave IS 'Clave simétrica utilizada para el cifrado de los datos';

COMMENT ON COLUMN core_token_data.modoencriptacion IS 'Modo de encriptación utilizado para el proceso de cifrado. Por defecto será TransportKey';

COMMENT ON COLUMN core_token_data.algoritmoencriptacion IS 'Algoritmo empleado en la encriptación del mensaje. Podrá tomar los siguientes valores: - AES128 - AES256 -DESDe';

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

CREATE TABLE core_em_aplicacion (idaplicacion INTEGER NOT NULL, nifcertificado VARCHAR2(16), numeroserie VARCHAR2(64) NOT NULL, cn VARCHAR2(512), tiempocomprobacion TIMESTAMP, autoridadcertif NUMBER(38, 0) NOT NULL, fechaalta TIMESTAMP, fechabaja TIMESTAMP, descripcion VARCHAR2(512), CONSTRAINT core_em_aplicacion_pk PRIMARY KEY (idaplicacion));

COMMENT ON TABLE core_em_aplicacion IS 'Aplicaciones dadas de alta en el emisor que van a emplear los organismos para poder realizar consultas a los servicios ofrecidos por este';

COMMENT ON COLUMN core_em_aplicacion.idaplicacion IS 'Identificador de la aplicación. Se va a generar automáticamente';

COMMENT ON COLUMN core_em_aplicacion.nifcertificado IS 'Nif del certificado con el que la aplicación firmara las peticiones';

COMMENT ON COLUMN core_em_aplicacion.numeroserie IS 'Numero de serie del certificado con el que la aplicación firmara las peticiones';

COMMENT ON COLUMN core_em_aplicacion.cn IS 'Common Name del certificado con el que la aplicación firmara las peticiones';

COMMENT ON COLUMN core_em_aplicacion.tiempocomprobacion IS 'Fecha en que se realizo la operación de validar el certificado';

COMMENT ON COLUMN core_em_aplicacion.fechaalta IS 'Fecha en la que se da de alta la aplicación';

COMMENT ON COLUMN core_em_aplicacion.fechabaja IS 'Fecha a partir de la cual la aplicación no va a poder acceder a ningún servicio';

CREATE TABLE core_em_autorizacion_ca (id NUMBER(38, 0) NOT NULL, codca VARCHAR2(512) NOT NULL, nombre VARCHAR2(256), CONSTRAINT core_em_autorizacion_ca_pk PRIMARY KEY (id));

COMMENT ON TABLE core_em_autorizacion_ca IS 'Contiene la información relativa a las distintas autoridades de certificación reconocidas por la aplicación';

COMMENT ON COLUMN core_em_autorizacion_ca.codca IS 'Codigo de la autoridad de certificación (Ej: FNMT)';

COMMENT ON COLUMN core_em_autorizacion_ca.nombre IS 'Nombre de la autoridad de certificación  (Ej: Fabrica Nacional de Moneda y Timbre)';

CREATE TABLE core_em_autorizacion_cert (id NUMBER(38, 0) NOT NULL, idcertificado NUMBER(38, 0) NOT NULL, idorganismo NUMBER(38, 0) NOT NULL, idaplicacion INTEGER NOT NULL, fechaalta TIMESTAMP, fechabaja TIMESTAMP, CONSTRAINT core_em_autorizacion_cert_pk PRIMARY KEY (id));

COMMENT ON TABLE core_em_autorizacion_cert IS 'Contiene la información relativa a las autorizaciones de los distintas aplicaciones para acceder a los servicios ofrecidos por el emisor';

COMMENT ON COLUMN core_em_autorizacion_cert.idorganismo IS 'Identificador del organismo';

COMMENT ON COLUMN core_em_autorizacion_cert.idaplicacion IS 'Identificador de la aplicación autorizada';

COMMENT ON COLUMN core_em_autorizacion_cert.fechaalta IS 'Fecha de alta de la autorización';

COMMENT ON COLUMN core_em_autorizacion_cert.fechabaja IS 'Fecha de baja de la autorización';

CREATE TABLE core_em_codigo_error_secun (codigo VARCHAR2(4) NOT NULL, codigosecundario VARCHAR2(16) NOT NULL, descripcion CLOB NOT NULL, CONSTRAINT core_em_cod_error_secun_pk PRIMARY KEY (codigo, codigosecundario));

COMMENT ON TABLE core_em_codigo_error_secun IS 'Tabla de errores que permitirá almacenar aquellos mensajes de excepción gestionados por cada lógica de negocio específica de cada emisor, y que está asociado a un error genérico de SCSP';

COMMENT ON COLUMN core_em_codigo_error_secun.codigo IS 'Código identificativo del error especifico secundario';

COMMENT ON COLUMN core_em_codigo_error_secun.codigosecundario IS 'Codigo de error SCSP al que esta asociado el error secundario';

COMMENT ON COLUMN core_em_codigo_error_secun.descripcion IS 'Literal descriptivo del mensaje de error secundario';

CREATE TABLE core_em_backoffice (certificado NUMBER(38, 0) NOT NULL, beanname VARCHAR2(256), classname VARCHAR2(256), ter INTEGER NOT NULL, CONSTRAINT core_em_backoffice_pk PRIMARY KEY (certificado));

COMMENT ON TABLE core_em_backoffice IS 'Tabla en la que se configura el módulo de backoffice del emisor que enlaza las comunicaciones SCSP con la logica de negocio especifica de cada organismo';

COMMENT ON COLUMN core_em_backoffice.beanname IS 'Identificador del bean del contexto de Spring que contiene la clase que ofrece la puerta de entrada al backoffice';

COMMENT ON COLUMN core_em_backoffice.classname IS 'Nombre completo  de la clase que ofrece la puerta de entrada al backoffice';

COMMENT ON COLUMN core_em_backoffice.ter IS 'Número de horas que harán esperar a un requirente para la generación de una respuesta definitiva ante una petición asíncrona';

CREATE TABLE core_em_secuencia_idtrans (prefijo VARCHAR2(9) NOT NULL, secuencia VARCHAR2(26) NOT NULL, fechageneracio TIMESTAMP NOT NULL, CONSTRAINT core_em_secuencia_idtrans_pk PRIMARY KEY (prefijo));

COMMENT ON TABLE core_em_secuencia_idtrans IS 'Tabla utilizada para la generación de los valores de los nodos IdTransmisión en los emisores';

COMMENT ON COLUMN core_em_secuencia_idtrans.prefijo IS 'Prefijo de IdTransmisiones al que estará asociada la secuencia';

COMMENT ON COLUMN core_em_secuencia_idtrans.secuencia IS 'Valor actual de la secuencia alfanumérica asociada al prefijo';

COMMENT ON COLUMN core_em_secuencia_idtrans.fechageneracio IS 'Última fecha en la que se ha generado un valor de secuencia';

CREATE TABLE core_req_secuencia_id_peticion (prefijo VARCHAR2(9) NOT NULL, secuencia VARCHAR2(23) NOT NULL, fechageneracion TIMESTAMP NOT NULL, CONSTRAINT core_req_secuencia_id_pet_pk PRIMARY KEY (prefijo));

COMMENT ON TABLE core_req_secuencia_id_peticion IS 'Tabla que almacena las semillas (secuenciales) utilizadas para la generación de los identificadores de peticion. Existirá un secuencial asociado a cada posible prefijo o número de serie de certificado digital firmante.';

COMMENT ON COLUMN core_req_secuencia_id_peticion.prefijo IS 'Prefijo utilizado para la construccion de los identificadores. Dicho valor podrá ser el prefijo especificado a ser utilizado para cada servicio o ante la no existencia del mismo, el número de serie del certificado digital firmante  de los mensajes.';

COMMENT ON COLUMN core_req_secuencia_id_peticion.secuencia IS 'Valor secuencial que será concatenado al prefijo para generar los identificadores de petición. Este secuencial será de tipo alfanumérico, de tal forma que el siguiente valor a 00000009 sería 0000000A.';

COMMENT ON COLUMN core_req_secuencia_id_peticion.fechageneracion IS 'Fecha en la que se registró el secuencial';

CREATE TABLE core_req_cesionarios_servicios (id NUMBER(38, 0) NOT NULL, servicio NUMBER(38, 0), claveprivada NUMBER(38, 0), organismo NUMBER(38, 0), fechaalta TIMESTAMP(6) NOT NULL, fechabaja TIMESTAMP(6), bloqueado NUMBER(1) NOT NULL, sslflag NUMBER(1) NOT NULL, CONSTRAINT core_req_cesionarios_serv_pk PRIMARY KEY (id));
