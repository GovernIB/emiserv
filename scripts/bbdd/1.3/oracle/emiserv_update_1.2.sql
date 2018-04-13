--
-- Aquest script està agafat dels scripts d'actualització proporcionat pel MINHAP i ja l'hem hagut de
-- revisar per algunes errades i mancances detectades (moltes).

/* UpdateEmisor-3.3.0a3.3.1 */
INSERT INTO CORE_PARAMETRO_CONFIGURACION (NOMBRE,VALOR,DESCRIPCION) VALUES ('validate.nif.emisor.enabled','true','Flag que indica si se valida el valor del nodo <NifEmisor> de la petición');

/* UpdateEmisor-3.3.1a3.4.0 */
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values  ('0253','No todas las solicitudes tienen el mismo identificador de solicitante');
/*CREATE SEQUENCE ID_CLAVE_PRIVADA_SEQUENCE  START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_CLAVE_PUBLIC_SEQUENCE  START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_ORGANISMO_CESIONARIO_SEQ START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_SERVICIO_CESIONARIO_SEQ START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;*/

BEGIN
  FOR c IN
  (SELECT c.owner, c.table_name, c.constraint_name
   FROM user_constraints c, user_tables t
   WHERE c.table_name = t.table_name 
   and (c.table_name='CORE_SERVICIO'  )
   and (c.constraint_type = 'R'  )
   ORDER BY c.constraint_type DESC)
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" DROP constraint ' || c.constraint_name);
  END LOOP;
END;
/

/*ALTER TABLE core_clave_privada DROP PRIMARY KEY;
ALTER TABLE core_clave_publica DROP PRIMARY KEY;
commit;
ALTER TABLE core_clave_privada ADD id NUMBER(19,0) ;
ALTER TABLE core_clave_publica ADD id NUMBER(19,0) ;
alter table core_clave_privada add fechaBaja  TIMESTAMP DEFAULT NULL;
alter table core_clave_privada add fechaAlta  TIMESTAMP DEFAULT NULL;
alter table core_clave_publica add fechaBaja  TIMESTAMP DEFAULT NULL;
alter table core_clave_publica add fechaAlta  TIMESTAMP DEFAULT NULL;
commit;
UPDATE   core_clave_privada SET fechaAlta  = to_date(sysdate, 'yyyy/mm/dd:hh:mi'); 
UPDATE   core_clave_publica SET fechaAlta  = to_date(sysdate, 'yyyy/mm/dd:hh:mi'); 
alter table  core_servicio  ADD id_clave_firma  NUMBER(19,0);
alter table  core_servicio  ADD id_clave_cifrado  NUMBER(19,0);
commit;
update   core_clave_privada SET ID = ( ID_CLAVE_PRIVADA_SEQUENCE.nextval);
update   core_clave_publica SET ID = ( ID_CLAVE_PUBLIC_SEQUENCE.nextval);
commit;
ALTER TABLE core_clave_privada   ADD    PRIMARY KEY (id);
ALTER TABLE  core_clave_publica  ADD  PRIMARY KEY (id);
UPDATE core_servicio s
   SET  id_clave_firma  = (SELECT c.id FROM core_clave_privada c   WHERE  c.alias =  s.clavefirma)
    WHERE EXISTS (   SELECT 1     FROM core_clave_privada c   WHERE c.alias =  s.clavefirma);
UPDATE core_servicio s
   SET  id_clave_cifrado  = (SELECT c.id FROM core_clave_publica c   WHERE  c.alias =  s.clavecifrado)
    WHERE EXISTS (   SELECT 1     FROM core_clave_publica c   WHERE c.alias =  s.clavecifrado);
commit;	
alter table  core_servicio  DROP   column    clavefirma;
alter table  core_servicio  DROP   column    clavecifrado;
commit; 
alter table  core_servicio  RENAME   column   id_clave_firma TO    clavefirma; 
alter table  core_servicio  RENAME   column   id_clave_cifrado TO    clavecifrado;
commit;*/

CREATE SEQUENCE ID_EMISOR_SEQUENCE  START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
alter table core_servicio ADD id_emisor  NUMBER(19,0);
ALTER TABLE core_emisor_certificado ADD id NUMBER(19,0);
update core_emisor_certificado SET ID = ( ID_EMISOR_SEQUENCE.nextval);
alter table core_emisor_certificado DROP PRIMARY KEY;
alter table core_emisor_certificado   ADD    PRIMARY KEY (id);
UPDATE core_servicio s
   SET  id_emisor  = (SELECT e.id FROM core_emisor_certificado e   WHERE  e.cif =  s.emisor)
    WHERE EXISTS (   SELECT 1     FROM core_emisor_certificado e   WHERE e.cif =  s.emisor);
alter table  core_servicio  DROP   column    emisor;
alter table  core_servicio  RENAME   column   id_emisor TO    emisor; 

/*  ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "serv_emisor" FOREIGN KEY ("EMISOR")
	  REFERENCES "CORE_EMISOR_CERTIFICADO" ("ID") ENABLE;
  ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "SERV_CLAVECIFRADO" FOREIGN KEY ("CLAVECIFRADO")
	  REFERENCES "CORE_CLAVE_PUBLICA" ("ID") ENABLE;
  ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "SERV_CLAVEFIRMA" FOREIGN KEY ("CLAVEFIRMA")
	  REFERENCES "CORE_CLAVE_PRIVADA" ("ID") ENABLE;*/
update core_parametro_configuracion set valor='3.4.0' where nombre='version.datamodel.scsp';
commit; 

/* UpdateEmisor-3.4.1a3.5.0 */
--------------
-- Si no posee específicado ningún prefijo para la generación de identificadores de petición, se deberá 
-- especificar uno del cual se tenga constancia que es unívoco.
-- A partir de la versión 3.5.0 será obligatorio especificar o un prefijo global genérico o uno por cada 
-- uno de los servicios a consumir.
--------------
CREATE SEQUENCE ID_CLAVE_PRIVADA_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_CLAVE_PUBLICA_SEQUENCE START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
CREATE SEQUENCE ID_ORGANISMO_CESIONARIO_SEQ START WITH     1 INCREMENT BY   1 NOCACHE NOCYCLE;
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
--ALTER TABLE  core_servicio  DROP CONSTRAINT  SERV_CLAVEFIRMA  ;
--ALTER TABLE  core_servicio  DROP CONSTRAINT  SERV_CLAVECIFRADO  ;

BEGIN
  FOR c IN
  (SELECT c.owner, c.table_name, c.constraint_name, c.constraint_type
   FROM user_constraints c, user_tables t
   WHERE c.table_name = t.table_name
  and (c.table_name='CORE_CLAVE_PRIVADA' or c.table_name='CORE_CLAVE_PUBLICA' ) AND c.constraint_type='P'
   ORDER BY c.constraint_type)
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" drop constraint "' || c.constraint_name||'"');
  END LOOP;
END;
/

BEGIN
  FOR c IN
  (select   c.owner, c.table_name, c.constraint_name   from   user_tab_columns t,user_cons_columns c where t.table_name = 'CORE_SERVICIO' and   t.table_name = c.table_name and   t.column_name = c.column_name and   t.nullable = 'N' and t.column_name='CLAVEFIRMA')
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" drop constraint "' || c.constraint_name||'"');
  END LOOP;
END;
/

ALTER table  core_clave_privada add   ID NUMBER(19,0);  
ALTER table  core_clave_publica add   ID NUMBER(19,0); 
ALTER table  core_clave_privada add FECHAALTA TIMESTAMP (6);
ALTER table  core_clave_privada add FECHABAJA TIMESTAMP (6); 	
ALTER table  core_clave_publica add FECHAALTA TIMESTAMP (6); 	
ALTER table  core_clave_publica add FECHABAJA TIMESTAMP (6); 	
ALTER TABLE  core_servicio  ADD PLANTILLAXSLT VARCHAR2(512)   NULL;
ALTER TABLE  core_clave_privada  ADD  ORGANISMO  NUMBER(19,0) NULL;
commit;
update core_clave_publica set id= (ID_CLAVE_PUBLICA_SEQUENCE.nextval);
update core_clave_privada set id= (ID_CLAVE_PRIVADA_SEQUENCE.nextval);
alter table core_servicio rename column clavefirma to clavefirma_alias;
alter table core_servicio rename column clavecifrado to clavecifrado_alias;
ALTER TABLE  core_servicio  ADD  clavecifrado  NUMBER(19,0) NULL;
ALTER TABLE  core_servicio  ADD  clavefirma  NUMBER(19,0) NULL; 									
commit; 
update core_clave_privada set fechaAlta=SYSDATE;
update core_clave_publica set fechaAlta=SYSDATE; 
update core_servicio cs set ( cs.clavecifrado ) = (select  cc.id  from core_clave_publica cc where   cs.clavecifrado_alias =cc.alias and  rownum = 1);
update core_servicio cs set ( cs.clavefirma ) = (select  cf.id from core_clave_privada cf where   cs.clavefirma_alias =cf.alias and  rownum = 1);   
commit;
ALTER TABLE  core_servicio drop column  CLAVEFIRMA_ALIAS;
ALTER TABLE  core_servicio drop column  CLAVECIFRADO_ALIAS;
ALTER TABLE  core_clave_publica MODIFY  FECHAALTA  TIMESTAMP (6) not null;
ALTER TABLE  core_clave_privada MODIFY  FECHAALTA  TIMESTAMP (6) not null;  
ALTER table  core_clave_privada MODIFY id   NUMBER(19,0) not null;
ALTER table  core_clave_publica MODIFY id   NUMBER(19,0) not null;
ALTER TABLE  core_clave_privada  ADD PRIMARY KEY ("ID");
ALTER TABLE  core_clave_publica  ADD PRIMARY KEY ("ID");
/*ALTER TABLE  core_clave_privada  ADD CONSTRAINT "clave_privada_org" FOREIGN KEY (organismo)
	  REFERENCES organismo_cesionario (id) ENABLE;
ALTER TABLE core_servicio ADD CONSTRAINT "serv_clavefirma" FOREIGN KEY (clavefirma)
	  REFERENCES core_clave_privada (id) ENABLE; 
ALTER TABLE core_servicio ADD CONSTRAINT "serv_clavecifrado" FOREIGN KEY (clavecifrado)
	  REFERENCES core_clave_publica (id) ENABLE;*/
ALTER table core_emisor_certificado MODIFY nombre varchar2(50);
INSERT INTO SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0504','Error en la configuración {0}');
delete from core_parametro_configuracion where nombre='cif.solicitante';
delete from core_parametro_configuracion where nombre='nombre.solicitante';
delete from core_parametro_configuracion where nombre='axis2.client.repository';
delete from core_parametro_configuracion where nombre='axis2.client.xml';
update core_parametro_configuracion set valor='3.4.0' where nombre='version.datamodel.scsp';
commit; 

/* UpdateEmisor-3.5.0a3.5.3 */
update SCSP_CODIGO_ERROR set DESCRIPCION='Error genérico del BackOffice' WHERE CODIGO='0242';
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR ='3.5.3' WHERE NOMBRE='version.datamodel.scsp';
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0244','No existe la petición {0} en el sistema');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0414','El número de elementos no coincide con el número de solicitudes recibidas');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0415','El número de solicitudes es mayor que 1, ejecute el servicio en modo asíncrono.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0419','Existen identificadores de solicitud repetidos.');

/* UpdateEmisor-3.5.3a3.5.4 */
ALTER TABLE  ORGANISMO_CESIONARIO MODIFY  (BLOQUEADO  NUMBER(1,0) DEFAULT 0 NOT NULL);   
UPDATE ORGANISMO_CESIONARIO  SET BLOQUEADO = 0 WHERE BLOQUEADO IS NULL;

/* Afegit per arreglar mancances */
BEGIN
  FOR c IN
  (SELECT c.owner, c.table_name, c.constraint_name
   FROM user_constraints c, user_tables t
   WHERE c.table_name = t.table_name 
   and (c.table_name='CORE_PETICION_RESPUESTA' or c.table_name='EMISOR_BACKOFFICE' or c.table_name='CORE_MODULO_CONFIGURACION')
   and (c.constraint_type = 'R'  )
   ORDER BY c.constraint_type DESC)
  LOOP
    dbms_utility.exec_ddl_statement('alter table "' || c.owner || '"."' || c.table_name || '" DROP constraint ' || c.constraint_name);
  END LOOP;
END;
/

create sequence ID_SERVICIO_SEQUENCE start with 1 increment by 1 nocache nocycle;
alter table core_servicio add id number(19,0);
update core_servicio set id = (ID_SERVICIO_SEQUENCE.nextval);
alter table core_servicio drop primary key;
alter table core_servicio add primary key (id);

alter table emisor_backoffice add id_certificado number(19,0);
update emisor_backoffice e set id_certificado = (select s.id from core_servicio s where s.codcertificado = e.certificado);
alter table emisor_backoffice drop primary key;
alter table emisor_backoffice drop column certificado;
alter table emisor_backoffice rename column id_certificado to certificado;
alter table emisor_backoffice add primary key (certificado);
alter table emisor_backoffice add constraint emisor_bo_servicio foreign key (certificado) references core_servicio (id) enable novalidate;

alter table core_peticion_respuesta add id_certificado number(19,0);
update core_peticion_respuesta p set id_certificado = (select s.id from core_servicio s where s.codcertificado = p.certificado);
alter table core_peticion_respuesta drop column certificado;
alter table core_peticion_respuesta rename column id_certificado to certificado;
alter table core_peticion_respuesta add constraint pet_resp_servicio foreign key (certificado) references core_servicio (id) enable novalidate;

alter table core_modulo_configuracion add id_certificado number(19,0);
update core_modulo_configuracion m set id_certificado = (select s.id from core_servicio s where s.codcertificado = m.certificado);
alter table core_modulo_configuracion drop primary key;
alter table core_modulo_configuracion drop column certificado;
alter table core_modulo_configuracion rename column id_certificado to certificado;
alter table core_modulo_configuracion add primary key (modulo,certificado);
alter table core_modulo_configuracion add constraint mod_conf_servicio foreign key (certificado) references core_servicio (id) enable novalidate;

ALTER TABLE "CORE_SERVICIO" ADD CONSTRAINT "serv_emisor" FOREIGN KEY ("EMISOR")
	  REFERENCES "CORE_EMISOR_CERTIFICADO" ("ID") ENABLE;
ALTER TABLE  core_clave_privada  ADD CONSTRAINT "clave_privada_org" FOREIGN KEY (organismo)
	  REFERENCES organismo_cesionario (id) ENABLE;
ALTER TABLE core_servicio ADD CONSTRAINT "serv_clavefirma" FOREIGN KEY (clavefirma)
	  REFERENCES core_clave_privada (id) ENABLE; 
ALTER TABLE core_servicio ADD CONSTRAINT "serv_clavecifrado" FOREIGN KEY (clavecifrado)
	  REFERENCES core_clave_publica (id) ENABLE;

update core_clave_privada set interoperabilidad = 0;

ALTER TABLE EMS_BACKOFFICE_COM ADD ERROR CLOB;
ALTER TABLE EMS_BACKOFFICE_COM MOVE LOB (ERROR) STORE AS EMS_BACKCOM_ERROR_LOB (TABLESPACE EMISERV_LOB);
commit;

/*                            */
/* Actualització a SCSP 4.0.0 */
/*                            */

/* Emisor_01.sql */
UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.0.0' WHERE NOMBRE='version.datamodel.scsp';
commit;
RENAME SCSP_CODIGO_ERROR TO CORE_CODIGO_ERROR; 
RENAME ORGANISMO_CESIONARIO TO CORE_ORGANISMO_CESIONARIO;
RENAME SCSP_ESTADO_PETICION TO CORE_ESTADO_PETICION;
RENAME SCSP_CODIGO_ERROR_SECUNDARIO TO CORE_EM_CODIGO_ERROR_SECUN;
RENAME AUTORIZACION_APLICACION TO CORE_EM_APLICACION;
RENAME AUTORIZACION_CA TO CORE_EM_AUTORIZACION_CA;
RENAME AUTORIZACION_CERTIFICADO TO CORE_EM_AUTORIZACION_CERT;
RENAME AUTORIZACION_ORGANISMO TO CORE_EM_AUTORIZACION_ORGANISMO;
RENAME EMISOR_BACKOFFICE TO CORE_EM_BACKOFFICE;
RENAME SCSP_SECUENCIA_IDTRANSMISION TO CORE_EM_SECUENCIA_IDTRANS;
COMMIT;
ALTER TABLE    CORE_EM_SECUENCIA_IDTRANS MODIFY    (  PREFIJO  varchar2(9)  );
ALTER TABLE    CORE_SERVICIO MODIFY    (  PREFIJOPETICION  VARCHAR2(9)  );
ALTER TABLE    CORE_SERVICIO MODIFY    (  PREFIJOIDTRANSMISION  VARCHAR2(9)  ); 
COMMIT;
DROP PROCEDURE GETSECUENCIAIDTRANSMISION;
COMMIT;
CREATE OR REPLACE PROCEDURE "GETSECUENCIAIDTRANSMISION"  (  prefijo_param in varchar2, on_Secuencial out number)as  rRegistro ROWID;

begin  
    select ROWID, SECUENCIA+1 into rRegistro, on_Secuencial from CORE_EM_SECUENCIA_IDTRANS where PREFIJO = prefijo_param for update;
    update CORE_EM_SECUENCIA_IDTRANS set SECUENCIA = on_Secuencial, FECHAGENERACION=sysdate where rowid = rRegistro;  
	commit; 
    exception when no_data_found then on_Secuencial := 1;    
    insert into CORE_EM_SECUENCIA_IDTRANS (PREFIJO, SECUENCIA,FECHAGENERACION) values (prefijo_param, on_Secuencial,(SELECT SYSDATE FROM DUAL)); 
commit; 
end;

/* Emisor_02.sql */
--ALTER TABLE CORE_EM_CODIGO_ERROR_SECUN MODIFY (DESCRIPCION CLOB);
--ALTER TABLE CORE_LOG MODIFY (MENSAJE CLOB );
ALTER TABLE CORE_ORGANISMO_CESIONARIO MODIFY ( LOGO BLOB ); 
--ALTER TABLE CORE_TOKEN_DATA MODIFY ( DATOS CLOB ); 
--ALTER TABLE CORE_TRANSMISION MODIFY ( XMLTRANSMISION CLOB );
COMMIT;
DECLARE
    cursor cnombre Is select index_name from user_indexes where status='UNUSABLE';
BEGIN 
    FOR rNOMBRE IN cnombre LOOP 
		execute immediate 'ALTER INDEX ' || rNOMBRE.index_name || ' REBUILD ';
    END LOOP;
	COMMIT;
END;
