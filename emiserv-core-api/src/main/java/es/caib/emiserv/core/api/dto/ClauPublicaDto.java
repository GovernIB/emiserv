/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una ClavePublica SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClauPublicaDto implements Serializable {

	private Long id;
	private String alies;
	private String nom;
	private String numSerie;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAlies() {
		return alies;
	}
	public void setAlies(String alies) {
		this.alies = alies;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getNumSerie() {
		return numSerie;
	}
	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -8620175604318725073L;

}
