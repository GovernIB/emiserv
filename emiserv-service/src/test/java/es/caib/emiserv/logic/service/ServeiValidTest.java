package es.caib.emiserv.logic.service;

import es.caib.emiserv.logic.intf.dto.BackofficeAsyncTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.BackofficeAutenticacioTipusEnumDto;
import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.model.ServeiResource;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Camps obligatoris d'un servei segons el tipus (ServeiValid), com els grups de validació de la JSP.
 */
class ServeiValidTest {

	private final Validator validator = Validation.byDefaultProvider().
			configure().
			messageInterpolator(new ParameterMessageInterpolator()).
			buildValidatorFactory().
			getValidator();

	@Test
	void backofficeRequereixElsCampsDelBackoffice() {
		ServeiResource servei = servei(ServeiTipusEnumDto.BACKOFFICE);
		assertEquals(
				new TreeSet<>(Set.of("backofficeClass", "backofficeCaibUrl", "backofficeCaibAutenticacio", "backofficeCaibAsyncTipus")),
				campsAmbError(servei));
		servei.setBackofficeClass("es.Backoffice");
		servei.setBackofficeCaibUrl("http://backoffice");
		servei.setBackofficeCaibAutenticacio(BackofficeAutenticacioTipusEnumDto.NINGUNA);
		servei.setBackofficeCaibAsyncTipus(BackofficeAsyncTipusEnumDto.REENVIAR_BACKOFFICE);
		assertTrue(campsAmbError(servei).isEmpty());
	}

	@Test
	void enrutadorsRequereixenLaSevaClasse() {
		assertEquals(Set.of("resolverClass"), campsAmbError(servei(ServeiTipusEnumDto.ENRUTADOR)));
		assertEquals(Set.of("responseResolverClass"), campsAmbError(servei(ServeiTipusEnumDto.ENRUTADOR_MULTIPLE)));
		ServeiResource enrutador = servei(ServeiTipusEnumDto.ENRUTADOR);
		enrutador.setResolverClass("  ");
		assertEquals(Set.of("resolverClass"), campsAmbError(enrutador));
	}

	@Test
	void senseTipusNomesFallaElTipus() {
		assertEquals(Set.of("tipus"), campsAmbError(servei(null)));
	}

	private Set<String> campsAmbError(ServeiResource servei) {
		Set<ConstraintViolation<ServeiResource>> errors = validator.validate(servei);
		return errors.stream().map(e -> e.getPropertyPath().toString()).collect(Collectors.toCollection(TreeSet::new));
	}

	private static ServeiResource servei(ServeiTipusEnumDto tipus) {
		ServeiResource servei = new ServeiResource();
		servei.setCodi("S1");
		servei.setNom("Servei");
		servei.setTipus(tipus);
		return servei;
	}

}
