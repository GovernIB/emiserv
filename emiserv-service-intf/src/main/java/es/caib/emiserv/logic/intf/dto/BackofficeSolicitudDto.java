/**
 * 
 */
package es.caib.emiserv.logic.intf.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una sol·licitud al backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeSolicitudDto implements Serializable {

	private Long id;
	private String solicitudId;
	private BackofficeSolicitudEstatEnumDto estat;
	private Long comunicacioDarreraId;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSolicitudId() {
		return solicitudId;
	}
	public void setSolicitudId(String solicitudId) {
		this.solicitudId = solicitudId;
	}
	public Long getComunicacioDarreraId() {
		return comunicacioDarreraId;
	}
	public void setComunicacioDarreraId(Long comunicacioDarreraId) {
		this.comunicacioDarreraId = comunicacioDarreraId;
	}
	public BackofficeSolicitudEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(BackofficeSolicitudEstatEnumDto estat) {
		this.estat = estat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
