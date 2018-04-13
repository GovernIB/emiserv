/**
 * 
 */
package es.caib.emiserv.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.emiserv.core.api.dto.ServeiDto;
import es.caib.emiserv.core.api.service.ServeiService;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiServeiNoRepetitValidator implements ConstraintValidator<CodiServeiNoRepetit, Object> {

	private String campId;
	private String campCodi;

	@Autowired
	private ServeiService serveiService;



	@Override
	public void initialize(final CodiServeiNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCodi = constraintAnnotation.campCodi();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String id = BeanUtils.getProperty(value, campId);
			final String codi = BeanUtils.getProperty(value, campCodi);
			ServeiDto servei = serveiService.findByCodi(codi);
			if (servei == null) {
				return true;
			} else {
				if (id == null)
					return false;
				else
					return id.equals(servei.getId().toString());
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi d'entitat és únic", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiServeiNoRepetitValidator.class);

}
