package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.domain.model.Key;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class KeyRepresentation {

    String id;
    JsonNode schema;
    String description;

    static KeyRepresentation valueOf(final Key key) {
        return new KeyRepresentation(
                key.getId(),
                key.getSchema(),
                key.getDescription());
    }

}