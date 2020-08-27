/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una autoritat certificadora per a les autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AutoritatCertificacioDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getFormatName() {
		return this.nom + " (" + this.codi + ")";
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	private static final long serialVersionUID = -8620175604318725073L;

}
