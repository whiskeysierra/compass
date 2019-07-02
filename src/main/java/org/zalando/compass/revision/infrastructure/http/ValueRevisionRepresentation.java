package org.zalando.compass.revision.infrastructure.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.infrastructure.http.RevisionRepresentation;
import org.zalando.compass.revision.domain.model.ValueRevision;

import static lombok.AccessLevel.PRIVATE;
import static org.zalando.compass.library.Maps.transform;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class ValueRevisionRepresentation {

    ImmutableMap<String, JsonNode> dimensions;
    RevisionRepresentation revision;
    JsonNode value;

    static ValueRevisionRepresentation valueOf(final ValueRevision value) {
        return new ValueRevisionRepresentation(
                transform(value.getDimensions(), Dimension::getId),
                RevisionRepresentation.valueOf(value.getRevision()),
                value.getValue());
    }

}
