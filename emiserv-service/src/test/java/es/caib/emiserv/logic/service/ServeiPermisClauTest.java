package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.emiserv.logic.intf.dto.PrincipalTipusEnumDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Identificador dels permisos del recurs serveiPermis (servei, tipus i principal).
 */
class ServeiPermisClauTest {

	@Test
	void identificadorReversible() {
		for (String nom: new String[] {"u999000", "EMS_RESP", "nom amb espais/barres-i-guions", "àèò"}) {
			String id = new ServeiPermisResourceServiceImpl.Clau(5L, PrincipalTipusEnumDto.ROL, nom).toId();
			ServeiPermisResourceServiceImpl.Clau clau = ServeiPermisResourceServiceImpl.Clau.parse(id);
			assertEquals(5L, clau.serveiId);
			assertEquals(PrincipalTipusEnumDto.ROL, clau.tipus);
			assertEquals(nom, clau.nom, id);
		}
	}

	@Test
	void identificadorIncorrecteNoEsTroba() {
		assertThrows(ResourceNotFoundException.class, () -> ServeiPermisResourceServiceImpl.Clau.parse("x"));
		assertThrows(ResourceNotFoundException.class, () -> ServeiPermisResourceServiceImpl.Clau.parse("5-NOEXISTEIX-YQ"));
	}

}
