/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Informació per a l'estadística de càrrega de consultes de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CarregaDto implements Serializable {

	private long count;
	private String entitatNom;
	@EqualsAndHashCode.Include
	private String entitatCif;
	private String departamentNom;
	@EqualsAndHashCode.Include
	private String procedimentCodi;
	private String procedimentNom;
	@EqualsAndHashCode.Include
	private String serveiCodi;
	private String serveiNom;
	private ServeiTipusEnumDto serveiTipus;
	private String emisor;
	private CarregaDetailedCountDto detailedCount;

	public CarregaDto(
			long count,
			String entitatNom,
			String entitatCif,
			String departamentNom,
			String procedimentCodi,
			String procedimentNom,
			String serveiCodi,
			String serveiNom,
			String emisor) {
		this(
				count,
				entitatNom,
				entitatCif,
				departamentNom,
				procedimentCodi,
				procedimentNom,
				serveiCodi,
				serveiNom,
				ServeiTipusEnumDto.BACKOFFICE,
				emisor);
	}

	public CarregaDto(
			long count,
			String entitatNom,
			String entitatCif,
			String departamentNom,
			String procedimentCodi,
			String procedimentNom,
			String serveiCodi,
			String serveiNom,
			ServeiTipusEnumDto serveiTipus,
			String emisor) {
		this.count = count;
		this.entitatNom = entitatNom;
		this.entitatCif = entitatCif;
		this.departamentNom = departamentNom;
		this.procedimentCodi = procedimentCodi;
		this.procedimentNom = procedimentNom;
		this.serveiCodi = serveiCodi;
		this.serveiNom = serveiNom;
		this.serveiTipus = serveiTipus;
		this.emisor = emisor;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CarregaDetailedCountDto {
		private long any;
		private long mes;
		private long dia;
		private long hora;
		private long minut;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
