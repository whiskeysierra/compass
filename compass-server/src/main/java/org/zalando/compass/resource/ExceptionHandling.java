package org.zalando.compass.resource;

import com.google.common.base.CaseFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.zalando.compass.domain.logic.NotFoundException;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.SpringAdviceTrait;

import javax.annotation.Nonnull;

import static com.google.common.base.Throwables.getRootCause;

@ControllerAdvice
public class ExceptionHandling implements ProblemHandling, SpringAdviceTrait {

    @ExceptionHandler
    public ResponseEntity<Problem> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException exception,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {

        return create(HttpStatus.BAD_REQUEST, getRootCause(exception), request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleNotFoundException(
            final NotFoundException exception,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {

        return create(HttpStatus.NOT_FOUND, exception, request);
    }

    @Override
    public String formatFieldName(@Nonnull final String fieldName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }

}
