package org.zalando.compass.core.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.Wither;

@lombok.Value
public final class Key {

    @Wither
    String id;
    JsonNode schema;
    String description;

    @Override
    public String toString() {
        return id;
    }

}
