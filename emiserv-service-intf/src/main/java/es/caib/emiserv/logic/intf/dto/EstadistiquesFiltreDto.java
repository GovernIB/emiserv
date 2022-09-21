package es.caib.emiserv.logic.intf.dto;

import es.caib.emiserv.client.comu.EstatTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Objecte DTO amb informació per filtrar les estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstadistiquesFiltreDto implements Serializable {

	private String entitatNif;
	private String procedimentCodi;
	private String serveiCodi;
	private EstatTipus estat;
	private Date dataInici;
	private Date dataFi;

	public boolean isEstatPendent() {
		return EstatTipus.PENDENT.equals(estat);
	}
	public boolean isEstatProcessant() {
		return EstatTipus.PROCESSANT.equals(estat);
	}
	public boolean isEstatTramitada() {
		return EstatTipus.TRAMITADA.equals(estat);
	}
	public boolean isEstatError() {
		return EstatTipus.ERROR.equals(estat);
	}

	private static final long serialVersionUID = -2822106398117415005L;

}
