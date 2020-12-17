package es.caib.emiserv.war.command;

import es.caib.emiserv.core.api.dto.AutoritatCertificacioDto;
import es.caib.emiserv.war.helper.ConversioTipusHelper;

public class AutoritzacioCaCommand {
	private Long id;
	private String codca;
	private String nombre;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodca() {
		return codca;
	}
	public void setCodca(String codca) {
		this.codca = codca;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nom) {
		this.nombre = nom;
	}
	
	public static AutoritzacioCaCommand asCommand(AutoritatCertificacioDto dto) {
		return ConversioTipusHelper.getMapperFacade().map(
				dto,
				AutoritzacioCaCommand.class);
	}
	
	public static AutoritatCertificacioDto asDto(AutoritzacioCaCommand command) {
		return ConversioTipusHelper.getMapperFacade().map(
				command,
				AutoritatCertificacioDto.class);
	}
}
