--------------------------------------------------------
--  Configuración de esquema scsp emisor 
--------------------------------------------------------  

--------------------------------------------------------
--  Inserts for Table CORE_PARAMETRO_CONFIGURACION
--	Se deben sustituir los valores puestos a null por los valores reales de cada configuración
--	Consulte el documento del modelo de datos y el de configuración para ver que valores puede tomar cada parámetro
--------------------------------------------------------

Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('cif.emisor',null,'CIF del Emisor');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('nombre.emisor',null,'Nombre del Emisor');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('nivelTraza','DEBUG','Nivel de traza a mostrar en los logs');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('pathLogs',null,'Ruta del fichero donde guardaremos los logs');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('logDinamico.enabled',null,'Parámetro que indica si se cargará de BBDD la configuración de logs o de fichero');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('core_log.enabled',null,'Parámetro que indica si se guardarán trazas en CORE_LOG de los errores ocurridos');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('almacenamiento.ficheros',null,'Directorio donde se guardan los ficheros de las peticiones');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('almacenamiento.transmisiones',1,'Parámetro que indica si se guardarán los nodos de las transmisiones');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('keystoreFile',null,'Ruta del fichero keystore');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('keystoreType','jks','Tipo de keystore');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('keystorePass',null,'Password del keystore');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.enabled','true','Parámetro que indica si @Firma está activo');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.idAplicacion',null,'Id de aplicación para enviar a @Firma');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.url','https://des-afirma.redsara.es/afirmaws/services/ValidarCertificado','URL de @Firma');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.modoValidacion',2,'Modo de validación del certificado con @Firma');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.autenticacion.aliasSignerCert',null,'Alias del certificado con el que firmamos la peticiones para @Firma');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('custom.cert.validation.class','none','Clase propia de validación de certificados');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('periodo.validacion.certificados',24,'Periodo de validez del certificado en caché');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('tipoId','long','Longitud del identificador de la petición');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('prefijo.idtransmision',null,'Cuando no haya indicado un prefijo asociado al servicio y este si está definido se utilizará este para la generación del id de transmisión.');
INSERT INTO CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) VALUES ('version.datamodel.scsp','3.5.3','Especifica la versión del modelo de datos actual');
INSERT INTO CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) VALUES ('validate.nif.emisor.enabled','true','Flag que indica si se valida el valor del nodo <NifEmisor> de la petición');
--------------------------------------------------------
--  Inserts for Table CORE_CLAVE_PRIVADA
-- Se debe modificar según necesidades y según la configuración del almacén de claves utilizado
--------------------------------------------------------
Insert into CORE_CLAVE_PRIVADA (ID,ALIAS,NOMBRE,PASSWORD,NUMEROSERIE,FECHAALTA) values (ID_CLAVE_PRIVADA_SEQUENCE.nextval, null,null,null,null,sysdate);

--------------------------------------------------------
--  Inserts for Table CORE_CLAVE_PUBLICA
-- Se debe modificar según necesidades y según la configuración del almacén de claves utilizado
--------------------------------------------------------
Insert into CORE_CLAVE_PUBLICA (ID,ALIAS,NOMBRE,NUMEROSERIE,FECHAALTA) values (ID_CLAVE_PUBLICA_SEQUENCE.nextval, null,null,null,sysdate);

--------------------------------------------------------
--  Inserts for Table CORE_EMISOR_CERTIFICADO
-- Se deben eliminar los inserts correspondientes a organismos de los que no vamos a consumir servicios
--------------------------------------------------------

Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('S2826053G','Catastro');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('Q2819009H','SPEE-INEM');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('Q2827003A','TGSS');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('Q2826000H','AEAT');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('S2818001F','Educacion');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('S2833002E','MINHAP');

Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('S2813001A','Ministerio de Justicia');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('S2826013A','Dirección General de Seguros');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('S3100000C','Gobierno de Navarra');

--------------------------------------------------------
--  Inserts for Table CORE_SERVICIO
-- Se deben eliminar los inserts correspondientes a los servicios que no vamos a ofrecer
-- Vienen preconfigurados dos ejemplos V2 XMLDsig y V3 WS-Security
-- Antes de ejecutar los inserts se debe de modificar PREFIJO-PETICION, PREFIJO-IDTRANSMISION y ALIAS-CERTIFICADO-FIRMA
--------------------------------------------------------

Insert into CORE_SERVICIO (CODCERTIFICADO,URLSINCRONA,URLASINCRONA,ACTIONSINCRONA,ACTIONASINCRONA,ACTIONSOLICITUD,VERSIONESQUEMA,TIPOSEGURIDAD,PREFIJOPETICION,XPATHCIFRADOSINCRONO,XPATHCIFRADOASINCRONO,ESQUEMAS,CLAVEFIRMA,CLAVECIFRADO,ALGORITMOCIFRADO,NUMEROMAXIMOREENVIOS,MAXSOLICITUDESPETICION,PREFIJOIDTRANSMISION,DESCRIPCION,EMISOR,FECHAALTA,FECHABAJA,CADUCIDAD,XPATHLITERALERROR,XPATHCODIGOERROR,TIMEOUT,VALIDACIONFIRMA) values ('CODCERTIFICADOV3',null,null,null,null,null,'V3','WSSecurity',null,'//*[local-name()=''DatosEspecificos'']','//*[local-name()=''Transmisiones'']',null,'ALIAS-CERTIFICADO-FIRMA',null,'Basic128Rsa15',5,0,'PREFIJO-IDTRANSMISION','Certificado prueba V3','Q2819009H',to_timestamp('08/08/11 16:17:21,928000000','DD/MM/RR HH24:MI:SS,FF'),null,7,null,null,60,'estricto');
Insert into CORE_SERVICIO (CODCERTIFICADO,URLSINCRONA,URLASINCRONA,ACTIONSINCRONA,ACTIONASINCRONA,ACTIONSOLICITUD,VERSIONESQUEMA,TIPOSEGURIDAD,PREFIJOPETICION,XPATHCIFRADOSINCRONO,XPATHCIFRADOASINCRONO,ESQUEMAS,CLAVEFIRMA,CLAVECIFRADO,ALGORITMOCIFRADO,NUMEROMAXIMOREENVIOS,MAXSOLICITUDESPETICION,PREFIJOIDTRANSMISION,DESCRIPCION,EMISOR,FECHAALTA,FECHABAJA,CADUCIDAD,XPATHLITERALERROR,XPATHCODIGOERROR,TIMEOUT,VALIDACIONFIRMA) values ('CODCERTIFICADOV2',null,null,null,null,null,'V2','XMLSignature',null,null,null,null,'ALIAS-CERTIFICADO-FIRMA',null,null,5,0,'PREFIJO-IDTRANSMISION','Certificado prueba V2','Q2819009H',to_timestamp('08/08/11 16:17:21,928000000','DD/MM/RR HH24:MI:SS,FF'),null,7,null,null,60,'estricto');

--------------------------------------------------------
--  Inserts for Table CORE_MODULO
-- Se debe modificar según necesidades los parámetros '1' Activado por defecto, '0' desactivado
--------------------------------------------------------

Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('AlmacenarBaseDatos','AlmacenarBaseDatos',1,1);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('ValidarCertificado','ValidarCertificado',1,0);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('AlmacenarFichero','AlmacenarFichero',1,1);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('ValidarEsquema','ValidarEsquema',1,1);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('AlmacenarFicheroPlain','AlmacenarFicheroPlain',1,1);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('AutorizacionPeticion', 'AutorizacionPeticion',1,0);

--------------------------------------------------------
--  Inserts for Table AUTORIZACION_APLICACION
-- Se deben modificar los parámetros a null
--------------------------------------------------------

Insert into AUTORIZACION_APLICACION (IDAPLICACION,NIFCERTIFICADO,NUMEROSERIE,CN,TIEMPOCOMPROBACION,AUTORIDADCERTIF,FECHAALTA,FECHABAJA) values (1,null,null,null,to_timestamp('30/09/11 10:16:21,897000000','DD/MM/RR HH24:MI:SS,FF'),null,to_timestamp('08/08/11 15:51:39,539000000','DD/MM/RR HH24:MI:SS,FF'),null);

--------------------------------------------------------
--  Inserts for Table AUTORIZACION_CERTIFICADO
-- Se deben modificar los parámetros a null
--------------------------------------------------------

Insert into AUTORIZACION_CERTIFICADO (CODCERTIFICADO,IDAPLICACION) values (null,null);

--------------------------------------------------------
--  Inserts for Table AUTORIZACION_ORGANISMO
-- Se deben modificar los parámetros a null
--------------------------------------------------------

Insert into AUTORIZACION_ORGANISMO (IDORGANISMO,IDAPLICACION,FECHAALTA,FECHABAJA,NOMBREORGANISMO) values (null,null,to_timestamp('08/08/11 15:51:39,539000000','DD/MM/RR HH24:MI:SS,FF'),null,null);

--------------------------------------------------------
--  Inserts for Table EMISOR_BACKOFFICE
-- Se deben modificar los parámetros a null
--------------------------------------------------------

Insert into EMISOR_BACKOFFICE (CERTIFICADO,BEANNAME,CLASSNAME,TER) values (null,null,null,null);

