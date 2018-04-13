/**
 * 
 */
package es.caib.emiserv.backoffice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Estado;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.SolicitudRespuesta;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.TransmisionDatos;
import es.scsp.bean.common.Transmisiones;
import es.scsp.common.backoffice.BackOffice;
import es.scsp.common.exceptions.ScspException;

/**
 * Backoffice d'exemple que construeix la resposta copiant la informació
 * i els DatosEspecificos de la petició.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TestBackoffice2 implements BackOffice {

	@SuppressWarnings("unused")
	private static final String XMLNS_V2 = "http://www.map.es/scsp/esquemas/datosespecificos";
	@SuppressWarnings("unused")
	private static final String XMLNS_V3 = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";

	public Respuesta NotificarSincrono(
			Peticion peticion) throws ScspException {
		LOGGER.info("Processant petició síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		Respuesta respuesta = getRespuesta(peticion);
		LOGGER.info("Retornant resposta síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		return respuesta;
	}

	public ConfirmacionPeticion NotificarAsincrono(
			Peticion peticion) throws ScspException {
		LOGGER.info("Processant petició asíncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		int tiempoRespuesta = 5;
		ConfirmacionPeticion respuesta = new ConfirmacionPeticion();
		respuesta.setAtributos(peticion.getAtributos());
		respuesta.getAtributos().setEstado(new Estado());
		respuesta.getAtributos().getEstado().setCodigoEstado("0002");
		respuesta.getAtributos().getEstado().setCodigoEstadoSecundario("");
		respuesta.getAtributos().getEstado().setLiteralError("Peticion procesada correctamente.");
		respuesta.getAtributos().getEstado().setTiempoEstimadoRespuesta(tiempoRespuesta);
		respuesta.getAtributos().setTimeStamp(peticion.getAtributos().getTimeStamp());
		return respuesta;
	}

	public Respuesta SolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) throws ScspException {
		LOGGER.info("Processant sol·licitud de resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
		Respuesta respuesta = new Respuesta();
		respuesta.setAtributos(solicitudRespuesta.getAtributos());
		respuesta.getAtributos().setEstado(new Estado());
		respuesta.getAtributos().getEstado().setCodigoEstado("0002");
		respuesta.getAtributos().getEstado().setCodigoEstadoSecundario(null);
		respuesta.getAtributos().getEstado().setLiteralError(null);
		respuesta.getAtributos().getEstado().setTiempoEstimadoRespuesta(1);
		respuesta.getAtributos().setTimeStamp(solicitudRespuesta.getAtributos().getTimeStamp());
		respuesta.getAtributos().setNumElementos("0");
		return respuesta;
	}



	private Respuesta getRespuesta(Peticion peticion) throws ScspException {
		Respuesta respuesta = new Respuesta();
		if (peticion.getSolicitudes() == null || peticion.getSolicitudes().getSolicitudTransmision() == null) {
			throw ScspException.getScspException(
					"0237",
					new String[] {"La petició ha de tenir alguna una sol·licitud"});
		}
		respuesta.setAtributos(peticion.getAtributos());
		respuesta.getAtributos().setEstado(new Estado());
		respuesta.getAtributos().getEstado().setCodigoEstado("0003");
		respuesta.getAtributos().getEstado().setLiteralError("Peticion procesada correctamente.");
		ArrayList<TransmisionDatos> listaTransmisionDatos = new ArrayList<TransmisionDatos>();
		for (int i = 0; i < peticion.getSolicitudes().getSolicitudTransmision().size(); i++) {
			SolicitudTransmision solicitudTransmisionPeticion = (SolicitudTransmision)peticion.getSolicitudes().getSolicitudTransmision().get(i);
			DatosGenericos datosGenericosPeticion = solicitudTransmisionPeticion.getDatosGenericos();
			TransmisionDatos transmisionDatos = new TransmisionDatos();
			datosGenericosPeticion.getTransmision().setIdTransmision(generateSemilla() + i);
			transmisionDatos.setDatosGenericos(datosGenericosPeticion);
			transmisionDatos.setDatosEspecificos(
					generarDatosEspecificosRespuesta());
			listaTransmisionDatos.add(transmisionDatos);
		}
		Transmisiones transmisiones = new Transmisiones();
        transmisiones.setTransmisionDatos(listaTransmisionDatos);
        respuesta.setTransmisiones(transmisiones);
        return respuesta;
	}

	private Element generarDatosEspecificosRespuesta() {
		/*
		 *	<ns1:Retorno>
		 *		<ns1:Estado>
		 *			<ns1:CodigoEstado>0233</ns1:CodigoEstado>
		 *			<ns1:LiteralError>Titular no identificado.</ns1:LiteralError>
		 *		</ns1:Estado>
		 *	</ns1:Retorno>
		 */
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc;
		try {
			doc = fac.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Error al crear el documento.", e);
		}
		Element datosEspecificos = doc.createElementNS(
				XMLNS_V3,
				"DatosEspecificos");
		Element estado = doc.createElementNS(
				XMLNS_V3,
				"Estado");
		Element codigoEstado = doc.createElementNS(
				XMLNS_V3,
				"CodigoEstado");
		codigoEstado.setTextContent("0233");
		estado.appendChild(codigoEstado);
		Element literalError = doc.createElementNS(
				XMLNS_V3,
				"LiteralError");
		literalError.setTextContent("Titular no identificado.");
		estado.appendChild(literalError);
		datosEspecificos.appendChild(estado);
		return datosEspecificos;
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

	public static String generateSemilla() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("ddMMHHss");
		String fechaString = formatoDeFecha.format(new Date());
		return "T" + fechaString;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(TestBackoffice2.class);

}
