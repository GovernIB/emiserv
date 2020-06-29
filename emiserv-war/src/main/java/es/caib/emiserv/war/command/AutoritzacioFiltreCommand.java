package es.caib.emiserv.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.emiserv.core.api.dto.AutoritzacioFiltreDto;

/**
 * Informació per a filtrar les autoritzacions d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AutoritzacioFiltreCommand {

	private String aplicacio;
	private Long organismeId;
	
	public String getAplicacio() {
		return aplicacio;
	}
	public void setAplicacio(String aplicacio) {
		this.aplicacio = aplicacio;
	}


	public Long getOrganismeId() {
		return organismeId;
	}
	public void setOrganismeId(Long organismeId) {
		this.organismeId = organismeId;
	}
	public AutoritzacioFiltreDto toDto() {
		AutoritzacioFiltreDto dto = new AutoritzacioFiltreDto();
		dto.setAplicacio(aplicacio);
		dto.setOrganismeId(organismeId);
		return dto;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
