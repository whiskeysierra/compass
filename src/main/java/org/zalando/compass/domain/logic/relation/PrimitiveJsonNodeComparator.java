package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Booleans;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;

// TODO library?
final class PrimitiveJsonNodeComparator extends Ordering<JsonNode> {

    @Override
    public int compare(@Nullable final JsonNode left, @Nullable final JsonNode right) {
        if (isNull(left) && isNull(right)) {
            return 0;
        } else if (isNull(left)) {
            return -1;
        } else if (isNull(right)) {
            return 1;
        }

        return compareNonNull(left, right);
    }

    private int compareNonNull(final JsonNode left, final JsonNode right) {
        checkArgument(left.getNodeType() == right.getNodeType(),
                "JSON type mismatch: %s vs. %s", left.getNodeType(), right.getNodeType());

        switch (left.getNodeType()) {
            case BOOLEAN:
                return Booleans.compare(left.booleanValue(), right.booleanValue());
            case NUMBER:
                return left.decimalValue().compareTo(right.decimalValue());
            case STRING:
                return left.asText().compareTo(right.asText());
        }

        // TODO test this
        return left.getNodeType().compareTo(right.getNodeType());
    }

    private static boolean isNull(@Nullable final JsonNode node) {
        return node == null || node.isNull();
    }

}
