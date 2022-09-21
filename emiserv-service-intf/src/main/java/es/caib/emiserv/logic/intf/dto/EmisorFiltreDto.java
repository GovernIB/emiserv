/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació del filtre d'autoritzacions d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class EmisorFiltreDto {

	private String nom;
	private String cif;

}
