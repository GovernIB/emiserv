/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una petició al backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficePeticioDto implements Serializable {

	private Long id;
	private PeticioEstatEnumDto estat;
	private Date tempsEstimatRespostaData;
	private String darreraSolicitudId;
	private Date darreraSolicitudData;
	private int processadesTotal;
	private int processadesError;
	private Long comunicacioDarreraId;
	private List<BackofficeSolicitudDto> solicituds;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public PeticioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(PeticioEstatEnumDto estat) {
		this.estat = estat;
	}
	public Date getTempsEstimatRespostaData() {
		return tempsEstimatRespostaData;
	}
	public void setTempsEstimatRespostaData(Date tempsEstimatRespostaData) {
		this.tempsEstimatRespostaData = tempsEstimatRespostaData;
	}
	public String getDarreraSolicitudId() {
		return darreraSolicitudId;
	}
	public void setDarreraSolicitudId(String darreraSolicitudId) {
		this.darreraSolicitudId = darreraSolicitudId;
	}
	public Date getDarreraSolicitudData() {
		return darreraSolicitudData;
	}
	public void setDarreraSolicitudData(Date darreraSolicitudData) {
		this.darreraSolicitudData = darreraSolicitudData;
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
	public Long getComunicacioDarreraId() {
		return comunicacioDarreraId;
	}
	public void setComunicacioDarreraId(Long comunicacioDarreraId) {
		this.comunicacioDarreraId = comunicacioDarreraId;
	}
	public List<BackofficeSolicitudDto> getSolicituds() {
		return solicituds;
	}
	public void setSolicituds(List<BackofficeSolicitudDto> solicituds) {
		this.solicituds = solicituds;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
