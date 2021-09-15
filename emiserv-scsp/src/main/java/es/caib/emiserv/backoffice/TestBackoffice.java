/**
 * 
 */
package es.caib.emiserv.backoffice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import lombok.extern.slf4j.Slf4j;

/**
 * Backoffice d'exemple que construeix la resposta copiant la informació
 * i els DatosEspecificos de la petició.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class TestBackoffice implements BackOffice {

	@SuppressWarnings("unused")
	private static final String XMLNS_V2 = "http://www.map.es/scsp/esquemas/datosespecificos";
	@SuppressWarnings("unused")
	private static final String XMLNS_V3 = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";

	public Respuesta NotificarSincrono(
			Peticion peticion) throws ScspException {
		log.info("Processant petició síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		Respuesta respuesta = getRespuesta(peticion);
		log.info("Retornant resposta síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		return respuesta;
	}

	public ConfirmacionPeticion NotificarAsincrono(
			Peticion peticion) throws ScspException {
		log.info("Processant petició asíncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
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
		log.info("Processant sol·licitud de resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
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
					solicitudTransmisionPeticion.getDatosEspecificos());
					//generarDatosEspecificosRespuesta());
			listaTransmisionDatos.add(transmisionDatos);
		}
		Transmisiones transmisiones = new Transmisiones();
        transmisiones.setTransmisionDatos(listaTransmisionDatos);
        respuesta.setTransmisiones(transmisiones);
        return respuesta;
	}

	/*private Element generarDatosEspecificosRespuesta() {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc;
		try {
			doc = fac.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Error al crear el documento.", e);
		}
		Element datosEspecificos = doc.createElement("DatosEspecificos");
		datosEspecificos.setAttribute("xmlns", XMLNS_V2);
		Element solicitud = doc.createElement("Solicitud");
		Element tutor = doc.createElement("Tutor");
		tutor.setTextContent("SI");
		solicitud.appendChild(tutor);
		Element provincia = doc.createElement("Provincia");
		Element provinciaNom = doc.createElement("Nombre");
		provinciaNom.setTextContent("Illes Balears");
		provincia.appendChild(provinciaNom);
		Element provinciaCodi = doc.createElement("Codigo");
		provinciaCodi.setTextContent("07");
		provincia.appendChild(provinciaCodi);
		solicitud.appendChild(provincia);
		Element municipi = doc.createElement("Municipio");
		Element municipiNom = doc.createElement("Nombre");
		municipiNom.setTextContent("Manacor");
		municipi.appendChild(municipiNom);
		Element municipiCodi = doc.createElement("Codigo");
		municipiCodi.setTextContent("033");
		municipi.appendChild(municipiCodi);
		solicitud.appendChild(municipi);
		datosEspecificos.appendChild(solicitud);
		return datosEspecificos;
	}*/

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

}
