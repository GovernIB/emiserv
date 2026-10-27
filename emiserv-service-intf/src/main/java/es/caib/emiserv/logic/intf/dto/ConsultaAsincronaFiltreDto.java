/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Filtre de les consultes asíncrones processades com a síncrones.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ConsultaAsincronaFiltreDto implements Serializable {

	private ConsultaAsincronaFiltreEstatEnumDto estat;
	private Long servei;
	private String peticioId;

	private static final long serialVersionUID = 4528112640187823311L;

}
