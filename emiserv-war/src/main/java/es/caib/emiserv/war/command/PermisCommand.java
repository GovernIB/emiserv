/**
 * 
 */
package es.caib.emiserv.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.emiserv.core.api.dto.PermisDto;
import es.caib.emiserv.core.api.dto.PrincipalTipusEnumDto;
import es.caib.emiserv.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String principalNom;
	@NotNull
	private PrincipalTipusEnumDto principalTipus;
	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPrincipalNom() {
		return principalNom;
	}
	public void setPrincipalNom(String principalNom) {
		this.principalNom = principalNom;
	}
	public PrincipalTipusEnumDto getPrincipalTipus() {
		return principalTipus;
	}
	public void setPrincipalTipus(PrincipalTipusEnumDto principalTipus) {
		this.principalTipus = principalTipus;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public boolean isWrite() {
		return write;
	}
	public void setWrite(boolean write) {
		this.write = write;
	}
	public boolean isCreate() {
		return create;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public boolean isAdministration() {
		return administration;
	}
	public void setAdministration(boolean administration) {
		this.administration = administration;
	}

	public static PermisCommand toCommand(PermisDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				PermisCommand.class);
	}
	public static PermisDto toDto(PermisCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				PermisDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
