/**
 * 
 */
package es.caib.emiserv.back.command;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.back.validation.CodiEntitatNoRepetit;
import es.caib.emiserv.logic.intf.dto.EntitatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Command pel formulari de entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@CodiEntitatNoRepetit
public class EntitatCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	@NotEmpty @Size(max=9)
	private String cif;
	@Size(max=9)
	private String unitatArrel;

	public static EntitatCommand toCommand(EntitatDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				EntitatCommand.class);
	}
	public static EntitatDto toDto(EntitatCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				EntitatDto.class);
	}

}
