/**
 * 
 */
package es.caib.emiserv.logic.helper;

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
import java.util.List;

import javax.naming.NamingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

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

import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.BackofficeSolicitudEstatEnumDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.exception.BackofficeException;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Consentimiento;
import es.caib.emiserv.logic.intf.service.ws.backoffice.DatosGenericos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.EmiservBackoffice;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Emisor;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Estado;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Funcionario;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Procedimiento;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitante;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitudes;
import es.caib.emiserv.logic.intf.service.ws.backoffice.TipoDocumentacion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Titular;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Transmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Transmisiones;
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
	private Environment env;

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
			terEnSegons = 3600; // Per defecte és una hora
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, terEnSegons);
		BackofficePeticioEntity peticio = BackofficePeticioEntity.getBuilder(
				peticionRespuesta,
				cal.getTime()).
				build();
		BackofficePeticioEntity peticioSaved = backofficePeticioRepository.save(peticio);
		for (String solicitudId: idSolicituds) {
			BackofficeSolicitudEntity solicitud = BackofficeSolicitudEntity.getBuilder(
					peticioSaved,
					solicitudId).build();
			backofficeSolicitudRepository.save(solicitud);
		}
		return peticioSaved;
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
				String emisorNombre = xmlHelper.getTextFromFirstNode(
						solicitudTransmisionElement,
						"//" + nsprefix + ":DatosGenericos/" + nsprefix + ":Emisor/" + nsprefix + ":NombreEmisor");
				String emisorNif = xmlHelper.getTextFromFirstNode(
						solicitudTransmisionElement,
						"//" + nsprefix + ":DatosGenericos/" + nsprefix + ":Emisor/" + nsprefix + ":NifEmisor");
				String titularDocumentoTipo = xmlHelper.getTextFromFirstNode(
						solicitudTransmisionElement,
						"//" + nsprefix + ":DatosGenericos/" + nsprefix + ":Titular/" + nsprefix + ":TipoDocumentacion");
				Node datosEspecificos = xmlHelper.getFirstNode(
						solicitudTransmisionElement,
						"//" + nsprefix + ":DatosEspecificos");
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
					solicitante.setConsentimiento(Consentimiento.valueOf(scspTransmision.getConsentimiento()));
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
		}
		int numSolicituds = backofficeSolicitudRepository.countByPeticio(backofficePeticio);
		if (numSolicituds == backofficePeticio.getProcessadesTotal()) {
			if (backofficePeticio.getProcessadesError() == 0) {
				backofficePeticio.updateEstat(PeticioEstatEnumDto.TRAMITADA);
			} else {
				backofficePeticio.updateEstat(PeticioEstatEnumDto.ERROR);
			}
		}
	}

	private RespuestaAmbException peticioBackofficeSincronaEnviar(
			Peticion peticion,
			BackofficePeticioEntity backofficePeticio,
			BackofficeSolicitudEntity backofficeSolicitud,
			boolean generarLogs) {
		String serveiCodi = peticion.getAtributos().getCodigoCertificado();
		ServeiEntity servei = serveiRepository.findByCodi(serveiCodi);
		Respuesta respuesta = null;
		Exception excepcioDurantPeticio = null;
		ByteArrayOutputStream xmlPeticio = new ByteArrayOutputStream();
		ByteArrayOutputStream xmlResposta = new ByteArrayOutputStream();
		PeticioRespostaHandler peticioRespostaHandler = new PeticioRespostaHandler(
				xmlPeticio,
				xmlResposta);
		DatosEspecificosHandler datosEspecificosHandler = new DatosEspecificosHandler();
		try {
			if (generarLogs) {
				log.debug("Enviant petició síncrona al backoffice " +
						getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + "(" +
						"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipus() + ", " +
						"backofficeUrl=" + servei.getBackofficeCaibUrl() + ")");
			}
			modificarDatosEspecificosTransmisionPeticio(peticion);
			EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
					getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
					servei,
					peticioRespostaHandler,
					datosEspecificosHandler);
			respuesta = emiservBackoffice.peticionSincrona(peticion);
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

	private ConfirmacionPeticionAmbException peticioBackofficeAsincronaEnviar(
			Peticion peticion,
			BackofficePeticioEntity backofficePeticio) {
		String serveiCodi = peticion.getAtributos().getCodigoCertificado();
		ServeiEntity servei = serveiRepository.findByCodi(serveiCodi);
		if (BackofficeAsyncTipusEnumDto.REENVIAR_BACKOFFICE.equals(servei.getBackofficeCaibAsyncTipus())) {
			log.debug("Reenviant petició asíncrona al backoffice " +
					getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + "(" +
					"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipus() + ", " +
					"backofficeUrl=" + servei.getBackofficeCaibUrl() + ")");
			ByteArrayOutputStream xmlPeticio = new ByteArrayOutputStream();
			ByteArrayOutputStream xmlResposta = new ByteArrayOutputStream();
			ConfirmacionPeticion confirmacionPeticion = null;
			BackofficeException excepcioDurantPeticio = null;
			try {
				modificarDatosEspecificosTransmisionPeticio(peticion);
				PeticioRespostaHandler peticioRespostaHandler = new PeticioRespostaHandler(
						xmlPeticio,
						xmlResposta);
				EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
						getIdentificacioDelsAtributsScsp(peticion.getAtributos()),
						servei,
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
					"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipus() + ", " +
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
		if (BackofficeAsyncTipusEnumDto.REENVIAR_BACKOFFICE.equals(servei.getBackofficeCaibAsyncTipus())) {
			log.debug("Reenviant sol·licitud de resposta al backoffice " +
					getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + "(" +
					"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipus() + ", " +
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
				EmiservBackoffice emiservBackoffice = getEmiservBackoffice(
						getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()),
						servei,
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
					"backofficeAsyncTipus=" + servei.getBackofficeCaibAsyncTipus() + ", " +
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
					document,
					"//" + nsprefix + ":DatosGenericos/" + nsprefix + ":Transmision/" + nsprefix + ":IdSolicitud");
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

	private EmiservBackoffice getEmiservBackoffice(
			String identificacio,
			ServeiEntity servei,
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
		if (!servei.isConfigurat()) {
			log.error("El servei està donat d'alta però encara no s'ha configurat (" +
					"idPeticion=" + identificacio + ", " +
					"codigoCertificado=" + servei.getCodi() + ")");
			throw new ValidationException("El servei (codi=" + servei.getCodi() + ") està donat d'alta però encara no s'ha configurat");
		}
		String backofficeUrl = servei.getBackofficeCaibUrl();
		String username = null;
		String password = null;
		switch(servei.getBackofficeCaibAutenticacio()) {
		case TOKEN_CAIB:
			try {
				ControladorSesion controlador = new ControladorSesion();
				controlador.autenticar(
						getBackofficePropertyUsername(servei.getCodi()),
						getBackofficePropertyPassword(servei.getCodi()));
				AuthorizationToken token = controlador.getToken();
				username = token.getUser();
				password = token.getPassword();
			} catch (Exception ex) {
				log.error("No s'ha pogut crear la instància de ControladorSesion", ex);
			}
			break;
		case TEXT_CLAR:
			username = getBackofficePropertyUsername(servei.getCodi());
			password = getBackofficePropertyPassword(servei.getCodi());
			break;
		case NINGUNA:
		default:
			break;
		}
		String soapAction = getBackofficePropertySoapAction(servei.getCodi());
		log.debug("Generant client de backoffice EMISERV per a la petició " + identificacio + " (" +
				"backofficeUrl=" + backofficeUrl + ", " +
				"soapAction=" + soapAction + ", " +
				"autenticacio=" + servei.getBackofficeCaibAutenticacio() + ", " +
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

	private String getBackofficePropertyUsername(String serveiCodi) {
		String username = env.getProperty(
				"es.caib.emiserv.int.backoffice." + serveiCodi + ".usuari");
		if (username == null) {
			username = env.getProperty(
					"es.caib.emiserv.backoffice." + serveiCodi + ".caib.auth.username");
		}
		if (username == null) {
			username = env.getProperty(
					"es.caib.emiserv.int.backoffice.usuari");
		}
		if (username == null) {
			username = env.getProperty(
					"es.caib.emiserv.backoffice.caib.auth.username");
		}
		return username;
	}

	private String getBackofficePropertyPassword(String serveiCodi) {
		String password = env.getProperty(
				"es.caib.emiserv.int.backoffice." + serveiCodi + ".secret");
		if (password == null) {
			password = env.getProperty(
					"es.caib.emiserv.backoffice." + serveiCodi + ".caib.auth.password");
		}
		if (password == null) {
			password = env.getProperty(
					"es.caib.emiserv.int.backoffice.secret");
		}
		if (password == null) {
			password = env.getProperty(
					"es.caib.emiserv.backoffice.caib.auth.password");
		}
		return password;
	}

	private String getBackofficePropertySoapAction(String serveiCodi) {
		String soapAction = env.getProperty(
				"es.caib.emiserv.int.backoffice." + serveiCodi + ".soap.action");
		if (soapAction == null) {
			soapAction = env.getProperty(
					"es.caib.emiserv.backoffice." + serveiCodi + ".caib.soap.action");
		}
		if (soapAction == null) {
			soapAction = env.getProperty(
					"es.caib.emiserv.int.backoffice.soap.action");
		}
		if (soapAction == null) {
			soapAction = env.getProperty(
					"es.caib.emiserv.backoffice.caib.soap.action");
		}
		return soapAction;
	}

	private boolean isBackofficeProcessarDatosEspecificosPeticio(String serveiCodi) {
		String processar = env.getProperty(
				"es.caib.emiserv.int.backoffice." + serveiCodi + ".processar.datosespecificos");
		if (processar == null) {
			processar = env.getProperty(
					"es.caib.emiserv.backoffice." + serveiCodi + ".processar.datos.especificos.peticio");
		}
		if (processar == null) {
			processar = env.getProperty(
					"es.caib.emiserv.int.backoffice.processar.datosespecificos");
		}
		if (processar == null) {
			processar = env.getProperty(
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
		String backofficeMock = env.getProperty("es.caib.emiserv.backoffice.mock");
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
