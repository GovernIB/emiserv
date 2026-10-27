/**
 * 
 */
package es.caib.emiserv.back.command;

import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaFiltreDto;
import es.caib.emiserv.logic.intf.dto.ConsultaAsincronaFiltreEstatEnumDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Command per al filtre de les consultes asíncrones.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ConsultaAsincronaFiltreCommand {

	/** Per defecte es mostren les consultes que s'estan processant. */
	private ConsultaAsincronaFiltreEstatEnumDto estat = ConsultaAsincronaFiltreEstatEnumDto.EN_CURS;
	private Long servei;
	private String peticioId;

	public static ConsultaAsincronaFiltreDto toDto(ConsultaAsincronaFiltreCommand command) {
		ConsultaAsincronaFiltreDto dto = new ConsultaAsincronaFiltreDto();
		dto.setEstat(command.getEstat());
		dto.setServei(command.getServei());
		dto.setPeticioId(command.getPeticioId());
		return dto;
	}

}
