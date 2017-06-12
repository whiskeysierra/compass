package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

@lombok.Value
public class ValueId {

    String key;
    Map<String, JsonNode> dimensions;

}
