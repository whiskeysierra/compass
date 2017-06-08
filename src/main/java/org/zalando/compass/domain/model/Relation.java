package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.BiPredicate;

// TODO primitive JSON types only
public interface Relation extends BiPredicate<JsonNode, JsonNode> {

    String getId();

    String getTitle();

    String getDescription();

    // TODO boolean supports(JsonNodeType type);

}
