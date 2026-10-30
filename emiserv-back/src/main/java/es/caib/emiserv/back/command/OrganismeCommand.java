/**
 * 
 */
package es.caib.emiserv.back.command;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.back.validation.CifOrganismeNoRepetit;
import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command pel formulari d'organismes per a autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CifOrganismeNoRepetit(campId = "id", campCif = "cif")
@Data
@NoArgsConstructor
public class OrganismeCommand {

	public Long id;
	@NotEmpty @Size(max = 64)
	public String nom;
	@NotEmpty @Size(max = 16)
	public String cif;
	@NotNull
	public Date dataAlta;
	public Date dataBaixa;


	public static OrganismeCommand toCommand(OrganismeDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				OrganismeCommand.class);
	}
	public static OrganismeDto toDto(OrganismeCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				OrganismeDto.class);
	}

}
