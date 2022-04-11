/**
 * 
 */
package es.caib.emiserv.client.comu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Informació d'una entitat associada a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Entitat {

//	private String codi;
	@EqualsAndHashCode.Include
	private String nom;
	@EqualsAndHashCode.Include
	private String nif;
	private List<Departament> departaments;
	private ConsultesOkError consultesBackoffice;
	private ConsultesOkError consultesEnrutador;
	private ConsultesOkError consultesTotal;
	private TotalAcumulat totalBackoffice;
	private TotalAcumulat totalEnrutador;

}
