/**
 * 
 */
package es.caib.emiserv.persist.entity.scsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 *  Classe de model de dades per a la taula CORE_EMISOR_CERTIFICADO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "core_emisor_certificado")
public class ScspCoreEmisorCertificadoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emisor_sequence")
	@SequenceGenerator(name = "emisor_sequence", sequenceName = "id_emisor_sequence", allocationSize = 1)
	private long id;
	@Column(name = "nombre", length = 50, nullable = false)
	private String nombre;
	@Column(name = "cif", length = 16, nullable = false)
	private String cif;
	@Temporal(TemporalType.DATE)
	@Column(name = "fechaalta", nullable = true)
	private Date fechaAlta;
	@Temporal(TemporalType.DATE)
	@Column(name = "fechabaja", nullable = true)
	private Date fechaBaja;

	public void update(
			String nombre,
			String cif,
			Date fechaAlta,
			Date fechaBaja) {
		this.nombre = nombre;
		this.cif = cif;
		this.fechaAlta = fechaAlta;
		this.fechaBaja = fechaBaja;
	}
}
