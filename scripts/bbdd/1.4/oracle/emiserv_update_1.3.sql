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
/

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
/

ALTER TABLE CORE_EM_AUTORIZACION_ORGANISMO DROP COLUMN IDAPLICACION CASCADE CONSTRAINTS;

update core_em_autorizacion_cert ac set idcertificado = (select s.id from core_servicio s where s.codcertificado = ac.codcertificado);
alter table core_em_autorizacion_cert drop primary key;
alter table core_em_autorizacion_cert drop column codcertificado;
alter table core_em_autorizacion_cert add primary key (idcertificado, idaplicacion, idorganismo);
alter table core_em_autorizacion_cert add constraint autorizacion_cert_servicio foreign key (idcertificado) references core_servicio (id) enable novalidate;

alter table core_em_autorizacion_cert modify fechaalta null;

ALTER TABLE EMS_SERVEI ADD XSD_ACTIVA NUMBER(1);
ALTER TABLE EMS_SERVEI ADD XSD_ESQUEMA_BAK VARCHAR2(256);
