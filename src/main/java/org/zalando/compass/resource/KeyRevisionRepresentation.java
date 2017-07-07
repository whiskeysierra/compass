package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Revision;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class KeyRevisionRepresentation {

    String id;
    RevisionRepresentation revision;
    JsonNode schema;
    String description;

}
