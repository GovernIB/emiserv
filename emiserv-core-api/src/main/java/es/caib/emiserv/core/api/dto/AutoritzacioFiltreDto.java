package es.caib.emiserv.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AutoritzacioFiltreDto {

	private Long serveiId;
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
	public Long getServeiId() {
		return serveiId;
	}
	public void setServeiId(Long serveiId) {
		this.serveiId = serveiId;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
