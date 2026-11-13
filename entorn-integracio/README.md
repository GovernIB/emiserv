# Entorn d'integració PINBAL + EMISERV

Entorn local semblant a producció per provar EMISERV amb els seus clients i cedents reals:

```
                     navegador ──► Keycloak (keycloak.localhost:8090, realm GOIB)

 PINBAL :8081 ──SCSP──► EMISERV central :8082 ──► 07040 EMISERV cedent :8083 ──► backoffice (fake)
      │                 (CAIB, S0711001H)     ├─► 07026 emissor SCSP d'un altre fabricant (fake)
      │                                       ├─► 07011 backoffice de l'ajuntament, connexió directa (fake)
      │                                       └─► 07033 emissor que respon amb error (fake)
      │                 SVDSCTFNWS01 (múltiple) ──► MALLORCA, MENORCA, EIVISSA (fake)
      │                 backoffices de la CAIB  ──► backoffice (fake)
      └──SCSP──► pinbal-scsp-fake :18082 (serveis de l'Estat, RedSARA)
```

| Servei | URL | Què és |
|---|---|---|
| `pinbal` | http://localhost:8081/pinbalback | PINBAL (imatge `goib/pinbal`), esquema i dades de prova amb el seu Liquibase (context `e2e`) |
| `emiserv-central` | http://localhost:8082/emiservback | EMISERV de la CAIB, amb els serveis de proves.caib.es (enrutadors, enrutador múltiple i backoffices) |
| `emiserv-cedent` | http://localhost:8083/emiservback | EMISERV de l'Ajuntament de Manacor (P0704000A): padró (SCDCPAJU, SCDHPAJU) amb backoffice propi |
| `emiserv-scsp-fake` | http://localhost:18081/__fake-emiserv | Emissors SCSP i backoffices simulats (mòdul `emiserv-scsp-fake`) |
| `pinbal-scsp-fake` | http://localhost:18082/__fake-scsp | Serveis de l'Estat simulats (mòdul `pinbal-scsp-fake` de PINBAL) |
| `mailpit` | http://localhost:8025 | Correus enviats per PINBAL (SMTP sense entrega real) |
| `keycloak` | http://keycloak.localhost:8090 | Keycloak 21 amb el realm `GOIB` (consola: `admin`/`admin`) |
| `oracle` | `localhost:1523/FREEPDB1` | Oracle 23 Free efímer: esquemes `PINBAL`, `EMISERV`, `EMISERV_CEDENT` (contrasenya `Integracio_1`) |

Els contenidors es parlen pel nom del servei (`http://emiserv-central:8080/...`). Keycloak té el
mateix nom per al navegador i per als contenidors (`keycloak.localhost`: els navegadors resolen
`*.localhost` a la màquina local i dins la xarxa és un àlies del contenidor), així els tokens tenen
el mateix emissor per a tothom.

## Requisits

- Imatges `280421/emiserv:latest` (`mvn clean install -P front` i `mvn docker:build -pl emiserv-ear`)
  i `goib/pinbal:latest`. Es poden canviar amb `EMISERV_IMAGE` i `PINBAL_IMAGE`.
- Els JAR dels dos fakes compilats: `mvn -f emiserv-scsp-fake/pom.xml package` aquí i
  `mvn -pl pinbal-scsp-fake package` a PINBAL.
- El repositori de PINBAL a `../../pinbal2` (relatiu a aquesta carpeta) o a `PINBAL_DIR`: se
  n'agafen els keystores i el fake.
- Uns 10 GB de memòria lliure (tres JBoss, Oracle i Keycloak). La primera arrencada tarda uns
  4 minuts.

Les variables es poden posar en un fitxer `.env` d'aquesta carpeta (ports: `PINBAL_PORT`,
`EMISERV_CENTRAL_PORT`, `EMISERV_CEDENT_PORT`, `KEYCLOAK_PORT`, `ORACLE_PORT`...). Totes les
contrasenyes són d'aquest entorn; no hi poseu cap credencial real.

## Ús

```bash
cd entorn-integracio
podman-compose up -d        # o docker compose up -d
podman-compose ps           # els JBoss tarden uns minuts a estar «healthy»
podman-compose down         # atura i esborra tot (la BD no es conserva)
```

Ordre d'arrencada: Oracle (crea els esquemes) → `emiserv-*-bd` (Liquibase d'EMISERV + dades) →
EMISERV central i cedent; PINBAL crea el seu esquema en desplegar-se i, quan respon,
`pinbal-bd` hi afegeix l'encaminament cap a l'EMISERV central. Per tornar a començar de zero n'hi
ha prou amb `down` i `up`.

Els contenidors de Liquibase (`*-bd`) no s'aturen quan acaben: es queden en marxa i «healthy», i
els serveis que els necessiten hi depenen amb `service_healthy`. podman-compose no comprova el
codi de sortida de `service_completed_successfully` (un Liquibase que fallàs deixaria arrencar
l'EMISERV) i, en engegar un servei, torna a engegar les dependències que ja s'han aturat. Si un
Liquibase falla, el seu contenidor s'atura sense arribar a «healthy» i `up -d` es queda esperant:
`podman logs entorn-integracio_emiserv-central-bd_1` (o el `*-bd` corresponent) en dona el motiu.

L'esquema d'EMISERV es crea amb el changelog del projecte (`emiserv-persistence`) i Liquibase
4.23: la 4.4.3 del projecte talla els triggers pel `;`, i a partir de la 4.24 els `BOOLEAN` serien
natius a Oracle 23 (a producció són `NUMBER(1)`). L'esquema resultant és igual que el de les
instal·lacions actualitzades amb `versions/`, llevat de `ems_config_group.description_key` i
`ems_entitat.unitat_arrel`, que aquí són `NOT NULL`.

## Usuaris (contrasenya = nom d'usuari)

| Usuari | Rols | Per a |
|---|---|---|
| `admin` | EMS_ADMIN, EMS_RESP, EMS_COM, EMS_REPORT, PBL_ADMIN, PBL_SUPERAUD | Tot |
| `ems_admin` | EMS_ADMIN, EMS_RESP | Administrador d'EMISERV |
| `ems_resp` | EMS_RESP | Responsable d'EMISERV (només aquest rol) |
| `ems_ws` | EMS_COM, EMS_REPORT | APIs REST d'EMISERV (basic auth) |
| `pbl_admin` | PBL_ADMIN | Administrador de PINBAL |
| `pbl_delegat` | tothom | Delegat de l'entitat E2EENT01 (pot consultar el padró amb E2EPROC01) |
| `pbl_repres`, `pbl_audit`, `pbl_all` | PBL_REPRES / PBL_AUDIT | Representant, auditor, tots dos |
| `pbl_ws` | PBL_WS | Aplicació de l'entitat E2EENT01 per al recobriment (padró amb E2EPROC01) |

Les APIs REST (`goib-ws`, secret `integracio-goib-ws`) accepten basic auth amb aquests usuaris.

## Configuració SCSP

- Certificats: el keystore `interoperabilitat.jks` renovat (el de PINBAL i dels fakes; el de la
  imatge d'EMISERV ha caducat), contrasenya `tecnologies`. PINBAL signa amb `limit_pinbal` i els
  EMISERV responen amb `limit_emiserv`. Sense @firma; els mòduls `ValidarCertificado` i
  `AutorizacionPeticion` estan desactivats (els valors per defecte).
- EMISERV central (`liquibase/sql/emiserv-central*.sql`): els mateixos serveis, tipus i
  configuració que l'EMISERV de proves (proves.caib.es), amb els destins simulats:

  | Servei | Tipus | Configuració |
  |---|---|---|
  | `SCDCPAJU`, `SCDHPAJU` | Enrutador | Per municipi (`EmpadronamentEntitatResolver`): `07040` → EMISERV cedent, `07026` → emissor simulat, `07011` → backoffice simulat amb connexió directa, `07033` → error |
  | `SVDSCTFNWS01` | Enrutador múltiple | `FamiliaNombrosaResponseResolver`: `MALLORCA` → emissor amb títol, `MENORCA` → emissor sense dades, `EIVISSA` → backoffice directe |
  | `SCMCEDU`, `SVDCCAAIRPHWS01` | Backoffice | Sense autenticació |
  | `SVDCCAACPASWS01`, `SVDCCAACPCWS01`, `SVDNIVCAT`, `SVDSCDDWS01`, `SVDSCDDWS02` | Backoffice | Autenticació en text clar (`ES_CAIB_EMISERV_BACKOFFICE_CAIB_AUTH_*` al compose; el fake no la comprova) |
  | `SVDSCDWS01` | Backoffice | Asíncrones processades com a síncrones al cap de 60 s |
  | `SVDSCDDWS01` | Backoffice | A més, dades específiques xifrades amb WS-Security (`liquibase/sql/*-xifrat.sql`): PINBAL xifra el `DatosEspecificos` de la petició amb el certificat `limit_emiserv` i l'EMISERV xifra el de la resposta amb `limit_pinbal`. Parts a xifrar en el format de WSS4J (`{Content}{namespace}DatosEspecificos`) |

- EMISERV cedent (`liquibase/sql/emiserv-cedent*.sql`): `SCDCPAJU` i `SCDHPAJU` backoffice (simulat).
- PINBAL (`liquibase/sql/pinbal*.sql`, sobre les dades `e2e` de PINBAL): tots aquests serveis
  tenen la CAIB com a emissor, a l'entitat E2E Principal i al procediment E2EPROC01 (permís per a
  `pbl_delegat` i `pbl_ws`). Els enrutadors van a `/emiservback/scspRouting/...` i els
  backoffices a `/emiservapi/interna/scsp/...`, com a producció. Els padrons i la família
  nombrosa tenen els camps de dades específiques de producció. Les respostes només es validen amb
  l'XSD per als padrons: el fake respon la resta de backoffices amb dades genèriques. La resta de
  serveis de prova de PINBAL (`Q2827003ATGSS001`, `SVDDGPCIWS02`, `SVDDGTVEHICULOSANCWS01`) van a
  `pinbal-scsp-fake`.

## Serveis en mode fake

L'EMISERV central té `es.caib.emiserv.fake.permes=true` (compose): el formulari de servei mostra
el bloc «Mode fake» i els serveis marcats com a fake responen sense contactar amb cap cedent ni
backoffice. Així es pot provar PINBAL contra EMISERV sense dependre dels destins reals.

- **Activar-ho**: a la fitxa del servei (JSP o React), bloc «Mode fake»: marcar-lo i triar el
  resultat (`OK`, `SENSE_DADES` o `ERROR`; `SENSE_DADES` només genera una resposta sense dades per a
  SVDSCTFNWS01, SCDCPAJU i SCDHPAJU: la resta de serveis necessiten una plantilla) i, opcionalment, les dades específiques de la resposta
  (plantilla amb `${codigoCertificado}`, `${idPeticion}`, `${idSolicitud}`, `${documentacion}`,
  `${nombre}`, `${apellido1}`, `${apellido2}`). Només és visible si la propietat és `true`; si es
  treu, els serveis fake deixen de ser-ho a l'execució (tornen als destins reals) però es
  conserven les dades del fake. Els serveis fake surten amb l'etiqueta FAKE als llistats.
- **Backoffices** (i rutes BACKOFFICE, i peticions asíncrones): un backoffice en procés respon i la
  resposta passa per scsp-core (signada i auditada). **Enrutadors**: les destinacions externes es
  reescriuen a `fake:<servei>/<entitat|->/` i l'enrutador es crida a si mateix a
  `/emiservback/scspFake/<servei>/<entitat|->/`, que respon signat amb la clau de signatura
  **real** del servei (la d'`core_servicio`). Aquest endpoint no té autenticació però només
  contesta peticions loopback (127.0.0.0/8 o `::1`); qualsevol altra adreça rep un 404.
  Les peticions **asíncrones** (`peticionAsincrona`) a un enrutador que no és `asyncSincron`
  reben un SOAP Fault `0502` (`[FAKE] Les peticions asíncrones només s'admeten amb asyncSincron`).
- **Auditoria**: les respostes correctes surten amb estat `0003` i el literal prefixat amb `[FAKE] `.
- **Errors (`ERROR`)**: els enrutadors responen un SOAP Fault amb codi `0502` i literal
  `[FAKE] Error simulat`; els backoffices llancen una excepció que SCSP embolcalla i que arriba
  al sol·licitant com a **`0227`** (literal `Error al generar la respuesta. BackofficeException: ... [FAKE] Error simulat`), no `0502`.
- **Limitacions conegudes**: la URL fake és `http://localhost:<port local>` (el port local del
  contenidor, 8080 aquí), i per tant necessita un connector HTTP pla en aquest port. Si es
  canvia el mode fake d'un servei amb peticions asíncrones en curs, aquestes poden quedar
  pendents.
- Els enrutadors es poden provar sense PINBAL amb les proves d'`emiserv-api-interna-scsp` (`EmisorScspPinbalTest` apuntant a `/emiservback/scspRouting`; `EmisorScspXmlSignatureTest` per als backoffices, que validen la signatura XMLSignature), signant amb `limit_pinbal`. L'auditoria de l'enrutador mostra la URL `/scspFake/<servei>/<entitat>/...`.
- **No s'ha d'activar a producció.**

## Provar una consulta

`proves/consulta.sh [servei] [municipi] [document]` fa una petició síncrona pel recobriment de
PINBAL (`/pinbalapi/interna/recobriment/v2/serveis/<servei>/peticioSincrona`) amb l'usuari
d'aplicació `pbl_ws`. Per als padrons, el municipi tria la ruta de l'EMISERV central: `040` (per
defecte, EMISERV cedent), `026`, `011` o `033`. Els fakes responen un volant de padró inventat amb
el document consultat.

```bash
proves/consulta.sh                    # SCDCPAJU: PINBAL → EMISERV central → EMISERV cedent → backoffice
proves/consulta.sh SCDHPAJU 033       # resposta d'error de l'emissor
proves/consulta.sh SVDSCTFNWS01       # enrutador múltiple: consulta els tres destins i en tria una resposta
proves/consulta.sh SVDCCAACPCWS01     # backoffice de la CAIB
proves/consulta.sh SVDSCDDWS01        # backoffice amb les dades específiques xifrades (WS-Security)
```

Les peticions dels enrutadors es veuen a l'auditoria de l'enrutador de l'EMISERV central; les dels
backoffices (i les rutes que van directament a un backoffice) a l'auditoria de backoffice. Les de
`040` també es veuen a l'auditoria de backoffice de l'EMISERV cedent. També es poden fer des de la
interfície de PINBAL amb `pbl_delegat` (procediment E2EPROC01).
