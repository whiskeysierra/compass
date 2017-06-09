package org.zalando.compass.resource;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = NotReserved.NotReservedValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface NotReserved {

    String message() default "may not be a reserved keyword";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    final class NotReservedValidator implements ConstraintValidator<NotReserved, String> {

        @Override
        public void initialize(final NotReserved annotation) {

        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context) {
            return !Keywords.RESERVED.contains(value);
        }
    }

}
