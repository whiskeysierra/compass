package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.Wither;

@lombok.Value
public final class Dimension {

    @NotReserved
    @Wither
    String id;

    JsonNode schema;
    String relation;
    String description;

}
