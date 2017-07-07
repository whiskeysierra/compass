package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Getter
@AllArgsConstructor
final class DimensionRevisionRepresentation {

    String id;
    RevisionRepresentation revision;
    JsonNode schema;
    String relation;
    String description;

}
