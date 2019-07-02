package org.zalando.compass.revision.infrastructure.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.core.infrastructure.http.RevisionRepresentation;
import org.zalando.compass.revision.domain.model.ValueRevision;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class ValueRevisionRepresentation {

    ImmutableMap<String, JsonNode> dimensions;
    RevisionRepresentation revision;
    JsonNode value;

    static ValueRevisionRepresentation valueOf(final ValueRevision value) {
        return new ValueRevisionRepresentation(
                value.getDimensions(),
                RevisionRepresentation.valueOf(value.getRevision()),
                value.getValue());
    }

}
