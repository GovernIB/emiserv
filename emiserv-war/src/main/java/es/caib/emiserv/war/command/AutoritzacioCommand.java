/**
 * 
 */
package es.caib.emiserv.war.command;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.emiserv.core.api.dto.AutoritzacioDto;
import es.caib.emiserv.war.helper.ConversioTipusHelper;

/**
 * Informació d'una autorització d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AutoritzacioCommand {

	private Long id;
	@NotNull
	private Integer aplicacioId;
	@NotNull
	private Long organismeId;
	private Date dataAlta;
	private Date dataBaixa;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getAplicacioId() {
		return aplicacioId;
	}
	public void setAplicacioId(Integer aplicacioId) {
		this.aplicacioId = aplicacioId;
	}
	public Long getOrganismeId() {
		return organismeId;
	}
	public void setOrganismeId(Long organismeId) {
		this.organismeId = organismeId;
	}
	public Date getDataAlta() {
		return dataAlta;
	}
	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}
	public Date getDataBaixa() {
		return dataBaixa;
	}
	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}

	public static AutoritzacioCommand toCommand(AutoritzacioDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				AutoritzacioCommand.class);
	}
	public static AutoritzacioDto toDto(AutoritzacioCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				AutoritzacioDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
