INSERT INTO scsp_estado_peticion (codigo, mensaje)
VALUES('0001', 'pendiente');


INSERT INTO scsp_estado_peticion (codigo, mensaje)
VALUES('0002', 'en proceso');


INSERT INTO scsp_estado_peticion (codigo, mensaje)
VALUES('0003', 'tramitada');


INSERT INTO scsp_estado_peticion (codigo, mensaje)
VALUES('0004', 'en proceso polling');

--------------------------------------------------------
--  inserts for table autorizacion_ca
--  no están todas las ca precargadas, unicamente las mas habituales
--------------------------------------------------------

INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('ou=fnmt clase 2 ca, o=fnmt, c=es', 'fabrica nacional de moneda y timbre');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('ou=fnmt clase 2 ca,o=fnmt,c=es', 'fabrica nacional de moneda y timbre');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('ou=ac ape, o=fnmt-rcm, c=es', 'fabrica nacional de moneda y timbre');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('ou=ac ape,o=fnmt-rcm,c=es', 'fabrica nacional de moneda y timbre');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('cn=ac dgp 001, ou=cnp, o=direccion general de la policia, c=es', 'direccion general de la policia');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('cn=ec-safp, ou=secretaria d''administracio i funcio publica, ou=vegeu https://www.catcert.net/vercic-2 [^] (c)03, ou=serveis publics de certificacio ecv-2, l=passatge de la concepcio 11 08008 barcelona, o=agencia catalana de certificacio (nif q-0801176-i), c=es', 'secretaria džadministracio i funcio publica');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('cn=eaeko haetako langileen ca - ca personal de aapp vascas (2), ou=azz ziurtagiri publikoa - certificado publico sca, o=izenpe s.a., c=es', 'izenpe s.a');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('cn=ac camerfirma aapp, serialnumber=a82743287, ou=ac camerfirma, l=madrid (ver en https://www.camerfirma.com/address [^]), o=ac camerfirma s.a., c=es', 'ac camerfirma');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('cn=ac administración pública, serialnumber=q2826004j, ou=ceres, o=fnmt-rcm, c=es', 'fabrica nacional de moneda y timbre');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('cn=ac camerfirma express corporate server v3, o=ac camerfirma sa, ou=http://www.camerfirma.com, [^] serialnumber=a82743287, l=madrid (see current address at www.camerfirma.com/address), emailaddress=info@camerfirma.com, c=es', 'ac camerfirma');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('cn=ac camerfirma certificados camerales, o=ac camerfirma sa, serialnumber=a82743287, l=madrid (see current address at www.camerfirma.com/address), emailaddress=ac_camerfirma_cc@camerfirma.com, c=es', 'ac camerfirma');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('cn=ec-al, ou=administracions locals de catalunya, ou=vegeu https://www.catcert.net/vercic-2 [^] (c)03, ou=serveis publics de certificacio ecv-2, l=passatge de la concepcio 11 08008 barcelona, o=agencia catalana de certificacio (nif q-0801176-i), c=es', 'agència catalana de certificació catcert');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('c=es, o=generalitat valenciana, ou=pkigva, cn=accv-ca2', 'autoritat de certificació de la comunitat valenciana (accv)');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('c=es,o=fnmt,ou=fnmt clase 2 ca', 'fabrica nacional de moneda y timbre');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('c=es, l=madrid, o=ministerio de trabajo e inmigracion, ou=subdireccion general de proceso de datos, ou=prestador de servicios de certificacion mtin, serialnumber=s2819001e, cn=ac1 raiz mtin', 'prestador de servicios de certificación del ministerio de trabajo e inmigración (pscmtin).');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('c=es,l=madrid,e=ac@acabogacia.org,o=consejo general de la abogacia nif:q-2863006i,ou=autoridad de certificacion de la abogacia,cn=aca - certificados corporativos', 'autoridad de certificacion de la abogacia');


INSERT INTO core_em_autorizacion_ca (codca, nombre)
VALUES ('c=es,o=agencia catalana de certificacio (nif q-0801176-i),l=passatge de la concepcio 11 08008 barcelona,ou=serveis publics de certificacio ecv-2,ou=vegeu https://www.catcert.net/vercic-2 [^] (c)03,ou=administracions locals de catalunya,cn=ec-al ', 'agència catalana de certificació catcert');

--------------------------------------------------------
--  inserts for table scsp_codigo_error
--------------------------------------------------------

INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0101','error al contactar con el servicio web especificado {0} {1}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0102','comunicación sin respuesta {0} {1}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0103','servidor responde mensaje que no es xml');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0201','error al generar el identificativo de petición');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0202','error al insertar la petición {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0203','error al actualizar el estado {0} {1}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0204','error al actualizar el ter {0} {1}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0205','error al actualizar la fecha de último sondeo {0} {1}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0206','error al actualizar el fichero de respuesta {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0207','error al recuperar el estado de la petición {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0208','fichero de respuesta caducado {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0209','error al comprobar las transmisiones insertadas {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0210','error al recuperar el cif del organismo requirente {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0211','error al recuperar el ter {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0212','error al descomponer el fichero de petición {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0213','error al recuperar peticiones pendientes. {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0214','error al insertar las transmisiones. {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0215','error al actualizar el campo error {0} {1}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0216','error al actualizar el mensaje de respuesta {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0217','error al recuperar la caducidad {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0218','error al descomponer el mensaje de petición {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0219','error al recuperar el fichero de respuesta {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0220','error al enviar la alarma de la petición {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0221','error al comprobar las peticiones pendientes.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0222','error al borrar las respuestas caducadas.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0223','error al escribir el fichero de errores {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0224','error al borrar el fichero de error {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0225','se ha alcanzado el número máximo de respuestas para la petición servidas.{0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0226','error al parsear el xml {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0227','error al generar la respuesta. {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0229','la petición ya ha sido tramitada o ya existe en el sistema, está repetida');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0230','el timestamp de la petición debe ser válido y de hoy o de ayer. {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0231','documento incorrecto {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0232','documento con más de un identificador.{0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0233','titular no identificado.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0234','{0}. no se ha encontrado en base de datos configuración alguna para algún certificado asociado al código pasado por parámetro.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0235','el nif del certificado no coincide con el tag nifsolicitante.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0236','consentimiento del solicitante inválido. {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0237','tag numelementos inválido. {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0238','información no disponible.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0239','error al tratar los datos específicos. {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0240','formato de documento inválido para nie');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0241','certificado o respuesta caducada');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0242','error genérico del backoffice');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0243','no todas las solicitudes de transmisión hacen referencia al mismo certificado especificado en nodo <atributos>');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0244','no existe la petición {0} en el sistema');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0246','error con los id transmision asignados por el backoffice. o todas las transmisiones poseen identificador o ninguna.{0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0247','error tag ter no valido');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0248','error respuesta con un numero de transmisiones diferente a las solicitudes incluidas en la petición {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0253','no todas las solicitudes tienen el mismo identificador de solicitante');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0301','organismo no autorizado {0} {1}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0302','certificado caducado {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0303','certificado revocado {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0304','el dn del organismo requirente no coincide con el almacenado para la petición {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0305','firma no válida {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0306','error al generar la firma del mensaje {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0307','no se ha encontrado el nodo firma.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0308','error al obtener la firma del mensaje soap {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0309','error general al verificar el certificado :{0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0310','no se ha podido verificar la ca del certificado.{0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0311','no se ha encontrado el certificado firmante en el documento xml.{0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0312','nif del emisor especificado no coincide con el organismo emisor');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0313','error al cifrar o descifrar el mensaje');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0314','procedimiento {0} no autorizado a consultar el servicio {1}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0401','la estructura del fichero recibido no corresponde con el esquema.{0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0402','falta informar campo obligatorio {0} {1}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0403','imposible obtener el contenido xml del mensaje soap.{0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0404','tipo de documento del titular inválido.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0405','error al transformar el xml en texto plano a partir de la plantilla {0}.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0406','contenido del mensaje soap no esperado');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0414','el número de elementos no coincide con el número de solicitudes recibidas');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0415','el número de solicitudes es mayor que 1, ejecute el servicio en modo asíncrono.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0419','existen identificadores de solicitud repetidos.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0501','error de base de datos: {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0502','error de sistema: {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0504','error en la configuración {0}');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0901','servicio no disponible');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0902','modo síncrono no soportado.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0903','modo asíncrono no soportado.');


INSERT INTO scsp_codigo_error (codigo, descripcion)
VALUES ('0904','error general indefinido {0}');



INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('cif.emisor','12345678Z','CIF del Emisor');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('nombre.emisor','Emisor1','Nombre del Emisor');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('nivelTraza','DEBUG','Nivel de traza a mostrar en los logs');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('pathLogs','','Ruta del fichero donde guardaremos los logs');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('logDinamico.enabled','true','Parámetro que indica si se cargará de BBDD la configuración de logs o de fichero');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('core_log.enabled','true','Parámetro que indica si se guardarán trazas en CORE_LOG de los errores ocurridos');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('almacenamiento.ficheros','','Directorio donde se guardan los ficheros de las peticiones');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('almacenamiento.transmisiones',1,'Parámetro que indica si se guardarán los nodos de las transmisiones');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('keystoreFile','','Ruta del fichero keystore');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('keystoreType','jks','Tipo de keystore');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('keystorePass','','Password del keystore');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('afirma.enabled','true','Parámetro que indica si @Firma está activo');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('afirma.idAplicacion','none','Id de aplicación para enviar a @Firma');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('afirma.url','','URL de @Firma');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('afirma.modoValidacion',2,'Modo de validación del certificado con @Firma');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('afirma.autenticacion.aliasSignerCert','emisor1','Alias del certificado con el que firmamos la peticiones para @Firma');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('custom.cert.validation.class','none','Clase propia de validación de certificados');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('periodo.validacion.certificados',24,'Periodo de validez del certificado en caché');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('tipoId','long','Longitud del identificador de la petición');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('prefijo.idtransmision','em1','Cuando no haya indicado un prefijo asociado al servicio y este si está definido se utilizará este para la generación del id de transmisión.');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('version.datamodel.scsp','4.2.0','Especifica la versión del modelo de datos actual');
INSERT INTO core_parametro_configuracion(nombre, valor, descripcion) VALUES('validate.nif.emisor.enabled','true','Flag que indica si se valida el valor del nodo <NifEmisor> de la petición');

