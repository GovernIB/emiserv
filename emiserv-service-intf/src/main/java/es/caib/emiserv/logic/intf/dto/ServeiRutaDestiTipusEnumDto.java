/**
 *
 */
package es.caib.emiserv.logic.intf.dto;

/**
 * Tipus de destí d'una ruta d'un servei enrutador.
 *
 * EMISERV: la petició SCSP es reenvia sense modificar a l'emisor SCSP del cedent
 * (EMISERV o emisor SCSP + backoffice instal·lat a l'organisme).
 * BACKOFFICE: EMISERV fa d'emisor de la petició i consulta directament el backoffice
 * del cedent (servei web EmiservBackoffice).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ServeiRutaDestiTipusEnumDto {
	EMISERV,
	BACKOFFICE
}
