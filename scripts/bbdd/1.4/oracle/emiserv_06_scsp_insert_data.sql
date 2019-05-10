
  
  
Insert into SCSP_ESTADO_PETICION (CODIGO,MENSAJE) values('0001', 'Pendiente');
Insert into SCSP_ESTADO_PETICION (CODIGO,MENSAJE) values('0002', 'En proceso');
Insert into SCSP_ESTADO_PETICION (CODIGO,MENSAJE) values('0003', 'Tramitada');
Insert into SCSP_ESTADO_PETICION (CODIGO,MENSAJE) values('0004', 'En proceso Polling');

--------------------------------------------------------
--  Inserts for Table AUTORIZACION_CA
--  No están todas las CA precargadas, unicamente las mas habituales
--------------------------------------------------------

Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('OU=FNMT Clase 2 CA, O=FNMT, C=ES', 'Fabrica Nacional de Moneda y Timbre');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('OU=FNMT Clase 2 CA,O=FNMT,C=ES', 'Fabrica Nacional de Moneda y Timbre');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('OU=AC APE, O=FNMT-RCM, C=ES', 'Fabrica Nacional de Moneda y Timbre');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('OU=AC APE,O=FNMT-RCM,C=ES', 'Fabrica Nacional de Moneda y Timbre');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('CN=AC DGP 001, OU=CNP, O=DIRECCION GENERAL DE LA POLICIA, C=ES', 'DIRECCION GENERAL DE LA POLICIA');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('CN=EC-SAFP, OU=Secretaria d''Administracio i Funcio Publica, OU="Vegeu https://www.catcert.net/verCIC-2 [^] (c)03", OU=Serveis Publics de Certificacio ECV-2, L=Passatge de la Concepcio 11 08008 Barcelona, O=Agencia Catalana de Certificacio (NIF Q-0801176-I), C=ES', 'Secretaria dŽAdministracio i Funcio Publica');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('CN=EAEko HAetako langileen CA - CA personal de AAPP vascas (2), OU=AZZ Ziurtagiri publikoa - Certificado publico SCA, O=IZENPE S.A., C=ES', 'IZENPE S.A');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('CN=AC CAMERFIRMA AAPP, SERIALNUMBER=A82743287, OU=AC CAMERFIRMA, L=MADRID (Ver en https://www.camerfirma.com/address [^]), O=AC CAMERFIRMA S.A., C=ES', 'AC Camerfirma');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('CN=AC Administración Pública, SERIALNUMBER=Q2826004J, OU=CERES, O=FNMT-RCM, C=ES', 'Fabrica Nacional de Moneda y Timbre');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('CN=AC Camerfirma Express Corporate Server v3, O=AC Camerfirma SA, OU=http://www.camerfirma.com, [^] SERIALNUMBER=A82743287, L=Madrid (see current address at www.camerfirma.com/address), EMAILADDRESS=info@camerfirma.com, C=ES', 'AC Camerfirma');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('CN=AC Camerfirma Certificados Camerales, O=AC Camerfirma SA, SERIALNUMBER=A82743287, L=Madrid (see current address at www.camerfirma.com/address), EMAILADDRESS=ac_camerfirma_cc@camerfirma.com, C=ES', 'AC Camerfirma');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('CN=EC-AL, OU=Administracions Locals de Catalunya, OU="Vegeu https://www.catcert.net/verCIC-2 [^] (c)03", OU=Serveis Publics de Certificacio ECV-2, L=Passatge de la Concepcio 11 08008 Barcelona, O=Agencia Catalana de Certificacio (NIF Q-0801176-I), C=ES', 'Agència Catalana de Certificació CATCert');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('C=ES, O=Generalitat Valenciana, OU=PKIGVA, CN=ACCV-CA2', 'Autoritat de Certificació de la Comunitat Valenciana (ACCV)');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('C=ES,O=FNMT,OU=FNMT Clase 2 CA', 'Fabrica Nacional de Moneda y Timbre');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('C=ES, L=MADRID, O=MINISTERIO DE TRABAJO E INMIGRACION, OU=SUBDIRECCION GENERAL DE PROCESO DE DATOS, OU=PRESTADOR DE SERVICIOS DE CERTIFICACION MTIN, SERIALNUMBER=S2819001E, CN=AC1 RAIZ MTIN', 'Prestador de Servicios de Certificación del Ministerio de Trabajo e Inmigración (PSCMTIN).');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('C=ES,L=Madrid,E=ac@acabogacia.org,O=Consejo General de la Abogacia NIF:Q-2863006I,OU=Autoridad de Certificacion de la Abogacia,CN=ACA - Certificados Corporativos', 'Autoridad de Certificacion de la Abogacia');
Insert into CORE_EM_AUTORIZACION_CA (CODCA,NOMBRE) values ('C=ES,O=Agencia Catalana de Certificacio (NIF Q-0801176-I),L=Passatge de la Concepcio 11 08008 Barcelona,OU=Serveis Publics de Certificacio ECV-2,OU=Vegeu https://www.catcert.net/verCIC-2 [^] (c)03,OU=Administracions Locals de Catalunya,CN=EC-AL ', 'Agència Catalana de Certificació CATCert');

UPDATE CORE_EM_AUTORIZACION_CA  SET ID=ID_AUTORIZACION_CA_SEQ.NEXTVAL;
COMMIT;

--------------------------------------------------------
--  Inserts for Table SCSP_CODIGO_ERROR
--------------------------------------------------------
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0101','Error al contactar con el servicio Web especificado {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0102','Comunicación sin respuesta {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0103','Servidor responde mensaje que no es XML');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0201','Error al generar el identificativo de petición');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0202','Error al insertar la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0203','Error al actualizar el estado {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0204','Error al actualizar el TER {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0205','Error al actualizar la fecha de último sondeo {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0206','Error al actualizar el fichero de respuesta {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0207','Error al recuperar el estado de la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0208','Fichero de respuesta caducado {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0209','Error al comprobar las transmisiones insertadas {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0210','Error al recuperar el CIF del Organismo Requirente {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0211','Error al recuperar el TER {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0212','Error al descomponer el fichero de petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0213','Error al recuperar peticiones pendientes. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0214','Error al insertar las transmisiones. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0215','Error al actualizar el campo error {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0216','Error al actualizar el mensaje de respuesta {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0217','Error al recuperar la CADUCIDAD {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0218','Error al descomponer el mensaje de petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0219','Error al recuperar el fichero de respuesta {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0220','Error al enviar la alarma de la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0221','Error al comprobar las peticiones pendientes.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0222','Error al borrar las respuestas caducadas.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0223','Error al escribir el fichero de errores {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0224','Error al borrar el fichero de error {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0225','Se ha alcanzado el número máximo de respuestas para la petición servidas.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0226','Error al parsear el XML {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0227','Error al generar la respuesta. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0229','La petición ya ha sido tramitada o ya existe en el sistema, está repetida');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0230','El timestamp de la petición debe ser válido y de hoy o de ayer. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0231','Documento Incorrecto {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0232','Documento con más de un identificador.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0233','Titular no identificado.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0234','{0}. No se ha encontrado en base de datos configuración alguna para algún certificado asociado al código pasado por parámetro.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0235','El NIF del certificado no coincide con el tag NifSolicitante.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0236','Consentimiento del solicitante inválido. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0237','Tag NumElementos inválido. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0238','Información no disponible.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0239','Error al tratar los datos específicos. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0240','Formato de documento inválido para NIE');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0241','Certificado o Respuesta Caducada');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0242','Error Genérico del BackOffice');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0243','No todas las solicitudes de transmisión hacen referencia al mismo certificado especificado en nodo <Atributos>');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0244','No existe la petición {0} en el sistema');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0246','Error con los id transmision asignados por el backoffice. O todas las transmisiones poseen identificador o ninguna.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0247','Error Tag TER no valido');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0248','Error respuesta con un numero de transmisiones diferente a las solicitudes incluidas en la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0253','No todas las solicitudes tienen el mismo identificador de solicitante');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0301','Organismo no autorizado {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0302','Certificado caducado {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0303','Certificado revocado {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0304','El DN del Organismo Requirente no coincide con el almacenado para la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0305','Firma no válida {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0306','Error al generar la firma del mensaje {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0307','No se ha encontrado el nodo firma.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0308','Error al obtener la firma del mensaje SOAP {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0309','Error general al verificar el certificado :{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0310','No se ha podido verificar la CA del certificado.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0311','No se ha encontrado el certificado firmante en el documento XML.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0312','NIF del emisor especificado no coincide con el Organismo Emisor');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0313','Error al Cifrar o descifrar el mensaje');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0314','Procedimiento {0} No Autorizado a consultar el servicio {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0401','La estructura del fichero recibido no corresponde con el esquema.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0402','Falta informar campo obligatorio {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0403','Imposible obtener el contenido XML del mensaje SOAP.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0404','Tipo de documento del titular inválido.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0405','Error al transformar el XML en texto plano a partir de la plantilla {0}.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0406','Contenido del mensaje SOAP no esperado');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0414','El número de elementos no coincide con el número de solicitudes recibidas');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0415','El número de solicitudes es mayor que 1, ejecute el servicio en modo asíncrono.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0419','Existen identificadores de solicitud repetidos.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0501','Error de Base de Datos: {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0502','Error de sistema: {0}');
INSERT INTO SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0504','Error en la configuración {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0901','Servicio no disponible');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0902','Modo síncrono no soportado.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0903','Modo asíncrono no soportado.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0904','Error general Indefinido {0}');

INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('cif.emisor','12345678Z','CIF del Emisor');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('nombre.emisor','Emisor1','Nombre del Emisor');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('nivelTraza','DEBUG','Nivel de traza a mostrar en los logs');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('pathLogs','.','Ruta del fichero donde guardaremos los logs');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('logDinamico.enabled','true','Parámetro que indica si se cargará de BBDD la configuración de logs o de fichero');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('core_log.enabled','true','Parámetro que indica si se guardarán trazas en CORE_LOG de los errores ocurridos');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('almacenamiento.ficheros','.','Directorio donde se guardan los ficheros de las peticiones');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('almacenamiento.transmisiones',1,'Parámetro que indica si se guardarán los nodos de las transmisiones');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('keystoreFile','.','Ruta del fichero keystore');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('keystoreType','jks','Tipo de keystore');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('keystorePass','.','Password del keystore');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('afirma.enabled','true','Parámetro que indica si @Firma está activo');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('afirma.idAplicacion','none','Id de aplicación para enviar a @Firma');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('afirma.url','.','URL de @Firma');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('afirma.modoValidacion',2,'Modo de validación del certificado con @Firma');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('afirma.autenticacion.aliasSignerCert','emisor1','Alias del certificado con el que firmamos la peticiones para @Firma');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('custom.cert.validation.class','none','Clase propia de validación de certificados');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('periodo.validacion.certificados',24,'Periodo de validez del certificado en caché');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('tipoId','long','Longitud del identificador de la petición');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('prefijo.idtransmision','em1','Cuando no haya indicado un prefijo asociado al servicio y este si está definido se utilizará este para la generación del id de transmisión.');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('version.datamodel.scsp','4.2.0','Especifica la versión del modelo de datos actual');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES('validate.nif.emisor.enabled','true','Flag que indica si se valida el valor del nodo <NifEmisor> de la petición');