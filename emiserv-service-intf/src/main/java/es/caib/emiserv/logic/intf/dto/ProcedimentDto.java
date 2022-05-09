/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class ProcedimentDto extends ObjecteAmbPermisosDto {

	private String codi;
	private String nom;

	public String getCodiNom() {
		if ((codi == null || codi.isBlank()) && (nom == null || nom.isBlank()))
			return null;
		if (codi == null || codi.isBlank())
			return nom;
		if ((nom == null || nom.isBlank()))
			return codi;
		return this.codi + " - " + this.nom;
	}
	private static final long serialVersionUID = -139254994389509932L;

}
