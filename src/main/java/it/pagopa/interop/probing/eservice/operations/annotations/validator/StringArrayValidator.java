package it.pagopa.interop.probing.eservice.operations.annotations.validator;

import it.pagopa.interop.probing.eservice.operations.annotations.ValidateStringArraySize;
import java.util.Objects;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StringArrayValidator implements ConstraintValidator<ValidateStringArraySize, String[]> {

	int maxSize;

	@Override
	public void initialize(ValidateStringArraySize constraintAnnotation) {
		maxSize = constraintAnnotation.maxSize();
	}

	@Override
	public boolean isValid(String[] array, ConstraintValidatorContext context) {
		return Objects.isNull(array) || Stream.of(array).noneMatch(s -> s.length() > maxSize);
	}

}
