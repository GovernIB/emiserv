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
 * Informació d'un departament associat a un informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Departament {

	@EqualsAndHashCode.Include
	private String codi;
	@EqualsAndHashCode.Include
	private String nom;
	private List<Procediment> procediments;
//	private List<Usuari> usuaris;
	private ConsultesOkError consultesBackoffice;
	private ConsultesOkError consultesEnrutador;
	private ConsultesOkError consultesTotal;
	private TotalAcumulat totalBackoffice;
	private TotalAcumulat totalEnrutador;
	
}
