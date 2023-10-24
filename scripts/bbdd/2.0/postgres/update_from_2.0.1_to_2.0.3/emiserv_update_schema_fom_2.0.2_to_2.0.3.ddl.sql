-- #36
CREATE TABLE ems_config_group (code VARCHAR(128) NOT NULL, parent_code VARCHAR(128), position SMALLINT NOT NULL, description_key VARCHAR(512) NOT NULL);
ALTER TABLE ems_config_group ADD PRIMARY KEY (code);

CREATE TABLE ems_config_type (code VARCHAR(128) NOT NULL, value VARCHAR(2048));
ALTER TABLE ems_config_type ADD PRIMARY KEY (code);

CREATE TABLE ems_config (key VARCHAR(256) NOT NULL, value VARCHAR(2048), description_key VARCHAR(2048), group_code VARCHAR(128) NOT NULL, position SMALLINT NOT NULL, source_property VARCHAR(16) NOT NULL, requereix_reinici BOOLEAN DEFAULT FALSE, type_code VARCHAR(128), lastmodifiedby VARCHAR(64), lastmodifieddate TIMESTAMP WITHOUT TIME ZONE);
ALTER TABLE ems_config ADD PRIMARY KEY (key);
ALTER TABLE ems_config ADD CONSTRAINT ems_config_group_fk FOREIGN KEY (group_code) REFERENCES ems_config_group (code);
