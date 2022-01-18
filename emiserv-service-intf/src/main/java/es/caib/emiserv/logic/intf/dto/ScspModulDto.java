/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;

/**
 * Informació d'activació de mòduls SCSP
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspModulDto {

	private String nom;
	private String descripcio;
	private boolean actiuEntrada;
	private boolean actiuSortida;

}
