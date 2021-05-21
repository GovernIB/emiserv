/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una petició per auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AuditoriaPeticioDto implements Serializable {

	private Long id;
	private String peticioId;
	private String serveiCodi;
	private String serveiDescripcio;
	private PeticioEstatEnumDto estat;
	private String estatScsp;
	private Date dataPeticio;
	private Date dataResposta;
	private String error;
	private boolean sincrona;
	private Date ter;
	private int numEnviaments;
	private int numTransmissions;
	private Date dataDarreraComprovacio;
	private boolean comunicacioBackofficeDisponible;
	private boolean comunicacioBackofficeError;
	private int processadesTotal;
	private int processadesError;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPeticioId() {
		return peticioId;
	}
	public void setPeticioId(String peticioId) {
		this.peticioId = peticioId;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
	public String getServeiDescripcio() {
		return serveiDescripcio;
	}
	public void setServeiDescripcio(String serveiDescripcio) {
		this.serveiDescripcio = serveiDescripcio;
	}
	public PeticioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(PeticioEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getEstatScsp() {
		return estatScsp;
	}
	public void setEstatScsp(String estatScsp) {
		this.estatScsp = estatScsp;
	}
	public Date getDataPeticio() {
		return dataPeticio;
	}
	public void setDataPeticio(Date dataPeticio) {
		this.dataPeticio = dataPeticio;
	}
	public Date getDataResposta() {
		return dataResposta;
	}
	public void setDataResposta(Date dataResposta) {
		this.dataResposta = dataResposta;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public boolean isSincrona() {
		return sincrona;
	}
	public void setSincrona(boolean sincrona) {
		this.sincrona = sincrona;
	}
	public Date getTer() {
		return ter;
	}
	public void setTer(Date ter) {
		this.ter = ter;
	}
	public int getNumEnviaments() {
		return numEnviaments;
	}
	public void setNumEnviaments(int numEnviaments) {
		this.numEnviaments = numEnviaments;
	}
	public int getNumTransmissions() {
		return numTransmissions;
	}
	public void setNumTransmissions(int numTransmissions) {
		this.numTransmissions = numTransmissions;
	}
	public Date getDataDarreraComprovacio() {
		return dataDarreraComprovacio;
	}
	public void setDataDarreraComprovacio(Date dataDarreraComprovacio) {
		this.dataDarreraComprovacio = dataDarreraComprovacio;
	}
	public boolean isComunicacioBackofficeDisponible() {
		return comunicacioBackofficeDisponible;
	}
	public void setComunicacioBackofficeDisponible(boolean comunicacioBackofficeDisponible) {
		this.comunicacioBackofficeDisponible = comunicacioBackofficeDisponible;
	}
	public boolean isComunicacioBackofficeError() {
		return comunicacioBackofficeError;
	}
	public void setComunicacioBackofficeError(boolean comunicacioBackofficeError) {
		this.comunicacioBackofficeError = comunicacioBackofficeError;
	}
	public int getProcessadesTotal() {
		return processadesTotal;
	}
	public void setProcessadesTotal(int processadesTotal) {
		this.processadesTotal = processadesTotal;
	}
	public int getProcessadesError() {
		return processadesError;
	}
	public void setProcessadesError(int processadesError) {
		this.processadesError = processadesError;
	}

	public int getProcessadesPercent() {
		if (numTransmissions == 0)
			return 100;
		return processadesTotal * 100 / numTransmissions;
	}

	public boolean isEstatScspError() {
		if (estatScsp == null) {
			return false;
		}
		return !estatScsp.startsWith("00");
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
