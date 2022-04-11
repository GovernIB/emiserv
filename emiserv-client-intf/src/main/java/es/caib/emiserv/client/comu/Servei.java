/**
 * 
 */
package es.caib.emiserv.client.comu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Informació d'un servei associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Servei {

	@EqualsAndHashCode.Include
	private String codi;
	@EqualsAndHashCode.Include
	private String nom;
	private String emisorNom;
	private String emisorNif;
//	private Integer usuarisAmbPermisos;
	private Long consultesOk;
	private Long consultesError;
	private ServeiTipus tipus;
	private TotalAcumulat consultesTotal;

}
