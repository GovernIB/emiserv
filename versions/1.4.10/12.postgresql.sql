/* 4.6.0 a 4.7.0 */
UPDATE core_parametro_configuracion set valor='4.6.0' where nombre='version.datamodel.scsp';  

/* 4.7.0 a 4.8.0 */
UPDATE core_parametro_configuracion set valor='4.8.0' where nombre='version.datamodel.scsp';  
ALTER TABLE core_emisor_certificado add fechaAlta  timestamp without time zone;  
UPDATE core_emisor_certificado SET fechaAlta = (select now()) WHERE fechaAlta IS NULL; 

/* 4.8.0 a 4.16.0 */
update core_parametro_configuracion set valor='4.16.0' where nombre='version.datamodel.scsp';  
ALTER TABLE core_transmision alter COLUMN estado type character varying(10);

/* 4.16.0 a 4.19.0 */
CREATE INDEX core_servicio_index_emisor  ON core_servicio (emisor);