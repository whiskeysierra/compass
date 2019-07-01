package org.zalando.compass.kernel.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.BiPredicate;

public interface Relation extends BiPredicate<JsonNode, JsonNode> {

    String getId();

    String getTitle();

    String getDescription();

}
