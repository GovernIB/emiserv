/**
 * 
 */
package es.caib.emiserv.back.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import es.caib.emiserv.logic.intf.service.ScspService;

/**
 * Validador per a controlar que no es repeteixi el CIF d'un organisme cessionari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CifOrganismeCessionariNoRepetitValidator implements ConstraintValidator<CifOrganismeCessionariNoRepetit, Object> {

	private String campId;
	private String campCif;

	@Autowired
	private ScspService scspService;

	@Override
	public void initialize(final CifOrganismeCessionariNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCif = constraintAnnotation.campCif();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		String id;
		try {
			id = BeanUtils.getProperty(value, campId);
			final String cif = BeanUtils.getProperty(value, campCif);
			OrganismeDto organisme = scspService.organismeCessionariFindByCif(cif);
			if (organisme == null) {
				return true;
			} else {
				return id.equals(organisme.getId().toString());
			}
		} catch (Exception ex) {
			LOGGER.error("No s'ha pogut validar el CIF de l'organisme cessionari", ex);
			return false;
		}
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(CifOrganismeCessionariNoRepetitValidator.class);

}
