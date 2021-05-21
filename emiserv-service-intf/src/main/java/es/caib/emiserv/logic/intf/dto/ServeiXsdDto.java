/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un fitxer XSD d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiXsdDto {

	private XsdTipusEnumDto tipus;
	private String arxiuNom;

	public XsdTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(XsdTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
