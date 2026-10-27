package es.caib.emiserv.persist.entity;

import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiRutaDestiTipusEnumDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tipus de destí de les rutes dels serveis enrutadors (EMISERV o BACKOFFICE).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class ServeiRutaDestiEntityTest {

	private static final String URL_EMISOR = "https://cedent.es/emiserv/services/peticionSincrona";
	private static final String URL_BACKOFFICE = "https://cedent.es/backoffice/ws/EmiservBackoffice";

	@Test
	void perDefecteEsDestiEmiserv() {
		ServeiRutaDestiEntity ruta = ServeiRutaDestiEntity.getBuilder(null, "7040", URL_EMISOR, 1L).build();

		assertEquals(ServeiRutaDestiTipusEnumDto.EMISERV, ruta.getDestiTipus());
		assertFalse(ruta.isDestiBackoffice());
		assertNull(ruta.getBackofficeAutenticacio());
	}

	@Test
	void creacioSenseTipusEsDestiEmiserv() {
		ServeiRutaDestiEntity ruta = ServeiRutaDestiEntity.getBuilder(null, "7040", URL_EMISOR, 1L).
				desti(null, BackofficeAutenticacioTipusEnumDto.TEXT_CLAR).
				build();

		assertEquals(ServeiRutaDestiTipusEnumDto.EMISERV, ruta.getDestiTipus());
		assertNull(ruta.getBackofficeAutenticacio());
	}

	@Test
	void destiBackofficeSenseAutenticacio() {
		ServeiRutaDestiEntity ruta = ServeiRutaDestiEntity.getBuilder(null, "7040", URL_BACKOFFICE, 1L).
				desti(ServeiRutaDestiTipusEnumDto.BACKOFFICE, null).
				build();

		assertTrue(ruta.isDestiBackoffice());
		assertEquals(BackofficeAutenticacioTipusEnumDto.NINGUNA, ruta.getBackofficeAutenticacio());
	}

	@Test
	void modificacioSenseTipusMantéElDesti() {
		ServeiRutaDestiEntity ruta = ServeiRutaDestiEntity.getBuilder(null, "7040", URL_BACKOFFICE, 1L).
				desti(ServeiRutaDestiTipusEnumDto.BACKOFFICE, BackofficeAutenticacioTipusEnumDto.TOKEN_CAIB).
				build();

		// El formulari de rutes anterior no informa el tipus de destí
		ruta.update("7041", URL_BACKOFFICE, null, null);

		assertEquals("7041", ruta.getEntitatCodi());
		assertTrue(ruta.isDestiBackoffice());
		assertEquals(BackofficeAutenticacioTipusEnumDto.TOKEN_CAIB, ruta.getBackofficeAutenticacio());
	}

	@Test
	void canviDeBackofficeAEmiserv() {
		ServeiRutaDestiEntity ruta = ServeiRutaDestiEntity.getBuilder(null, "7040", URL_BACKOFFICE, 1L).
				desti(ServeiRutaDestiTipusEnumDto.BACKOFFICE, BackofficeAutenticacioTipusEnumDto.TEXT_CLAR).
				build();

		ruta.update("7040", URL_EMISOR, ServeiRutaDestiTipusEnumDto.EMISERV, BackofficeAutenticacioTipusEnumDto.TEXT_CLAR);

		assertFalse(ruta.isDestiBackoffice());
		assertEquals(URL_EMISOR, ruta.getUrl());
		assertNull(ruta.getBackofficeAutenticacio());
	}

}
