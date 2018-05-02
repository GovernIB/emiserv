/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import java.io.Serializable;

/**
 * Tipus de fitxer XSD.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum XsdTipusEnumDto implements Serializable {
	PETICIO,
	RESPOSTA,
	DATOS_ESPECIFICOS,
	CONFIRMACIO_PETICIO,
	SOLICITUD_RESPOSTA
}

