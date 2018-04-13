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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -8620175604318725073L;

}
