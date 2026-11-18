package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.helper.ConversioTipusHelper;
import es.caib.emiserv.logic.helper.SecurityHelper;
import es.caib.emiserv.logic.intf.dto.ServeiDto;
import es.caib.emiserv.logic.intf.dto.ServeiFakeResultatEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import es.caib.emiserv.persist.entity.ServeiEntity;
import es.caib.emiserv.persist.repository.ServeiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Mode fake dels serveis: desat del resultat i de la plantilla de DatosEspecificos.
 */
class ServeiServiceImplFakeTest {

	private static final long SERVEI_ID = 10L;

	private ServeiServiceImpl serveiService;
	private ServeiEntity servei;

	@BeforeEach
	void setUp() {
		serveiService = new ServeiServiceImpl();
		ServeiRepository serveiRepository = mock(ServeiRepository.class);
		ReflectionTestUtils.setField(serveiService, "serveiRepository", serveiRepository);
		SecurityHelper securityHelper = mock(SecurityHelper.class);
		when(securityHelper.hasRole("EMS_ADMIN")).thenReturn(true);
		ReflectionTestUtils.setField(serveiService, "securityHelper", securityHelper);
		ReflectionTestUtils.setField(serveiService, "conversioTipusHelper", mock(ConversioTipusHelper.class));
		servei = ServeiEntity.getBuilder("SCDCPAJU", "Padró", ServeiTipusEnumDto.ENRUTADOR).
				resolverClass("es.Resolver").
				build();
		ReflectionTestUtils.setField(servei, "id", SERVEI_ID);
		when(serveiRepository.findById(SERVEI_ID)).thenReturn(Optional.of(servei));
	}

	private ServeiDto dto(boolean fake, ServeiFakeResultatEnumDto resultat, String plantilla) {
		ServeiDto dto = new ServeiDto();
		dto.setId(SERVEI_ID);
		dto.setCodi("SCDCPAJU");
		dto.setNom("Padró");
		dto.setTipus(ServeiTipusEnumDto.ENRUTADOR);
		dto.setResolverClass("es.Resolver");
		dto.setFake(fake);
		dto.setFakeResultat(resultat);
		dto.setFakeDadesEspecifiques(plantilla);
		return dto;
	}

	@Test
	void modeFakeSenseResultatDesaOk() {
		serveiService.update(dto(true, null, null));
		assertTrue(servei.isFake());
		assertEquals(ServeiFakeResultatEnumDto.OK, servei.getFakeResultat());
	}

	@Test
	void desactivarFakeConservaLaPlantilla() {
		String plantilla = "<DatosEspecificos><A>${x}</A></DatosEspecificos>";
		serveiService.update(dto(true, ServeiFakeResultatEnumDto.ERROR, plantilla));
		serveiService.update(dto(false, null, null));
		assertFalse(servei.isFake());
		assertEquals(ServeiFakeResultatEnumDto.ERROR, servei.getFakeResultat());
		assertEquals(plantilla, servei.getFakeDadesEspecifiques());
	}

	@Test
	void plantillaMalFormadaEsRebutja() {
		ValidationException ex = assertThrows(ValidationException.class, () ->
				serveiService.update(dto(true, ServeiFakeResultatEnumDto.OK, "<DatosEspecificos>")));
		assertEquals("La plantilla de dades específiques no és un XML DatosEspecificos vàlid", ex.getMessage());
		assertFalse(servei.isFake());
	}

	@Test
	void plantillaAmbAltreElementArrelEsRebutja() {
		assertThrows(ValidationException.class, () ->
				serveiService.update(dto(true, ServeiFakeResultatEnumDto.OK, "<Altre/>")));
	}

	@Test
	void plantillaAmbPrefixDeNamespaceEsAcceptada() {
		serveiService.update(dto(true, ServeiFakeResultatEnumDto.OK,
				"<ns:DatosEspecificos xmlns:ns=\"http://x\"><ns:A>${x}</ns:A></ns:DatosEspecificos>"));
		assertTrue(servei.isFake());
	}
}
