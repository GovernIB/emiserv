/**
 * 
 */
package es.caib.emiserv.client.comu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació d'un usuari associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuari {

	private String codi;
	private String nom;
	private String nif;
	
}
