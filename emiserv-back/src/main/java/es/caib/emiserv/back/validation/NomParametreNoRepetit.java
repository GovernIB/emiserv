/**
 * 
 */
package es.caib.emiserv.back.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= NomParametreNoRepetitValidator.class)
public @interface NomParametreNoRepetit {

	String message() default "Ja existeix una altre paràmetre amb aquest nom";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
