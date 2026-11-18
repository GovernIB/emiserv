package es.caib.emiserv.back.command;

import es.caib.emiserv.logic.intf.dto.ServeiDto;
import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprova que el formulari JSP porta els camps del mode fake fins al DTO (i viceversa): com que
 * l'actualització aplica sempre els camps fake del DTO, perdre'ls desactivaria el mode fake.
 */
public class ServeiCommandTest {

	@Test
	public void conservaElsCampsFakeEntreCommandIDto() {
		ServeiDto dto = new ServeiDto();
		dto.setCodi("S1");
		dto.setNom("Servei");
		dto.setTipus(ServeiTipusEnumDto.BACKOFFICE);
		dto.setFake(true);
		dto.setFakeResultat(ServeiFakeResultatEnumDto.SENSE_DADES);
		dto.setFakeDadesEspecifiques("<DatosEspecificos/>");

		ServeiCommand command = ServeiCommand.toCommand(dto);
		assertTrue(command.isFake());
		assertEquals(ServeiFakeResultatEnumDto.SENSE_DADES, command.getFakeResultat());
		assertEquals("<DatosEspecificos/>", command.getFakeDadesEspecifiques());

		ServeiDto retorn = ServeiCommand.toDto(command);
		assertTrue(retorn.isFake());
		assertEquals(ServeiFakeResultatEnumDto.SENSE_DADES, retorn.getFakeResultat());
		assertEquals("<DatosEspecificos/>", retorn.getFakeDadesEspecifiques());
	}

}
