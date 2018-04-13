/**
 * 
 */
package es.caib.emiserv.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.emiserv.core.audit.EmiservAuditable;

/**
 * Classe del model de dades que representa un Servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ems_servei_ruta_desti")
@EntityListeners(AuditingEntityListener.class)
public class ServeiRutaDestiEntity extends EmiservAuditable<Long> {

	@Column(name = "entitat_codi", length = 64, nullable = false)
	private String entitatCodi;
	@Column(name = "url", length = 256, nullable = false)
	private String url;
	/** Determina l'ordre de preferència de rutes per al resolutor de respostes. */
	@Column(name = "ordre")
	private Long ordre;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "servei_id",
			foreignKey = @ForeignKey(name = "ems_servei_ruta_fk"))
	private ServeiEntity servei;
	@Version
	private long version = 0;



	public String getEntitatCodi() {
		return entitatCodi;
	}
	public String getUrl() {
		return url;
	}
	public Long getOrdre() {
		return ordre;
	}
	public void setOrdre(Long ordre) {
		this.ordre = ordre;
	}
	public ServeiEntity getServei() {
		return servei;
	}

	public void update(
			String entitatCodi,
			String url) {
		this.entitatCodi = entitatCodi;
		this.url = url;
	}

	public static Builder getBuilder(
			ServeiEntity servei,
			String entitatCodi,
			String url,
			Long ordre) {
		return new Builder(
				servei,
				entitatCodi,
				url,
				ordre);
	}

	public static class Builder {
		ServeiRutaDestiEntity built;
		Builder(
				ServeiEntity servei,
				String entitatCodi,
				String url,
				Long ordre) {
			built = new ServeiRutaDestiEntity();
			built.servei = servei;
			built.entitatCodi = entitatCodi;
			built.url = url;
			built.ordre = ordre;
		}
		public ServeiRutaDestiEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entitatCodi == null) ? 0 : entitatCodi.hashCode());
		result = prime * result + ((servei == null) ? 0 : servei.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		ServeiRutaDestiEntity other = (ServeiRutaDestiEntity) obj;
		if (entitatCodi == null) {
			if (other.entitatCodi != null)
				return false;
		} else if (!entitatCodi.equals(other.entitatCodi))
			return false;
		if (servei == null) {
			if (other.servei != null)
				return false;
		} else if (!servei.equals(other.servei))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServeiRutaDestiEntity [entitatCodi=" + entitatCodi + ", url=" + url + ", servei=" + servei + ", ordre=" + ordre + "]";
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
