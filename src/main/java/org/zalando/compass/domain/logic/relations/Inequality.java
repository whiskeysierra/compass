package org.zalando.compass.domain.logic.relations;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.domain.model.Relation;

import java.util.Comparator;

abstract class Inequality implements Relation, Comparator<JsonNode> {

    private final Comparator<JsonNode> comparator = new PrimitiveJsonNodeComparator();

    @Override
    public int compare(final JsonNode left, final JsonNode right) {
        return comparator.compare(left, right);
    }

}
