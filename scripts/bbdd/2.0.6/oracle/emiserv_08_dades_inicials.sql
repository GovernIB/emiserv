--------------------------------------------------------------------------
-- EMISERV Dades inicials
--------------------------------------------------------------------------
-- Script generat a partir dels changelogs de Liquibase del projecte
-- (emiserv-persistence/src/main/resources/db/changelog), executant-los
-- amb el motor real de Liquibase 4.4.3 en mode "offline:oracle" contra
-- una base de dades buida. Representa la creacio completa de l'esquema
-- EMISERV des de zero, versio 2.0.6.
--
-- Execucio: com a usuari propietari de l'esquema (no WWW_EMISERV).
-- Requereix que emiserv_01_taules.sql i emiserv_02_constraints.sql s'hagin executat abans.
--------------------------------------------------------------------------

SET DEFINE OFF;
WHENEVER SQLERROR EXIT SQL.SQLCODE;

-- db/changelog/initial_data.yaml::init-data-scsp-1
INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (0,'Peticion');
INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (1,'ConfirmacionPeticion');
INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (2,'SolicitudRespuesta');
INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (3,'Respuesta');
INSERT INTO core_tipo_mensaje(tipo, descripcion) VALUES (4,'Fault');

INSERT INTO core_estado_peticion(codigo, mensaje) VALUES ('0001', 'Pendiente');
INSERT INTO core_estado_peticion(codigo, mensaje) VALUES ('0002', 'En proceso');
INSERT INTO core_estado_peticion(codigo, mensaje) VALUES ('0003', 'Tramitada');
INSERT INTO core_estado_peticion(codigo, mensaje) VALUES ('0004', 'En proceso Polling');

INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (1, 'OU=FNMT Clase 2 CA, O=FNMT, C=ES', 'Fabrica Nacional de Moneda y Timbre');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (2, 'OU=FNMT Clase 2 CA,O=FNMT,C=ES', 'Fabrica Nacional de Moneda y Timbre');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (3, 'OU=AC APE, O=FNMT-RCM, C=ES', 'Fabrica Nacional de Moneda y Timbre');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (4, 'OU=AC APE,O=FNMT-RCM,C=ES', 'Fabrica Nacional de Moneda y Timbre');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (5, 'CN=AC DGP 001, OU=CNP, O=DIRECCION GENERAL DE LA POLICIA, C=ES', 'DIRECCION GENERAL DE LA POLICIA');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (6, 'CN=EC-SAFP, OU=Secretaria d''Administracio i Funcio Publica, OU="Vegeu https://www.catcert.net/verCIC-2 [^] (c)03", OU=Serveis Publics de Certificacio ECV-2, L=Passatge de la Concepcio 11 08008 Barcelona, O=Agencia Catalana de Certificacio (NIF Q-0801176-I), C=ES', 'Secretaria d''Administracio i Funcio Publica');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (7, 'CN=EAEko HAetako langileen CA - CA personal de AAPP vascas (2), OU=AZZ Ziurtagiri publikoa - Certificado publico SCA, O=IZENPE S.A., C=ES', 'IZENPE S.A');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (8, 'CN=AC CAMERFIRMA AAPP, SERIALNUMBER=A82743287, OU=AC CAMERFIRMA, L=MADRID (Ver en https://www.camerfirma.com/address [^]), O=AC CAMERFIRMA S.A., C=ES', 'AC Camerfirma');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (9, 'CN=AC Administración Pública, SERIALNUMBER=Q2826004J, OU=CERES, O=FNMT-RCM, C=ES', 'Fabrica Nacional de Moneda y Timbre');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (10, 'CN=AC Camerfirma Express Corporate Server v3, O=AC Camerfirma SA, OU=http://www.camerfirma.com, [^] SERIALNUMBER=A82743287, L=Madrid (see current address at www.camerfirma.com/address), EMAILADDRESS=info@camerfirma.com, C=ES', 'AC Camerfirma');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (11, 'CN=AC Camerfirma Certificados Camerales, O=AC Camerfirma SA, SERIALNUMBER=A82743287, L=Madrid (see current address at www.camerfirma.com/address), EMAILADDRESS=ac_camerfirma_cc@camerfirma.com, C=ES', 'AC Camerfirma');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (12, 'CN=EC-AL, OU=Administracions Locals de Catalunya, OU="Vegeu https://www.catcert.net/verCIC-2 [^] (c)03", OU=Serveis Publics de Certificacio ECV-2, L=Passatge de la Concepcio 11 08008 Barcelona, O=Agencia Catalana de Certificacio (NIF Q-0801176-I), C=ES', 'Agència Catalana de Certificació CATCert');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (13, 'C=ES, O=Generalitat Valenciana, OU=PKIGVA, CN=ACCV-CA2', 'Autoritat de Certificació de la Comunitat Valenciana (ACCV)');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (14, 'C=ES,O=FNMT,OU=FNMT Clase 2 CA', 'Fabrica Nacional de Moneda y Timbre');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (15, 'C=ES, L=MADRID, O=MINISTERIO DE TRABAJO E INMIGRACION, OU=SUBDIRECCION GENERAL DE PROCESO DE DATOS, OU=PRESTADOR DE SERVICIOS DE CERTIFICACION MTIN, SERIALNUMBER=S2819001E, CN=AC1 RAIZ MTIN', 'Prestador de Servicios de Certificación del Ministerio de Trabajo e Inmigración (PSCMTIN).');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (16, 'C=ES,L=Madrid,E=ac@acabogacia.org,O=Consejo General de la Abogacia NIF:Q-2863006I,OU=Autoridad de Certificacion de la Abogacia,CN=ACA - Certificados Corporativos', 'Autoridad de Certificacion de la Abogacia');
INSERT INTO core_em_autorizacion_ca(id, codca, nombre) VALUES (17, 'C=ES,O=Agencia Catalana de Certificacio (NIF Q-0801176-I),L=Passatge de la Concepcio 11 08008 Barcelona,OU=Serveis Publics de Certificacio ECV-2,OU=Vegeu https://www.catcert.net/verCIC-2 [^] (c)03,OU=Administracions Locals de Catalunya,CN=EC-AL ', 'Agència Catalana de Certificació CATCert');

INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0101','Error al contactar con el servicio Web especificado {0} {1}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0102','Comunicación sin respuesta {0} {1}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0103','Servidor responde mensaje que no es XML');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0201','Error al generar el identificativo de petición');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0202','Error al insertar la petición {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0203','Error al actualizar el estado {0} {1}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0204','Error al actualizar el TER {0} {1}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0205','Error al actualizar la fecha de último sondeo {0} {1}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0206','Error al actualizar el fichero de respuesta {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0207','Error al recuperar el estado de la petición {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0208','Fichero de respuesta caducado {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0209','Error al comprobar las transmisiones insertadas {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0210','Error al recuperar el CIF del Organismo Requirente {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0211','Error al recuperar el TER {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0212','Error al descomponer el fichero de petición {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0213','Error al recuperar peticiones pendientes. {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0214','Error al insertar las transmisiones. {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0215','Error al actualizar el campo error {0} {1}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0216','Error al actualizar el mensaje de respuesta {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0217','Error al recuperar la CADUCIDAD {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0218','Error al descomponer el mensaje de petición {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0219','Error al recuperar el fichero de respuesta {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0220','Error al enviar la alarma de la petición {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0221','Error al comprobar las peticiones pendientes.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0222','Error al borrar las respuestas caducadas.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0223','Error al escribir el fichero de errores {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0224','Error al borrar el fichero de error {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0225','Se ha alcanzado el número máximo de respuestas para la petición servidas.{0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0226','Error al parsear el XML {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0227','Error al generar la respuesta. {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0229','La petición ya ha sido tramitada o ya existe en el sistema, está repetida');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0230','El timestamp de la petición debe ser válido y de hoy o de ayer. {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0231','Documento Incorrecto {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0232','Documento con más de un identificador.{0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0233','Titular no identificado.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0234','{0}. No se ha encontrado en base de datos configuración alguna para algún certificado asociado al código pasado por parámetro.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0235','El NIF del certificado no coincide con el tag NifSolicitante.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0236','Consentimiento del solicitante inválido. {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0237','Tag NumElementos inválido. {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0238','Información no disponible.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0239','Error al tratar los datos específicos. {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0240','Formato de documento inválido para NIE');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0241','Certificado o Respuesta Caducada');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0242','Error Genérico del BackOffice');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0243','No todas las solicitudes de transmisión hacen referencia al mismo certificado especificado en nodo <Atributos>');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0244','No existe la petición {0} en el sistema');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0246','Error con los id transmision asignados por el backoffice. O todas las transmisiones poseen identificador o ninguna.{0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0247','Error Tag TER no valido');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0248','Error respuesta con un numero de transmisiones diferente a las solicitudes incluidas en la petición {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0253','No todas las solicitudes tienen el mismo identificador de solicitante');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0301','Organismo no autorizado {0} {1}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0302','Certificado caducado {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0303','Certificado revocado {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0304','El DN del Organismo Requirente no coincide con el almacenado para la petición {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0305','Firma no válida {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0306','Error al generar la firma del mensaje {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0307','No se ha encontrado el nodo firma.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0308','Error al obtener la firma del mensaje SOAP {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0309','Error general al verificar el certificado :{0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0310','No se ha podido verificar la CA del certificado.{0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0311','No se ha encontrado el certificado firmante en el documento XML.{0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0312','NIF del emisor especificado no coincide con el Organismo Emisor');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0313','Error al Cifrar o descifrar el mensaje');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0314','Procedimiento {0} No Autorizado a consultar el servicio {1}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0401','La estructura del fichero recibido no corresponde con el esquema.{0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0402','Falta informar campo obligatorio {0} {1}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0403','Imposible obtener el contenido XML del mensaje SOAP.{0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0404','Tipo de documento del titular inválido.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0405','Error al transformar el XML en texto plano a partir de la plantilla {0}.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0406','Contenido del mensaje SOAP no esperado');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0414','El número de elementos no coincide con el número de solicitudes recibidas');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0415','El número de solicitudes es mayor que 1, ejecute el servicio en modo asíncrono.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0419','Existen identificadores de solicitud repetidos.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0501','Error de Base de Datos: {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0502','Error de sistema: {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0504','Error en la configuración {0}');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0901','Servicio no disponible');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0902','Modo síncrono no soportado.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0903','Modo asíncrono no soportado.');
INSERT INTO core_codigo_error(codigo, descripcion) VALUES ('0904','Error general Indefinido {0}');

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('cif.emisor','12345678Z','CIF del Emisor');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('nombre.emisor','Emisor1','Nombre del Emisor');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('nivelTraza','DEBUG','Nivel de traza a mostrar en los logs');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('pathLogs','.','Ruta del fichero donde guardaremos los logs');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('logDinamico.enabled','true','Parámetro que indica si se cargará de BBDD la configuración de logs o de fichero');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('core_log.enabled','true','Parámetro que indica si se guardarán trazas en CORE_LOG de los errores ocurridos');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('almacenamiento.ficheros','.','Directorio donde se guardan los ficheros de las peticiones');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('almacenamiento.transmisiones',1,'Parámetro que indica si se guardarán los nodos de las transmisiones');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('keystoreFile','file:.','Ruta del fichero keystore');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('keystoreType','jks','Tipo de keystore');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('keystorePass','.','Password del keystore');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.enabled','true','Parámetro que indica si @Firma está activo');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.idAplicacion','none','Id de aplicación para enviar a @Firma');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.url','.','URL de @Firma');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.modoValidacion',2,'Modo de validación del certificado con @Firma');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('afirma.autenticacion.aliasSignerCert','emisor1','Alias del certificado con el que firmamos la peticiones para @Firma');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('custom.cert.validation.class','none','Clase propia de validación de certificados');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('periodo.validacion.certificados',24,'Periodo de validez del certificado en caché');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('tipoId','long','Longitud del identificador de la petición');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('prefijo.idtransmision','em1','Cuando no haya indicado un prefijo asociado al servicio y este si está definido se utilizará este para la generación del id de transmisión.');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('version.datamodel.scsp','4.2.0','Especifica la versión del modelo de datos actual');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('validate.nif.emisor.enabled','true','Flag que indica si se valida el valor del nodo <NifEmisor> de la petición');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('polling.enabled','true','Parámetro que indica si el ciclo de pooling de solicitudes pendientes está activo');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('task.polling.espera','1000','Tiempo de espera antes de comenzar a ejecutar la tarea de pooling');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('task.polling.intervalo','1000','Intervalo entre ejecuciones de la tarea de pooling');

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AlmacenarBaseDatos','AlmacenarBaseDatos',1,1);
INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('ValidarCertificado','ValidarCertificado',0,0);
INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AlmacenarFichero','AlmacenarFichero',0,0);
INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('ValidarEsquema','ValidarEsquema',0,0);
INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AlmacenarFicheroPlain','AlmacenarFicheroPlain',0,0);
INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AutorizacionPeticion','AutorizacionPeticion',0,0);

INSERT INTO ems_config_group (code, position, description_key) VALUES ('GENERAL', '0', 'propietat.grup.general');
INSERT INTO ems_config_group (code, position, description_key) VALUES ('BACKOFFICES', '1', 'propietat.grup.backoffices');
INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('BACKOFFICE_SVDCCAACPASWS01', 'BACKOFFICES', '0', 'propietat.grup.backoffice.SVDCCAACPASWS01');
INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('BACKOFFICE_SVDCCAACPCWS01', 'BACKOFFICES', '1', 'propietat.grup.backoffice.SVDCCAACPCWS01');
INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('BACKOFFICE_SVDSCDDWS01', 'BACKOFFICES', '2', 'propietat.grup.backoffice.SVDSCDDWS01');
INSERT INTO ems_config_group (code, parent_code, position, description_key) VALUES ('PINBAL', 'GENERAL', '0', 'propietat.grup.pinbal');

INSERT INTO ems_config_type (code) VALUES ('BOOL');
INSERT INTO ems_config_type (code) VALUES ('TEXT');
INSERT INTO ems_config_type (code) VALUES ('INT');
INSERT INTO ems_config_type (code) VALUES ('FLOAT');
INSERT INTO ems_config_type (code) VALUES ('PASS');
INSERT INTO ems_config_type (code) VALUES ('CRON');
INSERT INTO ems_config_type (code, value) VALUES ('DIALECTE', 'es.caib.emiserv.persist.dialect.OracleCaibDialect,es.caib.emiserv.persist.dialect.PostgreSqlCaibDialect');

INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.xsd.base.path', '/app/caib/emiserv/xsd', 'propietat.xsd.base.path', 'GENERAL', '3', 'FILE', 'TEXT');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.tasca.backoffice.async.processar.pendents', '30000', 'propietat.tasca.backoffice.async.processar.pendents', 'GENERAL', '4', 'DATABASE', 'INT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code, requereix_reinici) VALUES ('es.caib.emiserv.security.mappableRoles', 'propietat.security.mappableRoles', 'GENERAL', '5', 'DATABASE', 'TEXT', '1');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code, requereix_reinici) VALUES ('es.caib.emiserv.security.useResourceRoleMappings', 'propietat.security.useResourceRoleMappings', 'GENERAL', '6', 'DATABASE', 'BOOL', '1');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.default.auditor', 'propietat.default.auditor', 'GENERAL', '7', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.jar.path', 'propietat.backoffice.jar.path', 'GENERAL', '8', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.mock', 'propietat.backoffice.mock', 'GENERAL', '9', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.caib.auth.username', 'propietat.backoffice.usuari', 'BACKOFFICES', '0', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.caib.auth.password', 'propietat.backoffice.secret', 'BACKOFFICES', '1', 'FILE', 'PASS');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.caib.soap.action', 'propietat.backoffice.soap.action', 'BACKOFFICES', '2', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.processar.datos.especificos.peticio', 'propietat.backoffice.processar.datosespecificos', 'BACKOFFICES', '3', 'DATABASE', 'BOOL');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.username', 'SIC_CERDEUTE', 'propietat.backoffice.servei.usuari', 'BACKOFFICE_SVDCCAACPASWS01', '0', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.auth.password', 'propietat.backoffice.servei.secret', 'BACKOFFICE_SVDCCAACPASWS01', '1', 'FILE', 'PASS');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPASWS01.caib.soap.action', 'http://sap.com/xi/WebService/soap1.1', 'propietat.backoffice.servei.soap.action', 'BACKOFFICE_SVDCCAACPASWS01', '2', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPASWS01.processar.datos.especificos.peticio', 'propietat.backoffice.servei.processar.datosespecificos', 'BACKOFFICE_SVDCCAACPASWS01', '3', 'DATABASE', 'BOOL');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.username', 'SIC_CERDEUTE', 'propietat.backoffice.servei.usuari', 'BACKOFFICE_SVDCCAACPCWS01', '0', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.auth.password', 'propietat.backoffice.servei.secret', 'BACKOFFICE_SVDCCAACPCWS01', '1', 'FILE', 'PASS');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPCWS01.caib.soap.action', 'http://sap.com/xi/WebService/soap1.1', 'propietat.backoffice.servei.soap.action', 'BACKOFFICE_SVDCCAACPCWS01', '2', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDCCAACPCWS01.processar.datos.especificos.peticio', 'propietat.backoffice.servei.processar.datosespecificos', 'BACKOFFICE_SVDCCAACPCWS01', '3', 'DATABASE', 'BOOL');
INSERT INTO ems_config (key, value, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.username', '$emiserv_sisdepen', 'propietat.backoffice.servei.usuari', 'BACKOFFICE_SVDSCDDWS01', '0', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDSCDDWS01.caib.auth.password', 'propietat.backoffice.servei.secret', 'BACKOFFICE_SVDSCDDWS01', '1', 'FILE', 'PASS');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDSCDDWS01.caib.soap.action', 'propietat.backoffice.servei.soap.action', 'BACKOFFICE_SVDSCDDWS01', '2', 'DATABASE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.backoffice.SVDSCDDWS01.processar.datos.especificos.peticio', 'propietat.backoffice.servei.processar.datosespecificos', 'BACKOFFICE_SVDSCDDWS01', '3', 'DATABASE', 'BOOL');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.url', 'propietat.pinbal.url', 'PINBAL', '0', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.username', 'propietat.pinbal.username', 'PINBAL', '1', 'FILE', 'TEXT');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.pinbal.password', 'propietat.pinbal.password', 'PINBAL', '2', 'FILE', 'PASS');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.log.enrutaments', 'propietat.log.enrutaments', 'GENERAL', '10', 'DATABASE', 'BOOL');
INSERT INTO ems_config (key, description_key, group_code, position, source_property, type_code) VALUES ('es.caib.emiserv.log.dir', 'propietat.log.dir', 'GENERAL', '11', 'DATABASE', 'TEXT');

-- db/changelog::init-data-serveis-1
-- Serveis, certificats i rutes de destinació d'entitat.
-- Dades importades dels CSV EMS_SERVEI.csv, CORE_SERVICIO.csv i
-- EMS_SERVEI_RUTA_DESTI.csv (exportació de producció facilitada 2026-07-22).

-- 9.1 Emissors/claus placeholder, necessaris nomes per satisfer les FK de
-- core_servicio (emisor, clavecifrado, clavefirma). NO son dades reals:
-- cal substituir-les pels valors reals (nom/cif de l'emissor i alies,
-- contrasenya i numero de serie del keystore) abans d'usar aquest entorn.
INSERT INTO core_emisor_certificado (id, nombre, cif, fechaalta) VALUES (1, 'PENDENT_CONFIG', 'PENDENT_CONFIG', SYSDATE);
INSERT INTO core_emisor_certificado (id, nombre, cif, fechaalta) VALUES (3, 'PENDENT_CONFIG', 'PENDENT_CONFIG', SYSDATE);

INSERT INTO core_clave_privada (id, alias, nombre, password, numeroserie, fechaalta, interoperabilidad) VALUES (1, 'PENDENT_CONFIG', 'PENDENT_CONFIG', 'PENDENT_CONFIG', 'PENDENT_CONFIG', SYSDATE, 0);

INSERT INTO core_clave_publica (id, alias, nombre, numeroserie, fechaalta) VALUES (1, 'PENDENT_CONFIG', 'PENDENT_CONFIG', 'PENDENT_CONFIG', SYSDATE);

-- 9.2 core_servicio (CORE_SERVICIO.csv)
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, esquemas, numeromaximoreenvios, maxsolicitudespeticion, emisor, fechaalta, caducidad, timeout, clavefirma, xpathcodigoerrorsecundario) VALUES (1, 'SVDSCDDWS01', 'Servei de consulta de dades de discapacitat', 'V3', 'XMLSignature', '/app/caib/emiserv/xsd/SVDSCDDWS01', 0, 0, 1, TO_DATE('21/09/15 00:00','DD/MM/RR HH24:MI'), 0, 0, 1, '//*[local-name()=''CodigoEstadoSecundario'']');
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, esquemas, numeromaximoreenvios, maxsolicitudespeticion, emisor, fechaalta, caducidad, timeout, clavefirma, xpathcodigoerrorsecundario) VALUES (2, 'SVDSCDWS01', 'Servei de Canvi de Domicili', 'V3', 'WS-Security', '/schemas/SVDSCDWS01v3', 0, 0, 1, TO_DATE('17/05/17 00:00','DD/MM/RR HH24:MI'), 0, 0, 1, '//*[local-name()=''CodigoEstadoSecundario'']');
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, esquemas, numeromaximoreenvios, maxsolicitudespeticion, emisor, fechaalta, caducidad, timeout, clavecifrado, clavefirma) VALUES (9, 'SVDSCDDWS02', 'Servei de consulta de dades de discapacitat V2', 'V3', 'WS-Security', '/app/caib/emiserv/xsd/SVDSCDDWS02', 0, 0, 1, TO_DATE('17/04/24 00:00','DD/MM/RR HH24:MI'), 0, 0, 1, 1);
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, esquemas, numeromaximoreenvios, maxsolicitudespeticion, emisor, fechaalta, caducidad, timeout, clavefirma) VALUES (3, 'SCMCEDU', 'Servei de consulta d''escolarització', 'V3', 'WS-Security', '/app/caib/emiserv/xsd/SCMCEDU', 0, 0, 1, TO_DATE('01/11/14 00:00','DD/MM/RR HH24:MI'), 0, 0, 1);
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, esquemas, numeromaximoreenvios, maxsolicitudespeticion, emisor, fechaalta, caducidad, timeout, clavefirma, xpathcodigoerrorsecundario) VALUES (4, 'SVDCCAACPCWS01', 'Estar al corrent de pagament per contractacions', 'V3', 'WS-Security', '/app/caib/emiserv/xsd/SVDCCAACPCWS01', 0, 0, 1, TO_DATE('01/06/16 00:00','DD/MM/RR HH24:MI'), 0, 0, 1, '//*[local-name()=''CodigoEstadoSecundario'']');
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios, maxsolicitudespeticion, emisor, fechaalta, caducidad, timeout, clavecifrado, clavefirma, xpathcodigoerrorsecundario) VALUES (5, 'SVDCCAACPASWS01', 'Estar al corrent de pagament per ajudes', 'V3', 'WS-Security', 0, 0, 1, TO_DATE('01/06/16 00:00','DD/MM/RR HH24:MI'), 0, 0, 1, 1, '//*[local-name()=''CodigoEstadoSecundario'']');
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, xpathcifradosincrono, xpathcifradoasincrono, algoritmocifrado, numeromaximoreenvios, maxsolicitudespeticion, prefijoidtransmision, emisor, fechaalta, caducidad, timeout, validacionfirma, clavefirma) VALUES (6, 'CODCERTIFICADOV3', 'Certificado prueba V3', 'V3', 'WSSecurity', '//*[local-name()=''DatosEspecificos'']', '//*[local-name()=''Transmisiones'']', 'Basic128Rsa15', 5, 0, 'EMISERV', 3, TO_DATE('08/08/11 16:17','DD/MM/RR HH24:MI'), 7, 60, 'estricto', 1);
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios, maxsolicitudespeticion, prefijoidtransmision, emisor, fechaalta, caducidad, timeout, validacionfirma, clavefirma) VALUES (7, 'CODCERTIFICADOV2', 'Certificado prueba V2', 'V2', 'XMLSignature', 5, 0, 'EMISERV', 3, TO_DATE('08/08/11 16:17','DD/MM/RR HH24:MI'), 7, 60, 'estricto', 1);
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, esquemas, numeromaximoreenvios, maxsolicitudespeticion, emisor, fechaalta, caducidad, timeout, clavefirma) VALUES (8, 'SVDSCDWS02', 'Servei de consulta de dades de discapacitat V2', 'V3', 'WS-Security', '/app/caib/emiserv/xsd/SVDSCDWS02', 0, 0, 1, TO_DATE('02/02/24 00:00','DD/MM/RR HH24:MI'), 0, 0, 1);
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios, maxsolicitudespeticion, emisor, fechaalta, caducidad, timeout, clavecifrado, clavefirma) VALUES (10, 'SVDCCAAIRPHWS01', 'Consulta dades de parelles de fet', 'V3', 'WS-Security', 0, 0, 1, TO_DATE('22/04/25 00:00','DD/MM/RR HH24:MI'), 0, 0, 1, 1);
INSERT INTO core_servicio (id, codcertificado, descripcion, versionesquema, tiposeguridad, numeromaximoreenvios, maxsolicitudespeticion, emisor, fechaalta, caducidad, timeout, clavecifrado, clavefirma) VALUES (11, 'SVDNIVCAT', 'Consulta de nivell de Català', 'V3', 'WS-Security', 0, 0, 1, TO_DATE('21/04/26 00:00','DD/MM/RR HH24:MI'), 0, 0, 1, 1);

-- 9.3 ems_servei (EMS_SERVEI.csv)
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backcaib_async_ter, backcaib_async_tipus, backcaib_autenticacio, backcaib_url, backoffice_class, xsd_activa, xsd_esquema_bak, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (41, 'SVDSCDDWS01', 'Servei de consulta de dades de discapacitat', 0, 1, 1, 0, 0, 2, 'https://proves.caib.es/precodews/ws/PinBalService', 'es.caib.emiserv.backoffice.CaibBackoffice', 1, '/schemas/SVDSCDDWS01v3', 'u81599', TO_DATE('23/09/15 14:30','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('24/05/24 13:24','DD/MM/RR HH24:MI'), 39);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backcaib_autenticacio, resolver_class, backcaib_async_ter, xsd_activa, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (61, 'SCDHPAJU', 'Servei de consulta de padró històric', 1, 1, 1, 0, 'es.caib.emiserv.logic.resolver.EmpadronamentEntitatResolver', 0, 0, 'u81599', TO_DATE('04/04/16 09:53','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('13/04/16 08:56','DD/MM/RR HH24:MI'), 1);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backcaib_autenticacio, resolver_class, backcaib_async_ter, xsd_activa, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (62, 'SCDCPAJU', 'Servei de consulta de padró de convivència', 1, 1, 1, 0, 'es.caib.emiserv.logic.resolver.EmpadronamentEntitatResolver', 0, 0, 'u81599', TO_DATE('04/04/16 09:55','DD/MM/RR HH24:MI'), 'e43110511r', TO_DATE('25/01/22 16:18','DD/MM/RR HH24:MI'), 9);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backoffice_class, backcaib_autenticacio, backcaib_url, backcaib_async_tipus, backcaib_async_ter, xsd_activa, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (14302, 'SVDSCDWS01', 'Servei de Canvi de Domicili', 0, 1, 1, 'es.caib.emiserv.backoffice.CaibBackoffice', 0, 'http://emiasin.fundaciobit.org/emiasin/ws/v1/EmiservBackoffice', 1, 60, 0, 'u88652', TO_DATE('18/05/17 13:47','DD/MM/RR HH24:MI'), 'e43110511r', TO_DATE('13/07/21 10:20','DD/MM/RR HH24:MI'), 12);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backoffice_class, backcaib_autenticacio, backcaib_url, backcaib_async_ter, xsd_activa, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (87897, 'SVDSCDDWS02', 'Servei de consulta de dades de discapacitat V2', 0, 1, 1, 'es.caib.emiserv.backoffice.CaibBackoffice', 2, 'https://proves.caib.es/precodews/ws/PinBalService', 0, 1, 'u81599', TO_DATE('18/04/24 11:18','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('24/05/24 13:25','DD/MM/RR HH24:MI'), 4);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backcaib_autenticacio, resolver_class, response_resolver_class, backcaib_async_ter, xsd_activa, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (281, 'SVDSCTFNWS01', 'Servei de consulta de familia nombrosa', 2, 1, 1, 0, 'es.caib.emiserv.logic.resolver.SampleEntitatResolver', 'es.caib.emiserv.logic.resolver.FamiliaNombrosaResponseResolver', 0, 0, 'u81599', TO_DATE('24/05/16 11:16','DD/MM/RR HH24:MI'), 'e43110511r', TO_DATE('30/09/21 09:55','DD/MM/RR HH24:MI'), 15);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backoffice_class, backcaib_autenticacio, backcaib_url, backcaib_async_ter, xsd_activa, xsd_esquema_bak, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (21, 'SCMCEDU', 'Servei de consulta d''escolarització', 0, 1, 1, 'es.caib.emiserv.backoffice.CaibBackoffice', 0, 'https://proves.caib.es/xestibback/ws/EmiservBackoffice', 0, 1, '/schemas/SCMCEDUv3', 'u81599', TO_DATE('24/11/14 14:22','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('06/10/25 13:29','DD/MM/RR HH24:MI'), 39);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backoffice_class, backcaib_autenticacio, backcaib_url, backcaib_async_ter, xsd_activa, xsd_esquema_bak, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (741, 'SVDCCAACPCWS01', 'Estar al corrent de pagament per contractacions', 0, 1, 1, 'es.caib.emiserv.backoffice.CaibBackoffice', 2, 'https://geminis.caib.es:52001/sap/xi/engine?type=entry&amp;version=3.0&amp;Sender.Service=EMISERV_BC&amp;Interface=http%3A%2F%2Fibsalut.caib.es%2Fxi%2Fpagos%5EPeticionSincrona_OB_SY', 0, 1, '/schemas/SVDCCAACPCWS01v3', 'u81599', TO_DATE('27/06/16 09:00','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('21/07/23 12:18','DD/MM/RR HH24:MI'), 16);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backoffice_class, backcaib_autenticacio, backcaib_url, backcaib_async_ter, xsd_activa, xsd_esquema_bak, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (742, 'SVDCCAACPASWS01', 'Estar al corrent de pagament per ajudes', 0, 1, 1, 'es.caib.emiserv.backoffice.CaibBackoffice', 2, 'https://geminis.caib.es:52001/sap/xi/engine?type=entry&amp;version=3.0&amp;Sender.Service=EMISERV_BC&amp;Interface=http%3A%2F%2Fibsalut.caib.es%2Fxi%2Fpagos%5EPeticionSincrona_OB_SY', 0, 1, '/schemas/SVDCCAACPASWS01v3', 'u81599', TO_DATE('27/06/16 09:03','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('13/07/21 11:49','DD/MM/RR HH24:MI'), 16);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backoffice_class, backcaib_autenticacio, backcaib_url, backcaib_async_ter, xsd_activa, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (130103, 'SVDCCAAIRPHWS01', 'Consulta dades de parelles de fet', 0, 1, 1, 'es.caib.emiserv.backoffice.CaibBackoffice', 0, 'https://proves.caib.es/repaesws/EmiservBackoffice', 0, 1, 'u81599', TO_DATE('23/04/25 09:09','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('15/05/25 12:29','DD/MM/RR HH24:MI'), 2);
INSERT INTO ems_servei (id, codi, nom, tipus, actiu, configurat, backoffice_class, backcaib_autenticacio, backcaib_url, backcaib_async_ter, xsd_activa, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (144177, 'SVDNIVCAT', 'Consulta de nivell de Català', 0, 1, 1, 'es.caib.emiserv.backoffice.CaibBackoffice', 2, 'https://proves.caib.es/avacat2api/interna/EmiservBackoffice', 0, 1, 'u81599', TO_DATE('22/04/26 12:15','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('15/05/26 16:00','DD/MM/RR HH24:MI'), 6);

-- 9.4 ems_servei_ruta_desti (EMS_SERVEI_RUTA_DESTI.csv)
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (81058, '7027', 15, 'http://10.215.224.130:8080/emiservapi/interna/scsp/services/peticionSincrona', 61, 'u82545', TO_DATE('09/10/23 19:43','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('09/10/23 19:43','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (105, '7015', 1, 'http://10.215.224.16:8080/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('14/04/16 12:05','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('27/12/16 10:32','DD/MM/RR HH24:MI'), 1);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (40601, '7040', 9, 'https://10.215.224.242:8021/emisor/services/peticionSincrona', 62, 'u82545', TO_DATE('27/10/20 14:41','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('27/10/20 14:41','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (36881, '7011', 8, 'https://10.215.224.59:8002/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u82545', TO_DATE('30/03/20 14:31','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('30/03/20 14:31','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (57716, '7001', 12, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('12/04/22 14:41','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('12/04/22 14:41','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (57717, '7001', 0, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('12/04/22 14:42','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('12/04/22 14:42','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58500, '7017', 16, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('10/05/22 10:25','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('10/05/22 10:25','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58501, '7041', 17, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('10/05/22 10:25','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('10/05/22 10:25','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58502, '7065', 18, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('10/05/22 10:25','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('10/05/22 10:25','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58503, '7017', 4, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('10/05/22 10:26','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('10/05/22 10:26','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58504, '7065', 5, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('10/05/22 10:26','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('10/05/22 10:26','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58505, '7041', 6, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('10/05/22 10:26','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('10/05/22 10:26','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (71938, '7026', 12, 'http://10.215.224.227:8080/emiservapi/interna/scsp/services/peticionSincrona', 61, 'u82545', TO_DATE('14/04/23 10:33','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('14/04/23 10:33','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (71937, '7026', 25, 'http://10.215.224.227:8080/emiservapi/interna/scsp/services/peticionSincrona', 62, 'u82545', TO_DATE('14/04/23 10:32','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('14/04/23 10:32','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (82683, '7027', 28, 'http://10.215.224.130:8004/emiservapi/interna/scsp/services/peticionSincrona', 62, 'u82545', TO_DATE('21/11/23 12:51','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('21/11/23 12:51','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (81, '7004', 0, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('13/04/16 08:58','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('27/12/16 10:32','DD/MM/RR HH24:MI'), 1);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (82, '7040', NULL, 'https://10.215.224.242:8021/emisor/services/peticionSincrona', 61, 'u81599', TO_DATE('13/04/16 08:59','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('13/04/16 08:59','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (83, '7011', NULL, 'https://10.215.224.59:8002/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('13/04/16 08:59','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('13/04/16 08:59','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (84, '7004', NULL, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('13/04/16 08:59','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('13/04/16 08:59','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58339, '7056', 1, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('04/05/22 10:10','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('04/05/22 10:10','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58337, '7901', 13, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('04/05/22 10:08','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('04/05/22 10:08','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58338, '7056', 14, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('04/05/22 10:09','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('04/05/22 10:09','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (58340, '7901', 2, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('04/05/22 10:10','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('04/05/22 10:10','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (67539, '7047', 22, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('11/01/23 13:57','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('11/01/23 13:57','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (60248, '7044', 19, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('13/06/22 09:15','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('13/06/22 09:15','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (60249, '7044', 7, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('13/06/22 09:15','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('13/06/22 09:15','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (67540, '7049', 23, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('11/01/23 13:57','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('11/01/23 13:57','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (67541, '7008', 8, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('11/01/23 13:58','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('11/01/23 13:58','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (67542, '7030', 9, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('11/01/23 13:58','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('11/01/23 13:58','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (67543, '7047', 10, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('11/01/23 13:58','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('11/01/23 13:58','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (73137, '7033', 26, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('05/05/23 12:01','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('05/05/23 12:01','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (73138, '7022', 27, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('05/05/23 12:01','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('05/05/23 12:01','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (73139, '7033', 13, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('05/05/23 12:01','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('05/05/23 12:01','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (73140, '7022', 14, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('05/05/23 12:02','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('05/05/23 12:02','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (67544, '7049', 11, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 61, 'u81599', TO_DATE('11/01/23 13:58','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('11/01/23 13:58','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (67537, '7008', 20, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('11/01/23 13:57','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('11/01/23 13:57','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (67538, '7030', 21, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('11/01/23 13:57','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('11/01/23 13:57','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (70092, 'MALLORCA', 1, 'https://10.215.224.60:8081/scsp-emisor-3.3.0/services/peticionSincrona', 281, 'u81599', TO_DATE('06/03/23 14:05','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('06/03/23 14:05','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (121256, '7001', 29, 'http://10.215.224.61:8182/scsp-emisor-3.3.0/services/peticionSincrona', 62, 'u81599', TO_DATE('10/07/24 10:32','DD/MM/RR HH24:MI'), 'u81599', TO_DATE('10/07/24 10:32','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135099, '7064', 31, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 62, 'u82545', TO_DATE('15/10/25 12:27','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:27','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (134977, 'MENORCA', 2, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 281, 'u82545', TO_DATE('10/10/25 10:25','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('10/10/25 10:25','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135078, '7002', 30, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 62, 'u82545', TO_DATE('15/10/25 12:25','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:25','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135100, '7032', 34, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 62, 'u82545', TO_DATE('15/10/25 12:29','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:29','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135101, '7037', 35, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 62, 'u82545', TO_DATE('15/10/25 12:29','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:29','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135088, '7902', 36, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 62, 'u82545', TO_DATE('15/10/25 12:29','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:29','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135086, '7015', 32, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 62, 'u82545', TO_DATE('15/10/25 12:28','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:28','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135087, '7023', 33, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 62, 'u82545', TO_DATE('15/10/25 12:28','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:28','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135089, '7052', 37, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 62, 'u82545', TO_DATE('15/10/25 12:30','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:30','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135094, '7002', 16, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 61, 'u82545', TO_DATE('15/10/25 12:38','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:38','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135095, '7064', 17, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 61, 'u82545', TO_DATE('15/10/25 12:38','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:38','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135096, '7015', 18, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 61, 'u82545', TO_DATE('15/10/25 12:39','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:39','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135097, '7023', 19, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 61, 'u82545', TO_DATE('15/10/25 12:39','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:39','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135098, '7032', 20, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 61, 'u82545', TO_DATE('15/10/25 12:39','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:39','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135119, '7037', 21, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 61, 'u82545', TO_DATE('15/10/25 12:40','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:40','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135120, '7902', 22, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 61, 'u82545', TO_DATE('15/10/25 12:40','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:40','DD/MM/RR HH24:MI'), 0);
INSERT INTO ems_servei_ruta_desti (id, entitat_codi, ordre, url, servei_id, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) VALUES (135121, '7052', 23, 'http://10.215.224.18:8080/scsp-emisor-SILME_test/services/peticionSincrona', 61, 'u82545', TO_DATE('15/10/25 12:41','DD/MM/RR HH24:MI'), 'u82545', TO_DATE('15/10/25 12:41','DD/MM/RR HH24:MI'), 0);

-- 9.5 Avançar les seqüències per sobre dels IDs explícits inserits a 9.1-9.4,
-- perquè les properes files creades per l'aplicació (via Hibernate) no
-- col·lideixin amb aquests IDs de producció ja usats.
ALTER SEQUENCE ems_hibernate_sequence INCREMENT BY 200000;
SELECT ems_hibernate_sequence.NEXTVAL FROM DUAL;
SELECT ems_hibernate_sequence.NEXTVAL FROM DUAL;
ALTER SEQUENCE ems_hibernate_sequence INCREMENT BY 1;

ALTER SEQUENCE id_servicio_sequence INCREMENT BY 1000;
SELECT id_servicio_sequence.NEXTVAL FROM DUAL;
SELECT id_servicio_sequence.NEXTVAL FROM DUAL;
ALTER SEQUENCE id_servicio_sequence INCREMENT BY 1;

ALTER SEQUENCE id_emisor_sequence INCREMENT BY 1000;
SELECT id_emisor_sequence.NEXTVAL FROM DUAL;
SELECT id_emisor_sequence.NEXTVAL FROM DUAL;
ALTER SEQUENCE id_emisor_sequence INCREMENT BY 1;

ALTER SEQUENCE id_clave_privada_sequence INCREMENT BY 1000;
SELECT id_clave_privada_sequence.NEXTVAL FROM DUAL;
SELECT id_clave_privada_sequence.NEXTVAL FROM DUAL;
ALTER SEQUENCE id_clave_privada_sequence INCREMENT BY 1;

ALTER SEQUENCE id_clave_publica_sequence INCREMENT BY 1000;
SELECT id_clave_publica_sequence.NEXTVAL FROM DUAL;
SELECT id_clave_publica_sequence.NEXTVAL FROM DUAL;
ALTER SEQUENCE id_clave_publica_sequence INCREMENT BY 1;


COMMIT;
