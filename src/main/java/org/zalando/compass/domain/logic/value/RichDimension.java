package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.zalando.compass.domain.model.Relation;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
final class RichDimension {
    private final String id;
    private final JsonNode schema;
    private final Relation relation;
    private final String description;
}
