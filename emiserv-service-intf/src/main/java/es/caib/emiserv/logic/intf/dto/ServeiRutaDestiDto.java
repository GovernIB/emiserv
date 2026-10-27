/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una ruta d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiRutaDestiDto extends ObjecteAmbPermisosDto {

	private Long id;
	private String entitatCodi;
	private String url;
	private Long ordre;
	private ServeiRutaDestiTipusEnumDto destiTipus;
	private BackofficeAutenticacioTipusEnumDto backofficeAutenticacio;
	/** Prefix de les propietats de les credencials del backoffice (rutes BACKOFFICE amb autenticació). */
	private String credencialsPropietats;
	/** Indica si les credencials del backoffice estan configurades (null si la ruta no en necessita). */
	private Boolean credencialsConfigurades;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public Long getOrdre() {
		return ordre;
	}
	public void setOrdre(Long ordre) {
		this.ordre = ordre;
	}
	public ServeiRutaDestiTipusEnumDto getDestiTipus() {
		return destiTipus;
	}
	public void setDestiTipus(ServeiRutaDestiTipusEnumDto destiTipus) {
		this.destiTipus = destiTipus;
	}
	public BackofficeAutenticacioTipusEnumDto getBackofficeAutenticacio() {
		return backofficeAutenticacio;
	}
	public void setBackofficeAutenticacio(BackofficeAutenticacioTipusEnumDto backofficeAutenticacio) {
		this.backofficeAutenticacio = backofficeAutenticacio;
	}
	public String getCredencialsPropietats() {
		return credencialsPropietats;
	}
	public void setCredencialsPropietats(String credencialsPropietats) {
		this.credencialsPropietats = credencialsPropietats;
	}
	public Boolean getCredencialsConfigurades() {
		return credencialsConfigurades;
	}
	public void setCredencialsConfigurades(Boolean credencialsConfigurades) {
		this.credencialsConfigurades = credencialsConfigurades;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
