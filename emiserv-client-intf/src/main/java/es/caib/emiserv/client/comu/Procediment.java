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
 * Informació d'un procediment associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Procediment {

	@EqualsAndHashCode.Include
	private String codi;
	@EqualsAndHashCode.Include
	private String nom;
	private boolean actiu;
	private ConsultesOkError consultesBackoffice;
	private ConsultesOkError consultesEnrutador;
	private ConsultesOkError consultesTotal;
	private TotalAcumulat totalBackoffice;
	private TotalAcumulat totalEnrutador;
	private List<Servei> serveis;
	
}
