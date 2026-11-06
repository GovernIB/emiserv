package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.dto.AuditoriaFiltreDto;
import es.caib.emiserv.logic.intf.dto.PaginacioParamsDto;
import es.caib.emiserv.logic.intf.dto.PeticioEstatEnumDto;
import es.caib.emiserv.logic.intf.exception.OrdenacioNoValidaException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Proves de la traducció del filtre i de l'ordenació de la graella d'auditoria a la consulta de la JSP.
 */
public class AuditoriaResourceHelperTest {

	private final AuditoriaResourceHelper helper = new AuditoriaResourceHelper();

	@Test
	public void filtreAmbTotsElsCamps() throws Exception {
		AuditoriaFiltreDto filtre = helper.toFiltre(
				"servei:'12' and procediment:'PRO\\'1' and estat:'ERROR' and numeroPeticio:'PET-1' and " +
				"dataPeticio>:'2026-10-01T00:00:00' and dataPeticio<:'2026-10-30T00:00:00'",
				null);
		assertEquals(12L, filtre.getServei());
		assertEquals("PRO'1", filtre.getProcediment());
		assertEquals(PeticioEstatEnumDto.ERROR, filtre.getEstat());
		assertEquals("PET-1", filtre.getNumeroPeticio());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		assertEquals("2026-10-01 00:00:00.000", sdf.format(filtre.getDataInici()));
		// La data final inclou tot el dia
		assertEquals("2026-10-30 23:59:59.999", sdf.format(filtre.getDataFi()));
	}

	@Test
	public void filtreRapidCercaPelNumeroDePeticio() {
		AuditoriaFiltreDto filtre = helper.toFiltre("serveiCodi:'SVDX'", " PET-2 ");
		assertEquals("SVDX", filtre.getServeiCodi());
		assertEquals("PET-2", filtre.getNumeroPeticio());
		assertNull(helper.toFiltre(null, "").getNumeroPeticio());
	}

	@Test
	public void filtreAmbCampOValorDesconegutEsRebutja() {
		assertThrows(ValidationException.class, () -> helper.toFiltre("codi:'x'", null));
		assertThrows(ValidationException.class, () -> helper.toFiltre("estat:'INVENTAT'", null));
		assertThrows(ValidationException.class, () -> helper.toFiltre("dataPeticio>:'ahir'", null));
	}

	@Test
	public void paginacioAmbOrdenacio() {
		PaginacioParamsDto paginacio = helper.toPaginacio(PageRequest.of(2, 20, Sort.by("serveiCodi").ascending()), 100);
		assertEquals(2, paginacio.getPaginaNum());
		assertEquals(20, paginacio.getPaginaTamany());
		assertEquals("serveiCodi", paginacio.getOrdres().get(0).getCamp());
		assertEquals(PaginacioParamsDto.OrdreDireccioDto.ASCENDENT, paginacio.getOrdres().get(0).getDireccio());
	}

	@Test
	public void paginacioPerDefecteIOrdenacioNoValida() {
		PaginacioParamsDto paginacio = helper.toPaginacio(null, 100);
		assertEquals(100, paginacio.getPaginaTamany());
		assertEquals("dataPeticio", paginacio.getOrdres().get(0).getCamp());
		assertEquals(PaginacioParamsDto.OrdreDireccioDto.DESCENDENT, paginacio.getOrdres().get(0).getDireccio());
		assertThrows(OrdenacioNoValidaException.class,
				() -> helper.toPaginacio(PageRequest.of(0, 10, Sort.by("error")), 100));
	}

}
