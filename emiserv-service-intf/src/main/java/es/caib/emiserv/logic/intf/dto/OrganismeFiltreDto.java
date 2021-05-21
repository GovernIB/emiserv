/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Filtre per als organismes SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganismeFiltreDto {

	private String nom;
	private String cif;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
