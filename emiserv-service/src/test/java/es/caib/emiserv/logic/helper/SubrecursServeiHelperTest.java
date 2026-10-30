package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Servei dels subrecursos (rutes, permisos i autoritzacions) a partir del filtre de la graella.
 */
class SubrecursServeiHelperTest {

	private final SubrecursServeiHelper helper = new SubrecursServeiHelper();

	@Test
	void llegeixElServeiDelFiltre() {
		assertEquals(5L, SubrecursServeiHelper.extreureServeiId("serveiId:5"));
		assertEquals(12L, SubrecursServeiHelper.extreureServeiId("(serveiId : '12') and actiu:true"));
		assertEquals(7L, SubrecursServeiHelper.extreureServeiId("serveiId=7"));
		assertNull(SubrecursServeiHelper.extreureServeiId("altreServeiId:5"));
		assertNull(SubrecursServeiHelper.extreureServeiId(null));
	}

	@Test
	void senseServeiAlFiltreEsRebutja() {
		assertThrows(ValidationException.class, () -> helper.getServeiIdDelFiltre("codi:'x'"));
	}

	@Test
	void paginaEnMemoria() {
		Page<Integer> pagina = helper.pagina(Arrays.asList(1, 2, 3, 4, 5), PageRequest.of(1, 2));
		assertEquals(Arrays.asList(3, 4), pagina.getContent());
		assertEquals(5, pagina.getTotalElements());
		assertEquals(5, helper.pagina(Arrays.asList(1, 2, 3, 4, 5), Pageable.unpaged()).getTotalElements());
		assertEquals(0, helper.pagina(Arrays.asList(1, 2), PageRequest.of(3, 2)).getContent().size());
	}

}
