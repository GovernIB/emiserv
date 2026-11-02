package es.caib.emiserv.persist.entity;

import es.caib.emiserv.logic.intf.dto.InterficieUsuariEnumDto;
import es.caib.emiserv.logic.intf.dto.TemaAplicacioEnumDto;
import lombok.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Classe de model de dades que conté la informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Entity
@Table(name = AbstractAuditableEntity.TABLE_PREFIX + "_usuari")
public class UsuariEntity implements Serializable {

	@Id
	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name="idioma", length = 2)
	private String idioma;
	@Enumerated(EnumType.STRING)
	@Column(name="interficie", length = 5)
	private InterficieUsuariEnumDto interficie;
	/** Tema de la interfície REACT (null: el del sistema). */
	@Enumerated(EnumType.STRING)
	@Column(name="tema", length = 10)
	private TemaAplicacioEnumDto tema;
	@Version
	private long version = 0;

	public void update(String idioma) {
		this.idioma = idioma;
	}

	public void updateTema(TemaAplicacioEnumDto tema) {
		this.tema = tema;
	}

	public void updateInterficie(InterficieUsuariEnumDto interficie) {
		this.interficie = interficie;
	}

	public static Builder getBuilder(String codi) {
		return new Builder(codi);
	}

	public static class Builder {
		UsuariEntity built;
		Builder(String codi) {
			built = new UsuariEntity();
			built.codi = codi;
		}
		public Builder idioma(String idioma) {
			built.idioma = idioma;
			return this;
		}
		public Builder interficie(InterficieUsuariEnumDto interficie) {
			built.interficie = interficie;
			return this;
		}
		public Builder tema(TemaAplicacioEnumDto tema) {
			built.tema = tema;
			return this;
		}
		public UsuariEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		UsuariEntity other = (UsuariEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UsuariEntity [codi=" + codi + ", idioma=" + idioma + ", interficie=" + interficie + "]";
	}

	private static final long serialVersionUID = -6657066865382086237L;

}
