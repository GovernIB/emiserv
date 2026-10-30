package es.caib.emiserv.logic.intf.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Comprova els camps obligatoris d'un servei segons el seu tipus, com els grups de validació
 * de ServeiCommand de la JSP (TipusBackoffice, TipusEnrutador i TipusEnrutadorMultiple).
 *
 * @author Limit Tecnologies
 */
@Documented
@Constraint(validatedBy = ServeiValidValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServeiValid {

	String message() default "{javax.validation.constraints.NotEmpty.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
