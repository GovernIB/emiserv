/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiDto extends ObjecteAmbPermisosDto {

	private Long id;
	private String codi;
	private String nom;
	private ServeiTipusEnumDto tipus;
	private String descripcio;
	private String backofficeClass;
	private String backofficeCaibUrl;
	private BackofficeAutenticacioTipusEnumDto backofficeCaibAutenticacio;
	private BackofficeAsyncTipusEnumDto backofficeCaibAsyncTipus;
	private int backofficeCaibAsyncTer;
	private String resolverClass;
	private String urlPerDefecte;
	private String responseResolverClass;
	private boolean xsdGestioActiva;
	private String xsdEsquemaBackup;
	private boolean configurat;
	private boolean actiu;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public ServeiTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ServeiTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getBackofficeClass() {
		return backofficeClass;
	}
	public void setBackofficeClass(String backofficeClass) {
		this.backofficeClass = backofficeClass;
	}
	public String getBackofficeCaibUrl() {
		return backofficeCaibUrl;
	}
	public void setBackofficeCaibUrl(String backofficeCaibUrl) {
		this.backofficeCaibUrl = backofficeCaibUrl;
	}
	public BackofficeAutenticacioTipusEnumDto getBackofficeCaibAutenticacio() {
		return backofficeCaibAutenticacio;
	}
	public void setBackofficeCaibAutenticacio(BackofficeAutenticacioTipusEnumDto backofficeCaibAutenticacio) {
		this.backofficeCaibAutenticacio = backofficeCaibAutenticacio;
	}
	public BackofficeAsyncTipusEnumDto getBackofficeCaibAsyncTipus() {
		return backofficeCaibAsyncTipus;
	}
	public void setBackofficeCaibAsyncTipus(BackofficeAsyncTipusEnumDto backofficeCaibAsyncTipus) {
		this.backofficeCaibAsyncTipus = backofficeCaibAsyncTipus;
	}
	public int getBackofficeCaibAsyncTer() {
		return backofficeCaibAsyncTer;
	}
	public void setBackofficeCaibAsyncTer(int backofficeCaibAsyncTer) {
		this.backofficeCaibAsyncTer = backofficeCaibAsyncTer;
	}
	public String getResolverClass() {
		return resolverClass;
	}
	public void setResolverClass(String resolverClass) {
		this.resolverClass = resolverClass;
	}
	public String getUrlPerDefecte() {
		return urlPerDefecte;
	}
	public void setUrlPerDefecte(String urlPerDefecte) {
		this.urlPerDefecte = urlPerDefecte;
	}
	public String getResponseResolverClass() {
		return responseResolverClass;
	}
	public void setResponseResolverClass(String responseResolverClass) {
		this.responseResolverClass = responseResolverClass;
	}
	public boolean isXsdGestioActiva() {
		return xsdGestioActiva;
	}
	public void setXsdGestioActiva(boolean xsdGestioActiva) {
		this.xsdGestioActiva = xsdGestioActiva;
	}
	public String getXsdEsquemaBackup() {
		return xsdEsquemaBackup;
	}
	public void setXsdEsquemaBackup(String xsdEsquemaBackup) {
		this.xsdEsquemaBackup = xsdEsquemaBackup;
	}
	public boolean isConfigurat() {
		return configurat;
	}
	public void setConfigurat(boolean configurat) {
		this.configurat = configurat;
	}
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
