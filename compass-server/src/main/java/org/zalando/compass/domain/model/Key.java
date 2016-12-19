package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

@lombok.Value
public final class Key {

    private final String id;
    private final JsonNode schema;
    private final String description;

}
