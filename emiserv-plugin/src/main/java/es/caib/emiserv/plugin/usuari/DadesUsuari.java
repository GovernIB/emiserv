/**
 * 
 */
package es.caib.emiserv.plugin.usuari;

import java.io.Serializable;

/**
 * Dades d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuari implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String[] rols;

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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String[] getRols() {
		return rols;
	}
	public void setRols(String[] rols) {
		this.rols = rols;
	}


	private static final long serialVersionUID = -139254994389509932L;

}
