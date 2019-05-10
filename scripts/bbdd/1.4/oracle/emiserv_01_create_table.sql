CREATE TABLE CORE_BLOQUEO 
   (	
    IDBLOQUEO VARCHAR2(64 CHAR) NOT NULL, 
	FECHAEXPIRACION TIMESTAMP (6) NOT NULL
   );
 

   COMMENT ON COLUMN CORE_BLOQUEO.IDBLOQUEO IS 'Identificador del recurso a ser bloqueado';
 
   COMMENT ON COLUMN CORE_BLOQUEO.FECHAEXPIRACION IS 'Fecha a partir de la cual un bloqueo deja de tener vigencia. Utilizado para evitar que un proceso que haya acabado con un fallo inesperado deje un recurso inutilizado';
 
   COMMENT ON TABLE CORE_BLOQUEO IS 'Tabla gestionada por las librerias para el establecimiento de bloqueos sobre un recurso que puede ser compartido de manera simultánea por varios procesos, pero cuyo acceso debe ser secuencial. Su principal uso será el control del acceso al secuencial que permite generar identificadores de petición de la tabla core_secuencia_idpeticion';

CREATE TABLE CORE_CACHE_CERTIFICADOS (	
    NUMSERIE VARCHAR2(256 CHAR) NOT NULL, 
	AUTORIDADCERTIF VARCHAR2(512 CHAR) NOT NULL, 
	TIEMPOCOMPROBACION TIMESTAMP (6) NOT NULL, 
	REVOCADO NUMBER(10,0) NOT NULL
   );
 

   COMMENT ON COLUMN CORE_CACHE_CERTIFICADOS.NUMSERIE IS 'Número de serie del certificado';
 
   COMMENT ON COLUMN CORE_CACHE_CERTIFICADOS.AUTORIDADCERTIF IS 'Autoridad certificadora que emitió el certificado';
 
   COMMENT ON COLUMN CORE_CACHE_CERTIFICADOS.TIEMPOCOMPROBACION IS 'Fecha en la que se realizó el proceso de validación del certificado por última vez';
 
   COMMENT ON COLUMN CORE_CACHE_CERTIFICADOS.REVOCADO IS 'Valor booleano (‘1’ o ‘0’) que indicará: -	‘1’:   si el proceso de validación revocó el certificado. -	‘0’: si el proceso de validación aceptó el certificado';
 
   COMMENT ON TABLE CORE_CACHE_CERTIFICADOS  IS 'Tabla que registra el resultado de la validación de los certificados   empleados en la firma de los mensajes recibidos por el requirente o por el emisor. Así mismo se registra la fecha en la que se realizó dicha validación para poder calcular el periodo de tiempo en la que dicha validación esta vigente';

  
CREATE TABLE CORE_ORGANISMO_CESIONARIO 
   (	
    ID NUMBER(19,0), 
	NOMBRE VARCHAR2(50 CHAR), 
	CIF VARCHAR2(50 CHAR), 
	FECHAALTA TIMESTAMP (6), 
	FECHABAJA TIMESTAMP (6), 
	BLOQUEADO NUMBER DEFAULT 0 NOT NULL, 
	LOGO BLOB
   );
  
CREATE TABLE CORE_CLAVE_PRIVADA 
   (	
    ALIAS VARCHAR2(256 CHAR) NOT NULL, 
	NOMBRE VARCHAR2(256 CHAR) NOT NULL, 
	PASSWORD VARCHAR2(256 CHAR) NOT NULL, 
	NUMEROSERIE VARCHAR2(256 CHAR) NOT NULL, 
	ID NUMBER(19,0) NOT NULL, 
	FECHAALTA TIMESTAMP (6) NOT NULL, 
	FECHABAJA TIMESTAMP (6), 
	ORGANISMO NUMBER(19,0), 
	INTEROPERABILIDAD NUMBER(1,0)
   );
 

   COMMENT ON COLUMN CORE_CLAVE_PRIVADA.ALIAS IS 'Alias que identifica de manera unívoca a la clave privada dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
 
   COMMENT ON COLUMN CORE_CLAVE_PRIVADA.NOMBRE IS 'Nombre descriptivo de la clave privada';
 
   COMMENT ON COLUMN CORE_CLAVE_PRIVADA.PASSWORD IS 'Password de la clave privada necesaria para hacer uso de la misma';
 
   COMMENT ON COLUMN CORE_CLAVE_PRIVADA.NUMEROSERIE IS 'Numero de serie de la clave privada';
 
   COMMENT ON TABLE CORE_CLAVE_PRIVADA IS 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves privadas disponibles en el almacén de certificados configurado. Las claves privadas aquí configuradas serán utilizadas para la firma de los mensajes emitidos';

CREATE TABLE CORE_CLAVE_PUBLICA 
   (	
    ALIAS VARCHAR2(256 CHAR) NOT NULL, 
	NOMBRE VARCHAR2(256 CHAR) NOT NULL, 
	NUMEROSERIE VARCHAR2(256 CHAR) NOT NULL, 
	ID NUMBER NOT NULL, 
	FECHAALTA TIMESTAMP (6) NOT NULL, 
	FECHABAJA TIMESTAMP (6)
   );
 

   COMMENT ON COLUMN CORE_CLAVE_PUBLICA.ALIAS IS 'Alias que identifica de manera unívoca a la clave pública dentro del almacén de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
 
   COMMENT ON COLUMN CORE_CLAVE_PUBLICA.NOMBRE IS 'Nombre descriptivo de la clave pública';
 
   COMMENT ON COLUMN CORE_CLAVE_PUBLICA.NUMEROSERIE IS 'Numero de serie de la clave publica';
 
   COMMENT ON TABLE CORE_CLAVE_PUBLICA IS 'Esta tabla almacenará los datos de configuración necesarios para acceder a las claves públicas disponibles en el almacén de certificados configurado. Las claves públicas  aquí configuradas serán utilizadas para el posible cifrado de los  mensajes emitidos';

CREATE TABLE CORE_CODIGO_ERROR
   (	
    CODIGO VARCHAR2(4 CHAR) NOT NULL, 
	DESCRIPCION VARCHAR2(1024 CHAR) NOT NULL
   );
 

   COMMENT ON COLUMN CORE_CODIGO_ERROR.CODIGO IS 'Código identificativo del error';
 
   COMMENT ON COLUMN CORE_CODIGO_ERROR.DESCRIPCION IS 'Litreral descriptivo del error';
 
   COMMENT ON TABLE CORE_CODIGO_ERROR IS 'Tabla que registrará los posibles errores genéricos que toda comunicación SCSP puede generar';

CREATE TABLE CORE_EMISOR_CERTIFICADO
   (	
    CIF VARCHAR2(16 CHAR) NOT NULL, 
	NOMBRE VARCHAR2(50 CHAR) NOT NULL, 
	FECHABAJA TIMESTAMP (6), 
	ID NUMBER(19,0)
   );
 

   COMMENT ON COLUMN CORE_EMISOR_CERTIFICADO.CIF IS 'Nombre descriptivo del organismo emisor de servicios';
 
   COMMENT ON COLUMN CORE_EMISOR_CERTIFICADO.NOMBRE IS 'CIF identificativo del organismo emisor de servicios';
 
   COMMENT ON COLUMN CORE_EMISOR_CERTIFICADO.FECHABAJA IS 'Fecha de Baja de emisores de certificados';
 
   COMMENT ON TABLE CORE_EMISOR_CERTIFICADO  IS 'Esta tabla almacenará los diferentes emisores de servicio configurados';
  
  CREATE TABLE CORE_EM_AUTORIZACION_CA 
   (
	ID NUMBER(19,0),
    CODCA VARCHAR2(512 CHAR) NOT NULL, 
	NOMBRE VARCHAR2(256 CHAR)
   );
 
   COMMENT ON COLUMN CORE_EM_AUTORIZACION_CA.CODCA IS 'Codigo de la autoridad de certificación (Ej: FNMT)';
 
   COMMENT ON COLUMN CORE_EM_AUTORIZACION_CA.NOMBRE IS 'Nombre de la autoridad de certificación  (Ej: Fabrica Nacional de Moneda y Timbre)';
 
   COMMENT ON TABLE CORE_EM_AUTORIZACION_CA IS 'Contiene la información relativa a las distintas autoridades de certificación reconocidas por la aplicación';
   
  
  CREATE TABLE CORE_EM_APLICACION
   (	
   	IDAPLICACION NUMBER(10,0) NOT NULL, 
	NIFCERTIFICADO VARCHAR2(16 CHAR), 
	NUMEROSERIE VARCHAR2(64 CHAR) NOT NULL, 
	CN VARCHAR2(512 CHAR) NOT NULL, 
	TIEMPOCOMPROBACION TIMESTAMP (6), 
	FECHAALTA TIMESTAMP (6), 
	FECHABAJA TIMESTAMP (6), 
	AUTORIDADCERTIF VARCHAR2(512), 
	DESCRIPCION VARCHAR2(512)
	);


   COMMENT ON COLUMN CORE_EM_APLICACION.IDAPLICACION IS 'Identificador de la aplicaci�n. Se va a generar autom�ticamente';
 
   COMMENT ON COLUMN CORE_EM_APLICACION.NIFCERTIFICADO IS 'Nif del certificado con el que la aplicaci�n firmara las peticiones';
 
   COMMENT ON COLUMN CORE_EM_APLICACION.NUMEROSERIE IS 'Numero de serie del certificado con el que la aplicaci�n firmara las peticiones';
 
   COMMENT ON COLUMN CORE_EM_APLICACION.CN IS 'Common Name del certificado con el que la aplicaci�n firmara las peticiones';
 
   COMMENT ON COLUMN CORE_EM_APLICACION.TIEMPOCOMPROBACION IS 'Fecha en que se realizo la operaci�n de validar el certificado';
 
   COMMENT ON COLUMN CORE_EM_APLICACION.FECHAALTA IS 'Fecha en la que se da de alta la aplicaci�n';
 
   COMMENT ON COLUMN CORE_EM_APLICACION.FECHABAJA IS 'Fecha a partir de la cual la aplicaci�n no va a poder acceder a ning�n servicio';
 
   COMMENT ON TABLE CORE_EM_APLICACION  IS 'Aplicaciones dadas de alta en el emisor que van a emplear los organismos para poder realizar consultas a los servicios ofrecidos por este';

  
  
  
  
  CREATE TABLE CORE_SERVICIO
   (	
    CODCERTIFICADO VARCHAR2(64 CHAR) NOT NULL , 
	URLSINCRONA VARCHAR2(256 CHAR), 
	URLASINCRONA VARCHAR2(256 CHAR), 
	ACTIONSINCRONA VARCHAR2(256 CHAR), 
	ACTIONASINCRONA VARCHAR2(256 CHAR), 
	ACTIONSOLICITUD VARCHAR2(256 CHAR), 
	VERSIONESQUEMA VARCHAR2(32 CHAR) NOT NULL , 
	TIPOSEGURIDAD VARCHAR2(16 CHAR) NOT NULL , 
	PREFIJOPETICION VARCHAR2(9 CHAR), 
	XPATHCIFRADOSINCRONO VARCHAR2(256 CHAR), 
	XPATHCIFRADOASINCRONO VARCHAR2(256 CHAR), 
	ESQUEMAS VARCHAR2(256 CHAR), 
	ALGORITMOCIFRADO VARCHAR2(32 CHAR), 
	NUMEROMAXIMOREENVIOS NUMBER(10,0) NOT NULL , 
	MAXSOLICITUDESPETICION NUMBER(10,0) NOT NULL , 
	PREFIJOIDTRANSMISION VARCHAR2(9 CHAR), 
	DESCRIPCION VARCHAR2(512 CHAR) NOT NULL , 
	FECHAALTA TIMESTAMP (6) NOT NULL , 
	FECHABAJA TIMESTAMP (6), 
	CADUCIDAD NUMBER(10,0), 
	XPATHLITERALERROR VARCHAR2(256 CHAR), 
	XPATHCODIGOERROR VARCHAR2(256 CHAR), 
	TIMEOUT NUMBER(10,0) DEFAULT 60 NOT NULL , 
	VALIDACIONFIRMA VARCHAR2(32 CHAR) DEFAULT 'estricto', 
	EMISOR NUMBER(19,0), 
	PLANTILLAXSLT VARCHAR2(512 CHAR), 
	CLAVECIFRADO NUMBER(19,0), 
	CLAVEFIRMA NUMBER(19,0), 
	ID NUMBER(19,0)
   );
 

   COMMENT ON COLUMN CORE_SERVICIO.CODCERTIFICADO IS 'Código del certificado que lo identifica unívocamente en las comunicaciones SCSP';
 
   COMMENT ON COLUMN CORE_SERVICIO.URLSINCRONA IS 'Endpoint de acceso al servicio de tipo síncrono';
 
   COMMENT ON COLUMN CORE_SERVICIO.URLASINCRONA IS 'Endpoint de acceso al servicio de tipo asíncrono del certificado';
 
   COMMENT ON COLUMN CORE_SERVICIO.ACTIONSINCRONA IS 'Valor del Soap:Action de la petición síncrona utilizado por el servidor WS en el caso de que sea necesario';
 
   COMMENT ON COLUMN CORE_SERVICIO.ACTIONASINCRONA IS 'Valor del Soap:Action de la petición asíncrona utilizado por el servidor WS en el caso de que sea necesario';
 
   COMMENT ON COLUMN CORE_SERVICIO.ACTIONSOLICITUD IS 'Valor del Soap:Action de la solicitud asíncrona de respuesta utilizado por el servidor WS en el caso de que sea necesario';
 
   COMMENT ON COLUMN CORE_SERVICIO.VERSIONESQUEMA IS 'Indica la versión de esquema utilizado en los mensajes SCSP pudiendo tomar los valores V2 y V3 (existe la posibilidad de añadir otros valores utilizando configuraciones avandadas de binding)';
 
   COMMENT ON COLUMN CORE_SERVICIO.TIPOSEGURIDAD IS 'Indica la politica de seguridad utilizada en la securización de los mensajes, pudiendo tomar los valores [XMLSignature| WS-Security]';
 
   COMMENT ON COLUMN CORE_SERVICIO.PREFIJOPETICION IS 'Literal con una longitud máxima de 8 caracteres, el cual será utilizado para la construcción de los identificadores de petición, anteponiendose a un valor secuencial. Mediante este literal pueden personalizarse los identificadores de petición haciendolos más descriptivos';
 
   COMMENT ON COLUMN CORE_SERVICIO.XPATHCIFRADOSINCRONO IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresión xpath que permite localizar  al nodo en el mensaje XML, presentará el siguiente formato //*[local-name()=´NODO_A_CIFRAR´], donde ´NODO_A_CIFRAR´ es el local name del nodo que se desea cifrar. Este nodo será empleado en el caso de realizar comunicaciones síncronas';
 
   COMMENT ON COLUMN CORE_SERVICIO.XPATHCIFRADOASINCRONO IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresión xpath que permite localizar  al nodo en el mensaje XML, presentará el siguiente formato //*[local-name()=´NODO_A_CIFRAR´], donde ´NODO_A_CIFRAR´ es el local name del nodo que se desea cifrar. Este nodo será empleado en el caso de realizar comunicaciones asíncronas';
 
   COMMENT ON COLUMN CORE_SERVICIO.ESQUEMAS IS 'Ruta que indica el directorio donde se encuentran los esquemas (*.xsd) con el que se validará el XML de los diferentes mensajes intercambiados. Esta ruta podrá tomar un valor relativo haciendo referencia al classpath de la aplicación o  un path absoluto';
 
   COMMENT ON COLUMN CORE_SERVICIO.ALGORITMOCIFRADO IS 'Literal que identifica el algoritmo utilizado para el cifrado de los mensajes enviados al emisor. Debe poseer un valor reconocido por las librerías de Rampart: - Basic128Rsa15 - TripleDesRsa15  - Basic256Rsa15';
 
   COMMENT ON COLUMN CORE_SERVICIO.NUMEROMAXIMOREENVIOS IS 'Valor que indica en el caso del requirente, el número máximo de reenvios que pueden llevarse a cabo sobre una petición asíncrona. En el caso del emisor, hace referencia al número máximo de veces que procesará y creará una respuesta para una petición con un mismo identificador';
 
   COMMENT ON COLUMN CORE_SERVICIO.MAXSOLICITUDESPETICION IS 'Número máximo de solicitudes de transmisión que se van a permitir por petición';
 
   COMMENT ON COLUMN CORE_SERVICIO.PREFIJOIDTRANSMISION IS 'Semilla empleada por el emisor para generar los identificadores de transmisión de las respuestas. Será un valor alfanumérico con un mínimo de 3 caracteres y un máximo de 8';
 
   COMMENT ON COLUMN CORE_SERVICIO.DESCRIPCION IS 'Literal descriptivo del servicio a solicitar utilizando el código de certificado';
 
   COMMENT ON COLUMN CORE_SERVICIO.FECHAALTA IS 'Timestamp con la fecha en la cual el certificado se dió de alta en el sistema y por lo tanto a partir de la cual se podrán emitir peticiones al mismo';
 
   COMMENT ON COLUMN CORE_SERVICIO.FECHABAJA IS 'Timestamp con la fecha en la cual el certificado se dio de baja en el sistema y por tanto a partir de la cual no se podrán emitir peticiones al mismo';
 
   COMMENT ON COLUMN CORE_SERVICIO.CADUCIDAD IS 'Número de dias que deberán sumarse a la fecharespuesta de una petición, para calcular la fecha a partir de la cual se podrá considerar que la respuesta esta caducada y se devolvera el error scsp correspondiente para indicar que la respuesta ha perdido su valor';
 
   COMMENT ON COLUMN CORE_SERVICIO.XPATHLITERALERROR IS 'Xpath para recuperar el literal del error de los datos específicos';
 
   COMMENT ON COLUMN CORE_SERVICIO.XPATHCODIGOERROR IS 'Xpath para recuperar el codigo de error de los datos específicos';
 
   COMMENT ON COLUMN CORE_SERVICIO.TIMEOUT IS 'Timeout que se establecerá para el envío de las peticiones a los servicios';
 
   COMMENT ON COLUMN CORE_SERVICIO.VALIDACIONFIRMA IS 'Parámetro que indica si se admite otro tipo de firma en el servicio además del configurado';
 
   COMMENT ON TABLE CORE_SERVICIO  IS 'Esta tabla registrará cada uno de los certificados SCSP que van a ser utilizados por el sistema tanto de la parte requirente como de la parte emisora';

  CREATE TABLE CORE_EM_AUTORIZACION_ORGANISMO 
   (	
    IDORGANISMO VARCHAR2(16 CHAR) NOT NULL , 
	FECHAALTA TIMESTAMP (6) NOT NULL , 
	FECHABAJA TIMESTAMP (6), 
	NOMBREORGANISMO VARCHAR2(64 CHAR), 
	ID NUMBER(19,0)
   );
 

   COMMENT ON COLUMN CORE_EM_AUTORIZACION_ORGANISMO.IDORGANISMO IS 'Identificador del organismo';
 
   COMMENT ON COLUMN CORE_EM_AUTORIZACION_ORGANISMO.FECHAALTA IS 'Fecha en la que se da de alta el organismo';
 
   COMMENT ON COLUMN CORE_EM_AUTORIZACION_ORGANISMO.FECHABAJA IS 'Fecha a partir de la cual el organismo no va a poder enviar peticiones';
 
   COMMENT ON COLUMN CORE_EM_AUTORIZACION_ORGANISMO.NOMBREORGANISMO IS 'Nombre descriptivo del organismo requirente de servicios';
 
   COMMENT ON TABLE CORE_EM_AUTORIZACION_ORGANISMO  IS 'Organismos que están dados de alta en el emisor y que van a poder consultar los servicios ofrecidos por este a través de alguna de las aplicaciones que tienen autorización para consultar los servicios';

CREATE TABLE CORE_EM_AUTORIZACION_CERT 
   (	
    IDAPLICACION NUMBER(10,0) NOT NULL , 
	FECHAALTA TIMESTAMP (6) NOT NULL , 
	FECHABAJA TIMESTAMP (6), 
	ID NUMBER(19,0), 
	IDCERTIFICADO NUMBER(19,0), 
	IDORGANISMO NUMBER(19,0)
   );
 

   COMMENT ON COLUMN CORE_EM_AUTORIZACION_CERT.IDAPLICACION IS 'Identificador de la aplicación autorizada';
 
   COMMENT ON COLUMN CORE_EM_AUTORIZACION_CERT.FECHAALTA IS 'Fecha de alta de la autorización';
 
   COMMENT ON COLUMN CORE_EM_AUTORIZACION_CERT.FECHABAJA IS 'Fecha de baja de la autorización';
 
   COMMENT ON TABLE CORE_EM_AUTORIZACION_CERT  IS 'Contiene la información relativa a las autorizaciones de los distintas aplicaciones para acceder a los servicios ofrecidos por el emisor';


CREATE TABLE CORE_EM_BACKOFFICE 
   (	
    BEANNAME VARCHAR2(256 CHAR), 
	CLASSNAME VARCHAR2(256 CHAR), 
	TER NUMBER(10,0) NOT NULL , 
	CERTIFICADO NUMBER(19,0)
   );
 

   COMMENT ON COLUMN CORE_EM_BACKOFFICE.BEANNAME IS 'Identificador del bean del contexto de Spring que contiene la clase que ofrece la puerta de entrada al backoffice';
 
   COMMENT ON COLUMN CORE_EM_BACKOFFICE.CLASSNAME IS 'Nombre completo  de la clase que ofrece la puerta de entrada al backoffice';
 
   COMMENT ON COLUMN CORE_EM_BACKOFFICE.TER IS 'Número de horas que harán esperar a un requirente para la generación de una respuesta definitiva ante una petición asíncrona';
 
   COMMENT ON TABLE CORE_EM_BACKOFFICE  IS 'Tabla en la que se configura el módulo de backoffice del emisor que enlaza las comunicaciones SCSP con la logica de negocio especifica de cada organismo';

CREATE TABLE CORE_EM_CODIGO_ERROR_SECUN 
   (	
    CODIGOSECUNDARIO VARCHAR2(4 CHAR) NOT NULL , 
	CODIGO VARCHAR2(4 CHAR) NOT NULL , 
	DESCRIPCION CLOB NOT NULL
   );
 

   COMMENT ON COLUMN CORE_EM_CODIGO_ERROR_SECUN.CODIGOSECUNDARIO IS 'Codigo de error SCSP al que esta asociado el error secundario';
 
   COMMENT ON COLUMN CORE_EM_CODIGO_ERROR_SECUN.CODIGO IS 'Código identificativo del error especifico secundario';
 
   COMMENT ON COLUMN CORE_EM_CODIGO_ERROR_SECUN.DESCRIPCION IS 'Literal descriptivo del mensaje de error secundario';
 
   COMMENT ON TABLE CORE_EM_CODIGO_ERROR_SECUN  IS 'Tabla de errores que permitirá almacenar aquellos mensajes de excepción gestionados por cada lógica de negocio específica de cada emisor, y que estrá asociado a un error genérico de SCSP';

CREATE TABLE CORE_EM_SECUENCIA_IDTRANS 
   (	
    PREFIJO VARCHAR2(9 CHAR) NOT NULL , 
	SECUENCIA VARCHAR2(26 CHAR) NOT NULL , 
	FECHAGENERACION TIMESTAMP (6) NOT NULL
   );
 

   COMMENT ON COLUMN CORE_EM_SECUENCIA_IDTRANS.PREFIJO IS 'Prefijo de IdTransmisiones al que estará asociada la secuencia';
 
   COMMENT ON COLUMN CORE_EM_SECUENCIA_IDTRANS.SECUENCIA IS 'Valor actual de la secuencia alfanumérica asociada al prefijo';
 
   COMMENT ON COLUMN CORE_EM_SECUENCIA_IDTRANS.FECHAGENERACION IS 'Última fecha en la que se ha generado un valor de secuencia';
 
   COMMENT ON TABLE CORE_EM_SECUENCIA_IDTRANS  IS 'Tabla utilizada para la generación de los valores de los nodos IdTransmisión en los emisores';

CREATE TABLE CORE_ESTADO_PETICION 
   (	
    CODIGO VARCHAR2(4 CHAR) NOT NULL , 
	MENSAJE VARCHAR2(256 CHAR) NOT NULL
   );
 

   COMMENT ON COLUMN CORE_ESTADO_PETICION.CODIGO IS 'Código identificativo del estado';
 
   COMMENT ON COLUMN CORE_ESTADO_PETICION.MENSAJE IS 'Literal descriptivo del estado';
 
   COMMENT ON TABLE CORE_ESTADO_PETICION  IS 'Tabla que almacena los posibles estados en los que se puede encontrar una petición SCSP. Sus posibles valores serán: •	0001 - Pendiente •	0002 - En proceso •	0003 - Tramitada';

CREATE TABLE CORE_LOG 
   (	
    ID NUMBER(19,0) NOT NULL , 
	FECHA TIMESTAMP (6), 
	CRITICIDAD VARCHAR2(10 CHAR), 
	CLASE VARCHAR2(256 CHAR), 
	METODO VARCHAR2(64 CHAR), 
	MENSAJE CLOB
   );
 

   COMMENT ON COLUMN CORE_LOG.ID IS 'Valor incremental autogenerado';
 
   COMMENT ON COLUMN CORE_LOG.FECHA IS 'Fecha  en la que se generó la traza de log';
 
   COMMENT ON COLUMN CORE_LOG.CRITICIDAD IS 'Tipo de nivel de la traza de log (WARN,INFO,DEBUG,ALL,ERROR)';
 
   COMMENT ON COLUMN CORE_LOG.CLASE IS 'Clase que generó la traza de log registrada';
 
   COMMENT ON COLUMN CORE_LOG.METODO IS 'Método específico de la clase que generó la traza de log';
 
   COMMENT ON COLUMN CORE_LOG.MENSAJE IS 'Literal descriptivo del error almacenado';
 
   COMMENT ON TABLE CORE_LOG  IS 'Tabla de configurada en el appender log4j para base de datos de la aplicación, donde se registrarán las posibles trazas de error';

CREATE TABLE CORE_MODULO 
   (	
    NOMBRE VARCHAR2(256 CHAR) NOT NULL , 
	DESCRIPCION VARCHAR2(512 CHAR), 
	ACTIVOENTRADA NUMBER(1,0) NOT NULL , 
	ACTIVOSALIDA NUMBER(1,0) NOT NULL
   );
 

   COMMENT ON COLUMN CORE_MODULO.NOMBRE IS 'Nombre del módulo';
 
   COMMENT ON COLUMN CORE_MODULO.DESCRIPCION IS 'Literal descriptivo del modulo a configurar su activación';
 
   COMMENT ON COLUMN CORE_MODULO.ACTIVOENTRADA IS 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) ó 0 (Inactivo)';
 
   COMMENT ON COLUMN CORE_MODULO.ACTIVOSALIDA IS 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) ó 0 (Inactivo)';
 
   COMMENT ON TABLE CORE_MODULO  IS 'Tabla que almacena la configuración de activación  o desactivación de los módulos que componen el ciclo de ejecución de las librerías SCSP';

CREATE TABLE CORE_MODULO_CONFIGURACION 
   (	
    MODULO VARCHAR2(256 CHAR) NOT NULL , 
	ACTIVOENTRADA NUMBER(1,0) NOT NULL , 
	ACTIVOSALIDA NUMBER(1,0) NOT NULL , 
	CERTIFICADO NUMBER(19,0)
   );
 

   COMMENT ON COLUMN CORE_MODULO_CONFIGURACION.MODULO IS 'Nombre del módulo a configurar';
 
   COMMENT ON COLUMN CORE_MODULO_CONFIGURACION.ACTIVOENTRADA IS 'Valor que indica si el módulo esta activo en la emisión de mensajes. Puede tomar valor 1 (Activo) ó 0 (Inactivo)';
 
   COMMENT ON COLUMN CORE_MODULO_CONFIGURACION.ACTIVOSALIDA IS 'Valor que indica si el módulo esta activo en la recepción de mensajes. Puede tomar valor 1 (Activo) ó 0 (Inactivo)';
 
   COMMENT ON TABLE CORE_MODULO_CONFIGURACION  IS 'Tabla que permite la sobreescritura de la configuración de activación de un módulo para un servicio concreto';


CREATE TABLE CORE_PARAMETRO_CONFIGURACION 
   (	
    NOMBRE VARCHAR2(64 CHAR) NOT NULL , 
	VALOR VARCHAR2(512 CHAR) NOT NULL , 
	DESCRIPCION VARCHAR2(512 CHAR)
   );
 

   COMMENT ON COLUMN CORE_PARAMETRO_CONFIGURACION.NOMBRE IS 'Nombre identificativo del parámetro';
 
   COMMENT ON COLUMN CORE_PARAMETRO_CONFIGURACION.VALOR IS 'Valor del parámetro';
 
   COMMENT ON COLUMN CORE_PARAMETRO_CONFIGURACION.DESCRIPCION IS 'Literal descriptivo de la utilidad del parámetro';
 
   COMMENT ON TABLE CORE_PARAMETRO_CONFIGURACION  IS 'Tabla  que almacena aquellos parámetros de configuración que son globales a todos los servicios y entornos para un mismo cliente integrador de las librerias SCSP';

CREATE TABLE CORE_PETICION_RESPUESTA 
   (	
    IDPETICION VARCHAR2(26 CHAR) NOT NULL , 
	ESTADO VARCHAR2(4 CHAR) NOT NULL , 
	FECHAPETICION TIMESTAMP (6) NOT NULL , 
	FECHARESPUESTA TIMESTAMP (6), 
	TER TIMESTAMP (6), 
	ERROR CLOB, 
	NUMEROENVIOS NUMBER(10,0) NOT NULL , 
	NUMEROTRANSMISIONES NUMBER(10,0) NOT NULL , 
	FECHAULTIMOSONDEO TIMESTAMP (6), 
	TRANSMISIONSINCRONA NUMBER(10,0), 
	DESCOMPUESTA VARCHAR2(1 CHAR), 
	CERTIFICADO NUMBER(19,0)
   );
 

   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.IDPETICION IS 'Identificador unívoco de la petición de servicio';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.ESTADO IS 'Codigo identificativo del estado de la petición. Tomará sus valores de las tablas scsp_estado_peticion y scsp_codigo_error';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.FECHAPETICION IS 'Timestamp que indica la fecha en la que se generó la petición';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.FECHARESPUESTA IS 'Timestamp que indica la fecha en la cual se recibió la respuesta a nuestra petición';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.TER IS 'Timestamp que indica la fecha a partir de la cual una petición de tipo asíncrono podrá solicitar una respuesta definitiva al servicio';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.ERROR IS 'Mensaje descriptivo del error, si lo hubiera, en la solicitud del servicio';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.NUMEROENVIOS IS 'Valor que indica el número de veces que se ha reenviado una petición al servicio';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.NUMEROTRANSMISIONES IS 'Número de Solicitudes de Transmisión que se enviaron en la petición';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.FECHAULTIMOSONDEO IS 'Timestamp que indica la fecha del último reenvio de una petición de tipo asincrono';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.TRANSMISIONSINCRONA IS 'Valor binario que indica si la petición fue solicitada a un servicio de tipo síncrono o asincrono. Podrá tomar los valores: 0: La comunicación fue de tipo asíncrono, 1: La comunicación fue de tipo síncrono';
 
   COMMENT ON COLUMN CORE_PETICION_RESPUESTA.DESCOMPUESTA IS 'Caracter que indica el estado del procesamiento de las transmisiones de la respuesta recibida. Podrá tomar los siguientes valores:    - S: Se ha procesado correctamente la respuesta, habiendo obtenido todas las transmisiones en ella incluidas y registradas en la tabla core_transmision. - N: No ha sido procesadas las transmisiones de la respuesta. - E: La respuesta terminó correctamente  (estado 0003), pero se ha producido un error al procesar sus trasmisiones';
 
   COMMENT ON TABLE CORE_PETICION_RESPUESTA  IS 'Esta tabla registrará un histórico de las peticiones y respuestas intercambiadas entre los requirentes y los emisores de servicios';

CREATE TABLE CORE_SECUENCIA_IDPETICION 
   (	
    PREFIJO VARCHAR2(8 CHAR) NOT NULL , 
	SECUENCIA VARCHAR2(23 CHAR) NOT NULL , 
	FECHAGENERACION TIMESTAMP (6) NOT NULL
   );
 

   COMMENT ON COLUMN CORE_SECUENCIA_IDPETICION.PREFIJO IS 'Prefijo utilizado para la construccion de los identificadores. Dicho valor podrá ser el prefijo especificado a ser utilizado para cada servicio o ante la no existencia del mismo, el número de serie del certificado digital firmante  de los mensajes';
 
   COMMENT ON COLUMN CORE_SECUENCIA_IDPETICION.SECUENCIA IS 'Valor secuencial que será concatenado al prefijo para generar los identificadores de petición. Este secuencial será de tipo alfanumérico, de tal forma que el siguiente valor a 00000009 sería 0000000A';
 
   COMMENT ON COLUMN CORE_SECUENCIA_IDPETICION.FECHAGENERACION IS 'Fecha en la que se registró  el secuencial';
 
   COMMENT ON TABLE CORE_SECUENCIA_IDPETICION  IS 'Tabla que almacena las semillas (secuenciales) utilizadas para la generación de los identificadores de peticion. Existirá un secuencial asociado a cada posible prefijo o número de serie de certificado digital firmante';


CREATE TABLE CORE_TIPO_MENSAJE 
   (	
    TIPO NUMBER(1,0) NOT NULL , 
	DESCRIPCION VARCHAR2(32 CHAR) NOT NULL
   );
 

   COMMENT ON COLUMN CORE_TIPO_MENSAJE.TIPO IS 'Tipo identificativo del mensaje';
 
   COMMENT ON COLUMN CORE_TIPO_MENSAJE.DESCRIPCION IS 'Literal descriptivo del tipo de mensaje';
 
   COMMENT ON TABLE CORE_TIPO_MENSAJE  IS 'Tabla maestra que almacena los diferentes tipos de mensajes que pueden ser intercambiados a lo largo de un ciclo de comunicación SCSP. Estos valores serán: •	Peticion •	Respuesta •	SolicitudRespuesta •	ConfirmaciónPeticion •	Fault';

CREATE TABLE CORE_TOKEN_DATA 
   (	
    IDPETICION VARCHAR2(26 CHAR) NOT NULL , 
	TIPOMENSAJE NUMBER(1,0) NOT NULL , 
	DATOS CLOB NOT NULL , 
	CLAVE VARCHAR2(256 CHAR), 
	MODOENCRIPTACION VARCHAR2(32 CHAR), 
	ALGORITMOENCRIPTACION VARCHAR2(32 CHAR)
   );

   COMMENT ON COLUMN CORE_TOKEN_DATA.IDPETICION IS 'Identificador de la petición a la cual está asociado el XML';
 
   COMMENT ON COLUMN CORE_TOKEN_DATA.TIPOMENSAJE IS 'Tipo de mensaje almacenado, podrá ser : -Peticion -Respuesta -SolicitudRespuesta -ConfirmaciónPeticion -Fault';
 
   COMMENT ON COLUMN CORE_TOKEN_DATA.DATOS IS 'Bytes del mensaje almacenado';
 
   COMMENT ON COLUMN CORE_TOKEN_DATA.CLAVE IS 'Clave simétrica utilizada para el cifrado de los datos';
 
   COMMENT ON COLUMN CORE_TOKEN_DATA.MODOENCRIPTACION IS 'Modo de encriptación utilizado para el proceso de cifrado.  Por defecto será TransportKey';
 
   COMMENT ON COLUMN CORE_TOKEN_DATA.ALGORITMOENCRIPTACION IS 'Algoritmo empleado en la encriptación del mensaje. Podrá tomar los siguientes valores: - AES128 - AES256 -DESDe';
 
   COMMENT ON TABLE CORE_TOKEN_DATA  IS 'Esta tabla almacenará el contenido de los mensajes intercambiados en un proceso de comunicación SCSP';

CREATE TABLE CORE_TRANSMISION 
   (
    IDSOLICITUD VARCHAR2(64 CHAR) NOT NULL , 
	IDPETICION VARCHAR2(26 CHAR) NOT NULL , 
	IDTRANSMISION VARCHAR2(64 CHAR), 
	IDSOLICITANTE VARCHAR2(10 CHAR) NOT NULL , 
	NOMBRESOLICITANTE VARCHAR2(256 CHAR), 
	DOCTITULAR VARCHAR2(16 CHAR), 
	NOMBRETITULAR VARCHAR2(40 CHAR), 
	APELLIDO1TITULAR VARCHAR2(40 CHAR), 
	APELLIDO2TITULAR VARCHAR2(40 CHAR), 
	NOMBRECOMPLETOTITULAR VARCHAR2(122 CHAR), 
	DOCFUNCIONARIO VARCHAR2(16 CHAR), 
	NOMBREFUNCIONARIO VARCHAR2(128 CHAR), 
	FECHAGENERACION TIMESTAMP (6), 
	UNIDADTRAMITADORA VARCHAR2(256 CHAR), 
	CODIGOPROCEDIMIENTO VARCHAR2(256 CHAR), 
	NOMBREPROCEDIMIENTO VARCHAR2(256 CHAR), 
	EXPEDIENTE VARCHAR2(256 CHAR), 
	FINALIDAD VARCHAR2(256 CHAR), 
	CONSENTIMIENTO VARCHAR2(3 CHAR), 
	ESTADO VARCHAR2(4 CHAR), 
	ERROR CLOB, 
	XMLTRANSMISION CLOB
   );
 

   COMMENT ON COLUMN CORE_TRANSMISION.IDSOLICITUD IS 'Identificador de la solicitud de transmisión dentro de las N posibles incluidas en una petición';
 
   COMMENT ON COLUMN CORE_TRANSMISION.IDPETICION IS 'Indentificador de la petición en la que se incluyó la solicitud de transmisión';
 
   COMMENT ON COLUMN CORE_TRANSMISION.IDTRANSMISION IS 'Indentificador de la transmisión que responde a la petición de servicio de la Solicitud de Transmisión identificada con idSolicitud';
 
   COMMENT ON COLUMN CORE_TRANSMISION.IDSOLICITANTE IS 'CIF de organismo solicitante del servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.NOMBRESOLICITANTE IS 'Nombre del organismo solicitante de servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.DOCTITULAR IS 'Documento identificativo del titular sobre el cual se está realizando la petición de servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.NOMBRETITULAR IS 'Nombre del titular sobre el que se realiza la petición del servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.APELLIDO1TITULAR IS 'Primer apellido del titular sobre el que se realiza la petición del servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.APELLIDO2TITULAR IS 'Segundo apellido del titular sobre el que se realiza la petición del servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.NOMBRECOMPLETOTITULAR IS 'Nombre completo del titular sobre el que se realiza la petición del servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.DOCFUNCIONARIO IS 'Documento identificativo del funcionario que generó la soliicitud de transmisión';
 
   COMMENT ON COLUMN CORE_TRANSMISION.NOMBREFUNCIONARIO IS 'Nombre del Funcionario que generó la solicitud de transmisión';
 
   COMMENT ON COLUMN CORE_TRANSMISION.FECHAGENERACION IS 'Fecha en la que se generó la transmisión';
 
   COMMENT ON COLUMN CORE_TRANSMISION.UNIDADTRAMITADORA IS 'Unidad Tramitadora asociada a la solicitud de transmisión';
 
   COMMENT ON COLUMN CORE_TRANSMISION.CODIGOPROCEDIMIENTO IS 'Código del procedimiento en base al cual se puede solicitar el servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.NOMBREPROCEDIMIENTO IS 'Nombre del procedimiento en base al cual se puede solicitar el servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.EXPEDIENTE IS 'Expediente asociado a la solicitud de transmsión';
 
   COMMENT ON COLUMN CORE_TRANSMISION.FINALIDAD IS 'Finalidad por la cual se emitió la solicitud de transmisión';
 
   COMMENT ON COLUMN CORE_TRANSMISION.CONSENTIMIENTO IS 'Tipo de consentimiento asociado a la transmisión. Deberá tomar uno de los dos posibles valores:  - Si -Ley ';
 
   COMMENT ON COLUMN CORE_TRANSMISION.ESTADO IS 'Estado concreto en el que se encuentra la transmisión';
 
   COMMENT ON COLUMN CORE_TRANSMISION.ERROR IS 'Descripción del posible error encontrado al solicitar la transmisión de servicio';
 
   COMMENT ON COLUMN CORE_TRANSMISION.XMLTRANSMISION IS 'El XML de la transmisión. Su almacenamiento será opcional, dependiendo de un parametro global almacenado en la tabla core_parametro_configuracion';
 
   COMMENT ON TABLE CORE_TRANSMISION  IS 'Esta tabla almacenará la información específica de cada transmisión que haya sido incluida en una respuesta de un servicio SCSP';
   
   
   
CREATE TABLE SCSP_CODIGO_ERROR 
   (	
	CODIGO VARCHAR2(4 CHAR) NOT NULL , 
	DESCRIPCION VARCHAR2(1024 CHAR) NOT NULL
   );
  
   CREATE TABLE SCSP_SECUENCIA_IDTRANSMISION 
   (	
	PREFIJO VARCHAR2(8 CHAR) NOT NULL , 
	SECUENCIA VARCHAR2(26 CHAR) NOT NULL , 
	FECHAGENERACION TIMESTAMP (6) NOT NULL
   );
   
    CREATE TABLE SCSP_CODIGO_ERROR_SECUNDARIO 
   (	
	CODIGOSECUNDARIO VARCHAR2(4 CHAR) NOT NULL , 
	CODIGO VARCHAR2(4 CHAR) NOT NULL , 
	DESCRIPCION CLOB NOT NULL
   );
  
  
  CREATE TABLE SCSP_ESTADO_PETICION 
   (	
	CODIGO VARCHAR2(4 CHAR) NOT NULL , 
	MENSAJE VARCHAR2(256 CHAR) NOT NULL
   );
   
   COMMENT ON COLUMN SCSP_CODIGO_ERROR.CODIGO IS 'Codigo identificativo del error';
   COMMENT ON COLUMN SCSP_CODIGO_ERROR.DESCRIPCION IS 'Litreral descriptivo del error';
   COMMENT ON TABLE SCSP_CODIGO_ERROR  IS 'Tabla que registraro los posibles errores genoricos que toda comunicacion SCSP puede generar';
   
  
  
   COMMENT ON COLUMN SCSP_ESTADO_PETICION.CODIGO IS 'Codigo identificativo del estado';
   COMMENT ON COLUMN SCSP_ESTADO_PETICION.MENSAJE IS 'Literal descriptivo del estado';
   COMMENT ON TABLE SCSP_ESTADO_PETICION  IS 'Tabla que almacena los posibles estados en los que se puede encontrar una peticion SCSP. Sus posibles valores seron: o	0001 - Pendiente o	0002 - En proceso o	0003 - Tramitada';
   
  
  
   COMMENT ON COLUMN SCSP_SECUENCIA_IDTRANSMISION.PREFIJO IS 'Prefijo de IdTransmisiones al que estaro asociada la secuencia';
   COMMENT ON COLUMN SCSP_SECUENCIA_IDTRANSMISION.SECUENCIA IS 'Valor actual de la secuencia alfanumorica asociada al prefijo';
   COMMENT ON COLUMN SCSP_SECUENCIA_IDTRANSMISION.FECHAGENERACION IS 'oltima fecha en la que se ha generado un valor de secuencia';
   COMMENT ON TABLE SCSP_SECUENCIA_IDTRANSMISION  IS 'Tabla utilizada para la generacion de los valores de los nodos IdTransmision en los emisores';
   
  
  
   COMMENT ON COLUMN SCSP_CODIGO_ERROR_SECUNDARIO.CODIGOSECUNDARIO IS 'Codigo de error SCSP al que esta asociado el error secundario';
   COMMENT ON COLUMN SCSP_CODIGO_ERROR_SECUNDARIO.CODIGO IS 'Codigo identificativo del error especifico secundario';
   COMMENT ON COLUMN SCSP_CODIGO_ERROR_SECUNDARIO.DESCRIPCION IS 'Literal descriptivo del mensaje de error secundario';
   COMMENT ON TABLE SCSP_CODIGO_ERROR_SECUNDARIO  IS 'Tabla de errores que permitiro almacenar aquellos mensajes de excepcion gestionados por cada logica de negocio especofica de cada emisor, y que estro asociado a un error genorico de SCSP';
	  

   CREATE TABLE EMS_ACL_CLASS 
   (	
    ID NUMBER(19,0) NOT NULL , 
	CLASS VARCHAR2(100 CHAR) NOT NULL
   );

  CREATE TABLE EMS_ACL_SID 
   (	
    ID NUMBER(19,0) NOT NULL , 
	PRINCIPAL NUMBER(1,0) NOT NULL , 
	SID VARCHAR2(100 CHAR) NOT NULL
   );
  
CREATE TABLE EMS_ACL_OBJECT_IDENTITY 
   (	
    ID NUMBER(19,0) NOT NULL , 
	OBJECT_ID_CLASS NUMBER(19,0) NOT NULL , 
	OBJECT_ID_IDENTITY NUMBER(19,0) NOT NULL , 
	PARENT_OBJECT NUMBER(19,0), 
	OWNER_SID NUMBER(19,0) NOT NULL , 
	ENTRIES_INHERITING NUMBER(1,0) NOT NULL
   );

  
CREATE TABLE EMS_ACL_ENTRY 
   (	
    ID NUMBER(19,0) NOT NULL , 
	ACL_OBJECT_IDENTITY NUMBER(19,0) NOT NULL , 
	ACE_ORDER NUMBER(19,0) NOT NULL , 
	SID NUMBER(19,0) NOT NULL , 
	MASK NUMBER(19,0) NOT NULL , 
	GRANTING NUMBER(1,0) NOT NULL , 
	AUDIT_SUCCESS NUMBER(1,0) NOT NULL , 
	AUDIT_FAILURE NUMBER(1,0) NOT NULL
   );

  CREATE TABLE EMS_REDIR_PETICIO 
   (	
    ID NUMBER(19,0) NOT NULL , 
	DATA_COMPROVACIO TIMESTAMP (6), 
	DATA_PETICIO TIMESTAMP (6), 
	DATA_RESPOSTA TIMESTAMP (6), 
	ERROR CLOB, 
	ESTAT VARCHAR2(64 CHAR) NOT NULL , 
	NUM_ENVIAMENTS NUMBER(10,0) NOT NULL , 
	NUM_TRANSMISSIONS NUMBER(10,0) NOT NULL , 
	PETICIO_ID VARCHAR2(512 CHAR) NOT NULL , 
	SERVEI_CODI VARCHAR2(1024 CHAR) NOT NULL , 
	SINCRONA NUMBER(1,0), 
	TER TIMESTAMP (6), 
	VERSION NUMBER(19,0) NOT NULL , 
	EMISSOR_CODI VARCHAR2(10 CHAR)
   );


CREATE TABLE EMS_REDIR_MISSATGE 
   (	
    ID NUMBER(19,0) NOT NULL , 
	TIPUS NUMBER(10,0) NOT NULL , 
	VERSION NUMBER(19,0) NOT NULL , 
	XML CLOB, 
	PETICIO_ID NUMBER(19,0) NOT NULL
   );
  
  CREATE TABLE EMS_REDIR_SOLICITUD 
   (	
    ID NUMBER(19,0) NOT NULL , 
	CONSENTIMENT VARCHAR2(12 CHAR), 
	DATA_GENERACIO TIMESTAMP (6), 
	ERROR CLOB, 
	ESTAT VARCHAR2(16 CHAR), 
	FINALITAT VARCHAR2(1024 CHAR), 
	FUNCIONARI_DOC VARCHAR2(64 CHAR), 
	FUNCIONARI_NOM VARCHAR2(640 CHAR), 
	PROCEDIMENT_CODI VARCHAR2(1024 CHAR), 
	PROCEDIMENT_NOM VARCHAR2(1024 CHAR), 
	SOLICITANT_ID VARCHAR2(160 CHAR) NOT NULL , 
	SOLICITANT_NOM VARCHAR2(1024 CHAR), 
	SOLICITUD_ID VARCHAR2(1024 CHAR) NOT NULL , 
	TITULAR_DOC VARCHAR2(64 CHAR), 
	TITULAR_LLINATGE1 VARCHAR2(640 CHAR), 
	TITULAR_LLINATGE2 VARCHAR2(640 CHAR), 
	TITULAR_NOM VARCHAR2(640 CHAR), 
	TITULAR_NOM_SENCER VARCHAR2(1024 CHAR), 
	TRANSMISION_ID VARCHAR2(1024 CHAR), 
	UNITAT_TRAMITADORA VARCHAR2(1024 CHAR), 
	VERSION NUMBER(19,0) NOT NULL , 
	PETICIO_ID NUMBER(19,0) NOT NULL
   );
  
  CREATE TABLE EMS_USUARI 
   (	
    CODI VARCHAR2(64 CHAR) NOT NULL , 
	INICIALITZAT NUMBER(1,0), 
	NIF VARCHAR2(9 CHAR) NOT NULL , 
	NOM VARCHAR2(200 CHAR), 
	EMAIL VARCHAR2(200 CHAR), 
	VERSION NUMBER(19,0) NOT NULL
   );


CREATE TABLE EMS_SERVEI 
   (	
    ID NUMBER(19,0) NOT NULL , 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	ACTIU NUMBER(1,0), 
	CODI VARCHAR2(256 CHAR) NOT NULL , 
	CONTRASENYA VARCHAR2(1024 CHAR), 
	DESCRIPCIO VARCHAR2(4000 CHAR), 
	NOM VARCHAR2(1024 CHAR) NOT NULL , 
	USUARI VARCHAR2(256 CHAR), 
	VERSION NUMBER(19,0) NOT NULL , 
	CREATEDBY_CODI VARCHAR2(256 CHAR), 
	LASTMODIFIEDBY_CODI VARCHAR2(256 CHAR), 
	CONFIGURAT NUMBER(1,0), 
	BACKOFFICE_CLASS VARCHAR2(256 CHAR), 
	BACKCAIB_AUTENTICACIO NUMBER(10,0), 
	BACKCAIB_URL VARCHAR2(256 CHAR), 
	TIPUS NUMBER(2,0) NOT NULL , 
	RESOLVER_CLASS VARCHAR2(1024 CHAR), 
	URL_PER_DEFECTE VARCHAR2(256 CHAR), 
	RESPONSE_RESOLVER_CLASS VARCHAR2(1024 CHAR), 
	BACKCAIB_ASYNC_TIPUS NUMBER(10,0), 
	BACKCAIB_ASYNC_TER NUMBER(10,0),
	XSD_ACTIVA NUMBER(1),
	XSD_ESQUEMA_BAK VARCHAR2(256)
   );

CREATE TABLE EMS_SERVEI_RUTA_DESTI 
   (	
    ID NUMBER(19,0) NOT NULL , 
	ENTITAT_CODI VARCHAR2(256 CHAR) NOT NULL , 
	URL VARCHAR2(1024 CHAR) NOT NULL , 
	SERVEI_ID NUMBER(19,0) NOT NULL , 
	VERSION NUMBER(19,0) NOT NULL , 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	CREATEDBY_CODI VARCHAR2(256 CHAR), 
	LASTMODIFIEDBY_CODI VARCHAR2(256 CHAR), 
	ORDRE NUMBER(19,0)
   );



CREATE TABLE schema_version 
   (	
    installed_rank NUMBER(19,0) NOT NULL , 
	version VARCHAR2(50 CHAR), 
	description VARCHAR2(200 CHAR) NOT NULL , 
	type VARCHAR2(20 CHAR) NOT NULL , 
	script VARCHAR2(1000 CHAR) NOT NULL , 
	checksum NUMBER(19), 
	installed_by VARCHAR2(100 CHAR) NOT NULL , 
	installed_on TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP NOT NULL , 
	execution_time NUMBER(19,0) NOT NULL , 
	success NUMBER(1,0) NOT NULL
);
  
  
  
CREATE TABLE EMS_BACKOFFICE_COM 
   (	
    ID NUMBER(19) NOT NULL , 
	PETICIO_ID NUMBER(19) NOT NULL , 
	SOLICITUD_ID NUMBER(19), 
	PETICIO_DATA TIMESTAMP (6) NOT NULL , 
	PETICIO_XML CLOB NOT NULL , 
	RESPOSTA_DATA TIMESTAMP (6), 
	RESPOSTA_XML CLOB, 
	VERSION NUMBER(19), 
	ERROR CLOB
	 );

CREATE TABLE EMS_BACKOFFICE_PET 
   (	
    ID NUMBER(19) NOT NULL , 
	PETICIO_ID VARCHAR2(26 CHAR) NOT NULL , 
	ESTAT NUMBER(10) NOT NULL , 
	TER_DATA TIMESTAMP (6) NOT NULL , 
	DARRERA_SOL_DATA TIMESTAMP (6), 
	DARRERA_SOL_ID VARCHAR2(256 CHAR), 
	PROCESSADES_ERROR NUMBER(10), 
	PROCESSADES_TOTAL NUMBER(10), 
	COMUNICACIO_ID NUMBER(19), 
	VERSION NUMBER(19)
   );
  
  
CREATE TABLE EMS_BACKOFFICE_SOL 
   (	
    ID NUMBER(19) NOT NULL , 
	PETICIO_ID NUMBER(19) NOT NULL , 
	SOLICITUD_ID VARCHAR2(256 CHAR) NOT NULL , 
	ESTAT NUMBER(10) NOT NULL , 
	COMUNICACIO_ID NUMBER(19), 
	VERSION NUMBER(19)
   );
 
    CREATE TABLE AUTORIZACION_CA 
   (	
	CODCA VARCHAR2(512 CHAR), 
	NOMBRE VARCHAR2(256 CHAR)
   );
    