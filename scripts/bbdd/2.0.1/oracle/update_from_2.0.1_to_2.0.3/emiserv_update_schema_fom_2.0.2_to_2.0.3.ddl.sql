-- #36
CREATE TABLE ems_config_group (code VARCHAR2(128) NOT NULL, parent_code VARCHAR2(128), position NUMBER(3) NOT NULL, description_key VARCHAR2(512) NOT NULL);
ALTER TABLE ems_config_group ADD PRIMARY KEY (code);
grant select, update, insert, delete on ems_config_group to www_emiserv;

CREATE TABLE ems_config_type (code VARCHAR2(128) NOT NULL, value VARCHAR2(2048));
ALTER TABLE ems_config_type ADD PRIMARY KEY (code);
grant select, update, insert, delete on ems_config_type to www_emiserv;

CREATE TABLE ems_config (key VARCHAR2(256) NOT NULL, value VARCHAR2(2048), description_key VARCHAR2(2048), group_code VARCHAR2(128) NOT NULL, position NUMBER(3) NOT NULL, source_property VARCHAR2(16) NOT NULL, requereix_reinici NUMBER(1) DEFAULT 0, type_code VARCHAR2(128), lastmodifiedby VARCHAR2(64), lastmodifieddate TIMESTAMP);
ALTER TABLE ems_config ADD PRIMARY KEY (key);
ALTER TABLE ems_config ADD CONSTRAINT ems_config_group_fk FOREIGN KEY (group_code) REFERENCES ems_config_group (code);
grant select, update, insert, delete on ems_config to www_emiserv;
