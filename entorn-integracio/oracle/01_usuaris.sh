#!/bin/bash
# Esquemes de l'entorn d'integració a la PDB FREEPDB1. La imatge gvenzl/oracle-free executa
# aquest script en arrencar el contenidor per primera vegada; el fitxer de /tmp indica al
# healthcheck del compose que els esquemes ja existeixen.
set -e

sqlplus -s / as sysdba <<EOF
WHENEVER SQLERROR EXIT FAILURE
ALTER SESSION SET CONTAINER = FREEPDB1;
CREATE USER PINBAL IDENTIFIED BY "${INTEGRACIO_DB_PASSWORD}" QUOTA UNLIMITED ON USERS;
CREATE USER EMISERV IDENTIFIED BY "${INTEGRACIO_DB_PASSWORD}" QUOTA UNLIMITED ON USERS;
CREATE USER EMISERV_CEDENT IDENTIFIED BY "${INTEGRACIO_DB_PASSWORD}" QUOTA UNLIMITED ON USERS;
GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE SYNONYM, CREATE MATERIALIZED VIEW TO PINBAL, EMISERV, EMISERV_CEDENT;
-- Rols dels usuaris d'aplicació de producció: els changelogs hi fan grants
CREATE ROLE WWW_EMISERV;
CREATE ROLE WWW_PINBAL;
EXIT
EOF

touch /tmp/integracio-usuaris-creats
