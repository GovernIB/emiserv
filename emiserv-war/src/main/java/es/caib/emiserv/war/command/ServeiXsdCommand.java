/**
 * 
 */
package es.caib.emiserv.war.command;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.emiserv.core.api.dto.ServeiXsdDto;
import es.caib.emiserv.core.api.dto.XsdTipusEnumDto;
import es.caib.emiserv.war.helper.ConversioTipusHelper;

/**
 * Informació d'un fitxer XSD d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiXsdCommand {

	private Long id;
	@NotNull
	private XsdTipusEnumDto tipus;
	@JsonIgnore
	private MultipartFile contingut;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public XsdTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(XsdTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public MultipartFile getContingut() {
		return contingut;
	}
	public void setContingut(MultipartFile contingut) {
		this.contingut = contingut;
	}

	public static ServeiXsdCommand toCommand(ServeiXsdDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ServeiXsdCommand.class);
	}
	public static ServeiXsdDto toDto(ServeiXsdCommand command) throws IOException {
		return ConversioTipusHelper.convertir(
				command,
				ServeiXsdDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
