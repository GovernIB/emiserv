/**
 * 
 */
package es.caib.emiserv.backoffice;

import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.emiserv.backoffice.util.ConversioTipusHelper;
import es.caib.emiserv.backoffice.util.SpringApplicationContext;
import es.caib.emiserv.core.api.service.BackofficeService;
import es.caib.emiserv.core.api.service.ws.backoffice.TransmisionDatos;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.SolicitudRespuesta;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.common.backoffice.BackOffice;
import es.scsp.common.exceptions.ScspException;

/**
 * Backoffice genèric per a accedir als backoffices emissors
 * desplegats a la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CaibBackoffice implements BackOffice {

	public Respuesta NotificarSincrono(
			Peticion peticion) throws ScspException {
		LOGGER.debug("Processant petició síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		try {
			es.caib.emiserv.core.api.service.ws.backoffice.Peticion peticionBackoffice = ConversioTipusHelper.convertir(
					peticion,
					es.caib.emiserv.core.api.service.ws.backoffice.Peticion.class);
			copiarDatosEspecificosPeticion(
					peticion,
					peticionBackoffice);
			es.caib.emiserv.core.api.service.ws.backoffice.Respuesta respuestaBackoffice = getBackofficeService().peticioBackofficeSincrona(
					peticionBackoffice);
			LOGGER.debug("Retornant resposta a petició síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
			Respuesta respuesta = ConversioTipusHelper.convertir(
					respuestaBackoffice,
					Respuesta.class);
			copiarDatosEspecificosRespuesta(
					respuestaBackoffice,
					respuesta);
			LOGGER.debug(
					"Contingut de la resposta que s'envia a les llibreries SCSP: " +
					ReflectionToStringBuilder.toString(
							respuesta,
							new RecursiveToStringStyle()) + ".");
			return respuesta;
		} catch (Exception ex) {
			LOGGER.error("Error al invocar el mètode peticionSincrona del backoffice per a la petició " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()), ex);
			throw ScspException.getScspException(
					ex,
					"0227",
					new String[] {ex.getCause().getMessage()});
		}
	}

	public ConfirmacionPeticion NotificarAsincrono(
			Peticion peticion) throws ScspException {
		LOGGER.debug("Processant petició asíncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		try {
			es.caib.emiserv.core.api.service.ws.backoffice.Peticion peticionBackoffice = ConversioTipusHelper.convertir(
					peticion,
					es.caib.emiserv.core.api.service.ws.backoffice.Peticion.class);
			copiarDatosEspecificosPeticion(
					peticion,
					peticionBackoffice);
			es.caib.emiserv.core.api.service.ws.backoffice.ConfirmacionPeticion confirmacionPeticionBackoffice = getBackofficeService().peticioBackofficeAsincrona(
					peticionBackoffice);
			LOGGER.debug("Retornant resposta a petició asíncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
			ConfirmacionPeticion confirmacionPeticion = ConversioTipusHelper.convertir(
					confirmacionPeticionBackoffice,
					ConfirmacionPeticion.class);
			LOGGER.debug(
					"Contingut de la resposta que s'envia a les llibreries SCSP: " +
					ReflectionToStringBuilder.toString(
							confirmacionPeticion,
							new RecursiveToStringStyle()) + ".");
			return confirmacionPeticion;
		} catch (Exception ex) {
			LOGGER.error("Error al invocar el mètode peticionAsincrona del backoffice per a la petició " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()), ex);
			throw ScspException.getScspException(
					ex,
					"0227",
					new String[] {ex.getCause().getMessage()});
		}
	}

	public Respuesta SolicitudRespuesta(
			SolicitudRespuesta solicitudRespuesta) throws ScspException {
		LOGGER.debug("Processant sol·licitud de resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
		try {
			es.caib.emiserv.core.api.service.ws.backoffice.SolicitudRespuesta solicitudRespuestaBackoffice = ConversioTipusHelper.convertir(
					solicitudRespuesta,
					es.caib.emiserv.core.api.service.ws.backoffice.SolicitudRespuesta.class);
			es.caib.emiserv.core.api.service.ws.backoffice.Respuesta respuestaBackoffice = getBackofficeService().peticioBackofficeSolicitudRespuesta(
					solicitudRespuestaBackoffice);
			LOGGER.debug("Retornant resposta a sol·licitud de resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
			Respuesta respuesta = ConversioTipusHelper.convertir(
					respuestaBackoffice,
					Respuesta.class);
			copiarDatosEspecificosRespuesta(
					respuestaBackoffice,
					respuesta);
			LOGGER.debug(
					"Contingut de la resposta que s'envia a les llibreries SCSP: " +
					ReflectionToStringBuilder.toString(
							respuesta,
							new RecursiveToStringStyle()) + ".");
			return respuesta;
		} catch (Exception ex) {
			LOGGER.error("Error al invocar el mètode solicitarRespuesta del backoffice per a la petició " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()), ex);
			throw ScspException.getScspException(
					ex,
					"0227",
					new String[] {ex.getCause().getMessage()});
		}
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

	private BackofficeService getBackofficeService() {
		return SpringApplicationContext.getBean(BackofficeService.class);
	}

	private void copiarDatosEspecificosPeticion(
			Peticion peticionOrigen,
			es.caib.emiserv.core.api.service.ws.backoffice.Peticion peticionDesti) {
		// Si no es copies d'aquesta forma els DatosEspecificos copiats amb orika
		// queden com a Objects en lloc de org.w3c.dom.Elements i després sorgeixen
		// errors al processar-los.
		if (peticionOrigen.getSolicitudes() != null && peticionOrigen.getSolicitudes().getSolicitudTransmision() != null) {
			List<SolicitudTransmision> solicitudesOrigen = peticionOrigen.getSolicitudes().getSolicitudTransmision();
			List<es.caib.emiserv.core.api.service.ws.backoffice.SolicitudTransmision> solicitudesDesti = peticionDesti.getSolicitudes().getSolicitudTransmision();
			for (int i = 0; i < solicitudesOrigen.size(); i++) {
				solicitudesDesti.get(i).setDatosEspecificos(
						solicitudesOrigen.get(i).getDatosEspecificos());
			}
		}
	}

	private void copiarDatosEspecificosRespuesta(
			es.caib.emiserv.core.api.service.ws.backoffice.Respuesta respuestaOrigen,
			Respuesta respuesta) {
		// Si no es copies d'aquesta forma els DatosEspecificos copiats amb orika
		// queden com a Objects en lloc de org.w3c.dom.Elements i després sorgeixen
		// errors al processar-los.
		if (respuestaOrigen.getTransmisiones() != null && respuestaOrigen.getTransmisiones().getTransmisionDatos() != null) {
			List<TransmisionDatos> transmisionesOrigen = respuestaOrigen.getTransmisiones().getTransmisionDatos();
			List<es.scsp.bean.common.TransmisionDatos> transmisionesDesti = respuesta.getTransmisiones().getTransmisionDatos();
			for (int i = 0; i < transmisionesOrigen.size(); i++) {
				transmisionesDesti.get(i).setDatosEspecificos(
						transmisionesOrigen.get(i).getDatosEspecificos());
			}
		}
	}

	@SuppressWarnings("serial")
	private static class RecursiveToStringStyle extends ToStringStyle {
		private static final int    INFINITE_DEPTH  = -1;
		private int maxDepth;
		private int depth;
		public RecursiveToStringStyle() {
			this(INFINITE_DEPTH);
		}
		public RecursiveToStringStyle(int maxDepth) {
			setUseShortClassName(true);
			setUseIdentityHashCode(false);
			this.maxDepth = maxDepth;
		}
		@Override
		protected void appendDetail(
				StringBuffer buffer,
				String fieldName,
				Object value) {
			if (value.getClass().getName().startsWith("java.lang.") || (maxDepth != INFINITE_DEPTH && depth >= maxDepth)) {
				buffer.append(value);
			} else {
				depth++;
				buffer.append(ReflectionToStringBuilder.toString(value, this));
				depth--;
			}
		}
		@Override
		protected void appendDetail(
				StringBuffer buffer,
				String fieldName,
				Object[] array) {
			depth++;
			buffer.append(ReflectionToStringBuilder.toString(
					array,
					this,
					true,
					true));
			depth--;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CaibBackoffice.class);

}
