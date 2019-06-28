package org.zalando.compass.infrastructure.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.zalando.problem.violations.Violation;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Component
class JsonSchemaValidator {

    private final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

    @SneakyThrows
    List<Violation> validate(final JsonNode schema, final JsonNode node) {
        return validate(factory.getJsonSchema(schema), node);
    }

    @SneakyThrows
    private List<Violation> validate(final JsonSchema schema, final JsonNode node) {
        final ProcessingReport report = schema.validate(node);

        return stream(report.spliterator(), false)
                .sorted(comparing(ProcessingMessage::getMessage))
                .map(message -> new Violation("/", message.getMessage().replace("\"", "")))
                .collect(toList());
    }

}
