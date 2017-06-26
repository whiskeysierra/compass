package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

@lombok.Value
public final class Key {

    String id;
    JsonNode schema;
    String description;

}
