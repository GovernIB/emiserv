/**
 * 
 */
package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.helper.BackofficeDestiHelper.BackofficeDesti;
import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.BackofficeException;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.service.ws.backoffice.*;
import es.caib.emiserv.logic.resolver.ResponseResolver;
import es.caib.emiserv.logic.service.ws.DatosEspecificosHandler;
import es.caib.emiserv.logic.service.ws.EmiservBackofficeImpl;
import es.caib.emiserv.logic.service.ws.PeticioRespostaHandler;
import es.caib.emiserv.logic.service.ws.WsClientHelper;
import es.caib.emiserv.persist.entity.BackofficeComunicacioEntity;
import es.caib.emiserv.persist.entity.BackofficePeticioEntity;
import es.caib.emiserv.persist.entity.BackofficeSolicitudEntity;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCorePeticionRespuestaEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreServicioEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreTokenDataEntity;
import es.caib.emiserv.persist.entity.scsp.ScspCoreTransmisionEntity;
import es.caib.emiserv.persist.repository.BackofficeComunicacioRepository;
import es.caib.emiserv.persist.repository.BackofficePeticioRepository;
import es.caib.emiserv.persist.repository.BackofficeSolicitudRepository;
import es.caib.emiserv.persist.repository.ServeiRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCorePeticionRespuestaRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreServicioRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTokenDataRepository;
import es.caib.emiserv.persist.repository.scsp.ScspCoreTransmisionRepository;
import es.caib.loginModule.auth.ControladorSesion;
import es.caib.loginModule.client.AuthorizationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.naming.NamingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Helper per a operacions amb els backoffices.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class BackofficeHelper {

	public static final String BACKOFFICE_WSDL_RESOURCE = "/es/caib/emiserv/backoffice/EmiservBackoffice.wsdl";

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private ScspCoreServicioRepository scspCoreServicioRepository;
	@Autowired
	private ScspCorePeticionRespuestaRepository scspCorePeticionRespuestaRepository;
	@Autowired
	private ScspCoreTransmisionRepository scspCoreTransmisionRepository;
	@Autowired
	private ScspCoreTokenDataRepository scspCoreTokenDataRepository;
	@Autowired
	private BackofficePeticioRepository backofficePeticioRepository;
	@Autowired
	private BackofficeSolicitudRepository backofficeSolicitudRepository;
	@Autowired
	private BackofficeComunicacioRepository backofficeComunicacioRepository;
	@Autowired
	private XmlHelper xmlHelper;
	@Autowired
	private Environment environment;
	@Autowired
	private BackofficeDestiHelper backofficeDestiHelper;

	private static final String NS_SOAPENV = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String NS_RESPUESTA3 = "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";
	/** Temps estimat de resposta de les peticions asíncrones dels enrutadors (es processen com a síncrones). */
	private static final int TER_ENRUTADOR_SEGONS = 60;

	private EmiservBackofficeHandlerProxy testBackoffice;
	private String testXmlPeticio;
	private String testXmlResposta;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public RespuestaAmbException peticioSincrona(
			Peticion peticion) {
		BackofficePeticioEntity backofficePeticio = peticioBackofficeCreate(
				peticion);
		return peticioBackofficeSincronaEnviar(
						peticion,
						backofficePeticio,
						null,
						true);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ConfirmacionPeticionAmbException peticioAsincrona(
			Peticion peticion) {
		BackofficePeticioEntity backofficePeticio = peticioBackofficeCreate(
				peticion);
		return peticioBackofficeAsincronaEnviar(
				peticion,
				backofficePeticio);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public RespuestaAmbException solicitudResposta(
			SolicitudRespuesta solicitudRespuesta) {
		String idPeticion = solicitudRespuesta.getAtributos().getIdPeticion();
		ScspCorePeticionRespuestaEntity scspPeticionRespuesta = scspCorePeticionRespuestaRepository.getOne(idPeticion);
		BackofficePeticioEntity backofficePeticio = backofficePeticioRepository.findByScspPeticionRespuesta(scspPeticionRespuesta);
		return solicitudResposta(
				solicitudRespuesta,
				backofficePeticio);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processarPeticioPendent(
			Long peticioId) {
		BackofficePeticioEntity peticio = backofficePeticioRepository.getOne(peticioId);
		ScspCorePeticionRespuestaEntity scspPeticionRespuesta = peticio.getScspPeticionRespuesta();
		log.debug("Processant petició pendent (" +
				"codigoCertificado=" + scspPeticionRespuesta.getCertificado() + ", " +
				"idPeticion=" + scspPeticionRespuesta.getPeticionId() + ", " +
				"darreraSolicitudId=" + peticio.getDarreraSolicitudId() + ")");
		List<BackofficeSolicitudEntity> solicituds = backofficeSolicitudRepository.findByPeticioOrderByIdAsc(
				peticio);
		boolean trobada = peticio.getDarreraSolicitudId() == null;
		// Cerca la darrera sol·licitud enviada i envia la següent
		for (BackofficeSolicitudEntity solicitud: solicituds) {
			if (!trobada) {
				trobada = solicitud.getSolicitudId().equals(peticio.getDarreraSolicitudId());
				log.debug("Comprovant sol·licitud a enviar (" +
						"codigoCertificado=" + scspPeticionRespuesta.getCertificado() + ", " + 
						"idPeticion=" + scspPeticionRespuesta.getPeticionId() + ", " + 
						"idSolicitud=" + solicitud.getSolicitudId() + ", " +
						"trobada=" + trobada + ")");
			} else {
				peticioBackofficeEnviarNomesUna(
						peticio,
						solicitud);
				break;
			}
		}
		if (!trobada) {
			log.error("No s'han trobat sol·licituds pendents d'enviar per a la petició (" +
					"codigoCertificado=" + scspPeticionRespuesta.getCertificado() + ", " +
					"idPeticion=" + scspPeticionRespuesta.getPeticionId() + ", " +
					"darreraSolicitudId=" + peticio.getDarreraSolicitudId() + ")");
		}
	}

	public ScspCoreServicioEntity findCoreServicioPerPeticionRespuesta(
			ScspCorePeticionRespuestaEntity peticionRespuesta) {
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.getOne(peticionRespuesta.getCertificado());
		if (scspCoreServicio == null) {
			throw new NotFoundException(
					peticionRespuesta.getCertificado(),
					ScspCoreServicioEntity.class);
		}
		return scspCoreServicio;
	}

	public void configuracioTest(
			EmiservBackoffice backoffice,
			String xmlPeticio,
			String xmlResposta) {
		this.testBackoffice = new EmiservBackofficeHandlerProxy(backoffice);
		this.testXmlPeticio = xmlPeticio;
		this.testXmlResposta = xmlResposta;
	}

	private BackofficePeticioEntity peticioBackofficeCreate(
			Peticion peticion) {
		List<String> idSolicituds = new ArrayList<String>();
		for (SolicitudTransmision solicitudTransmision: peticion.getSolicitudes().getSolicitudTransmision()) {
			String idSolicitud = null;
			if (solicitudTransmision.getDatosGenericos() != null && solicitudTransmision.getDatosGenericos().getTransmision() != null)
				idSolicitud = solicitudTransmision.getDatosGenericos().getTransmision().getIdSolicitud();
			idSolicituds.add(idSolicitud);
		}
		String idPeticion = peticion.getAtributos().getIdPeticion();
		ScspCorePeticionRespuestaEntity peticionRespuesta = scspCorePeticionRespuestaRepository.getOne(
				idPeticion);
		if (peticionRespuesta == null) {
			throw new NotFoundException(
					idPeticion,
					ScspCorePeticionRespuestaEntity.class);
		}
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.getOne(peticionRespuesta.getCertificado());
		ServeiEntity servei = serveiRepository.findByCodi(
				scspCoreServicio.getCodigoCertificado());
		if (servei == null) {
			throw new NotFoundException(
					peticionRespuesta.getCertificado(),
					ServeiEntity.class);
		}
		int terEnSegons = servei.getBackofficeCaibAsyncTer();
		if (terEnSegons == 0)
			// Per defecte és una hora. Els enrutadors processen les asíncrones com a síncrones en pocs segons.
			terEnSegons = servei.isEnrutador() ? TER_ENRUTADOR_SEGONS : 3600;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, terEnSegons);
		BackofficePeticioEntity peticio = BackofficePeticioEntity.getBuilder(
				peticionRespuesta,
				cal.getTime()).
				build();
		// Update estat SCSP
		updateScspEstat(peticionRespuesta, peticio);
		BackofficePeticioEntity peticioSaved = backofficePeticioRepository.save(peticio);
		for (String solicitudId: idSolicituds) {
			BackofficeSolicitudEntity solicitud = BackofficeSolicitudEntity.getBuilder(
					peticioSaved,
					solicitudId).build();
			backofficeSolicitudRepository.save(solicitud);
		}
		return peticioSaved;
	}

	private void updateScspEstat(ScspCorePeticionRespuestaEntity scspPeticionRespuesta, BackofficePeticioEntity peticio) {
		PeticioEstatEnumDto estat = peticio.getEstat();
		if (estat == null) {
			estat = getPeticioEstat(scspPeticionRespuesta.getEstado());
		}
		scspPeticionRespuesta.updateEstat(estat);
	}

	private PeticioEstatEnumDto getPeticioEstat(String estatTransmissio) {
		if (estatTransmissio != null) {
			switch (estatTransmissio) {
				case "0001":
					return PeticioEstatEnumDto.PENDENT;
				case "0002":
					return PeticioEstatEnumDto.EN_PROCES;
				case "0003":
					return PeticioEstatEnumDto.TRAMITADA;
				case "0004":
					return PeticioEstatEnumDto.POLLING;
			}
		}
		return PeticioEstatEnumDto.ERROR;
	}

	private void peticioBackofficeEnviarNomesUna(
			BackofficePeticioEntity backofficePeticio,
			BackofficeSolicitudEntity backofficeSolicitud) {
		Exception excepcio = null;
		ScspCorePeticionRespuestaEntity scspPeticionRespuesta = backofficePeticio.getScspPeticionRespuesta();
		ScspCoreTransmisionEntity scspTransmision = scspCoreTransmisionRepository.findByPeticionIdAndSolicitudId(
				scspPeticionRespuesta.getPeticionId(),
				backofficeSolicitud.getSolicitudId());
		if (scspTransmision == null) {
			excepcio = new NotFoundException(
					scspPeticionRespuesta.getPeticionId() + ", " + backofficeSolicitud.getSolicitudId(),
					ScspCoreTransmisionEntity.class);
		}
		ScspCoreTokenDataEntity scspTokenData = scspCoreTokenDataRepository.findByPeticionIdAndTipoMensaje(
				scspPeticionRespuesta.getPeticionId(),
				0);
		if (scspTokenData == null) {
			excepcio = new NotFoundException(
					scspPeticionRespuesta.getPeticionId() + ", 0",
					ScspCoreTokenDataEntity.class);
		}
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.getOne(scspPeticionRespuesta.getCertificado());
		if (scspCoreServicio == null) {
			excepcio = new NotFoundException(
					scspPeticionRespuesta.getCertificado(),
					ScspCoreServicioEntity.class);
		}
		if (excepcio == null) {
			log.debug("Enviant al backoffice una petició síncrona provinent d'una petició asíncrona (" +
					"codigoCertificado=" + scspPeticionRespuesta.getCertificado() + ", " + 
					"idPeticion=" + scspPeticionRespuesta.getPeticionId() + ", " +
					"idSolicitud=" + backofficeSolicitud.getSolicitudId() + ")");
			try {
				String xml = scspTokenData.getDatos();
				Element solicitudTransmisionElement = getElementSolicitudTransmision(
						xml,
						backofficeSolicitud.getSolicitudId());
				String nsprefix = (xmlHelper.isPeticioScspV2(solicitudTransmisionElement)) ? "peticion2" : "peticion3";
				// Rutes relatives a la sol·licitud: amb "//" s'obtindrien les dades de la primera sol·licitud de la petició
				String emisorNombre = xmlHelper.getTextFromFirstNode(
						solicitudTransmisionElement,
						"./" + nsprefix + ":DatosGenericos/" + nsprefix + ":Emisor/" + nsprefix + ":NombreEmisor");
				String emisorNif = xmlHelper.getTextFromFirstNode(
						solicitudTransmisionElement,
						"./" + nsprefix + ":DatosGenericos/" + nsprefix + ":Emisor/" + nsprefix + ":NifEmisor");
				String titularDocumentoTipo = xmlHelper.getTextFromFirstNode(
						solicitudTransmisionElement,
						"./" + nsprefix + ":DatosGenericos/" + nsprefix + ":Titular/" + nsprefix + ":TipoDocumentacion");
				// A SCSP v3 l'element DatosEspecificos és del namespace de dades específiques, no del de la petició
				Node datosEspecificos = xmlHelper.getFirstNode(
						solicitudTransmisionElement,
						"./*[local-name()='DatosEspecificos']");
				Peticion peticion = new Peticion();
				Atributos atributos = new Atributos();
				atributos.setCodigoCertificado(scspCoreServicio.getCodigoCertificado());
				atributos.setIdPeticion(scspPeticionRespuesta.getPeticionId());
				atributos.setNumElementos("1");
				atributos.setTimeStamp(crearTimestampScsp(new Date()));
				peticion.setAtributos(atributos);
				Solicitudes solicitudes = new Solicitudes();
				SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
				DatosGenericos datosGenericos = new DatosGenericos();
				Emisor emisor = new Emisor();
				emisor.setNombreEmisor(emisorNombre);
				emisor.setNifEmisor(emisorNif);
				datosGenericos.setEmisor(emisor);
				Solicitante solicitante = new Solicitante();
				Procedimiento procedimiento = new Procedimiento();
				procedimiento.setCodProcedimiento(scspTransmision.getProcedimientoCodigo());
				procedimiento.setNombreProcedimiento(scspTransmision.getProcedimientoNombre());
				solicitante.setProcedimiento(procedimiento);
				solicitante.setNombreSolicitante(scspTransmision.getSolicitanteNombre());
				solicitante.setIdentificadorSolicitante(scspTransmision.getSolicitanteId());
				Funcionario funcionario = new Funcionario();
				funcionario.setNombreCompletoFuncionario(scspTransmision.getFuncionarioNombre());
				funcionario.setNifFuncionario(scspTransmision.getFuncionarioDocumento());
				solicitante.setFuncionario(funcionario);
				solicitante.setUnidadTramitadora(scspTransmision.getUnidadTramitadora());
				solicitante.setIdExpediente(scspTransmision.getExpediente());
				if (scspTransmision.getConsentimiento() != null) {
					solicitante.setConsentimiento(toConsentimiento(scspTransmision.getConsentimiento()));
				}
				solicitante.setFinalidad(scspTransmision.getFinalidad());
				datosGenericos.setSolicitante(solicitante);
				Titular titular = new Titular();
				titular.setNombre(scspTransmision.getTitularNombre());
				titular.setApellido1(scspTransmision.getTitularApellido1());
				titular.setApellido2(scspTransmision.getTitularApellido2());
				titular.setNombreCompleto(scspTransmision.getTitularNombreCompleto());
				if (titularDocumentoTipo != null) {
					titular.setTipoDocumentacion(TipoDocumentacion.valueOf(titularDocumentoTipo));
				}
				titular.setDocumentacion(scspTransmision.getTitularDocumento());
				datosGenericos.setTitular(titular);
				Transmision transmision = new Transmision();
				transmision.setCodigoCertificado(scspCoreServicio.getCodigoCertificado());
				transmision.setIdSolicitud(scspTransmision.getSolicitudId());
				transmision.setIdTransmision(scspTransmision.getTransmisionId());
				if (scspTransmision.getFechaGeneracion() != null) {
					transmision.setFechaGeneracion(crearTimestampScsp(scspTransmision.getFechaGeneracion()));
				}
				datosGenericos.setTransmision(transmision);
				solicitudTransmision.setDatosGenericos(datosGenericos);
				solicitudTransmision.setDatosEspecificos(datosEspecificos);
				solicitudes.setSolicitudTransmision(new ArrayList<SolicitudTransmision>());
				solicitudes.getSolicitudTransmision().add(solicitudTransmision);
				peticion.setSolicitudes(solicitudes);
				RespuestaAmbException respuesta = peticioBackofficeSincronaEnviar(
						peticion,
						backofficePeticio,
						backofficeSolicitud,
						false);
				if (respuesta.getException() == null) {
					backofficeSolicitud.updateEstat(BackofficeSolicitudEstatEnumDto.TRAMITADA);
					backofficePeticio.updateSolicitudProcessada(
							backofficeSolicitud.getSolicitudId(),
							false);
//					scspPeticionRespuesta.updateProcessadesTotal(backofficePeticio.getProcessadesTotal());
				} else {
					excepcio = respuesta.getException();
				}
			} catch (Exception ex) {
				excepcio = ex;
			}
		}
		if (excepcio != null) {
			log.error("Error al enviar al backoffice una petició síncrona provinent d'una petició asíncrona (" +
					"codigoCertificado=" + scspPeticionRespuesta.getCertificado() + ", " + 
					"idPeticion=" + scspPeticionRespuesta.getPeticionId() + ", " +
					"idSolicitud=" + backofficeSolicitud.getSolicitudId() + ")",
					excepcio);
			backofficeSolicitud.updateEstat(BackofficeSolicitudEstatEnumDto.ERROR);
			backofficePeticio.updateSolicitudProcessada(
					backofficeSolicitud.getSolicitudId(),
					true);
//			scspPeticionRespuesta.updateProcessadesTotal(backofficePeticio.getProcessadesTotal());
		}
		int numSolicituds = backofficeSolicitudRepository.countByPeticio(backofficePeticio);
		if (numSolicituds == backofficePeticio.getProcessadesTotal()) {
			if (backofficePeticio.getProcessadesError() == 0) {
				backofficePeticio.updateEstat(PeticioEstatEnumDto.TRAMITADA);
			} else {
				backofficePeticio.updateEstat(PeticioEstatEnumDto.ERROR);
			}
			// Update estat SCSP
			updateScspEstat(scspPeticionRespuesta, backofficePeticio);
		}
	}

	private RespuestaAmbException peticioBackofficeSincronaEnviar(
			Peticion peticion,
			BackofficePeticioEntity backofficePeticio,
			BackofficeSolicitudEntity backofficeSolicitud,
			boolean generarLogs) {
		String serveiCodi = peticion.getAtributos().getCodigoCertificado();
		ServeiEntity servei = serveiRepository.findByCodi(serveiCodi);
		if (ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(servei.getTipus()) && !isPropertyBackofficeMock()) {
			return peticioBackofficeSincronaEnviarEnrutadorMultiple(
					peticion,
					servei,
					backofficePeticio,
					backofficeSolicitud,
					generarLogs);
		}
		Respuesta respuesta = null;
		Exception excepcioDurantPeticio = null;
		ByteArrayOutputStream xmlPeticio = new ByteArrayOutputStream();
		ByteArrayOutputStream xmlResposta = new ByteArrayOutputStream();
		PeticioRespostaHandler peticioRespostaHandler = new PeticioRespostaHandler(
				xmlPeticio,
				xmlResposta);
		DatosEspecificosHandler datosEspecificosHandler = new DatosEspecificosHandler();
		try {
			// El destí es resol abans de modificar les dades específiques (els resolutors d'entitats en necessiten els namespaces)
			BackofficeDesti desti = getBackofficeDesti(servei, peticion, backofficePeticio);
			if (generarLogs) {
				log.debug("Enviant petició síncrona al backoffice " +
						getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + "(" +
						"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipusEfectiu() + ", " +
						"backofficeDesti=" + desti + ")");
			}
			modificarDatosEspecificosTransmisionPeticio(peticion);
			EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
					getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
					desti,
					peticioRespostaHandler,
					datosEspecificosHandler);
			respuesta = emiservBackoffice.peticionSincrona(peticion);
			comprovarTransmisionsResposta(respuesta);
			copiarDatosEspecificosResposta(
					respuesta,
					datosEspecificosHandler,
					generarLogs);
			modificarDatosEspecificosTransmisionResposta(
					respuesta,
					peticion.getAtributos().getCodigoCertificado());
			emplenarCampsBuits(respuesta);
		} catch (Exception ex) {
			if (generarLogs) {
				log.error("Error al enviar la petició síncrona al backoffice " +
						getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
						ex);
			}
			excepcioDurantPeticio = ex;
		} finally {
			if (xmlPeticio.size() == 0) {
				peticioXmlToString(peticion, xmlPeticio);
			}
			processarComunicacioBackoffice(
					backofficePeticio,
					backofficeSolicitud,
					peticion.getAtributos(),
					(respuesta != null) ? respuesta.getAtributos() : null,
					xmlPeticio,
					xmlResposta,
					excepcioDurantPeticio);
		}
		return new RespuestaAmbException(
				respuesta,
				excepcioDurantPeticio);
	}

	private void copiarDatosEspecificosResposta(
			Respuesta respuesta,
			DatosEspecificosHandler datosEspecificosHandler,
			boolean generarLogs) throws TransformerException {
		if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
			for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
				if (transmisionDatos != null && transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTransmision() != null) {
					String idSolicitud = transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud();
					Element datosEspecificos = datosEspecificosHandler.getDatosEspecificosRespostaComElement(
							idSolicitud);
					if (datosEspecificos != null) {
						if (generarLogs) {
							log.debug("Copiant dades específiques a la resposta(idSolicitud=" + idSolicitud + "): " + datosEspecificosHandler.getDatosEspecificosRespostaComString(idSolicitud) + ".");
						}
						transmisionDatos.setDatosEspecificos(datosEspecificos);
					}
				}
			}
		}
	}

	/**
	 * Els backoffices que no poden processar la petició retornen l'estat de l'error sense transmissions.
	 */
	static void comprovarTransmisionsResposta(Respuesta respuesta) {
		if (respuesta == null) {
			throw new BackofficeException("El backoffice no ha retornat cap resposta");
		}
		if (respuesta.getTransmisiones() == null || respuesta.getTransmisiones().getTransmisionDatos() == null || respuesta.getTransmisiones().getTransmisionDatos().isEmpty()) {
			Estado estado = respuesta.getAtributos() != null ? respuesta.getAtributos().getEstado() : null;
			throw new BackofficeException("El backoffice ha retornat una resposta sense transmissions" +
					(estado != null ? " (codigoEstado=" + estado.getCodigoEstado() + ", literalError=" + estado.getLiteralError() + ")" : ""));
		}
	}

	/**
	 * Enrutadors múltiples amb rutes de tipus BACKOFFICE: s'envia la petició en paral·lel als backoffices
	 * de totes les rutes i s'escull la resposta amb el resolutor de respostes del servei (el mateix que fa
	 * servir l'enrutador per a les respostes de les rutes de tipus EMISERV).
	 */
	private RespuestaAmbException peticioBackofficeSincronaEnviarEnrutadorMultiple(
			Peticion peticion,
			ServeiEntity servei,
			BackofficePeticioEntity backofficePeticio,
			BackofficeSolicitudEntity backofficeSolicitud,
			boolean generarLogs) {
		RespostaBackofficeEntitat escollida = null;
		Exception excepcioDurantPeticio = null;
		try {
			List<BackofficeDesti> destins = backofficeDestiHelper.getDestinsEnrutadorMultiple(servei);
			if (generarLogs) {
				log.debug("Enviant petició síncrona als backoffices de l'enrutador múltiple " +
						getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + "(" +
						"backofficeDestins=" + destins + ")");
			}
			modificarDatosEspecificosTransmisionPeticio(peticion);
			List<RespostaBackofficeEntitat> respostes = new ArrayList<RespostaBackofficeEntitat>();
			ExecutorService executor = Executors.newFixedThreadPool(destins.size());
			try {
				List<Future<RespostaBackofficeEntitat>> futures = new ArrayList<Future<RespostaBackofficeEntitat>>();
				for (BackofficeDesti desti: destins) {
					// Cada fil fa servir la seva còpia de la petició (els DOM no són segurs entre fils)
					Peticion copia = copiarPeticio(peticion);
					futures.add(executor.submit(() -> enviarPeticioSincronaBackoffice(copia, desti, generarLogs)));
				}
				for (Future<RespostaBackofficeEntitat> future: futures) {
					respostes.add(future.get());
				}
			} finally {
				executor.shutdown();
			}
			for (RespostaBackofficeEntitat resposta: respostes) {
				if (resposta.getRespuesta() != null && resposta.getException() == null) {
					try {
						modificarDatosEspecificosTransmisionResposta(
								resposta.getRespuesta(),
								servei.getCodi());
						emplenarCampsBuits(resposta.getRespuesta());
					} catch (Exception ex) {
						resposta.setException(ex);
					}
				}
			}
			escollida = escollirRespostaEnrutadorMultiple(servei, respostes);
			if (escollida == null) {
				throw new BackofficeException("No s'ha pogut escollir cap resposta dels backoffices de l'enrutador múltiple (" +
						"codigoCertificado=" + servei.getCodi() + ", " +
						"entitats=" + respostes + ")");
			}
			log.debug("Resposta escollida de l'enrutador múltiple " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ": " + escollida.getEntitatCodi());
			if (backofficePeticio != null) {
				backofficePeticio.updateEntitatCodi(escollida.getEntitatCodi());
			}
			excepcioDurantPeticio = escollida.getException();
		} catch (Exception ex) {
			if (generarLogs) {
				log.error("Error al enviar la petició síncrona als backoffices de l'enrutador múltiple " +
						getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
						ex);
			}
			excepcioDurantPeticio = ex;
		} finally {
			ByteArrayOutputStream xmlPeticio = escollida != null ? escollida.getXmlPeticio() : new ByteArrayOutputStream();
			if (xmlPeticio.size() == 0) {
				peticioXmlToString(peticion, xmlPeticio);
			}
			Respuesta respuestaEscollida = escollida != null ? escollida.getRespuesta() : null;
			processarComunicacioBackoffice(
					backofficePeticio,
					backofficeSolicitud,
					peticion.getAtributos(),
					(respuestaEscollida != null) ? respuestaEscollida.getAtributos() : null,
					xmlPeticio,
					escollida != null ? escollida.getXmlResposta() : new ByteArrayOutputStream(),
					excepcioDurantPeticio);
		}
		return new RespuestaAmbException(
				escollida != null ? escollida.getRespuesta() : null,
				excepcioDurantPeticio);
	}

	private RespostaBackofficeEntitat enviarPeticioSincronaBackoffice(
			Peticion peticion,
			BackofficeDesti desti,
			boolean generarLogs) {
		RespostaBackofficeEntitat resposta = new RespostaBackofficeEntitat(desti.getEntitatCodi());
		PeticioRespostaHandler peticioRespostaHandler = new PeticioRespostaHandler(
				resposta.getXmlPeticio(),
				resposta.getXmlResposta());
		DatosEspecificosHandler datosEspecificosHandler = new DatosEspecificosHandler();
		try {
			EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
					getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
					desti,
					peticioRespostaHandler,
					datosEspecificosHandler);
			Respuesta respuesta = emiservBackoffice.peticionSincrona(peticion);
			comprovarTransmisionsResposta(respuesta);
			copiarDatosEspecificosResposta(
					respuesta,
					datosEspecificosHandler,
					generarLogs);
			resposta.setRespuesta(respuesta);
		} catch (Exception ex) {
			log.warn("Error al enviar la petició síncrona al backoffice de l'entitat " + desti.getEntitatCodi() + " " +
					getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ": " + ex.getMessage());
			resposta.setException(ex);
		}
		return resposta;
	}

	/**
	 * Escull la resposta amb el resolutor de respostes del servei. Si el servei no en té, s'escull
	 * la primera resposta correcta per ordre de les rutes.
	 */
	RespostaBackofficeEntitat escollirRespostaEnrutadorMultiple(
			ServeiEntity servei,
			List<RespostaBackofficeEntitat> respostes) throws Exception {
		List<String> entitatsOrdenades = new ArrayList<String>();
		Map<String, RespostaBackofficeEntitat> respostesPerEntitat = new HashMap<String, RespostaBackofficeEntitat>();
		for (RespostaBackofficeEntitat resposta: respostes) {
			entitatsOrdenades.add(resposta.getEntitatCodi());
			respostesPerEntitat.put(resposta.getEntitatCodi(), resposta);
		}
		String entitatCodi = null;
		if (servei.getResponseResolverClass() != null) {
			Map<String, Document> documents = new HashMap<String, Document>();
			for (RespostaBackofficeEntitat resposta: respostes) {
				documents.put(resposta.getEntitatCodi(), respostaToDocument(resposta));
			}
			@SuppressWarnings("unchecked")
			Class<ResponseResolver> responseResolverClass = (Class<ResponseResolver>)Class.forName(servei.getResponseResolverClass());
			entitatCodi = responseResolverClass.getDeclaredConstructor().newInstance().resolve(
					entitatsOrdenades,
					documents);
		} else {
			for (RespostaBackofficeEntitat resposta: respostes) {
				if (resposta.getException() == null && resposta.getRespuesta() != null) {
					entitatCodi = resposta.getEntitatCodi();
					break;
				}
			}
		}
		return entitatCodi != null ? respostesPerEntitat.get(entitatCodi) : null;
	}

	/**
	 * Document sobre el qual treballen els resolutors de respostes: un SOAP Fault si el backoffice ha
	 * retornat un error o les dades específiques de les transmissions de la resposta.
	 */
	static Document respostaToDocument(RespostaBackofficeEntitat resposta) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document document = dbf.newDocumentBuilder().newDocument();
		if (resposta.getException() != null || resposta.getRespuesta() == null) {
			Element envelope = document.createElementNS(NS_SOAPENV, "soapenv:Envelope");
			document.appendChild(envelope);
			Element body = document.createElementNS(NS_SOAPENV, "soapenv:Body");
			envelope.appendChild(body);
			Element fault = document.createElementNS(NS_SOAPENV, "soapenv:Fault");
			body.appendChild(fault);
			Element faultcode = document.createElement("faultcode");
			faultcode.setTextContent("soapenv:Server");
			fault.appendChild(faultcode);
			Element faultstring = document.createElement("faultstring");
			faultstring.setTextContent(resposta.getException() != null ? String.valueOf(resposta.getException().getMessage()) : "Sense resposta");
			fault.appendChild(faultstring);
			return document;
		}
		Element respuesta = document.createElementNS(NS_RESPUESTA3, "Respuesta");
		document.appendChild(respuesta);
		Respuesta respuestaBackoffice = resposta.getRespuesta();
		if (respuestaBackoffice.getTransmisiones() != null && respuestaBackoffice.getTransmisiones().getTransmisionDatos() != null) {
			for (TransmisionDatos transmisionDatos: respuestaBackoffice.getTransmisiones().getTransmisionDatos()) {
				if (transmisionDatos != null && transmisionDatos.getDatosEspecificos() instanceof Node) {
					respuesta.appendChild(document.importNode((Node)transmisionDatos.getDatosEspecificos(), true));
				}
			}
		}
		return document;
	}

	/** Còpia de la petició per enviar-la des d'un altre fil (amb una còpia pròpia de les dades específiques). */
	static Peticion copiarPeticio(Peticion peticion) throws Exception {
		Peticion copia = new Peticion();
		copia.setAtributos(peticion.getAtributos());
		if (peticion.getSolicitudes() != null) {
			Solicitudes solicitudes = new Solicitudes();
			solicitudes.setSolicitudTransmision(new ArrayList<SolicitudTransmision>());
			for (SolicitudTransmision solicitud: peticion.getSolicitudes().getSolicitudTransmision()) {
				SolicitudTransmision solicitudCopia = new SolicitudTransmision();
				solicitudCopia.setDatosGenericos(solicitud.getDatosGenericos());
				Object datosEspecificos = solicitud.getDatosEspecificos();
				if (datosEspecificos instanceof Node) {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);
					Document document = dbf.newDocumentBuilder().newDocument();
					datosEspecificos = document.appendChild(document.importNode((Node)datosEspecificos, true));
				}
				solicitudCopia.setDatosEspecificos(datosEspecificos);
				solicitudes.getSolicitudTransmision().add(solicitudCopia);
			}
			copia.setSolicitudes(solicitudes);
		}
		return copia;
	}

	/** Resposta del backoffice d'una entitat d'un enrutador múltiple. */
	static class RespostaBackofficeEntitat {
		private final String entitatCodi;
		private final ByteArrayOutputStream xmlPeticio = new ByteArrayOutputStream();
		private final ByteArrayOutputStream xmlResposta = new ByteArrayOutputStream();
		private Respuesta respuesta;
		private Exception exception;
		RespostaBackofficeEntitat(String entitatCodi) {
			this.entitatCodi = entitatCodi;
		}
		String getEntitatCodi() {
			return entitatCodi;
		}
		ByteArrayOutputStream getXmlPeticio() {
			return xmlPeticio;
		}
		ByteArrayOutputStream getXmlResposta() {
			return xmlResposta;
		}
		Respuesta getRespuesta() {
			return respuesta;
		}
		void setRespuesta(Respuesta respuesta) {
			this.respuesta = respuesta;
		}
		Exception getException() {
			return exception;
		}
		void setException(Exception exception) {
			this.exception = exception;
		}
		@Override
		public String toString() {
			return entitatCodi + (exception != null ? "(error: " + exception.getMessage() + ")" : "");
		}
	}

	private ConfirmacionPeticionAmbException peticioBackofficeAsincronaEnviar(
			Peticion peticion,
			BackofficePeticioEntity backofficePeticio) {
		String serveiCodi = peticion.getAtributos().getCodigoCertificado();
		ServeiEntity servei = serveiRepository.findByCodi(serveiCodi);
		if (BackofficeAsyncTipusEnumDto.REENVIAR_BACKOFFICE.equals(servei.getBackofficeCaibAsyncTipusEfectiu())) {
			log.debug("Reenviant petició asíncrona al backoffice " +
					getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + "(" +
					"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipusEfectiu() + ", " +
					"backofficeUrl=" + servei.getBackofficeCaibUrl() + ")");
			ByteArrayOutputStream xmlPeticio = new ByteArrayOutputStream();
			ByteArrayOutputStream xmlResposta = new ByteArrayOutputStream();
			ConfirmacionPeticion confirmacionPeticion = null;
			BackofficeException excepcioDurantPeticio = null;
			try {
				BackofficeDesti desti = getBackofficeDesti(servei, peticion, backofficePeticio);
				modificarDatosEspecificosTransmisionPeticio(peticion);
				PeticioRespostaHandler peticioRespostaHandler = new PeticioRespostaHandler(
						xmlPeticio,
						xmlResposta);
				EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
						getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
						desti,
						peticioRespostaHandler,
						null);
				confirmacionPeticion = emiservBackoffice.peticionAsincrona(peticion);
			} catch (Exception ex) {
				log.error("Error al reenviar la petició asíncrona al backoffice " +
						getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
						ex);
				excepcioDurantPeticio = new BackofficeException(ex);
			} finally {
				if (xmlPeticio.size() == 0) {
					peticioXmlToString(peticion, xmlPeticio);
				}
				processarComunicacioBackoffice(
						backofficePeticio,
						null,
						peticion.getAtributos(),
						(confirmacionPeticion != null) ? confirmacionPeticion.getAtributos() : null,
						xmlPeticio,
						xmlResposta,
						excepcioDurantPeticio);
			}
			return new ConfirmacionPeticionAmbException(
					confirmacionPeticion,
					excepcioDurantPeticio);
		} else {
			log.debug("Emmagatzemant petició asíncrona per a reenviar-la posteriorment al backoffice com a sol·licituds síncrones " +
					getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + "(" +
					"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipusEfectiu() + ", " +
					"backofficeUrl=" + servei.getBackofficeCaibUrl() + ", " +
					"backofficeAsyncTer=" + servei.getBackofficeCaibAsyncTer() + ")");
			ConfirmacionPeticion confirmacionPeticion = new ConfirmacionPeticion();
			Atributos atributos = new Atributos();
			atributos.setIdPeticion(peticion.getAtributos().getIdPeticion());
			atributos.setCodigoCertificado(peticion.getAtributos().getCodigoCertificado());
			Estado estado = new Estado();
			atributos.setEstado(estado);
			estado.setCodigoEstado("0002");
			estado.setTiempoEstimadoRespuesta(calcularTerPeticio(backofficePeticio));
			atributos.setNumElementos("0");
			atributos.setTimeStamp(crearTimestampScsp(new Date()));
			confirmacionPeticion.setAtributos(atributos);
			return new ConfirmacionPeticionAmbException(confirmacionPeticion);
		}
	}

	private RespuestaAmbException solicitudResposta(
			SolicitudRespuesta solicitudRespuesta,
			BackofficePeticioEntity backofficePeticio) {
		String serveiCodi = solicitudRespuesta.getAtributos().getCodigoCertificado();
		ServeiEntity servei = serveiRepository.findByCodi(serveiCodi);
		if (BackofficeAsyncTipusEnumDto.REENVIAR_BACKOFFICE.equals(servei.getBackofficeCaibAsyncTipusEfectiu())) {
			log.debug("Reenviant sol·licitud de resposta al backoffice " +
					getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + "(" +
					"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipusEfectiu() + ", " +
					"backofficeUrl=" + servei.getBackofficeCaibUrl() + ")");
			ByteArrayOutputStream xmlPeticio = new ByteArrayOutputStream();
			ByteArrayOutputStream xmlResposta = new ByteArrayOutputStream();
			PeticioRespostaHandler peticioRespostaHandler = new PeticioRespostaHandler(
					xmlPeticio,
					xmlResposta);
			DatosEspecificosHandler datosEspecificosHandler = new DatosEspecificosHandler();
			Respuesta respuesta = null;
			Exception excepcioDurantPeticio = null;
			try {
				// Només per als serveis BACKOFFICE: els enrutadors sempre processen les asíncrones com a síncrones
				EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
						getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()),
						getBackofficeDesti(servei, null, null),
						peticioRespostaHandler,
						datosEspecificosHandler);
				respuesta = emiservBackoffice.solicitarRespuesta(solicitudRespuesta);
				if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
					for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
						if (transmisionDatos != null && transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTransmision() != null) {
							String idSolicitud = transmisionDatos.getDatosGenericos().getTransmision().getIdSolicitud();
							Element datosEspecificos = datosEspecificosHandler.getDatosEspecificosRespostaComElement(
									idSolicitud);
							if (datosEspecificos != null) {
								transmisionDatos.setDatosEspecificos(datosEspecificos);
							}
						}
					}
				}
				modificarDatosEspecificosTransmisionResposta(
						respuesta,
						solicitudRespuesta.getAtributos().getCodigoCertificado());
				emplenarCampsBuits(respuesta);
			} catch (Exception ex) {
				log.error("Error al reenviar la sol·licitud de resposta al backoffice " +
						getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()),
						ex);
				excepcioDurantPeticio = new BackofficeException(ex);
			} finally {
				processarComunicacioBackoffice(
						backofficePeticio,
						null,
						solicitudRespuesta.getAtributos(),
						(respuesta != null) ? respuesta.getAtributos() : null,
						xmlPeticio,
						xmlResposta,
						excepcioDurantPeticio);
			}
			return new RespuestaAmbException(
					respuesta,
					excepcioDurantPeticio);
		} else {
			log.debug("Consultant estat de la petició asíncrona " + 
					getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + "(" +
					"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipusEfectiu() + ", " +
					"backofficeUrl=" + servei.getBackofficeCaibUrl() + ", " +
					"backofficeAsyncTer=" + servei.getBackofficeCaibAsyncTer() + ")");
			Respuesta respuesta = new Respuesta();
			try {
				Atributos atributos = new Atributos();
				atributos.setIdPeticion(solicitudRespuesta.getAtributos().getIdPeticion());
				atributos.setCodigoCertificado(solicitudRespuesta.getAtributos().getCodigoCertificado());
				Estado estado = new Estado();
				atributos.setEstado(estado);
				atributos.setTimeStamp(crearTimestampScsp(new Date()));
				respuesta.setAtributos(atributos);
				int numSolicituds = backofficeSolicitudRepository.countByPeticio(backofficePeticio);
				log.debug("Comprovant si s'han enviat totes les sol·licituds al backoffice " +
						getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + "(" +
						"total=" + numSolicituds + ", " +
						"enviades=" + backofficePeticio.getProcessadesTotal() + ")");
				boolean respostaDisponible = (numSolicituds == backofficePeticio.getProcessadesTotal()) && backofficePeticio.getProcessadesError() == 0;
				if (respostaDisponible) {
					Transmisiones transmisiones = new Transmisiones();
					transmisiones.setTransmisionDatos(new ArrayList<TransmisionDatos>());
					List<BackofficeSolicitudEntity> solicituds = backofficeSolicitudRepository.findByPeticioOrderByIdAsc(backofficePeticio);
					for (BackofficeSolicitudEntity solicitud: solicituds) {
						transmisiones.getTransmisionDatos().add(
								solicitudToTransmisionDatos(
										solicitud,
										solicitudRespuesta.getAtributos().getCodigoCertificado()));
					}
					respuesta.setTransmisiones(transmisiones);
					estado.setCodigoEstado("0003");
					atributos.setNumElementos(Integer.valueOf(solicituds.size()).toString());
				} else {
					estado.setCodigoEstado("0002");
					estado.setTiempoEstimadoRespuesta(
							calcularTerPeticio(backofficePeticio));
					atributos.setNumElementos("0");
				}
				return new RespuestaAmbException(respuesta);
			} catch (Exception ex) {
				log.error("Error al comprovar l'estat de la petició asíncrona " +
						getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()),
						ex);
				return new RespuestaAmbException(
						respuesta,
						ex);
			}
		}
	}

	private void processarComunicacioBackoffice(
			BackofficePeticioEntity backofficePeticio,
			BackofficeSolicitudEntity backofficeSolicitud,
			Atributos peticionAtributos,
			Atributos respuestaAtributos,
			ByteArrayOutputStream xmlPeticio,
			ByteArrayOutputStream xmlResposta,
			Throwable excepcioDurantPeticio) {
		log.debug("Processant comunicació backoffice (" +
				"backofficePeticio=" + backofficePeticio + ", " +
				"backofficeSolicitud=" + backofficeSolicitud + ", " +
				"peticionAtributos=" + peticionAtributos + ", " +
				"respuestaAtributos=" + respuestaAtributos + ", " +
				"excepcioDurantPeticio=" + excepcioDurantPeticio + ")");
		String xmlPet = (testXmlPeticio != null) ? testXmlPeticio : xmlPeticio.toString();
		log.debug("Emmagatzemant missatge XML de la petició enviada al backoffice " + getIdentificacioDelsAtributsScsp(peticionAtributos) + " (" +
				"xml=" + xmlPet + ")");
		peticioBackofficeNovaComunicacio(
				backofficePeticio,
				backofficeSolicitud,
				xmlPet,
				null,
				null);
		Respuesta respuesta = new Respuesta();
		if (respuestaAtributos != null) {
			respuesta.setAtributos(respuestaAtributos);
		} else {
			Atributos attrs = new Atributos();
			Estado estado = new Estado();
			estado.setCodigoEstado("0904");
			attrs.setEstado(estado);
			respuesta.setAtributos(attrs);
		}
		String comunicacioError = null;
		if (excepcioDurantPeticio != null) {
			comunicacioError = ExceptionUtils.getFullStackTrace(excepcioDurantPeticio);
		}
		String xmlRes = (testXmlResposta != null) ? testXmlResposta : xmlResposta.toString();
		log.debug("Emmagatzemant missatge XML de la resposta rebuda del backoffice " + getIdentificacioDelsAtributsScsp(peticionAtributos) + " (" +
				"xml=" + xmlRes + ", " +
				"comunicacioError=" + comunicacioError + ")");
		peticioBackofficeNovaComunicacio(
				backofficePeticio,
				backofficeSolicitud,
				xmlRes,
				comunicacioError,
				respuesta);
	}

	private void peticioBackofficeNovaComunicacio(
			BackofficePeticioEntity peticio,
			BackofficeSolicitudEntity solicitud,
			String xml,
			String comunicacioError,
			Respuesta respuesta) {
		if (respuesta != null) {
			BackofficeComunicacioEntity comunicacio = peticio.getComunicacioDarrera();
			if (comunicacio == null) {
				throw new ValidationException(
						"No s'ha trobat la comunicació amb el backoffice");
			}
			comunicacio.updateResposta(xml);
			if (comunicacioError != null) {
				comunicacio.updateError(comunicacioError);
			}
		} else {
			BackofficeComunicacioEntity comunicacio = BackofficeComunicacioEntity.getBuilder(
					peticio,
					xml).build();
			backofficeComunicacioRepository.save(comunicacio);
			peticio.updateComunicacioDarrera(
					comunicacio);
			if (solicitud != null) {
				solicitud.updateComunicacioDarrera(
						comunicacio);
			}
		}
		// Només actualitzam l'estat si aquesta comunicació correspon a una petició
		// SCSP normal. És a dir, que no prové de la tasca automàtica encarregada de
		// processar les peticions asíncrones a backoffices pendents.
		// Aquestes peticions arriben amb el paràmetre solicitud != null.
		if (solicitud == null && respuesta != null) {
			if (respuesta.getAtributos() != null && respuesta.getAtributos().getEstado() != null) {
				boolean error = !"0003".equals(respuesta.getAtributos().getEstado().getCodigoEstado());
				if (error)
					peticio.updateEstat(PeticioEstatEnumDto.ERROR);
				else
					peticio.updateEstat(PeticioEstatEnumDto.TRAMITADA);
				// Update estat SCSP
				updateScspEstat(peticio.getScspPeticionRespuesta(), peticio);
				List<BackofficeSolicitudEntity> solicituds = backofficeSolicitudRepository.findByPeticioOrderByIdAsc(peticio);
				for (BackofficeSolicitudEntity sol: solicituds) {
					sol.updateEstat(BackofficeSolicitudEstatEnumDto.TRAMITADA);
				}
			} else {
				throw new ValidationException(
						"No s'ha trobat l'estat de la resposta del backoffice");
			}
		}
	}

	private void modificarDatosEspecificosTransmisionPeticio(
			Peticion peticion) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, SAXException, IOException {
		// Elimina els atributs xmlns de l'element DatosEspecificos
		for (SolicitudTransmision solicitudTransmision: peticion.getSolicitudes().getSolicitudTransmision()) {
			if (solicitudTransmision.getDatosEspecificos() != null) {
				Element datosEspecificos = (Element)solicitudTransmision.getDatosEspecificos();
				String datosEspecificosModificats = modificarDatosEspecificos(
						xmlHelper.nodeToString(datosEspecificos),
						"");
				solicitudTransmision.setDatosEspecificos(
						xmlHelper.bytesToDocument(
								datosEspecificosModificats.getBytes()).getDocumentElement());
			}
		}
	}

	private String modificarDatosEspecificos(
			String datosEspecificosText,
			String codigoCertificado) {
		String datosEspecificosSenseNs = removeXmlStringNamespaceAndPreamble(datosEspecificosText);
		// Cercam l'inici de l'etiqueta de DatosEspecificos, tenint en compte
		// que pot arribar amb diferents formats canviant majúscules i minúscules.
		String token = "<DatosEspecificos";
		int indexInici = datosEspecificosSenseNs.indexOf(token);
		if (indexInici != -1) {
			indexInici += token.length();
		} else {
			token = "<datosEspecificos";
			indexInici = datosEspecificosSenseNs.indexOf(token);
			if (indexInici != -1) {
				indexInici += token.length();
			} else {
				token = "<datosespecificos";
				indexInici = datosEspecificosSenseNs.indexOf(token);
				if (indexInici != -1) {
					indexInici += token.length();
				} else {
					token = "<DATOSESPECIFICOS";
					indexInici = datosEspecificosSenseNs.indexOf(token);
					if (indexInici != -1) {
						indexInici += token.length();
					}
				}
			}
		}// Cercam el final de l'etiqueta de DatosEspecificos
		int indexFi = datosEspecificosSenseNs.indexOf(">", indexInici);
		if (indexInici != -1 && !datosEspecificosSenseNs.substring(indexFi - 1, indexFi).equals("/")) {
			// Depenent de la propietat [...].processar.datos.especificos.peticio
			// substituim l'etiqueta DatosEspecificos pel seu equivalent
			// normalitzat.
			boolean processar = isBackofficeProcessarDatosEspecificosPeticio(codigoCertificado);
			String tokenInici = (processar) ? "<DatosEspecificos" : datosEspecificosSenseNs.substring(0, indexInici);
			String tokenFi = "</" + token.substring(1);
			int indexTokenFi = datosEspecificosSenseNs.indexOf(tokenFi);
			StringBuilder datosEspecificosModificats = new StringBuilder();
			datosEspecificosModificats.append(tokenInici);
			datosEspecificosModificats.append(datosEspecificosSenseNs.substring(indexFi, indexTokenFi));
			if (processar) {
				datosEspecificosModificats.append("</" + tokenInici.substring(1));
			} else {
				datosEspecificosModificats.append(tokenFi);
			}
			datosEspecificosModificats.append(datosEspecificosSenseNs.substring(indexTokenFi + tokenFi.length()));
			return datosEspecificosModificats.toString();
		} else {
			return datosEspecificosSenseNs;
		}
	}

	private void modificarDatosEspecificosTransmisionResposta(
			Respuesta respuesta,
			String serveiCodi) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, SAXException, IOException {
		for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
			if (transmisionDatos.getDatosEspecificos() != null) {
				Element datosEspecificos = (Element)transmisionDatos.getDatosEspecificos();
				String datosEspecificosModificats = modificarDatosEspecificosResposta(
						xmlHelper.nodeToString(datosEspecificos),
						serveiCodi);
				transmisionDatos.setDatosEspecificos(
						xmlHelper.bytesToDocument(datosEspecificosModificats.getBytes()).getDocumentElement());
			}
		}
	}

	private String modificarDatosEspecificosResposta(
			String datosEspecificosText,
			String serveiCodi) {
		String token1 = "DatosEspecificos";
		String token2 = "datosEspecificos";
		int indexDatespInici = datosEspecificosText.indexOf(token1);
		int indexDatespFi = 0;
		if (indexDatespInici != -1) {
			indexDatespFi = indexDatespInici + token1.length();
		} else {
			indexDatespInici = datosEspecificosText.indexOf(token2);
			if (indexDatespInici != -1) {
				indexDatespFi = indexDatespInici + token2.length();
			}
		}
		int indexFi = datosEspecificosText.indexOf(">");
		if (indexDatespInici != -1) {
			String datosEspecificosAmbNs1 = datosEspecificosText.substring(1, indexDatespInici) + token1;
			StringBuilder datosEspecificosSb = new StringBuilder();
			datosEspecificosSb.append("<");
			datosEspecificosSb.append(datosEspecificosAmbNs1);
			int indexXmlns = datosEspecificosText.indexOf("xmlns");
			int indexXmlnsDatesp = datosEspecificosText.indexOf("esquemas/datosespecificos");
			boolean conteXmlns = indexXmlns != -1 && indexXmlnsDatesp != -1 && indexXmlnsDatesp < indexFi;
			if (!conteXmlns) {
				String xmlns;
				ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.findByCodigoCertificado(serveiCodi);
				if (scspCoreServicio.getVersionEsquema().contains("2")) {
					xmlns = " xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\"";
				} else if (scspCoreServicio.getVersionEsquema().contains("3")) {
					xmlns = " xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"";
				} else {
					xmlns = "";
				}
				datosEspecificosSb.append(xmlns);
			}
			int indexDatespBarraInici = datosEspecificosText.indexOf("/" + datosEspecificosAmbNs1);
			if (indexDatespBarraInici == -1) {
				String datosEspecificosAmbNs2 = datosEspecificosText.substring(1, indexDatespInici) + token2;
				indexDatespBarraInici = datosEspecificosText.indexOf("/" + datosEspecificosAmbNs2);
			}
			datosEspecificosSb.append(datosEspecificosText.substring(indexDatespFi, indexDatespBarraInici + 1));
			datosEspecificosSb.append(datosEspecificosAmbNs1);
			datosEspecificosSb.append(">");
			return datosEspecificosSb.toString();
		} else {
			return null;
		}
	}

	private TransmisionDatos solicitudToTransmisionDatos(
			BackofficeSolicitudEntity solicitud,
			String serveiCodi) throws IOException, ParserConfigurationException, SAXException {
		TransmisionDatos transmisionDatos = new TransmisionDatos();
		String xmlResposta = solicitud.getComunicacioDarrera().getRespostaXml();
		String datosGenericosXml = getContingutEtiquetaXml(
				xmlResposta,
				"DatosGenericos",
				"datosGenericos");
		DatosGenericos datosGenericos = JAXB.unmarshal(
				new StringReader(datosGenericosXml),
				DatosGenericos.class);
		transmisionDatos.setDatosGenericos(datosGenericos);
		String datosEspecificosXml = getContingutEtiquetaXml(
				xmlResposta,
				"DatosEspecificos",
				"datosEspecificos");
		if (datosEspecificosXml != null) {
			String datosEspecificosModificats = modificarDatosEspecificosResposta(
					datosEspecificosXml,
					serveiCodi);
			transmisionDatos.setDatosEspecificos(
					xmlHelper.bytesToDocument(datosEspecificosModificats.getBytes()).getDocumentElement());
		}
		return transmisionDatos;
	}

	private void emplenarCampsBuits(
			Respuesta respuesta) {
		if (respuesta.getTransmisiones() != null && respuesta.getTransmisiones().getTransmisionDatos() != null) {
			for (TransmisionDatos transmisionDatos: respuesta.getTransmisiones().getTransmisionDatos()) {
				if (transmisionDatos.getDatosGenericos() != null && transmisionDatos.getDatosGenericos().getTransmision() != null) {
					Transmision transmision = transmisionDatos.getDatosGenericos().getTransmision();
					if (transmision.getFechaGeneracion() == null || transmision.getFechaGeneracion().isEmpty()) {
						transmision.setFechaGeneracion(
								new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
					}
					if (transmision.getIdTransmision() == null || transmision.getIdTransmision().isEmpty()) {
						transmision.setIdTransmision(transmision.getIdSolicitud());
					}
				}
			}
		}
	}

	private String getContingutEtiquetaXml(
			String xml,
			String... etiquetes) {
		int indexInici = -1;
		String etiquetaActual = null;
		for (String etiqueta: etiquetes) {
			etiquetaActual = etiqueta;
			indexInici = xml.indexOf(etiqueta);
			if (indexInici != -1) {
				break;
			}
		}
		if (indexInici != -1) {
			String etiquetaFi = etiquetaActual + ">";
			int indexFi = xml.indexOf(etiquetaFi, indexInici + etiquetaActual.length());
			int indexIniciTag = xml.substring(0, indexInici).lastIndexOf("<");
			return xml.substring(indexIniciTag, indexFi + etiquetaFi.length());
		} else {
			throw new RuntimeException("No s'ha trobat cap coincidència de les etiquetes a dins el missatge XML");
		}
	}

	private Element getElementSolicitudTransmision(
			String xml,
			String solicitudId) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		Document document = xmlHelper.bytesToDocument(xml.getBytes());
		String nsprefix = (xmlHelper.isPeticioScspV2(document)) ? "peticion2" : "peticion3";
		NodeList solicitudTransmisionNodes = xmlHelper.getNodeList(
				document,
				"//" + nsprefix + ":Peticion/" + nsprefix + ":Solicitudes/" + nsprefix + ":SolicitudTransmision");
		for (int i = 0; i < solicitudTransmisionNodes.getLength(); i++) {
			Element solicitudTransmision = (Element)solicitudTransmisionNodes.item(i);
			String idSolicitud = xmlHelper.getTextFromFirstNode(
					solicitudTransmision,
					"./" + nsprefix + ":DatosGenericos/" + nsprefix + ":Transmision/" + nsprefix + ":IdSolicitud");
			if (solicitudId.equals(idSolicitud)) {
				return solicitudTransmision;
			}
		}
		return null;
	}

	private static String removeXmlStringNamespaceAndPreamble(String xmlString) {
		return xmlString.replaceAll("(<\\?[^<]*\\?>)?", ""). /* remove preamble */
				replaceAll("xmlns.*?(\"|\').*?(\"|\')", ""). /* remove xmlns declaration */
				replaceAll("(<)(\\w+:)(.*?>)", "$1$3"). /* remove opening tag prefix */
				replaceAll("(</)(\\w+:)(.*?>)", "$1$3"); /* remove closing tags prefix */
	}

	private String getIdentificacioDelsAtributsScsp(Atributos atributos) {
		String codigoCertificado = "???";
		String idPeticion = "???";
		if (atributos != null) {
			codigoCertificado = atributos.getCodigoCertificado();
			idPeticion = atributos.getIdPeticion();
		}
		return "(codigoCertificado=" + codigoCertificado + ", idPeticion=" + idPeticion + ")";
	}

	private Integer calcularTerPeticio(BackofficePeticioEntity peticio) {
		long terMilis = peticio.getTempsEstimatRespostaData().getTime();
		long araMilis = System.currentTimeMillis();
		double restaEnHores = (terMilis - araMilis) / 3600000.0;
		if (restaEnHores <= 0) {
			return 0;
		} else {
			return Double.valueOf(Math.ceil(restaEnHores)).intValue();
		}
	}

	/**
	 * Backoffice al qual s'ha d'enviar la petició (el del servei o, per als enrutadors, el de la ruta
	 * de l'entitat). Per als enrutadors es guarda l'entitat a la petició de backoffice.
	 */
	private BackofficeDesti getBackofficeDesti(
			ServeiEntity servei,
			Peticion peticion,
			BackofficePeticioEntity backofficePeticio) {
		if (isPropertyBackofficeMock()) {
			return null;
		}
		try {
			BackofficeDesti desti = backofficeDestiHelper.getDesti(servei, peticion);
			if (backofficePeticio != null && desti.getEntitatCodi() != null) {
				backofficePeticio.updateEntitatCodi(desti.getEntitatCodi());
			}
			return desti;
		} catch (ValidationException ex) {
			log.error("No s'ha pogut obtenir el backoffice de la petició (" +
					"codigoCertificado=" + servei.getCodi() + "): " + ex.getMessage());
			throw ex;
		}
	}

	private EmiservBackoffice getEmiservBackoffice(
			String identificacio,
			BackofficeDesti desti,
			PeticioRespostaHandler peticioRespostaHandler,
			DatosEspecificosHandler datosEspecificosHandler) throws RemoteException, NamingException, MalformedURLException {
		if (isPropertyBackofficeMock() && testBackoffice == null) {
			testBackoffice = new EmiservBackofficeHandlerProxy(
					new EmiservBackofficeImpl());
		}
		if (testBackoffice != null) {
			testBackoffice.setPeticioRespostaHandler(peticioRespostaHandler);
			return testBackoffice;
		}
		String backofficeUrl = desti.getUrl();
		String username = null;
		String password = null;
		switch(desti.getAutenticacio()) {
		case TOKEN_CAIB:
			try {
				ControladorSesion controlador = new ControladorSesion();
				controlador.autenticar(
						getBackofficePropertyUsername(desti),
						getBackofficePropertyPassword(desti));
				AuthorizationToken token = controlador.getToken();
				username = token.getUser();
				password = token.getPassword();
			} catch (Exception ex) {
				log.error("No s'ha pogut crear la instància de ControladorSesion", ex);
			}
			break;
		case TEXT_CLAR:
			username = getBackofficePropertyUsername(desti);
			password = getBackofficePropertyPassword(desti);
			break;
		case NINGUNA:
		default:
			break;
		}
		String soapAction = getBackofficePropertySoapAction(desti);
		log.debug("Generant client de backoffice EMISERV per a la petició " + identificacio + " (" +
				"backofficeUrl=" + backofficeUrl + ", " +
				"soapAction=" + soapAction + ", " +
				"entitat=" + desti.getEntitatCodi() + ", " +
				"autenticacio=" + desti.getAutenticacio() + ", " +
				"username=" + username + ", " +
				"password=" + (password != null ? password.replaceAll(".", "*") : password) + ")");
		EmiservBackoffice port = new WsClientHelper<EmiservBackoffice>().generarClientWs(
				getClass().getResource(BACKOFFICE_WSDL_RESOURCE),
				backofficeUrl,
				new QName(
						"http://caib.es/emiserv/backoffice",
						"EmiservBackofficeService"),
				username,
				password,
				soapAction,
				EmiservBackoffice.class,
				datosEspecificosHandler,
				peticioRespostaHandler);
		return port;
	}

	private String crearTimestampScsp(Date data) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(data);
	}

	/**
	 * Converteix el consentiment guardat a CORE_TRANSMISION. Les llibreries SCSP 4 hi guardaven
	 * el valor de l'esquema (Si, Ley) i les SCSP 5 hi guarden el nom de l'enum (SI, LEY, NO_OPO).
	 */
	static Consentimiento toConsentimiento(String consentimiento) {
		String valor = consentimiento.trim().replace("_", "");
		for (Consentimiento c: Consentimiento.values()) {
			if (c.name().equalsIgnoreCase(valor)) {
				return c;
			}
		}
		throw new IllegalArgumentException("Valor de consentiment desconegut: " + consentimiento);
	}

	private String getBackofficePropertyUsername(BackofficeDesti desti) {
		return getBackofficeProperty(desti, "caib.auth.username");
	}

	private String getBackofficePropertyPassword(BackofficeDesti desti) {
		return getBackofficeProperty(desti, "caib.auth.password");
	}

	private String getBackofficePropertySoapAction(BackofficeDesti desti) {
		return getBackofficeProperty(desti, "caib.soap.action");
	}

	/**
	 * Propietat de configuració del backoffice, de la més específica a la més general:
	 * es.caib.emiserv.backoffice.[SERVEI.[ENTITAT.]]nom
	 */
	private String getBackofficeProperty(BackofficeDesti desti, String nom) {
		return backofficeDestiHelper.getPropietat(desti, nom);
	}

	private boolean isBackofficeProcessarDatosEspecificosPeticio(String serveiCodi) {
		String processar = environment.getProperty(
					"es.caib.emiserv.backoffice." + serveiCodi + ".processar.datos.especificos.peticio");
		if (processar == null) {
			processar = environment.getProperty(
					"es.caib.emiserv.backoffice.processar.datos.especificos.peticio");
		}
		if (processar == null) {
			return false;
		} else {
			return Boolean.valueOf(processar).booleanValue();
		}
	}

	public class RespuestaAmbException {
		private Respuesta respuesta;
		private Exception exception;
		public RespuestaAmbException(
				Respuesta respuesta) {
			super();
			this.respuesta = respuesta;
		}
		public RespuestaAmbException(
				Respuesta respuesta,
				Exception exception) {
			super();
			this.respuesta = respuesta;
			this.exception = exception;
		}
		public Respuesta getRespuesta() {
			return respuesta;
		}
		public Exception getException() {
			return exception;
		}
	}

	public class ConfirmacionPeticionAmbException {
		private ConfirmacionPeticion confirmacionPeticion;
		private Exception exception;
		public ConfirmacionPeticionAmbException(
				ConfirmacionPeticion confirmacionPeticion) {
			super();
			this.confirmacionPeticion = confirmacionPeticion;
		}
		public ConfirmacionPeticionAmbException(
				ConfirmacionPeticion confirmacionPeticion,
				Exception exception) {
			super();
			this.confirmacionPeticion = confirmacionPeticion;
			this.exception = exception;
		}
		public ConfirmacionPeticion getConfirmacionPeticion() {
			return confirmacionPeticion;
		}
		public Exception getException() {
			return exception;
		}
	}

	private class EmiservBackofficeHandlerProxy implements EmiservBackoffice {
		private EmiservBackoffice delegate;
		private PeticioRespostaHandler peticioRespostaHandler;
		public EmiservBackofficeHandlerProxy(EmiservBackoffice delegate) {
			this.delegate = delegate;
		}
		@Override
		public Respuesta peticionSincrona(Peticion peticion) {
			peticioRespostaHandler.actualitzarXmlMock(
					toXml(peticion).getBytes(),
					null);
			Respuesta respuesta = delegate.peticionSincrona(peticion);
			peticioRespostaHandler.actualitzarXmlMock(
					null,
					toXml(respuesta).getBytes());
			return respuesta;
		}
		@Override
		public ConfirmacionPeticion peticionAsincrona(Peticion peticion) {
			peticioRespostaHandler.actualitzarXmlMock(
					toXml(peticion).getBytes(),
					null);
			ConfirmacionPeticion confirmacionPeticion = delegate.peticionAsincrona(peticion);
			peticioRespostaHandler.actualitzarXmlMock(
					null,
					toXml(confirmacionPeticion).getBytes());
			return confirmacionPeticion;
		}
		@Override
		public Respuesta solicitarRespuesta(SolicitudRespuesta solicitudRespuesta) {
			peticioRespostaHandler.actualitzarXmlMock(
					toXml(solicitudRespuesta).getBytes(),
					null);
			Respuesta respuesta = delegate.solicitarRespuesta(solicitudRespuesta);
			peticioRespostaHandler.actualitzarXmlMock(
					null,
					toXml(respuesta).getBytes());
			return respuesta;
		}
		public void setPeticioRespostaHandler(PeticioRespostaHandler peticioRespostaHandler) {
			this.peticioRespostaHandler = peticioRespostaHandler;
		}
		private String toXml(
				Object obj) {
			try {
				JAXBContext context = JAXBContext.newInstance(obj.getClass());
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				StringWriter sw = new StringWriter();
				if (obj instanceof Peticion) {
					marshaller.marshal(
							new JAXBElement<Peticion>(
									new QName("http://caib.es/emiserv/backoffice", "peticion"),
									Peticion.class,
									(Peticion)obj),
							sw);
				} else if (obj instanceof Respuesta) {
					marshaller.marshal(
							new JAXBElement<Respuesta>(
									new QName("http://caib.es/emiserv/backoffice", "respuesta"),
									Respuesta.class,
									(Respuesta)obj),
							sw);
				} else if (obj instanceof ConfirmacionPeticion) {
					marshaller.marshal(
							new JAXBElement<ConfirmacionPeticion>(
									new QName("http://caib.es/emiserv/backoffice", "confirmacionPeticion"),
									ConfirmacionPeticion.class,
									(ConfirmacionPeticion)obj),
							sw);
				} else if (obj instanceof SolicitudRespuesta) {
					marshaller.marshal(
							new JAXBElement<SolicitudRespuesta>(
									new QName("http://caib.es/emiserv/backoffice", "solicitudRespuesta"),
									SolicitudRespuesta.class,
									(SolicitudRespuesta)obj),
							sw);
				} else {
					return "<CLASSE_DESCONEGUDA>";
				}
				return sw.toString();
			} catch (JAXBException ex) {
				return ExceptionUtils.getFullStackTrace(ex);
			}
		}
	}

	private boolean isPropertyBackofficeMock() {
		String backofficeMock = environment.getProperty("es.caib.emiserv.backoffice.mock");
		if (backofficeMock != null) {
			return Boolean.valueOf(backofficeMock);
		} else {
			return false;
		}
	}

	private void peticioXmlToString(Peticion peticion, OutputStream os) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(peticion.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(peticion, os);
		} catch (JAXBException jbex) {
			log.error("Error al convertir objecte de petició a XML", jbex);
		}
	}

}
