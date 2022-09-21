/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Informació d'una autoritat certificadora per a les autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoritatCertificacioDto implements Serializable {

	private Long id;
	private String codca;
	private String nombre;
	
	public String getFormatName() {
		return this.nombre + " (" + this.codca + ")";
	}
	
	private static final long serialVersionUID = -8620175604318725073L;

}
