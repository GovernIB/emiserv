/**
 * 
 */
package es.caib.emiserv.logic.service.ws;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Service;

import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Atributos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.ConfirmacionPeticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.EmiservBackoffice;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Estado;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Peticion;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Respuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudRespuesta;
import es.caib.emiserv.logic.intf.service.ws.backoffice.SolicitudTransmision;
import es.caib.emiserv.logic.intf.service.ws.backoffice.TransmisionDatos;
import es.caib.emiserv.logic.intf.service.ws.backoffice.Transmisiones;

/**
 * Backoffice que retorna sempre la mateixa resposta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
/*@WebService(
		name = "EmiservBackoffice",
		serviceName = "EmiservBackofficeService",
		portName = "EmiservBackofficePort",
		targetNamespace = "http://caib.es/emiserv/backoffice")*/
public class EmiservBackofficeImpl implements EmiservBackoffice {

	private ConversioTipusHelper conversioTipusHelper;

	public EmiservBackofficeImpl() {
		this.conversioTipusHelper = new ConversioTipusHelper();
	}

	@Override
	public Respuesta peticionSincrona(Peticion peticion) {
		Respuesta respuesta = new Respuesta();
		if (peticion.getAtributos() != null) {
			Atributos atributos = conversioTipusHelper.convertir(
					peticion.getAtributos(),
					Atributos.class);
			Estado estado = new Estado();
			estado.setCodigoEstado("0003");
			estado.setCodigoEstadoSecundario("2");
			estado.setLiteralError("Peticion procesada correctamente.");
			estado.setTiempoEstimadoRespuesta(Integer.valueOf(0));
			atributos.setEstado(estado);
			respuesta.setAtributos(atributos);
		}
		if (peticion.getSolicitudes() != null) {
			Transmisiones transmisiones = new Transmisiones();
			if (peticion.getSolicitudes().getSolicitudTransmision() != null) {
				int indexTransmissio = 0;
				for (SolicitudTransmision solicitudTransmision: peticion.getSolicitudes().getSolicitudTransmision()) {
					Object datosEspecificos = solicitudTransmision.getDatosEspecificos();
					solicitudTransmision.setDatosEspecificos(null);
					TransmisionDatos transmisionDatos = conversioTipusHelper.convertir(
							solicitudTransmision,
							TransmisionDatos.class);
					solicitudTransmision.setDatosEspecificos(datosEspecificos);
					transmisionDatos.getDatosGenericos().getTransmision().setIdTransmision(
							generarTimestampTransmissio() + indexTransmissio++);
					try {
						transmisionDatos.setDatosEspecificos(
								transmisionDatos.getDatosEspecificos());
								//generarDatosEspecificosRespuesta());
					} catch (Exception ex) {
						
					}
					transmisiones.setTransmisionDatos(new ArrayList<TransmisionDatos>());
					transmisiones.getTransmisionDatos().add(transmisionDatos);
				}
			}
			respuesta.setTransmisiones(transmisiones);
		}
		return respuesta;
	}

	@Override
	public ConfirmacionPeticion peticionAsincrona(
			Peticion peticion) {
		ConfirmacionPeticion confirmacionPeticion = conversioTipusHelper.convertir(
				peticion,
				ConfirmacionPeticion.class);
		return confirmacionPeticion;
	}

	@Override
	public Respuesta solicitarRespuesta(
			SolicitudRespuesta solicitudRespuesta) {
		Respuesta respuesta = conversioTipusHelper.convertir(
				solicitudRespuesta,
				Respuesta.class);
		return respuesta;
	}

	private String generarTimestampTransmissio() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("ddMMHHss");
		String fechaString = formatoDeFecha.format(new Date());
		return "T" + fechaString;
	}

}
