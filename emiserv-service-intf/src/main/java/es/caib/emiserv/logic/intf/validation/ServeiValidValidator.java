package es.caib.emiserv.logic.intf.validation;

import es.caib.emiserv.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.emiserv.logic.intf.model.ServeiResource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador de {@link ServeiValid}. Els camps de cada tipus són els que la JSP mostra al
 * formulari del servei (serveiForm.jsp):
 * <ul>
 *   <li>BACKOFFICE: classe, URL, autenticació i tipus de processament asíncron;</li>
 *   <li>ENRUTADOR: classe resolvedora;</li>
 *   <li>ENRUTADOR_MULTIPLE: classe resolvedora de respostes.</li>
 * </ul>
 *
 * @author Limit Tecnologies
 */
public class ServeiValidValidator implements ConstraintValidator<ServeiValid, ServeiResource> {

	@Override
	public boolean isValid(ServeiResource servei, ConstraintValidatorContext context) {
		if (servei == null || servei.getTipus() == null) {
			// El tipus obligatori el comprova @NotNull
			return true;
		}
		context.disableDefaultConstraintViolation();
		boolean valid = true;
		if (ServeiTipusEnumDto.BACKOFFICE.equals(servei.getTipus())) {
			valid &= requerit(context, ServeiResource.Fields.backofficeClass, servei.getBackofficeClass());
			valid &= requerit(context, ServeiResource.Fields.backofficeCaibUrl, servei.getBackofficeCaibUrl());
			valid &= requerit(context, ServeiResource.Fields.backofficeCaibAutenticacio, servei.getBackofficeCaibAutenticacio());
			valid &= requerit(context, ServeiResource.Fields.backofficeCaibAsyncTipus, servei.getBackofficeCaibAsyncTipus());
		} else if (ServeiTipusEnumDto.ENRUTADOR.equals(servei.getTipus())) {
			valid &= requerit(context, ServeiResource.Fields.resolverClass, servei.getResolverClass());
		} else if (ServeiTipusEnumDto.ENRUTADOR_MULTIPLE.equals(servei.getTipus())) {
			valid &= requerit(context, ServeiResource.Fields.responseResolverClass, servei.getResponseResolverClass());
		}
		return valid;
	}

	private static boolean requerit(ConstraintValidatorContext context, String camp, Object valor) {
		if (valor == null || (valor instanceof String && ((String)valor).trim().isEmpty())) {
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).
					addPropertyNode(camp).
					addConstraintViolation();
			return false;
		}
		return true;
	}

}
