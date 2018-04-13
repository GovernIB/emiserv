--------------------------------------------------------
--  Configuraci�n de esquema scsp emisor 
--------------------------------------------------------  

--------------------------------------------------------
--  Inserts for Table CORE_PARAMETRO_CONFIGURACION
--	Se deben sustituir los valores puestos a null por los valores reales de cada configuraci�n
--	Consulte el documento del modelo de datos y el de configuraci�n para ver que valores puede tomar cada par�metro
--------------------------------------------------------

Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('cif.emisor',null,'CIF del Emisor');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('nombre.emisor',null,'Nombre del Emisor');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('nivelTraza','DEBUG','Nivel de traza a mostrar en los logs');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('pathLogs',null,'Ruta del fichero donde guardaremos los logs');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('logDinamico.enabled',null,'Par�metro que indica si se cargar� de BBDD la configuraci�n de logs o de fichero');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('core_log.enabled',null,'Par�metro que indica si se guardar�n trazas en CORE_LOG de los errores ocurridos');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('almacenamiento.ficheros',null,'Directorio donde se guardan los ficheros de las peticiones');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('almacenamiento.transmisiones',1,'Par�metro que indica si se guardar�n los nodos de las transmisiones');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('keystoreFile',null,'Ruta del fichero keystore');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('keystoreType','jks','Tipo de keystore');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('keystorePass',null,'Password del keystore');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.enabled','true','Par�metro que indica si @Firma est� activo');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.idAplicacion',null,'Id de aplicaci�n para enviar a @Firma');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.url','https://des-afirma.redsara.es/afirmaws/services/ValidarCertificado','URL de @Firma');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.modoValidacion',2,'Modo de validaci�n del certificado con @Firma');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('afirma.autenticacion.aliasSignerCert',null,'Alias del certificado con el que firmamos la peticiones para @Firma');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('custom.cert.validation.class','none','Clase propia de validaci�n de certificados');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('periodo.validacion.certificados',24,'Periodo de validez del certificado en cach�');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('tipoId','long','Longitud del identificador de la petici�n');
Insert into CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) values ('prefijo.idtransmision',null,'Cuando no haya indicado un prefijo asociado al servicio y este si est� definido se utilizar� este para la generaci�n del id de transmisi�n.');
INSERT INTO CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) VALUES ('version.datamodel.scsp','3.2.2','Especifica la versi�n del modelo de datos actual');

--------------------------------------------------------
--  Inserts for Table CORE_CLAVE_PRIVADA
-- Se debe modificar seg�n necesidades y seg�n la configuraci�n del almac�n de claves utilizado
--------------------------------------------------------
Insert into CORE_CLAVE_PRIVADA (ALIAS,NOMBRE,PASSWORD,NUMEROSERIE) values (null,null,null,null);

--------------------------------------------------------
--  Inserts for Table CORE_CLAVE_PUBLICA
-- Se debe modificar seg�n necesidades y seg�n la configuraci�n del almac�n de claves utilizado
--------------------------------------------------------
Insert into CORE_CLAVE_PUBLICA (ALIAS,NOMBRE,NUMEROSERIE) values (null,null,null);

--------------------------------------------------------
--  Inserts for Table CORE_EMISOR_CERTIFICADO
-- Se deben eliminar los inserts correspondientes a organismos de los que no vamos a consumir servicios
--------------------------------------------------------

Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('S2826053G','Catastro');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('Q2819009H','SPEE-INEM');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('Q2827003A','TGSS');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('Q2826000H','AEAT');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('S2818001F','Educacion');
Insert into CORE_EMISOR_CERTIFICADO (CIF,NOMBRE) values ('S2833002E','MPTAP');

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
-- Se debe modificar seg�n necesidades los par�metros '1' Activado por defecto � '0' desactivado
--------------------------------------------------------

Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('AlmacenarBaseDatos','AlmacenarBaseDatos',1,1);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('ValidarCertificado','ValidarCertificado',1,0);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('AlmacenarFichero','AlmacenarFichero',1,1);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('ValidarEsquema','ValidarEsquema',1,1);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('AlmacenarFicheroPlain','AlmacenarFicheroPlain',1,1);
Insert into CORE_MODULO (NOMBRE,DESCRIPCION,ACTIVOENTRADA,ACTIVOSALIDA) values ('AutorizacionPeticion', 'AutorizacionPeticion',1,0);

--------------------------------------------------------
--  Inserts for Table AUTORIZACION_APLICACION
-- Se deben modificar los par�metros a null
--------------------------------------------------------

Insert into AUTORIZACION_APLICACION (IDAPLICACION,NIFCERTIFICADO,NUMEROSERIE,CN,TIEMPOCOMPROBACION,AUTORIDADCERTIF,FECHAALTA,FECHABAJA) values (1,null,null,null,to_timestamp('30/09/11 10:16:21,897000000','DD/MM/RR HH24:MI:SS,FF'),null,to_timestamp('08/08/11 15:51:39,539000000','DD/MM/RR HH24:MI:SS,FF'),null);

--------------------------------------------------------
--  Inserts for Table AUTORIZACION_CERTIFICADO
-- Se deben modificar los par�metros a null
--------------------------------------------------------

Insert into AUTORIZACION_CERTIFICADO (CODCERTIFICADO,IDAPLICACION) values (null,null);

--------------------------------------------------------
--  Inserts for Table AUTORIZACION_ORGANISMO
-- Se deben modificar los par�metros a null
--------------------------------------------------------

Insert into AUTORIZACION_ORGANISMO (IDORGANISMO,IDAPLICACION,FECHAALTA,FECHABAJA,NOMBREORGANISMO) values (null,null,to_timestamp('08/08/11 15:51:39,539000000','DD/MM/RR HH24:MI:SS,FF'),null,null);

--------------------------------------------------------
--  Inserts for Table EMISOR_BACKOFFICE
-- Se deben modificar los par�metros a null
--------------------------------------------------------

Insert into EMISOR_BACKOFFICE (CERTIFICADO,BEANNAME,CLASSNAME,TER) values (null,null,null,null);
