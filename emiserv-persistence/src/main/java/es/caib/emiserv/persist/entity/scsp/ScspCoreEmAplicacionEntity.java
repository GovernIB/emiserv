/**
 * 
 */
package es.caib.emiserv.persist.entity.scsp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * Classe de model de dades per a la taula CORE_EM_APLICACION.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@EqualsAndHashCode
@Entity
@Table(name = "core_em_aplicacion")
public class ScspCoreEmAplicacionEntity {

	@Id
	@GenericGenerator(
			name = "sequence_scsp_apl",
			strategy = "native",
			parameters = {
					@org.hibernate.annotations.Parameter(
							name = "sequence_name",
							value = "ID_APLICACION_SEQUENCE")
			})
	@GeneratedValue(generator = "sequence_scsp_apl")
	@Column(name = "idaplicacion")
	private Integer idAplicacion;
	
	@Column(name = "nifcertificado", length = 16)
	private String nifCertificado;
	
	@Column(name = "numeroserie", length = 64, nullable = false)
	private String numeroSerie;
	
	@Column(length = 512)
	private String cn;

	@Column(name = "tiempocomprobacion")
	private Date tiempoComprobacion;

	@ManyToOne
	@JoinColumn(
			name = "autoridadcertif",
			foreignKey = @ForeignKey(name = "apli_codca"),
			nullable = false)
	private ScspCoreEmAutorizacionAutoridadCertEntity autoridadCertificacion;
	
	@Column(name = "fechaalta")
	private Date fechaAlta;
	
	@Column(name = "fechabaja")
	private Date fechaBaja;

	public void update(
			String nifCertificado,
			String numeroSerie,
			String cn,
			ScspCoreEmAutorizacionAutoridadCertEntity autoridadCertificacion,
			Date fechaAlta,
			Date fechaBaja) {
		this.nifCertificado = nifCertificado;
		this.numeroSerie = numeroSerie;
		this.cn = cn;
		this.autoridadCertificacion = autoridadCertificacion;
		this.fechaAlta = fechaAlta;
		this.fechaBaja = fechaBaja;
	}

	public static Builder getBuilder(
			String nifCertificado,
			String numeroSerie,
			String cn,
			ScspCoreEmAutorizacionAutoridadCertEntity autoridadCertificacion) {
		return new Builder(
				nifCertificado,
				numeroSerie,
				cn,
				autoridadCertificacion);
	}

	public static class Builder {
		ScspCoreEmAplicacionEntity built;
		Builder(
				String nifCertificado,
				String numeroSerie,
				String cn,
				ScspCoreEmAutorizacionAutoridadCertEntity autoridadCertificacion) {
			built = new ScspCoreEmAplicacionEntity();
			built.nifCertificado = nifCertificado;
			built.numeroSerie = numeroSerie;
			built.cn = cn;
			built.autoridadCertificacion = autoridadCertificacion;
		}
		public Builder fechaAlta(Date fechaAlta) {
			built.fechaAlta = fechaAlta;
			return this;
		}
		public Builder fechaBaja(Date fechaBaja) {
			built.fechaBaja = fechaBaja;
			return this;
		}
		public ScspCoreEmAplicacionEntity build() {
			return built;
		}
	}

}
