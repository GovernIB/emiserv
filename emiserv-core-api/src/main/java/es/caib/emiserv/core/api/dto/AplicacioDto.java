/**
 * 
 */
package es.caib.emiserv.core.api.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una aplicació per a autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioDto {

	private Integer id;
	private String certificatNif;
	private String cn;
	private String numeroSerie;
	private Date darreraComprovacio;
	private String codiCa;
	private Date dataAlta;
	private Date dataBaixa;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCertificatNif() {
		return certificatNif;
	}
	public void setCertificatNif(String certificatNif) {
		this.certificatNif = certificatNif;
	}
	public String getCn() {
		return cn;
	}
	public void setCn(String cn) {
		this.cn = cn;
	}
	public String getNumeroSerie() {
		return numeroSerie;
	}
	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}
	public Date getDarreraComprovacio() {
		return darreraComprovacio;
	}
	public void setDarreraComprovacio(Date darreraComprovacio) {
		this.darreraComprovacio = darreraComprovacio;
	}
	public String getCodiCa() {
		return codiCa;
	}
	public void setCodiCa(String codiCa) {
		this.codiCa = codiCa;
	}
	public Date getDataAlta() {
		return dataAlta;
	}
	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}
	public Date getDataBaixa() {
		return dataBaixa;
	}
	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
