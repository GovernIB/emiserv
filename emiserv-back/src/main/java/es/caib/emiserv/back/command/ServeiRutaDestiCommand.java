/**
 * 
 */
package es.caib.emiserv.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiDto;

/**
 * Command pel formulari de rutes d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiRutaDestiCommand {

	private Long id;
	private Long ordre;
	@NotEmpty @Size(max=64)
	private String entitatCodi;
	@NotEmpty @Size(max=256)
	private String url;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrdre() {
		return ordre;
	}
	public void setOrdre(Long ordre) {
		this.ordre = ordre;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public static ServeiRutaDestiCommand toCommand(ServeiRutaDestiDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ServeiRutaDestiCommand.class);
	}
	public static ServeiRutaDestiDto toDto(ServeiRutaDestiCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ServeiRutaDestiDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
