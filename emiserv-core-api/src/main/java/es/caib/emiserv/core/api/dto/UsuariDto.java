/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariDto implements Serializable {

	private String codi;
	private String nom;
	private String nif;

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
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
