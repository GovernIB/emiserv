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

INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES ('keystoreFile','.','Ruta del fichero keystore');

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

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AlmacenarBaseDatos','AlmacenarBaseDatos',1,1);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('ValidarCertificado','ValidarCertificado',0,0);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AlmacenarFichero','AlmacenarFichero',0,0);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('ValidarEsquema','ValidarEsquema',0,0);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AlmacenarFicheroPlain','AlmacenarFicheroPlain',0,0);

INSERT INTO core_modulo(nombre, descripcion, activoentrada, activosalida) VALUES ('AutorizacionPeticion','AutorizacionPeticion',0,0);

SELECT id_autorizacion_ca_seq.nextval FROM core_em_autorizacion_ca;