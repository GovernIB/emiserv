/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Informació d'un organisme per a autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
public class OrganismeDto {

	private Long id;
	private String nom;
	private String cif;
	private Date dataAlta;
	private Date dataBaixa;

}
