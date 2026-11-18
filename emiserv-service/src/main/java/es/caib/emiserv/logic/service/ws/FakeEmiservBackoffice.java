/**
 *
 */
package es.caib.emiserv.logic.service.ws;

import es.caib.emiserv.logic.helper.BackofficeHelper;
import es.caib.emiserv.logic.helper.ServeiFakeHelper;
import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.logic.intf.exception.BackofficeException;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.DatosGenericos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.EmiservBackoffice;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Estado;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Solicitudes;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Transmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Transmisiones;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.scsp.fake.core.FakeRequest;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Backoffice d'un servei en mode fake: respon les peticions amb dades simulades (plantilla del
 * servei o dades generades pel servidor SCSP fake) sense contactar cap backoffice.
 *
 * No té estat compartit: se'n crea una instància per a cada petició.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FakeEmiservBackoffice implements EmiservBackoffice {

	public static final String LITERAL_ERROR = ServeiFakeHelper.LITERAL_PREFIX + "Error simulat";

	private final ServeiEntity servei;
	private final ServeiFakeHelper serveiFakeHelper;

	public FakeEmiservBackoffice(
			ServeiEntity servei,
			ServeiFakeHelper serveiFakeHelper) {
		this.servei = servei;
		this.serveiFakeHelper = serveiFakeHelper;
	}

	@Override
	public Respuesta peticionSincrona(Peticion peticion) {
		if (ServeiFakeResultatEnumDto.ERROR.equals(servei.getFakeResultat())) {
			throw new BackofficeException(LITERAL_ERROR);
		}
		String data = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
		Respuesta respuesta = new Respuesta();
		Atributos atributos = new Atributos();
		Estado estado = new Estado();
		estado.setCodigoEstado("0003");
		estado.setLiteralError(serveiFakeHelper.literalEstat(servei));
		estado.setTiempoEstimadoRespuesta(Integer.valueOf(0));
		atributos.setEstado(estado);
		atributos.setTimeStamp(data);
		if (peticion.getAtributos() != null) {
			atributos.setIdPeticion(peticion.getAtributos().getIdPeticion());
			atributos.setCodigoCertificado(peticion.getAtributos().getCodigoCertificado());
		}
		respuesta.setAtributos(atributos);
		Transmisiones transmisiones = new Transmisiones();
		transmisiones.setTransmisionDatos(new ArrayList<TransmisionDatos>());
		if (peticion.getSolicitudes() != null && peticion.getSolicitudes().getSolicitudTransmision() != null) {
			for (SolicitudTransmision solicitud: peticion.getSolicitudes().getSolicitudTransmision()) {
				transmisiones.getTransmisionDatos().add(
						transmisionDatos(peticion, solicitud, data));
			}
		}
		respuesta.setTransmisiones(transmisiones);
		atributos.setNumElementos(String.valueOf(transmisiones.getTransmisionDatos().size()));
		return respuesta;
	}

	@Override
	public ConfirmacionPeticion peticionAsincrona(Peticion peticion) {
		// Els serveis fake processen les peticions asíncrones com a síncrones (no es reenvien mai)
		throw new BackofficeException(ServeiFakeHelper.LITERAL_PREFIX + "Les peticions asíncrones dels serveis fake es processen com a síncrones");
	}

	@Override
	public Respuesta solicitarRespuesta(SolicitudRespuesta solicitudRespuesta) {
		throw new BackofficeException(ServeiFakeHelper.LITERAL_PREFIX + "Les peticions asíncrones dels serveis fake es processen com a síncrones");
	}

	/**
	 * Transmissió de la resposta d'una sol·licitud: còpia de les dades genèriques (la petició no es
	 * modifica: als enrutadors múltiples es comparteixen entre fils) i dades específiques simulades
	 * a partir de les dades d'aquesta sol·licitud.
	 */
	private TransmisionDatos transmisionDatos(
			Peticion peticion,
			SolicitudTransmision solicitud,
			String data) {
		TransmisionDatos transmisionDatos = new TransmisionDatos();
		DatosGenericos datosGenericos = new DatosGenericos();
		Transmision transmision = new Transmision();
		String idSolicitud = null;
		DatosGenericos datosGenericosPeticio = solicitud.getDatosGenericos();
		if (datosGenericosPeticio != null) {
			datosGenericos.setEmisor(datosGenericosPeticio.getEmisor());
			datosGenericos.setSolicitante(datosGenericosPeticio.getSolicitante());
			datosGenericos.setTitular(datosGenericosPeticio.getTitular());
			if (datosGenericosPeticio.getTransmision() != null) {
				idSolicitud = datosGenericosPeticio.getTransmision().getIdSolicitud();
				transmision.setCodigoCertificado(datosGenericosPeticio.getTransmision().getCodigoCertificado());
			}
		}
		if (transmision.getCodigoCertificado() == null && peticion.getAtributos() != null) {
			transmision.setCodigoCertificado(peticion.getAtributos().getCodigoCertificado());
		}
		transmision.setIdSolicitud(idSolicitud);
		transmision.setIdTransmision(idSolicitud);
		transmision.setFechaGeneracion(data);
		datosGenericos.setTransmision(transmision);
		transmisionDatos.setDatosGenericos(datosGenericos);
		transmisionDatos.setDatosEspecificos(
				datosEspecificos(peticion, solicitud, idSolicitud));
		return transmisionDatos;
	}

	private Element datosEspecificos(
			Peticion peticion,
			SolicitudTransmision solicitud,
			String idSolicitud) {
		String xml;
		try {
			FakeRequest fakeRequest = FakeRequest.fromXml(
					BackofficeHelper.toXmlBackoffice(peticioSolicitud(peticion, solicitud)));
			xml = serveiFakeHelper.dadesEspecifiques(servei, fakeRequest, idSolicitud);
		} catch (Exception ex) {
			throw new BackofficeException(ServeiFakeHelper.LITERAL_PREFIX + "No s'han pogut generar les dades específiques: " + ex.getMessage(), ex);
		}
		try {
			return documentBuilderFactory().newDocumentBuilder().parse(
					new InputSource(new StringReader(xml))).getDocumentElement();
		} catch (Exception ex) {
			throw new BackofficeException(ServeiFakeHelper.LITERAL_PREFIX + "Les dades específiques simulades no són XML vàlid: " + ex.getMessage(), ex);
		}
	}

	/** Petició amb una sola sol·licitud: les dades simulades són les del titular d'aquesta sol·licitud. */
	private static Peticion peticioSolicitud(
			Peticion peticion,
			SolicitudTransmision solicitud) {
		Peticion peticioSolicitud = new Peticion();
		peticioSolicitud.setAtributos(peticion.getAtributos());
		Solicitudes solicitudes = new Solicitudes();
		solicitudes.setSolicitudTransmision(new ArrayList<SolicitudTransmision>(Collections.singletonList(solicitud)));
		peticioSolicitud.setSolicitudes(solicitudes);
		return peticioSolicitud;
	}

	/** Parser reforçat (sense DOCTYPE ni entitats externes): la plantilla la introdueix l'usuari. */
	private static DocumentBuilderFactory documentBuilderFactory() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);
		return factory;
	}

}
