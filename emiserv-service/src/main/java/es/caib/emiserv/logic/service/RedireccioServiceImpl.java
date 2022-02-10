/**
 * 
 */
package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper;
import es.caib.emiserv.logic.helper.PaginacioHelper.Converter;
import es.caib.emiserv.logic.helper.XmlHelper;
import es.caib.emiserv.logic.intf.dto.*;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.RedireccioService;
import es.caib.emiserv.logic.resolver.EntitatResolver;
import es.caib.emiserv.logic.resolver.ResponseResolver;
import es.caib.emiserv.persist.entity.*;
import es.caib.emiserv.persist.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.stream.Collectors;

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
	private XmlHelper xmlHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
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
				int numElementos = (numElementosStr != null) ? Integer.parseInt(numElementosStr) : 0;
				if (numElementos != numSolicituds) {
					resposta = new RedireccioProcessarResultatDto(
							"0502",
							"[EMISERV] El nombre de sol·licituds no coincideix amb l'atribut numElementos (" +
									"numSolicituds=" + numSolicituds + ", " +
									"numElementos=" + numElementosStr + ")",
							idPeticion,
							timestamp,
							codigoCertificado);
					resposta.setScspVersio((isV2) ? 2 : 3);
				} else if (numElementos != 1) {
					resposta = new RedireccioProcessarResultatDto(
							"0502",
							"[EMISERV] Només es permet una solicitud per petició",
							idPeticion,
							timestamp,
							codigoCertificado);
					resposta.setScspVersio((isV2) ? 2 : 3);
				} else {
					ServeiEntity servei = serveiRepository.findByCodi(codigoCertificado);
					if (servei == null) {
						resposta = new RedireccioProcessarResultatDto(
								"0502",
								"[EMISERV] No s'ha trobat el servei (" +
										"codi=" + codigoCertificado + ")",
								idPeticion,
								timestamp,
								codigoCertificado);
						resposta.setScspVersio((isV2) ? 2 : 3);
					} else if (redireccioPeticioRepository.findByPeticioIdAndServeiCodi(idPeticion, codigoCertificado).size() > 0) {
						resposta = new RedireccioProcessarResultatDto(
								"0502",
								"[EMISERV] L'identificador de la petició ja ha estat enregistrada per aquest servei (" +
										"codigoCertificado=" + codigoCertificado + ", " +
										"idPeticion=" + idPeticion + ")");
						resposta.setScspVersio((isV2) ? 2 : 3);
					} else if (!ServeiTipusEnumDto.ENRUTADOR.equals(servei.getTipus())
								&& !ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(servei.getTipus())) {
						resposta = new RedireccioProcessarResultatDto(
								"0502",
								"[EMISERV] El servei no és del tipus enrutador (" +
										"codi=" + codigoCertificado + ")",
								idPeticion,
								timestamp,
								codigoCertificado);
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
												"0502",
												"[EMISERV] No s'ha trobat cap ruta per a redirigir la petició (" +
														"serveiCodi=" + codigoCertificado + ", " +
														"entitatCodi=" + entitatCodi + ")",
												idPeticion,
												timestamp,
												codigoCertificado);
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
											"0502",
											"[EMISERV] El servei no te url per defecte ni entitat resolver configurat (" +
													"serveiCodi=" + codigoCertificado + ")",
											idPeticion,
											timestamp,
											codigoCertificado);
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
										"0502",
										"[EMISERV] No s'ha trobat cap ruta per a redirigir la petició (" +
												"serveiCodi=" + codigoCertificado + ")",
										idPeticion,
										timestamp,
										codigoCertificado);
								resposta.setScspVersio((isV2) ? 2 : 3);
							}
						}
					} else {
						resposta = new RedireccioProcessarResultatDto(
								"0502",
								"[EMISERV] El missatge XML no pot ser null",
								idPeticion,
								timestamp,
								codigoCertificado);
						resposta.setScspVersio((isV2) ? 2 : 3);
					}
				}
				RedireccioPeticioEntity redireccioPeticio = RedireccioPeticioEntity.getBuilder(
						idPeticion,
						codigoCertificado,
						"0001",
						0,
						numSolicituds,
						emissorNif).build();
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
						solicitantNom(solicitantNom).
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
							generarSoapFault(resposta)).build();
					redireccioPeticio.updateResposta(
							resposta.getErrorCodi(),
							resposta.getErrorDescripcio());
					redireccioMissatgeRepository.save(redireccioMissatgeFault);
				}
			} else {
				log.error(
						"No s'ha pogut obtenir la versió de protocol SCSP de la petició");
				resposta = new RedireccioProcessarResultatDto(
						"0502",
						"No s'ha pogut obtenir la versió de protocol SCSP de la petició");
			}
		} catch (Exception ex) {
			log.error(
					"Error al processar petició de redirecció (" + ex.getMessage() + ")",
					ex);
			resposta = new RedireccioProcessarResultatDto(
					"0502",
					"[EMISERV] Error al processar petició de redirecció (" + ex.getMessage() + ")");
		}
		return resposta;
	}

	@Transactional
	@Override
	public void processarResposta(
			String peticioId,
			String serveiCodi,
			byte[] xml) throws Exception {
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
						estadoError);
				int missatgeTipus = (numFaults == 0) ? RedireccioMissatgeEntity.TIPUS_RESPUESTA : RedireccioMissatgeEntity.TIPUS_FAULT;
				RedireccioMissatgeEntity redireccioMissatge = RedireccioMissatgeEntity.getBuilder(
						redireccioPeticio,
						missatgeTipus,
						xmlstr).build();
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
		}
		if (entitatCodi == null) {
			resultat.setError(true);
			resultat.setErrorCodi("0502");
			resultat.setErrorDescripcio("[EMISERV] No s'ha pogut resoldre cap resposta vàlida per a la petició");
		}
		return entitatCodi;
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
		PaginaDto<AuditoriaPeticioDto> resposta = paginacioHelper.toPaginaDto(
				redireccioPeticioRepository.findByFiltrePaginat(
						filtre.getProcediment() == null || filtre.getProcediment().isEmpty(),
						filtre.getProcediment(),
						filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
						filtre.getServeiCodi(),
						filtre.getEstat() == null,
						PeticioEstatEnumDto.ERROR.equals(filtre.getEstat()),
						toEstatScsp(filtre.getEstat()),
						filtre.getDataInici() == null,
						filtre.getDataInici(),
						filtre.getDataFi() == null,
						filtre.getDataFi(),
						filtre.getNumeroPeticio() == null || filtre.getNumeroPeticio().isEmpty(),
						filtre.getNumeroPeticio() != null ? filtre.getNumeroPeticio() : "",
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				AuditoriaPeticioDto.class,
				new Converter<RedireccioPeticioEntity, AuditoriaPeticioDto>() {
					@Override
					public AuditoriaPeticioDto convert(
							RedireccioPeticioEntity source) {
						return toAuditoriaPeticioDto(source);
					}
				});
		for (AuditoriaPeticioDto peticio: resposta.getContingut()) {
			String serveiCodi = peticio.getServeiCodi();
			peticio.setServeiDescripcio(serveiCodi);
			for (ServeiEntity serveiPermes: serveiRepository.findAll()) {
				if (serveiPermes.getCodi().equals(serveiCodi)) {
					peticio.setServeiDescripcio(serveiPermes.getNom());
					break;
				}
			}
		}
		return resposta;
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
		RedireccioMissatgeEntity redireccioMissatge = redireccioMissatgeRepository.findByPeticioAndTipus(
				redireccioPeticio,
				RedireccioMissatgeEntity.TIPUS_PETICION);
		if (redireccioMissatge == null) {
			throw new NotFoundException(
					"[peticioId=" + peticioId + ", tipus=" + RedireccioMissatgeEntity.TIPUS_PETICION + "]",
					RedireccioMissatgeEntity.class);
		}
		return redireccioMissatge.getXml();
	}

	@Transactional(readOnly = true)
	@Override
	public String peticioXmlResposta(
			Long peticioId) {
		log.debug("Consulta del missatge XML de resposta (" +
				"peticioId=" + peticioId + ")");
		RedireccioPeticioEntity redireccioPeticio = comprovarRedireccioPeticio(peticioId);
		RedireccioMissatgeEntity redireccioMissatge;
		if ("0003".equals(redireccioPeticio.getEstat())) {
			redireccioMissatge = redireccioMissatgeRepository.findByPeticioAndTipus(
					redireccioPeticio,
					RedireccioMissatgeEntity.TIPUS_RESPUESTA);
			if (redireccioMissatge == null) {
				throw new NotFoundException(
						"[peticioId=" + peticioId + ", tipus=" + RedireccioMissatgeEntity.TIPUS_RESPUESTA + "]",
						RedireccioMissatgeEntity.class);
			}
		} else {
			redireccioMissatge = redireccioMissatgeRepository.findByPeticioAndTipus(
					redireccioPeticio,
					RedireccioMissatgeEntity.TIPUS_FAULT);
			if (redireccioMissatge == null) {
				redireccioMissatge = redireccioMissatgeRepository.findByPeticioAndTipus(
						redireccioPeticio,
						RedireccioMissatgeEntity.TIPUS_FAULT_LOCAL);
			}
			if (redireccioMissatge == null) {
				throw new NotFoundException(
						"[peticioId=" + peticioId + ", tipus=" + RedireccioMissatgeEntity.TIPUS_FAULT + "]",
						RedireccioMissatgeEntity.class);
			}
		}
		return redireccioMissatge.getXml();
	}

	@Override
	public String generarSoapFault(
			RedireccioProcessarResultatDto redireccioProcessarResultat) {
		StringBuilder soapFaultSb = new StringBuilder();
		soapFaultSb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soapFaultSb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		soapFaultSb.append("<soapenv:Body>");
		soapFaultSb.append("<soapenv:Fault>");
		soapFaultSb.append("<faultcode>" + redireccioProcessarResultat.getErrorCodi() + "</faultcode>");
		soapFaultSb.append("<faultstring>" + HtmlUtils.htmlEscapeHex(redireccioProcessarResultat.getErrorDescripcio()) + "</faultstring>");
		if (redireccioProcessarResultat.isAtributs()) {
			String xmlnsAtributos = "";
			if (redireccioProcessarResultat.getScspVersio() == 2) {
				xmlnsAtributos = "http://www.map.es/scsp/esquemas/atributos";
			} else if (redireccioProcessarResultat.getScspVersio() == 3) {
				xmlnsAtributos = "http://intermediacion.redsara.es/scsp/esquemas/V3/soapfaultatributos";
			}
			soapFaultSb.append("<detail>");
			soapFaultSb.append("<a:Atributos xmlns:a=\"" + xmlnsAtributos + "\">");
			soapFaultSb.append("<a:IdPeticion>" + redireccioProcessarResultat.getAtributPeticioId() + "</a:IdPeticion>");
			soapFaultSb.append("<a:TimeStamp>" + redireccioProcessarResultat.getAtributTimestamp() + "</a:TimeStamp>");
			soapFaultSb.append("<a:CodigoCertificado>" + redireccioProcessarResultat.getAtributCodigoCertificado() + "</a:CodigoCertificado>");
			soapFaultSb.append("<a:Estado>");
			soapFaultSb.append("<a:CodigoEstado>" + redireccioProcessarResultat.getErrorCodi() + "</a:CodigoEstado>");
			soapFaultSb.append("<a:LiteralError>" + HtmlUtils.htmlEscapeHex(redireccioProcessarResultat.getErrorDescripcio()) + "</a:LiteralError>");
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
		AuditoriaPeticioDto peticio = new AuditoriaPeticioDto();
		peticio.setId(redireccioPeticio.getId());
		peticio.setPeticioId(redireccioPeticio.getPeticioId());
		peticio.setServeiCodi(redireccioPeticio.getServeiCodi());
		peticio.setDataPeticio(redireccioPeticio.getDataPeticio());
		peticio.setDataResposta(redireccioPeticio.getDataResposta());
		peticio.setTer(redireccioPeticio.getTer());
		peticio.setDataDarreraComprovacio(redireccioPeticio.getDataDarreraComprovacio());
		peticio.setSincrona(redireccioPeticio.isSincrona());
		peticio.setNumEnviaments(redireccioPeticio.getNumEnviaments());
		peticio.setNumTransmissions(redireccioPeticio.getNumTransmissions());
		if (redireccioPeticio.getEstat() != null) {
			peticio.setEstatScsp(redireccioPeticio.getEstat());
			if (redireccioPeticio.getEstat().startsWith("00")) {
				if (redireccioPeticio.getEstat().equals("0001"))
					peticio.setEstat(PeticioEstatEnumDto.PENDENT);
				else if (redireccioPeticio.getEstat().equals("0002"))
					peticio.setEstat(PeticioEstatEnumDto.EN_PROCES);
				else if (redireccioPeticio.getEstat().equals("0003"))
					peticio.setEstat(PeticioEstatEnumDto.TRAMITADA);
				else if (redireccioPeticio.getEstat().equals("0004"))
					peticio.setEstat(PeticioEstatEnumDto.POLLING);
			} else {
				peticio.setEstat(PeticioEstatEnumDto.ERROR);
			}
		}
		peticio.setError(redireccioPeticio.getError());
		List<RedireccioSolicitudEntity> solicituds = redireccioSolicitudRepository.findByPeticioOrderBySolicitudIdAsc(redireccioPeticio);
		peticio.setProcedimentCodi(solicituds.stream().map(s -> s.getProcedimentCodi()).distinct().collect(Collectors.joining(", ")));
		peticio.setProcedimentNom(solicituds.stream().map(s -> s.getProcedimentNom()).distinct().collect(Collectors.joining(", ")));
		peticio.setProcedimentCodiNom(solicituds.stream().map(s -> getCodiNom(s.getProcedimentCodi(),  s.getProcedimentNom())).distinct().collect(Collectors.joining(", ")));
		return peticio;
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
