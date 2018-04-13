/**
 * 
 */
package es.caib.emiserv.war.command;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.emiserv.core.api.dto.OrganismeDto;
import es.caib.emiserv.war.helper.ConversioTipusHelper;

/**
 * Informació d'un organisme per a autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganismeCommand {

	public Long id;
	@NotEmpty @Size(max = 64)
	public String nom;
	@NotEmpty @Size(max = 16)
	public String cif;
	@NotNull
	public Date dataAlta;
	public Date dataBaixa;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
