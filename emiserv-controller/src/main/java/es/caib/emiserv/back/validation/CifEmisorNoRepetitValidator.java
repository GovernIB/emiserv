/**
 * 
 */
package es.caib.emiserv.back.validation;

import es.caib.emiserv.back.command.EmisorCommand;
import es.caib.emiserv.back.helper.MessageHelper;
import es.caib.emiserv.logic.intf.dto.EmisorDto;
import es.caib.emiserv.logic.intf.service.ScspService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador per a controlar que no es repeteixi el CIF d'un organisme cessionari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CifEmisorNoRepetitValidator implements ConstraintValidator<CifEmisorNoRepetit, EmisorCommand> {

	@Autowired
	private ScspService scspService;

	@Override
	public void initialize(final CifEmisorNoRepetit constraintAnnotation) {
	}

	@Override
	public boolean isValid(final EmisorCommand command, final ConstraintValidatorContext context) {
		try {
			final Long id = command.getId();
			final String cif = command.getCif();

			boolean valid = true;

			EmisorDto emisor = scspService.emisorFindByCif(cif);
			if (emisor == null) {
				valid = true;
			} else {
				valid = emisor.getId().equals(id);
			}
			if (!valid) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("emisor.validation.cif.repetit")).addPropertyNode("cif").addConstraintViolation();
			}
			return valid;

		} catch (Exception ex) {
			LOGGER.error("No s'ha pogut validar el CIF de l'emisor", ex);
			return false;
		}
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(CifEmisorNoRepetitValidator.class);

}
