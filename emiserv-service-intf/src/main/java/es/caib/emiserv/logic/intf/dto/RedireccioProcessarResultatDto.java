/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Resultat de processar la petició de redirecció SCSP.
 * 
 */
public class RedireccioProcessarResultatDto extends ObjecteAmbPermisosDto {

	/** Per indicar el tipus de redirecció simple o múltiple. */
	private ServeiTipusEnumDto tipus;
	private String urlDesti;
	/** Mapeig entre el codi de la entitat i el llistat de destins. */
	private Map<String, String> urlDestins = new HashMap<String, String>();
	/** Per indicar la entitat amb la resposta seleccionada */
	private String entitatCodiRedireccio;

	private int scspVersio;
	private boolean error = false;
	private String errorCodi;
	private String errorDescripcio;
	private String atributPeticioId;
	private String atributTimestamp;
	private String atributCodigoCertificado;

	public ServeiTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ServeiTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public RedireccioProcessarResultatDto() {
		super();
	}
	/** Constructor per a un destí simple. */
	public RedireccioProcessarResultatDto(
			String urlDesti,
			int scspVersio,
			String atributPeticioId,
			String atributTimestamp,
			String atributCodigoCertificado) {
		super();
		this.tipus = ServeiTipusEnumDto.ENRUTADOR;
		this.urlDesti = urlDesti;
		this.scspVersio = scspVersio;
		this.atributPeticioId = atributPeticioId;
		this. atributTimestamp = atributTimestamp;
		this.atributCodigoCertificado = atributCodigoCertificado;
	}	
	/** Constructor per a un destí múltiple. */
	public RedireccioProcessarResultatDto(
			Map<String, String> urlDestins,
			int scspVersio,
			String atributPeticioId,
			String atributTimestamp,
			String atributCodigoCertificado) {
		super();
		this.tipus = ServeiTipusEnumDto.ENRUTADOR_MULTIPLE;
		this.urlDestins = urlDestins;
		this.scspVersio = scspVersio;
		this.atributPeticioId = atributPeticioId;
		this. atributTimestamp = atributTimestamp;
		this.atributCodigoCertificado = atributCodigoCertificado;
	}
	public RedireccioProcessarResultatDto(
			String errorCodi,
			String errorDescripcio) {
		super();
		this.error = true;
		this.errorCodi = errorCodi;
		this.errorDescripcio = errorDescripcio;
	}
	public RedireccioProcessarResultatDto(
			String errorCodi,
			String errorDescripcio,
			String atributPeticioId,
			String atributTimestamp,
			String atributCodigoCertificado) {
		super();
		this.error = true;
		this.errorCodi = errorCodi;
		this.errorDescripcio = errorDescripcio;
		this.atributPeticioId = atributPeticioId;
		this.atributTimestamp = atributTimestamp;
		this.atributCodigoCertificado = atributCodigoCertificado;
	}

	public String getUrlDesti() {
		return this.urlDesti;
	}
	public void setUrlDesti(String urlDesti) {
		this.urlDesti = urlDesti;
	}
	public Map<String, String> getUrlDestins() {
		return urlDestins;
	}
	public void setUrlDestins(Map<String, String> urlDestins) {
		this.urlDestins = urlDestins;
	}
	public void addUrlDesti(String codiEntitat, String urlDesti) {
		this.urlDestins.put(codiEntitat, urlDesti);
	}
	public String getEntitatCodiRedireccio() {
		return entitatCodiRedireccio;
	}
	public void setEntitatCodiRedireccio(String entitatCodiRedireccio) {
		this.entitatCodiRedireccio = entitatCodiRedireccio;
	}
	public int getScspVersio() {
		return scspVersio;
	}
	public void setScspVersio(int scspVersio) {
		this.scspVersio = scspVersio;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorCodi() {
		return errorCodi;
	}
	public void setErrorCodi(String errorCodi) {
		this.errorCodi = errorCodi;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public String getAtributPeticioId() {
		return atributPeticioId;
	}
	public void setAtributPeticioId(String atributPeticioId) {
		this.atributPeticioId = atributPeticioId;
	}
	public String getAtributTimestamp() {
		return atributTimestamp;
	}
	public void setAtributTimestamp(String atributTimestamp) {
		this.atributTimestamp = atributTimestamp;
	}
	public String getAtributCodigoCertificado() {
		return atributCodigoCertificado;
	}
	public void setAtributCodigoCertificado(String atributCodigoCertificado) {
		this.atributCodigoCertificado = atributCodigoCertificado;
	}

	public boolean isAtributs() {
		return atributPeticioId != null || atributTimestamp != null || atributCodigoCertificado != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public RedireccioProcessarResultatDto copy() {
		RedireccioProcessarResultatDto copy = new RedireccioProcessarResultatDto();
		copy.setTipus(this.tipus);
		copy.setUrlDesti(this.urlDesti);
		for(String entitatCodi:	this.urlDestins.keySet())
			copy.getUrlDestins().put(entitatCodi, this.urlDestins.get(entitatCodi));
		/** Mapeig entre el codi de la entitat i el llistat de destins. */
		//private Map<String, String> urlDestins;
		copy.setScspVersio(this.scspVersio);
		copy.setError(this.error);
		copy.setErrorCodi(this.errorCodi);
		copy.setErrorDescripcio(this.errorDescripcio);
		copy.setAtributPeticioId(this.atributPeticioId);
		copy.setAtributTimestamp(this.atributTimestamp);
		copy.setAtributCodigoCertificado(this.atributCodigoCertificado);
		
		return copy;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
