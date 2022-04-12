/**
 * 
 */
package es.caib.emiserv.persist.entity.scsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe de model de dades que conté la informació d'un
 * organisme cesionari
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "core_organismo_cesionario")
@EntityListeners(AuditingEntityListener.class)
public class ScspCoreOrganismoCessionarioEntity implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_ces_seq")
	@SequenceGenerator(name = "org_ces_seq", sequenceName = "ID_ORGANISMO_CESIONARIO_SEQ", allocationSize = 1)
	private Long id;
	
	@Column(name = "nombre", length = 50)
	private String nom;
	@Column(name = "cif", length = 50)
	private String cif;
	@Column(name = "fechaalta", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataAlta;
	@Column(name = "fechabaja")
	@Temporal(TemporalType.DATE)
	private Date dataBaixa;
	@Column(name = "bloqueado", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean bloquejat;
	@Column(name = "logo")
	@Type(type="org.hibernate.type.MaterializedBlobType")
	private byte[] logo;
	@Column(name = "codigounidadtramitadora", length = 9)
	public String codiUnitatTramitadora;
	
	public void update(
			String nom,
			String cif,
			Date dataAlta,
			Date dataBaixa,
			boolean bloquejat,
			String codiUnitatTramitadora) {
		this.nom = nom;
		this.cif = cif;
		this.dataAlta = dataAlta;
		this.dataBaixa = dataBaixa;
		this.bloquejat = bloquejat;
		this.codiUnitatTramitadora = codiUnitatTramitadora;
	}

	public static Builder getBuilder(
			String nom,
			String cif,
			Date dataAlta) {
		return new Builder(
				nom,
				cif,
				dataAlta);
	}

	public static class Builder {
		ScspCoreOrganismoCessionarioEntity built;
		Builder(
				String nom,
				String cif,
				Date dataAlta) {
			built = new ScspCoreOrganismoCessionarioEntity();
			built.nom = nom;
			built.cif = cif;
			built.dataAlta = dataAlta;
			built.bloquejat = false;
		}
		public Builder dataBaixa(Date dataBaixa) {
			built.dataBaixa = dataBaixa;
			return this;
		}
		public Builder bloquejat(boolean bloquejat) {
			built.bloquejat = bloquejat;
			return this;
		}
		public Builder codiUnitatTramitadora(String codiUnitatTramitadora) {
			built.codiUnitatTramitadora = codiUnitatTramitadora;
			return this;
		}
		public ScspCoreOrganismoCessionarioEntity build() {
			return built;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScspCoreOrganismoCessionarioEntity other = (ScspCoreOrganismoCessionarioEntity) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -6657066865382086237L;
	
}
