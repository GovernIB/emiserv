/**
 * 
 */
package es.caib.emiserv.back.command;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.intf.dto.AplicacioDto;

/**
 * Command pel formulari d'aplicacions de les autoritzacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioCommand {

	private Integer id;
	@NotEmpty @Size(max = 16)
	private String certificatNif;
	@NotEmpty @Size(max = 512)
	private String cn;
	@NotEmpty @Size(max = 64)
	private String numeroSerie;
	@NotEmpty @Size(max = 512)
	private String autoridadCertifId;
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
	public String getAutoridadCertifId() {
		return autoridadCertifId;
	}
	
	public void setAutoridadCertifId(String autoridadCertifId) {
		this.autoridadCertifId = autoridadCertifId;
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

	public static AplicacioCommand toCommand(AplicacioDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				AplicacioCommand.class);
	}
	public static AplicacioDto toDto(AplicacioCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				AplicacioDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
