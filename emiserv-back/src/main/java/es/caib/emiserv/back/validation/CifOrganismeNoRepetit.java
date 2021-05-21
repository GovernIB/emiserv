/**
 * 
 */
package es.caib.emiserv.back.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint de validació per a controlar que no es repeteixi
 * el CIF d'un organisme.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=CifOrganismeNoRepetitValidator.class)
public @interface CifOrganismeNoRepetit {

	String message() default "Ja existeix una altra organisme amb aquest cif";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String campId();

	String campCif();

}
