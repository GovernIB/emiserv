/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'una petició per auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaPeticioDto implements Serializable {

	private Long id;
	private String peticioId;
	private String serveiCodi;
	private String serveiDescripcio;
	private ServeiTipusEnumDto serveiTipus;
	private PeticioEstatEnumDto estat;
	private String estatScsp;
	private Date dataPeticio;
	private Date dataResposta;
	private String error;
	private boolean sincrona;
	private Date ter;
	private int numEnviaments;
	private int numTransmissions;
	private Date dataDarreraComprovacio;
	private boolean comunicacioBackofficeDisponible;
	private boolean comunicacioBackofficeError;
	private int processadesTotal;
	private int processadesError;
	private String entitatCodi; // Entitat que ha retornat la resposta

	// Alerta --> Aquests camps poden ser múltiple en cas de peticions múltiples
	private String procedimentCodi;
	private String procedimentNom;
	private String procedimentCodiNom;

	public int getProcessadesPercent() {
		if (numTransmissions == 0)
			return 100;
		return processadesTotal * 100 / numTransmissions;
	}

	public boolean isEstatScspError() {
		if (estatScsp == null) {
			return false;
		}
		return !estatScsp.startsWith("00");
	}

	public String getServeiCodiNom() {
		if ((serveiCodi == null || serveiCodi.isBlank()) && (serveiDescripcio == null || serveiDescripcio.isBlank()))
			return null;
		if (serveiCodi == null || serveiCodi.isBlank())
			return serveiDescripcio;
		if ((serveiDescripcio == null || serveiDescripcio.isBlank()))
			return serveiCodi;
		return serveiCodi + " - " + serveiDescripcio;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
