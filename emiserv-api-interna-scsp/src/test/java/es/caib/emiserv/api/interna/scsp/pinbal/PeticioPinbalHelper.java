package es.caib.emiserv.api.interna.scsp.pinbal;

import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Atributos;
import es.scsp.bean.common.peticion.Consentimiento;
import es.scsp.bean.common.peticion.DatosGenericos;
import es.scsp.bean.common.peticion.Emisor;
import es.scsp.bean.common.peticion.Funcionario;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.Procedimiento;
import es.scsp.bean.common.peticion.Solicitante;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.peticion.Solicitudes;
import es.scsp.bean.common.peticion.TipoDocumentacion;
import es.scsp.bean.common.peticion.Titular;
import es.scsp.bean.common.peticion.Transmision;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.solicitud.SolicitudRespuesta;
import es.scsp.common.utils.DateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Construeix les peticions SCSP de la mateixa manera que PINBAL
 * (es.caib.pinbal.scsp.ScspHelper.crearPeticion) i les converteix a XML amb JAXB
 * com fa el ClienteUnico de les llibreries SCSP 5 abans d'enviar-les.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PeticioPinbalHelper {

	public static final String NAMESPACE_DATOS_ESPECIFICOS = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";

	private static final AtomicInteger SEQUENCIA = new AtomicInteger();
	private static final JAXBContext JAXB_CONTEXT;
	static {
		try {
			JAXB_CONTEXT = JAXBContext.newInstance(
					Peticion.class,
					SolicitudRespuesta.class,
					Respuesta.class,
					ConfirmacionPeticion.class);
		} catch (JAXBException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private final ConfiguracioProva configuracio;

	public PeticioPinbalHelper(ConfiguracioProva configuracio) {
		this.configuracio = configuracio;
	}

	/** Genera un identificador de petició únic (màxim 26 caràcters, com els de PINBAL). */
	public String generarIdPeticio() {
		String id = configuracio.getIdPeticioPrefix() +
				new SimpleDateFormat("yyMMddHHmmss").format(new Date()) +
				String.format("%03d", SEQUENCIA.incrementAndGet() % 1000);
		return id.length() > 26 ? id.substring(id.length() - 26) : id;
	}

	/** Petició amb una única sol·licitud, igual que les consultes simples de PINBAL. */
	public Peticion crearPeticion(String idPeticion, String serveiCodi) throws Exception {
		return crearPeticion(idPeticion, serveiCodi, 1);
	}

	/** Petició amb múltiples sol·licituds, igual que les consultes múltiples de PINBAL. */
	public Peticion crearPeticion(String idPeticion, String serveiCodi, int numSolicituds) throws Exception {
		Peticion peticion = new Peticion();
		Solicitudes solicitudes = new Solicitudes();
		for (int index = 1; index <= numSolicituds; index++) {
			solicitudes.getSolicitudTransmision().add(
					crearSolicitudTransmision(idPeticion, serveiCodi, numSolicituds, index));
		}
		peticion.setSolicitudes(solicitudes);
		Atributos atributos = new Atributos();
		atributos.setCodigoCertificado(serveiCodi);
		atributos.setIdPeticion(idPeticion);
		atributos.setTimeStamp(DateUtils.parseISO8601(new Date()));
		atributos.setNumElementos(peticion.getSolicitudes().getSolicitudTransmision().size());
		peticion.setAtributos(atributos);
		return peticion;
	}

	/** Sol·licitud de resposta d'una petició asíncrona, igual que ClienteUnico.recuperaRespuesta. */
	public SolicitudRespuesta crearSolicitudRespuesta(String idPeticion, String serveiCodi, int numElementos) {
		SolicitudRespuesta solicitudRespuesta = new SolicitudRespuesta();
		es.scsp.bean.common.solicitud.Atributos atributos = new es.scsp.bean.common.solicitud.Atributos();
		atributos.setIdPeticion(idPeticion);
		atributos.setCodigoCertificado(serveiCodi);
		atributos.setNumElementos(numElementos);
		atributos.setTimeStamp(DateUtils.parseISO8601(new Date()));
		es.scsp.bean.common.solicitud.Solicitante solicitante = new es.scsp.bean.common.solicitud.Solicitante();
		solicitante.setIdentificadorSolicitante(configuracio.getSolicitantIdentificacio());
		solicitante.setNombreSolicitante(configuracio.getSolicitantNom());
		atributos.setSolicitante(solicitante);
		solicitudRespuesta.setAtributos(atributos);
		return solicitudRespuesta;
	}

	public static Element marshal(Object objecte) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document document = dbf.newDocumentBuilder().newDocument();
		JAXB_CONTEXT.createMarshaller().marshal(objecte, new DOMResult(document));
		return document.getDocumentElement();
	}

	public static <T> T unmarshal(Element element, Class<T> classe) throws JAXBException {
		return JAXB_CONTEXT.createUnmarshaller().unmarshal(element, classe).getValue();
	}

	private SolicitudTransmision crearSolicitudTransmision(
			String idPeticion,
			String serveiCodi,
			int numSolicitudes,
			int index) throws Exception {
		SolicitudTransmision st = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Emisor emisor = new Emisor();
		emisor.setNifEmisor(configuracio.getEmisorNif());
		emisor.setNombreEmisor(configuracio.getEmisorNom());
		datosGenericos.setEmisor(emisor);
		Solicitante solicitante = new Solicitante();
		datosGenericos.setSolicitante(solicitante);
		solicitante.setIdentificadorSolicitante(configuracio.getSolicitantIdentificacio());
		solicitante.setNombreSolicitante(configuracio.getSolicitantNom());
		solicitante.setFinalidad(configuracio.getFinalitat() != null ? configuracio.getFinalitat() : configuracio.getProcedimentCodi());
		if ("Si".equalsIgnoreCase(configuracio.getConsentiment())) {
			solicitante.setConsentimiento(Consentimiento.SI);
		} else {
			solicitante.setConsentimiento(Consentimiento.LEY);
		}
		Funcionario funcionario = new Funcionario();
		funcionario.setNombreCompletoFuncionario(configuracio.getFuncionariNom());
		funcionario.setNifFuncionario(configuracio.getFuncionariNif());
		solicitante.setFuncionario(funcionario);
		Procedimiento procedimiento = new Procedimiento();
		procedimiento.setCodProcedimiento(configuracio.getProcedimentCodi());
		procedimiento.setNombreProcedimiento(configuracio.getProcedimentNom());
		solicitante.setProcedimiento(procedimiento);
		solicitante.setUnidadTramitadora(configuracio.getUnitatTramitadora());
		solicitante.setCodigoUnidadTramitadora(configuracio.getUnitatTramitadoraCodi());
		Titular titular = new Titular();
		titular.setTipoDocumentacion(TipoDocumentacion.fromValue(configuracio.getTitularDocumentTipus()));
		titular.setDocumentacion(configuracio.getTitularDocument());
		titular.setNombre(configuracio.getTitularNom());
		titular.setApellido1(configuracio.getTitularLlinatge1());
		titular.setApellido2(configuracio.getTitularLlinatge2());
		datosGenericos.setTitular(titular);
		Transmision transmision = new Transmision();
		transmision.setCodigoCertificado(serveiCodi);
		transmision.setFechaGeneracion(DateUtils.parseISO8601(new Date()));
		// Igual que PINBAL: si només hi ha una sol·licitud l'id de sol·licitud és el mateix que el de la petició
		transmision.setIdSolicitud(numSolicitudes == 1 ? idPeticion : String.format("%06d", index));
		datosGenericos.setTransmision(transmision);
		st.setDatosGenericos(datosGenericos);
		Element datosEspecificos = llegirDadesEspecifiques();
		if (datosEspecificos != null) {
			st.setDatosEspecificos(datosEspecificos);
		}
		return st;
	}

	private Element llegirDadesEspecifiques() throws Exception {
		if (configuracio.getDadesEspecifiquesPath() == null) {
			return null;
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try (InputStream is = Files.newInputStream(configuracio.getDadesEspecifiquesPath())) {
			Element element = dbf.newDocumentBuilder().parse(is).getDocumentElement();
			// ClienteUnico (SCSP 5) rebutja les dades específiques sense cap node amb el namespace de dades específiques
			if (!NAMESPACE_DATOS_ESPECIFICOS.equals(element.getNamespaceURI()) &&
					element.getElementsByTagNameNS(NAMESPACE_DATOS_ESPECIFICOS, "*").getLength() == 0) {
				throw new IllegalStateException("Les dades específiques han de tenir el namespace " + NAMESPACE_DATOS_ESPECIFICOS);
			}
			return element;
		}
	}

}
