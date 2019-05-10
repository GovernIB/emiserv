
  
  CREATE OR REPLACE PROCEDURE GETSECUENCIAIDTRANSMISION  (  prefijo_param in varchar2, on_Secuencial out number)as  rRegistro ROWID;

begin
    select ROWID, SECUENCIA+1 into rRegistro, on_Secuencial from CORE_EM_SECUENCIA_IDTRANS where PREFIJO = prefijo_param for update;
    update CORE_EM_SECUENCIA_IDTRANS set SECUENCIA = on_Secuencial, FECHAGENERACION=sysdate where rowid = rRegistro;
	commit;
    exception when no_data_found then on_Secuencial := 1;
    insert into CORE_EM_SECUENCIA_IDTRANS (PREFIJO, SECUENCIA,FECHAGENERACION) values (prefijo_param, on_Secuencial,(SELECT SYSDATE FROM DUAL));
commit;
end;