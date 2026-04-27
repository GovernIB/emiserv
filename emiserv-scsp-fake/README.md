# emiserv-scsp-fake

Servidor local fake per simular serveis SCSP i backoffices de `emiserv`.

Execució:

```bash
mvn -pl emiserv-scsp-fake package
java -jar emiserv-scsp-fake/target/emiserv-scsp-fake-2.0.5.jar
```

Propietats útils:

```bash
-Dfake.emiserv.host=127.0.0.1
-Dfake.emiserv.port=18080
```

Endpoints principals:

- `GET /__fake-emiserv`
- `GET /ws/EmiservBackoffice?wsdl`
- `POST /ws/EmiservBackoffice`
- `POST /scsp/<codi-servei>/<codi-entitat>`
- `POST /scsp/<codi-servei>/<codi-entitat>/ko`

Comportament:

- Qualsevol `POST` fora dels paths de backoffice es tracta com una petició SCSP.
- Per a enrutador múltiple, el path esperat és `/scsp/<codi-servei>/<codi-entitat>`.
- Si el path acaba amb `/ko` o l’XML conté `<error>`, el fake retorna un `SOAP Fault`.
- En cas contrari retorna una resposta `OK` i copia `codigoCertificado`, `idPeticion` i `idSolicitud` si són presents.

Exemples de configuració a `emiserv`:

Enrutador simple:

- Tipus de servei: `ENRUTADOR`
- URL destí / ruta destí: `http://localhost:18080/scsp/SVDSCDDWS01/CAIB`
- Variante per forçar error: `http://localhost:18080/scsp/SVDSCDDWS01/CAIB/ko`
- Si s'executa dins Docker, habitualment: `http://host.docker.internal:18080/...`

Enrutador múltiple:

- Tipus de servei: `ENRUTADOR_MULTIPLE`
- Ruta entitat `CAIB`: `http://localhost:18080/scsp/SVDSCDDWS01/CAIB`
- Ruta entitat `ALTRE`: `http://localhost:18080/scsp/SVDSCDDWS01/ALTRE`
- Si vols simular que una entitat falla: `http://localhost:18080/scsp/SVDSCDDWS01/ALTRE/ko`

Backoffice:

- Tipus de servei: `BACKOFFICE`
- Camp `backofficeCaibUrl`: `http://localhost:18080/ws/EmiservBackoffice`
- WSDL de referència: `http://localhost:18080/ws/EmiservBackoffice?wsdl`

Propietats opcionals relacionades amb backoffice:

```properties
es.caib.emiserv.backoffice.mock=false
es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.username=
es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.password=
es.caib.emiserv.backoffice.SVDSCDDWS01.caib.soap.action=
```

Exemple ràpid de mapeig local:

```text
Servei SCSP SVDSCDDWS01
  tipus = ENRUTADOR
  url desti = http://localhost:18080/scsp/SVDSCDDWS01/CAIB

Servei SCSP múltiple PROVA_MULTI
  tipus = ENRUTADOR_MULTIPLE
  CAIB  -> http://localhost:18080/scsp/SVDSCDDWS01/CAIB
  IBEST -> http://localhost:18080/scsp/SVDSCDDWS01/IBEST

Servei backoffice SVDCCAACPCWS01
  tipus = BACKOFFICE
  backofficeCaibUrl = http://localhost:18080/ws/EmiservBackoffice
```
