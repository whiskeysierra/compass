package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@lombok.Value
@Builder
@AllArgsConstructor
public class Realization {

    private String key;
    private Map<String, JsonNode> dimensions;

}
