/**
 * 
 */
package es.caib.emiserv.core.entity.scsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public class OrganismeCessionariEntity implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "nombre", length = 50)
	private String nom;
	@Column(name = "cif", length = 50)
	private String cif;
	@Column(name = "fechabaja")
	@Temporal(TemporalType.DATE)
	private Date dataBaixa;
	@Column(name = "fechaalta")
	@Temporal(TemporalType.DATE)
	private Date dataAlta;
	@Column(name = "bloqueado", nullable = false)
	private Boolean bloquejat;
	@Column(name = "logo")
	private byte[] logo;
	
	@OneToMany(
			mappedBy = "organisme",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<ClauPrivadaEntity> claus = new ArrayList<ClauPrivadaEntity>();
	
	public void update(
			String nom,
			String cif,
			Date dataBaixa,
			Date dataAlta,
			Boolean bloquejat,
			byte[] logo,
			List<ClauPrivadaEntity> claus) {
		this.nom = nom;
		this.cif = cif;
		this.dataBaixa = dataBaixa;
		this.dataAlta = dataAlta;
		this.bloquejat = bloquejat;
		this.logo = logo;
		this.claus = claus;
	}
	
	public void updateEntitat(
			String nom,
			String cif,
			Boolean bloquejat) {
		this.nom = nom;
		this.cif = cif;
		this.bloquejat = bloquejat;
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
		OrganismeCessionariEntity other = (OrganismeCessionariEntity) obj;
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
