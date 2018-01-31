package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonType;
import org.zalando.compass.domain.model.Relation;

import java.util.Comparator;
import java.util.Set;

import static com.google.common.collect.Sets.immutableEnumSet;
import static java.util.EnumSet.allOf;

abstract class Inequality implements Relation, Comparator<JsonNode> {

    private final Set<JsonType> supported = immutableEnumSet(allOf(JsonType.class));
    private final Comparator<JsonNode> comparator = NaturalOrderJsonComparator.comparingJson();

    @Override
    public Set<JsonType> supports() {
        return supported;
    }

    @Override
    public int compare(final JsonNode left, final JsonNode right) {
        return comparator.compare(left, right);
    }

    @Override
    public String toString() {
        return getId();
    }

}
