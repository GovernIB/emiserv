CREATE UNIQUE INDEX ems_servei_codi_i ON ems_servei(codi);

CREATE INDEX core_servicio_index_emisor ON core_servicio(emisor);

CREATE INDEX core_transmision_index_idpet ON core_transmision(idpeticion);

CREATE UNIQUE INDEX unique_serv_apl_org_baja ON core_em_autorizacion_cert(idcertificado, idorganismo, idaplicacion, fechabaja);

CREATE INDEX cesionarios_index_organismo ON core_req_cesionarios_servicios(organismo);