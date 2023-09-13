/**
 * 
 */
package es.caib.emiserv.back.validation;

import es.caib.emiserv.back.command.EntitatCommand;
import es.caib.emiserv.back.helper.MessageHelper;
import es.caib.emiserv.logic.intf.dto.EntitatDto;
import es.caib.emiserv.logic.intf.service.EntitatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiEntitatNoRepetitValidator implements ConstraintValidator<CodiEntitatNoRepetit, EntitatCommand> {

	private String campId;
	private String campCodi;

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private HttpServletRequest request;



	@Override
	public void initialize(final CodiEntitatNoRepetit constraintAnnotation) {
	}

	@Override
	public boolean isValid(final EntitatCommand command, final ConstraintValidatorContext context) {

		boolean valid = true;

		try {
			final Long id = command.getId();
			final String codi = command.getCodi();
			EntitatDto entitat = entitatService.findByCodi(codi);
			if (entitat != null) {
				valid = id != null && id.equals(entitat.getId());
				if (!valid) {
					context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(
											"entitat.codi.repetit",
											null,
											new RequestContext(request).getLocale()))
							.addPropertyNode("codi")
							.addConstraintViolation();
					context.disableDefaultConstraintViolation();
				}
			}
		} catch (final Exception ex) {
			LOGGER.error("Error al validar si el codi d'entitat és únic", ex);
		}
		return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiEntitatNoRepetitValidator.class);

}
