/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper;
import es.caib.emiserv.logic.helper.PermisosHelper;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.helper.XmlHelper;
import es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaPeticioDto;
import es.caib.emiserv.logic.intf.dto.AuditoriaSolicitudDto;
import es.caib.emiserv.logic.intf.dto.PaginaDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ProcedimentDto;
import es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.logic.intf.dto.RedireccioRespostaDto;
import es.caib.emiserv.logic.intf.dto.ServeiDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.PermissionDeniedException;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import es.caib.emiserv.logic.resolver.EntitatResolver;
import es.caib.emiserv.logic.resolver.ResponseResolver;
import es.caib.emiserv.persist.entity.RedireccioMissatgeEntity;
import es.caib.emiserv.persist.entity.RedireccioPeticioEntity;
import es.caib.emiserv.persist.entity.RedireccioSolicitudEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.persist.repository.EntitatRepository;
import es.caib.emiserv.persist.repository.RedireccioMissatgeRepository;
import es.caib.emiserv.persist.repository.RedireccioPeticioRepository;
import es.caib.emiserv.persist.repository.RedireccioSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto.FaultCodeEnum.CLIENT;
import static es.caib.emiserv.logic.intf.dto.RedireccioProcessarResultatDto.FaultCodeEnum.SERVER;

/**
 * Implementació del servei de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class RedireccioServiceImpl implements RedireccioService {

	private static final SimpleDateFormat scspDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	@Autowired
	private RedireccioPeticioRepository redireccioPeticioRepository;
	@Autowired
	private RedireccioSolicitudRepository redireccioSolicitudRepository;
	@Autowired
	private RedireccioMissatgeRepository redireccioMissatgeRepository;
	@Autowired
	private EntitatRepository entitatRepository;

	@Autowired
	private XmlHelper xmlHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private SecurityHelper securityHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@Transactional
	@Override
	public RedireccioProcessarResultatDto processarPeticio(
			byte[] xml) {
		String xmlstr = xml != null ? new String(xml) : null;
		log.debug(
				"Obtenint URL servei SCSP per missatge XML (" +
						"xml=" + xmlstr != null ? xmlstr : "<null>" + ")");
		RedireccioProcessarResultatDto resposta;
		try {
			Document document = xmlHelper.bytesToDocument(xml);
			boolean isV2 = xmlHelper.countNodes(
					document,
					"//peticion2:Peticion") > 0;
			boolean isV3 = xmlHelper.countNodes(
					document,
					"//peticion3:Peticion") > 0;
			if (isV2 || isV3) {
				String nsprefix = (isV2) ? "peticion2" : "peticion3";
				String numElementosStr = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Atributos/" + nsprefix + ":NumElementos");
				String codigoCertificado = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Atributos/" + nsprefix + ":CodigoCertificado");
				String idPeticion = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Atributos/" + nsprefix + ":IdPeticion");
				String timestamp = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Atributos/" + nsprefix + ":TimeStamp");
				int numSolicituds = xmlHelper.countNodes(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision");
				String idSolicitud = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Transmision/" + nsprefix + ":IdSolicitud");
				String solicitantId = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Solicitante/" + nsprefix + ":IdentificadorSolicitante");
				String solicitantNom = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Solicitante/" + nsprefix + ":NombreSolicitante",
						true);
				String titularTipusDoc = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Titular/" + nsprefix + ":TipoDocumentacion",
						true);
				String titularDocument = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Titular/" + nsprefix + ":Documentacion",
						true);
				String titularNom = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Titular/" + nsprefix + ":Nombre",
						true);
				String titularLlinatge1 = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Titular/" + nsprefix + ":Apellido1",
						true);
				String titularLlinatge2 = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Titular/" + nsprefix + ":Apellido2",
						true);
				String titularNomSencer = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Titular/" + nsprefix + ":NombreCompleto",
						true);
				String funcionariDocument = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Solicitante/" + nsprefix + ":Funcionario/" + nsprefix + ":NifFuncionario",
						true);
				String funcionariNom = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Solicitante/" + nsprefix + ":Funcionario/" + nsprefix + ":NombreCompletoFuncionario",
						true);
				String dataGeneracio = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Transmision/" + nsprefix + ":FechaGeneracion",
						true);
				String emissorNif = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Emisor/" + nsprefix + ":NifEmisor",
						true);
				String procedimentCodi = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Solicitante/" + nsprefix + ":Procedimiento/" + nsprefix + ":CodProcedimiento",
						true);
				String procedimentNom = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Solicitante/" + nsprefix + ":Procedimiento/" + nsprefix + ":NombreProcedimiento",
						true);
				String unitatTramitadora = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Solicitante/" + nsprefix + ":UnidadTramitadora",
						true);
				String finalitat = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Solicitante/" + nsprefix + ":Finalidad",
						true);
				String consentiment = xmlHelper.getTextFromFirstNode(
						document,
						"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision/" + nsprefix + ":DatosGenericos/" + nsprefix + ":Solicitante/" + nsprefix + ":Consentimiento",
						true);
				String solicitantCodi = null;
				String entitatDesti = null;
				if (solicitantId != null) {
					var entitat = entitatRepository.findByCif(solicitantId);
					if (entitat.isPresent()) {
						solicitantCodi = entitat.get().getCodi();
					}
				}
				int numElementos = (numElementosStr != null) ? Integer.parseInt(numElementosStr) : 0;
				if (numElementos != numSolicituds) {
					resposta = new RedireccioProcessarResultatDto(
							"0414",
							"[EMS] El número de elementos no coincide con el número de solicitudes recibidas (" +
									"numSolicitudes=" + numSolicituds + ", " +
									"numElementos=" + numElementosStr + ")",
							idPeticion,
							timestamp,
							codigoCertificado,
							CLIENT,
							"0414",
							"El número de elementos no coincide con el número de solicitudes recibidas");
					resposta.setNumElements(numElementos);
					resposta.setScspVersio((isV2) ? 2 : 3);
				} else if (numElementos != 1) {
					resposta = new RedireccioProcessarResultatDto(
							"0415",
							"[EMS] El número de solicitudes es mayor que uno. Ejecute el servicio en modo asíncrono",
							idPeticion,
							timestamp,
							codigoCertificado,
							CLIENT,
							"0415",
							"El número de solicitudes es mayor que uno. Ejecute el servicio en modo asíncrono");
					resposta.setNumElements(numElementos);
					resposta.setScspVersio((isV2) ? 2 : 3);
				} else {
					ServeiEntity servei = serveiRepository.findByCodi(codigoCertificado);
					if (servei == null) {
						resposta = new RedireccioProcessarResultatDto(
								"0255",
								"[EMS] El servicio " + codigoCertificado + " no se encuentra disponible en el entorno",
								idPeticion,
								timestamp,
								codigoCertificado,
								SERVER,
								"0255",
								"El servicio " + codigoCertificado + " no se encuentra disponible en el entorno");
						resposta.setNumElements(numElementos);
						resposta.setScspVersio((isV2) ? 2 : 3);
					} else if (redireccioPeticioRepository.findByPeticioIdAndServeiCodi(idPeticion, codigoCertificado).size() > 0) {
						resposta = new RedireccioProcessarResultatDto(
								"0229",
								"[EMS] La petición ya ha sido tramitada o ya existe en el sistema o está repetida (" +
										"codigoCertificado=" + codigoCertificado + ", " +
										"idPeticion=" + idPeticion + ")",
								CLIENT,
								"0229",
								"La petición ya ha sido tramitada o ya existe en el sistema o está repetida");
						resposta.setNumElements(numElementos);
						resposta.setScspVersio((isV2) ? 2 : 3);
					} else if (!ServeiTipusEnumDto.ENRUTADOR.equals(servei.getTipus())
								&& !ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(servei.getTipus())) {
						resposta = new RedireccioProcessarResultatDto(
								"0242",
								"[EMS] El servicio no es de tipo enrutador (" +
										"codi=" + codigoCertificado + ")",
								idPeticion,
								timestamp,
								codigoCertificado,
								SERVER,
								"0503",
								"Error al obtener la respuesta o el resultado del servicio del Backoffice");
						resposta.setNumElements(numElementos);
						resposta.setScspVersio((isV2) ? 2 : 3);
					} else if (xml != null) {
						if (ServeiTipusEnumDto.ENRUTADOR.equals(servei.getTipus())){
							// ENRUTADOR SIMPLE
							String resolverClass = servei.getResolverClass();
							if (resolverClass != null) {
								@SuppressWarnings("unchecked")
								Class<EntitatResolver> entitatResolverClass = (Class<EntitatResolver>)Class.forName(resolverClass);
								EntitatResolver entitatResolver = entitatResolverClass.getDeclaredConstructor().newInstance();
								Document doc = getDocumentXml(xml);
								String entitatCodi = entitatResolver.resolve(doc.getDocumentElement());
								entitatDesti = entitatCodi;
								ServeiRutaDestiEntity serveiRutaDesti = serveiRutaDestiRepository.findByServeiAndEntitatCodi(
										servei,
										entitatCodi);
								if (serveiRutaDesti == null) {
									if (servei.getUrlPerDefecte() != null) {
										resposta = new RedireccioProcessarResultatDto(
												servei.getUrlPerDefecte(),
												(isV2) ? 2 : 3,
												idPeticion,
												timestamp,
												codigoCertificado);
									} else {
										resposta = new RedireccioProcessarResultatDto(
												"0242",
												"[EMS] No se ha encontrado ninguna ruta para redirigir la petición al servicio correspondiente (" +
														"serveiCodi=" + codigoCertificado + ", " +
														"entitatCodi=" + entitatCodi + ")",
												idPeticion,
												timestamp,
												codigoCertificado,
												SERVER,
												"0503",
												"Error al obtener la respuesta o el resultado del servicio del Backoffice");
										resposta.setNumElements(numElementos);
										resposta.setScspVersio((isV2) ? 2 : 3);
									}
								} else {
									resposta = new RedireccioProcessarResultatDto(
											serveiRutaDesti.getUrl(),
											(isV2) ? 2 : 3,
											idPeticion,
											timestamp,
											codigoCertificado);
								}
								resposta.setEntitatCodiRedireccio(entitatCodi);
							} else {
								if (servei.getUrlPerDefecte() != null) {
									resposta = new RedireccioProcessarResultatDto(
											servei.getUrlPerDefecte(),
											(isV2) ? 2 : 3,
											idPeticion,
											timestamp,
											codigoCertificado);
								} else {
									resposta = new RedireccioProcessarResultatDto(
											"0504",
											"[EMS] Error en la configuración: El servicio no tiene url por defecto ni entidad resolver configurado (" +
													"serveiCodi=" + codigoCertificado + ")",
											idPeticion,
											timestamp,
											codigoCertificado,
											SERVER,
											"0504",
											"Error en la configuración: El servicio no tiene url por defecto ni entidad resolver configurado");
									resposta.setNumElements(numElementos);
									resposta.setScspVersio((isV2) ? 2 : 3);
								}
							}
						} else {
							// ENRUTADOR MULTIPLE
							List<ServeiRutaDestiEntity> serveiRutaDestins = serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(
									servei);
							if (serveiRutaDestins.size() > 0) {
								Map<String, String> urlDestins = new HashMap<String, String>();
								for (ServeiRutaDestiEntity desti : serveiRutaDestins) {
									urlDestins.put(desti.getEntitatCodi(), desti.getUrl());
								}
								resposta = new RedireccioProcessarResultatDto(
										urlDestins,
										(isV2) ? 2 : 3,
										idPeticion,
										timestamp,
										codigoCertificado);
							} else {
								resposta = new RedireccioProcessarResultatDto(
										"0242",
										"[EMS] Backoffice destinatario no disponible: no se encuentra ninguna ruta a la que redirigir la petición (" +
												"serveiCodi=" + codigoCertificado + ")",
										idPeticion,
										timestamp,
										codigoCertificado,
										SERVER,
										"0503",
										"Error al obtener la respuesta o el resultado del servicio del Backoffice");
								resposta.setNumElements(numElementos);
								resposta.setScspVersio((isV2) ? 2 : 3);
							}
						}
					} else {
						resposta = new RedireccioProcessarResultatDto(
								"0403",
								"[EMS] Imposible obtener el contenido XML del mensaje: El mensaje XML no puede estar vacio",
								idPeticion,
								timestamp,
								codigoCertificado,
								CLIENT,
								"0403",
								"Imposible obtener el contenido XML del mensaje");
						resposta.setNumElements(numElementos);
						resposta.setScspVersio((isV2) ? 2 : 3);
					}
				}
				ServeiEntity servei = serveiRepository.findByCodi(codigoCertificado);
				RedireccioPeticioEntity redireccioPeticio = RedireccioPeticioEntity.getBuilder(
						idPeticion,
						codigoCertificado,
						"0001",
						0,
						numSolicituds,
						emissorNif).
						procedimentCodi(procedimentCodi).
						procedimentNom(procedimentNom).
						build();
				redireccioPeticio.updateEntitatCodiRedireccio(entitatDesti);
				redireccioPeticioRepository.save(redireccioPeticio);
				Date dataGeneracioParsed = null;
				if (dataGeneracio != null) {
					try {
						dataGeneracioParsed = scspDateFormat.parse(dataGeneracio);
					} catch (ParseException ex) {
						log.error("Error al obtenir la data de la sol·licitud", ex);
					}
				}
				RedireccioSolicitudEntity redireccioSolicitud = RedireccioSolicitudEntity.getBuilder(
						redireccioPeticio,
						idSolicitud,
						solicitantId).
						solicitantCodi(solicitantCodi).
						solicitantNom(solicitantNom).
						titularTipusDoc(titularTipusDoc).
						titularDocument(titularDocument).
						titularNom(titularNom).
						titularLlinatge1(titularLlinatge1).
						titularLlinatge2(titularLlinatge2).
						titularNomSencer(titularNomSencer).
						funcionariDocument(funcionariDocument).
						funcionariNom(funcionariNom).
						dataGeneracio(dataGeneracioParsed).
						procedimentCodi(procedimentCodi).
						procedimentNom(procedimentNom).
						unitatTramitadora(unitatTramitadora).
						finalitat(finalitat).
						consentiment(consentiment).
						build();
				redireccioSolicitudRepository.save(redireccioSolicitud);
				int missatgeTipus = RedireccioMissatgeEntity.TIPUS_PETICION;
				RedireccioMissatgeEntity redireccioMissatge = RedireccioMissatgeEntity.getBuilder(
						redireccioPeticio,
						missatgeTipus,
						xmlstr).build();
				redireccioMissatgeRepository.save(redireccioMissatge);
				if (resposta.isError()) {
					RedireccioMissatgeEntity redireccioMissatgeFault = RedireccioMissatgeEntity.getBuilder(
							redireccioPeticio,
							RedireccioMissatgeEntity.TIPUS_FAULT_LOCAL,
							generarSoapFault(resposta),
							entitatDesti,
							resposta.getUrlDesti()).build();
					redireccioPeticio.updateResposta(
							resposta.getErrorCodi(),
							resposta.getErrorDescripcio());
					redireccioMissatgeRepository.save(redireccioMissatgeFault);
				}
				resposta.setSolicitantId(solicitantId);
			} else {
				log.error("No s'ha pogut obtenir la versió de protocol SCSP de la petició");
				resposta = new RedireccioProcessarResultatDto(
						"0401",
						"No se ha podido obtenir la versión del protocolo SCSP de la petición " +
								"(no se ha encontrado el nodo Peticion en los namespaces esperados)",
						CLIENT,
						"0401",
						"La estructura del fichero recibido no corresponde con el esquema.");
			}
		} catch (Exception ex) {
			log.error(
					"Error al processar petició de redirecció (" + ex.getMessage() + ")",
					ex);
			resposta = new RedireccioProcessarResultatDto(
					"0502",
					"[EMS] Error interno procesando la petición de redirección (" + ex.getMessage() + ")",
					SERVER,
					"0502",
					"Error de sistema & identificación del sistema");
		}
		return resposta;
	}

	@Transactional
	@Override
	public void processarResposta(
			String peticioId,
			String serveiCodi,
			byte[] xml,
			String entitatCodiRedireccio,
			String urlRedireccio) throws Exception {
		String xmlstr = xml != null ? new String(xml) : null;
		log.debug(
				"Obtenint URL servei SCSP per missatge XML (" +
						"peticioId=" + peticioId + ", " +
						"serveiCodi=" + serveiCodi + ", " +
						"xml=" + xmlstr != null ? xmlstr : "<null>" + ")");
		RedireccioPeticioEntity redireccioPeticio = null;
		List<RedireccioPeticioEntity> redireccioPeticions = redireccioPeticioRepository.findByPeticioIdAndServeiCodi(peticioId, serveiCodi);
		if (! redireccioPeticions.isEmpty())
			redireccioPeticio = redireccioPeticions.get(0);
		
		if (redireccioPeticio != null) {
			Document document = xmlHelper.bytesToDocument(xml);
			String codigoEstado = null;
			String estadoError = null;
			int numFaults = xmlHelper.countNodes(
					document,
					"//soapenv:Fault");
			boolean isV2 = false;
			boolean isV3 = false;
			if (numFaults == 0) {
				isV2 = xmlHelper.countNodes(
						document,
						"//respuesta2:Respuesta") > 0;
				isV3 = xmlHelper.countNodes(
						document,
						"//respuesta3:Respuesta") > 0;
				if (isV2 || isV3) {
					String nsprefix = (isV2) ? "respuesta2" : "respuesta3";
					codigoEstado = xmlHelper.getTextFromFirstNode(
							document,
							"//" + nsprefix + ":Atributos/" + nsprefix + ":Estado/" + nsprefix + ":CodigoEstado");
				}
			} else {
				isV2 = xmlHelper.countNodes(
						document,
						"//soapfaultatr2:Atributos") > 0;
				isV3 = xmlHelper.countNodes(
						document,
						"//soapfaultatr3:Atributos") > 0;
				if (isV2 || isV3) {
					String nsprefix = (isV2) ? "soapfaultatr2" : "soapfaultatr3";
					codigoEstado = xmlHelper.getTextFromFirstNode(
							document,
							"//" + nsprefix + ":Atributos/" + nsprefix + ":Estado/" + nsprefix + ":CodigoEstado");
					estadoError = xmlHelper.getTextFromFirstNode(
							document,
							"//" + nsprefix + ":Atributos/" + nsprefix + ":Estado/" + nsprefix + ":LiteralError",
							true);
				}
			}
			if (isV2 || isV3) {
				redireccioPeticio.updateResposta(
						codigoEstado,
						estadoError,
						entitatCodiRedireccio);

				int missatgeTipus = (numFaults == 0) ? RedireccioMissatgeEntity.TIPUS_RESPUESTA : RedireccioMissatgeEntity.TIPUS_FAULT;
				RedireccioMissatgeEntity redireccioMissatge = RedireccioMissatgeEntity.getBuilder(
						redireccioPeticio,
						missatgeTipus,
						xmlstr,
						entitatCodiRedireccio,
						urlRedireccio).build();
				redireccioMissatgeRepository.save(redireccioMissatge);
			} else {
				log.error(
						"No s'ha pogut obtenir la versió de protocol de la resposta SCSP (" +
						"peticioId=" + peticioId + ", " +
						"serveiCodi=" + serveiCodi + ")");
			}
		} else {
			log.error(
					"No s'ha pogut trobar una petició per a la resposta SCSP (" +
					"peticioId=" + peticioId + ", " +
					"serveiCodi=" + serveiCodi + ")");
		}
	}
	
	@Transactional
	@Override
	public String escollirResposta(
			RedireccioProcessarResultatDto resultat,
			Map<String, byte[]> xmls) {
		String entitatCodi = null;
		String peticioId = resultat.getAtributPeticioId();
		String serveiCodi = resultat.getAtributCodigoCertificado();
		log.debug(
				"Escollint una resposta pel servei SCSP i missatges XML (" +
						"peticioId=" + peticioId + ", " +
						"serveiCodi=" + serveiCodi + ", " +
						"xmls=" + xmls != null ? xmls.toString() : "<null>" + ")");
		ServeiEntity servei = serveiRepository.findByCodi(serveiCodi);
		if (ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(servei.getTipus())) {
			String responseResolverClassName = servei.getResponseResolverClass();
			if (responseResolverClassName != null) {
				try {
					@SuppressWarnings("unchecked")
					Class<ResponseResolver> responseResolverClass = (Class<ResponseResolver>) Class.forName(responseResolverClassName);
					ResponseResolver responseResolver;
					responseResolver = responseResolverClass.getDeclaredConstructor().newInstance();
					// Crea la llista de claus de rutes ordenades per ordre
					List<String> codisEntitatsOrdenades = new ArrayList<String>();
					for (ServeiRutaDestiEntity rutaDesti: serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(servei))
						codisEntitatsOrdenades.add(rutaDesti.getEntitatCodi());
					// Crea el mapa d'elements
					Map<String, Document> documents = new HashMap<String, Document>();
					for (String codi: xmls.keySet()) {
						if (xmls.get(codi) != null) {
							try {
								Document doc = getDocumentXml(xmls.get(codi));
								documents.put(codi, doc);
							} catch (Exception ex) {
								log.error("Error obtenint Document de la resposta a la entitat (" +
										"codi=" + codi + ", " +
										"xml = " + xmls.get(codi) + ")",
										ex);
							}
						}
					}
					entitatCodi = responseResolver.resolve(
							codisEntitatsOrdenades, 
							documents);
				} catch (Exception ex) {
					log.error("Error escollint la resposta de la redirecció múltiple (" +
							"serveiCodi=" + serveiCodi + ", " +
							"peticioId=" + peticioId + ", " +
							"xmls= " + xmls + ")",
							ex);
				}
			}

//			// Desam tots els missatges rebuts
//			if (xmls != null) {
//				int missatgeTipus = RedireccioMissatgeEntity.TIPUS_RESPOSTA_ENTITAT;
//				RedireccioPeticioEntity redireccioPeticio = null;
//				List<RedireccioPeticioEntity> redireccioPeticions = redireccioPeticioRepository.findByPeticioIdAndServeiCodi(peticioId, serveiCodi);
//				if (!redireccioPeticions.isEmpty()) {
//					redireccioPeticio = redireccioPeticions.get(0);
//					for (var msgResposta : xmls.entrySet()) {
//						var xmlBytes = msgResposta.getValue();
//						if (xmlBytes != null && xmlBytes.length > 0) {
//							RedireccioMissatgeEntity redireccioMissatge = RedireccioMissatgeEntity.getBuilder(
//									redireccioPeticio,
//									missatgeTipus,
//									new String(xmlBytes),
//									msgResposta.getKey()).build();
//							redireccioMissatgeRepository.save(redireccioMissatge);
//						}
//					}
//				}
//			}
		}
		if (entitatCodi == null) {
			resultat.setError(true);
			resultat.setErrorCodi("0242");
			resultat.setErrorDescripcio("[EMS] Backoffice destinatario no disponible");
			resultat.setFaultCode(SERVER);
			resultat.setFaultErrorCode("0503");
			resultat.setFaultErrorString("Error al obtener la respuesta o el resultado del servicio del Backoffice");
		}
		return entitatCodi;
	}

    private static final int RESPOSTA_ENTITAT_TIPUS = RedireccioMissatgeEntity.TIPUS_RESPOSTA_ENTITAT;

	@Transactional
	@Override
	public void saveRespostesPerEntitat(
			Map<String, String> respostes,
			Map<String, String> urlsPerEntitat,
			String peticioId,
			String serveiCodi) {
        if (respostes != null && !respostes.isEmpty()) {
            List<RedireccioPeticioEntity> redireccioPeticioList = redireccioPeticioRepository.findByPeticioIdAndServeiCodi(peticioId, serveiCodi);
            if (!redireccioPeticioList.isEmpty()) {
                RedireccioPeticioEntity redireccioPeticio = redireccioPeticioList.get(0);
                saveRedirectionMessages(respostes, urlsPerEntitat, redireccioPeticio);
			}
		}
	}

    private void saveRedirectionMessages(
			Map<String, String> respostes,
			Map<String, String> urlsPerEntitat,
			RedireccioPeticioEntity redireccioPeticio) {
        for (var respostaEntry : respostes.entrySet()) {
			String resposta = respostaEntry.getValue();
			String entitat = respostaEntry.getKey();
            if (resposta != null && !resposta.isBlank()) {
                RedireccioMissatgeEntity redirectionMessage = RedireccioMissatgeEntity.getBuilder(
                        redireccioPeticio,
						RESPOSTA_ENTITAT_TIPUS,
                        resposta,
						entitat,
						urlsPerEntitat != null ? urlsPerEntitat.get(entitat) : null).build();
                redireccioMissatgeRepository.save(redirectionMessage);
            }
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentDto> procedimentFindAll() {
		log.debug("Consulta dels diferents procediments rebuts");
		List<ProcedimentDto> resposta = new ArrayList<ProcedimentDto>();
		List<Object[]> procedimentsInfo = redireccioSolicitudRepository.findProcedimentDistint();
		for (Object[] procedimentInfo: procedimentsInfo) {
			ProcedimentDto procediment = new ProcedimentDto();
			procediment.setCodi((String)procedimentInfo[0]);
			procediment.setNom((String)procedimentInfo[1]);
			resposta.add(procediment);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ServeiDto> serveiFindAll() {
		log.debug("Consulta dels diferents serveis rebuts");
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<String> serveiCodis = redireccioPeticioRepository.findServeiDistint();
		if (!serveiCodis.isEmpty()) {
			List<ServeiEntity> serveis = serveiRepository.findServeisPerCodis(serveiCodis);
			for (ServeiEntity servei: serveis) {
				ServeiDto dto = new ServeiDto();
				dto.setId(servei.getId());
				dto.setCodi(servei.getCodi());
				dto.setNom(servei.getNom());
				resposta.add(dto);
			}
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<AuditoriaPeticioDto> peticioFindByFiltrePaginat(
			AuditoriaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		log.debug("Consulta amb filtre de peticions per a les auditories ("
				+ "filtre=" + filtre + ")");
		Map<String, String> mapeigOrdenacio = new HashMap<>();
		mapeigOrdenacio.put("procedimentCodiNom", "procedimentCodi");
		mapeigOrdenacio.put("entitatCodi", "entitatCodiRedireccio");
		mapeigOrdenacio.put("estat", "estatEnum");

		// Evitar problema quan s'ordena per estat
		if (paginacioParams.getOrdres().size() == 1 && "estat".equals(paginacioParams.getOrdres().get(0).getCamp())) {
			paginacioParams.getOrdres().add(new PaginacioParamsDto.OrdreDto("id", PaginacioParamsDto.OrdreDireccioDto.DESCENDENT));
		}
		PaginaDto<AuditoriaPeticioDto> resposta = paginacioHelper.toPaginaDto(
				redireccioPeticioRepository.findByFiltrePaginat(
						filtre.getProcediment() == null || filtre.getProcediment().isEmpty(),
						filtre.getProcediment(),
						filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
						filtre.getServeiCodi(),
						filtre.getEstat() == null,
						filtre.getEstat(),
						filtre.getDataInici() == null,
						filtre.getDataInici(),
						filtre.getDataFi() == null,
						filtre.getDataFi(),
						filtre.getNumeroPeticio() == null || filtre.getNumeroPeticio().isEmpty(),
						filtre.getNumeroPeticio() != null ? filtre.getNumeroPeticio() : "",
						paginacioHelper.toSpringDataPageable(paginacioParams, mapeigOrdenacio)),
				AuditoriaPeticioDto.class,
				this::toAuditoriaPeticioDto);
		List<ServeiEntity> serveis = serveiRepository.findAll();
		for (AuditoriaPeticioDto peticio: resposta.getContingut()) {
			String serveiCodi = peticio.getServeiCodi();
			peticio.setServeiDescripcio(serveiCodi);
			peticio.setServeiDescripcio(serveis.stream().filter(s -> s.getCodi().equals(serveiCodi)).findFirst().map(s -> s.getNom()).orElseGet(() -> null));
			peticio.setTeRespostes(redireccioMissatgeRepository.countByPeticioIdAndTipus(peticio.getPeticioId(), peticio.getServeiCodi(), RESPOSTA_ENTITAT_TIPUS) > 0);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public AuditoriaPeticioDto peticioFindById(Long idPeticio) {
		log.debug("Consulta dels detalls de la petició (id=" + idPeticio + ")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		var peticionRespuesta = redireccioPeticioRepository.getById(idPeticio);
		if (peticionRespuesta == null) {
			throw new NotFoundException(
					idPeticio,
					RedireccioPeticioEntity.class);
		}
		ServeiEntity servei = serveiRepository.findByCodi(peticionRespuesta.getServeiCodi());
		if (servei == null) {
			throw new NotFoundException(
					"(codi=" + peticionRespuesta.getServeiCodi() + ")",
					ServeiEntity.class);
		}
		if (!securityHelper.hasRole("EMS_ADMIN")) {
			boolean tePermisos = permisosHelper.isGrantedAll(
					servei.getId(),
					ServeiEntity.class,
					new Permission[] {BasePermission.ADMINISTRATION},
					auth);
			if (!tePermisos) {
				throw new PermissionDeniedException(
						servei.getId(),
						ServeiEntity.class,
						auth.getName(),
						BasePermission.ADMINISTRATION.toString());
			}
		}
		var peticio = toAuditoriaPeticioDto(peticionRespuesta);
		peticio.setTeRespostes(redireccioMissatgeRepository.countByPeticioIdAndTipus(peticio.getPeticioId(), peticio.getServeiCodi(), RESPOSTA_ENTITAT_TIPUS) > 0);
		return peticio;
	}

	@Transactional(readOnly = true)
	@Override
	public List<AuditoriaSolicitudDto> solicitudFindByPeticioId(
			Long peticioId) {
		log.debug("Consulta de transmissions associades a una petició ("
				+ "peticioId=" + peticioId + ")");
		Optional<RedireccioPeticioEntity> peticio = redireccioPeticioRepository.findById(peticioId);
		if (peticio.isPresent()) {
			return conversioTipusHelper.convertirList(
					redireccioSolicitudRepository.findByPeticioOrderBySolicitudIdAsc(peticio.get()),
					AuditoriaSolicitudDto.class);
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public String peticioXmlPeticio(
			Long peticioId) {
		log.debug("Consulta del missatge XML de petició (" +
				"peticioId=" + peticioId + ")");
		RedireccioPeticioEntity redireccioPeticio = comprovarRedireccioPeticio(peticioId);
		List<RedireccioMissatgeEntity> redireccioMissatge = redireccioMissatgeRepository.findByPeticioAndTipus(
				redireccioPeticio,
				RedireccioMissatgeEntity.TIPUS_PETICION);
		if (redireccioMissatge == null || redireccioMissatge.isEmpty()) {
			throw new NotFoundException(
					"[peticioId=" + peticioId + ", tipus=" + RedireccioMissatgeEntity.TIPUS_PETICION + "]",
					RedireccioMissatgeEntity.class);
		}
		return redireccioMissatge.get(0).getXml();
	}

	@Transactional(readOnly = true)
	@Override
	public String peticioXmlResposta(
			Long peticioId) {
		log.debug("Consulta del missatge XML de resposta (" +
				"peticioId=" + peticioId + ")");
		RedireccioPeticioEntity redireccioPeticio = comprovarRedireccioPeticio(peticioId);
		List<RedireccioMissatgeEntity> redireccioMissatge;
		if ("0003".equals(redireccioPeticio.getEstat())) {
			redireccioMissatge = redireccioMissatgeRepository.findByPeticioAndTipus(
					redireccioPeticio,
					RedireccioMissatgeEntity.TIPUS_RESPUESTA);
			if (redireccioMissatge == null || redireccioMissatge.isEmpty()) {
				throw new NotFoundException(
						"[peticioId=" + peticioId + ", tipus=" + RedireccioMissatgeEntity.TIPUS_RESPUESTA + "]",
						RedireccioMissatgeEntity.class);
			}
		} else {
			redireccioMissatge = redireccioMissatgeRepository.findByPeticioAndTipus(
					redireccioPeticio,
					RedireccioMissatgeEntity.TIPUS_FAULT);
			if (redireccioMissatge == null || redireccioMissatge.isEmpty()) {
				redireccioMissatge = redireccioMissatgeRepository.findByPeticioAndTipus(
						redireccioPeticio,
						RedireccioMissatgeEntity.TIPUS_FAULT_LOCAL);
			}
			if (redireccioMissatge == null || redireccioMissatge.isEmpty()) {
				throw new NotFoundException(
						"[peticioId=" + peticioId + ", tipus=" + RedireccioMissatgeEntity.TIPUS_FAULT + "]",
						RedireccioMissatgeEntity.class);
			}
		}
		return redireccioMissatge.get(0).getXml();
	}

	@Transactional(readOnly = true)
	@Override
	public List<RedireccioRespostaDto> peticioXmlRespostes(Long peticioId) {
		log.debug("Consulta dels missatge XML de resposta rebuts (peticioId=" + peticioId + ")");
		RedireccioPeticioEntity redireccioPeticio = comprovarRedireccioPeticio(peticioId);
		List<RedireccioMissatgeEntity> redireccioMissatges = redireccioMissatgeRepository.findByPeticioAndTipus(
				redireccioPeticio,
				RedireccioMissatgeEntity.TIPUS_RESPOSTA_ENTITAT);
		if (redireccioMissatges == null || redireccioMissatges.isEmpty()) {
			throw new NotFoundException(
					"[peticioId=" + peticioId + ", tipus=" + RedireccioMissatgeEntity.TIPUS_RESPOSTA_ENTITAT + "]",
					RedireccioMissatgeEntity.class);
		}
		return redireccioMissatges.stream().map(
				m -> RedireccioRespostaDto.builder()
						.entitat(m.getEntitatCodi())
						.urlResposta(m.getUrl())
						.xmlResposta(m.getXml())
						.respostaEscollida(m.getEntitatCodi().equals(redireccioPeticio.getEntitatCodiRedireccio()))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public String generarSoapFault(RedireccioProcessarResultatDto faultResult) {

		String faultCode = faultResult.getFaultCode() != null ? ("soapnenv:" + faultResult.getFaultCode().getValue()) : "soapenv:Server";
		String faultString = (faultResult.getFaultErrorString() != null ? "[" + faultResult.getFaultErrorCode() + "]" : "")
				+ (faultResult.getFaultErrorString() != null ? HtmlUtils.htmlEscapeHex(faultResult.getFaultErrorString()) : "");
		String codigoEstado = faultResult.getErrorCodi();
		String literalError = HtmlUtils.htmlEscapeHex(faultResult.getErrorDescripcio());

		StringBuilder soapFaultSb = new StringBuilder();
		soapFaultSb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soapFaultSb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		soapFaultSb.append("<soapenv:Body>");
		soapFaultSb.append("<soapenv:Fault>");
		soapFaultSb.append("<faultcode>" + faultCode + "</faultcode>");
		soapFaultSb.append("<faultstring>" + faultString + "</faultstring>");
		if (faultResult.isAtributs()) {
			String xmlnsAtributos = "";
			if (faultResult.getScspVersio() == 2) {
				xmlnsAtributos = "http://www.map.es/scsp/esquemas/atributos";
			} else if (faultResult.getScspVersio() == 3) {
				xmlnsAtributos = "http://intermediacion.redsara.es/scsp/esquemas/V3/soapfaultatributos";
			}
			soapFaultSb.append("<detail>");
			soapFaultSb.append("<a:Atributos xmlns:a=\"" + xmlnsAtributos + "\">");
			soapFaultSb.append("<a:IdPeticion>" + faultResult.getAtributPeticioId() + "</a:IdPeticion>");
			if (faultResult.getNumElements() != null) {
				soapFaultSb.append("<a:NumElementos>" + faultResult.getNumElements() + "</a:NumElementos>");
			}
			soapFaultSb.append("<a:TimeStamp>" + faultResult.getAtributTimestamp() + "</a:TimeStamp>");
			soapFaultSb.append("<a:CodigoCertificado>" + faultResult.getAtributCodigoCertificado() + "</a:CodigoCertificado>");
			soapFaultSb.append("<a:Estado>");
			soapFaultSb.append("<a:CodigoEstado>" + codigoEstado + "</a:CodigoEstado>");
			soapFaultSb.append("<a:LiteralError>" + literalError + "</a:LiteralError>");
			soapFaultSb.append("</a:Estado>");
			soapFaultSb.append("</a:Atributos>");
			soapFaultSb.append("</detail>");
		}
		soapFaultSb.append("</soapenv:Fault>");
		soapFaultSb.append("</soapenv:Body>");
		soapFaultSb.append("</soapenv:Envelope>");
		return soapFaultSb.toString();
	}


	private Document getDocumentXml(byte[] xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xml));
	}

	private String toEstatScsp(PeticioEstatEnumDto estat) {
		if (PeticioEstatEnumDto.PENDENT.equals(estat))
			return "0001";
		if (PeticioEstatEnumDto.EN_PROCES.equals(estat))
			return "0002";
		if (PeticioEstatEnumDto.TRAMITADA.equals(estat))
			return "0003";
		if (PeticioEstatEnumDto.POLLING.equals(estat))
			return "0004";
		return null;
	}

	private RedireccioPeticioEntity comprovarRedireccioPeticio(
			Long id) throws NotFoundException {
		Optional<RedireccioPeticioEntity> redireccioPeticio = redireccioPeticioRepository.findById(id);
		if (!redireccioPeticio.isPresent()) {
			throw new NotFoundException(
					id,
					RedireccioPeticioEntity.class);
		}
		return redireccioPeticio.get();
	}

	private AuditoriaPeticioDto toAuditoriaPeticioDto(
			RedireccioPeticioEntity redireccioPeticio) {
		ServeiEntity servei = serveiRepository.findByCodi(redireccioPeticio.getServeiCodi());
		return AuditoriaPeticioDto.builder()
				.id(redireccioPeticio.getId())
				.peticioId(redireccioPeticio.getPeticioId())
				.serveiCodi(redireccioPeticio.getServeiCodi())
				.serveiDescripcio(servei != null ? servei.getNom() : null)
				.serveiTipus(servei != null ? servei.getTipus() : null)
				.dataPeticio(redireccioPeticio.getDataPeticio())
				.sincrona(redireccioPeticio.isSincrona())
				.numTransmissions(redireccioPeticio.getNumTransmissions())
				.estat(redireccioPeticio.getEstatEnum())
				.estatScsp(redireccioPeticio.getEstat())
				.error(redireccioPeticio.getError())
				.procedimentCodi(redireccioPeticio.getProcedimentCodi())
				.procedimentNom(redireccioPeticio.getProcedimentNom())
				.procedimentCodiNom(redireccioPeticio.getPRocedimentCodiNom())
				.entitatCodi(redireccioPeticio.getEntitatCodiRedireccio())
				.build();
	}

	private String getCodiNom(String codi, String nom) {
		if ((codi == null || codi.isBlank()) && (nom == null || nom.isBlank()))
			return null;
		if (codi == null || codi.isBlank())
			return nom;
		if ((nom == null || nom.isBlank()))
			return codi;
		return codi + " - " + nom;
	}

}
