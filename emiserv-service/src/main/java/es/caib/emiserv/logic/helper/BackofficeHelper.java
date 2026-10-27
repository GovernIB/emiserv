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
import java.util.concurrent.Callable;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import es.caib.emiserv.persist.repository.ServeiRutaDestiRepository;
import es.caib.emiserv.persist.entity.ServeiRutaDestiEntity;
import es.caib.emiserv.logic.helper.ClientScspHelper.ScspCedentException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
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
import java.nio.charset.StandardCharsets;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	@Autowired
	private ConfiguracioAsincronHelper configuracioAsincronHelper;
	@Autowired
	private ClientScspHelper clientScspHelper;
	@Autowired
	private ServeiRutaDestiRepository serveiRutaDestiRepository;
	@PersistenceContext
	private EntityManager entityManager;

	private static final String NS_SOAPENV = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String NS_RESPUESTA3 = "http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta";
	/** Codi d'estat de les sol·licituds que no s'han pogut enviar al destí (error definitiu). */
	static final String CODI_ERROR_DESTI = "0503";

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

	/**
	 * Resol l'entitat destí d'una sol·licitud d'una consulta asíncrona a un enrutador (cada sol·licitud
	 * pot anar a un cedent diferent). Si no es pot resoldre, la sol·licitud finalitza amb error.
	 *
	 * @return l'id de la petició si la sol·licitud ha finalitzat amb error (s'ha de recomptar), o null.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long resoldreEntitatSolicitud(Long solicitudId) {
		BackofficeSolicitudEntity solicitud = backofficeSolicitudRepository.getOne(solicitudId);
		BackofficePeticioEntity peticio = solicitud.getPeticio();
		try {
			ServeiEntity servei = getServeiPeticio(peticio);
			String entitatCodi = backofficeDestiHelper.resoldreEntitat(
					servei,
					crearPeticioSolicitud(peticio, solicitud));
			if (entitatCodi == null || entitatCodi.isBlank()) {
				throw new ValidationException("El resolutor no ha retornat cap entitat (servei=" + servei.getCodi() + ")");
			}
			solicitud.updateEntitatCodi(entitatCodi);
			return null;
		} catch (Exception ex) {
			log.error("No s'ha pogut resoldre l'entitat destí de la sol·licitud (" +
					"idPeticion=" + peticio.getScspPeticionRespuesta().getPeticionId() + ", " +
					"idSolicitud=" + solicitud.getSolicitudId() + "): " + ex.getMessage());
			solicitud.finalitzarAmbError(CODI_ERROR_DESTI, "No s'ha pogut resoldre l'entitat destí: " + ex.getMessage());
			return peticio.getId();
		}
	}

	/**
	 * Reserva la següent sol·licitud pendent d'un destí (servei SCSP i entitat).
	 *
	 * @return l'id de la sol·licitud reservada (estat ENVIANT) o null si no n'hi ha cap.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long reservarSeguentSolicitud(
			Long certificado,
			String entitatCodi) {
		Date ara = new Date();
		List<Long> ids = backofficeSolicitudRepository.findIdsEnviablesDesti(
				Collections.singletonList(certificado),
				entitatCodi,
				ara,
				PageRequest.of(0, 5));
		for (Long id: ids) {
			// Un altre procés (o node) pot haver reservat la mateixa sol·licitud
			if (backofficeSolicitudRepository.reservarEnviament(id, ara) == 1) {
				return id;
			}
		}
		return null;
	}

	/**
	 * Envia al destí una sol·licitud reservada (ENVIANT) com a petició síncrona. Si falla es reprograma
	 * amb espera creixent fins al nombre màxim d'intents; els errors de configuració són definitius.
	 * No modifica la petició (altres sol·licituds de la mateixa petició s'envien en paral·lel): s'ha de
	 * cridar després actualitzarEstatPeticio.
	 *
	 * @return l'id de la petició de la sol·licitud.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long enviarSolicitud(Long solicitudId) {
		BackofficeSolicitudEntity solicitud = backofficeSolicitudRepository.getOne(solicitudId);
		BackofficePeticioEntity peticio = solicitud.getPeticio();
		if (!BackofficeSolicitudEstatEnumDto.ENVIANT.equals(solicitud.getEstat())) {
			log.warn("La sol·licitud no està reservada per enviar (id=" + solicitudId + ", estat=" + solicitud.getEstat() + ")");
			return peticio.getId();
		}
		String idPeticion = peticio.getScspPeticionRespuesta().getPeticionId();
		log.debug("Enviant la sol·licitud d'una petició asíncrona (" +
				"idPeticion=" + idPeticion + ", " +
				"idSolicitud=" + solicitud.getSolicitudId() + ", " +
				"entitat=" + solicitud.getEntitatCodi() + ", " +
				"intent=" + solicitud.getIntents() + ")");
		Exception excepcio;
		try {
			ServeiEntity servei = getServeiPeticio(peticio);
			ServeiRutaDestiEntity rutaEmisor = getRutaEmisorCedent(servei, solicitud);
			RespuestaAmbException respuesta;
			if (rutaEmisor != null) {
				// El cedent no té backoffice: es fa la petició SCSP al seu emisor
				respuesta = enviarSolicitudEmisorCedent(peticio, solicitud, servei, rutaEmisor);
			} else {
				respuesta = peticioBackofficeSincronaEnviar(
						crearPeticioSolicitud(peticio, solicitud),
						peticio,
						solicitud,
						false);
			}
			excepcio = respuesta.getException();
		} catch (Exception ex) {
			excepcio = ex;
		}
		if (excepcio == null) {
			solicitud.marcarTramitada();
		} else {
			String error = excepcio.getClass().getSimpleName() + ": " + excepcio.getMessage();
			ScspCedentException errorCedent = excepcio instanceof ScspCedentException ? (ScspCedentException)excepcio : null;
			boolean definitiu = excepcio instanceof ValidationException || excepcio instanceof NotFoundException ||
					(errorCedent != null && errorCedent.isDefinitiu());
			if (definitiu || solicitud.getIntents() >= configuracioAsincronHelper.getReintentsMaxim()) {
				log.error("Error definitiu enviant la sol·licitud d'una petició asíncrona (" +
						"idPeticion=" + idPeticion + ", " +
						"idSolicitud=" + solicitud.getSolicitudId() + ", " +
						"intents=" + solicitud.getIntents() + "): " + error);
				// L'estat de la transmissió és el de l'error de l'emisor del cedent, si n'ha retornat
				solicitud.finalitzarAmbError(
						errorCedent != null && errorCedent.getCodiEstat() != null ? errorCedent.getCodiEstat() : CODI_ERROR_DESTI,
						error);
			} else {
				Date properIntent = new Date(System.currentTimeMillis() + configuracioAsincronHelper.getEsperaReintentMs(solicitud.getIntents()));
				log.warn("Error enviant la sol·licitud d'una petició asíncrona, es tornarà a intentar (" +
						"idPeticion=" + idPeticion + ", " +
						"idSolicitud=" + solicitud.getSolicitudId() + ", " +
						"intents=" + solicitud.getIntents() + ", " +
						"properIntent=" + properIntent + "): " + error);
				solicitud.reprogramar(error, properIntent);
			}
		}
		return peticio.getId();
	}

	/**
	 * Recompta les sol·licituds processades de la petició (amb la fila bloquejada: les sol·licituds
	 * s'envien en paral·lel) i, si ja no en queda cap per enviar, la dona per finalitzada.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void actualitzarEstatPeticio(
			Long peticioId,
			Long darreraSolicitudId) {
		BackofficePeticioEntity peticio = backofficePeticioRepository.findAmbBloqueig(peticioId);
		if (peticio != null) {
			recomptarPeticio(peticio, darreraSolicitudId);
		}
	}

	/**
	 * Recompte de la petició dins la transacció actual (la petició s'ha d'haver obtingut amb
	 * bloqueig o ja s'ha de modificar a la transacció, com a les accions de la pantalla de gestió).
	 */
	public void recomptarPeticio(
			BackofficePeticioEntity peticio,
			Long darreraSolicitudId) {
		if (!PeticioEstatEnumDto.PENDENT.equals(peticio.getEstat())) {
			return;
		}
		int total = backofficeSolicitudRepository.countByPeticio(peticio);
		int tramitades = backofficeSolicitudRepository.countByPeticioAndEstat(peticio, BackofficeSolicitudEstatEnumDto.TRAMITADA);
		int errors = backofficeSolicitudRepository.countByPeticioAndEstat(peticio, BackofficeSolicitudEstatEnumDto.ERROR);
		String darreraSolicitud = null;
		if (darreraSolicitudId != null) {
			BackofficeSolicitudEntity solicitud = backofficeSolicitudRepository.getOne(darreraSolicitudId);
			darreraSolicitud = solicitud.getSolicitudId();
			if (solicitud.getComunicacioDarrera() != null) {
				peticio.updateComunicacioDarrera(solicitud.getComunicacioDarrera());
			}
		}
		peticio.updateProcessades(tramitades + errors, errors, darreraSolicitud);
		if (tramitades + errors >= total) {
			log.debug("Petició asíncrona finalitzada (" +
					"idPeticion=" + peticio.getScspPeticionRespuesta().getPeticionId() + ", " +
					"tramitades=" + tramitades + ", " +
					"errors=" + errors + ")");
			peticio.updateEstat(errors == 0 ? PeticioEstatEnumDto.TRAMITADA : PeticioEstatEnumDto.ERROR);
			updateScspEstat(peticio.getScspPeticionRespuesta(), peticio);
		}
	}

	/**
	 * Enviaments no finalitzats (aturada de l'aplicació o del node): es tornen a fer o, si ja s'han
	 * esgotat els intents, finalitzen amb error.
	 *
	 * @param enCurs sol·licituds que aquest node està enviant ara mateix (no s'han de recuperar).
	 * @return els ids de les peticions de les sol·licituds finalitzades amb error.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Set<Long> recuperarEnviamentsInterromputs(Set<Long> enCurs) {
		Set<Long> peticions = new HashSet<Long>();
		Date limit = new Date(System.currentTimeMillis() - configuracioAsincronHelper.getEnviamentCaducatSegons() * 1000L);
		for (BackofficeSolicitudEntity solicitud: backofficeSolicitudRepository.findByEstatAndDataEnviamentBefore(BackofficeSolicitudEstatEnumDto.ENVIANT, limit)) {
			if (enCurs.contains(solicitud.getId())) {
				continue;
			}
			String error = "Enviament interromput (iniciat el " + solicitud.getDataEnviament() + ")";
			if (solicitud.getIntents() >= configuracioAsincronHelper.getReintentsMaxim()) {
				solicitud.finalitzarAmbError(CODI_ERROR_DESTI, error);
				peticions.add(solicitud.getPeticio().getId());
			} else {
				solicitud.reprogramar(error, new Date());
			}
			log.warn("Recuperada una sol·licitud amb l'enviament interromput (" +
					"id=" + solicitud.getId() + ", " +
					"idSolicitud=" + solicitud.getSolicitudId() + ", " +
					"estat=" + solicitud.getEstat() + ")");
		}
		return peticions;
	}

	private ServeiEntity getServeiPeticio(BackofficePeticioEntity peticio) {
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.getOne(peticio.getScspPeticionRespuesta().getCertificado());
		ServeiEntity servei = serveiRepository.findByCodi(scspCoreServicio.getCodigoCertificado());
		if (servei == null) {
			throw new NotFoundException(scspCoreServicio.getCodigoCertificado(), ServeiEntity.class);
		}
		return servei;
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
		if (servei.isEnrutador()) {
			// Les sol·licituds s'envien als cedents amb un ritme limitat: el TER depèn del nombre de
			// sol·licituds i de les que ja hi ha pendents per al mateix servei
			long pendents = backofficeSolicitudRepository.countPendentsCertificat(peticionRespuesta.getCertificado());
			terEnSegons = configuracioAsincronHelper.calcularTerMinuts(servei, idSolicituds.size() + pendents) * 60;
		} else if (terEnSegons == 0) {
			// Per defecte és una hora
			terEnSegons = 3600;
		}
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

	/**
	 * Petició SCSP d'una única sol·licitud d'una petició asíncrona, construïda amb les dades de la
	 * sol·licitud desades per l'emisor (transmissió i missatge de la petició).
	 */
	Peticion crearPeticioSolicitud(
			BackofficePeticioEntity backofficePeticio,
			BackofficeSolicitudEntity backofficeSolicitud) throws Exception {
		ScspCorePeticionRespuestaEntity scspPeticionRespuesta = backofficePeticio.getScspPeticionRespuesta();
		ScspCoreTransmisionEntity scspTransmision = scspCoreTransmisionRepository.findByPeticionIdAndSolicitudId(
				scspPeticionRespuesta.getPeticionId(),
				backofficeSolicitud.getSolicitudId());
		if (scspTransmision == null) {
			throw new NotFoundException(
					scspPeticionRespuesta.getPeticionId() + ", " + backofficeSolicitud.getSolicitudId(),
					ScspCoreTransmisionEntity.class);
		}
		ScspCoreTokenDataEntity scspTokenData = scspCoreTokenDataRepository.findByPeticionIdAndTipoMensaje(
				scspPeticionRespuesta.getPeticionId(),
				0);
		if (scspTokenData == null) {
			throw new NotFoundException(
					scspPeticionRespuesta.getPeticionId() + ", 0",
					ScspCoreTokenDataEntity.class);
		}
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.getOne(scspPeticionRespuesta.getCertificado());
		if (scspCoreServicio == null) {
			throw new NotFoundException(
					scspPeticionRespuesta.getCertificado(),
					ScspCoreServicioEntity.class);
		}
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
		return peticion;
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
			// Les sol·licituds de les consultes asíncrones s'envien en paral·lel: no es modifica la petició
			BackofficeDesti desti = getBackofficeDesti(servei, peticion, backofficeSolicitud == null ? backofficePeticio : null);
			if (backofficeSolicitud != null && desti != null && desti.getEntitatCodi() != null) {
				backofficeSolicitud.updateEntitatCodi(desti.getEntitatCodi());
			}
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
			if (backofficeSolicitud != null) {
				WsClientHelper.configurarTimeout(emiservBackoffice, configuracioAsincronHelper.getTimeoutSegons());
			}
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
			// Les peticions de les consultes asíncrones (amb sol·licitud) també es fan als emisors dels
			// cedents (rutes de tipus EMISERV). Les tasques es creen per ordre de les rutes.
			List<Callable<RespostaBackofficeEntitat>> tasques = new ArrayList<Callable<RespostaBackofficeEntitat>>();
			List<PeticioEmisorCedent> peticionsCedents = backofficeSolicitud != null ?
					prepararPeticionsEmisorsCedents(backofficePeticio, backofficeSolicitud, servei) :
					Collections.<PeticioEmisorCedent>emptyList();
			List<BackofficeDesti> destins = backofficeSolicitud != null ?
					backofficeDestiHelper.getDestinsEnrutadorMultipleOrdenats(servei) :
					backofficeDestiHelper.getDestinsEnrutadorMultiple(servei);
			if (generarLogs) {
				log.debug("Enviant petició síncrona als backoffices de l'enrutador múltiple " +
						getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + "(" +
						"backofficeDestins=" + destins + ")");
			}
			modificarDatosEspecificosTransmisionPeticio(peticion);
			for (BackofficeDesti desti: destins) {
				PeticioEmisorCedent peticioCedent = null;
				for (PeticioEmisorCedent p: peticionsCedents) {
					if (p.getEntitatCodi().equals(desti.getEntitatCodi()) && desti.getUrl() == null) {
						peticioCedent = p;
					}
				}
				if (peticioCedent != null) {
					PeticioEmisorCedent peticioFil = peticioCedent;
					tasques.add(() -> enviarPeticioSincronaEmisorCedent(peticioFil));
				} else if (desti.getUrl() != null) {
					// Cada fil fa servir la seva còpia de la petició (els DOM no són segurs entre fils)
					Peticion copia = copiarPeticio(peticion);
					tasques.add(() -> enviarPeticioSincronaBackoffice(copia, desti, generarLogs));
				}
			}
			if (tasques.isEmpty()) {
				throw new ValidationException("El servei enrutador múltiple no té cap ruta (codi=" + servei.getCodi() + ")");
			}
			List<RespostaBackofficeEntitat> respostes = new ArrayList<RespostaBackofficeEntitat>();
			ExecutorService executor = Executors.newFixedThreadPool(tasques.size());
			try {
				List<Future<RespostaBackofficeEntitat>> futures = new ArrayList<Future<RespostaBackofficeEntitat>>();
				for (Callable<RespostaBackofficeEntitat> tasca: tasques) {
					futures.add(executor.submit(tasca));
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
			if (backofficePeticio != null && backofficeSolicitud == null) {
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

	/**
	 * Ruta de tipus EMISERV (emisor SCSP del cedent) a la qual s'ha d'enviar la sol·licitud d'un
	 * enrutador simple, o null si s'ha d'enviar a un backoffice.
	 */
	private ServeiRutaDestiEntity getRutaEmisorCedent(
			ServeiEntity servei,
			BackofficeSolicitudEntity solicitud) {
		if (!ServeiTipusEnumDto.ENRUTADOR.equals(servei.getTipus()) || solicitud.getEntitatCodi() == null || isPropertyBackofficeMock()) {
			return null;
		}
		ServeiRutaDestiEntity ruta = serveiRutaDestiRepository.findPrimeraByServeiAndEntitatCodi(servei, solicitud.getEntitatCodi());
		return ruta != null && !ruta.isDestiBackoffice() ? ruta : null;
	}

	private RespuestaAmbException enviarSolicitudEmisorCedent(
			BackofficePeticioEntity peticio,
			BackofficeSolicitudEntity solicitud,
			ServeiEntity servei,
			ServeiRutaDestiEntity ruta) throws Exception {
		ScspCoreServicioEntity scspCoreServicio = scspCoreServicioRepository.getOne(peticio.getScspPeticionRespuesta().getCertificado());
		String idPeticionDesti = getIdPeticioDesti(solicitud);
		PeticioEmisorCedent peticioCedent = prepararPeticioEmisorCedent(
				peticio,
				solicitud,
				servei,
				ruta,
				idPeticionDesti,
				clientScspHelper.getCredencials(scspCoreServicio.getClaveFirma()));
		RespostaBackofficeEntitat resposta = enviarPeticioSincronaEmisorCedent(peticioCedent);
		Atributos atributos = new Atributos();
		atributos.setIdPeticion(idPeticionDesti);
		atributos.setCodigoCertificado(servei.getCodi());
		processarComunicacioBackoffice(
				peticio,
				solicitud,
				atributos,
				resposta.getRespuesta() != null ? resposta.getRespuesta().getAtributos() : null,
				resposta.getXmlPeticio(),
				resposta.getXmlResposta(),
				resposta.getException());
		return new RespuestaAmbException(
				resposta.getRespuesta(),
				resposta.getException());
	}

	/** Peticions als emisors dels cedents de les rutes de tipus EMISERV d'un enrutador múltiple. */
	private List<PeticioEmisorCedent> prepararPeticionsEmisorsCedents(
			BackofficePeticioEntity peticio,
			BackofficeSolicitudEntity solicitud,
			ServeiEntity servei) throws Exception {
		List<PeticioEmisorCedent> peticions = new ArrayList<PeticioEmisorCedent>();
		ScspCoreServicioEntity scspCoreServicio = null;
		ClientScspHelper.Credencials credencials = null;
		String idPeticionDesti = null;
		for (ServeiRutaDestiEntity ruta: serveiRutaDestiRepository.findByServeiOrderByOrdreAsc(servei)) {
			if (ruta.isDestiBackoffice()) {
				continue;
			}
			if (credencials == null) {
				scspCoreServicio = scspCoreServicioRepository.getOne(peticio.getScspPeticionRespuesta().getCertificado());
				credencials = clientScspHelper.getCredencials(scspCoreServicio.getClaveFirma());
				// El mateix IdPeticion per a tots els cedents (emisors diferents)
				idPeticionDesti = getIdPeticioDesti(solicitud);
			}
			peticions.add(prepararPeticioEmisorCedent(peticio, solicitud, servei, ruta, idPeticionDesti, credencials));
		}
		return peticions;
	}

	/**
	 * Petició SCSP d'una sola sol·licitud per a l'emisor del cedent: les dades de la sol·licitud
	 * original (requeridor, titular, dades específiques) amb un IdPeticion propi d'EMISERV. L'emisor
	 * es pot substituir per ruta (propietats emisor.nif i emisor.nom del backoffice de la ruta).
	 */
	PeticioEmisorCedent prepararPeticioEmisorCedent(
			BackofficePeticioEntity peticio,
			BackofficeSolicitudEntity solicitud,
			ServeiEntity servei,
			ServeiRutaDestiEntity ruta,
			String idPeticionDesti,
			ClientScspHelper.Credencials credencials) throws Exception {
		String idPeticion = peticio.getScspPeticionRespuesta().getPeticionId();
		ScspCoreTokenDataEntity scspTokenData = scspCoreTokenDataRepository.findByPeticionIdAndTipoMensaje(idPeticion, 0);
		if (scspTokenData == null) {
			throw new NotFoundException(idPeticion + ", 0", ScspCoreTokenDataEntity.class);
		}
		Element solicitudOriginal = getElementSolicitudTransmision(scspTokenData.getDatos(), solicitud.getSolicitudId());
		if (solicitudOriginal == null) {
			throw new NotFoundException(idPeticion + ", " + solicitud.getSolicitudId(), SolicitudTransmision.class);
		}
		String ns = solicitudOriginal.getNamespaceURI();
		Document document = ClientScspHelper.nouDocument();
		Element peticion = document.createElementNS(ns, "Peticion");
		document.appendChild(peticion);
		Element atributos = document.createElementNS(ns, "Atributos");
		peticion.appendChild(atributos);
		afegirElement(atributos, ns, "IdPeticion", idPeticionDesti);
		afegirElement(atributos, ns, "NumElementos", "1");
		afegirElement(atributos, ns, "TimeStamp", crearTimestampScsp(new Date()));
		afegirElement(atributos, ns, "CodigoCertificado", servei.getCodi());
		Element solicitudes = document.createElementNS(ns, "Solicitudes");
		peticion.appendChild(solicitudes);
		Element solicitudTransmision = (Element)document.importNode(solicitudOriginal, true);
		solicitudes.appendChild(solicitudTransmision);
		BackofficeDesti desti = new BackofficeDesti(servei.getCodi(), ruta.getEntitatCodi(), ruta.getUrl(), ruta.getBackofficeAutenticacio());
		String emisorNif = backofficeDestiHelper.getPropietat(desti, "emisor.nif");
		String emisorNom = backofficeDestiHelper.getPropietat(desti, "emisor.nom");
		if (emisorNif != null || emisorNom != null) {
			Element emisor = primerDescendent(solicitudTransmision, "Emisor");
			if (emisor != null) {
				Element nif = primerDescendent(emisor, "NifEmisor");
				Element nom = primerDescendent(emisor, "NombreEmisor");
				if (emisorNif != null && nif != null) {
					nif.setTextContent(emisorNif);
				}
				if (emisorNom != null && nom != null) {
					nom.setTextContent(emisorNom);
				}
			}
		}
		String validarSignatura = backofficeDestiHelper.getPropietat(desti, "signatura.validar");
		String soapAction = backofficeDestiHelper.getPropietat(desti, "soapaction");
		return new PeticioEmisorCedent(
				ruta.getEntitatCodi(),
				ruta.getUrl(),
				soapAction != null ? soapAction : "peticionSincrona",
				peticion,
				idPeticionDesti,
				credencials,
				configuracioAsincronHelper.getTimeoutSegons(),
				validarSignatura == null || Boolean.parseBoolean(validarSignatura.trim()));
	}

	/** Enviament a l'emisor del cedent (es pot fer des d'un altre fil: no accedeix a la BBDD). */
	RespostaBackofficeEntitat enviarPeticioSincronaEmisorCedent(PeticioEmisorCedent peticio) {
		RespostaBackofficeEntitat resposta = new RespostaBackofficeEntitat(peticio.getEntitatCodi());
		try {
			ClientScspHelper.RespostaCedent respostaCedent = clientScspHelper.enviarPeticioSincrona(
					peticio.getUrl(),
					peticio.getSoapAction(),
					peticio.getPeticion(),
					peticio.getCredencials(),
					peticio.getTimeoutSegons(),
					peticio.isValidarSignatura());
			ClientScspHelper.escriure(resposta.getXmlPeticio(), respostaCedent.getXmlPeticio());
			// Es desa en el format del backoffice: la resposta final es construeix igual per a tots els destins
			Document formatBackoffice = RespostaScspConversor.toFormatBackoffice(respostaCedent.getRespuesta());
			ClientScspHelper.escriure(resposta.getXmlResposta(), ClientScspHelper.toXml(formatBackoffice));
			Respuesta respuesta = RespostaScspConversor.toRespuesta(formatBackoffice);
			Estado estado = respuesta.getAtributos() != null ? respuesta.getAtributos().getEstado() : null;
			String codigoEstado = estado != null ? estado.getCodigoEstado() : null;
			if (!"0003".equals(codigoEstado)) {
				throw new ScspCedentException(codigoEstado, "L'emisor del cedent ha retornat l'estat " + codigoEstado +
						(estado != null && estado.getLiteralError() != null ? ": " + estado.getLiteralError() : ""), null, null);
			}
			if (respuesta.getAtributos().getIdPeticion() != null && !peticio.getIdPeticion().equals(respuesta.getAtributos().getIdPeticion())) {
				throw new ScspCedentException(null, "La resposta de l'emisor del cedent és d'una altra petició (" +
						"enviada=" + peticio.getIdPeticion() + ", " +
						"rebuda=" + respuesta.getAtributos().getIdPeticion() + ")", null, null);
			}
			comprovarTransmisionsResposta(respuesta);
			emplenarCampsBuits(respuesta);
			resposta.setRespuesta(respuesta);
		} catch (Exception ex) {
			if (ex instanceof ScspCedentException) {
				ScspCedentException errorCedent = (ScspCedentException)ex;
				if (resposta.getXmlPeticio().size() == 0) {
					ClientScspHelper.escriure(resposta.getXmlPeticio(), errorCedent.getXmlPeticio());
				}
				if (resposta.getXmlResposta().size() == 0) {
					ClientScspHelper.escriure(resposta.getXmlResposta(), errorCedent.getXmlResposta());
				}
			}
			log.warn("Error al enviar la petició a l'emisor del cedent " + peticio.getEntitatCodi() + " (" +
					"url=" + peticio.getUrl() + ", " +
					"idPeticion=" + peticio.getIdPeticion() + "): " + ex.getMessage());
			resposta.setException(ex);
		}
		return resposta;
	}

	/**
	 * IdPeticion de les peticions als emisors dels cedents: prefix configurat i número de seqüència.
	 * Es desa a la sol·licitud (relació amb la petició original); cada intent en fa servir un de nou.
	 */
	private String getIdPeticioDesti(BackofficeSolicitudEntity solicitud) {
		String prefix = configuracioAsincronHelper.getPrefixIdPeticio();
		if (prefix.length() > 16) {
			prefix = prefix.substring(0, 16);
		}
		String sql = entityManager.getEntityManagerFactory().unwrap(SessionFactoryImplementor.class).
				getJdbcServices().getDialect().getSequenceNextValString("ems_async_idpet_seq");
		long sequencia = ((Number)entityManager.createNativeQuery(sql).getSingleResult()).longValue();
		String idPeticion = prefix + String.format("%0" + (26 - prefix.length()) + "d", sequencia);
		solicitud.updatePeticioDestiId(idPeticion);
		return idPeticion;
	}

	private static void afegirElement(Element pare, String ns, String nom, String valor) {
		Element element = pare.getOwnerDocument().createElementNS(ns, nom);
		element.setTextContent(valor);
		pare.appendChild(element);
	}

	private static Element primerDescendent(Element element, String nom) {
		NodeList nodes = element.getElementsByTagNameNS("*", nom);
		return nodes.getLength() > 0 ? (Element)nodes.item(0) : null;
	}

	/** Petició preparada per a l'emisor d'un cedent (sense dependències de la sessió de BBDD). */
	static class PeticioEmisorCedent {
		private final String entitatCodi;
		private final String url;
		private final String soapAction;
		private final Element peticion;
		private final String idPeticion;
		private final ClientScspHelper.Credencials credencials;
		private final int timeoutSegons;
		private final boolean validarSignatura;
		PeticioEmisorCedent(String entitatCodi, String url, String soapAction, Element peticion, String idPeticion,
				ClientScspHelper.Credencials credencials, int timeoutSegons, boolean validarSignatura) {
			this.entitatCodi = entitatCodi;
			this.url = url;
			this.soapAction = soapAction;
			this.peticion = peticion;
			this.idPeticion = idPeticion;
			this.credencials = credencials;
			this.timeoutSegons = timeoutSegons;
			this.validarSignatura = validarSignatura;
		}
		String getEntitatCodi() { return entitatCodi; }
		String getUrl() { return url; }
		String getSoapAction() { return soapAction; }
		Element getPeticion() { return peticion; }
		String getIdPeticion() { return idPeticion; }
		ClientScspHelper.Credencials getCredencials() { return credencials; }
		int getTimeoutSegons() { return timeoutSegons; }
		boolean isValidarSignatura() { return validarSignatura; }
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
				// La resposta està disponible quan s'han processat totes les sol·licituds: les que han
				// finalitzat amb error es retornen amb l'estat de l'error a les dades específiques
				boolean respostaDisponible = numSolicituds == backofficePeticio.getProcessadesTotal();
				if (respostaDisponible) {
					Transmisiones transmisiones = new Transmisiones();
					transmisiones.setTransmisionDatos(new ArrayList<TransmisionDatos>());
					List<BackofficeSolicitudEntity> solicituds = backofficeSolicitudRepository.findByPeticioOrderByIdAsc(backofficePeticio);
					for (BackofficeSolicitudEntity solicitud: solicituds) {
						if (BackofficeSolicitudEstatEnumDto.TRAMITADA.equals(solicitud.getEstat())) {
							transmisiones.getTransmisionDatos().add(
									solicitudToTransmisionDatos(
											solicitud,
											solicitudRespuesta.getAtributos().getCodigoCertificado()));
						} else {
							transmisiones.getTransmisionDatos().add(
									solicitudErrorToTransmisionDatos(
											backofficePeticio,
											solicitud,
											solicitudRespuesta.getAtributos().getCodigoCertificado()));
						}
					}
					respuesta.setTransmisiones(transmisiones);
					// Les transmissions de les sol·licituds amb error no tenen IdTransmision del destí
					emplenarCampsBuits(respuesta);
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
			// Les sol·licituds de les consultes asíncrones tenen la seva comunicació (s'envien en paral·lel)
			BackofficeComunicacioEntity comunicacio = (solicitud != null) ? solicitud.getComunicacioDarrera() : peticio.getComunicacioDarrera();
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
			if (solicitud != null) {
				// La petició s'actualitza a actualitzarEstatPeticio (amb la fila bloquejada)
				solicitud.updateComunicacioDarrera(
						comunicacio);
			} else {
				peticio.updateComunicacioDarrera(
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

	/**
	 * Transmissió d'una sol·licitud finalitzada amb error: dades genèriques de la petició i l'estat de
	 * l'error a les dades específiques (DatosEspecificos/Estado), d'on els requeridors l'obtenen amb
	 * els XPath d'error configurats per servei.
	 */
	TransmisionDatos solicitudErrorToTransmisionDatos(
			BackofficePeticioEntity peticio,
			BackofficeSolicitudEntity solicitud,
			String serveiCodi) throws Exception {
		TransmisionDatos transmisionDatos = new TransmisionDatos();
		DatosGenericos datosGenericos;
		try {
			datosGenericos = crearPeticioSolicitud(peticio, solicitud).getSolicitudes().getSolicitudTransmision().get(0).getDatosGenericos();
		} catch (Exception ex) {
			log.warn("No s'han pogut obtenir les dades genèriques de la sol·licitud (idSolicitud=" + solicitud.getSolicitudId() + "): " + ex.getMessage());
			datosGenericos = new DatosGenericos();
			Transmision transmision = new Transmision();
			transmision.setCodigoCertificado(serveiCodi);
			transmision.setIdSolicitud(solicitud.getSolicitudId());
			datosGenericos.setTransmision(transmision);
		}
		transmisionDatos.setDatosGenericos(datosGenericos);
		String errorCodi = solicitud.getErrorCodi() != null ? solicitud.getErrorCodi() : CODI_ERROR_DESTI;
		String literal = solicitud.getDarrerError() != null ? solicitud.getDarrerError() : "Error al obtener la respuesta del servicio";
		String datosEspecificosXml = "<DatosEspecificos><Estado>" +
				"<CodigoEstado>" + escapeXml(errorCodi) + "</CodigoEstado>" +
				"<LiteralError>" + escapeXml(literal.length() > 255 ? literal.substring(0, 255) : literal) + "</LiteralError>" +
				"</Estado></DatosEspecificos>";
		transmisionDatos.setDatosEspecificos(
				xmlHelper.bytesToDocument(modificarDatosEspecificosResposta(datosEspecificosXml, serveiCodi).getBytes(StandardCharsets.UTF_8)).getDocumentElement());
		return transmisionDatos;
	}

	private static String escapeXml(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
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
