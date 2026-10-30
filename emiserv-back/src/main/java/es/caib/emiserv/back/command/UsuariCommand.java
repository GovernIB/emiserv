/*
 * 
 */
package es.caib.emiserv.back.command;

import es.caib.emiserv.logic.intf.dto.InterficieUsuariEnumDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Command pel formulari de preferències d'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@AllArgsConstructor
@Getter
@Setter
public class UsuariCommand {

	private String codi;
	private String nom;
	private String email;
	private String idioma;
	private InterficieUsuariEnumDto interficie;

}
