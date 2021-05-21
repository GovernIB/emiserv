/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Data;

/**
 * Informació d'una autoritat certificadora per a les autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class AutoritatCertificacioDto implements Serializable {

	private Long id;
	private String codca;
	private String nombre;
	
	public String getFormatName() {
		return this.nombre + " (" + this.codca + ")";
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -8620175604318725073L;

}
