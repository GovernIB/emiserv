ALTER TABLE ems_backoffice_com ADD CONSTRAINT ems_backcom_backpet_fk FOREIGN KEY (peticio_id) REFERENCES ems_backoffice_pet (id);

ALTER TABLE ems_backoffice_com ADD CONSTRAINT ems_backcom_backsol_fk FOREIGN KEY (solicitud_id) REFERENCES ems_backoffice_sol (id);

ALTER TABLE ems_backoffice_pet ADD CONSTRAINT ems_backpet_backcom_fk FOREIGN KEY (comunicacio_id) REFERENCES ems_backoffice_com (id);

ALTER TABLE ems_backoffice_pet ADD CONSTRAINT ems_backpet_scsppet_fk FOREIGN KEY (peticio_id) REFERENCES core_peticion_respuesta (idpeticion);

ALTER TABLE ems_backoffice_sol ADD CONSTRAINT ems_backsol_backcom_fk FOREIGN KEY (comunicacio_id) REFERENCES ems_backoffice_com (id);

ALTER TABLE ems_backoffice_sol ADD CONSTRAINT ems_backsol_backpet_fk FOREIGN KEY (peticio_id) REFERENCES ems_backoffice_pet (id);

ALTER TABLE ems_redir_missatge ADD CONSTRAINT ems_redmsg_peticio_fk FOREIGN KEY (peticio_id) REFERENCES ems_redir_peticio (id);

ALTER TABLE ems_redir_solicitud ADD CONSTRAINT ems_redsol_peticio_fk FOREIGN KEY (peticio_id) REFERENCES ems_redir_peticio (id);

ALTER TABLE ems_servei_ruta_desti ADD CONSTRAINT ems_serrutadesti_servei_fk FOREIGN KEY (servei_id) REFERENCES ems_servei (id);

ALTER TABLE ems_acl_sid ADD CONSTRAINT ems_acl_sid_uk UNIQUE (sid, principal);

ALTER TABLE ems_acl_class ADD CONSTRAINT ems_acl_class_uk UNIQUE (class);

ALTER TABLE ems_acl_object_identity ADD CONSTRAINT ems_acl_oid_uk UNIQUE (object_id_class, object_id_identity);

ALTER TABLE ems_acl_object_identity ADD CONSTRAINT ems_acl_oid_parent_fk FOREIGN KEY (parent_object) REFERENCES ems_acl_object_identity (id);

ALTER TABLE ems_acl_object_identity ADD CONSTRAINT ems_acl_oid_class_fk FOREIGN KEY (object_id_class) REFERENCES ems_acl_class (id);

ALTER TABLE ems_acl_object_identity ADD CONSTRAINT ems_acl_oid_owner_fk FOREIGN KEY (owner_sid) REFERENCES ems_acl_sid (id);

ALTER TABLE ems_acl_entry ADD CONSTRAINT ems_acl_entry_uk UNIQUE (acl_object_identity, ace_order);

ALTER TABLE ems_acl_entry ADD CONSTRAINT ems_acl_entry_object_fk FOREIGN KEY (acl_object_identity) REFERENCES ems_acl_object_identity (id);

ALTER TABLE ems_acl_entry ADD CONSTRAINT ems_acl_entry_acl_fk FOREIGN KEY (sid) REFERENCES ems_acl_sid (id);

ALTER TABLE core_clave_privada ADD CONSTRAINT clave_privada_organismo FOREIGN KEY (organismo) REFERENCES core_organismo_cesionario (id);

ALTER TABLE core_modulo_configuracion ADD CONSTRAINT mod_conf_modulo FOREIGN KEY (modulo) REFERENCES core_modulo (nombre);

ALTER TABLE core_modulo_configuracion ADD CONSTRAINT mod_conf_servicio FOREIGN KEY (certificado) REFERENCES core_servicio (id);

ALTER TABLE core_req_modulo_pdf_cesionario ADD CONSTRAINT fk_modulo_mod_pdf FOREIGN KEY (modulo) REFERENCES core_modulo (nombre);

ALTER TABLE core_req_modulo_pdf_cesionario ADD CONSTRAINT fk_servicio_mod_pdf FOREIGN KEY (servicio) REFERENCES core_servicio (id);

ALTER TABLE core_req_modulo_pdf_cesionario ADD CONSTRAINT fk_org_cesionario_mod_pdf FOREIGN KEY (organismo) REFERENCES core_organismo_cesionario (id);

ALTER TABLE core_peticion_respuesta ADD CONSTRAINT pet_resp_servicio FOREIGN KEY (certificado) REFERENCES core_servicio (id);

ALTER TABLE core_servicio ADD CONSTRAINT serv_emisor FOREIGN KEY (emisor) REFERENCES core_emisor_certificado (id);

ALTER TABLE core_servicio ADD CONSTRAINT serv_clavecifrado FOREIGN KEY (clavecifrado) REFERENCES core_clave_publica (id);

ALTER TABLE core_servicio ADD CONSTRAINT serv_clavefirma FOREIGN KEY (clavefirma) REFERENCES core_clave_privada (id);

ALTER TABLE core_token_data ADD CONSTRAINT token_peticion FOREIGN KEY (idpeticion) REFERENCES core_peticion_respuesta (idpeticion);

ALTER TABLE core_token_data ADD CONSTRAINT token_tipo FOREIGN KEY (tipomensaje) REFERENCES core_tipo_mensaje (tipo);

ALTER TABLE core_transmision ADD CONSTRAINT trans_peticion FOREIGN KEY (idpeticion) REFERENCES core_peticion_respuesta (idpeticion);

ALTER TABLE core_em_aplicacion ADD CONSTRAINT apli_codca FOREIGN KEY (autoridadcertif) REFERENCES core_em_autorizacion_ca (id);

ALTER TABLE core_em_autorizacion_cert ADD CONSTRAINT aut_cert_servicio FOREIGN KEY (idcertificado) REFERENCES core_servicio (id);

ALTER TABLE core_em_autorizacion_cert ADD CONSTRAINT aut_cert_organismo FOREIGN KEY (idorganismo) REFERENCES core_em_autorizacion_organismo (id);

ALTER TABLE core_em_autorizacion_cert ADD CONSTRAINT aut_cert_aplicacion FOREIGN KEY (idaplicacion) REFERENCES core_em_aplicacion (idaplicacion);

ALTER TABLE core_em_codigo_error_secun ADD CONSTRAINT codigosecundario_fk FOREIGN KEY (codigo) REFERENCES core_codigo_error (codigo);

ALTER TABLE core_em_backoffice ADD CONSTRAINT emisor_bo_servicio FOREIGN KEY (certificado) REFERENCES core_servicio (id);