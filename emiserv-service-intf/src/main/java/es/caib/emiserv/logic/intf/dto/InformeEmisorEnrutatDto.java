package es.caib.emiserv.logic.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació per a generar l'informe de peticions per emissor
 * dels serveis enrutats.
 */
public class InformeEmisorEnrutatDto {

	private String emissorCodi;
	private ServeiTipusEnumDto serveiTipus;
	private String serveiCodi;
	private String serveiNom;
	private String entitatCodi;
	private String urlDesti;
	private Long peticionsTotals;
	private Long peticionsCorrectes;
	private Long peticionsErronies;
	private Long peticionsRespostaEsperada;
	private Long peticionsTotalsServei;
	private Long peticionsCorrectesServei;
	private Long peticionsErroniesServei;
	private Long peticionsRespostaEsperadaServei;

	public InformeEmisorEnrutatDto(
			String emissorCodi,
			ServeiTipusEnumDto serveiTipus,
			String serveiCodi,
			String serveiNom,
			String entitatCodi,
			String urlDesti,
			Long peticionsTotals,
			Long peticionsCorrectes,
			Long peticionsErronies,
			Long peticionsRespostaEsperada,
			Long peticionsTotalsServei,
			Long peticionsCorrectesServei,
			Long peticionsErroniesServei,
			Long peticionsRespostaEsperadaServei) {
		this.emissorCodi = emissorCodi;
		this.serveiTipus = serveiTipus;
		this.serveiCodi = serveiCodi;
		this.serveiNom = serveiNom;
		this.entitatCodi = entitatCodi;
		this.urlDesti = urlDesti;
		this.peticionsTotals = peticionsTotals;
		this.peticionsCorrectes = peticionsCorrectes;
		this.peticionsErronies = peticionsErronies;
		this.peticionsRespostaEsperada = peticionsRespostaEsperada;
		this.peticionsTotalsServei = peticionsTotalsServei;
		this.peticionsCorrectesServei = peticionsCorrectesServei;
		this.peticionsErroniesServei = peticionsErroniesServei;
		this.peticionsRespostaEsperadaServei = peticionsRespostaEsperadaServei;
	}

	public String getEmissorCodi() {
		return emissorCodi;
	}

	public void setEmissorCodi(String emissorCodi) {
		this.emissorCodi = emissorCodi;
	}

	public ServeiTipusEnumDto getServeiTipus() {
		return serveiTipus;
	}

	public void setServeiTipus(ServeiTipusEnumDto serveiTipus) {
		this.serveiTipus = serveiTipus;
	}

	public boolean isEnrutadorMultiple() {
		return ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(serveiTipus);
	}

	public String getEntitatCodi() {
		return entitatCodi;
	}

	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
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

	public String getUrlDesti() {
		return urlDesti;
	}

	public void setUrlDesti(String urlDesti) {
		this.urlDesti = urlDesti;
	}

	public Long getPeticionsTotals() {
		return peticionsTotals;
	}

	public void setPeticionsTotals(Long peticionsTotals) {
		this.peticionsTotals = peticionsTotals;
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

	public Long getPeticionsRespostaEsperada() {
		return peticionsRespostaEsperada;
	}

	public void setPeticionsRespostaEsperada(Long peticionsRespostaEsperada) {
		this.peticionsRespostaEsperada = peticionsRespostaEsperada;
	}

	public Long getPeticionsTotalsServei() {
		return peticionsTotalsServei;
	}

	public void setPeticionsTotalsServei(Long peticionsTotalsServei) {
		this.peticionsTotalsServei = peticionsTotalsServei;
	}

	public Long getPeticionsCorrectesServei() {
		return peticionsCorrectesServei;
	}

	public void setPeticionsCorrectesServei(Long peticionsCorrectesServei) {
		this.peticionsCorrectesServei = peticionsCorrectesServei;
	}

	public Long getPeticionsErroniesServei() {
		return peticionsErroniesServei;
	}

	public void setPeticionsErroniesServei(Long peticionsErroniesServei) {
		this.peticionsErroniesServei = peticionsErroniesServei;
	}

	public Long getPeticionsRespostaEsperadaServei() {
		return peticionsRespostaEsperadaServei;
	}

	public void setPeticionsRespostaEsperadaServei(Long peticionsRespostaEsperadaServei) {
		this.peticionsRespostaEsperadaServei = peticionsRespostaEsperadaServei;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
