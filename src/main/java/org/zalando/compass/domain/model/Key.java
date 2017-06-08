package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.Wither;

@lombok.Value
public final class Key {

    @Wither
    private final String id;
    private final JsonNode schema;
    private final String description;

}
