/**
 * 
 */
package es.caib.emiserv.back.command;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.back.validation.CifEmisorNoRepetit;
import es.caib.emiserv.logic.intf.dto.EmisorDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command pel formulari d'organismes cessionaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@CifEmisorNoRepetit
public class EmisorCommand {

	public Long id;
	@NotEmpty @Size(max = 50)
	public String nom;
	@NotEmpty @Size(max = 16)
	public String cif;
	public Date dataAlta;
	public Date dataBaixa;


	public static EmisorCommand toCommand(EmisorDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				EmisorCommand.class);
	}
	public static EmisorDto toDto(EmisorCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				EmisorDto.class);
	}

}
