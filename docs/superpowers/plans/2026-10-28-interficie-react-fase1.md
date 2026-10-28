# Interfície REACT d'EMISERV — Fase 1: infraestructura + llistat de serveis

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Tenir la nova interfície React d'EMISERV funcionant de punta a punta (JAR Spring Boot i EAR JBoss), amb la preferència d'interfície i la pantalla de llistat de serveis completa (millores 2 i 5 i exportació).

**Architecture:** Les classes de base-boot 4.0.104 (`~/git/base-boot`) es copien als paquets `es.caib.emiserv.*.base` dels mòduls existents. La capa REST va a un nou mòdul `emiserv-backend`. Els recursos es mapen sobre les taules `ems_` amb entitats pròpies (`persist.resourceentity`), com a Distribució, i deleguen les operacions de negoci als `*ServiceImpl` existents. La SPA (Vite + React 19 + MUI 7, `lib/` de base-react i `src/lot2/`) viu a `emiserv-back/src/main/reactapp/emiserv-back`, es construeix amb el perfil Maven `front` i se serveix a `/emiservback/reactapp`.

**Tech Stack:** Java 11, Spring Boot 2.7.18, Spring HATEOAS (HAL-FORMS), Spring Data JPA, spring-filter, JasperReports, EJB 3.2 (JBoss EAP 7.2), Liquibase, React 19, MUI 7 / MUI X Pro 8, Vite 7, ketting 7.5.2.

**Spec:** `docs/superpowers/specs/2026-10-28-interficie-react-design.md`

**Plans següents** (s'escriuran quan aquesta fase estigui acabada, perquè depenen de les interfícies reals que en surtin):
- Fase 2: fitxa de servei (pestanyes Dades, Configuració SCSP, Rutes amb arrossegament i prova, XSD, Permisos, Autoritzacions, Darreres peticions). Spec §5.2.
- Fase 3: auditoria backoffice i enrutador (llistats, detall amb anterior/següent, visor XML, exportació). Spec §5.3–5.4.

## Global Constraints

- Java 11. Spring Boot parent **2.7.18** (la darrera 2.7.x).
- Ha de funcionar amb el JAR (`emiserv-boot`) i amb l'EAR (`emiserv-ear`, JBoss EAP 7.2), amb el mateix codi.
- BD: Oracle, PostgreSQL i HSQLDB. Els tipus nous han de funcionar a les tres.
- Les classes base es copien de `~/git/base-boot` (`es.limit.base.boot.X.base.*`) i es renomenen a `es.caib.emiserv.X.base.*`. La còpia de Distribució (`~/git/distribucio`, branca `dis-dev`) només serveix de referència per a les adaptacions a JBoss, EJB i empaquetat.
- `src/lot2/` és una còpia **sencera i sense edicions** de `~/git/base-react/src/lot2`. `lib/` és una còpia de `~/git/base-react/lib`.
- La JSP no pot canviar de comportament. Només s'hi afegeixen el camp de preferència al perfil i l'opció «Nova interfície» al menú d'usuari.
- Els permisos han de ser idèntics als de la JSP. Amb `EMS_ADMIN` es veuen tots els serveis; amb `EMS_RESP`, només els que tenen permís ACL `ADMINISTRATION` (`ServeiServiceImpl.findAllPaginat`). Crear, activar, desactivar i esborrar requereix `EMS_ADMIN`.
- Propietats noves:
  - `es.caib.emiserv.interface.defecte` (`JSP` | `REACT`, per defecte `JSP`);
  - `es.caib.emiserv.exportacio.max` (per defecte `10000`).
- Comentaris i identificadors en català. Textos a `ca` i `es`.
- Tests desactivats per defecte. S'executen amb `-DskipTests=false`.
- Commits petits, un per tasca, amb el format de missatge del repositori.

## Review Focus

1. **Usuari `EMS_RESP` sense cap servei permès.** Ha de veure una graella buida i no un 500. `findAllPaginat` avui torna una pàgina buida si la llista està buida, i un `IN ()` buit peta a Oracle. Test a la Task 8.
2. **Exportació que supera el màxim.** Amb més files que `es.caib.emiserv.exportacio.max` no es genera el fitxer i es torna un error 4xx amb un missatge traduït, no un timeout ni un fitxer truncat. Test a la Task 9.
3. **Sessió caducada amb la SPA oberta.** La pròxima crida a l'API no ha de mostrar un error genèric: s'ha de redirigir al login i tornar a la mateixa URL. Verificació manual a la Task 11.
4. **Esborrar un servei amb rutes, permisos o peticions auditades.** S'ha de propagar l'error de negoci de `ServeiService.delete` com a missatge a l'usuari, igual que la JSP, i no com un error tècnic. Test a la Task 8.
5. **Usuari sense fila a `ems_usuari`** (primer accés) o amb `interficie` nul·la. Mana la propietat de sistema, i en desar la preferència es crea o s'actualitza la fila. Test a la Task 5.

---

### Task 1: Pujar Spring Boot a 2.7.18

**Files:**
- Modify: `pom.xml` (parent `spring-boot-starter-parent` 2.6.6 → 2.7.18, propietat `spring-boot.version`)
- Modify: els `pom.xml` de mòduls amb versions fixades que entrin en conflicte (Spring Security, Hibernate, Jackson, springdoc), només si la compilació ho exigeix

- [ ] **Step 1:** Canviar la versió del parent i de `spring-boot.version` a `2.7.18`.
- [ ] **Step 2:** Run: `mvn clean install -DskipTests` → Expected: BUILD SUCCESS a tots els mòduls. Corregir les incompatibilitats de compilació sense canviar comportament:
  - `WebSecurityConfigurerAdapter` continua existint a la 2.7 (deprecated): no s'ha de migrar;
  - l'adaptador de Keycloak continua funcionant.
- [ ] **Step 3:** Arrencar `emiserv-boot` (`mvn spring-boot:run -pl emiserv-boot -P hsql`) → Expected: arrenca. `GET /emiservback/servei` amb sessió mostra la JSP de serveis. `GET /emiservapi/interna/...` respon.
- [ ] **Step 4:** Desplegar l'EAR a JBoss EAP 7.2 local → Expected: desplega sense errors. La JSP de serveis i d'auditoria funciona, i `api-interna-scsp` (AspectJ) arrenca.
- [ ] **Step 5:** Commit `Actualitzar Spring Boot a 2.7.18`.

### Task 2: Copiar les bases de base-boot (contracte, persistència i lògica)

**Files:**
- Create: `emiserv-service-intf/src/main/java/es/caib/emiserv/logic/intf/base/**` ← `base-boot-logic-intf/.../logic/intf/base/**`
- Create: `emiserv-persistence/src/main/java/es/caib/emiserv/persist/base/**` ← `base-boot-persist/.../persist/base/**`
- Create: `emiserv-service/src/main/java/es/caib/emiserv/logic/base/**` ← `base-boot-logic/.../logic/base/**`
- Modify: els `pom.xml` d'aquests tres mòduls. S'afegeixen les dependències dels poms de base-boot corresponents (`spring-security-acl`, `spring-filter`, `jasperreports`, `dynamicreports-core`...), amb les versions de `~/git/base-boot/pom.xml`.
- Modify: `emiserv-service-intf/.../logic/intf/base/config/BaseBootConfig.java`. Valors: `BASE_PACKAGE = "es.caib.emiserv"`, `APP_NAME = "emiserv"`.

**Interfaces:**
- Produces: `es.caib.emiserv.logic.intf.base.model.BaseResource<ID>`, `ResourceReference`, `@ResourceConfig`, `@ResourceField`, `ReadonlyResourceService<R,ID>`, `MutableResourceService<R,ID>`, `es.caib.emiserv.persist.base.entity.ResourceEntity<R,PK>`, `es.caib.emiserv.persist.base.repository.BaseRepository<E,PK>`, `es.caib.emiserv.logic.base.service.BaseReadonlyResourceService<R,ID,E>`, `BaseMutableResourceService<R,ID,E>`.

- [ ] **Step 1:** Copiar els fitxers i substituir `es.limit.base.boot` → `es.caib.emiserv` (paquets i imports).
- [ ] **Step 2:** Aplicar l'adaptació de Distribució a `PropertyConfig`. `PROPERTY_PREFIX = BASE_PACKAGE + "."` (fa que les propietats siguin `es.caib.emiserv.*`) i s'hi afegeixen `PROPERTY_PREFIX_FRONT`, `PROP_FRONT_*`, `REACT_APP_PROPS_MAP` i `VITE_PROPS_MAP`. Referència: `distribucio-service-intf/.../logic/intf/base/config/PropertyConfig.java`.
- [ ] **Step 3:** Verificar que la configuració de persistència de base-boot no crea un segon `EntityManagerFactory` ni `TransactionManager`. Ha de reutilitzar els existents d'EMISERV: la configuració JPA de `emiserv-persistence` amb els paquets d'escaneig ampliats a `persist.base` i `persist.resourceentity`. Referència: `distribucio-persistence/.../persist/config`.
- [ ] **Step 4:** Run: `mvn clean install -DskipTests` → Expected: BUILD SUCCESS. `emiserv-boot` arrenca i la JSP funciona igual.
- [ ] **Step 5:** Commit `Incorporar les classes base de base-boot`.

### Task 3: Mòdul `emiserv-backend` (capa REST de base-boot)

**Files:**
- Create: `emiserv-backend/pom.xml`. Dependències de `base-boot-back` (veure `distribucio-backend/pom.xml`) + `emiserv-service-intf`.
- Create: `emiserv-backend/src/main/java/es/caib/emiserv/back/base/**` ← `base-boot-back/.../back/base/**`
- Create: `emiserv-backend/src/main/java/org/springframework/hateoas/mediatype/hal/forms/{CustomHalFormsPropertyFactory,CustomHalFormsTemplateBuilder}.java` (còpia)
- Modify: `pom.xml` arrel (`<module>emiserv-backend</module>`); `emiserv-controller/pom.xml` (dependència a `emiserv-backend`)
- Modify: `emiserv-back/.../war/config/SecurityConfig.java` i `emiserv-boot/.../config/SecurityConfig.java`. `/api/**` exigeix autenticació amb la mateixa sessió. Les respostes 401 són JSON i no redirigeixen al login (`AuthenticationEntryPoint` per a `/api/**`).
- Create: `emiserv-backend/src/test/java/es/caib/emiserv/back/base/ApiIndexTest.java`

**Interfaces:**
- Produces: l'arrel `GET /emiservback/api` amb l'índex HAL dels recursos; `BaseMutableResourceController<R,ID>` i `BaseReadonlyResourceController<R,ID>` per estendre. Base path de l'API: `/api` dins del context `/emiservback`.

- [ ] **Step 1: Test** `ApiIndexTest.indexRetornaEnllacos`: amb `@WebMvcTest` + usuari `EMS_ADMIN`, `GET /api` → `200`, `Content-Type` `application/hal+json` (o `prs.hal-forms+json`) i `_links.self` present. `indexSenseSessioRetorna401`: sense usuari → `401`.
- [ ] **Step 2:** Run: `mvn -pl emiserv-backend test -DskipTests=false -Dtest=ApiIndexTest` → FAIL.
- [ ] **Step 3:** Copiar les classes, renomenar els paquets i ajustar `BaseWebSecurityConfig`. Les apps ja tenen seguretat pròpia: la de base-boot no ha de registrar cap segon `SecurityFilterChain` que entri en conflicte (referència: `distribucio-backend/.../BaseWebSecurityConfig.java`).
- [ ] **Step 4:** Run el test → PASS. `mvn clean install -DskipTests` → BUILD SUCCESS.
- [ ] **Step 5:** Commit `Afegir el mòdul emiserv-backend amb la capa REST de base-boot`.

### Task 4: EJB per als serveis de recurs

**Files:**
- Create: `emiserv-ejb/src/main/java/es/caib/emiserv/ejb/base/{AbstractServiceEjb,ResourceApiServiceEjb,PermissionEvaluatorServiceEjb}.java`. Referència: `distribucio-ejb/.../ejb/base/`, adaptats a `EjbContextConfig` d'EMISERV.
- Modify: `emiserv-back/.../war/config/EjbClientConfig.java`. Beans `resourceApiService` i `permissionEvaluatorService`. Cada `*ResourceService` nou hi afegirà el seu bean.

**Interfaces:**
- Produces: patró de wrapper per a cada servei de recurs, `es.caib.emiserv.ejb.resourceService.<Nom>ResourceServiceEjb extends AbstractServiceEjb<<Nom>ResourceService> implements <Nom>ResourceService`. Porta `@Stateless`, `@RolesAllowed("**")`, `@Delegate` al servei, i sobreescriu explícitament els mètodes genèrics `artifactActionExec`, `artifactOnChange` i `artifactReportGenerateData` (vegeu `AlertaResourceServiceEjb` de Distribució).

- [ ] **Step 1:** Crear les classes i els beans del client EJB.
- [ ] **Step 2:** Run: `mvn clean install -DskipTests` → BUILD SUCCESS. Desplegar l'EAR → `GET /emiservback/api` amb sessió → 200.
- [ ] **Step 3:** Commit `Afegir els EJB de l'API de recursos`.

### Task 5: Preferència d'interfície

**Files:**
- Create: `emiserv-persistence/src/main/resources/db/changelog/changes/2.0.7/ems_usuari_interficie.yaml`. Columna `interficie` `VARCHAR(5)`, nul·lable, a `ems_usuari`.
- Create: `versions/2.0.7/oracle/03_ems_usuari_interficie.sql` i `versions/2.0.7/postgresql/03_ems_usuari_interficie.sql`. Mateix canvi + la propietat a la taula de configuració, si EMISERV defineix les propietats a `ems_config` (comprovar-ho a `initial_data.yaml`).
- Create: `emiserv-service-intf/.../logic/intf/dto/InterficieUsuariEnumDto.java` (`JSP`, `REACT`)
- Modify: `UsuariEntity` (camp `interficie`), `UsuariDto`, `AplicacioService`/`AplicacioServiceImpl` (+ EJB `AplicacioService`)
- Modify: `emiserv-controller/.../controller/{UsuariController,IndexController}.java`, `command/UsuariCommand.java`, `jsp/usuariForm.jsp`, el decorator JSP del menú d'usuari, i `messages_emiserv_{ca,es}.properties`
- Test: `emiserv-service/src/test/java/es/caib/emiserv/logic/service/AplicacioServiceInterficieTest.java`

**Interfaces:**
- Produces: `InterficieUsuariEnumDto AplicacioService.getInterficieEfectiva()` (la preferència de l'usuari actual o, si és nul·la, la propietat `es.caib.emiserv.interface.defecte`, per defecte `JSP`). També `void AplicacioService.updateInterficie(InterficieUsuariEnumDto interficie)`, on `null` esborra la preferència.

- [ ] **Step 1: Tests:**
  - `senseFilaUsuariManaLaPropietat`: usuari nou i propietat `REACT` → `REACT`;
  - `interficieNullaManaLaPropietat`: propietat absent → `JSP`;
  - `preferenciaUsuariGuanya`: preferència `JSP` amb propietat `REACT` → `JSP`;
  - `updateInterficieCreaFila`: després de `updateInterficie(REACT)` sense fila prèvia, `getInterficieEfectiva()` → `REACT`.
- [ ] **Step 2:** Run: `mvn -pl emiserv-service test -DskipTests=false -Dtest=AplicacioServiceInterficieTest` → FAIL.
- [ ] **Step 3:** Implementar la columna, l'enum, l'entitat, el DTO i els dos mètodes (+ EJB).
- [ ] **Step 4:** Run tests → PASS.
- [ ] **Step 5:** JSP:
  - Camp «Interfície» al perfil (`usuariForm.jsp`), amb les opcions per defecte, JSP i React.
  - Opció «Nova interfície» al menú d'usuari, que porta a `/emiservback/reactapp`.
  - `IndexController`: a `GET /`, si `getInterficieEfectiva()==REACT`, redirigeix a `/reactapp`.
- [ ] **Step 6:** Verificació manual a `emiserv-boot`: amb la preferència React, entrar a `/emiservback/` porta a `/emiservback/reactapp`. Amb la JSP, tot continua igual.
- [ ] **Step 7:** Commit `Preferència d'interfície (JSP o REACT) per usuari`.

### Task 6: Endpoints de sessió i servei del SPA

**Files:**
- Create: `emiserv-controller/src/main/java/es/caib/emiserv/back/controller/ReactController.java`. Còpia adaptada de Distribució:
  - `@RequestMapping("/reactapp/**")`;
  - estén `BaseUtilsController`;
  - `getAuthToken()` torna `null` (autenticació per sessió).
- Create: `emiserv-controller/src/main/java/es/caib/emiserv/back/controller/SessioUsuariController.java`
- Test: `emiserv-controller/src/test/java/es/caib/emiserv/back/controller/SessioUsuariControllerTest.java`

**Interfaces:**
- Produces:
  - `GET /api/sessioUsuari` → `{ codi: string, nom?: string, email?: string, rols: string[], interficie: "JSP"|"REACT" }`. `rols` ⊆ {`EMS_ADMIN`, `EMS_RESP`}. Sense sessió torna `401`.
  - `PUT /api/sessioUsuari/interficie` amb `{ interficie: "JSP"|"REACT"|null }` → `204`.
  - `GET /sessioUsuari/login?redirect=<url>`: força l'autenticació i redirigeix a `redirect` si és del mateix origen o de `localhost:5173`. Si no, redirigeix a `/reactapp`.
  - `GET /sysenv` i `GET /manifest` (de `BaseUtilsController`).

- [ ] **Step 1: Tests:**
  - `sessioRetornaCodiIRols`: usuari `u1` amb `EMS_RESP` → `codi=="u1"` i `rols==["EMS_RESP"]`;
  - `senseSessio401`;
  - `loginNoRedirigeixAOrigenExtern`: `redirect=https://evil.example` → `Location` acaba en `/reactapp`;
  - `canviInterficie204`.
- [ ] **Step 2:** Run: `mvn -pl emiserv-controller test -DskipTests=false -Dtest=SessioUsuariControllerTest` → FAIL.
- [ ] **Step 3:** Implementar. El nom i el correu surten de `KeycloakUserDetails`, si està disponible.
- [ ] **Step 4:** Run → PASS.
- [ ] **Step 5:** Commit `Endpoints de sessió d'usuari i servei del SPA`.

### Task 7: Esquelet de la SPA i build Maven

**Files:**
- Create: `emiserv-back/src/main/reactapp/emiserv-back/`, amb els fitxers `package.json`, `vite.config.ts`, `tsconfig*.json`, `eslint.config.js`, `.prettierrc`, `index.html`, `patches/ketting+7.5.2.patch` i `.gitignore`. Es copien de Distribució i se substitueix `distribucio` per `emiserv` (`base=/emiservback/reactapp`, proxy de `/emiservback/sysenv` i `/emiservback/api`, `devManifest`, `fixBackendEndpointsBase`, ruta del pom a `../../../../pom.xml`). A `package.json` només es mantenen les dependències que la fase 1 fa servir, més `@dnd-kit/core`.
- Create: `lib/` ← `~/git/base-react/lib` (sense els `*.test.tsx`); `src/lot2/` ← `~/git/base-react/src/lot2` (sencera).
- Create: els fitxers de `src/`:
  - `main.tsx` i `App.tsx`;
  - `router.tsx`, amb les rutes `/` → `/serveis` i `/serveis`;
  - `theme.ts`, amb `createLot2Themes`. Els colors es prenen de l'aspecte actual de la JSP (CSS del decorator).
  - `i18n/{index.ts,translationCa.ts,translationEs.ts}`, amb `lot2Translations` registrades;
  - `components/EmiservAuthProvider.tsx`, adaptat de `DistribucioAuthProvider.tsx` (usa `api/sessioUsuari` i `sessioUsuari/login`);
  - `components/InterficieClassica.tsx`, que porta a la JSP equivalent (`/serveis` → `/emiservback/servei`) i fa `PUT interficie=JSP` si l'usuari ho tria com a preferència;
  - `components/UserProfile.tsx`, amb el selector d'interfície;
  - `components/EmiservBaseApp.tsx`, amb `Lot2BaseApp` i el menú: «Serveis», «Auditoria» (subopcions Backoffice i Enrutador, desactivades fins a la fase 3) i «Interfície clàssica»;
  - `assets/` (logos).
- Modify: `emiserv-back/pom.xml`: propietats `frontend.app.path` i `frontend.skip`, perfil `front` (`frontend-maven-plugin` 1.15.4, `npm run build-jboss`, còpia a `src/main/webapp/reactapp`), `webResources` → `reactapp` i neteja amb `maven-clean-plugin`. Referència: `distribucio-back/pom.xml`.
- Modify: `emiserv-boot/pom.xml`. Inclou el `dist` del SPA perquè es serveixi a `/emiservback/reactapp`, per exemple copiant-lo a `META-INF/resources/reactapp` de les classes. El `ReactController` ha de trobar l'`index.html` en els dos empaquetats: si `ServletContext` no el troba, es cerca al classpath.
- Modify: `.gitignore` (`emiserv-back/src/main/webapp/reactapp`, `node_modules`, `dist`).

- [ ] **Step 1:** Crear els fitxers. Run: `npm install && npm run lint && npx tsc -b` a la carpeta de la SPA → sense errors.
- [ ] **Step 2:** Run: `mvn clean install -P front` → BUILD SUCCESS. `emiserv-back-*.war` conté `reactapp/index.html` i el JAR de `emiserv-boot` també el serveix.
- [ ] **Step 3:** Verificació manual a `emiserv-boot`: `/emiservback/reactapp` mostra el marc Lot2 (capçalera, menú, peu Drassana) amb el nom de l'usuari. «Interfície clàssica» porta a la JSP. Una ruta inexistent (`/emiservback/reactapp/xyz`) carrega la SPA. `/emiservback/reactapp/no-existeix.js` → 404.
- [ ] **Step 4:** Verificació manual en dev: `npm run dev` + backend a 8080 → l'app funciona a `localhost:5173/emiservback/reactapp` després d'iniciar sessió.
- [ ] **Step 5:** Commit `Esquelet de la interfície REACT (base-react + Lot2)`.

### Task 8: Diagnòstic de configuració del servei

**Files:**
- Create: `emiserv-service-intf/.../logic/intf/dto/ServeiDiagnosticEnumDto.java`
- Modify: `emiserv-service-intf/.../service/ServeiService.java`, `emiserv-service/.../service/ServeiServiceImpl.java`, `emiserv-ejb/.../ServeiService.java`
- Test: `emiserv-service/src/test/java/es/caib/emiserv/logic/service/ServeiDiagnosticTest.java`

**Interfaces:**
- Produces:
  - `enum ServeiDiagnosticEnumDto { FALTA_CONFIG_SCSP, FALTA_EMISOR, ENRUTADOR_SENSE_RUTES, RUTA_BACKOFFICE_SENSE_CREDENCIALS }`;
  - `Map<Long, List<ServeiDiagnosticEnumDto>> ServeiService.diagnosticFindByServeis(List<Long> serveiIds)`: tots els motius de cada servei, en l'ordre de l'enum, amb llista buida si està bé. Té `@PreAuthorize` com `findAllPaginat`. Es calcula en bloc per a una pàgina, sense N+1 consultes per servei.

- [ ] **Step 1: Tests** (un per regla, més els casos límit):
  - backoffice sense `CORE_SERVICIO` → `[FALTA_CONFIG_SCSP]`;
  - backoffice amb `CORE_SERVICIO` sense emissor → `[FALTA_EMISOR]`;
  - enrutador sense rutes → conté `ENRUTADOR_SENSE_RUTES`;
  - enrutador amb una ruta `BACKOFFICE` que requereix credencials no configurades (les mateixes condicions que avui calcula `credencialsConfigurades` a la graella de rutes) → conté `RUTA_BACKOFFICE_SENSE_CREDENCIALS`;
  - servei correcte → `[]`;
  - llista d'ids buida → mapa buit, sense consulta.
- [ ] **Step 2:** Run: `mvn -pl emiserv-service test -DskipTests=false -Dtest=ServeiDiagnosticTest` → FAIL.
- [ ] **Step 3:** Implementar reutilitzant `isConfiguratScsp` i el càlcul de credencials existent, sense duplicar-los. Si cal compartir-los, s'extreuen a un helper.
- [ ] **Step 4:** Run → PASS.
- [ ] **Step 5:** Commit `Diagnòstic de configuració dels serveis`.

### Task 9: Recurs `ServeiResource` (llistat, accions i exportació)

**Files:**
- Create: `emiserv-service-intf/.../logic/intf/model/ServeiResource.java`
- Create: `emiserv-service-intf/.../logic/intf/service/ServeiResourceService.java` (`extends MutableResourceService<ServeiResource, Long>`)
- Create: `emiserv-persistence/.../persist/resourceentity/ServeiResourceEntity.java` (taula `ems_servei`) i `.../persist/resourcerepository/ServeiResourceRepository.java` (`extends BaseRepository<ServeiResourceEntity, Long>`)
- Create: `emiserv-service/.../logic/service/ServeiResourceServiceImpl.java` (`extends BaseMutableResourceService<ServeiResource, Long, ServeiResourceEntity>`)
- Create: `emiserv-service/.../logic/helper/ExportacioHelper.java`
- Create: `emiserv-ejb/.../ejb/resourceService/ServeiResourceServiceEjb.java` + bean a `EjbClientConfig`
- Create: `emiserv-controller/.../controller/api/ServeiApiController.java` (`extends BaseMutableResourceController<ServeiResource, Long>`, `@RequestMapping("/api/servei")`)
- Test: `emiserv-service/src/test/java/es/caib/emiserv/logic/service/ServeiResourceServiceTest.java`, `emiserv-service/src/test/java/es/caib/emiserv/logic/helper/ExportacioHelperTest.java`

**Interfaces:**
- Consumes: `ServeiService.diagnosticFindByServeis` (Task 8), `ServeiService.updateActiu`, `ServeiService.delete`.
- Produces:
  - `ServeiResource`:
    - camps: `codi`, `nom`, `tipus` (`ServeiTipusEnumDto`), `actiu`, `configurat`, `configuratScsp`, `xsdGestioActiva`, `permisosCount`, `rutesCount`, `diagnostic: List<ServeiDiagnosticEnumDto>`;
    - `@ResourceConfig(quickFilterFields = {"codi","nom"}, descriptionField = "nom", defaultSortFields = codi ASC)`;
    - artefactes ACTION `ACTIVAR`, `DESACTIVAR`, `ESBORRAR` i REPORT `EXPORTAR` (paràmetre `format`: `CSV` | `XLSX`).
  - `ExportacioHelper`:
    - `void comprovarMaxim(long total)`: llança `ExportacioMassaGranException` (HTTP 422 amb missatge traduït que inclou `total` i el màxim) si `total > es.caib.emiserv.exportacio.max`;
    - `byte[] generar(List<?> files, List<String> columnes, String format)`.

- [ ] **Step 1: Tests de `ServeiResourceServiceTest`:**
  - `adminVeuTots`: 3 serveis → `findPage` torna 3;
  - `respVeuNomesPermesos`: `EMS_RESP` amb permís a 1 → torna 1;
  - `respSensePermisosPaginaBuida`: torna una pàgina buida, sense excepció;
  - `filtreRapidPerCodiINom`: `quickFilter="abc"` coincideix amb el codi o el nom, sense distingir majúscules;
  - `diagnosticOmplert`: un backoffice sense config SCSP té `diagnostic==[FALTA_CONFIG_SCSP]`;
  - `activarDesactivarNomesAdmin`: `EMS_RESP` → `AccessDenied`;
  - `esborrarPropagaErrorNegoci`: un servei amb dependències → l'excepció de `ServeiService.delete` es propaga amb el seu missatge.
- [ ] **Step 2: Tests de `ExportacioHelperTest`:**
  - `superaMaximLlança` (màxim 10 i total 11);
  - `dinsMaximOk`;
  - `csvTeCapcaleraIFiles`;
  - `xlsxEsLlegible` (s'obre amb POI i té el mateix nombre de files).
- [ ] **Step 3:** Run: `mvn -pl emiserv-service test -DskipTests=false -Dtest='ServeiResourceServiceTest,ExportacioHelperTest'` → FAIL.
- [ ] **Step 4:** Implementar:
  - el filtre de permisos amb `additionalSpecification`: ids permesos per ACL `ADMINISTRATION` quan no és `EMS_ADMIN`, mateixa lògica que `findAllPaginat`;
  - els comptadors per subconsulta o fórmula, no carregant col·leccions;
  - `diagnostic` omplert a `afterConversion` de la pàgina amb una sola crida a `diagnosticFindByServeis`;
  - les accions, que deleguen a `ServeiService`;
  - l'exportació, que compta primer, crida `comprovarMaxim` i genera amb el mateix filtre i ordre.
- [ ] **Step 5:** Run → PASS.
- [ ] **Step 6:** Commit `Recurs de serveis per a la interfície REACT`.

### Task 10: Pantalla de llistat de serveis

**Files:**
- Create: `emiserv-back/src/main/reactapp/emiserv-back/src/pages/serveis/ServeisLlistat.tsx`, `ServeisFiltre.tsx` i `DiagnosticAvis.tsx`
- Modify: `src/router.tsx`, `src/i18n/translation{Ca,Es}.ts`

**Interfaces:**
- Consumes: el recurs `servei` (Task 9), via `useResourceApiService('servei')` i `MuiDataGrid resourceName="servei"`.

Comportament (spec §5.1):
- `ScreenFrame` + `MuiDataGrid` amb `title={<ScreenTitle icon=... title={t('serveis.titol')} />}` i `{...lot2ScreenGridProps(theme)}`.
- Columnes: codi, nom, tipus i, només amb `EMS_ADMIN`, actiu (xip), permisos (xip amb el nombre) i rutes (xip amb el nombre).
- Al costat del codi, `DiagnosticAvis`: icona d'avís amb un tooltip que llista els motius traduïts. En clicar-la, navega a `/serveis/:id?pestanya=<pestanya del primer motiu>`. Mapatge:

  | Motiu | Pestanya |
  | --- | --- |
  | `FALTA_CONFIG_SCSP`, `FALTA_EMISOR` | `configScsp` |
  | `ENRUTADOR_SENSE_RUTES`, `RUTA_BACKOFFICE_SENSE_CREDENCIALS` | `rutes` |

  La fitxa és de la fase 2. Fins aleshores, la ruta `/serveis/:id` mostra el formulari bàsic del recurs.
- Filtre:
  - ràpid, per codi i nom;
  - plegable, amb codi, nom, tipus i actiu;
  - l'estat del filtre persisteix per usuari amb `usePersistentState`;
  - `FilterCount` + `FilterButtons`.
- Accions de fila, només amb `EMS_ADMIN`: activar o desactivar (segons l'estat) i esborrar (amb confirmació, text de `servei.list.confirmacio.esborrar`). Els errors de negoci es mostren com a missatge.
- Barra:
  - «Nou servei» (`EMS_ADMIN`; fins a la fase 2 obre el formulari bàsic);
  - exportar a CSV o Excel, amb el filtre actiu; si supera el màxim, mostra el missatge del 422.

- [ ] **Step 1:** Implementar la pantalla. Run: `npm run lint && npx tsc -b` → sense errors.
- [ ] **Step 2:** Verificació manual a `emiserv-boot` amb usuari `EMS_ADMIN`:
  - filtres, ordenació i paginació;
  - avís de diagnòstic amb motius;
  - activar, desactivar i esborrar (incloent-hi un esborrat que falli per dependències);
  - exportació CSV i XLSX (s'obre a LibreOffice);
  - el filtre es recorda després de recarregar.
- [ ] **Step 3:** Verificació manual amb usuari `EMS_RESP`: només surten els serveis permesos, sense columnes ni accions d'administrador, i amb zero permisos la graella surt buida i sense error.
- [ ] **Step 4:** Commit `Pantalla de llistat de serveis a la interfície REACT`.

### Task 11: Verificació de punta a punta (JAR i EAR)

- [ ] **Step 1:** Run: `mvn clean install -P front` → BUILD SUCCESS.
- [ ] **Step 2:** Executar tots els tests nous: `mvn test -DskipTests=false -pl emiserv-service,emiserv-controller,emiserv-backend -Dtest='AplicacioServiceInterficieTest,ServeiDiagnosticTest,ServeiResourceServiceTest,ExportacioHelperTest,SessioUsuariControllerTest,ApiIndexTest'` → tots PASS.
- [ ] **Step 3:** A `emiserv-boot` i a l'EAR desplegat a JBoss EAP 7.2:
  - entrar amb la preferència React → s'obre la SPA;
  - canviar a la clàssica → JSP;
  - tornar a la nova des del menú d'usuari de la JSP;
  - repetir les verificacions de la Task 10.
- [ ] **Step 4:** Sessió caducada: invalidar la sessió amb la SPA oberta i fer una acció → login → torna a la mateixa URL i no apareix cap error genèric.
- [ ] **Step 5:** Regressió JSP:
  - llistat i formulari de serveis;
  - auditoria backoffice i enrutador amb detall i XML;
  - `scspRouting` amb el servidor fals (`emiserv-scsp-fake`).

  Tot ha de funcionar com abans.
- [ ] **Step 6:** Actualitzar `CLAUDE.md` (secció de la SPA: ubicació, `npm run dev`, perfil `front`, `frontend.skip`). Commit `Documentar la interfície REACT`.
