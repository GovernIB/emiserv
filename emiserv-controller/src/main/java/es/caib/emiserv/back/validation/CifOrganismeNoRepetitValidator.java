/**
 * 
 */
package es.caib.emiserv.back.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.emiserv.logic.intf.dto.OrganismeDto;
import es.caib.emiserv.logic.intf.service.ScspService;

/**
 * Validador per a controlar que no es repeteixi el CIF d'un organisme.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CifOrganismeNoRepetitValidator implements ConstraintValidator<CifOrganismeNoRepetit, Object> {

	private String campId;
	private String campCif;

	@Autowired
	private ScspService scspService;

	@Override
	public void initialize(final CifOrganismeNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCif = constraintAnnotation.campCif();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String id = BeanUtils.getProperty(value, campId);
			final String cif = BeanUtils.getProperty(value, campCif);
			List<OrganismeDto> organismes = scspService.organismeFindByCif(cif);
			if (organismes == null || organismes.size() == 0) {
				return true;
			} else if (organismes.size() == 1) {
				OrganismeDto dto = organismes.get(0);
				return id.equals(dto.getId().toString());
			}else {
				return false;
			}
		} catch (final Exception ex) {
			LOGGER.error("Error al validar si el cif de l'organisme és únic", ex);
		}
		return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CifOrganismeNoRepetitValidator.class);

}
