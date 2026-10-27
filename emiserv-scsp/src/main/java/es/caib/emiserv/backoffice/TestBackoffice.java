/**
 * 
 */
package es.caib.emiserv.backoffice;

import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.respuesta.TransmisionDatos;
import es.scsp.bean.common.respuesta.Transmisiones;
import es.scsp.bean.common.solicitud.SolicitudRespuesta;
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
		log.info("Processant petició síncrona " + TestBackofficeHelper.getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		Respuesta respuesta = getRespuesta(peticion);
		log.info("Retornant resposta síncrona " + TestBackofficeHelper.getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		return respuesta;
	}

	public ConfirmacionPeticion NotificarAsincrono(
			Peticion peticion) throws ScspException {
		log.info("Processant petició asíncrona " + TestBackofficeHelper.getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		int tiempoRespuesta = 5;
		ConfirmacionPeticion respuesta = new ConfirmacionPeticion();
		respuesta.setAtributos(TestBackofficeHelper.toConfirmacion(peticion.getAtributos()));
		es.scsp.bean.common.confirmacion.Estado estado = new es.scsp.bean.common.confirmacion.Estado();
		estado.setCodigoEstado("0002");
		estado.setCodigoEstadoSecundario("");
		estado.setLiteralError("Peticion procesada correctamente.");
		estado.setTiempoEstimadoRespuesta(tiempoRespuesta);
		respuesta.getAtributos().setEstado(estado);
		return respuesta;
	}

	public Respuesta SolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) throws ScspException {
		log.info("Processant sol·licitud de resposta " + TestBackofficeHelper.getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
		Respuesta respuesta = new Respuesta();
		respuesta.setAtributos(TestBackofficeHelper.toRespuesta(solicitudRespuesta.getAtributos()));
		respuesta.getAtributos().setEstado(TestBackofficeHelper.estado("0002", null, null, 1));
		respuesta.getAtributos().setNumElementos(0);
		return respuesta;
	}



	private Respuesta getRespuesta(Peticion peticion) throws ScspException {
		Respuesta respuesta = new Respuesta();
		if (peticion.getSolicitudes() == null || peticion.getSolicitudes().getSolicitudTransmision() == null) {
			throw ScspException.getScspException(
					"0237",
					new String[] {"La petició ha de tenir alguna una sol·licitud"});
		}
		respuesta.setAtributos(TestBackofficeHelper.toRespuesta(peticion.getAtributos()));
		respuesta.getAtributos().setEstado(TestBackofficeHelper.estado("0003", null, "Peticion procesada correctamente.", null));
		Transmisiones transmisiones = new Transmisiones();
		for (int i = 0; i < peticion.getSolicitudes().getSolicitudTransmision().size(); i++) {
			SolicitudTransmision solicitudTransmisionPeticion = peticion.getSolicitudes().getSolicitudTransmision().get(i);
			TransmisionDatos transmisionDatos = new TransmisionDatos();
			transmisionDatos.setDatosGenericos(
					TestBackofficeHelper.toRespuesta(solicitudTransmisionPeticion.getDatosGenericos()));
			transmisionDatos.getDatosGenericos().getTransmision().setIdTransmision(TestBackofficeHelper.generateSemilla() + i);
			transmisionDatos.setDatosEspecificos(
					solicitudTransmisionPeticion.getDatosEspecificos());
					//generarDatosEspecificosRespuesta());
			transmisiones.getTransmisionDatos().add(transmisionDatos);
		}
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

}
