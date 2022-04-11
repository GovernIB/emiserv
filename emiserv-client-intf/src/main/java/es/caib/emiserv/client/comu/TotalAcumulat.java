/**
 * 
 */
package es.caib.emiserv.client.comu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Informació del total acumulat de peticions en una data determinada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TotalAcumulat {

	private long any;
	private long mes;
	private long dia;
	private long hora;
	private long minut;

	public void incrementarAcumulat(TotalAcumulat increment) {
		if (increment != null) {
			this.any += increment.getAny();
			this.mes += increment.getMes();
			this.dia += increment.getDia();
			this.hora += increment.getHora();
			this.minut += increment.getMinut();
		}
	}
	
}
