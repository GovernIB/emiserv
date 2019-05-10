CREATE TABLE CORE_BLOQUEO 
  ( 
     IDBLOQUEO       VARCHAR(64) NOT NULL, 
     FECHAEXPIRACION timestamp (6) NOT NULL
  ); 

COMMENT ON COLUMN CORE_BLOQUEO.IDBLOQUEO IS 'identificador del recurso a ser bloqueado'; 

COMMENT ON COLUMN CORE_BLOQUEO.FECHAEXPIRACION IS 'fecha a partir de la cual un bloqueo deja de tener vigencia. utilizado para evitar que un proceso que haya acabado con un fallo inesperado deje un recurso inutilizado'; 

COMMENT ON TABLE CORE_BLOQUEO IS 'tabla gestionada por las librerias para el establecimiento de bloqueos sobre un recurso que puede ser compartido de manera simultánea por varios procesos, pero cuyo acceso debe ser secuencial. su principal uso será el control del acceso al secuencial que permite generar identificadores de petición de la tabla core_secuencia_idpeticion'; 

CREATE TABLE CORE_CACHE_CERTIFICADOS 
  ( 
     NUMSERIE           VARCHAR(256) NOT NULL, 
     AUTORIDADCERTIF    VARCHAR(512) NOT NULL, 
     TIEMPOCOMPROBACION timestamp (6) NOT NULL, 
     REVOCADO           bigint NOT NULL
  ); 

COMMENT ON COLUMN CORE_CACHE_CERTIFICADOS.NUMSERIE IS 'número de serie del certificado'; 

COMMENT ON COLUMN CORE_CACHE_CERTIFICADOS.AUTORIDADCERTIF IS 'autoridad certificadora que emitió el certificado'; 

COMMENT ON COLUMN CORE_CACHE_CERTIFICADOS.TIEMPOCOMPROBACION IS 'fecha en la que se realizó el proceso de validación del certificado por última vez' ; 

COMMENT ON COLUMN CORE_CACHE_CERTIFICADOS.REVOCADO IS 'valor booleano (‘1’ o ‘0’) que indicará: - ‘1’:   si el proceso de validación revocó el certificado. - ‘0’: si el proceso de validación aceptó el certificado' ; 

COMMENT ON TABLE CORE_CACHE_CERTIFICADOS IS 'tabla que registra el resultado de la validación de los certificados   empleados en la firma de los mensajes recibidos por el requirente o por el emisor. así mismo se registra la fecha en la que se realizó dicha validación para poder calcular el periodo de tiempo en la que dicha validación esta vigente'; 

CREATE TABLE CORE_ORGANISMO_CESIONARIO 
  ( 
     ID        DECIMAL(19, 0), 
     NOMBRE    VARCHAR(50), 
     CIF       VARCHAR(50), 
     FECHAALTA timestamp (6), 
     FECHABAJA timestamp (6), 
     BLOQUEADO DOUBLE PRECISION DEFAULT 0 NOT NULL, 
     LOGO      bytea
  ); 

CREATE TABLE CORE_CLAVE_PRIVADA 
  ( 
     ALIAS             VARCHAR(256) NOT NULL, 
     NOMBRE            VARCHAR(256) NOT NULL, 
     PASSWORD          VARCHAR(256) NOT NULL, 
     NUMEROSERIE       VARCHAR(256) NOT NULL, 
     ID                DECIMAL(19, 0) NOT NULL, 
     FECHAALTA         timestamp (6) NOT NULL, 
     FECHABAJA         timestamp (6), 
     ORGANISMO         DECIMAL(19, 0), 
     INTEROPERABILIDAD INT
  ); 

COMMENT ON COLUMN CORE_CLAVE_PRIVADA.ALIAS IS 'alias que identifica de manera unívoca a la clave privada dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystorefile'; 

COMMENT ON COLUMN CORE_CLAVE_PRIVADA.NOMBRE IS 'nombre descriptivo de la clave privada'; 

COMMENT ON COLUMN CORE_CLAVE_PRIVADA.PASSWORD IS 'password de la clave privada necesaria para hacer uso de la misma'; 

COMMENT ON COLUMN CORE_CLAVE_PRIVADA.NUMEROSERIE IS 'numero de serie de la clave privada'; 

COMMENT ON TABLE CORE_CLAVE_PRIVADA IS 'esta tabla almacenará los datos de configuración necesarios para acceder a las claves privadas disponibles en el almacén de certificados configurado. las claves privadas aquí configuradas serán utilizadas para la firma de los mensajes emitidos'; 

CREATE TABLE CORE_CLAVE_PUBLICA 
  ( 
     ALIAS       VARCHAR(256) NOT NULL, 
     NOMBRE      VARCHAR(256) NOT NULL, 
     NUMEROSERIE VARCHAR(256) NOT NULL, 
     ID          DOUBLE PRECISION NOT NULL, 
     FECHAALTA   timestamp (6) NOT NULL, 
     FECHABAJA   timestamp (6)
  ); 

COMMENT ON COLUMN CORE_CLAVE_PUBLICA.ALIAS IS 'alias que identifica de manera unívoca a la clave pública dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystorefile'; 

COMMENT ON COLUMN CORE_CLAVE_PUBLICA.NOMBRE IS 'nombre descriptivo de la clave pública'; 

COMMENT ON COLUMN CORE_CLAVE_PUBLICA.NUMEROSERIE IS 'numero de serie de la clave publica'; 

COMMENT ON TABLE CORE_CLAVE_PUBLICA IS 'esta tabla almacenará los datos de configuración necesarios para acceder a las claves públicas disponibles en el almacén de certificados configurado. las claves públicas  aquí configuradas serán utilizadas para el posible cifrado de los  mensajes emitidos'; 

CREATE TABLE CORE_CODIGO_ERROR 
  ( 
     CODIGO      VARCHAR(4) NOT NULL, 
     DESCRIPCION VARCHAR(1024) NOT NULL
  ); 

COMMENT ON COLUMN CORE_CODIGO_ERROR.CODIGO IS 'código identificativo del error' ; 

COMMENT ON COLUMN CORE_CODIGO_ERROR.DESCRIPCION IS 'litreral descriptivo del error'; 

COMMENT ON TABLE CORE_CODIGO_ERROR IS 'tabla que registrará los posibles errores genéricos que toda comunicación scsp puede generar' ; 

CREATE TABLE CORE_EMISOR_CERTIFICADO 
  ( 
     CIF       VARCHAR(16) NOT NULL, 
     NOMBRE    VARCHAR(50) NOT NULL, 
     FECHABAJA timestamp (6), 
     ID        DECIMAL(19, 0)
  ); 

COMMENT ON COLUMN CORE_EMISOR_CERTIFICADO.CIF IS 'nombre descriptivo del organismo emisor de servicios'; 

COMMENT ON COLUMN CORE_EMISOR_CERTIFICADO.NOMBRE IS 'cif identificativo del organismo emisor de servicios'; 

COMMENT ON COLUMN CORE_EMISOR_CERTIFICADO.FECHABAJA IS 'fecha de baja de emisores de certificados'; 

COMMENT ON TABLE CORE_EMISOR_CERTIFICADO IS 'esta tabla almacenará los diferentes emisores de servicio configurados'; 

CREATE TABLE CORE_EM_AUTORIZACION_CA 
  ( 
     ID     DECIMAL(19, 0), 
     CODCA  VARCHAR(512) NOT NULL, 
     NOMBRE VARCHAR(256)
  ); 

COMMENT ON COLUMN CORE_EM_AUTORIZACION_CA.CODCA IS 'codigo de la autoridad de certificación (ej: fnmt)'; 

COMMENT ON COLUMN CORE_EM_AUTORIZACION_CA.NOMBRE IS 'nombre de la autoridad de certificación  (ej: fabrica nacional de moneda y timbre)' ; 

COMMENT ON TABLE CORE_EM_AUTORIZACION_CA IS 'contiene la información relativa a las distintas autoridades de certificación reconocidas por la aplicación'; 

CREATE TABLE CORE_EM_APLICACION 
  ( 
     IDAPLICACION       bigint NOT NULL, 
     NIFCERTIFICADO     VARCHAR(16), 
     NUMEROSERIE        VARCHAR(64) NOT NULL, 
     CN                 VARCHAR(512) NOT NULL, 
     TIEMPOCOMPROBACION timestamp (6), 
     AUTORIDADCERTIF    VARCHAR(512), 
     DESCRIPCION        VARCHAR(512), 
     FECHAALTA          timestamp (6), 
     FECHABAJA          timestamp (6)
  ); 

COMMENT ON COLUMN CORE_EM_APLICACION.IDAPLICACION IS 'identificador de la aplicación. se va a generar automáticamente'; 

COMMENT ON COLUMN CORE_EM_APLICACION.NIFCERTIFICADO IS 'nif del certificado con el que la aplicación firmara las peticiones'; 

COMMENT ON COLUMN CORE_EM_APLICACION.NUMEROSERIE IS 'numero de serie del certificado con el que la aplicación firmara las peticiones' ; 

COMMENT ON COLUMN CORE_EM_APLICACION.CN IS 'common name del certificado con el que la aplicación firmara las peticiones'; 

COMMENT ON COLUMN CORE_EM_APLICACION.TIEMPOCOMPROBACION IS 'fecha en que se realizo la operación de validar el certificado'; 

COMMENT ON COLUMN CORE_EM_APLICACION.AUTORIDADCERTIF IS 'autoridad de certificación del certificado'; 

COMMENT ON COLUMN CORE_EM_APLICACION.FECHAALTA IS 'fecha en la que se da de alta la aplicación'; 

COMMENT ON COLUMN CORE_EM_APLICACION.FECHABAJA IS 'fecha a partir de la cual la aplicación no va a poder acceder a ningún servicio' ; 

COMMENT ON TABLE CORE_EM_APLICACION IS 'aplicaciones dadas de alta en el emisor que van a emplear los organismos para poder realizar consultas a los servicios ofrecidos por este'; 

CREATE TABLE CORE_SERVICIO 
  ( 
     CODCERTIFICADO         VARCHAR(64) NOT NULL, 
     URLSINCRONA            VARCHAR(256), 
     URLASINCRONA           VARCHAR(256), 
     ACTIONSINCRONA         VARCHAR(256), 
     ACTIONASINCRONA        VARCHAR(256), 
     ACTIONSOLICITUD        VARCHAR(256), 
     VERSIONESQUEMA         VARCHAR(32) NOT NULL, 
     TIPOSEGURIDAD          VARCHAR(16) NOT NULL, 
     PREFIJOPETICION        VARCHAR(9), 
     XPATHCIFRADOSINCRONO   VARCHAR(256), 
     XPATHCIFRADOASINCRONO  VARCHAR(256), 
     ESQUEMAS               VARCHAR(256), 
     ALGORITMOCIFRADO       VARCHAR(32), 
     NUMEROMAXIMOREENVIOS   bigint NOT NULL, 
     MAXSOLICITUDESPETICION bigint NOT NULL, 
     PREFIJOIDTRANSMISION   VARCHAR(9), 
     DESCRIPCION            VARCHAR(512) NOT NULL, 
     FECHAALTA              timestamp (6) NOT NULL, 
     FECHABAJA              timestamp (6), 
     CADUCIDAD              bigint, 
     XPATHLITERALERROR      VARCHAR(256), 
     XPATHCODIGOERROR       VARCHAR(256), 
     TIMEOUT                bigint DEFAULT 60 NOT NULL, 
     VALIDACIONFIRMA        VARCHAR(32) DEFAULT 'estricto', 
     EMISOR                 DECIMAL(19, 0), 
     PLANTILLAXSLT          VARCHAR(512), 
     CLAVECIFRADO           DECIMAL(19, 0), 
     CLAVEFIRMA             DECIMAL(19, 0), 
     ID                     DECIMAL(19, 0)
  ); 

COMMENT ON COLUMN CORE_SERVICIO.CODCERTIFICADO IS 'código del certificado que lo identifica unívocamente en las comunicaciones scsp' ; 

COMMENT ON COLUMN CORE_SERVICIO.URLSINCRONA IS 'endpoint de acceso al servicio de tipo síncrono'; 

COMMENT ON COLUMN CORE_SERVICIO.URLASINCRONA IS 'endpoint de acceso al servicio de tipo asíncrono del certificado'; 

COMMENT ON COLUMN CORE_SERVICIO.ACTIONSINCRONA IS 'valor del soap:action de la petición síncrona utilizado por el servidor ws en el caso de que sea necesario'; 

COMMENT ON COLUMN CORE_SERVICIO.ACTIONASINCRONA IS 'valor del soap:action de la petición asíncrona utilizado por el servidor ws en el caso de que sea necesario'; 

COMMENT ON COLUMN CORE_SERVICIO.ACTIONSOLICITUD IS 'valor del soap:action de la solicitud asíncrona de respuesta utilizado por el servidor ws en el caso de que sea necesario'; 

COMMENT ON COLUMN CORE_SERVICIO.VERSIONESQUEMA IS 'indica la versión de esquema utilizado en los mensajes scsp pudiendo tomar los valores v2 y v3 (existe la posibilidad de añadir otros valores utilizando configuraciones avandadas de binding)'; 

COMMENT ON COLUMN CORE_SERVICIO.TIPOSEGURIDAD IS 'indica la politica de seguridad utilizada en la securización de los mensajes, pudiendo tomar los valores [xmlsignature| ws-security]' ; 

COMMENT ON COLUMN CORE_SERVICIO.PREFIJOPETICION IS 'literal con una longitud máxima de 8 caracteres, el cual será utilizado para la construcción de los identificadores de petición, anteponiendose a un valor secuencial. mediante este literal pueden personalizarse los identificadores de petición haciendolos más descriptivos'; 

COMMENT ON COLUMN CORE_SERVICIO.XPATHCIFRADOSINCRONO IS 'literal que identifica el nodo del mensaje xml a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. se corresponde con una expresión xpath que permite localizar  al nodo en el mensaje xml, presentará el siguiente formato //*[local-name()=´nodo_a_cifrar´], donde ´nodo_a_cifrar´ es el local name del nodo que se desea cifrar. este nodo será empleado en el caso de realizar comunicaciones síncronas'; 

COMMENT ON COLUMN CORE_SERVICIO.XPATHCIFRADOASINCRONO IS 'literal que identifica el nodo del mensaje xml a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. se corresponde con una expresión xpath que permite localizar  al nodo en el mensaje xml, presentará el siguiente formato //*[local-name()=´nodo_a_cifrar´], donde ´nodo_a_cifrar´ es el local name del nodo que se desea cifrar. este nodo será empleado en el caso de realizar comunicaciones asíncronas'; 

COMMENT ON COLUMN CORE_SERVICIO.ESQUEMAS IS 'ruta que indica el directorio donde se encuentran los esquemas (*.xsd) con el que se validará el xml de los diferentes mensajes intercambiados. esta ruta podrá tomar un valor relativo haciendo referencia al classpath de la aplicación o  un path absoluto'; 

COMMENT ON COLUMN CORE_SERVICIO.ALGORITMOCIFRADO IS 'literal que identifica el algoritmo utilizado para el cifrado de los mensajes enviados al emisor. debe poseer un valor reconocido por las librerías de rampart: - basic128rsa15 - tripledesrsa15  - basic256rsa15'; 

COMMENT ON COLUMN CORE_SERVICIO.NUMEROMAXIMOREENVIOS IS 'valor que indica en el caso del requirente, el número máximo de reenvios que pueden llevarse a cabo sobre una petición asíncrona. en el caso del emisor, hace referencia al número máximo de veces que procesará y creará una respuesta para una petición con un mismo identificador'; 

COMMENT ON COLUMN CORE_SERVICIO.MAXSOLICITUDESPETICION IS 'número máximo de solicitudes de transmisión que se van a permitir por petición' ; 

COMMENT ON COLUMN CORE_SERVICIO.PREFIJOIDTRANSMISION IS 'semilla empleada por el emisor para generar los identificadores de transmisión de las respuestas. será un valor alfanumérico con un mínimo de 3 caracteres y un máximo de 8'; 

COMMENT ON COLUMN CORE_SERVICIO.DESCRIPCION IS 'literal descriptivo del servicio a solicitar utilizando el código de certificado' ; 

COMMENT ON COLUMN CORE_SERVICIO.FECHAALTA IS 'timestamp con la fecha en la cual el certificado se dió de alta en el sistema y por lo tanto a partir de la cual se podrán emitir peticiones al mismo'; 

COMMENT ON COLUMN CORE_SERVICIO.FECHABAJA IS 'timestamp con la fecha en la cual el certificado se dio de baja en el sistema y por tanto a partir de la cual no se podrán emitir peticiones al mismo'; 

COMMENT ON COLUMN CORE_SERVICIO.CADUCIDAD IS 'número de dias que deberán sumarse a la fecharespuesta de una petición, para calcular la fecha a partir de la cual se podrá considerar que la respuesta esta caducada y se devolvera el error scsp correspondiente para indicar que la respuesta ha perdido su valor'; 

COMMENT ON COLUMN CORE_SERVICIO.XPATHLITERALERROR IS 'xpath para recuperar el literal del error de los datos específicos'; 

COMMENT ON COLUMN CORE_SERVICIO.XPATHCODIGOERROR IS 'xpath para recuperar el codigo de error de los datos específicos'; 

COMMENT ON COLUMN CORE_SERVICIO.TIMEOUT IS 'timeout que se establecerá para el envío de las peticiones a los servicios'; 

COMMENT ON COLUMN CORE_SERVICIO.VALIDACIONFIRMA IS 'parámetro que indica si se admite otro tipo de firma en el servicio además del configurado' ; 

COMMENT ON TABLE CORE_SERVICIO IS 'esta tabla registrará cada uno de los certificados scsp que van a ser utilizados por el sistema tanto de la parte requirente como de la parte emisora'; 

CREATE TABLE CORE_EM_AUTORIZACION_ORGANISMO 
  ( 
     IDORGANISMO     VARCHAR(16) NOT NULL, 
     FECHAALTA       timestamp (6) NOT NULL, 
     FECHABAJA       timestamp (6), 
     NOMBREORGANISMO VARCHAR(64), 
     ID              DECIMAL(19, 0)
  ); 

COMMENT ON COLUMN CORE_EM_AUTORIZACION_ORGANISMO.IDORGANISMO IS 'identificador del organismo'; 

COMMENT ON COLUMN CORE_EM_AUTORIZACION_ORGANISMO.FECHAALTA IS 'fecha en la que se da de alta el organismo'; 

COMMENT ON COLUMN CORE_EM_AUTORIZACION_ORGANISMO.FECHABAJA IS 'fecha a partir de la cual el organismo no va a poder enviar peticiones'; 

COMMENT ON COLUMN CORE_EM_AUTORIZACION_ORGANISMO.NOMBREORGANISMO IS 'nombre descriptivo del organismo requirente de servicios'; 

COMMENT ON TABLE CORE_EM_AUTORIZACION_ORGANISMO IS 'organismos que están dados de alta en el emisor y que van a poder consultar los servicios ofrecidos por este a través de alguna de las aplicaciones que tienen autorización para consultar los servicios'; 

CREATE TABLE CORE_EM_AUTORIZACION_CERT 
  ( 
     IDAPLICACION  bigint NOT NULL, 
     FECHAALTA     timestamp (6) NOT NULL, 
     FECHABAJA     timestamp (6), 
     ID            DECIMAL(19, 0), 
     IDCERTIFICADO DECIMAL(19, 0), 
     IDORGANISMO   DECIMAL(19, 0)
  ); 

COMMENT ON COLUMN CORE_EM_AUTORIZACION_CERT.IDAPLICACION IS'identificador de la aplicación autorizada'; 

COMMENT ON COLUMN CORE_EM_AUTORIZACION_CERT.FECHAALTA IS 'fecha de alta de la autorización'; 

COMMENT ON COLUMN CORE_EM_AUTORIZACION_CERT.FECHABAJA IS 'fecha de baja de la autorización'; 

COMMENT ON TABLE CORE_EM_AUTORIZACION_CERT IS 'contiene la información relativa a las autorizaciones de los distintas aplicaciones para acceder a los servicios ofrecidos por el emisor'; 

CREATE TABLE CORE_EM_BACKOFFICE 
  ( 
     BEANNAME    VARCHAR(256), 
     CLASSNAME   VARCHAR(256), 
     TER         bigint NOT NULL, 
     CERTIFICADO DECIMAL(19, 0) 
  ); 

COMMENT ON COLUMN CORE_EM_BACKOFFICE.BEANNAME IS 'identificador del bean del contexto de spring que contiene la clase que ofrece la puerta de entrada al backoffice'; 

COMMENT ON COLUMN CORE_EM_BACKOFFICE.CLASSNAME IS 'nombre completo  de la clase que ofrece la puerta de entrada al backoffice'; 

COMMENT ON COLUMN CORE_EM_BACKOFFICE.TER IS 'número de horas que harán esperar a un requirente para la generación de una respuesta definitiva ante una petición asíncrona'; 

COMMENT ON TABLE CORE_EM_BACKOFFICE IS 'tabla en la que se configura el módulo de backoffice del emisor que enlaza las comunicaciones scsp con la logica de negocio especifica de cada organismo'; 

CREATE TABLE CORE_EM_CODIGO_ERROR_SECUN 
  ( 
     CODIGOSECUNDARIO VARCHAR(4) NOT NULL, 
     CODIGO           VARCHAR(4) NOT NULL, 
     DESCRIPCION      text NOT NULL
  ); 

COMMENT ON COLUMN CORE_EM_CODIGO_ERROR_SECUN.CODIGOSECUNDARIO IS 'codigo de error scsp al que esta asociado el error secundario'; 

COMMENT ON COLUMN CORE_EM_CODIGO_ERROR_SECUN.CODIGO IS 'código identificativo del error especifico secundario'; 

COMMENT ON COLUMN CORE_EM_CODIGO_ERROR_SECUN.DESCRIPCION IS 'literal descriptivo del mensaje de error secundario'; 

COMMENT ON TABLE CORE_EM_CODIGO_ERROR_SECUN IS 'tabla de errores que permitirá almacenar aquellos mensajes de excepción gestionados por cada lógica de negocio específica de cada emisor, y que estrá asociado a un error genérico de scsp'; 

CREATE TABLE CORE_EM_SECUENCIA_IDTRANS 
  ( 
     PREFIJO         VARCHAR(9) NOT NULL, 
     SECUENCIA       VARCHAR(26) NOT NULL, 
     FECHAGENERACION timestamp (6) NOT NULL
  ); 

COMMENT ON COLUMN CORE_EM_SECUENCIA_IDTRANS.PREFIJO IS 'prefijo de idtransmisiones al que estará asociada la secuencia'; 

COMMENT ON COLUMN CORE_EM_SECUENCIA_IDTRANS.SECUENCIA IS 'valor actual de la secuencia alfanumérica asociada al prefijo'; 

COMMENT ON COLUMN CORE_EM_SECUENCIA_IDTRANS.FECHAGENERACION IS 'última fecha en la que se ha generado un valor de secuencia'; 

COMMENT ON TABLE CORE_EM_SECUENCIA_IDTRANS IS 'tabla utilizada para la generación de los valores de los nodos idtransmisión en los emisores'; 

CREATE TABLE CORE_ESTADO_PETICION 
  ( 
     CODIGO  VARCHAR(4) NOT NULL, 
     MENSAJE VARCHAR(256) NOT NULL
  ); 

COMMENT ON COLUMN CORE_ESTADO_PETICION.CODIGO IS 'código identificativo del estado'; 

COMMENT ON COLUMN CORE_ESTADO_PETICION.MENSAJE IS 'literal descriptivo del estado'; 

COMMENT ON TABLE CORE_ESTADO_PETICION IS 'tabla que almacena los posibles estados en los que se puede encontrar una petición scsp. sus posibles valores serán: • 0001 - pendiente •	0002 - en proceso •	0003 - tramitada'; 

CREATE TABLE CORE_LOG 
  ( 
     ID         DECIMAL(19, 0) NOT NULL, 
     FECHA      timestamp (6), 
     CRITICIDAD VARCHAR(10), 
     CLASE      VARCHAR(256), 
     METODO     VARCHAR(64), 
     MENSAJE    text
  ); 

COMMENT ON COLUMN CORE_LOG.ID IS 'valor incremental autogenerado'; 

COMMENT ON COLUMN CORE_LOG.FECHA IS 'fecha  en la que se generó la traza de log'; 

COMMENT ON COLUMN CORE_LOG.CRITICIDAD IS 'tipo de nivel de la traza de log (warn,info,debug,all,error)'; 

COMMENT ON COLUMN CORE_LOG.CLASE IS 'clase que generó la traza de log registrada'; 

COMMENT ON COLUMN CORE_LOG.METODO IS 'método específico de la clase que generó la traza de log'; 

COMMENT ON COLUMN CORE_LOG.MENSAJE IS 'literal descriptivo del error almacenado'; 

COMMENT ON TABLE CORE_LOG IS 'tabla de configurada en el appender log4j para base de datos de la aplicación, donde se registrarán las posibles trazas de error'; 

CREATE TABLE CORE_MODULO 
  ( 
     NOMBRE        VARCHAR(256) NOT NULL, 
     DESCRIPCION   VARCHAR(512), 
     ACTIVOENTRADA INT NOT NULL, 
     ACTIVOSALIDA  INT NOT NULL
  ); 

COMMENT ON COLUMN CORE_MODULO.NOMBRE IS 'nombre del módulo'; 

COMMENT ON COLUMN CORE_MODULO.DESCRIPCION IS 'literal descriptivo del modulo a configurar su activación'; 

COMMENT ON COLUMN CORE_MODULO.ACTIVOENTRADA IS 'valor que indica si el módulo esta activo en la emisión de mensajes. puede tomar valor 1 (activo) ó 0 (inactivo)'; 

COMMENT ON COLUMN CORE_MODULO.ACTIVOSALIDA IS 'valor que indica si el módulo esta activo en la recepción de mensajes. puede tomar valor 1 (activo) ó 0 (inactivo)'; 

COMMENT ON TABLE CORE_MODULO IS 'tabla que almacena la configuración de activación  o desactivación de los módulos que componen el ciclo de ejecución de las librerías scsp'; 

CREATE TABLE CORE_MODULO_CONFIGURACION 
  ( 
     MODULO        VARCHAR(256) NOT NULL, 
     ACTIVOENTRADA INT NOT NULL, 
     ACTIVOSALIDA  INT NOT NULL, 
     CERTIFICADO   DECIMAL(19, 0)
  ); 

COMMENT ON COLUMN CORE_MODULO_CONFIGURACION.MODULO IS 'nombre del módulo a configurar'; 

COMMENT ON COLUMN CORE_MODULO_CONFIGURACION.ACTIVOENTRADA IS 'valor que indica si el módulo esta activo en la emisión de mensajes. puede tomar valor 1 (activo) ó 0 (inactivo)'; 

COMMENT ON COLUMN CORE_MODULO_CONFIGURACION.ACTIVOSALIDA IS 'valor que indica si el módulo esta activo en la recepción de mensajes. puede tomar valor 1 (activo) ó 0 (inactivo)'; 

COMMENT ON TABLE CORE_MODULO_CONFIGURACION IS 'tabla que permite la sobreescritura de la configuración de activación de un módulo para un servicio concreto'; 

CREATE TABLE CORE_PARAMETRO_CONFIGURACION 
  ( 
     NOMBRE      VARCHAR(64) NOT NULL, 
     VALOR       VARCHAR(512) NOT NULL, 
     DESCRIPCION VARCHAR(512)
  ); 

COMMENT ON COLUMN CORE_PARAMETRO_CONFIGURACION.NOMBRE IS 'nombre identificativo del parámetro'; 

COMMENT ON COLUMN CORE_PARAMETRO_CONFIGURACION.VALOR IS 'valor del parámetro'; 

COMMENT ON COLUMN CORE_PARAMETRO_CONFIGURACION.DESCRIPCION IS 'literal descriptivo de la utilidad del parámetro'; 

COMMENT ON TABLE CORE_PARAMETRO_CONFIGURACION IS 'tabla  que almacena aquellos parámetros de configuración que son globales a todos los servicios y entornos para un mismo cliente integrador de las librerias scsp'; 

CREATE TABLE CORE_PETICION_RESPUESTA 
  ( 
     IDPETICION          VARCHAR(26) NOT NULL, 
     ESTADO              VARCHAR(4) NOT NULL, 
     FECHAPETICION       timestamp (6) NOT NULL, 
     FECHARESPUESTA      timestamp (6), 
     TER                 timestamp (6), 
     ERROR               text, 
     NUMEROENVIOS        bigint NOT NULL, 
     NUMEROTRANSMISIONES bigint NOT NULL, 
     FECHAULTIMOSONDEO   timestamp (6), 
     TRANSMISIONSINCRONA bigint, 
     DESCOMPUESTA        VARCHAR(1), 
     CERTIFICADO         DECIMAL(19, 0)
  ); 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.IDPETICION IS 'identificador unívoco de la petición de servicio'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.ESTADO IS 'codigo identificativo del estado de la petición. tomará sus valores de las tablas scsp_estado_peticion y scsp_codigo_error'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.FECHAPETICION IS 'timestamp que indica la fecha en la que se generó la petición'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.FECHARESPUESTA IS 'timestamp que indica la fecha en la cual se recibió la respuesta a nuestra petición'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.TER IS 'timestamp que indica la fecha a partir de la cual una petición de tipo asíncrono podrá solicitar una respuesta definitiva al servicio'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.ERROR IS 'mensaje descriptivo del error, si lo hubiera, en la solicitud del servicio'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.NUMEROENVIOS IS 'valor que indica el número de veces que se ha reenviado una petición al servicio'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.NUMEROTRANSMISIONES IS 'número de solicitudes de transmisión que se enviaron en la petición'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.FECHAULTIMOSONDEO IS 'timestamp que indica la fecha del último reenvio de una petición de tipo asincrono'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.TRANSMISIONSINCRONA IS 'valor binario que indica si la petición fue solicitada a un servicio de tipo síncrono o asincrono. podrá tomar los valores: 0: la comunicación fue de tipo asíncrono, 1: la comunicación fue de tipo síncrono'; 

COMMENT ON COLUMN CORE_PETICION_RESPUESTA.DESCOMPUESTA IS 'caracter que indica el estado del procesamiento de las transmisiones de la respuesta recibida. podrá tomar los siguientes valores:    - s: se ha procesado correctamente la respuesta, habiendo obtenido todas las transmisiones en ella incluidas y registradas en la tabla core_transmision. - n: no ha sido procesadas las transmisiones de la respuesta. - e: la respuesta terminó correctamente  (estado 0003), pero se ha producido un error al procesar sus trasmisiones'; 

COMMENT ON TABLE CORE_PETICION_RESPUESTA IS 'esta tabla registrará un histórico de las peticiones y respuestas intercambiadas entre los requirentes y los emisores de servicios'; 

CREATE TABLE CORE_SECUENCIA_IDPETICION 
  ( 
     PREFIJO         VARCHAR(8) NOT NULL, 
     SECUENCIA       VARCHAR(23) NOT NULL, 
     FECHAGENERACION timestamp (6) NOT NULL
  ); 

COMMENT ON COLUMN CORE_SECUENCIA_IDPETICION.PREFIJO IS 'prefijo utilizado para la construccion de los identificadores. dicho valor podrá ser el prefijo especificado a ser utilizado para cada servicio o ante la no existencia del mismo, el número de serie del certificado digital firmante  de los mensajes'; 

COMMENT ON COLUMN CORE_SECUENCIA_IDPETICION.SECUENCIA IS 'valor secuencial que será concatenado al prefijo para generar los identificadores de petición. este secuencial será de tipo alfanumérico, de tal forma que el siguiente valor a 00000009 sería 0000000a'; 

COMMENT ON COLUMN CORE_SECUENCIA_IDPETICION.FECHAGENERACION IS 'fecha en la que se registró  el secuencial'; 

COMMENT ON TABLE CORE_SECUENCIA_IDPETICION IS 'tabla que almacena las semillas (secuenciales) utilizadas para la generación de los identificadores de peticion. existirá un secuencial asociado a cada posible prefijo o número de serie de certificado digital firmante'; 

CREATE TABLE CORE_TIPO_MENSAJE 
  ( 
     TIPO        INT NOT NULL, 
     DESCRIPCION VARCHAR(32) NOT NULL
  ); 

COMMENT ON COLUMN CORE_TIPO_MENSAJE.TIPO IS 'tipo identificativo del mensaje'; 

COMMENT ON COLUMN CORE_TIPO_MENSAJE.DESCRIPCION IS 'literal descriptivo del tipo de mensaje'; 

COMMENT ON TABLE CORE_TIPO_MENSAJE IS 'tabla maestra que almacena los diferentes tipos de mensajes que pueden ser intercambiados a lo largo de un ciclo de comunicación scsp. estos valores serán: • peticion •	respuesta •	solicitudrespuesta •	confirmaciónpeticion •	fault'; 

CREATE TABLE CORE_TOKEN_DATA 
  ( 
     IDPETICION            VARCHAR(26) NOT NULL, 
     TIPOMENSAJE           INT NOT NULL, 
     DATOS                 text NOT NULL, 
     CLAVE                 VARCHAR(256), 
     MODOENCRIPTACION      VARCHAR(32), 
     ALGORITMOENCRIPTACION VARCHAR(32)
  ); 

COMMENT ON COLUMN CORE_TOKEN_DATA.IDPETICION IS 'identificador de la petición a la cual está asociado el xml'; 

COMMENT ON COLUMN CORE_TOKEN_DATA.TIPOMENSAJE IS 'tipo de mensaje almacenado, podrá ser : -peticion -respuesta -solicitudrespuesta -confirmaciónpeticion -fault';

COMMENT ON COLUMN CORE_TOKEN_DATA.DATOS IS 'bytes del mensaje almacenado'; 

COMMENT ON COLUMN CORE_TOKEN_DATA.CLAVE IS 'clave simétrica utilizada para el cifrado de los datos'; 

COMMENT ON COLUMN CORE_TOKEN_DATA.MODOENCRIPTACION IS 'modo de encriptación utilizado para el proceso de cifrado.  por defecto será transportkey';

COMMENT ON COLUMN CORE_TOKEN_DATA.ALGORITMOENCRIPTACION IS 'algoritmo empleado en la encriptación del mensaje. podrá tomar los siguientes valores: - aes128 - aes256 -desde';

COMMENT ON TABLE CORE_TOKEN_DATA IS 'esta tabla almacenará el contenido de los mensajes intercambiados en un proceso de comunicación scsp'; 

CREATE TABLE CORE_TRANSMISION 
  ( 
     IDSOLICITUD           VARCHAR(64) NOT NULL, 
     IDPETICION            VARCHAR(26) NOT NULL, 
     IDTRANSMISION         VARCHAR(64), 
     IDSOLICITANTE         VARCHAR(10) NOT NULL, 
     NOMBRESOLICITANTE     VARCHAR(256), 
     DOCTITULAR            VARCHAR(16), 
     NOMBRETITULAR         VARCHAR(40), 
     APELLIDO1TITULAR      VARCHAR(40), 
     APELLIDO2TITULAR      VARCHAR(40), 
     NOMBRECOMPLETOTITULAR VARCHAR(122), 
     DOCFUNCIONARIO        VARCHAR(16), 
     NOMBREFUNCIONARIO     VARCHAR(128), 
     FECHAGENERACION       timestamp (6), 
     UNIDADTRAMITADORA     VARCHAR(256), 
     CODIGOPROCEDIMIENTO   VARCHAR(256), 
     NOMBREPROCEDIMIENTO   VARCHAR(256), 
     EXPEDIENTE            VARCHAR(256), 
     FINALIDAD             VARCHAR(256), 
     CONSENTIMIENTO        VARCHAR(3), 
     ESTADO                VARCHAR(4), 
     ERROR                 text, 
     XMLTRANSMISION        text
  ); 

COMMENT ON COLUMN CORE_TRANSMISION.IDSOLICITUD IS 'identificador de la solicitud de transmisión dentro de las n posibles incluidas en una petición'; 

COMMENT ON COLUMN CORE_TRANSMISION.IDPETICION IS 'indentificador de la petición en la que se incluyó la solicitud de transmisión'; 

COMMENT ON COLUMN CORE_TRANSMISION.IDTRANSMISION IS 'indentificador de la transmisión que responde a la petición de servicio de la solicitud de transmisión identificada con idsolicitud'; 

COMMENT ON COLUMN CORE_TRANSMISION.IDSOLICITANTE IS 'cif de organismo solicitante del servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.NOMBRESOLICITANTE IS 'nombre del organismo solicitante de servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.DOCTITULAR IS 'documento identificativo del titular sobre el cual se está realizando la petición de servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.NOMBRETITULAR IS 'nombre del titular sobre el que se realiza la petición del servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.APELLIDO1TITULAR IS 'primer apellido del titular sobre el que se realiza la petición del servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.APELLIDO2TITULAR IS 'segundo apellido del titular sobre el que se realiza la petición del servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.NOMBRECOMPLETOTITULAR IS 'nombre completo del titular sobre el que se realiza la petición del servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.DOCFUNCIONARIO IS 'documento identificativo del funcionario que generó la soliicitud de transmisión'; 

COMMENT ON COLUMN CORE_TRANSMISION.NOMBREFUNCIONARIO IS 'nombre del funcionario que generó la solicitud de transmisión'; 

COMMENT ON COLUMN CORE_TRANSMISION.FECHAGENERACION IS 'fecha en la que se generó la transmisión'; 

COMMENT ON COLUMN CORE_TRANSMISION.UNIDADTRAMITADORA IS 'unidad tramitadora asociada a la solicitud de transmisión'; 

COMMENT ON COLUMN CORE_TRANSMISION.CODIGOPROCEDIMIENTO IS 'código del procedimiento en base al cual se puede solicitar el servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.NOMBREPROCEDIMIENTO IS 'nombre del procedimiento en base al cual se puede solicitar el servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.EXPEDIENTE IS 'expediente asociado a la solicitud de transmsión'; 

COMMENT ON COLUMN CORE_TRANSMISION.FINALIDAD IS 'finalidad por la cual se emitió la solicitud de transmisión'; 

COMMENT ON COLUMN CORE_TRANSMISION.CONSENTIMIENTO IS 'tipo de consentimiento asociado a la transmisión. deberá tomar uno de los dos posibles valores:  - si -ley '; 

COMMENT ON COLUMN CORE_TRANSMISION.ESTADO IS 'estado concreto en el que se encuentra la transmisión'; 

COMMENT ON COLUMN CORE_TRANSMISION.ERROR IS 'descripción del posible error encontrado al solicitar la transmisión de servicio'; 

COMMENT ON COLUMN CORE_TRANSMISION.XMLTRANSMISION IS 'el xml de la transmisión. su almacenamiento será opcional, dependiendo de un parametro global almacenado en la tabla core_parametro_configuracion'; 

COMMENT ON TABLE CORE_TRANSMISION IS 'esta tabla almacenará la información específica de cada transmisión que haya sido incluida en una respuesta de un servicio scsp'; 

CREATE TABLE SCSP_CODIGO_ERROR 
  ( 
     CODIGO      VARCHAR(4) NOT NULL, 
     DESCRIPCION VARCHAR(1024) NOT NULL
  ); 

CREATE TABLE SCSP_SECUENCIA_IDTRANSMISION 
  ( 
     PREFIJO         VARCHAR(8) NOT NULL, 
     SECUENCIA       VARCHAR(26) NOT NULL, 
     FECHAGENERACION timestamp (6) NOT NULL
  ); 

CREATE TABLE SCSP_CODIGO_ERROR_SECUNDARIO 
  ( 
     CODIGOSECUNDARIO VARCHAR(4) NOT NULL, 
     CODIGO           VARCHAR(4) NOT NULL, 
     DESCRIPCION      text NOT NULL
  ); 

CREATE TABLE SCSP_ESTADO_PETICION 
  ( 
     CODIGO  VARCHAR(4) NOT NULL, 
     MENSAJE VARCHAR(256) NOT NULL
  ); 

CREATE TABLE EMS_ACL_CLASS 
  ( 
     ID    DECIMAL(19, 0) NOT NULL, 
     CLASS VARCHAR(100) NOT NULL
  ); 

CREATE TABLE EMS_ACL_SID 
  ( 
     ID        DECIMAL(19, 0) NOT NULL, 
     PRINCIPAL BOOLEAN NOT NULL, 
     SID       VARCHAR(100) NOT NULL
  ); 

CREATE TABLE EMS_ACL_OBJECT_IDENTITY 
  ( 
     ID                 DECIMAL(19, 0) NOT NULL,
     OBJECT_ID_CLASS    DECIMAL(19, 0) NOT NULL, 
     OBJECT_ID_IDENTITY DECIMAL(19, 0) NOT NULL, 
     PARENT_OBJECT      DECIMAL(19, 0), 
     OWNER_SID          DECIMAL(19, 0) NOT NULL, 
     ENTRIES_INHERITING BOOLEAN NOT NULL
  ); 

CREATE TABLE EMS_ACL_ENTRY 
  ( 
     ID                  DECIMAL(19, 0) NOT NULL,
     ACL_OBJECT_IDENTITY DECIMAL(19, 0) NOT NULL, 
     ACE_ORDER           DECIMAL(19, 0) NOT NULL, 
     SID                 DECIMAL(19, 0) NOT NULL, 
     MASK                DECIMAL(19, 0) NOT NULL, 
     GRANTING            BOOLEAN NOT NULL, 
     AUDIT_SUCCESS       BOOLEAN NOT NULL, 
     AUDIT_FAILURE       BOOLEAN NOT NULL
  ); 

CREATE TABLE EMS_REDIR_PETICIO 
  ( 
     ID                DECIMAL(19, 0) NOT NULL, 
     DATA_COMPROVACIO  timestamp (6), 
     DATA_PETICIO      timestamp (6), 
     DATA_RESPOSTA     timestamp (6), 
     ERROR             text, 
     ESTAT             VARCHAR(64) NOT NULL, 
     NUM_ENVIAMENTS    bigint NOT NULL, 
     NUM_TRANSMISSIONS bigint NOT NULL, 
     PETICIO_ID        VARCHAR(512) NOT NULL, 
     SERVEI_CODI       VARCHAR(1024) NOT NULL, 
     SINCRONA          INT, 
     TER               timestamp (6), 
     VERSION           DECIMAL(19, 0) NOT NULL, 
     EMISSOR_CODI      VARCHAR(10)
  ); 

CREATE TABLE EMS_REDIR_MISSATGE 
  ( 
     ID         DECIMAL(19, 0) NOT NULL, 
     TIPUS      bigint NOT NULL, 
     VERSION    DECIMAL(19, 0) NOT NULL, 
     XML        text, 
     PETICIO_ID DECIMAL(19, 0) NOT NULL
  ); 

CREATE TABLE EMS_REDIR_SOLICITUD 
  ( 
     ID                 DECIMAL(19, 0) NOT NULL, 
     CONSENTIMENT       VARCHAR(12), 
     DATA_GENERACIO     timestamp (6), 
     ERROR              text, 
     ESTAT              VARCHAR(16), 
     FINALITAT          VARCHAR(1024), 
     FUNCIONARI_DOC     VARCHAR(64), 
     FUNCIONARI_NOM     VARCHAR(640), 
     PROCEDIMENT_CODI   VARCHAR(1024), 
     PROCEDIMENT_NOM    VARCHAR(1024), 
     SOLICITANT_ID      VARCHAR(160) NOT NULL, 
     SOLICITANT_NOM     VARCHAR(1024), 
     SOLICITUD_ID       VARCHAR(1024) NOT NULL, 
     TITULAR_DOC        VARCHAR(64), 
     TITULAR_LLINATGE1  VARCHAR(640), 
     TITULAR_LLINATGE2  VARCHAR(640), 
     TITULAR_NOM        VARCHAR(640), 
     TITULAR_NOM_SENCER VARCHAR(1024), 
     TRANSMISION_ID     VARCHAR(1024), 
     UNITAT_TRAMITADORA VARCHAR(1024), 
     VERSION            DECIMAL(19, 0) NOT NULL, 
     PETICIO_ID         DECIMAL(19, 0) NOT NULL 
  ); 

CREATE TABLE EMS_USUARI 
  ( 
     CODI         VARCHAR(64) NOT NULL, 
     INICIALITZAT BOOLEAN, 
     NIF          VARCHAR(9) NOT NULL, 
     NOM          VARCHAR(200), 
     EMAIL        VARCHAR(200), 
     VERSION      DECIMAL(19, 0) NOT NULL
  ); 

CREATE TABLE EMS_SERVEI 
  ( 
     ID                      DECIMAL(19, 0) NOT NULL, 
     CREATEDDATE             timestamp (6), 
     LASTMODIFIEDDATE        timestamp (6), 
     ACTIU                   BOOLEAN, 
     CODI                    VARCHAR(256) NOT NULL, 
     CONTRASENYA             VARCHAR(1024), 
     DESCRIPCIO              VARCHAR(4000), 
     NOM                     VARCHAR(1024) NOT NULL, 
     USUARI                  VARCHAR(256), 
     VERSION                 DECIMAL(19, 0) NOT NULL, 
     CREATEDBY_CODI          VARCHAR(256), 
     LASTMODIFIEDBY_CODI     VARCHAR(256), 
     CONFIGURAT              BOOLEAN, 
     BACKOFFICE_CLASS        VARCHAR(256), 
     BACKCAIB_AUTENTICACIO   bigint, 
     BACKCAIB_URL            VARCHAR(256), 
     TIPUS                   INT NOT NULL, 
     RESOLVER_CLASS          VARCHAR(1024), 
     URL_PER_DEFECTE         VARCHAR(256), 
     RESPONSE_RESOLVER_CLASS VARCHAR(1024), 
     BACKCAIB_ASYNC_TIPUS    bigint, 
     BACKCAIB_ASYNC_TER      bigint, 
     XSD_ACTIVA              BOOLEAN, 
     XSD_ESQUEMA_BAK         VARCHAR(256)
  ); 

CREATE TABLE EMS_SERVEI_RUTA_DESTI 
  ( 
     ID                  DECIMAL(19, 0) NOT NULL, 
     ENTITAT_CODI        VARCHAR(256) NOT NULL, 
     URL                 VARCHAR(1024) NOT NULL, 
     SERVEI_ID           DECIMAL(19, 0) NOT NULL, 
     VERSION             DECIMAL(19, 0) NOT NULL, 
     CREATEDDATE         timestamp (6), 
     LASTMODIFIEDDATE    timestamp (6), 
     CREATEDBY_CODI      VARCHAR(256), 
     LASTMODIFIEDBY_CODI VARCHAR(256), 
     ORDRE               DECIMAL(19, 0)
  ); 

CREATE TABLE SCHEMA_VERSION 
  ( 
     INSTALLED_RANK DECIMAL(38, 0) NOT NULL, 
     VERSION        VARCHAR(50), 
     DESCRIPTION    VARCHAR(200) NOT NULL, 
     TYPE           VARCHAR(20) NOT NULL, 
     SCRIPT         VARCHAR(1000) NOT NULL, 
     CHECKSUM       DECIMAL(38, 0), 
     INSTALLED_BY   VARCHAR(100) NOT NULL, 
     INSTALLED_ON   timestamp (6) DEFAULT CURRENT_TIMESTAMP NOT NULL, 
     EXECUTION_TIME DECIMAL(38, 0) NOT NULL, 
     SUCCESS        INT NOT NULL
  ); 

CREATE TABLE EMS_BACKOFFICE_COM 
  ( 
     ID            DECIMAL(19) NOT NULL, 
     PETICIO_ID    DECIMAL(19) NOT NULL, 
     SOLICITUD_ID  DECIMAL(19), 
     PETICIO_DATA  timestamp (6) NOT NULL, 
     PETICIO_XML   text NOT NULL, 
     RESPOSTA_DATA timestamp (6), 
     RESPOSTA_XML  text, 
     VERSION       DECIMAL(19), 
     ERROR         text
  ); 

CREATE TABLE EMS_BACKOFFICE_PET 
  ( 
     ID                DECIMAL(19) NOT NULL, 
     PETICIO_ID        VARCHAR(26) NOT NULL, 
     ESTAT             bigint NOT NULL, 
     TER_DATA          timestamp (6) NOT NULL, 
     DARRERA_SOL_DATA  timestamp (6), 
     DARRERA_SOL_ID    VARCHAR(256), 
     PROCESSADES_ERROR bigint, 
     PROCESSADES_TOTAL bigint, 
     COMUNICACIO_ID    DECIMAL(19), 
     VERSION           DECIMAL(19)
  ); 

CREATE TABLE EMS_BACKOFFICE_SOL 
  ( 
     ID             DECIMAL(19) NOT NULL, 
     PETICIO_ID     DECIMAL(19) NOT NULL, 
     SOLICITUD_ID   VARCHAR(256) NOT NULL, 
     ESTAT          bigint NOT NULL, 
     COMUNICACIO_ID DECIMAL(19), 
     VERSION        DECIMAL(19)
  ); 

CREATE TABLE AUTORIZACION_CA 
  ( 
     CODCA  VARCHAR(512), 
     NOMBRE VARCHAR(256) 
  );

COMMENT ON COLUMN SCSP_CODIGO_ERROR.CODIGO IS 'codigo identificativo del error'; 

COMMENT ON COLUMN SCSP_CODIGO_ERROR.DESCRIPCION IS 'litreral descriptivo del error'; 

COMMENT ON TABLE SCSP_CODIGO_ERROR IS 'tabla que registraro los posibles errores genoricos que toda comunicacion scsp puede generar'; 

COMMENT ON COLUMN SCSP_ESTADO_PETICION.CODIGO IS 'codigo identificativo del estado'; 

COMMENT ON COLUMN SCSP_ESTADO_PETICION.MENSAJE IS 'literal descriptivo del estado'; 

COMMENT ON TABLE SCSP_ESTADO_PETICION IS 'tabla que almacena los posibles estados en los que se puede encontrar una peticion scsp. sus posibles valores seron: o 0001 - pendiente o	0002 - en proceso o	0003 - tramitada'; 

COMMENT ON COLUMN SCSP_SECUENCIA_IDTRANSMISION.PREFIJO IS 'prefijo de idtransmisiones al que estaro asociada la secuencia'; 

COMMENT ON COLUMN SCSP_SECUENCIA_IDTRANSMISION.SECUENCIA IS 'valor actual de la secuencia alfanumorica asociada al prefijo'; 

COMMENT ON COLUMN SCSP_SECUENCIA_IDTRANSMISION.FECHAGENERACION IS 'oltima fecha en la que se ha generado un valor de secuencia'; 

COMMENT ON TABLE SCSP_SECUENCIA_IDTRANSMISION IS 'tabla utilizada para la generacion de los valores de los nodos idtransmision en los emisores'; 

COMMENT ON COLUMN SCSP_CODIGO_ERROR_SECUNDARIO.CODIGOSECUNDARIO IS 'codigo de error scsp al que esta asociado el error secundario'; 

COMMENT ON COLUMN SCSP_CODIGO_ERROR_SECUNDARIO.CODIGO IS 'codigo identificativo del error especifico secundario'; 

COMMENT ON COLUMN SCSP_CODIGO_ERROR_SECUNDARIO.DESCRIPCION IS 'literal descriptivo del mensaje de error secundario'; 

COMMENT ON TABLE SCSP_CODIGO_ERROR_SECUNDARIO IS 'tabla de errores que permitiro almacenar aquellos mensajes de excepcion gestionados por cada logica de negocio especofica de cada emisor, y que estro asociado a un error genorico de scsp'; 