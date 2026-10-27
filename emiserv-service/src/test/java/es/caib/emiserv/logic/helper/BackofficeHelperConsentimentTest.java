package es.caib.emiserv.logic.helper;

import es.caib.emiserv.logic.intf.service.ws.backoffice.Consentimiento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Conversió del consentiment guardat a CORE_TRANSMISION per les llibreries SCSP 4 (Si, Ley)
 * i SCSP 5 (SI, LEY, NO_OPO).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
class BackofficeHelperConsentimentTest {

	@Test
	void consentimentScsp4() {
		assertEquals(Consentimiento.Si, BackofficeHelper.toConsentimiento("Si"));
		assertEquals(Consentimiento.Ley, BackofficeHelper.toConsentimiento("Ley"));
		assertEquals(Consentimiento.NoOpo, BackofficeHelper.toConsentimiento("NoOpo"));
	}

	@Test
	void consentimentScsp5() {
		assertEquals(Consentimiento.Si, BackofficeHelper.toConsentimiento("SI"));
		assertEquals(Consentimiento.Ley, BackofficeHelper.toConsentimiento("LEY"));
		assertEquals(Consentimiento.NoOpo, BackofficeHelper.toConsentimiento("NO_OPO"));
	}

	@Test
	void consentimentDesconegut() {
		assertThrows(IllegalArgumentException.class, () -> BackofficeHelper.toConsentimiento("Altre"));
	}

}
