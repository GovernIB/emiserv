/**
 * 
 */
package es.caib.emiserv.core.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.caib.emiserv.core.api.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.core.api.service.ws.backoffice.DatosGenericos;
import es.caib.emiserv.core.api.service.ws.backoffice.EmiservBackoffice;
import es.caib.emiserv.core.api.service.ws.backoffice.Estado;
import es.caib.emiserv.core.api.service.ws.backoffice.Peticion;
import es.caib.emiserv.core.api.service.ws.backoffice.Respuesta;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudRespuesta;
import es.caib.emiserv.core.api.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.core.api.service.ws.backoffice.TransmisionDatos;
import es.caib.emiserv.core.api.service.ws.backoffice.Transmisiones;

/**
 * Backoffice EMISERV per als tests.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EmiservBackofficeTest  implements EmiservBackoffice {

	@Override
	public Respuesta peticionSincrona(Peticion peticion) {
		Respuesta respuesta = new Respuesta();
		respuesta.setAtributos(peticion.getAtributos());
		respuesta.getAtributos().setTimeStamp(dataFormatScsp(new Date()));
		respuesta.getAtributos().setEstado(new Estado());
		respuesta.getAtributos().getEstado().setCodigoEstado("0003");
		respuesta.getAtributos().getEstado().setLiteralError("Peticion procesada correctamente.");
		ArrayList<TransmisionDatos> listaTransmisionDatos = new ArrayList<TransmisionDatos>();
		for (int i = 0; i < peticion.getSolicitudes().getSolicitudTransmision().size(); i++) {
			SolicitudTransmision solicitudTransmisionPeticion = (SolicitudTransmision)peticion.getSolicitudes().getSolicitudTransmision().get(i);
			DatosGenericos datosGenericosPeticion = solicitudTransmisionPeticion.getDatosGenericos();
			TransmisionDatos transmisionDatos = new TransmisionDatos();
			datosGenericosPeticion.getTransmision().setIdTransmision(
					generateSemilla() + i);
			transmisionDatos.setDatosGenericos(datosGenericosPeticion);
			/*transmisionDatos.setDatosEspecificos(
					generarDatosEspecificosRespuesta());*/
			listaTransmisionDatos.add(transmisionDatos);
		}
		Transmisiones transmisiones = new Transmisiones();
        transmisiones.setTransmisionDatos(listaTransmisionDatos);
        respuesta.setTransmisiones(transmisiones);
        return respuesta;
	}
	@Override
	public ConfirmacionPeticion peticionAsincrona(Peticion peticion) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Respuesta solicitarRespuesta(SolicitudRespuesta solicitudRespuesta) {
		// TODO Auto-generated method stub
		return null;
	}

	private String dataFormatScsp(Date data) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		return df.format(data);
	}

	public static String generateSemilla() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("ddMMHHss");
		String fechaString = formatoDeFecha.format(new Date());
		return "T" + fechaString;
	}

}
