/**
 * 
 */
package es.caib.emiserv.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *  Classe de model de dades per a la taula CORE_EMISOR_CERTIFICADO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "core_emisor_certificado")
public class ScspCoreEmisorCertificadoEntity {

	@Id
	private long id;
	@Column(name = "nombre", length = 64, nullable = false)
	private String nombre;
	@Column(name = "cif", length = 512, nullable = false)
	private String cif;
	@Temporal(TemporalType.DATE)
	@Column(name = "fechabaja", nullable = false)
	private Date fechaBaja;

	public long getId() {
		return id;
	}
	public String getNombre() {
		return nombre;
	}
	public String getCif() {
		return cif;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cif == null) ? 0 : cif.hashCode());
		result = prime * result + ((fechaBaja == null) ? 0 : fechaBaja.hashCode());
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
		ScspCoreEmisorCertificadoEntity other = (ScspCoreEmisorCertificadoEntity) obj;
		if (cif == null) {
			if (other.cif != null)
				return false;
		} else if (!cif.equals(other.cif))
			return false;
		if (fechaBaja == null) {
			if (other.fechaBaja != null)
				return false;
		} else if (!fechaBaja.equals(other.fechaBaja))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}

}
