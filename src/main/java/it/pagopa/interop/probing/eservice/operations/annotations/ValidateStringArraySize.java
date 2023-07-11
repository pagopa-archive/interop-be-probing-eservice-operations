package it.pagopa.interop.probing.eservice.operations.annotations;

import it.pagopa.interop.probing.eservice.operations.annotations.validator.StringArrayValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StringArrayValidator.class)
public @interface ValidateStringArraySize {

	int maxSize();

	String message() default "One of the strings of the array is more than {maxSize} characters long";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
