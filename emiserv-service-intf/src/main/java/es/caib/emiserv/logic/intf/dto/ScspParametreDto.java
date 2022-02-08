/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Informació d'activació de mòduls SCSP
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@AllArgsConstructor
@Data
public class ScspParametreDto {

	private String nombre;
	private String valor;
	private String descripcion;

}
