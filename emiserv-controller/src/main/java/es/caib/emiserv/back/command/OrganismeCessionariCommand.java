/**
 * 
 */
package es.caib.emiserv.back.command;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.back.validation.CifOrganismeCessionariNoRepetit;
import es.caib.emiserv.logic.intf.dto.OrganismeDto;

/**
 * Command pel formulari d'organismes cessionaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CifOrganismeCessionariNoRepetit(campId = "id", campCif = "cif")
public class OrganismeCessionariCommand {

	public Long id;
	@NotEmpty @Size(max = 64)
	public String nom;
	@NotEmpty @Size(max = 16)
	public String cif;
	@NotNull
	public Date dataAlta;
	public Date dataBaixa;
	private boolean bloquejat = false;
	@Size(max = 9)
	private String codiUnitatTramitadora;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
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
	public boolean isBloquejat() {
		return bloquejat;
	}
	public void setBloquejat(boolean bloquejat) {
		this.bloquejat = bloquejat;
	}
	public String getCodiUnitatTramitadora() {
		return codiUnitatTramitadora;
	}
	public void setCodiUnitatTramitadora(String codiUnitatTramitadora) {
		this.codiUnitatTramitadora = codiUnitatTramitadora;
	}

	public static OrganismeCessionariCommand toCommand(OrganismeDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				OrganismeCessionariCommand.class);
	}
	public static OrganismeDto toDto(OrganismeCessionariCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				OrganismeDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
