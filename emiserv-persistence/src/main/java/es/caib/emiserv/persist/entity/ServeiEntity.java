/**
 * 
 */
package es.caib.emiserv.persist.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;

/**
 * Classe del model de dades que representa un Servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = AbstractAuditableEntity.TABLE_PREFIX + "_servei")
@EntityListeners(AuditingEntityListener.class)
public class ServeiEntity extends AbstractAuditableEntity<Long> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipus", nullable = false)
	private ServeiTipusEnumDto tipus;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "backoffice_class", length = 256)
	private String backofficeClass;
	@Column(name = "backcaib_url", length = 256)
	private String backofficeCaibUrl;
	@Column(name = "backcaib_autenticacio")
	private BackofficeAutenticacioTipusEnumDto backofficeCaibAutenticacio;
	@Column(name = "backcaib_async_tipus")
	private BackofficeAsyncTipusEnumDto backofficeCaibAsyncTipus;
	@Column(name = "backcaib_async_ter")
	private int backofficeCaibAsyncTer;
	@Column(name = "resolver_class", length = 256)
	private String resolverClass;
	@Column(name = "response_resolver_class", length = 256)
	private String responseResolverClass;
	@Column(name = "url_per_defecte", length = 256)
	private String urlPerDefecte;
	@Column(name = "xsd_activa")
	private boolean xsdGestioActiva;
	@Column(name = "xsd_esquema_bak", length = 256)
	private String xsdEsquemaBackup;
	@Column(name = "configurat")
	private boolean configurat = false;
	@Column(name = "actiu")
	private boolean actiu = true;
	@OneToMany(
			mappedBy = "servei",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<ServeiRutaDestiEntity> rutes = new ArrayList<ServeiRutaDestiEntity>();
	@Version
	private long version = 0;



	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public ServeiTipusEnumDto getTipus() {
		return tipus;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public String getBackofficeClass() {
		return backofficeClass;
	}
	public String getBackofficeCaibUrl() {
		return backofficeCaibUrl;
	}
	public BackofficeAutenticacioTipusEnumDto getBackofficeCaibAutenticacio() {
		return backofficeCaibAutenticacio;
	}
	public BackofficeAsyncTipusEnumDto getBackofficeCaibAsyncTipus() {
		return backofficeCaibAsyncTipus;
	}
	public int getBackofficeCaibAsyncTer() {
		return backofficeCaibAsyncTer;
	}
	public String getResolverClass() {
		return resolverClass;
	}
	public String getResponseResolverClass() {
		return responseResolverClass;
	}
	public void setResponseResolverClass(String resonseResolverClass) {
		this.responseResolverClass = resonseResolverClass;
	}
	public String getUrlPerDefecte() {
		return urlPerDefecte;
	}
	public boolean isXsdGestioActiva() {
		return xsdGestioActiva;
	}
	public String getXsdEsquemaBackup() {
		return xsdEsquemaBackup;
	}
	public boolean isConfigurat() {
		return configurat;
	}
	public boolean isActiu() {
		return actiu;
	}
	public List<ServeiRutaDestiEntity> getRutes() {
		return rutes;
	}

	public void updateBackoffice(
			String nom,
			String descripcio,
			String backofficeClass,
			String backofficeCaibUrl,
			BackofficeAsyncTipusEnumDto backofficeCaibAsyncTipus,
			int backofficeCaibAsyncTer,
			BackofficeAutenticacioTipusEnumDto backofficeCaibAutenticacio) {
		this.nom = nom;
		this.tipus = ServeiTipusEnumDto.BACKOFFICE;
		this.descripcio = descripcio;
		this.backofficeClass = backofficeClass;
		this.backofficeCaibUrl = backofficeCaibUrl;
		this.backofficeCaibAsyncTipus = backofficeCaibAsyncTipus;
		this.backofficeCaibAsyncTer = backofficeCaibAsyncTer;
		this.backofficeCaibAutenticacio = backofficeCaibAutenticacio;
	}
	public void updateEnrutador(
			String nom,
			String descripcio,
			String resolverClass,
			String urlPerDefecte) {
		this.nom = nom;
		this.tipus = ServeiTipusEnumDto.ENRUTADOR;
		this.descripcio = descripcio;
		this.resolverClass = resolverClass;
		this.urlPerDefecte = urlPerDefecte;
		this.configurat = true;
	}
	public void updateEnrutadorMultiple(
			String nom,
			String descripcio,
			String responseResolverClass) {
		this.nom = nom;
		this.tipus = ServeiTipusEnumDto.ENRUTADOR_MULTIPLE;
		this.descripcio = descripcio;
		this.responseResolverClass= responseResolverClass;
		this.configurat = true;
	}
	public void updateConfigurat(
			boolean configurat) {
		this.configurat = configurat;
	}
	public void updateActiu(
			boolean actiu) {
		this.actiu = actiu;
	}
	public void updateXsd(
			boolean xsdGestioActiva,
			String xsdEsquemaBackup) {
		this.xsdGestioActiva = xsdGestioActiva;
		this.xsdEsquemaBackup = xsdEsquemaBackup;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			ServeiTipusEnumDto tipus) {
		return new Builder(
				codi,
				nom,
				tipus);
	}

	public static class Builder {
		ServeiEntity built;
		Builder(
				String codi,
				String nom,
				ServeiTipusEnumDto tipus) {
			built = new ServeiEntity();
			built.codi = codi;
			built.nom = nom;
			built.tipus = tipus;
			built.configurat = ServeiTipusEnumDto.ENRUTADOR.equals(tipus)
								|| ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(tipus);
		}
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public Builder backofficeClass(String backofficeClass) {
			built.backofficeClass = backofficeClass;
			return this;
		}
		public Builder backofficeCaibUrl(String backofficeCaibUrl) {
			built.backofficeCaibUrl = backofficeCaibUrl;
			return this;
		}
		public Builder backofficeCaibAutenticacio(BackofficeAutenticacioTipusEnumDto backofficeCaibAutenticacio) {
			built.backofficeCaibAutenticacio = backofficeCaibAutenticacio;
			return this;
		}
		public Builder backofficeCaibAsyncTipus(BackofficeAsyncTipusEnumDto backofficeCaibAsyncTipus) {
			built.backofficeCaibAsyncTipus = backofficeCaibAsyncTipus;
			return this;
		}
		public Builder backofficeCaibAsyncTer(int backofficeCaibAsyncTer) {
			built.backofficeCaibAsyncTer = backofficeCaibAsyncTer;
			return this;
		}
		public Builder resolverClass(String resolverClass) {
			built.resolverClass = resolverClass;
			return this;
		}
		public Builder urlPerDefecte(String urlPerDefecte) {
			built.urlPerDefecte = urlPerDefecte;
			return this;
		}
		public Builder responseResolverClass(String responseResolverClass) {
			built.responseResolverClass = responseResolverClass;
			return this;
		}
		public ServeiEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServeiEntity other = (ServeiEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServeiEntity [codi=" + codi + ", nom=" + nom + ", tipus=" + tipus + ", descripcio=" + descripcio
				+ ", configurat=" + configurat + ", actiu=" + actiu + "]";
	}

}
