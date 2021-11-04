/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un organisme per a autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganismeDto {

	private Long id;
	private String nom;
	private String cif;
	private Date dataAlta;
	private Date dataBaixa;
	private boolean bloquejat = false;
	private String codiUnitatTramitadora;

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
	public boolean isBloquejat() {
		return bloquejat;
	}
	public void setBloquejat(boolean bloquejat) {
		this.bloquejat = bloquejat;
	}
	public String getCodiUnitatTramitadora() {
		return codiUnitatTramitadora;
	}
	public void setCodiUnitatTramitadora(String codiUnitatTramitadora) {
		this.codiUnitatTramitadora = codiUnitatTramitadora;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
