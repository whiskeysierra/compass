package org.zalando.compass.domain.model;

import com.google.common.collect.ImmutableSet;

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

        private final ImmutableSet<String> reserved = ImmutableSet.of(
                // Zalando REST API Guidelines
                // http://zalando.github.io/restful-api-guidelines/naming/Naming.html#may-use-conventional-query-strings
                "q", // default query parameter
                "limit", // to restrict the number of entries
                "cursor", // key-based page start
                "offset", // numeric offset page start
                "sort", // comma-separated list of fields to sort
                "fields", // to retrieve a subset of fields
                "embed", // to expand embedded entFities

                // Google Cloud Platform: API Design Guide
                // https://cloud.google.com/apis/design/standard_fields
                "filter",
                "query",

                // Compass specific
                "key",
                "revisions"
        );

        @Override
        public void initialize(final NotReserved annotation) {

        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context) {
            return !reserved.contains(value);
        }
    }

}
