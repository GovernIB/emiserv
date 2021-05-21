/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un EmisorCertificado SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EmisorDto implements Serializable {

	private Long id;
	private String nom;
	private String cif;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -8620175604318725073L;

}
