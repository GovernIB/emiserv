/**
 * 
 */
package es.caib.emiserv.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;

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
	/** EMISERV: reenviament a l'emisor del cedent. BACKOFFICE: consulta directa al backoffice del cedent. */
	private ServeiRutaDestiTipusEnumDto destiTipus = ServeiRutaDestiTipusEnumDto.EMISERV;
	private BackofficeAutenticacioTipusEnumDto backofficeAutenticacio;

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
	public ServeiRutaDestiTipusEnumDto getDestiTipus() {
		return destiTipus;
	}
	public void setDestiTipus(ServeiRutaDestiTipusEnumDto destiTipus) {
		this.destiTipus = destiTipus;
	}
	public BackofficeAutenticacioTipusEnumDto getBackofficeAutenticacio() {
		return backofficeAutenticacio;
	}
	public void setBackofficeAutenticacio(BackofficeAutenticacioTipusEnumDto backofficeAutenticacio) {
		this.backofficeAutenticacio = backofficeAutenticacio;
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
