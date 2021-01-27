/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class UsuariDto implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String idioma;

	private static final long serialVersionUID = -139254994389509932L;

}
