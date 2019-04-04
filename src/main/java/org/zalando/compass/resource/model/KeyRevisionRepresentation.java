package org.zalando.compass.resource.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.domain.model.KeyRevision;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
public final class KeyRevisionRepresentation {

    String id;
    RevisionRepresentation revision;
    JsonNode schema;
    String description;

    public static KeyRevisionRepresentation valueOf(final KeyRevision key) {
        return new KeyRevisionRepresentation(
                key.getId(),
                RevisionRepresentation.valueOf(key.getRevision()),
                key.getSchema(),
                key.getDescription()
        );
    }

}
