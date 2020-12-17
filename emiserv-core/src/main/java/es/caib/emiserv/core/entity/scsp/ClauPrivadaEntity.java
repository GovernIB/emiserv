/**
 * 
 */
package es.caib.emiserv.core.entity.scsp;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Classe de model de dades que conté la informació d'una
 * clau privada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@SuppressWarnings("deprecation")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "core_clave_privada")
@EntityListeners(AuditingEntityListener.class)
public class ClauPrivadaEntity implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "alias", length = 256, nullable = false)
	private String alies;
	
	@Column(name = "nombre", length = 256, nullable = false)
	private String nom;
	
	@Column(name = "password", length = 256, nullable = false)
	private String password;
	
	@Column(name = "numeroserie", length = 256, nullable = false)
	private String numSerie;
	
	@Column(name = "fechabaja")
	@Temporal(TemporalType.DATE)
	private Date dataBaixa;
	
	@Column(name = "fechaalta")
	@Temporal(TemporalType.DATE)
	private Date dataAlta;
	
	@Column(name = "interoperabilidad")
	private boolean interoperabilitat;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "organismo")
	@ForeignKey(name = "clave_privada_org")
	private OrganismeCessionariEntity organisme;
	
	
	public void update(
			String alies,
			String nom,
			String password,
			String numSerie,
			Date dataBaixa,
			Date dataAlta,
			boolean interoperabilitat,
			OrganismeCessionariEntity organisme) {
		this.alies = alies;
		this.nom = nom;
		this.password = password;
		this.numSerie = numSerie;
		this.dataBaixa = dataBaixa;
		this.dataAlta = dataAlta;
		this.interoperabilitat = interoperabilitat;
		this.organisme = organisme;
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
		ClauPrivadaEntity other = (ClauPrivadaEntity) obj;
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
