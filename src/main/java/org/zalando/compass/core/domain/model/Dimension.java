package org.zalando.compass.core.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.experimental.Wither;

@lombok.Value
@EqualsAndHashCode(of = "id")
public final class Dimension {

    @Wither
    String id;
    JsonNode schema;
    Relation relation;
    String description;

    @Override
    public String toString() {
        return id;
    }

}
