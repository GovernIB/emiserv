/**
 * 
 */
package es.caib.emiserv.back.command;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.back.validation.NomParametreNoRepetit;
import es.caib.emiserv.logic.intf.dto.ScspParametreDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Command pel formulari de mòduls SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NomParametreNoRepetit
public class ScspParametreCommand {

	@NotNull
	private String nombre;
	private String valor;
	private String descripcion;
	private boolean nou;

	public boolean isPassword() {
		return nombre != null && nombre.toUpperCase().contains("PASS");
	}

	public static ScspParametreCommand toCommand(ScspParametreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ScspParametreCommand.class);
	}
	public static ScspParametreDto toDto(ScspParametreCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ScspParametreDto.class);
	}
}
