/**
 * 
 */
package es.caib.emiserv.backoffice;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.emiserv.logic.intf.service.BackofficeService;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.SolicitudRespuesta;
import es.scsp.common.backoffice.BackOffice;
import es.scsp.common.exceptions.ScspException;
import es.scsp.emiserv.backoffice.BackofficeServiceEjbLocator;

/**
 * Backoffice genèric per a accedir als backoffices emissors
 * desplegats a la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CaibBackoffice implements BackOffice {

	public Respuesta NotificarSincrono(
			Peticion peticion) throws ScspException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processant petició síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		}
		try {
			Respuesta respuesta = getBackofficeService().peticioBackofficeSincrona(peticion);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Retornant resposta a petició síncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
				LOGGER.debug(
						"Contingut de la resposta que s'envia a les llibreries SCSP: " +
						ReflectionToStringBuilder.toString(
								respuesta,
								new RecursiveToStringStyle()) + ".");
			}
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processant petició asíncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
		}
		try {
			ConfirmacionPeticion confirmacionPeticion = getBackofficeService().peticioBackofficeAsincrona(peticion);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Retornant resposta a petició asíncrona " + getIdentificacioDelsAtributsScsp(peticion.getAtributos()) + ".");
				LOGGER.debug(
						"Contingut de la resposta que s'envia a les llibreries SCSP: " +
						ReflectionToStringBuilder.toString(
								confirmacionPeticion,
								new RecursiveToStringStyle()) + ".");
			}
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processant sol·licitud de resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
		}
		try {
			Respuesta respuesta = getBackofficeService().peticioBackofficeSolicitudRespuesta(solicitudRespuesta);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Retornant resposta a sol·licitud de resposta " + getIdentificacioDelsAtributsScsp(solicitudRespuesta.getAtributos()) + ".");
				LOGGER.debug(
						"Contingut de la resposta que s'envia a les llibreries SCSP: " +
						ReflectionToStringBuilder.toString(
								respuesta,
								new RecursiveToStringStyle()) + ".");
			}
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

	public BackofficeService getBackofficeService() {
		return BackofficeServiceEjbLocator.getBackofficeService();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CaibBackoffice.class);

}
