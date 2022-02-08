/**
 * 
 */
package es.caib.emiserv.back.validation;

import es.caib.emiserv.back.command.ScspParametreCommand;
import es.caib.emiserv.back.helper.MessageHelper;
import es.caib.emiserv.logic.intf.dto.ScspParametreDto;
import es.caib.emiserv.logic.intf.exception.NotFoundException;
import es.caib.emiserv.logic.intf.service.ScspService;
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
public class NomParametreNoRepetitValidator implements ConstraintValidator<NomParametreNoRepetit, ScspParametreCommand> {

	@Autowired
	private ScspService scspService;
	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(final NomParametreNoRepetit constraintAnnotation) {
	}

	@Override
	public boolean isValid(final ScspParametreCommand command, final ConstraintValidatorContext context) {

		boolean valid = true;

		if (command.isNou() && command.getNombre() != null) {
			try {
				ScspParametreDto parametre = scspService.getScspParametre(command.getNombre());
				// Si troba el paràmetre vol dir que està repetit
				context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(
										"parametres.nom.repetit",
										null,
										new RequestContext(request).getLocale()))
						.addPropertyNode("nombre")
						.addConstraintViolation();
				valid = false;
			} catch (NotFoundException nfe) {}

		}

		if (!valid)
			context.disableDefaultConstraintViolation();

		return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(NomParametreNoRepetitValidator.class);

}
