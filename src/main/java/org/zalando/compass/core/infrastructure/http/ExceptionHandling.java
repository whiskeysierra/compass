package org.zalando.compass.core.infrastructure.http;

import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.report.ValidationReport.Message;
import com.atlassian.oai.validator.springmvc.InvalidRequestException;
import com.atlassian.oai.validator.springmvc.InvalidResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.compass.core.domain.api.BadArgumentException;
import org.zalando.compass.core.domain.api.EntityAlreadyExistsException;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.SpringAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
class ExceptionHandling implements ProblemHandling, SpringAdviceTrait {

    @ExceptionHandler
    public ResponseEntity<Problem> handleBadArgument(final BadArgumentException exception,
            final NativeWebRequest request) {
        return create(HttpStatus.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleNotFoundException(final NotFoundException exception,
            final NativeWebRequest request) {
        return create(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleEntityAlreadyExists(final EntityAlreadyExistsException exception,
            final NativeWebRequest request) {
        return create(HttpStatus.PRECONDITION_FAILED, exception, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleInvalidRequestException(final InvalidRequestException exception,
            final NativeWebRequest request) {
        return create(exception, request, exception.getValidationReport());
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleInvalidResponseException(final InvalidResponseException exception,
            final NativeWebRequest request) {
        return create(exception, request, exception.getValidationReport());
    }

    private ResponseEntity<Problem> create(final Exception exception, final NativeWebRequest request,
            final ValidationReport report) {
        final var violations = report.getMessages().stream()
                .sorted(Comparator.comparing(Message::getMessage))
                // TODO message + additional info
                .map(message -> new Violation("/", message.getMessage().replace("\"", "")))
                .collect(toList());

        return create(exception, new ConstraintViolationProblem(Status.BAD_REQUEST, violations), request);
    }

}
