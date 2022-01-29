/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'una petició per auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class AuditoriaPeticioDto implements Serializable {

	private Long id;
	private String peticioId;
	private String serveiCodi;
	private String serveiDescripcio;
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

	// Alerta --> Aquests camps poden ser múltiple en cas de peticions múltiples
	private String procedimentCodi;
	private String procedimentNom;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
