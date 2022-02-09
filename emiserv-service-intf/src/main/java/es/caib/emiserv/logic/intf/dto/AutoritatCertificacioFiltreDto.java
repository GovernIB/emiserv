/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filtre per als organismes SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class AutoritatCertificacioFiltreDto {

	private String nom;
	private String codi;

}
