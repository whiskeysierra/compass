package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.Wither;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueRevision;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class ValueRevisionRepresentation {

    ImmutableMap<String, JsonNode> dimensions;
    RevisionRepresentation revision;
    JsonNode value;

    static ValueRevisionRepresentation valueOf(final ValueRevision value) {
        final Revision revision = value.getRevision();
        return new ValueRevisionRepresentation(value.getDimensions(),
                new RevisionRepresentation(
                        revision.getId(),
                        revision.getTimestamp(),
                        null,
                        revision.getType(),
                        revision.getUser(),
                        revision.getComment()
                ),
                value.getValue());
    }

}
