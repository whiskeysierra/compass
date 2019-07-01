package org.zalando.compass.core.domain.model.relation;

import com.fasterxml.jackson.databind.JsonNode;
import org.zalando.compass.kernel.domain.model.Relation;

import java.util.Comparator;

abstract class Inequality implements Relation, Comparator<JsonNode> {

    private final Comparator<JsonNode> comparator = NaturalOrderJsonComparator.comparingJson();

    @Override
    public int compare(final JsonNode left, final JsonNode right) {
        return comparator.compare(left, right);
    }

    @Override
    public String toString() {
        return getId();
    }

}
