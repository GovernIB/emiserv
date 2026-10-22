--------------------------------------------------------------------------
-- EMISERV Creacio de taules
--------------------------------------------------------------------------
-- Script generat a partir dels changelogs de Liquibase del projecte
-- (emiserv-persistence/src/main/resources/db/changelog), aplanant les
-- sentencies CREATE TABLE + ALTER TABLE (ADD/DROP/RENAME COLUMN) en una
-- unica sentencia CREATE TABLE per taula, amb l'estat final de columnes.
-- Nomes conte sentencies CREATE TABLE (cap ALTER TABLE). Representa la
-- creacio completa de l'esquema EMISERV des de zero, versio 2.0.6.
--
-- Les taules "ems_*" (a excepcio de les d'ACL de Spring Security, que no
-- tenen entitat JPA) s'han contrastat una a una amb les entitats Hibernate
-- corresponents (emiserv-persistence/src/main/java/es/caib/emiserv/persist/entity).
--
-- Execucio: com a usuari propietari de l'esquema (no WWW_EMISERV).
--------------------------------------------------------------------------

SET DEFINE OFF;
WHENEVER SQLERROR EXIT SQL.SQLCODE;

CREATE TABLE ems_backoffice_com (
    id NUMBER(38, 0) NOT NULL,
    peticio_id NUMBER(38, 0) NOT NULL,
    peticio_data TIMESTAMP NOT NULL,
    peticio_xml CLOB NOT NULL,
    solicitud_id NUMBER(38, 0),
    resposta_data TIMESTAMP,
    resposta_xml CLOB,
    error CLOB,
    version NUMBER(38, 0) NOT NULL,
    CONSTRAINT ems_backoffice_com_pk PRIMARY KEY (id)
);

CREATE TABLE ems_backoffice_pet (
    id NUMBER(38, 0) NOT NULL,
    peticio_id VARCHAR2(26) NOT NULL,
    estat INTEGER NOT NULL,
    ter_data TIMESTAMP NOT NULL,
    darrera_sol_data TIMESTAMP,
    darrera_sol_id VARCHAR2(256),
    processades_error INTEGER,
    processades_total INTEGER,
    comunicacio_id NUMBER(38, 0),
    version NUMBER(38, 0) NOT NULL,
    CONSTRAINT ems_backoffice_pet_pk PRIMARY KEY (id)
);

CREATE TABLE ems_backoffice_sol (
    id NUMBER(38, 0) NOT NULL,
    peticio_id NUMBER(38, 0) NOT NULL,
    solicitud_id VARCHAR2(256),
    comunicacio_id NUMBER(38, 0),
    estat INTEGER,
    version NUMBER(38, 0) NOT NULL,
    CONSTRAINT ems_backoffice_sol_pk PRIMARY KEY (id)
);

CREATE TABLE ems_redir_missatge (
    id NUMBER(38, 0) NOT NULL,
    tipus INTEGER NOT NULL,
    peticio_id NUMBER(38, 0) NOT NULL,
    xml CLOB,
    version NUMBER(38, 0) NOT NULL,
    entitat_codi VARCHAR2(64 CHAR),
    url VARCHAR2(1024 CHAR),
    CONSTRAINT ems_redir_msg_pk PRIMARY KEY (id)
);

CREATE TABLE ems_redir_peticio (
    id NUMBER(38, 0) NOT NULL,
    data_comprovacio TIMESTAMP,
    data_peticio TIMESTAMP,
    data_resposta TIMESTAMP,
    emissor_codi VARCHAR2(10),
    error CLOB,
    estat VARCHAR2(16) NOT NULL,
    num_enviaments INTEGER NOT NULL,
    num_transmissions INTEGER NOT NULL,
    peticio_id VARCHAR2(128) NOT NULL,
    servei_codi VARCHAR2(256) NOT NULL,
    sincrona NUMBER(1),
    ter TIMESTAMP,
    version NUMBER(38, 0) NOT NULL,
    procediment_codi VARCHAR2(256 CHAR),
    procediment_nom VARCHAR2(256 CHAR),
    estatenum INTEGER,
    entitat_codi VARCHAR2(64 CHAR),
    CONSTRAINT ems_redir_pet_pk PRIMARY KEY (id)
);

CREATE TABLE ems_redir_solicitud (
    id NUMBER(38, 0) NOT NULL,
    solicitud_id VARCHAR2(256) NOT NULL,
    solicitant_id VARCHAR2(40) NOT NULL,
    solicitant_nom VARCHAR2(256),
    consentiment VARCHAR2(3),
    data_generacio TIMESTAMP,
    error CLOB,
    estat VARCHAR2(4),
    finalitat VARCHAR2(256),
    funcionari_doc VARCHAR2(16),
    funcionari_nom VARCHAR2(160),
    procediment_codi VARCHAR2(256),
    procediment_nom VARCHAR2(256),
    titular_doc VARCHAR2(16),
    titular_llinatge1 VARCHAR2(160),
    titular_llinatge2 VARCHAR2(160),
    titular_nom VARCHAR2(160),
    titular_nom_sencer VARCHAR2(256),
    transmision_id VARCHAR2(256),
    unitat_tramitadora VARCHAR2(256),
    peticio_id NUMBER(38, 0) NOT NULL,
    version NUMBER(38, 0) NOT NULL,
    solicitant_codi VARCHAR2(64 CHAR),
    titular_tipus_doc VARCHAR2(16 CHAR),
    CONSTRAINT ems_redir_sol_pk PRIMARY KEY (id)
);

CREATE TABLE ems_servei (
    id NUMBER(38, 0) NOT NULL,
    codi VARCHAR2(64) NOT NULL,
    nom VARCHAR2(256) NOT NULL,
    tipus INTEGER NOT NULL,
    descripcio VARCHAR2(1024),
    configurat NUMBER(1),
    actiu NUMBER(1),
    backcaib_async_ter INTEGER,
    backcaib_async_tipus INTEGER,
    backcaib_autenticacio INTEGER,
    backcaib_url VARCHAR2(256),
    backoffice_class VARCHAR2(256),
    resolver_class VARCHAR2(256),
    response_resolver_class VARCHAR2(256),
    url_per_defecte VARCHAR2(256),
    xsd_activa NUMBER(1),
    xsd_esquema_bak VARCHAR2(256),
    createdby_codi VARCHAR2(64) NOT NULL,
    createddate TIMESTAMP NOT NULL,
    lastmodifiedby_codi VARCHAR2(64),
    lastmodifieddate TIMESTAMP,
    version NUMBER(38, 0) NOT NULL,
    CONSTRAINT ems_servei_pk PRIMARY KEY (id)
);

CREATE TABLE ems_servei_ruta_desti (
    id NUMBER(38, 0) NOT NULL,
    entitat_codi VARCHAR2(64) NOT NULL,
    ordre NUMBER(38, 0),
    url VARCHAR2(256) NOT NULL,
    servei_id NUMBER(38, 0) NOT NULL,
    createdby_codi VARCHAR2(64) NOT NULL,
    createddate TIMESTAMP NOT NULL,
    lastmodifiedby_codi VARCHAR2(64),
    lastmodifieddate TIMESTAMP,
    version NUMBER(38, 0) NOT NULL,
    CONSTRAINT ems_servei_rut_des_pk PRIMARY KEY (id)
);

CREATE TABLE ems_usuari (
    codi VARCHAR2(64) NOT NULL,
    idioma VARCHAR2(2) DEFAULT 'CA',
    version NUMBER(38, 0) NOT NULL,
    CONSTRAINT ems_usuari_pk PRIMARY KEY (codi)
);

CREATE TABLE ems_acl_sid (
    id NUMBER(38, 0) NOT NULL,
    principal NUMBER(1) NOT NULL,
    sid VARCHAR2(100) NOT NULL,
    CONSTRAINT ems_acl_sid_pk PRIMARY KEY (id)
);

CREATE TABLE ems_acl_class (
    id NUMBER(38, 0) NOT NULL,
    class VARCHAR2(100) NOT NULL,
    CONSTRAINT ems_acl_class_pk PRIMARY KEY (id)
);

CREATE TABLE ems_acl_object_identity (
    id NUMBER(38, 0) NOT NULL,
    object_id_class NUMBER(38, 0) NOT NULL,
    object_id_identity NUMBER(38, 0) NOT NULL,
    parent_object NUMBER(38, 0),
    owner_sid NUMBER(38, 0) NOT NULL,
    entries_inheriting NUMBER(1) NOT NULL,
    CONSTRAINT ems_acl_object_identity_pk PRIMARY KEY (id)
);

CREATE TABLE ems_acl_entry (
    id NUMBER(38, 0) NOT NULL,
    acl_object_identity NUMBER(38, 0) NOT NULL,
    ace_order NUMBER(38, 0) NOT NULL,
    sid NUMBER(38, 0) NOT NULL,
    mask INTEGER NOT NULL,
    granting NUMBER(1) NOT NULL,
    audit_success NUMBER(1) NOT NULL,
    audit_failure NUMBER(1) NOT NULL,
    CONSTRAINT ems_acl_entry_pk PRIMARY KEY (id)
);

CREATE TABLE core_cache_certificados (
    numserie VARCHAR2(256) NOT NULL,
    autoridadcertif VARCHAR2(512) NOT NULL,
    tiempocomprobacion TIMESTAMP NOT NULL,
    revocado INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT core_cache_certificados_pk PRIMARY KEY (numserie, autoridadcertif)
);

CREATE TABLE core_clave_privada (
    id NUMBER(38, 0) NOT NULL,
    alias VARCHAR2(256) NOT NULL,
    nombre VARCHAR2(256) NOT NULL,
    password VARCHAR2(256) NOT NULL,
    numeroserie VARCHAR2(256) NOT NULL,
    fechabaja TIMESTAMP,
    fechaalta TIMESTAMP NOT NULL,
    organismo NUMBER(38, 0),
    interoperabilidad NUMBER(1),
    CONSTRAINT core_clave_privada_pk PRIMARY KEY (id)
);

CREATE TABLE core_clave_publica (
    id NUMBER(38, 0) NOT NULL,
    alias VARCHAR2(256) NOT NULL,
    nombre VARCHAR2(256) NOT NULL,
    numeroserie VARCHAR2(256) NOT NULL,
    fechaalta TIMESTAMP NOT NULL,
    fechabaja TIMESTAMP,
    CONSTRAINT core_clave_publica_pk PRIMARY KEY (id)
);

CREATE TABLE core_codigo_error (
    codigo VARCHAR2(4) NOT NULL,
    descripcion VARCHAR2(1024) NOT NULL,
    CONSTRAINT core_codigo_error_pk PRIMARY KEY (codigo)
);

CREATE TABLE core_emisor_certificado (
    id NUMBER(38, 0) NOT NULL,
    nombre VARCHAR2(50) NOT NULL,
    cif VARCHAR2(16) NOT NULL,
    fechaalta TIMESTAMP,
    fechabaja TIMESTAMP,
    CONSTRAINT core_emisor_certificado_pk PRIMARY KEY (id)
);

CREATE TABLE core_estado_peticion (
    codigo VARCHAR2(4) NOT NULL,
    mensaje VARCHAR2(256) NOT NULL,
    CONSTRAINT core_estado_peticion_pk PRIMARY KEY (codigo)
);

CREATE TABLE core_modulo (
    nombre VARCHAR2(256) NOT NULL,
    descripcion VARCHAR2(512),
    activoentrada INTEGER DEFAULT 1 NOT NULL,
    activosalida INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT core_modulo_pk PRIMARY KEY (nombre)
);

CREATE TABLE core_modulo_configuracion (
    certificado NUMBER(38, 0) NOT NULL,
    modulo VARCHAR2(256) NOT NULL,
    activoentrada INTEGER DEFAULT 1 NOT NULL,
    activosalida INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT core_modulo_config_pk PRIMARY KEY (certificado, modulo)
);

CREATE TABLE core_req_modulo_pdf (
    nombre VARCHAR2(256) NOT NULL,
    activo INTEGER DEFAULT 1 NOT NULL,
    orden INTEGER NOT NULL,
    CONSTRAINT core_req_modulo_pdf_pk PRIMARY KEY (nombre)
);

CREATE TABLE core_req_modulo_pdf_cesionario (
    servicio NUMBER(38, 0) NOT NULL,
    organismo NUMBER(38, 0) NOT NULL,
    modulo VARCHAR2(256) NOT NULL,
    activo INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT core_req_modpdf_ces_pk PRIMARY KEY (servicio, organismo, modulo)
);

CREATE TABLE core_em_autorizacion_organismo (
    id NUMBER(38, 0) NOT NULL,
    idorganismo VARCHAR2(16) NOT NULL,
    fechaalta TIMESTAMP NOT NULL,
    fechabaja TIMESTAMP,
    nombreorganismo VARCHAR2(64),
    CONSTRAINT core_em_autorizacion_org_pk PRIMARY KEY (id)
);

CREATE TABLE core_organismo_cesionario (
    id NUMBER(38, 0) NOT NULL,
    fechaalta TIMESTAMP NOT NULL,
    fechabaja TIMESTAMP,
    nombre VARCHAR2(50) NOT NULL,
    cif VARCHAR2(50) NOT NULL,
    bloqueado INTEGER DEFAULT 0 NOT NULL,
    logo BLOB,
    codigounidadtramitadora VARCHAR2(9),
    CONSTRAINT core_organismo_cesionario_pk PRIMARY KEY (id)
);

CREATE TABLE core_parametro_configuracion (
    nombre VARCHAR2(64) NOT NULL,
    valor VARCHAR2(512) NOT NULL,
    descripcion VARCHAR2(512),
    CONSTRAINT core_parametro_config_pk PRIMARY KEY (nombre)
);

CREATE TABLE core_peticion_respuesta (
    idpeticion VARCHAR2(26) NOT NULL,
    certificado NUMBER(38, 0) NOT NULL,
    estado VARCHAR2(4) NOT NULL,
    estadosecundario VARCHAR2(16),
    fechapeticion TIMESTAMP,
    fecharespuesta TIMESTAMP,
    ter TIMESTAMP,
    numeroenvios INTEGER DEFAULT 0 NOT NULL,
    numerotransmisiones INTEGER DEFAULT 1 NOT NULL,
    fechaultimosondeo TIMESTAMP,
    transmisionsincrona INTEGER DEFAULT 1 NOT NULL,
    descompuesta VARCHAR2(1),
    error VARCHAR2(4000),
    errorsecundario VARCHAR2(1024),
    emscodiprocediment VARCHAR2(256 CHAR),
    emsnomprocediment VARCHAR2(256 CHAR),
    emsestat INTEGER,
    emscodiservei VARCHAR2(64 CHAR),
    emsnomservei VARCHAR2(512 CHAR),
    CONSTRAINT core_peticion_respuesta_pk PRIMARY KEY (idpeticion)
);

CREATE TABLE core_servicio (
    id NUMBER(38, 0) NOT NULL,
    codcertificado VARCHAR2(64) NOT NULL,
    descripcion VARCHAR2(512) NOT NULL,
    urlsincrona VARCHAR2(256),
    urlasincrona VARCHAR2(256),
    actionsincrona VARCHAR2(256),
    actionasincrona VARCHAR2(256),
    actionsolicitud VARCHAR2(256),
    versionesquema VARCHAR2(32) NOT NULL,
    tiposeguridad VARCHAR2(16) NOT NULL,
    prefijopeticion VARCHAR2(9),
    xpathcifradosincrono VARCHAR2(256),
    xpathcifradoasincrono VARCHAR2(256),
    esquemas VARCHAR2(256),
    clavecifrado NUMBER(38, 0),
    clavefirma NUMBER(38, 0),
    algoritmocifrado VARCHAR2(32),
    numeromaximoreenvios INTEGER NOT NULL,
    maxsolicitudespeticion INTEGER NOT NULL,
    prefijoidtransmision VARCHAR2(9),
    emisor NUMBER(38, 0) NOT NULL,
    fechaalta TIMESTAMP NOT NULL,
    fechabaja TIMESTAMP,
    caducidad INTEGER DEFAULT 0 NOT NULL,
    xpathliteralerror VARCHAR2(256),
    xpathcodigoerror VARCHAR2(256),
    xpathcodigoerrorsecundario VARCHAR2(256 BYTE),
    timeout INTEGER DEFAULT 60 NOT NULL,
    validacionfirma VARCHAR2(32) DEFAULT 'estricto',
    plantillaxslt VARCHAR2(512),
    CONSTRAINT core_servicio_pk PRIMARY KEY (id)
);

CREATE TABLE core_tipo_mensaje (
    tipo INTEGER NOT NULL,
    descripcion VARCHAR2(32) NOT NULL,
    CONSTRAINT core_tipo_mensaje_pk PRIMARY KEY (tipo)
);

CREATE TABLE core_token_data (
    idpeticion VARCHAR2(26) NOT NULL,
    tipomensaje INTEGER NOT NULL,
    datos CLOB NOT NULL,
    clave VARCHAR2(256),
    modoencriptacion VARCHAR2(32),
    algoritmoencriptacion VARCHAR2(32),
    CONSTRAINT core_token_data_pk PRIMARY KEY (idpeticion, tipomensaje)
);

CREATE TABLE core_transmision (
    idsolicitud VARCHAR2(64) NOT NULL,
    idpeticion VARCHAR2(26) NOT NULL,
    idtransmision VARCHAR2(64),
    idsolicitante VARCHAR2(10) NOT NULL,
    nombresolicitante VARCHAR2(256),
    doctitular VARCHAR2(30),
    nombretitular VARCHAR2(40),
    apellido1titular VARCHAR2(40),
    apellido2titular VARCHAR2(40),
    nombrecompletotitular VARCHAR2(122),
    docfuncionario VARCHAR2(16),
    nombrefuncionario VARCHAR2(128),
    seudonimofuncionario VARCHAR2(32),
    fechageneracion TIMESTAMP,
    unidadtramitadora VARCHAR2(250),
    codigounidadtramitadora VARCHAR2(9),
    codigoprocedimiento VARCHAR2(256),
    nombreprocedimiento VARCHAR2(256),
    expediente VARCHAR2(65),
    finalidad VARCHAR2(256),
    consentimiento VARCHAR2(3),
    error VARCHAR2(4000),
    xmltransmision CLOB,
    estado VARCHAR2(10),
    estadosecundario VARCHAR2(16),
    codigosolicitante VARCHAR2(64 CHAR),
    tipodoctitular VARCHAR2(16 CHAR),
    CONSTRAINT core_transmision_pk PRIMARY KEY (idsolicitud, idpeticion)
);

CREATE TABLE core_em_aplicacion (
    idaplicacion INTEGER NOT NULL,
    nifcertificado VARCHAR2(16),
    numeroserie VARCHAR2(64) NOT NULL,
    cn VARCHAR2(512),
    tiempocomprobacion TIMESTAMP,
    autoridadcertif NUMBER(38, 0) NOT NULL,
    fechaalta TIMESTAMP,
    fechabaja TIMESTAMP,
    descripcion VARCHAR2(512),
    CONSTRAINT core_em_aplicacion_pk PRIMARY KEY (idaplicacion)
);

CREATE TABLE core_em_autorizacion_ca (
    id NUMBER(38, 0) NOT NULL,
    codca VARCHAR2(512) NOT NULL,
    nombre VARCHAR2(256),
    CONSTRAINT core_em_autorizacion_ca_pk PRIMARY KEY (id)
);

CREATE TABLE core_em_autorizacion_cert (
    id NUMBER(38, 0) NOT NULL,
    idcertificado NUMBER(38, 0) NOT NULL,
    idorganismo NUMBER(38, 0) NOT NULL,
    idaplicacion INTEGER NOT NULL,
    fechaalta TIMESTAMP,
    fechabaja TIMESTAMP,
    CONSTRAINT core_em_autorizacion_cert_pk PRIMARY KEY (id)
);

CREATE TABLE core_em_codigo_error_secun (
    codigo VARCHAR2(4) NOT NULL,
    codigosecundario VARCHAR2(16) NOT NULL,
    descripcion CLOB NOT NULL,
    CONSTRAINT core_em_cod_error_secun_pk PRIMARY KEY (codigo, codigosecundario)
);

CREATE TABLE core_em_backoffice (
    certificado NUMBER(38, 0) NOT NULL,
    beanname VARCHAR2(256),
    classname VARCHAR2(256),
    ter INTEGER NOT NULL,
    CONSTRAINT core_em_backoffice_pk PRIMARY KEY (certificado)
);

CREATE TABLE core_em_secuencia_idtrans (
    prefijo VARCHAR2(9) NOT NULL,
    secuencia VARCHAR2(26) NOT NULL,
    fechageneracion TIMESTAMP NOT NULL,
    CONSTRAINT core_em_secuencia_idtrans_pk PRIMARY KEY (prefijo)
);

CREATE TABLE core_req_secuencia_id_peticion (
    prefijo VARCHAR2(9) NOT NULL,
    secuencia VARCHAR2(23) NOT NULL,
    fechageneracion TIMESTAMP NOT NULL,
    CONSTRAINT core_req_secuencia_id_pet_pk PRIMARY KEY (prefijo)
);

CREATE TABLE core_req_cesionarios_servicios (
    id NUMBER(38, 0) NOT NULL,
    servicio NUMBER(38, 0),
    claveprivada NUMBER(38, 0),
    organismo NUMBER(38, 0),
    fechaalta TIMESTAMP(6) NOT NULL,
    fechabaja TIMESTAMP(6),
    bloqueado NUMBER(1) NOT NULL,
    sslflag NUMBER(1) NOT NULL,
    CONSTRAINT core_req_cesionarios_serv_pk PRIMARY KEY (id)
);

CREATE TABLE ems_config_group (
    code VARCHAR2(128) NOT NULL,
    parent_code VARCHAR2(128),
    position NUMBER(3) NOT NULL,
    description_key VARCHAR2(512)
);

CREATE TABLE ems_config_type (
    code VARCHAR2(128) NOT NULL,
    value VARCHAR2(2048)
);

CREATE TABLE ems_config (
    key VARCHAR2(256) NOT NULL,
    value VARCHAR2(2048),
    description_key VARCHAR2(2048),
    group_code VARCHAR2(128) NOT NULL,
    position NUMBER(3) NOT NULL,
    source_property VARCHAR2(16) NOT NULL,
    requereix_reinici NUMBER(1) DEFAULT 0,
    type_code VARCHAR2(128),
    lastmodifiedby VARCHAR2(64),
    lastmodifieddate TIMESTAMP
);

CREATE TABLE ems_entitat (
    id NUMBER(38, 0) NOT NULL,
    codi VARCHAR2(64 CHAR) NOT NULL,
    nom VARCHAR2(255 CHAR) NOT NULL,
    cif VARCHAR2(9 CHAR) NOT NULL,
    unitat_arrel VARCHAR2(9 CHAR),
    CONSTRAINT ems_entitat_pk PRIMARY KEY (id),
    UNIQUE (codi),
    UNIQUE (cif)
);

