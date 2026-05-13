/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Resultat de processar la petició de redirecció SCSP.
 * 
 */
@Getter
@Setter
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

	private String solicitantId;

	// Atributs per montar un SoapFault
	private FaultCodeEnum faultCode;
	private String faultErrorCode;
	private String faultErrorString;
	private Integer numElements;

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
		this.atributTimestamp = atributTimestamp;
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
		this.atributTimestamp = atributTimestamp;
		this.atributCodigoCertificado = atributCodigoCertificado;
	}
	public RedireccioProcessarResultatDto(
			String errorCodi,
			String errorDescripcio,
			FaultCodeEnum faultCode,
			String faultErrorCode,
			String faultErrorString) {
		super();
		this.error = true;
		this.errorCodi = errorCodi;
		this.errorDescripcio = errorDescripcio;
		this.faultCode = faultCode;
		this.faultErrorCode = faultErrorCode;
		this.faultErrorString = faultErrorString;
	}
	public RedireccioProcessarResultatDto(
			String errorCodi,
			String errorDescripcio,
			String atributPeticioId,
			String atributTimestamp,
			String atributCodigoCertificado,
			FaultCodeEnum faultCode,
			String faultErrorCode,
			String faultErrorString) {
		super();
		this.error = true;
		this.errorCodi = errorCodi;
		this.errorDescripcio = errorDescripcio;
		this.atributPeticioId = atributPeticioId;
		this.atributTimestamp = atributTimestamp;
		this.atributCodigoCertificado = atributCodigoCertificado;
		this.faultCode = faultCode;
		this.faultErrorCode = faultErrorCode;
		this.faultErrorString = faultErrorString;
	}

	public void addUrlDesti(String codiEntitat, String urlDesti) {
		this.urlDestins.put(codiEntitat, urlDesti);
	}
	public void setError(boolean error) {
		this.error = error;
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
		copy.setFaultCode(this.faultCode);
		copy.setFaultErrorCode(this.faultErrorCode);
		copy.setFaultErrorString(this.faultErrorString);
		copy.setNumElements(this.numElements);
		copy.setSolicitantId(this.solicitantId);

		return copy;
	}

	public static enum FaultCodeEnum {
		SERVER("Server"),
		CLIENT("Client");

		@Getter
		private String value;

		private FaultCodeEnum(String value) {
			this.value = value;
		}
	}

	private static final long serialVersionUID = -139254994389509932L;
}
