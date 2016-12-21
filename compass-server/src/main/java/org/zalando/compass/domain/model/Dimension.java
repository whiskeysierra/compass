package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.Wither;

@lombok.Value
public final class Dimension {

    @Wither
    private final String id;
    private final JsonNode schema;
    private final String relation;
    private final String description;

}
