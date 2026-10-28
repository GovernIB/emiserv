# Interfície REACT d'EMISERV — Disseny

Data: 2026-10-28 · Estat: esborrany per aprovar

## 1. Intenció

Modernitzar EMISERV amb una nova interfície web React, amb el mateix model que ja s'aplica a
**Distribució** (branca `dis-dev`) i amb l'aspecte visual comú de les aplicacions del **Lot 2**, tal
com el fa servir **Comanda**. La interfície JSP actual continua funcionant. Cada usuari tria amb quina
entra.

La primera versió cobreix les pantalles que més es fan servir: **gestió de serveis** i
**auditoria** (backoffice i enrutador). A més, aprofita la migració per incorporar onze millores de cara
a l'usuari (secció 5), totes aprovades per a aquesta primera versió.

## 2. Objectius, no-objectius i anti-objectius

**Objectius**
- SPA React servida a `/emiservback/reactapp`, construïda amb base-react (`lib/`) i Lot 2 (`src/lot2/`).
- API HAL / HAL-FORMS de base-boot per als recursos d'aquestes pantalles.
- Coexistència amb la JSP:
  - preferència d'interfície per usuari, i propietat de sistema per defecte;
  - enllaços per passar d'una interfície a l'altra.
- Funciona igual en els dos desplegaments:
  - JAR Spring Boot (`emiserv-boot`);
  - EAR JBoss EAP 7.2 (`emiserv-back` WAR + `emiserv-ejb`).

**No-objectius (primera versió)**
- Migrar la resta de pantalles: entitats, aplicacions, organismes, emissors, claus, autoritats de
  certificació, mòduls i paràmetres SCSP, configuració, informes, consultes asíncrones i usuaris.
  Continuen a la JSP i es migraran en iteracions posteriors.
- Canviar l'esquema de dades, llevat de la columna de preferència d'interfície (secció 4.6).
- Retirar o modificar les JSP existents, llevat d'afegir-hi el selector d'interfície.

**Anti-objectius (seria un fracàs encara que funcionàs)**
- Que la JSP deixi de funcionar o canviï de comportament per als usuaris que la fan servir.
- Editar `src/lot2/` dins EMISERV. És una còpia mestra de base-react.
- Duplicar regles de negoci que ja existeixen als `*ServiceImpl` / helpers:
  - validacions de servei;
  - prova de rutes;
  - càlcul de «configurat»;
  - resolució de credencials.

  Els recursos nous les han de reutilitzar.
- Tokens OAuth gestionats pel navegador. L'autenticació és la sessió de servidor de la JSP.
- Que un usuari amb `EMS_RESP` pugui fer o veure a la React més del que pot a la JSP.

## 3. Referències (llegir abans de planificar)

| Què | On |
| --- | --- |
| Model d'integració complet (backend + SPA + EJB) | `~/git/distribucio`, branca `dis-dev` |
| Còpia de base-boot dins l'aplicació | `distribucio-backend` (`back.base.*`), `distribucio-service` (`logic.base.*`), `distribucio-service-intf` (`logic.intf.base.*`), `distribucio-persistence` (`persist.base.*`) |
| Recursos i serveis de recurs | `distribucio-service-intf/.../logic/intf/model/*Resource.java`, `distribucio-service/.../logic/service/*ResourceServiceImpl`, `distribucio-ejb/.../ejb/resourceService/*ResourceServiceEjb` |
| Servir el SPA | `distribucio-back/.../controller/ReactController.java` (+ `BaseUtilsController`: `/sysenv`, `/manifest`, ...) |
| Build del SPA | `distribucio-back/pom.xml`, perfil `front` (`frontend-maven-plugin`, còpia a `src/main/webapp/reactapp`, `webResources`, `maven-clean-plugin`) |
| SPA | `distribucio-back/src/main/reactapp/distribucio-back` (`vite.config.ts`, `package.json`, `src/components/*`) |
| Autenticació per sessió | `DistribucioAuthProvider.tsx` + `SessioUsuariController` |
| Interfície clàssica / preferència | `InterficieClassica.tsx`, `UserProfile.tsx`, `InterficieUsuariEnumDto`, `UsuariEntity.interficieUsuari`, `versions/1.1.1/dis_usuari_interficie.sql`, `decorators/default.jsp` |
| Navegació anterior/següent | `RecordNavigation.tsx` (Distribució) |
| Reordenació per arrossegament | `@dnd-kit/core` (ja és dependència de Distribució) |
| Ús de `lib/` + `src/lot2/` | `~/git/comanda/comanda-ms-visualitzacio/src/main/jsapp/comanda-visualitzacio` (`src/lot2/README.md`) |
| Font de `lib/` i `src/lot2/` | `~/git/base-react` (`lib/`, `src/lot2/`) |
| Font de base-boot | `~/git/base-boot` (`doc/*.md`) |

## 4. Arquitectura (decidit)

### 4.1 Mòduls

- **Nou mòdul `emiserv-backend`.** Capa REST de base-boot, copiada i adaptada a `es.caib.emiserv.back.base.*` com a
  `distribucio-backend`. Afegir-lo al `pom.xml` arrel.
- **Bases de lògica, contracte i persistència** copiades, com a Distribució, a:
  - `es.caib.emiserv.logic.base.*` (`emiserv-service`);
  - `es.caib.emiserv.logic.intf.base.*` (`emiserv-service-intf`);
  - `es.caib.emiserv.persist.base.*` (`emiserv-persistence`).

  Les classes copiades són les de la **darrera versió de base-boot** (`~/git/base-boot`), que són les
  més actualitzades. La còpia adaptada de Distribució serveix només de referència per saber com s'han
  adaptat a JBoss, als EJB i a l'empaquetat de l'aplicació. Les adaptacions es tornen a aplicar sobre
  les classes de base-boot. *(decisió de l'usuari)*
- **Recursos nous** a `emiserv-service-intf` (`logic.intf.model`). Serveis de recurs a `emiserv-service`.
  Wrappers EJB a `emiserv-ejb` (`ejb.resourceService`), i entrades a `EjbClientConfig` del WAR, com a la
  resta de serveis.
- **Controladors REST** dels recursos sota `/emiservback/api/...`, al mòdul que els serveixi en els dos
  desplegaments. Pot ser `emiserv-controller`, que ja comparteixen Boot i WAR, o `emiserv-backend`.
  La tria és del constructor. Condició: un sol codi per als dos desplegaments.
- **Spring Boot.** Es puja el parent de 2.6.6 a la **darrera 2.7.x** (2.7.18, la mateixa que base-boot)
  *(decisió de l'usuari)*. Cal verificar que continuen arrencant:
  - la JSP;
  - les API (`api-interna`, `api-interna-scsp` amb AspectJ, `api-externa`);
  - el desplegament EAR.

### 4.2 SPA

- Ubicació: `emiserv-back/src/main/reactapp/emiserv-back`.
- Mateixa pila i versions que el `package.json` de Distribució: React 19, MUI 7, MUI X Pro 8, Vite 7,
  `ketting` 7.5.2 amb el pedaç de `patches/`, i `react-router-dom` 7.
- Dues carpetes copiades:
  - `lib/`: còpia de la darrera `~/git/base-react/lib`, com a Comanda;
  - `src/lot2/`: còpia **sencera i sense edicions** de `~/git/base-react/src/lot2`, segons les regles de
    `src/lot2/README.md`.
- Àlies `reactlib` → `lib/main` (tsconfig + vite).
- Marc d'aplicació: `Lot2BaseApp` amb:
  - els colors i logos d'EMISERV, passats per paràmetre (`createLot2Themes(colors)`);
  - el peu `DrassanaFooter`;
  - les traduccions `lot2Translations` registrades;
  - pantalles amb `ScreenFrame`, `ScreenTitle` i `lot2ScreenGridProps(theme)` a la graella principal.
- Idiomes: català i castellà, com la JSP.
- `vite.config.ts` equivalent al de Distribució:
  - `base=/emiservback/reactapp`;
  - proxy de `/emiservback/sysenv` i `/emiservback/api` cap al backend;
  - `devManifest`;
  - `fixBackendEndpointsBase`.

### 4.3 Servir el SPA i build

- `ReactController` equivalent al de Distribució, mapejat a `/reactapp/**`:
  - torna el fitxer estàtic si existeix;
  - si no, l'`index.html`;
  - un 404 per als estàtics inexistents.
- Endpoints `/sysenv` i `/manifest` (equivalents a `BaseUtilsController`).
- `emiserv-back/pom.xml`: perfil `front` amb `frontend-maven-plugin`, `npm run build-jboss`, `webResources` → `reactapp`,
  còpia a `src/main/webapp/reactapp` (a `.gitignore`) i neteja amb `maven-clean-plugin`.
- `emiserv-boot` ha de servir també el SPA a `/emiservback/reactapp`. El mecanisme d'empaquetat és del
  constructor.
- La propietat `frontend.skip` permet compilar sense Node.

### 4.4 Autenticació i rols

- Sessió de servidor compartida amb la JSP, com a `DistribucioAuthProvider`:
  - **JBoss:** adaptador Keycloak + `J2eePreAuthenticatedProcessingFilter` (ja existent a `war/config/SecurityConfig`);
  - **Boot:** `KeycloakWebSecurityConfigurerAdapter` (ja existent a `emiserv-boot/config/SecurityConfig`).
- Endpoint `api/sessioUsuari` amb `codi`, `nom`, `email`, `rols` i `interficie`.
  - Si no hi ha sessió, la SPA va al login i després torna a la mateixa URL.
  - La SPA no consulta la sessió periòdicament. Ho fa en tornar a la pestanya i amb l'activitat de
    l'usuari (com a màxim un cop per minut), com a Distribució.
- Rols: `EMS_ADMIN` i `EMS_RESP`. Cada recurs i cada acció aplica **exactament** els mateixos permisos
  que el controlador JSP equivalent:
  - `RolHelper.isUsuariActualAdministrador`;
  - filtratge de serveis per permís, a `ServeiServiceImpl` i a `AplicacioService`.

  El constructor els ha d'inventariar controlador per controlador abans d'implementar.

### 4.5 Navegació i menú

- Menú React:
  - **Serveis**;
  - **Auditoria** → Backoffice, Enrutador;
  - **Interfície clàssica**, que porta a la JSP equivalent a la pantalla actual quan n'hi ha.
- El menú React **no** enllaça les pantalles JSP no migrades. La React no es posarà en producció fins
  que s'hagin migrat totes les pàgines, en iteracions posteriors *(decisió de l'usuari)*.
- JSP: al menú d'usuari (`decorators`), opció «Nova interfície», que porta a la ruta React equivalent.

### 4.6 Preferència d'interfície

- Columna `ems_usuari.interficie` (`VARCHAR(5)`, `JSP`|`REACT`, nul = mana la propietat):
  - canvi de Liquibase a `db/changelog/changes/`;
  - scripts a `versions/2.0.7/{oracle,postgresql}`.
- Propietat `es.caib.emiserv.interface.defecte` (valor per defecte `JSP` fins que la React cobreixi tot l'ús
  diari). La configuració ha de ser coherent amb com EMISERV defineix les propietats a `ems_config`.
- La preferència es pot canviar des del perfil d'usuari JSP (`usuariForm.jsp`) i des del perfil React.
- En entrar a `/emiservback/`, `IndexController` redirigeix a la React si la interfície efectiva és `REACT`.

## 5. Pantalles i comportament

Tots els textos es basen en les claus actuals de `messages_emiserv_{ca,es}.properties` quan n'hi ha.

### 5.1 Serveis — llistat (`/serveis`)

Rèplica de `serveiList.jsp` + millores 2 i 5.

- **Columnes:** codi, nom, tipus i, només per a administradors, actiu, nombre de permisos i nombre de rutes.
- **Estat:**
  - xip actiu/inactiu;
  - avís de «no configurat» amb el motiu concret (millora 2), *aprovat per l'usuari*.

    Avui no existeix cap càlcul del motiu. `configurat` només indica, als serveis backoffice, que s'ha
    desat la configuració SCSP, i als enrutadors sempre val `true`. Per això es crea un **mètode de
    diagnòstic nou** a la capa de servei (`ServeiServiceImpl` o un helper), que torna la llista de motius
    d'un servei:

    | Motiu | Quan | Pestanya |
    | --- | --- | --- |
    | Falta la configuració SCSP | no hi ha `CORE_SERVICIO` (`isConfiguratScsp` = false) | Configuració SCSP |
    | Falta l'emissor | configuració SCSP sense emissor | Configuració SCSP |
    | Enrutador sense rutes | servei `ENRUTADOR` / `ENRUTADOR_MULTIPLE` sense cap `ServeiRutaDesti` | Rutes |
    | Ruta BACKOFFICE sense credencials | ruta de tipus `BACKOFFICE` amb autenticació que requereix credencials no configurades | Rutes |

    - El diagnòstic té tests unitaris.
    - Clicar l'avís obre la fitxa a la pestanya del primer motiu.
    - Només es mostra a la React. La JSP i el camp `configurat` no canvien, encara que la React avisi de
      casos que la JSP no marca.
- **Filtres:**
  - filtre ràpid (codi/nom);
  - filtre plegable (codi, nom, tipus, actiu), persistit per usuari (`usePersistentState` de base-react);
  - `FilterCount`.
- **Accions** (administrador): nou, activar/desactivar, esborrar amb confirmació, exportar a CSV/Excel.
  Clicar una fila obre la fitxa.

### 5.2 Serveis — fitxa (`/serveis/:id`, `/serveis/nou`)

Millores 1 i 3. Pestanyes (només les aplicables al tipus i al rol):

1. **Dades.** Camps de `serveiForm.jsp`. Mostra només els camps del tipus triat (`BACKOFFICE` o
   `ENRUTADOR` / `ENRUTADOR_MULTIPLE`), via `onChange` de base-boot. Validació en línia (HTTP 422).
2. **Configuració SCSP.** Camps de `serveiConfigScsp.jsp`. També per als enrutadors, com ara.
3. **Rutes** (enrutadors). Millora 4:
   - graella de rutes amb alta, edició i esborrat;
   - **reordenació per arrossegament**, que persisteix l'ordre;
   - **Provar** a cada fila, amb el resultat en línia;
   - **Provar-les totes**.

   La prova reutilitza la lògica del `provar` actual.
4. **XSD** (backoffice amb `xsdGestioActiva`): llistat, pujada, descàrrega i esborrat (`serveiXsd.jsp`).
5. **Permisos** (administrador): `serveiPermis*.jsp`.
6. **Autoritzacions** (administrador, només si `configuratScsp`): `serveiAutoritzacio*.jsp`.
7. **Darreres peticions** (millora 11). Les N darreres peticions auditades del servei (backoffice o
   enrutador, segons el tipus), amb enllaç al detall i a l'auditoria filtrada pel servei.

### 5.3 Auditoria — llistats (`/auditoria/backoffice`, `/auditoria/enrutador`)

Rèplica de `auditoriaBackofficeList.jsp` / `auditoriaEnrutadorList.jsp` + millores 6, 7 i 10.

- **Filtres actuals:** servei, procediment, número de petició, estat, dates inici/fi, document i nom del
  funcionari.
- **Dreceres de dates:** avui, darrers 7 dies i aquest mes (millora 6).
- **Estats** amb color i llegenda (millora 7). Hi surten tots els estats que poden aparèixer a les dades:
  els de `PeticioEstatEnumDto`, inclòs `EN_PROCES`, i `ERROR_EMISOR`, que l'enrutador ja tracta *(decisió del
  dissenyador)*.
- **Errors** (SCSP / backoffice): resum a la fila i text complet en desplegar-la.
- **Columnes:** les mateixes que avui, inclosos:
  - el percentatge processat i síncrona/asíncrona (backoffice);
  - entitat, tipus de servei i respostes (enrutador).
- **Servei:** enllaç a la fitxa del servei (millora 11).
- **Exportació:** CSV/Excel del llistat filtrat (millora 10), *aprovada per l'usuari*.
  - Es genera **al servidor**, de manera síncrona, amb el mateix filtre i ordre que la graella. Per
    exemple, amb l'exportació/informe de base-boot. La graella MUI X Pro només exporta CSV de la pàgina
    carregada.
  - Màxim configurable de files: propietat `es.caib.emiserv.auditoria.exportacio.max` (per defecte 10.000).
  - Si el filtre supera el màxim, no s'exporta i s'avisa l'usuari perquè el restringeixi (per exemple,
    amb un rang de dates).
  - El mateix mecanisme serveix per a l'exportació del llistat de serveis.
- Les consultes han de mantenir el rendiment actual sobre taules grans. Paginació i ordenació es fan al
  servidor i no es fa cap `count` addicional innecessari.

### 5.4 Auditoria — detall (`/auditoria/backoffice/:id`, `/auditoria/enrutador/:id`)

Millores 8 i 9.

- Pantalla pròpia amb les dades de capçalera dels JSP de detall actuals.
- Subgraella de sol·licituds, amb el detall de cada sol·licitud: `auditoriaBackofficeSolicitudDetall.jsp`.
- Navegació **anterior/següent** dins dels resultats del llistat d'origen, amb el mateix filtre i ordre.
- **Visor XML** per a cada missatge que avui es pot veure:
  - petició / resposta SCSP;
  - petició / resposta;
  - petició / resposta backoffice;
  - petició REST;
  - respostes (enrutador);
  - error de comunicació.

  El visor:
  - carrega el missatge en obrir-lo;
  - formata i ressalta la sintaxi;
  - permet plegar i desplegar nodes;
  - permet copiar i descarregar;
  - ofereix una vista **petició | resposta** costat a costat.

## 6. Restriccions

- Java 11. Ha de compilar i desplegar a JBoss EAP 7.2 (EAR) i com a JAR Spring Boot.
- BD: Oracle (principal), PostgreSQL, HSQLDB (dev).
- Les taules i la lògica SCSP (`scsp-core`) no es modifiquen.
- `src/lot2/` no s'edita. Si cal un canvi, es proposa a base-react.
- Comentaris i identificadors en català, com la resta del projecte.
- Tests desactivats per defecte (`skipTests`). Els tests nous s'executen amb `-DskipTests=false`.

## 7. Decisions del constructor

- Nom exacte, camps i perspectives de cada recurs. Per exemple `ServeiResource`, `ServeiRutaResource`,
  `ServeiXsdResource`, `ServeiPermisResource`, `ServeiAutoritzacioResource`, `PeticioBackofficeResource`,
  `SolicitudBackofficeResource`, `PeticioEnrutadorResource`. Són recursos de BD o «NoDatabase» que
  delegen als serveis existents, a criteri del constructor, sempre que es reutilitzi la lògica actual.
- Llibreria del visor XML i del ressaltat, dins les dependències que accepta la pila.
- Colors i logos d'EMISERV per a `createLot2Themes`. Es prenen del tema actual de la JSP.
- Ordre de les fases d'implementació. Es recomana començar per la infraestructura i una pantalla de
  punta a punta (llistat de serveis) als dos desplegaments.
- Valor N de «darreres peticions» (suggeriment: 20).

## 8. Criteris d'acceptació

- `mvn clean install` (amb el perfil `front`) genera el JAR de `emiserv-boot` i l'EAR. Cap dels dos
  necessita passos manuals.
- Al JAR i a l'EAR desplegat, un usuari:
  - entra a la React si la seva preferència o la propietat ho indica;
  - canvia d'interfície en els dos sentits;
  - completa totes les operacions de les seccions 5.1–5.4 amb els permisos del seu rol.
- La JSP continua fent exactament el mateix que abans.
- `npm run lint` i `tsc` sense errors a la SPA.
