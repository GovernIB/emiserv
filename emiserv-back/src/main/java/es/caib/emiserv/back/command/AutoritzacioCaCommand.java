/**
 * 
 */
package es.caib.emiserv.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import es.caib.emiserv.back.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.intf.dto.AutoritatCertificacioDto;

/**
 * Command pel formulari d'autoritzacions CA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AutoritzacioCaCommand {

	private Long id;
	@NotEmpty @Size(max=256)
	private String codca;
	@NotEmpty @Size(max=256)
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
