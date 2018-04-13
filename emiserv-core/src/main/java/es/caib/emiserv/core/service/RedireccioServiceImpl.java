/**
 * 
 */
package es.caib.emiserv.core.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.caib.emiserv.core.api.dto.AuditoriaFiltreDto;
import es.caib.emiserv.core.api.dto.AuditoriaPeticioDto;
import es.caib.emiserv.core.api.dto.AuditoriaSolicitudDto;
import es.caib.emiserv.core.api.dto.PaginaDto;
import es.caib.emiserv.core.api.dto.PaginacioParamsDto;
import es.caib.emiserv.core.api.dto.PeticioEstatEnumDto;
import es.caib.emiserv.core.api.dto.ProcedimentDto;
import es.caib.emiserv.core.api.dto.RedireccioProcessarResultatDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.dto.ServeiTipusEnumDto;
import es.caib.emiserv.core.api.exception.NotFoundException;
import es.caib.emiserv.core.api.service.RedireccioService;
import es.caib.emiserv.core.entity.RedireccioMissatgeEntity;
import es.caib.emiserv.core.entity.RedireccioPeticioEntity;
import es.caib.emiserv.core.entity.RedireccioSolicitudEntity;
import es.caib.emiserv.core.entity.ServeiEntity;
import es.caib.emiserv.core.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.core.helper.ConversioTipusHelper;
import es.caib.emiserv.core.helper.PaginacioHelper;
import es.caib.emiserv.core.helper.PaginacioHelper.Converter;
import es.caib.emiserv.core.helper.PermisosHelper;
import es.caib.emiserv.core.helper.SecurityHelper;
import es.caib.emiserv.core.helper.XmlHelper;
import es.caib.emiserv.core.repository.RedireccioMissatgeRepository;
import es.caib.emiserv.core.repository.RedireccioPeticioRepository;
import es.caib.emiserv.core.repository.RedireccioSolicitudRepository;
import es.caib.emiserv.core.repository.ServeiRepository;
import es.caib.emiserv.core.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.core.resolver.EntitatResolver;
import es.caib.emiserv.core.resolver.ResponseResolver;

/**
 * Implementació del servei de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class RedireccioServiceImpl implements RedireccioService {

	private static final SimpleDateFormat scspDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

	@Resource
	private ServeiRepository serveiRepository;
	@Resource
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	@Resource
	private RedireccioPeticioRepository redireccioPeticioRepository;
	@Resource
	private RedireccioSolicitudRepository redireccioSolicitudRepository;
	@Resource
	private RedireccioMissatgeRepository redireccioMissatgeRepository;

	@Resource
	private XmlHelper xmlHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private SecurityHelper securityHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	@Transactional
	@Override
	public RedireccioProcessarResultatDto processarPeticio(
			byte[] xml) {
		String xmlstr = xml != null ? new String(xml) : null;
		logger.debug(
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
				} else if (numElementos != 1) {
					resposta = new RedireccioProcessarResultatDto(
							"0502",
							"[EMISERV] Només es permet una solicitud per petició",
							idPeticion,
							timestamp,
							codigoCertificado);
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
					} else if (redireccioPeticioRepository.findByPeticioIdAndServeiCodi(idPeticion, codigoCertificado).size() > 0) {
						resposta = new RedireccioProcessarResultatDto(
								"0502",
								"[EMISERV] L'identificador de la petició ja ha estat enregistrada per aquest servei (" +
										"codigoCertificado=" + codigoCertificado + ", " +
										"idPeticion=" + idPeticion + ")");
					} else if (!ServeiTipusEnumDto.ENRUTADOR.equals(servei.getTipus())
								&& !ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(servei.getTipus())) {
						resposta = new RedireccioProcessarResultatDto(
								"0502",
								"[EMISERV] El servei no és del tipus enrutador (" +
										"codi=" + codigoCertificado + ")",
								idPeticion,
								timestamp,
								codigoCertificado);
					} else if (xml != null) {
						if (ServeiTipusEnumDto.ENRUTADOR.equals(servei.getTipus())){
							// ENRUTADOR SIMPLE
							String resolverClass = servei.getResolverClass();
							if (resolverClass != null) {
								@SuppressWarnings("unchecked")
								Class<EntitatResolver> entitatResolverClass = (Class<EntitatResolver>)Class.forName(resolverClass);
								EntitatResolver entitatResolver = entitatResolverClass.newInstance();
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
							}
						}
					} else {
						resposta = new RedireccioProcessarResultatDto(
								"0502",
								"[EMISERV] El missatge XML no pot ser null",
								idPeticion,
								timestamp,
								codigoCertificado);
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
						logger.error("Error al obtenir la data de la sol·licitud", ex);
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
				logger.error(
						"No s'ha pogut obtenir la versió de protocol SCSP de la petició");
				resposta = new RedireccioProcessarResultatDto(
						"0502",
						"No s'ha pogut obtenir la versió de protocol SCSP de la petició");
			}
		} catch (Exception ex) {
			logger.error(
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
		logger.debug(
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
				logger.error(
						"No s'ha pogut obtenir la versió de protocol de la resposta SCSP (" +
						"peticioId=" + peticioId + ", " +
						"serveiCodi=" + serveiCodi + ")");
			}
		} else {
			logger.error(
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
		logger.debug(
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
					responseResolver = responseResolverClass.newInstance();
					// Crea la llista de claus de rutes ordenades per ordre
					List<String> codisEntitatsOrdenades = new ArrayList<String>();
					for (ServeiRutaDestiEntity rutaDesti : serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(servei))
						codisEntitatsOrdenades.add(rutaDesti.getEntitatCodi());
					// Crea el mapa d'elements
					Map<String, Document> documents = new HashMap<String, Document>();
					for(String codi : xmls.keySet()) {
						if (xmls.get(codi) != null) {
							try 
							{
								Document doc = getDocumentXml(xmls.get(codi));
								documents.put(codi, doc);
							} catch (Exception e) {
								logger.error("Error obtenint Document de la resposta a la entitat '"+codi+"', xml = " + xmls.get(codi));
								e.printStackTrace();
							}
						}
					}
					entitatCodi = responseResolver.resolve(
							codisEntitatsOrdenades, 
							documents);
				} catch (Exception e) {
					logger.error("Error escollint la resposta de la redirecció múltiple (serveiCodi=" + serveiCodi +
							", peticioId=" + peticioId + "xmls= " + xmls + "): " + e.getLocalizedMessage());
					e.printStackTrace();
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
		logger.debug("Consulta dels diferents procediments rebuts");
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
		logger.debug("Consulta dels diferents serveis rebuts");
		List<ServeiDto> resposta = new ArrayList<ServeiDto>();
		List<String> serveiCodis = redireccioPeticioRepository.findServeiDistint();
		if (!serveiCodis.isEmpty()) {
			List<ServeiEntity> serveis = serveiRepository.findServeisPerCodis(serveiCodis);
			for (ServeiEntity servei: serveis) {
				ServeiDto dto = new ServeiDto();
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
		logger.debug("Consulta amb filtre de peticions per a les auditories ("
				+ "filtre=" + filtre + ")");
		PaginaDto<AuditoriaPeticioDto> resposta = paginacioHelper.toPaginaDto(
				redireccioPeticioRepository.findByFiltrePaginat(
						filtre.getProcediment() == null || filtre.getProcediment().isEmpty(),
						filtre.getProcediment(),
						filtre.getServei() == null || filtre.getServei().isEmpty(),
						filtre.getServei(),
						filtre.getEstat() == null,
						PeticioEstatEnumDto.ERROR.equals(filtre.getEstat()),
						toEstatScsp(filtre.getEstat()),
						filtre.getDataInici() == null,
						filtre.getDataInici(),
						filtre.getDataFi() == null,
						filtre.getDataFi(),
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
		logger.debug("Consulta de transmissions associades a una petició ("
				+ "peticioId=" + peticioId + ")");
		RedireccioPeticioEntity peticio = redireccioPeticioRepository.findOne(peticioId);
		if (peticio != null) {
			return conversioTipusHelper.convertirList(
					redireccioSolicitudRepository.findByPeticioOrderBySolicitudIdAsc(peticio),
					AuditoriaSolicitudDto.class);
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public String peticioXmlPeticio(
			Long peticioId) {
		logger.debug("Consulta del missatge XML de petició (" +
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
		logger.debug("Consulta del missatge XML de resposta (" +
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
		RedireccioPeticioEntity redireccioPeticio = redireccioPeticioRepository.findOne(id);
		if (redireccioPeticio == null) {
			throw new NotFoundException(
					id,
					RedireccioPeticioEntity.class);
		}
		return redireccioPeticio;
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
		return peticio;
	}

	private static final Logger logger = LoggerFactory.getLogger(RedireccioServiceImpl.class);

}
