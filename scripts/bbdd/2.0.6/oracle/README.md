# Scripts d'instal·lació de la base de dades EMISERV (Oracle)

Scripts SQL per crear l'esquema EMISERV des de zero en un entorn nou amb Oracle.
Generats a partir dels changelogs de Liquibase del projecte
(`emiserv-persistence/src/main/resources/db/changelog/db.changelog-master.yaml`
i tot el que inclou), executant-los amb el motor real de Liquibase 4.4.3 en
mode `offline:oracle` contra un esquema buit. Representen, doncs, exactament
el SQL que Liquibase generaria per a Oracle en una instal·lació nova, versió
2.0.6.

## Fitxers

| Fitxer | Contingut |
|---|---|
| `emiserv_01_taules.sql` | Només `CREATE TABLE`, una columna per línia. Les `ALTER TABLE ADD/DROP/RENAME COLUMN` de les migracions s'han aplanat dins de cada `CREATE TABLE` (estat final de columnes); els `COMMENT ON` s'han descartat |
| `emiserv_02_constraints.sql` | Claus primàries, foranies i uniques (`ALTER TABLE ADD CONSTRAINT` / `ADD PRIMARY KEY`) |
| `emiserv_03_indexos.sql` | Índexs addicionals (a banda dels implícits de les PK/UK) |
| `emiserv_04_sequencies.sql` | Seqüències (`CREATE SEQUENCE`) |
| `emiserv_05_triggers.sql` | Triggers generadors d'identificador (taules `ems_acl_*`) |
| `emiserv_06_lobs.sql` | Moviment de columnes LOB al tablespace `EMISERV_LOB` |
| `emiserv_07_grants.sql` | `GRANT` sobre taules i seqüències per a l'usuari `WWW_EMISERV` |
| `emiserv_08_dades_inicials.sql` | Dades de configuració, mòduls, tipus i paràmetres inicials |
| `emiserv_00_instalar.sql` | Executa els anteriors en l'ordre correcte |

L'ordre d'execució (taules → constraints → índexs → seqüències → triggers →
logs → grants → dades) és el mateix que fa servir `db.changelog-master.yaml`
i evita problemes de dependències (p.ex. les claus foranies necessiten que
totes les taules ja existeixin; els triggers usen `NEXTVAL` de seqüències
que ja han d'existir).

## Prerequisits (a càrrec del DBA, fora d'aquests scripts)

- L'usuari/rol **`WWW_EMISERV`**, amb el qual es connectarà l'aplicació. Els
  scripts només li atorguen permisos (`SELECT/INSERT/UPDATE/DELETE` sobre
  taules i `SELECT` sobre seqüències); no el creen.
- El **tablespace `EMISERV_LOB`**, usat per emmagatzemar les columnes CLOB
  més voluminoses (`emiserv_06_lobs.sql`).
- Un esquema/usuari propietari amb permisos de `CREATE TABLE`, `CREATE
  SEQUENCE` i `CREATE TRIGGER`, que serà qui executi els scripts (no
  `WWW_EMISERV`).

## Execució

```bash
sqlplus USUARI_ESQUEMA/PASSWORD@BBDD @emiserv_00_instalar.sql
```

## Nota sobre `hibernate_sequence`

Als changelogs de Liquibase, la seqüència es crea amb el nom
`hibernate_sequence` i es renombra posteriorment a `ems_hibernate_sequence`
(canvi introduït a la versió 2.0.1, per adaptar-se al dialecte Oracle propi
de l'aplicació, `OracleCaibDialect`). Com que aquí partim d'un entorn nou,
`emiserv_04_sequencies.sql` la crea directament amb el nom final
`ems_hibernate_sequence`, sense pas intermedi.

## Contrast amb les entitats Hibernate

Les taules `ems_*` amb entitat JPA (totes excepte `ems_acl_*`, que són les
taules estàndard d'ACL de Spring Security i no tenen entitat pròpia) s'han
contrastat una a una amb les classes
`emiserv-persistence/src/main/java/es/caib/emiserv/persist/entity/*Entity.java`.
Es van trobar i corregir 3 discrepàncies entre el que declarava explícitament
l'anotació `@Column` de l'entitat i el DDL heretat dels changelogs:

- `ems_entitat.cif`: l'entitat declara `unique = true` però no hi havia cap
  restricció d'unicitat a la BD. S'ha afegit `UNIQUE (cif)`.
- `ems_entitat.unitat_arrel`: l'entitat no declara `nullable = false` però la
  columna era `NOT NULL`. S'ha fet nullable.
- `ems_config_group.description_key`: l'entitat declara explícitament
  `nullable = true` però la columna era `NOT NULL`. S'ha fet nullable.
- `ems_config_type.value`: l'entitat declara `nullable = false` però a la
  columna hi faltava el `NOT NULL`. S'ha afegit.

Altres diferències detectades però **no** modificades perquè semblen més
aviat una imprecisió a l'entitat que no pas un error de la BD (a revisar pel
propi equip de desenvolupament si es vol una coherència total):

- `ems_config.group_code` és `VARCHAR2(128)` a la BD (coherent amb la FK cap
  a `ems_config_group.code`, que és `VARCHAR2(128)`), però `ConfigEntity`
  declara `length = 2048` (sembla un copiar-enganxar de `description_key`).
- `ems_redir_peticio.procediment_codi` / `procediment_nom` són `VARCHAR2(256)`
  a la BD, però `RedireccioPeticioEntity` no declara `length` (per tant
  Hibernate n'esperaria 255); a `ems_redir_solicitud`, en canvi, els mateixos
  camps sí que declaren `length = 256`. La BD és més ampla, no hi ha risc de
  truncament.
- `ConfigEntity.type` mapeja la columna `type_code` amb
  `@JoinColumn(insertable = false, updatable = false, foreignKey = @ForeignKey(name = "not_config_type_fk"))`,
  però aquesta FK no existeix a la BD generada. El nom (`not_...`) suggereix
  que és intencionat i no es crea.

Les columnes d'auditoria compartides (`createdby_codi`, `createddate`,
`lastmodifiedby_codi`, `lastmodifieddate`, heretades de
`AbstractAuditableEntity`) no declaren `length`/`nullable` a la superclasse
però la BD sí que els aplica (`createdby_codi VARCHAR2(64) NOT NULL`,
`createddate TIMESTAMP NOT NULL`); és un patró consistent a totes les taules
que n'hereten (`ems_servei`, `ems_servei_ruta_desti`), no una discrepància
puntual, i per tant no s'ha tocat.

## Regenerar aquests scripts

Si el model de dades canvia, es poden tornar a generar amb Liquibase en
mode offline (sense necessitat de connexió a una base de dades real):

```bash
cd emiserv-persistence
mvn liquibase:updateSQL -Dliquibase.propertyFile=liquibase/liquibase.properties
```

(cal que `liquibase/databasechangelog.csv` NO existeixi perquè generi
l'script complet de creació en lloc d'un script d'actualització; vegeu
`liquibase/README`). La sortida resultant s'ha de tornar a repartir pels
fitxers d'aquest directori segons el tipus de sentència SQL.
