/**
 * 
 */
package es.caib.emiserv.back.command;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;

/**
 * Command per a la generació de l'informe.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeCommand {
	
	@NotNull
	private Date dataInici;
	@NotNull
	private Date dataFi;
	
	private ServeiTipusEnumDto tipusPeticio;

	public Date getDataInici() {
		return dataInici;
	}

	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	public Date getDataFi() {
		return dataFi;
	}

	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	public ServeiTipusEnumDto getTipusPeticio() {
		return tipusPeticio;
	}

	public void setTipusPeticio(ServeiTipusEnumDto tipusPeticio) {
		this.tipusPeticio = tipusPeticio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
