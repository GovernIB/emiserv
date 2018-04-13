/**
 * 
 */
package es.caib.emiserv.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.emiserv.core.api.dto.ServeiRutaDestiDto;
import es.caib.emiserv.war.helper.ConversioTipusHelper;

/**
 * Informació d'una ruta d'un servei.
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
