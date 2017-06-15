package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonType;

import java.util.Set;
import java.util.function.BiPredicate;

public interface Relation extends BiPredicate<JsonNode, JsonNode> {

    String getId();

    String getTitle();

    String getDescription();

    Set<JsonType> supports();

}
