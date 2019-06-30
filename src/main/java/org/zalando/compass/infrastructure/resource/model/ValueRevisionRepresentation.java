package org.zalando.compass.infrastructure.resource.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.domain.model.revision.ValueRevision;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class ValueRevisionRepresentation {

    ImmutableMap<String, JsonNode> dimensions;
    RevisionRepresentation revision;
    JsonNode value;

    public static ValueRevisionRepresentation valueOf(final ValueRevision value) {
        return new ValueRevisionRepresentation(
                value.getDimensions(),
                RevisionRepresentation.valueOf(value.getRevision()),
                value.getValue());
    }

}
