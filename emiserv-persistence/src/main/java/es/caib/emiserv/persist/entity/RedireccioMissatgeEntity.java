/**
 * 
 */
package es.caib.emiserv.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Classe del model de dades que representa un missatge d'una
 * petició d'una redirecció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = AbstractAuditableEntity.TABLE_PREFIX + "_redir_missatge")
@EntityListeners(AuditingEntityListener.class)
public class RedireccioMissatgeEntity extends AbstractPersistable<Long> {

	public static final int TIPUS_PETICION = 0;
	public static final int TIPUS_CONFIRMACION_PETICION = 1;
	public static final int TIPUS_SOLICITUD_RESPUESTA = 2;
	public static final int TIPUS_RESPUESTA = 3;
	public static final int TIPUS_FAULT = 4;
	public static final int TIPUS_FAULT_LOCAL = 5;

	@Column(name = "tipus", nullable = false)
	private int tipus;
	@Lob
	@Column(name = "xml")
	private String xml;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "peticio_id",
			foreignKey = @ForeignKey(name = "ems_redmsg_peticio_fk"))
	private RedireccioPeticioEntity peticio;
	@Version
	private long version = 0;



	public int getTipus() {
		return tipus;
	}
	public String getXml() {
		return xml;
	}
	public RedireccioPeticioEntity getPeticio() {
		return peticio;
	}

	public static Builder getBuilder(
			RedireccioPeticioEntity peticio,
			int tipus,
			String xml) {
		return new Builder(
				peticio,
				tipus,
				xml);
	}

	public static class Builder {
		RedireccioMissatgeEntity built;
		Builder(
				RedireccioPeticioEntity peticio,
				int tipus,
				String xml) {
			built = new RedireccioMissatgeEntity();
			built.peticio = peticio;
			built.tipus = tipus;
			built.xml = xml;
		}
		public RedireccioMissatgeEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((peticio == null) ? 0 : peticio.hashCode());
		result = prime * result + tipus;
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
		RedireccioMissatgeEntity other = (RedireccioMissatgeEntity) obj;
		if (peticio == null) {
			if (other.peticio != null)
				return false;
		} else if (!peticio.equals(other.peticio))
			return false;
		if (tipus != other.tipus)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RedireccioMissatgeEntity [peticio=" + peticio + ", tipus=" + tipus + "]";
	}

}
