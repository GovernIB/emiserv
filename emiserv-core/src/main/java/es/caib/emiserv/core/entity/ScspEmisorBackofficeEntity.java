/**
 * 
 */
package es.caib.emiserv.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_EM_BACKOFFICE.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_em_backoffice")
public class ScspEmisorBackofficeEntity {

	@Id
	@Column(name = "certificado", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="servicio_seq_gen")
	@SequenceGenerator(name="servicio_seq_gen", sequenceName="id_servicio_sequence")
	private Long certificado;
	@Column(name = "beanname", length = 256)
	private String beanName;
	@Column(name = "classname", length = 256)
	private String className;
	@Column(name = "ter", nullable = false)
	private Integer ter;



	public Long getCertificado() {
		return certificado;
	}
	public String getBeanName() {
		return beanName;
	}
	public String getClassName() {
		return className;
	}
	public Integer getTer() {
		return ter;
	}

	public void update(
			String className,
			Integer ter) {
		this.className = className;
		this.ter = ter;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus Usuari.
	 * 
	 * @param certificado
	 *            El certificado.
	 * @param className
	 *            El className.
	 * @param ter
	 *            El tiempo estimado de respuesta.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			Long certificado,
			String className,
			Integer ter) {
		return new Builder(
				certificado,
				className,
				ter);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta entitat.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		ScspEmisorBackofficeEntity built;
		Builder(Long certificado,
				String className,
				Integer ter) {
			built = new ScspEmisorBackofficeEntity();
			built.certificado = certificado;
			built.className = className;
			built.ter = ter;
		}
		public ScspEmisorBackofficeEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((certificado == null) ? 0 : certificado.hashCode());
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
		ScspEmisorBackofficeEntity other = (ScspEmisorBackofficeEntity) obj;
		if (certificado == null) {
			if (other.certificado != null)
				return false;
		} else if (!certificado.equals(other.certificado))
			return false;
		return true;
	}

}
