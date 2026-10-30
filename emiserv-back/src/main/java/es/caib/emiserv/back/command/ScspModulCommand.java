/**
 * 
 */
package es.caib.emiserv.back.command;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.intf.dto.ScspModulDto;
import lombok.Data;

/**
 * Command pel formulari de mòduls SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ScspModulCommand {

	private String nom;
	private String descripcio;
	private boolean actiuEntrada;
	private boolean actiuSortida;

	public static ScspModulCommand toCommand(ScspModulDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ScspModulCommand.class);
	}
	public static ScspModulDto toDto(ScspModulCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ScspModulDto.class);
	}
}
