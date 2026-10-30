/**
 * 
 */
package es.caib.emiserv.back.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;

/**
 * Command per a filtrar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AuditoriaFiltreCommand {

	private String procediment;
	private Long servei;
	private String serveiCodi;
	private PeticioEstatEnumDto estat;
	private Date dataInici;
	private Date dataFi;
	private String funcionariNom;
	private String funcionariDocument;
	private String numeroPeticio;

	public String getProcediment() {
		return procediment;
	}
	public void setProcediment(String procediment) {
		this.procediment = procediment;
	}
	public Long getServei() {
		return servei;
	}
	public void setServei(Long servei) {
		this.servei = servei;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
	public PeticioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(PeticioEstatEnumDto estat) {
		this.estat = estat;
	}
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
	public String getFuncionariNom() {
		return funcionariNom;
	}
	public void setFuncionariNom(String funcionariNom) {
		this.funcionariNom = funcionariNom;
	}
	public String getFuncionariDocument() {
		return funcionariDocument;
	}
	public void setFuncionariDocument(String funcionariDocument) {
		this.funcionariDocument = funcionariDocument;
	}
	public String getNumeroPeticio() {
		return numeroPeticio;
	}
	public void setNumeroPeticio(String numeroPeticio) {
		this.numeroPeticio = numeroPeticio;
	}

	public static AuditoriaFiltreCommand toCommand(AuditoriaFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				AuditoriaFiltreCommand.class);
	}
	public static AuditoriaFiltreDto toDto(AuditoriaFiltreCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				AuditoriaFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
