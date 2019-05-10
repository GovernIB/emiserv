
create or replace function getsecuenciaidtransmision  (  prefijo_param in varchar(4000), on_secuencial out int) 
returns int as $$
declare rregistro char(10);

begin
    select rowid, secuencia+1 into rregistro, on_secuencial from core_em_secuencia_idtrans where prefijo = prefijo_param for update;
    update core_em_secuencia_idtrans set secuencia = on_secuencial, fechageneracion=current_timestamp where rowid = rregistro;
	commit;
    exception when no_data_found then on_secuencial := 1;
    insert into core_em_secuencia_idtrans (prefijo, secuencia,fechageneracion) values (prefijo_param, on_secuencial,(select current_timestamp from now()));
commit;
end;
 $$  language plpgsql;