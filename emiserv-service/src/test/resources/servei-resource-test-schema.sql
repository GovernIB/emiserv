-- Taules que les fórmules de ServeiResourceEntity consulten i que no són entitats del test
-- object_id_identity és VARCHAR(36) com a PostgreSQL (a Oracle és NUMBER)
create table ems_acl_class (id bigint primary key, class varchar(100));
create table ems_acl_object_identity (id bigint primary key, object_id_class bigint, object_id_identity varchar(36));
create table ems_acl_entry (id bigint primary key, acl_object_identity bigint, sid bigint);
create table ems_servei_ruta_desti (id bigint primary key, servei_id bigint);
