/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

/**
 * Estat per a les sol·licituds enviades als backoffices.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum BackofficeSolicitudEstatEnumDto {
	PENDENT,
	TRAMITADA,
	ERROR,
	/** S'està enviant al destí (consultes asíncrones processades com a síncrones). Es desa com a ordinal: s'ha d'afegir al final. */
	ENVIANT
}
