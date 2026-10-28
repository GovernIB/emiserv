package es.caib.emiserv.logic.intf.dto;

/**
 * Motius pels quals un servei no està completament configurat. L'ordre dels valors és l'ordre
 * en què es retornen els motius d'un servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ServeiDiagnosticEnumDto {
	/** No s'ha desat la configuració SCSP del servei (no existeix CORE_SERVICIO). */
	FALTA_CONFIG_SCSP,
	/** Hi ha configuració SCSP però sense emissor. */
	FALTA_EMISOR,
	/** Enrutador sense cap ruta. */
	ENRUTADOR_SENSE_RUTES,
	/** Ruta de tipus BACKOFFICE amb autenticació les credencials de la qual no estan configurades. */
	RUTA_BACKOFFICE_SENSE_CREDENCIALS
}
