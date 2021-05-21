/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una autorització d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AutoritzacioDto {

	private Long id;
	private Long serveiId;
	private Integer aplicacioId;
	private String aplicacioNom;
	private Long organismeId;
	private String organismeNom;
	private Date dataAlta;
	private Date dataBaixa;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getServeiId() {
		return serveiId;
	}
	public void setServeiId(Long serveiId) {
		this.serveiId = serveiId;
	}
	public Integer getAplicacioId() {
		return aplicacioId;
	}
	public void setAplicacioId(Integer aplicacioId) {
		this.aplicacioId = aplicacioId;
	}
	public String getAplicacioNom() {
		return aplicacioNom;
	}
	public void setAplicacioNom(String aplicacioNom) {
		this.aplicacioNom = aplicacioNom;
	}
	public Long getOrganismeId() {
		return organismeId;
	}
	public void setOrganismeId(Long organismeId) {
		this.organismeId = organismeId;
	}
	public String getOrganismeNom() {
		return organismeNom;
	}
	public void setOrganismeNom(String organismeNom) {
		this.organismeNom = organismeNom;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
