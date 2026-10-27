/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Consulta asíncrona processada com a peticions síncrones a cada destí (backoffice o cedent), amb
 * el progrés de l'enviament de les sol·licituds.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ConsultaAsincronaDto implements Serializable {

	private Long id;
	private String peticioId;
	private String serveiCodi;
	private String serveiNom;
	private Date dataPeticio;
	/** Temps estimat de resposta comunicat al requeridor. */
	private Date terData;
	private PeticioEstatEnumDto estat;
	private boolean pausada;
	private int total;
	private int tramitades;
	private int errors;
	private int enviant;
	private int pendents;
	/** Sol·licituds pendents amb un reintent programat. */
	private int reintents;
	/** Destins (cedents) pausats de les sol·licituds pendents. */
	private boolean destinsPausats;
	/** Data prevista de finalització (null si està pausada o finalitzada). */
	private Date dataFiPrevista;
	private String darrerError;

	public boolean isFinalitzada() {
		return estat != PeticioEstatEnumDto.PENDENT;
	}
	public int getProcessades() {
		return tramitades + errors;
	}
	public int getProgres() {
		return total > 0 ? (int)Math.floor(100.0 * getProcessades() / total) : 0;
	}
	/** La data prevista de finalització supera el temps estimat de resposta comunicat. */
	public boolean isRetardada() {
		return dataFiPrevista != null && terData != null && dataFiPrevista.after(terData);
	}

	private static final long serialVersionUID = -5130784561129850473L;

}
