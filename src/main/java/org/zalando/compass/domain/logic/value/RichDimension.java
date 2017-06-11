package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Relation;

@lombok.Value
class RichDimension {
    String id;
    JsonNode schema;
    Relation relation;
    String description;
}
