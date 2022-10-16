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
public class ServeiFiltreDto {

	private String codi;
	private String nom;
	private ServeiTipusEnumDto tipus;
	private Boolean actiu;

}
