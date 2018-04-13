/**
 * 
 */
package es.caib.emiserv.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.emiserv.core.api.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.core.api.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.dto.ServeiTipusEnumDto;
import es.caib.emiserv.war.helper.ConversioTipusHelper;
import es.caib.emiserv.war.validation.CodiServeiNoRepetit;

/**
 * Command per al manteniment de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CodiServeiNoRepetit(campId = "id", campCodi = "codi")
public class ServeiCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	@NotNull
	private ServeiTipusEnumDto tipus;
	@Size(max=1024)
	private String descripcio;
	@NotEmpty(groups = TipusBackoffice.class) @Size(max=256)
	private String backofficeClass;
	@NotEmpty(groups = TipusBackoffice.class) @Size(max=256)
	private String backofficeCaibUrl;
	@NotNull(groups = TipusBackoffice.class)
	private BackofficeAutenticacioTipusEnumDto backofficeCaibAutenticacio;
	@NotNull(groups = TipusBackoffice.class)
	private BackofficeAsyncTipusEnumDto backofficeCaibAsyncTipus;
	private int backofficeCaibAsyncTer;
	@Size(max=256)
	@NotNull(groups = TipusEnrutador.class)
	private String resolverClass;
	@Size(max=256)
	private String urlPerDefecte;
	@Size(max=256)
	@NotNull(groups = TipusEnrutadorMultiple.class)
	private String responseResolverClass;

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
	public static ServeiCommand toCommand(ServeiDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ServeiCommand.class);
	}
	public static ServeiDto toDto(ServeiCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ServeiDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public interface TipusBackoffice {}
	public interface TipusEnrutador {}
	public interface TipusEnrutadorMultiple {}

}
