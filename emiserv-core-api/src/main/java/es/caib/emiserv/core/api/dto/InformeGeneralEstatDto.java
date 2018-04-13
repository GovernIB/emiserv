/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació per a generar l'informe d'estat general.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeGeneralEstatDto {

	private ServeiTipusEnumDto peticioTipus;
	private String entitatNom;
	private String entitatCif;
	private String departament;
	private String procedimentCodi;
	private String procedimentNom;
	private String serveiCodi;
	private String serveiNom;
	private String emissorNom;
	private String emissorCif;
	private Long peticionsCorrectes;
	private Long peticionsErronies;

	public InformeGeneralEstatDto(
			String entitatNom,
			String entitatCif,
			String departament,
			String procedimentCodi,
			String procedimentNom,
			String serveiCodi,
			String serveiNom,
			String emissorCif,
			Long peticionsCorrectes,
			Long peticionsErronies) {
		this(
				ServeiTipusEnumDto.BACKOFFICE,
				entitatNom,
				entitatCif,
				departament,
				procedimentCodi,
				procedimentNom,
				serveiCodi,
				serveiNom,
				emissorCif,
				peticionsCorrectes,
				peticionsErronies);
	}

	public InformeGeneralEstatDto(
			ServeiTipusEnumDto peticioTipus,
			String entitatNom,
			String entitatCif,
			String departament,
			String procedimentCodi,
			String procedimentNom,
			String serveiCodi,
			String serveiNom,
			String emissorCif,
			Long peticionsCorrectes,
			Long peticionsErronies) {
		this.peticioTipus = peticioTipus;
		this.entitatNom = entitatNom;
		this.entitatCif = entitatCif;
		this.departament = departament;
		this.procedimentCodi = procedimentCodi;
		this.procedimentNom = procedimentNom;
		this.serveiCodi = serveiCodi;
		this.serveiNom = serveiNom;
		this.emissorCif = emissorCif;
		this.peticionsCorrectes = peticionsCorrectes;
		this.peticionsErronies =  peticionsErronies;
	}

	public InformeGeneralEstatDto() {}



	public ServeiTipusEnumDto getPeticioTipus() {
		return peticioTipus;
	}
	public void setPeticioTipus(ServeiTipusEnumDto peticioTipus) {
		this.peticioTipus = peticioTipus;
	}
	public String getEntitatNom() {
		return entitatNom;
	}
	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}
	public String getEntitatCif() {
		return entitatCif;
	}
	public void setEntitatCif(String entitatCif) {
		this.entitatCif = entitatCif;
	}
	public String getDepartament() {
		return departament;
	}
	public void setDepartament(String departament) {
		this.departament = departament;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public void setProcedimentNom(String procedimentNom) {
		this.procedimentNom = procedimentNom;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
	public String getServeiNom() {
		return serveiNom;
	}
	public void setServeiNom(String serveiNom) {
		this.serveiNom = serveiNom;
	}
	public String getEmissorNom() {
		return emissorNom;
	}
	public void setEmissorNom(String emissorNom) {
		this.emissorNom = emissorNom;
	}
	public String getEmissorCif() {
		return emissorCif;
	}
	public void setEmissorCif(String emissorCif) {
		this.emissorCif = emissorCif;
	}
	public Long getPeticionsCorrectes() {
		return peticionsCorrectes;
	}
	public void setPeticionsCorrectes(Long peticionsCorrectes) {
		this.peticionsCorrectes = peticionsCorrectes;
	}
	public Long getPeticionsErronies() {
		return peticionsErronies;
	}
	public void setPeticionsErronies(Long peticionsErronies) {
		this.peticionsErronies = peticionsErronies;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
