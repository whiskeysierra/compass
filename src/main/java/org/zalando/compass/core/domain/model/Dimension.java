package org.zalando.compass.core.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.experimental.Wither;

import java.util.function.BiPredicate;

@lombok.Value
@EqualsAndHashCode
public final class Dimension implements BiPredicate<JsonNode, JsonNode> {

    @Wither
    String id;
    JsonNode schema;
    Relation relation;
    String description;

    @Override
    public boolean test(final JsonNode left, final JsonNode right) {
        return relation.test(left, right);
    }

    @Override
    public String toString() {
        return id;
    }

}
