package es.caib.emiserv.logic.intf.dto;

/**
 * Resultat simulat de les peticions d'un servei en mode fake.
 */
public enum ServeiFakeResultatEnumDto {
	/** Resposta correcta amb dades. */
	OK,
	/** Resposta correcta sense dades (cap resultat trobat). */
	SENSE_DADES,
	/** Error SCSP simulat. */
	ERROR
}
