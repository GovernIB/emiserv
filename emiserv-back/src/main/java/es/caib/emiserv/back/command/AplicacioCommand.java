/**
 * 
 */
package es.caib.emiserv.back.command;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.intf.dto.AplicacioDto;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioDto;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command pel formulari d'aplicacions de les autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class AplicacioCommand {

	private Integer id;
	@NotEmpty @Size(max = 16)
	private String certificatNif;
	@NotEmpty @Size(max = 512)
	private String cn;
	@NotEmpty @Size(max = 64)
	private String numeroSerie;
	@NotNull
	private AutoritatCertificacioDto autoridadCertificacio;
	private Date dataAlta;
	private Date dataBaixa;

	public static AplicacioCommand toCommand(AplicacioDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				AplicacioCommand.class);
	}
	public static AplicacioDto toDto(AplicacioCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				AplicacioDto.class);
	}

}
