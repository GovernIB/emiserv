
CREATE TABLE EMS_USUARI
(
  CODI          VARCHAR2(64)                    NOT NULL,
  NOM           VARCHAR2(200),
  NIF           VARCHAR2(9)                     NOT NULL,
  EMAIL         VARCHAR2(200),
  INICIALITZAT  NUMBER(1),
  VERSION       NUMBER(19)                      NOT NULL
);

CREATE TABLE EMS_SERVEI
(
  ID                     NUMBER(19)               NOT NULL,
  CODI                   VARCHAR2(256)            NOT NULL,
  NOM                    VARCHAR2(256)            NOT NULL,
  TIPUS                  NUMBER(2)                NOT NULL,
  DESCRIPCIO             VARCHAR2(1024),
  BACKOFFICE_CLASS       VARCHAR2(256),
  BACKCAIB_URL           VARCHAR2(256),
  BACKCAIB_AUTENTICACIO  NUMBER(10),
  BACKCAIB_ASYNC_TIPUS   NUMBER(10),
  BACKCAIB_ASYNC_TER     NUMBER(10),
  RESOLVER_CLASS         VARCHAR2(1024),
  RESPONSE_RESOLVER_CLASS VARCHAR2(1024),
  CONFIGURAT             NUMBER(1),
  ACTIU                  NUMBER(1),
  URL_PER_DEFECTE        VARCHAR2(256),
  XSD_ACTIVA             NUMBER(1),
  XSD_ESQUEMA_BAK        VARCHAR2(256),
  VERSION                NUMBER(19)               NOT NULL,
  CREATEDDATE            TIMESTAMP(6),
  LASTMODIFIEDDATE       TIMESTAMP(6),
  CREATEDBY_CODI         VARCHAR2(256),
  LASTMODIFIEDBY_CODI    VARCHAR2(256)
);

CREATE TABLE EMS_SERVEI_RUTA_DESTI
(
  ID                   NUMBER(19)               NOT NULL,
  ENTITAT_CODI         VARCHAR2(256)            NOT NULL,
  URL                  VARCHAR2(1024)           NOT NULL,
  SERVEI_ID            NUMBER(19)               NOT NULL,
  VERSION              NUMBER(19)               NOT NULL,
  ORDRE                NUMBER(19),
  CREATEDDATE          TIMESTAMP(6),
  LASTMODIFIEDDATE     TIMESTAMP(6),
  CREATEDBY_CODI       VARCHAR2(256),
  LASTMODIFIEDBY_CODI  VARCHAR2(256)
);

CREATE TABLE EMS_REDIR_PETICIO
(
  ID                 NUMBER(19)                 NOT NULL,
  DATA_COMPROVACIO   TIMESTAMP(6),
  DATA_PETICIO       TIMESTAMP(6),
  DATA_RESPOSTA      TIMESTAMP(6),
  ERROR              CLOB,
  ESTAT              VARCHAR2(64)               NOT NULL,
  NUM_ENVIAMENTS     NUMBER(10)                 NOT NULL,
  NUM_TRANSMISSIONS  NUMBER(10)                 NOT NULL,
  PETICIO_ID         VARCHAR2(512)              NOT NULL,
  SERVEI_CODI        VARCHAR2(1024)             NOT NULL,
  SINCRONA           NUMBER(1),
  TER                TIMESTAMP(6),
  VERSION            NUMBER(19)                 NOT NULL,
  EMISSOR_CODI       VARCHAR2(10)
);

CREATE TABLE EMS_REDIR_SOLICITUD
(
  ID                  NUMBER(19)                NOT NULL,
  CONSENTIMENT        VARCHAR2(12),
  DATA_GENERACIO      TIMESTAMP(6),
  ERROR               CLOB,
  ESTAT               VARCHAR2(16),
  FINALITAT           VARCHAR2(1024),
  FUNCIONARI_DOC      VARCHAR2(64),
  FUNCIONARI_NOM      VARCHAR2(640),
  PROCEDIMENT_CODI    VARCHAR2(1024),
  PROCEDIMENT_NOM     VARCHAR2(1024),
  SOLICITANT_ID       VARCHAR2(160)             NOT NULL,
  SOLICITANT_NOM      VARCHAR2(1024),
  SOLICITUD_ID        VARCHAR2(1024)            NOT NULL,
  TITULAR_DOC         VARCHAR2(64),
  TITULAR_LLINATGE1   VARCHAR2(640),
  TITULAR_LLINATGE2   VARCHAR2(640),
  TITULAR_NOM         VARCHAR2(640),
  TITULAR_NOM_SENCER  VARCHAR2(1024),
  TRANSMISION_ID      VARCHAR2(1024),
  UNITAT_TRAMITADORA  VARCHAR2(1024),
  VERSION             NUMBER(19)                NOT NULL,
  PETICIO_ID          NUMBER(19)                NOT NULL
);

CREATE TABLE EMS_REDIR_MISSATGE
(
  ID          NUMBER(19)                        NOT NULL,
  TIPUS       NUMBER(10)                        NOT NULL,
  VERSION     NUMBER(19)                        NOT NULL,
  XML         CLOB,
  PETICIO_ID  NUMBER(19)                        NOT NULL
);

CREATE TABLE EMS_BACKOFFICE_COM
(
  ID            NUMBER(19)                      NOT NULL,
  PETICIO_ID    NUMBER(19)                      NOT NULL,
  SOLICITUD_ID  NUMBER(19),
  PETICIO_DATA  TIMESTAMP(6)                    NOT NULL,
  PETICIO_XML   CLOB                            NOT NULL,
  RESPOSTA_DATA TIMESTAMP (6),
  RESPOSTA_XML  CLOB,
  ERROR         CLOB,
  VERSION       NUMBER(19)
);

CREATE TABLE EMS_BACKOFFICE_PET
(
  ID                NUMBER(19)                  NOT NULL,
  PETICIO_ID        VARCHAR2(26)                NOT NULL,
  ESTAT             NUMBER(10)                  NOT NULL,
  TER_DATA          TIMESTAMP (6)               NOT NULL,
  DARRERA_SOL_DATA  TIMESTAMP (6),
  DARRERA_SOL_ID    VARCHAR2(256),
  PROCESSADES_ERROR NUMBER(10),
  PROCESSADES_TOTAL NUMBER(10),
  COMUNICACIO_ID    NUMBER(19),
  VERSION           NUMBER(19)
);

CREATE TABLE EMS_BACKOFFICE_SOL
(
  ID             NUMBER(19)                     NOT NULL,
  PETICIO_ID     NUMBER(19)                     NOT NULL,
  SOLICITUD_ID   VARCHAR2(256)                  NOT NULL,
  ESTAT          NUMBER(10)                     NOT NULL,
  COMUNICACIO_ID NUMBER(19),
  VERSION        NUMBER(19)
);

CREATE TABLE EMS_ACL_CLASS
(
  ID     NUMBER(19)                             NOT NULL,
  CLASS  VARCHAR2(100)                          NOT NULL
);


CREATE TABLE EMS_ACL_SID
(
  ID         NUMBER(19)                         NOT NULL,
  PRINCIPAL  NUMBER(1)                          NOT NULL,
  SID        VARCHAR2(100)                      NOT NULL
);


CREATE TABLE EMS_ACL_ENTRY
(
  ID                   NUMBER(19)               NOT NULL,
  ACL_OBJECT_IDENTITY  NUMBER(19)               NOT NULL,
  ACE_ORDER            NUMBER(19)               NOT NULL,
  SID                  NUMBER(19)               NOT NULL,
  MASK                 NUMBER(19)               NOT NULL,
  GRANTING             NUMBER(1)                NOT NULL,
  AUDIT_SUCCESS        NUMBER(1)                NOT NULL,
  AUDIT_FAILURE        NUMBER(1)                NOT NULL
);


CREATE TABLE EMS_ACL_OBJECT_IDENTITY
(
  ID                  NUMBER(19)                NOT NULL,
  OBJECT_ID_CLASS     NUMBER(19)                NOT NULL,
  OBJECT_ID_IDENTITY  NUMBER(19)                NOT NULL,
  PARENT_OBJECT       NUMBER(19),
  OWNER_SID           NUMBER(19)                NOT NULL,
  ENTRIES_INHERITING  NUMBER(1)                 NOT NULL
);
