--------------------------------------------------------
--  Creacion de esquema scsp emisor 
--------------------------------------------------------  

CREATE SEQUENCE ID_APLICACION_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;

 
--------------------------------------------------------
--  DDL for Table SCSP_CODIGO_ERROR
--------------------------------------------------------

  CREATE TABLE "SCSP_CODIGO_ERROR" 
   (	
	"CODIGO" VARCHAR2(4 CHAR), 
	"DESCRIPCION" VARCHAR2(1024 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_TRANSMISION
--------------------------------------------------------

  CREATE TABLE "CORE_TRANSMISION" 
   (	
	"IDSOLICITUD" VARCHAR2(64 CHAR), 
	"IDPETICION" VARCHAR2(26 CHAR), 
	"IDTRANSMISION" VARCHAR2(64 CHAR), 
	"IDSOLICITANTE" VARCHAR2(10 CHAR), 
	"NOMBRESOLICITANTE" VARCHAR2(256 CHAR), 
	"DOCTITULAR" VARCHAR2(16 CHAR), 
	"NOMBRETITULAR" VARCHAR2(40 CHAR),
	"APELLIDO1TITULAR" VARCHAR2(40 CHAR),
	"APELLIDO2TITULAR" VARCHAR2(40 CHAR),
	"NOMBRECOMPLETOTITULAR" VARCHAR2(122 CHAR),
	"DOCFUNCIONARIO" VARCHAR2(16 CHAR), 
	"NOMBREFUNCIONARIO" VARCHAR2(128 CHAR), 
	"FECHAGENERACION" TIMESTAMP (6), 
	"UNIDADTRAMITADORA" VARCHAR2(256 CHAR), 
	"CODIGOPROCEDIMIENTO" VARCHAR2(256 CHAR), 
	"NOMBREPROCEDIMIENTO" VARCHAR2(256 CHAR), 
	"EXPEDIENTE" VARCHAR2(256 CHAR), 
	"FINALIDAD" VARCHAR2(256 CHAR), 
	"CONSENTIMIENTO" VARCHAR2(3 CHAR), 
	"ESTADO" VARCHAR2(4  CHAR), 
	"ERROR" VARCHAR2(4000 CHAR), 
	"XMLTRANSMISION" LONG
   );
 CREATE INDEX core_transmision_index_idpet  ON core_transmision (idpeticion);
--------------------------------------------------------
--  DDL for Table CORE_TIPO_MENSAJE
--------------------------------------------------------

  CREATE TABLE "CORE_TIPO_MENSAJE" 
   (	
	"TIPO" NUMBER(1,0), 
	"DESCRIPCION" VARCHAR2(32 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_CACHE_CERTIFICADOS
--------------------------------------------------------

  CREATE TABLE "CORE_CACHE_CERTIFICADOS" 
   (	
	"NUMSERIE" VARCHAR2(256 CHAR), 
	"AUTORIDADCERTIF" VARCHAR2(512 CHAR), 
	"TIEMPOCOMPROBACION" TIMESTAMP (6), 
	"REVOCADO" NUMBER(10,0)
   );
 
--------------------------------------------------------
--  DDL for Table SCSP_ESTADO_PETICION
--------------------------------------------------------

  CREATE TABLE "SCSP_ESTADO_PETICION" 
   (	
	"CODIGO" VARCHAR2(4 CHAR), 
	"MENSAJE" VARCHAR2(256 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_MODULO
--------------------------------------------------------

  CREATE TABLE "CORE_MODULO" 
   (	
	"NOMBRE" VARCHAR2(256 CHAR), 
	"DESCRIPCION" VARCHAR2(512 CHAR), 
	"ACTIVOENTRADA" NUMBER(1,0), 
	"ACTIVOSALIDA" NUMBER(1,0)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

  CREATE TABLE "CORE_MODULO_CONFIGURACION" 
   (	
	"MODULO" VARCHAR2(256 CHAR), 
	"CERTIFICADO" NUMBER(19,0), 
	"ACTIVOENTRADA" NUMBER(1,0), 
	"ACTIVOSALIDA" NUMBER(1,0)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_CLAVE_PUBLICA
--------------------------------------------------------

 CREATE TABLE "CORE_CLAVE_PUBLICA" 
   (	
    "ID" NUMBER(19,0), 
	"ALIAS" VARCHAR2(256 CHAR), 
	"NOMBRE" VARCHAR2(256 CHAR),
	"FECHAALTA" TIMESTAMP (6), 	
	"FECHABAJA" TIMESTAMP (6), 	
	"NUMEROSERIE" VARCHAR2(256 CHAR)
   );

  ALTER TABLE "CORE_CLAVE_PUBLICA" ADD PRIMARY KEY ("ID");
--------------------------------------------------------
--  DDL for Table CORE_SECUENCIA_IDPETICION
--------------------------------------------------------

  CREATE TABLE "CORE_SECUENCIA_IDPETICION" 
   (	
	"PREFIJO" VARCHAR2(8 CHAR), 
	"SECUENCIA" VARCHAR2(23 CHAR), 
	"FECHAGENERACION" TIMESTAMP (6)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

  CREATE TABLE "CORE_PETICION_RESPUESTA" 
   (	
	"IDPETICION" VARCHAR2(26 CHAR), 
	"CERTIFICADO"  NUMBER(19,0),
	"ESTADO" VARCHAR2(4 CHAR), 
	"FECHAPETICION" TIMESTAMP (6), 
	"FECHARESPUESTA" TIMESTAMP (6), 
	"TER" TIMESTAMP (6), 
	"ERROR" VARCHAR2(4000 CHAR), 
	"NUMEROENVIOS" NUMBER(10,0), 
	"NUMEROTRANSMISIONES" NUMBER(10,0), 
	"FECHAULTIMOSONDEO" TIMESTAMP (6), 
	"TRANSMISIONSINCRONA" NUMBER(10,0), 
	"DESCOMPUESTA" VARCHAR2(1 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_TOKEN_DATA
--------------------------------------------------------

  CREATE TABLE "CORE_TOKEN_DATA" 
   (	
	"IDPETICION" VARCHAR2(26 CHAR), 
	"TIPOMENSAJE" NUMBER(1,0), 
	"DATOS" LONG, 
	"CLAVE" VARCHAR2(256 CHAR), 
	"MODOENCRIPTACION" VARCHAR2(32 CHAR), 
	"ALGORITMOENCRIPTACION" VARCHAR2(32 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table CORE_PARAMETRO_CONFIGURACION
--------------------------------------------------------

  CREATE TABLE "CORE_PARAMETRO_CONFIGURACION" 
   (	
	"NOMBRE" VARCHAR2(64 CHAR), 
	"VALOR" VARCHAR2(512 CHAR), 
	"DESCRIPCION" VARCHAR2(512 CHAR)
   );
 

--------------------------------------------------------
--  DDL for Table CORE_EMISOR_CERTIFICADO
--------------------------------------------------------

  CREATE TABLE "CORE_EMISOR_CERTIFICADO" 
   (	
  "ID" NUMBER(19,0), 
	"CIF" VARCHAR2(16 CHAR), 
	"NOMBRE" VARCHAR2(50 CHAR),
	"FECHABAJA" TIMESTAMP(6)
   );
 

--------------------------------------------------------
--  DDL for Table CORE_SERVICIO
--------------------------------------------------------

  CREATE TABLE "CORE_SERVICIO" 
   (	
    "ID" NUMBER(19,0), 
	"CODCERTIFICADO" VARCHAR2(64 CHAR), 
	"URLSINCRONA" VARCHAR2(256 CHAR), 
	"URLASINCRONA" VARCHAR2(256 CHAR), 
	"ACTIONSINCRONA" VARCHAR2(256 CHAR), 
	"ACTIONASINCRONA" VARCHAR2(256 CHAR), 
	"ACTIONSOLICITUD" VARCHAR2(256 CHAR), 
	"VERSIONESQUEMA" VARCHAR2(32 CHAR), 
	"TIPOSEGURIDAD" VARCHAR2(16 CHAR), 
	"PREFIJOPETICION" VARCHAR2(8 CHAR), 
	"XPATHCIFRADOSINCRONO" VARCHAR2(256 CHAR), 
	"XPATHCIFRADOASINCRONO" VARCHAR2(256 CHAR), 
	"ESQUEMAS" VARCHAR2(256 CHAR), 
	"CLAVEFIRMA" NUMBER(19,0), 
	"CLAVECIFRADO" NUMBER(19,0), 
	"ALGORITMOCIFRADO" VARCHAR2(32 CHAR), 
	"NUMEROMAXIMOREENVIOS" NUMBER(10,0), 
	"MAXSOLICITUDESPETICION" NUMBER(10,0), 
	"PREFIJOIDTRANSMISION" VARCHAR2(8 CHAR), 
	"DESCRIPCION" VARCHAR2(512 CHAR), 
	"EMISOR" NUMBER(19,0), 
	"FECHAALTA" TIMESTAMP (6), 
	"FECHABAJA" TIMESTAMP (6), 
	"CADUCIDAD" NUMBER(10,0),
	"XPATHLITERALERROR" VARCHAR2(256 CHAR),
	"XPATHCODIGOERROR" VARCHAR2(256 CHAR),
	"TIMEOUT" NUMBER(10,0) DEFAULT 60,
	"VALIDACIONFIRMA" VARCHAR2(32 CHAR) DEFAULT 'estricto',
	"PLANTILLAXSLT" VARCHAR2(512 CHAR)
   );
   ALTER TABLE "CORE_SERVICIO" ADD PRIMARY KEY ("ID");
   
--------------------------------------------------------
--  DDL for Table ORGANISMO_CESIONARIO
--------------------------------------------------------
   
  CREATE TABLE   ORGANISMO_CESIONARIO  (
   ID NUMBER(19,0), 
   NOMBRE  VARCHAR2(50 CHAR),
   CIF  VARCHAR2(50 CHAR),
   FECHAALTA TIMESTAMP (6), 
   FECHABAJA TIMESTAMP (6), 
   BLOQUEADO  NUMBER(1,0),
   LOGO  long raw,
   PRIMARY KEY (ID)
  )  ;
  
--------------------------------------------------------
--  DDL for Table CORE_CLAVE_PRIVADA
--------------------------------------------------------

  CREATE TABLE "CORE_CLAVE_PRIVADA" 
   (	
	"ALIAS" VARCHAR2(256 CHAR), 
	"NOMBRE" VARCHAR2(256 CHAR), 
	"PASSWORD" VARCHAR2(256 CHAR), 
	"ID" NUMBER(19,0), 
	"ORGANISMO" NUMBER(19,0), 
	"FECHAALTA" TIMESTAMP (6), 	
	"FECHABAJA" TIMESTAMP (6), 	
	"NUMEROSERIE" VARCHAR2(256 CHAR),
	"INTEROPERABILIDAD" NUMBER(1,0)
   );
   ALTER TABLE "CORE_CLAVE_PRIVADA" ADD PRIMARY KEY ("ID");

   
--------------------------------------------------------
--  DDL for Table CORE_LOG
--------------------------------------------------------

  CREATE TABLE "CORE_LOG" 
   (	
	"ID" NUMBER(19,0), 
	"FECHA" TIMESTAMP (6), 
	"CRITICIDAD" VARCHAR2(10 CHAR), 
	"CLASE" VARCHAR2(256 CHAR), 
	"METODO" VARCHAR2(64 CHAR), 
	"MENSAJE" CLOB
   );
 --------------------------------------------------------
--  DDL for Table AUTORIZACION_APLICACION
--------------------------------------------------------

  CREATE TABLE "AUTORIZACION_APLICACION" 
   (	
	"IDAPLICACION" NUMBER(10,0), 
	"NIFCERTIFICADO" VARCHAR2(16 CHAR), 
	"NUMEROSERIE" VARCHAR2(64 CHAR), 
	"CN" VARCHAR2(512 CHAR), 
	"TIEMPOCOMPROBACION" TIMESTAMP (6), 
	"AUTORIDADCERTIF" VARCHAR2(512 CHAR), 
	"FECHAALTA" TIMESTAMP (6), 
	"FECHABAJA" TIMESTAMP (6)
   );
 
--------------------------------------------------------
--  DDL for Table AUTORIZACION_CA
--------------------------------------------------------

  CREATE TABLE "AUTORIZACION_CA" 
   (	
	"CODCA" VARCHAR2(512 CHAR), 
	"NOMBRE" VARCHAR2(256 CHAR)
   );
   
--------------------------------------------------------
--  DDL for Table AUTORIZACION_CERTIFICADO
--------------------------------------------------------

  CREATE TABLE "AUTORIZACION_CERTIFICADO" 
   (	
    "ID" NUMBER(19,0), 
	"IDCERTIFICADO" NUMBER(19,0),
	"IDAPLICACION" NUMBER(10,0),
	"IDORGANISMO" NUMBER(19,0),
	"FECHAALTA" TIMESTAMP (6),
	"FECHABAJA" TIMESTAMP (6)
   );
   
--------------------------------------------------------
--  DDL for Table AUTORIZACION_ORGANISMO
--------------------------------------------------------

  CREATE TABLE "AUTORIZACION_ORGANISMO" 
   (	
    "ID" NUMBER(19,0), 
	"IDORGANISMO" VARCHAR2(16 CHAR),  
	"FECHAALTA" TIMESTAMP (6), 
	"FECHABAJA" TIMESTAMP (6), 
	"NOMBREORGANISMO" VARCHAR2(64 CHAR)
   );
 
--------------------------------------------------------
--  DDL for Table EMISOR_BACKOFFICE
--------------------------------------------------------

  CREATE TABLE "EMISOR_BACKOFFICE" 
   (	
	"CERTIFICADO" NUMBER(19,0), 
	"BEANNAME" VARCHAR2(256 CHAR), 
	"CLASSNAME" VARCHAR2(256 CHAR), 
	"TER" NUMBER(10,0)
   );
 
--------------------------------------------------------
--  DDL for Table SCSP_SECUENCIA_IDTRANSMISION
--------------------------------------------------------

  CREATE TABLE "SCSP_SECUENCIA_IDTRANSMISION" 
   (	
	"PREFIJO" VARCHAR2(8 CHAR), 
	"SECUENCIA" VARCHAR2(26 CHAR), 
	"FECHAGENERACION" TIMESTAMP (6)
   );
   
--------------------------------------------------------
--  DDL for Table SCSP_CODIGO_ERROR_SECUNDARIO
--------------------------------------------------------

  CREATE TABLE "SCSP_CODIGO_ERROR_SECUNDARIO" 
   (	
	"CODIGOSECUNDARIO" VARCHAR2(4 CHAR), 
	"CODIGO" VARCHAR2(4 CHAR), 
	"DESCRIPCION" CLOB
   );
   

--------------------------------------------------------
--  Secuencia ID_LOG_SEQUENCE
--------------------------------------------------------

CREATE SEQUENCE ID_LOG_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE; 
CREATE SEQUENCE ID_CLAVE_PRIVADA_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_SERVICIO_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_EMISOR_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_CLAVE_PUBLICA_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_ORGANISMO_CESIONARIO_SEQ START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE; 
CREATE SEQUENCE ID_AUTORIZACION_ORGANISMO_SEQ START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE; 
CREATE SEQUENCE ID_AUTORIZACION_CERTIFIC_SEQ START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE; 
--------------------------------------------------------
--  Constraints for Table SCSP_CODIGO_ERROR
--------------------------------------------------------

  ALTER TABLE "SCSP_CODIGO_ERROR" MODIFY ("CODIGO" NOT NULL ENABLE);
  ALTER TABLE "SCSP_CODIGO_ERROR" MODIFY ("DESCRIPCION" NOT NULL ENABLE);
  ALTER TABLE "SCSP_CODIGO_ERROR" ADD PRIMARY KEY ("CODIGO");
--------------------------------------------------------
--  Constraints for Table CORE_TRANSMISION
--------------------------------------------------------

  ALTER TABLE "CORE_TRANSMISION" MODIFY ("IDSOLICITUD" NOT NULL ENABLE);
  ALTER TABLE "CORE_TRANSMISION" MODIFY ("IDPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_TRANSMISION" MODIFY ("IDSOLICITANTE" NOT NULL ENABLE);
  ALTER TABLE "CORE_TRANSMISION" ADD PRIMARY KEY ("IDSOLICITUD", "IDPETICION");
--------------------------------------------------------
--  Constraints for Table CORE_TIPO_MENSAJE
--------------------------------------------------------

  ALTER TABLE "CORE_TIPO_MENSAJE" MODIFY ("TIPO" NOT NULL ENABLE);
  ALTER TABLE "CORE_TIPO_MENSAJE" MODIFY ("DESCRIPCION" NOT NULL ENABLE);
  ALTER TABLE "CORE_TIPO_MENSAJE" ADD PRIMARY KEY ("TIPO");
--------------------------------------------------------
--  Constraints for Table CORE_CACHE_CERTIFICADOS
--------------------------------------------------------

  ALTER TABLE "CORE_CACHE_CERTIFICADOS" MODIFY ("NUMSERIE" NOT NULL ENABLE);
  ALTER TABLE "CORE_CACHE_CERTIFICADOS" MODIFY ("AUTORIDADCERTIF" NOT NULL ENABLE);
  ALTER TABLE "CORE_CACHE_CERTIFICADOS" MODIFY ("TIEMPOCOMPROBACION" NOT NULL ENABLE);
  ALTER TABLE "CORE_CACHE_CERTIFICADOS" MODIFY ("REVOCADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_CACHE_CERTIFICADOS" ADD PRIMARY KEY ("NUMSERIE", "AUTORIDADCERTIF");
--------------------------------------------------------
--  Constraints for Table SCSP_ESTADO_PETICION
--------------------------------------------------------

  ALTER TABLE "SCSP_ESTADO_PETICION" MODIFY ("CODIGO" NOT NULL ENABLE);
  ALTER TABLE "SCSP_ESTADO_PETICION" MODIFY ("MENSAJE" NOT NULL ENABLE);
  ALTER TABLE "SCSP_ESTADO_PETICION" ADD PRIMARY KEY ("CODIGO");
--------------------------------------------------------
--  Constraints for Table CORE_MODULO
--------------------------------------------------------

  ALTER TABLE "CORE_MODULO" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO" MODIFY ("ACTIVOENTRADA" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO" MODIFY ("ACTIVOSALIDA" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO" ADD PRIMARY KEY ("NOMBRE");
--------------------------------------------------------
--  Constraints for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

  ALTER TABLE "CORE_MODULO_CONFIGURACION" MODIFY ("MODULO" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO_CONFIGURACION" MODIFY ("CERTIFICADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO_CONFIGURACION" MODIFY ("ACTIVOENTRADA" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO_CONFIGURACION" MODIFY ("ACTIVOSALIDA" NOT NULL ENABLE);
  ALTER TABLE "CORE_MODULO_CONFIGURACION" ADD PRIMARY KEY ("MODULO", "CERTIFICADO");
--------------------------------------------------------
--  Constraints for Table CORE_CLAVE_PUBLICA
--------------------------------------------------------

  ALTER TABLE "CORE_CLAVE_PUBLICA" MODIFY ("ALIAS" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PUBLICA" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PUBLICA" MODIFY ("NUMEROSERIE" NOT NULL ENABLE); 
--------------------------------------------------------
--  Constraints for Table CORE_SECUENCIA_IDPETICION
--------------------------------------------------------

  ALTER TABLE "CORE_SECUENCIA_IDPETICION" MODIFY ("PREFIJO" NOT NULL ENABLE);
  ALTER TABLE "CORE_SECUENCIA_IDPETICION" MODIFY ("SECUENCIA" NOT NULL ENABLE);
  ALTER TABLE "CORE_SECUENCIA_IDPETICION" MODIFY ("FECHAGENERACION" NOT NULL ENABLE);
  ALTER TABLE "CORE_SECUENCIA_IDPETICION" ADD PRIMARY KEY ("PREFIJO");
--------------------------------------------------------
--  Constraints for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("IDPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("CERTIFICADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("ESTADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("FECHAPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("NUMEROENVIOS" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" MODIFY ("NUMEROTRANSMISIONES" NOT NULL ENABLE);
  ALTER TABLE "CORE_PETICION_RESPUESTA" ADD PRIMARY KEY ("IDPETICION");
--------------------------------------------------------
--  Constraints for Table CORE_TOKEN_DATA
--------------------------------------------------------

  ALTER TABLE "CORE_TOKEN_DATA" MODIFY ("IDPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_TOKEN_DATA" MODIFY ("TIPOMENSAJE" NOT NULL ENABLE);
  ALTER TABLE "CORE_TOKEN_DATA" MODIFY ("DATOS" NOT NULL ENABLE);
  ALTER TABLE "CORE_TOKEN_DATA" ADD PRIMARY KEY ("IDPETICION", "TIPOMENSAJE");
--------------------------------------------------------
--  Constraints for Table CORE_PARAMETRO_CONFIGURACION
--------------------------------------------------------

  ALTER TABLE "CORE_PARAMETRO_CONFIGURACION" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_PARAMETRO_CONFIGURACION" MODIFY ("VALOR" NOT NULL ENABLE);
  ALTER TABLE "CORE_PARAMETRO_CONFIGURACION" ADD PRIMARY KEY ("NOMBRE");
--------------------------------------------------------
--  Constraints for Table CORE_EMISOR_CERTIFICADO
--------------------------------------------------------

  ALTER TABLE "CORE_EMISOR_CERTIFICADO" MODIFY ("CIF" NOT NULL ENABLE);
  ALTER TABLE "CORE_EMISOR_CERTIFICADO" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_EMISOR_CERTIFICADO" ADD PRIMARY KEY ("ID");
--------------------------------------------------------
--  Constraints for Table CORE_SERVICIO
--------------------------------------------------------

  ALTER TABLE "CORE_SERVICIO" MODIFY ("CODCERTIFICADO" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("VERSIONESQUEMA" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("TIPOSEGURIDAD" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("CLAVEFIRMA" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("NUMEROMAXIMOREENVIOS" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("MAXSOLICITUDESPETICION" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("DESCRIPCION" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("EMISOR" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("FECHAALTA" NOT NULL ENABLE);
  ALTER TABLE "CORE_SERVICIO" MODIFY ("TIMEOUT" NOT NULL ENABLE); 
--------------------------------------------------------
--  Constraints for Table CORE_CLAVE_PRIVADA
--------------------------------------------------------

  ALTER TABLE "CORE_CLAVE_PRIVADA" MODIFY ("ALIAS" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PRIVADA" MODIFY ("NOMBRE" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PRIVADA" MODIFY ("PASSWORD" NOT NULL ENABLE);
  ALTER TABLE "CORE_CLAVE_PRIVADA" MODIFY ("NUMEROSERIE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table CORE_LOG
--------------------------------------------------------

  ALTER TABLE "CORE_LOG" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "CORE_LOG" ADD PRIMARY KEY ("ID");
--------------------------------------------------------
--  Constraints for Table AUTORIZACION_APLICACION
--------------------------------------------------------

  ALTER TABLE "AUTORIZACION_APLICACION" MODIFY ("IDAPLICACION" NOT NULL ENABLE);
  ALTER TABLE "AUTORIZACION_APLICACION" MODIFY ("NUMEROSERIE" NOT NULL ENABLE);
  ALTER TABLE "AUTORIZACION_APLICACION" MODIFY ("CN" NOT NULL ENABLE);
  ALTER TABLE "AUTORIZACION_APLICACION" ADD PRIMARY KEY ("IDAPLICACION");
--------------------------------------------------------
--  Constraints for Table AUTORIZACION_CA
--------------------------------------------------------

  ALTER TABLE "AUTORIZACION_CA" MODIFY ("CODCA" NOT NULL ENABLE);
  ALTER TABLE "AUTORIZACION_CA" ADD PRIMARY KEY ("CODCA");
--------------------------------------------------------
--  Constraints for Table AUTORIZACION_CERTIFICADO
--------------------------------------------------------

  ALTER TABLE "AUTORIZACION_CERTIFICADO" MODIFY ("IDCERTIFICADO" NOT NULL ENABLE);
  ALTER TABLE "AUTORIZACION_CERTIFICADO" MODIFY ("IDAPLICACION" NOT NULL ENABLE);
  ALTER TABLE "AUTORIZACION_CERTIFICADO" MODIFY ("IDORGANISMO" NOT NULL ENABLE);
  ALTER TABLE "AUTORIZACION_CERTIFICADO" MODIFY ("FECHAALTA" NOT NULL ENABLE);
  ALTER TABLE "AUTORIZACION_CERTIFICADO" ADD PRIMARY KEY ("ID");
  ALTER TABLE "AUTORIZACION_CERTIFICADO" ADD CONSTRAINT "unique_serv_apl_org_baja" UNIQUE ("IDCERTIFICADO", "IDAPLICACION", "IDORGANISMO","FECHABAJA");
  
--------------------------------------------------------
--  Constraints for Table AUTORIZACION_ORGANISMO
--------------------------------------------------------

  ALTER TABLE "AUTORIZACION_ORGANISMO" MODIFY ("IDORGANISMO" NOT NULL ENABLE); 
  ALTER TABLE "AUTORIZACION_ORGANISMO" MODIFY ("FECHAALTA" NOT NULL ENABLE); 
  ALTER TABLE "AUTORIZACION_ORGANISMO" ADD PRIMARY KEY ("ID");
  ALTER TABLE "AUTORIZACION_ORGANISMO" ADD CONSTRAINT "unique_id_org_baja" UNIQUE ( "IDORGANISMO","FECHABAJA");
 
--------------------------------------------------------
--  Constraints for Table EMISOR_BACKOFFICE
--------------------------------------------------------

  ALTER TABLE "EMISOR_BACKOFFICE" MODIFY ("CERTIFICADO" NOT NULL ENABLE);
  ALTER TABLE "EMISOR_BACKOFFICE" MODIFY ("TER" NOT NULL ENABLE);
  ALTER TABLE "EMISOR_BACKOFFICE" ADD PRIMARY KEY ("CERTIFICADO");
--------------------------------------------------------
--  Constraints for Table SCSP_SECUENCIA_IDTRANSMISION
--------------------------------------------------------

  ALTER TABLE "SCSP_SECUENCIA_IDTRANSMISION" MODIFY ("PREFIJO" NOT NULL ENABLE);
  ALTER TABLE "SCSP_SECUENCIA_IDTRANSMISION" MODIFY ("SECUENCIA" NOT NULL ENABLE);
  ALTER TABLE "SCSP_SECUENCIA_IDTRANSMISION" MODIFY ("FECHAGENERACION" NOT NULL ENABLE);
  ALTER TABLE "SCSP_SECUENCIA_IDTRANSMISION" ADD PRIMARY KEY ("PREFIJO");
--------------------------------------------------------
--  Constraints for Table SCSP_CODIGO_ERROR_SECUNDARIO
--------------------------------------------------------

  ALTER TABLE "SCSP_CODIGO_ERROR_SECUNDARIO" MODIFY ("CODIGOSECUNDARIO" NOT NULL ENABLE);
  ALTER TABLE "SCSP_CODIGO_ERROR_SECUNDARIO" MODIFY ("CODIGO" NOT NULL ENABLE);
  ALTER TABLE "SCSP_CODIGO_ERROR_SECUNDARIO" MODIFY ("DESCRIPCION" NOT NULL ENABLE);
  ALTER TABLE "SCSP_CODIGO_ERROR_SECUNDARIO" ADD PRIMARY KEY ("CODIGOSECUNDARIO", "CODIGO");
  
ALTER TABLE  CORE_CLAVE_PRIVADA ADD CONSTRAINT "clavefirma_org" FOREIGN KEY ("ORGANISMO")
	  REFERENCES  ORGANISMO_CESIONARIO ("ID") ENABLE;
	  
--------------------------------------------------------
--  Ref Constraints for Table CORE_TRANSMISION
--------------------------------------------------------

  ALTER TABLE "CORE_TRANSMISION" ADD CONSTRAINT "trans_peticion" FOREIGN KEY ("IDPETICION")
	  REFERENCES "CORE_PETICION_RESPUESTA" ("IDPETICION") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

  ALTER TABLE "CORE_MODULO_CONFIGURACION" ADD CONSTRAINT "mod_conf_servicio" FOREIGN KEY ("CERTIFICADO")
	  REFERENCES "CORE_SERVICIO" ("ID") ENABLE;
  ALTER TABLE "CORE_MODULO_CONFIGURACION" ADD CONSTRAINT "mod_conf_modulo" FOREIGN KEY ("MODULO")
	  REFERENCES "CORE_MODULO" ("NOMBRE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

  ALTER TABLE "CORE_PETICION_RESPUESTA" ADD CONSTRAINT "pet_resp_servicio" FOREIGN KEY ("CERTIFICADO")
	  REFERENCES "CORE_SERVICIO" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CORE_TOKEN_DATA
--------------------------------------------------------

  ALTER TABLE "CORE_TOKEN_DATA" ADD CONSTRAINT "toke_tipo" FOREIGN KEY ("TIPOMENSAJE")
	  REFERENCES "CORE_TIPO_MENSAJE" ("TIPO") ENABLE;
  ALTER TABLE "CORE_TOKEN_DATA" ADD CONSTRAINT "token_peticion" FOREIGN KEY ("IDPETICION")
	  REFERENCES "CORE_PETICION_RESPUESTA" ("IDPETICION") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CORE_SERVICIO
--------------------------------------------------------

  ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "serv_emisor" FOREIGN KEY ("EMISOR")
	  REFERENCES "CORE_EMISOR_CERTIFICADO" ("ID") ENABLE;
  ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "serv_clavecifrado" FOREIGN KEY ("CLAVECIFRADO")
	  REFERENCES "CORE_CLAVE_PUBLICA" ("ID") ENABLE;
  ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "serv_clavefirma" FOREIGN KEY ("CLAVEFIRMA")
	  REFERENCES "CORE_CLAVE_PRIVADA" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table AUTORIZACION_APLICACION
--------------------------------------------------------

  ALTER TABLE "AUTORIZACION_APLICACION" ADD CONSTRAINT "apli_codca" FOREIGN KEY ("AUTORIDADCERTIF")
	  REFERENCES "AUTORIZACION_CA" ("CODCA") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table AUTORIZACION_CERTIFICADO
--------------------------------------------------------

  ALTER TABLE "AUTORIZACION_CERTIFICADO" ADD CONSTRAINT "aut_cert_servicio" FOREIGN KEY ("IDCERTIFICADO")
	  REFERENCES "CORE_SERVICIO" ("ID") ENABLE;
 
  ALTER TABLE "AUTORIZACION_CERTIFICADO" ADD CONSTRAINT "aut_cert_aplicacion" FOREIGN KEY ("IDAPLICACION")
	  REFERENCES "AUTORIZACION_APLICACION" ("IDAPLICACION") ENABLE;

  ALTER TABLE "AUTORIZACION_CERTIFICADO" ADD CONSTRAINT "aut_cert_organismo" FOREIGN KEY ("IDORGANISMO")
	  REFERENCES "AUTORIZACION_ORGANISMO" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EMISOR_BACKOFFICE
--------------------------------------------------------

  ALTER TABLE "EMISOR_BACKOFFICE" ADD CONSTRAINT "emisor_bo_servicio" FOREIGN KEY ("CERTIFICADO")
	  REFERENCES "CORE_SERVICIO" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SCSP_CODIGO_ERROR_SECUNDARIO
--------------------------------------------------------

  ALTER TABLE "SCSP_CODIGO_ERROR_SECUNDARIO" ADD CONSTRAINT "error_sec_error" FOREIGN KEY ("CODIGO")
	  REFERENCES "SCSP_CODIGO_ERROR" ("CODIGO") ENABLE;
	  
--------------------------------------------------------
--  Comments for Table SCSP_CODIGO_ERROR
--------------------------------------------------------

   COMMENT ON COLUMN "SCSP_CODIGO_ERROR"."CODIGO" IS 'Codigo identificativo del error';
   COMMENT ON COLUMN "SCSP_CODIGO_ERROR"."DESCRIPCION" IS 'Litreral descriptivo del error';
   COMMENT ON TABLE "SCSP_CODIGO_ERROR"  IS 'Tabla que registraro los posibles errores genoricos que toda comunicacion SCSP puede generar';
--------------------------------------------------------
--  Comments for Table CORE_TRANSMISION
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_TRANSMISION"."IDSOLICITUD" IS 'Identificador de la solicitud de transmision dentro de las N posibles incluidas en una peticion';
   COMMENT ON COLUMN "CORE_TRANSMISION"."IDPETICION" IS 'Indentificador de la peticion en la que se incluyo la solicitud de transmision';
   COMMENT ON COLUMN "CORE_TRANSMISION"."IDTRANSMISION" IS 'Indentificador de la transmision que responde a la peticion de servicio de la Solicitud de Transmision identificada con idSolicitud';
   COMMENT ON COLUMN "CORE_TRANSMISION"."IDSOLICITANTE" IS 'CIF de organismo solicitante del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBRESOLICITANTE" IS 'Nombre del organismo solicitante de servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."DOCTITULAR" IS 'Documento identificativo del titular sobre el cual se esto realizando la peticion de servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBRETITULAR" IS 'Nombre del titular sobre el que se realiza la peticion del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."APELLIDO1TITULAR" IS 'Primer apellido del titular sobre el que se realiza la peticion del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."APELLIDO2TITULAR" IS 'Segundo apellido del titular sobre el que se realiza la peticion del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBRECOMPLETOTITULAR" IS 'Nombre completo del titular sobre el que se realiza la peticion del servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."DOCFUNCIONARIO" IS 'Documento identificativo del funcionario que genero la soliicitud de transmision';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBREFUNCIONARIO" IS 'Nombre del Funcionario que genero la solicitud de transmision';
   COMMENT ON COLUMN "CORE_TRANSMISION"."FECHAGENERACION" IS 'Fecha en la que se genero la transmision';
   COMMENT ON COLUMN "CORE_TRANSMISION"."UNIDADTRAMITADORA" IS 'Unidad Tramitadora asociada a la solicitud de transmision';
   COMMENT ON COLUMN "CORE_TRANSMISION"."CODIGOPROCEDIMIENTO" IS 'Codigo del procedimiento en base al cual se puede solicitar el servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."NOMBREPROCEDIMIENTO" IS 'Nombre del procedimiento en base al cual se puede solicitar el servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."EXPEDIENTE" IS 'Expediente asociado a la solicitud de transmsion';
   COMMENT ON COLUMN "CORE_TRANSMISION"."FINALIDAD" IS 'Finalidad por la cual se emitio la solicitud de transmision';
   COMMENT ON COLUMN "CORE_TRANSMISION"."CONSENTIMIENTO" IS 'Tipo de consentimiento asociado a la transmision. Debero tomar uno de los dos posibles valores:  - Si -Ley ';
   COMMENT ON COLUMN "CORE_TRANSMISION"."ESTADO" IS 'Estado concreto en el que se encuentra la transmision';
   COMMENT ON COLUMN "CORE_TRANSMISION"."ERROR" IS 'Descripcion del posible error encontrado al solicitar la transmision de servicio';
   COMMENT ON COLUMN "CORE_TRANSMISION"."XMLTRANSMISION" IS 'El XML de la transmision. Su almacenamiento sero opcional, dependiendo de un parametro global almacenado en la tabla core_parametro_configuracion';
   COMMENT ON TABLE "CORE_TRANSMISION"  IS 'Esta tabla almacenaro la informacion especofica de cada transmision que haya sido incluida en una respuesta de un servicio SCSP';
--------------------------------------------------------
--  Comments for Table CORE_TIPO_MENSAJE
-------------------------------------------------------- 

   COMMENT ON COLUMN "CORE_TIPO_MENSAJE"."TIPO" IS 'Tipo identificativo del mensaje';
   COMMENT ON COLUMN "CORE_TIPO_MENSAJE"."DESCRIPCION" IS 'Literal descriptivo del tipo de mensaje';
   COMMENT ON TABLE "CORE_TIPO_MENSAJE"  IS 'Tabla maestra que almacena los diferentes tipos de mensajes que pueden ser intercambiados a lo largo de un ciclo de comunicacion SCSP. Estos valores seron: o	Peticion o	Respuesta o	SolicitudRespuesta o	ConfirmacionPeticion o	Fault';
--------------------------------------------------------
--  Comments for Table CORE_CACHE_CERTIFICADOS
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."NUMSERIE" IS 'Nomero de serie del certificado';
   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."AUTORIDADCERTIF" IS 'Autoridad certificadora que emitio el certificado';
   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."TIEMPOCOMPROBACION" IS 'Fecha en la que se realizo el proceso de validacion del certificado por oltima vez'; 
   COMMENT ON COLUMN "CORE_CACHE_CERTIFICADOS"."REVOCADO" IS 'Valor booleano (o1o o o0o) que indicaro: -	o1o:   si el proceso de validacion revoco el certificado. -	o0o: si el proceso de validacion acepto el certificado';
   COMMENT ON TABLE "CORE_CACHE_CERTIFICADOS"  IS 'Tabla que registra el resultado de la validacion de los certificados   empleados en la firma de los mensajes recibidos por el requirente o por el emisor. Aso mismo se registra la fecha en la que se realizo dicha validacion para poder calcular el periodo de tiempo en la que dicha validacion esta vigente';
--------------------------------------------------------
--  Comments for Table SCSP_ESTADO_PETICION
--------------------------------------------------------

   COMMENT ON COLUMN "SCSP_ESTADO_PETICION"."CODIGO" IS 'Codigo identificativo del estado';
   COMMENT ON COLUMN "SCSP_ESTADO_PETICION"."MENSAJE" IS 'Literal descriptivo del estado';
   COMMENT ON TABLE "SCSP_ESTADO_PETICION"  IS 'Tabla que almacena los posibles estados en los que se puede encontrar una peticion SCSP. Sus posibles valores seron: o	0001 - Pendiente o	0002 - En proceso o	0003 - Tramitada';
--------------------------------------------------------
--  Comments for Table CORE_MODULO
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_MODULO"."NOMBRE" IS 'Nombre del modulo';
   COMMENT ON COLUMN "CORE_MODULO"."DESCRIPCION" IS 'Literal descriptivo del modulo a configurar su activacion';
   COMMENT ON COLUMN "CORE_MODULO"."ACTIVOENTRADA" IS 'Valor que indica si el modulo esta activo en la emision de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo)';
   COMMENT ON COLUMN "CORE_MODULO"."ACTIVOSALIDA" IS 'Valor que indica si el modulo esta activo en la recepcion de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo)';
   COMMENT ON TABLE "CORE_MODULO"  IS 'Tabla que almacena la configuracion de activacion  o desactivacion de los modulos que componen el ciclo de ejecucion de las libreroas SCSP';
--------------------------------------------------------
--  Comments for Table CORE_MODULO_CONFIGURACION
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."MODULO" IS 'Nombre del modulo a configurar';
   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."CERTIFICADO" IS 'Codigo del certificado solicitado';
   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."ACTIVOENTRADA" IS 'Valor que indica si el modulo esta activo en la emision de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo)';
   COMMENT ON COLUMN "CORE_MODULO_CONFIGURACION"."ACTIVOSALIDA" IS 'Valor que indica si el modulo esta activo en la recepcion de mensajes. Puede tomar valor 1 (Activo) o 0 (Inactivo)';
   COMMENT ON TABLE "CORE_MODULO_CONFIGURACION"  IS 'Tabla que permite la sobreescritura de la configuracion de activacion de un modulo para un servicio concreto';
--------------------------------------------------------
--  Comments for Table CORE_CLAVE_PUBLICA
--------------------------------------------------------
 
   COMMENT ON COLUMN "CORE_CLAVE_PUBLICA"."ALIAS" IS 'Alias que identifica de manera unovoca a la clave poblica dentro del almacon de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
   COMMENT ON COLUMN "CORE_CLAVE_PUBLICA"."NOMBRE" IS 'Nombre descriptivo de la clave poblica';
   COMMENT ON COLUMN "CORE_CLAVE_PUBLICA"."NUMEROSERIE" IS 'Numero de serie de la clave publica';
   COMMENT ON TABLE "CORE_CLAVE_PUBLICA"  IS 'Esta tabla almacenaro los datos de configuracion necesarios para acceder a las claves poblicas disponibles en el almacon de certificados configurado. Las claves poblicas  aquo configuradas seron utilizadas para el posible cifrado de los  mensajes emitidos';
--------------------------------------------------------
--  Comments for Table CORE_SECUENCIA_IDPETICION
-------------------------------------------------------- 

   COMMENT ON COLUMN "CORE_SECUENCIA_IDPETICION"."PREFIJO" IS 'Prefijo utilizado para la construccion de los identificadores. Dicho valor podro ser el prefijo especificado a ser utilizado para cada servicio o ante la no existencia del mismo, el nomero de serie del certificado digital firmante  de los mensajes';
   COMMENT ON COLUMN "CORE_SECUENCIA_IDPETICION"."SECUENCIA" IS 'Valor secuencial que sero concatenado al prefijo para generar los identificadores de peticion. Este secuencial sero de tipo alfanumerico, de tal forma que el siguiente valor a 00000009 seroa 0000000A';
   COMMENT ON COLUMN "CORE_SECUENCIA_IDPETICION"."FECHAGENERACION" IS 'Fecha en la que se registro  el secuencial';
   COMMENT ON TABLE "CORE_SECUENCIA_IDPETICION"  IS 'Tabla que almacena las semillas (secuenciales) utilizadas para la generacion de los identificadores de peticion. Existiro un secuencial asociado a cada posible prefijo o nomero de serie de certificado digital firmante';
--------------------------------------------------------
--  Comments for Table CORE_PETICION_RESPUESTA
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."IDPETICION" IS 'Identificador unovoco de la peticion de servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."CERTIFICADO" IS 'Codigo del certificado que se solicita en la peticion';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."ESTADO" IS 'Codigo identificativo del estado de la peticion. Tomaro sus valores de las tablas scsp_estado_peticion y scsp_codigo_error';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."FECHAPETICION" IS 'Timestamp que indica la fecha en la que se genero la peticion';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."FECHARESPUESTA" IS 'Timestamp que indica la fecha en la cual se recibio la respuesta a nuestra peticion'; 
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."TER" IS 'Timestamp que indica la fecha a partir de la cual una peticion de tipo asoncrono podro solicitar una respuesta definitiva al servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."ERROR" IS 'Mensaje descriptivo del error, si lo hubiera, en la solicitud del servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."NUMEROENVIOS" IS 'Valor que indica el nomero de veces que se ha reenviado una peticion al servicio';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."NUMEROTRANSMISIONES" IS 'Nomero de Solicitudes de Transmision que se enviaron en la peticion';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."FECHAULTIMOSONDEO" IS 'Timestamp que indica la fecha del oltimo reenvio de una peticion de tipo asincrono';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."TRANSMISIONSINCRONA" IS 'Valor binario que indica si la peticion fue solicitada a un servicio de tipo soncrono o asincrono. Podro tomar los valores: 0: La comunicacion fue de tipo asoncrono, 1: La comunicacion fue de tipo soncrono';
   COMMENT ON COLUMN "CORE_PETICION_RESPUESTA"."DESCOMPUESTA" IS 'Caracter que indica el estado del procesamiento de las transmisiones de la respuesta recibida. Podro tomar los siguientes valores:    - S: Se ha procesado correctamente la respuesta, habiendo obtenido todas las transmisiones en ella incluidas y registradas en la tabla core_transmision. - N: No ha sido procesadas las transmisiones de la respuesta. - E: La respuesta termino correctamente  (estado 0003), pero se ha producido un error al procesar sus trasmisiones';
   COMMENT ON TABLE "CORE_PETICION_RESPUESTA"  IS 'Esta tabla registraro un historico de las peticiones y respuestas intercambiadas entre los requirentes y los emisores de servicios';
--------------------------------------------------------
--  Comments for Table CORE_TOKEN_DATA
--------------------------------------------------------
 
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."IDPETICION" IS 'Identificador de la peticion a la cual esto asociado el XML';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."TIPOMENSAJE" IS 'Tipo de mensaje almacenado, podro ser : -Peticion -Respuesta -SolicitudRespuesta -ConfirmacionPeticion -Fault';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."DATOS" IS 'Bytes del mensaje almacenado';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."CLAVE" IS 'Clave simotrica utilizada para el cifrado de los datos';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."MODOENCRIPTACION" IS 'Modo de encriptacion utilizado para el proceso de cifrado.  Por defecto sero TransportKey';
   COMMENT ON COLUMN "CORE_TOKEN_DATA"."ALGORITMOENCRIPTACION" IS 'Algoritmo empleado en la encriptacion del mensaje. Podro tomar los siguientes valores: - AES128 - AES256 -DESDe';
   COMMENT ON TABLE "CORE_TOKEN_DATA"  IS 'Esta tabla almacenaro el contenido de los mensajes intercambiados en un proceso de comunicacion SCSP';
--------------------------------------------------------
--  Comments for Table CORE_PARAMETRO_CONFIGURACION
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_PARAMETRO_CONFIGURACION"."NOMBRE" IS 'Nombre identificativo del parometro';
   COMMENT ON COLUMN "CORE_PARAMETRO_CONFIGURACION"."VALOR" IS 'Valor del parometro';
   COMMENT ON COLUMN "CORE_PARAMETRO_CONFIGURACION"."DESCRIPCION" IS 'Literal descriptivo de la utilidad del parometro';
   COMMENT ON TABLE "CORE_PARAMETRO_CONFIGURACION"  IS 'Tabla  que almacena aquellos parometros de configuracion que son globales a todos los servicios y entornos para un mismo cliente integrador de las librerias SCSP';
--------------------------------------------------------
--  Comments for Table CORE_EMISOR_CERTIFICADO
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_EMISOR_CERTIFICADO"."CIF" IS 'CIF identificativo del organismo emisor de servicios';
   COMMENT ON COLUMN "CORE_EMISOR_CERTIFICADO"."NOMBRE" IS 'Nombre descriptivo del organismo emisor de servicios';
   COMMENT ON COLUMN "CORE_EMISOR_CERTIFICADO"."FECHABAJA" IS 'Fecha de Baja de emisores de certificados';
   COMMENT ON TABLE "CORE_EMISOR_CERTIFICADO"  IS 'Esta tabla almacenaro los diferentes emisores de servicio configurados';
--------------------------------------------------------
--  Comments for Table CORE_SERVICIO
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_SERVICIO"."CODCERTIFICADO" IS 'Codigo del certificado que lo identifica unovocamente en las comunicaciones SCSP';
   COMMENT ON COLUMN "CORE_SERVICIO"."URLSINCRONA" IS 'Endpoint de acceso al servicio de tipo soncrono';
   COMMENT ON COLUMN "CORE_SERVICIO"."URLASINCRONA" IS 'Endpoint de acceso al servicio de tipo asoncrono del certificado';
   COMMENT ON COLUMN "CORE_SERVICIO"."ACTIONSINCRONA" IS 'Valor del Soap:Action de la peticion soncrona utilizado por el servidor WS en el caso de que sea necesario';
   COMMENT ON COLUMN "CORE_SERVICIO"."ACTIONASINCRONA" IS 'Valor del Soap:Action de la peticion asoncrona utilizado por el servidor WS en el caso de que sea necesario';
   COMMENT ON COLUMN "CORE_SERVICIO"."ACTIONSOLICITUD" IS 'Valor del Soap:Action de la solicitud asoncrona de respuesta utilizado por el servidor WS en el caso de que sea necesario';
   COMMENT ON COLUMN "CORE_SERVICIO"."VERSIONESQUEMA" IS 'Indica la version de esquema utilizado en los mensajes SCSP pudiendo tomar los valores V2 y V3 (existe la posibilidad de aoadir otros valores utilizando configuraciones avandadas de binding)';
   COMMENT ON COLUMN "CORE_SERVICIO"."TIPOSEGURIDAD" IS 'Indica la politica de seguridad utilizada en la securizacion de los mensajes, pudiendo tomar los valores [XMLSignature| WS-Security]';
   COMMENT ON COLUMN "CORE_SERVICIO"."PREFIJOPETICION" IS 'Literal con una longitud moxima de 8 caracteres, el cual sero utilizado para la construccion de los identificadores de peticion, anteponiendose a un valor secuencial. Mediante este literal pueden personalizarse los identificadores de peticion haciendolos mos descriptivos';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHCIFRADOSINCRONO" IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresion xpath que permite localizar  al nodo en el mensaje XML, presentaro el siguiente formato //*[local-name()=oNODO_A_CIFRARo], donde oNODO_A_CIFRARo es el local name del nodo que se desea cifrar. Este nodo sero empleado en el caso de realizar comunicaciones soncronas';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHCIFRADOASINCRONO" IS 'Literal que identifica el nodo del mensaje XML a cifrar en caso de que los mensajes intercambiados con el emisor de servicio viajasen cifrados. Se corresponde con una expresion xpath que permite localizar  al nodo en el mensaje XML, presentaro el siguiente formato //*[local-name()=oNODO_A_CIFRARo], donde oNODO_A_CIFRARo es el local name del nodo que se desea cifrar. Este nodo sero empleado en el caso de realizar comunicaciones asoncronas';
   COMMENT ON COLUMN "CORE_SERVICIO"."ESQUEMAS" IS 'Ruta que indica el directorio donde se encuentran los esquemas (*.xsd) con el que se validaro el XML de los diferentes mensajes intercambiados. Esta ruta podro tomar un valor relativo haciendo referencia al classpath de la aplicacion o  un path absoluto';
   COMMENT ON COLUMN "CORE_SERVICIO"."CLAVEFIRMA" IS 'Alias,  que identifica univocamente en el almacon de certificados configurado, la clave privada con la que se firmaron los mensajes';
   COMMENT ON COLUMN "CORE_SERVICIO"."CLAVECIFRADO" IS 'Alias,  que identifica univocamente en el almacon de certificados configurado, el certificado de clave poblica con el que se cifraroan los mensajes enviados al emisor de servicios, en el caso de que se desease encriptacion';
   COMMENT ON COLUMN "CORE_SERVICIO"."ALGORITMOCIFRADO" IS 'Literal que identifica el algoritmo utilizado para el cifrado de los mensajes enviados al emisor. Debe poseer un valor reconocido por las libreroas de Rampart: - Basic128Rsa15 - TripleDesRsa15  - Basic256Rsa15';
   COMMENT ON COLUMN "CORE_SERVICIO"."NUMEROMAXIMOREENVIOS" IS 'Valor que indica en el caso del requirente, el nomero moximo de reenvios que pueden llevarse a cabo sobre una peticion asoncrona. En el caso del emisor, hace referencia al nomero moximo de veces que procesaro y crearo una respuesta para una peticion con un mismo identificador';
   COMMENT ON COLUMN "CORE_SERVICIO"."MAXSOLICITUDESPETICION" IS 'Nomero moximo de solicitudes de transmision que se van a permitir por peticion';
   COMMENT ON COLUMN "CORE_SERVICIO"."PREFIJOIDTRANSMISION" IS 'Semilla empleada por el emisor para generar los identificadores de transmision de las respuestas. Sero un valor alfanumorico con un monimo de 3 caracteres y un moximo de 8';
   COMMENT ON COLUMN "CORE_SERVICIO"."DESCRIPCION" IS 'Literal descriptivo del servicio a solicitar utilizando el codigo de certificado';
   COMMENT ON COLUMN "CORE_SERVICIO"."EMISOR" IS 'Nombre del emisor del certificado';
   COMMENT ON COLUMN "CORE_SERVICIO"."FECHAALTA" IS 'Timestamp con la fecha en la cual el certificado se dio de alta en el sistema y por lo tanto a partir de la cual se podron emitir peticiones al mismo';
   COMMENT ON COLUMN "CORE_SERVICIO"."FECHABAJA" IS 'Timestamp con la fecha en la cual el certificado se dio de baja en el sistema y por tanto a partir de la cual no se podron emitir peticiones al mismo';
   COMMENT ON COLUMN "CORE_SERVICIO"."CADUCIDAD" IS 'Nomero de dias que deberon sumarse a la fecharespuesta de una peticion, para calcular la fecha a partir de la cual se podro considerar que la respuesta esta caducada y se devolvera el error scsp correspondiente para indicar que la respuesta ha perdido su valor';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHLITERALERROR" IS 'Xpath para recuperar el literal del error de los datos especoficos';
   COMMENT ON COLUMN "CORE_SERVICIO"."XPATHCODIGOERROR" IS 'Xpath para recuperar el codigo de error de los datos especoficos';
   COMMENT ON COLUMN "CORE_SERVICIO"."TIMEOUT" IS 'Timeout que se establecero para el envoo de las peticiones a los servicios';
   COMMENT ON COLUMN "CORE_SERVICIO"."VALIDACIONFIRMA" IS 'Parometro que indica si se admite otro tipo de firma en el servicio ademos del configurado';
   COMMENT ON TABLE "CORE_SERVICIO"  IS 'Esta tabla registraro cada uno de los certificados SCSP que van a ser utilizados por el sistema tanto de la parte requirente como de la parte emisora';
--------------------------------------------------------
--  Comments for Table CORE_CLAVE_PRIVADA
--------------------------------------------------------

   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."ALIAS" IS 'Alias que identifica de manera unovoca a la clave privada dentro del almacon de certificados que haya sido configurado en la tabla core_parametro_configuracion bajo el nombre keystoreFile';
   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."NOMBRE" IS 'Nombre descriptivo de la clave privada';
   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."PASSWORD" IS 'Password de la clave privada necesaria para hacer uso de la misma';
   COMMENT ON COLUMN "CORE_CLAVE_PRIVADA"."NUMEROSERIE" IS 'Numero de serie de la clave privada';
   COMMENT ON TABLE "CORE_CLAVE_PRIVADA"  IS 'Esta tabla almacenaro los datos de configuracion necesarios para acceder a las claves privadas disponibles en el almacon de certificados configurado. Las claves privadas aquo configuradas seron utilizadas para la firma de los mensajes emitidos';
--------------------------------------------------------
--  Comments for Table CORE_LOG
-------------------------------------------------------- 

   COMMENT ON COLUMN "CORE_LOG"."ID" IS 'Valor incremental autogenerado';
   COMMENT ON COLUMN "CORE_LOG"."FECHA" IS 'Fecha  en la que se genero la traza de log';
   COMMENT ON COLUMN "CORE_LOG"."CRITICIDAD" IS 'Tipo de nivel de la traza de log (WARN,INFO,DEBUG,ALL,ERROR)';
   COMMENT ON COLUMN "CORE_LOG"."CLASE" IS 'Clase que genero la traza de log registrada';
   COMMENT ON COLUMN "CORE_LOG"."METODO" IS 'Motodo especofico de la clase que genero la traza de log';
   COMMENT ON COLUMN "CORE_LOG"."MENSAJE" IS 'Literal descriptivo del error almacenado';
   COMMENT ON TABLE "CORE_LOG"  IS 'Tabla de configurada en el appender log4j para base de datos de la aplicacion, donde se registraron las posibles trazas de error';
   
   --------------------------------------------------------
--  Comments for Table AUTORIZACION_APLICACION
--------------------------------------------------------

   COMMENT ON COLUMN "AUTORIZACION_APLICACION"."IDAPLICACION" IS 'Identificador de la aplicacion. Se va a generar automoticamente';
   COMMENT ON COLUMN "AUTORIZACION_APLICACION"."NIFCERTIFICADO" IS 'Nif del certificado con el que la aplicacion firmara las peticiones';
   COMMENT ON COLUMN "AUTORIZACION_APLICACION"."NUMEROSERIE" IS 'Numero de serie del certificado con el que la aplicacion firmara las peticiones';
   COMMENT ON COLUMN "AUTORIZACION_APLICACION"."CN" IS 'Common Name del certificado con el que la aplicacion firmara las peticiones';
   COMMENT ON COLUMN "AUTORIZACION_APLICACION"."TIEMPOCOMPROBACION" IS 'Fecha en que se realizo la operacion de validar el certificado';
   COMMENT ON COLUMN "AUTORIZACION_APLICACION"."AUTORIDADCERTIF" IS 'Autoridad de certificacion del certificado';
   COMMENT ON COLUMN "AUTORIZACION_APLICACION"."FECHAALTA" IS 'Fecha en la que se da de alta la aplicacion';
   COMMENT ON COLUMN "AUTORIZACION_APLICACION"."FECHABAJA" IS 'Fecha a partir de la cual la aplicacion no va a poder acceder a ningon servicio';
   COMMENT ON TABLE "AUTORIZACION_APLICACION"  IS 'Aplicaciones dadas de alta en el emisor que van a emplear los organismos para poder realizar consultas a los servicios ofrecidos por este';
--------------------------------------------------------
--  Comments for Table AUTORIZACION_CA
--------------------------------------------------------

   COMMENT ON COLUMN "AUTORIZACION_CA"."CODCA" IS 'Codigo de la autoridad de certificacion (Ej: FNMT)';
   COMMENT ON COLUMN "AUTORIZACION_CA"."NOMBRE" IS 'Nombre de la autoridad de certificacion  (Ej: Fabrica Nacional de Moneda y Timbre)';
   COMMENT ON TABLE "AUTORIZACION_CA"  IS 'Contiene la informacion relativa a las distintas autoridades de certificacion reconocidas por la aplicacion';
--------------------------------------------------------
--  Comments for Table AUTORIZACION_CERTIFICADO
--------------------------------------------------------

   COMMENT ON COLUMN "AUTORIZACION_CERTIFICADO"."IDCERTIFICADO" IS 'Codigo del certificado autorizado';
   COMMENT ON COLUMN "AUTORIZACION_CERTIFICADO"."IDAPLICACION" IS 'Identificador de la aplicacion autorizada';
   COMMENT ON COLUMN "AUTORIZACION_CERTIFICADO"."IDORGANISMO" IS 'Identificador del organismo';
   COMMENT ON COLUMN "AUTORIZACION_CERTIFICADO"."FECHAALTA" IS 'Fecha de alta de la autorizacion';
   COMMENT ON COLUMN "AUTORIZACION_CERTIFICADO"."FECHABAJA" IS 'Fecha de baja de la autorizacion';
   COMMENT ON TABLE "AUTORIZACION_CERTIFICADO"  IS 'Contiene la informacion relativa a las autorizaciones de los distintas aplicaciones para acceder a los servicios ofrecidos por el emisor';
--------------------------------------------------------
--  Comments for Table AUTORIZACION_ORGANISMO
-------------------------------------------------------- 

   COMMENT ON COLUMN "AUTORIZACION_ORGANISMO"."IDORGANISMO" IS 'Identificador del organismo';
   COMMENT ON COLUMN "AUTORIZACION_ORGANISMO"."FECHAALTA" IS 'Fecha en la que se da de alta el organismo';
   COMMENT ON COLUMN "AUTORIZACION_ORGANISMO"."FECHABAJA" IS 'Fecha a partir de la cual el organismo no va a poder enviar peticiones';
   COMMENT ON COLUMN "AUTORIZACION_ORGANISMO"."NOMBREORGANISMO" IS 'Nombre descriptivo del organismo requirente de servicios';
   COMMENT ON TABLE "AUTORIZACION_ORGANISMO"  IS 'Organismos que eston dados de alta en el emisor y que van a poder consultar los servicios ofrecidos por este a travos de alguna de las aplicaciones que tienen autorizacion para consultar los servicios';
--------------------------------------------------------
--  Comments for Table EMISOR_BACKOFFICE
-------------------------------------------------------- 

   COMMENT ON COLUMN "EMISOR_BACKOFFICE"."CERTIFICADO" IS 'Codigo del certificado gestionado por el backoffice';
   COMMENT ON COLUMN "EMISOR_BACKOFFICE"."BEANNAME" IS 'Identificador del bean del contexto de Spring que contiene la clase que ofrece la puerta de entrada al backoffice';
   COMMENT ON COLUMN "EMISOR_BACKOFFICE"."CLASSNAME" IS 'Nombre completo  de la clase que ofrece la puerta de entrada al backoffice';
   COMMENT ON COLUMN "EMISOR_BACKOFFICE"."TER" IS 'Nomero de horas que haron esperar a un requirente para la generacion de una respuesta definitiva ante una peticion asoncrona';
   COMMENT ON TABLE "EMISOR_BACKOFFICE"  IS 'Tabla en la que se configura el modulo de backoffice del emisor que enlaza las comunicaciones SCSP con la logica de negocio especifica de cada organismo';
--------------------------------------------------------
--  Comments for Table SCSP_SECUENCIA_IDTRANSMISION
--------------------------------------------------------

   COMMENT ON COLUMN "SCSP_SECUENCIA_IDTRANSMISION"."PREFIJO" IS 'Prefijo de IdTransmisiones al que estaro asociada la secuencia';
   COMMENT ON COLUMN "SCSP_SECUENCIA_IDTRANSMISION"."SECUENCIA" IS 'Valor actual de la secuencia alfanumorica asociada al prefijo';
   COMMENT ON COLUMN "SCSP_SECUENCIA_IDTRANSMISION"."FECHAGENERACION" IS 'oltima fecha en la que se ha generado un valor de secuencia';
   COMMENT ON TABLE "SCSP_SECUENCIA_IDTRANSMISION"  IS 'Tabla utilizada para la generacion de los valores de los nodos IdTransmision en los emisores';
   
--------------------------------------------------------
--  Comments for Table SCSP_CODIGO_ERROR_SECUNDARIO
--------------------------------------------------------

   COMMENT ON COLUMN "SCSP_CODIGO_ERROR_SECUNDARIO"."CODIGOSECUNDARIO" IS 'Codigo de error SCSP al que esta asociado el error secundario';
   COMMENT ON COLUMN "SCSP_CODIGO_ERROR_SECUNDARIO"."CODIGO" IS 'Codigo identificativo del error especifico secundario';
   COMMENT ON COLUMN "SCSP_CODIGO_ERROR_SECUNDARIO"."DESCRIPCION" IS 'Literal descriptivo del mensaje de error secundario';
   COMMENT ON TABLE "SCSP_CODIGO_ERROR_SECUNDARIO"  IS 'Tabla de errores que permitiro almacenar aquellos mensajes de excepcion gestionados por cada logica de negocio especofica de cada emisor, y que estro asociado a un error genorico de SCSP';
	  
--------------------------------------------------------
--  Procedimiento Almacenado GETSECUENCIAIDTRANSMISION
--  Creacion del procedimiento almacenado para generar las secuencias del id de transmision
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE "GETSECUENCIAIDTRANSMISION"  (  prefijo_param in varchar2, on_Secuencial out number)as  rRegistro ROWID;

begin  
    select ROWID, SECUENCIA+1 into rRegistro, on_Secuencial from SCSP_SECUENCIA_IDTRANSMISION where PREFIJO = prefijo_param for update;
    update SCSP_SECUENCIA_IDTRANSMISION set SECUENCIA = on_Secuencial, FECHAGENERACION=sysdate where rowid = rRegistro;  
	commit; 
    exception when no_data_found then on_Secuencial := 1;    
    insert into SCSP_SECUENCIA_IDTRANSMISION (PREFIJO, SECUENCIA,FECHAGENERACION) values (prefijo_param, on_Secuencial,(SELECT SYSDATE FROM DUAL)); 
commit; 
end;
/